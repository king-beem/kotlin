/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.low.level.api.fir.providers

import org.jetbrains.kotlin.analysis.project.structure.KtModule
import org.jetbrains.kotlin.utils.topologicalSort

/**
 * Changes positions of modules that belong to the same KMP project: (A dependsOn B) -> (A goes before B in the list).
 * Allows giving actuals higher priority. Keeps positions of other modules unchanged.
 */
object KmpModulePrecedenceOracle {
    fun order(modules: List<KtModule>): List<KtModule> {
        val groupsByModules = mutableMapOf<KtModule, KmpGroup>()
        registerModules(modules, groupsByModules)
        val sortedModules = sortModules(modules, groupsByModules)

        check(sortedModules.size == modules.size) {
            "The resulting number of modules (${sortedModules.size}) doesn't match the number of input modules ($modules.size)"
        }

        return sortedModules
    }

    private fun registerModules(modules: List<KtModule>, groupsByModules: MutableMap<KtModule, KmpGroup>) {
        val originalPositions = mutableMapOf<KtModule, Int>()
        for ((index, module) in modules.withIndex()) {
            originalPositions[module] = index
            val group = findOrCreateKmpGroup(module, groupsByModules, originalPositions)
            group.registerDependsOnEdges(module)
            groupsByModules.putIfAbsent(module, group)
            module.directDependsOnDependencies.forEach { dependency -> groupsByModules.putIfAbsent(dependency, group) }
        }
    }

    private fun sortModules(modules: List<KtModule>, groupsByModules: Map<KtModule, KmpGroup>): List<KtModule> {
        val sortedModules = arrayOfNulls<KtModule>(modules.size)
        modules.forEachIndexed { index, module ->
            val group = groupsByModules[module]
            if (group == null) {
                sortedModules[index] = module
            } else {
                sortedModules[group.getUpdatedIndexOf(module)] = module
            }
        }

        return sortedModules.toList().filterNotNull()
    }

    private fun findOrCreateKmpGroup(
        module: KtModule,
        groups: MutableMap<KtModule, KmpGroup>,
        originalPositions: MutableMap<KtModule, Int>,
    ): KmpGroup {
        groups[module]?.let { return it }
        module.directDependsOnDependencies.firstNotNullOfOrNull { dependency -> groups[dependency] }?.let { return it }
        return KmpGroup(originalPositions)
    }
}

/**
 * Modules, corresponding to source sets of the same KMP project.
 */
private class KmpGroup(private val originalPositions: Map<KtModule, Int>) {
    private val registeredModules = mutableListOf<KtModule>()
    private val sortedModules by lazy {
        topologicalSort(registeredModules) { directDependsOnDependencies }.also {
            check(it.size == registeredModules.size) { "The number of sorted modules doesn't match the number of registered modules" }
        }
    }

    private val zippedModules by lazy { sortedModules.zip(registeredModules).toMap() }

    fun registerDependsOnEdges(module: KtModule) {
        registeredModules.add(module)
    }

    fun getUpdatedIndexOf(module: KtModule): Int {
        check(module in registeredModules)
        if (registeredModules.size == 1) return originalPositions[module]!!

        return zippedModules[module]?.let { originalPositions[it] }
            ?: error("Can't find position for module: $module")
    }

    // N.B.: evaluating debug text before all modules are registered will corrupt the group
    @Suppress("unused")
    fun debugText(): String = buildString {
        zippedModules.entries.joinToString(separator = "; ", prefix = "[", postfix = "]") { (old, new) ->
            "$old (${originalPositions[old]}) -> $new"
        }
    }
}
