/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.low.level.api.fir.providers

import org.jetbrains.kotlin.analysis.project.structure.KtModule
import org.jetbrains.kotlin.utils.topologicalSort

/**
 * Changes positions of modules that belong to the same KMP project: (A dependsOn B) -> (A goes before B in the list).
 * Allows giving actuals higher priority. Keeps positions of other non-KMP modules unchanged.
 */
class KmpModuleSorter(private val modules: List<KtModule>) {
    private val groupsByModules = mutableMapOf<KtModule, KmpGroup>()
    private val originalPositions = mutableMapOf<KtModule, Int>()

    fun sort(): List<KtModule> {
        groupModules()
        return sortModules()
    }

    private fun groupModules() {
        for ((index, module) in modules.withIndex()) {
            originalPositions[module] = index
            val group = findOrCreateKmpGroup(module, groupsByModules)
            group.addModule(module)
            groupsByModules.putIfAbsent(module, group)
            module.directDependsOnDependencies.forEach { dependency -> groupsByModules.putIfAbsent(dependency, group) }
        }
    }

    private fun sortModules(): List<KtModule> {
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
    ): KmpGroup {
        groups[module]?.let { return it }
        module.directDependsOnDependencies.firstNotNullOfOrNull { dependency -> groups[dependency] }?.let { return it }
        return KmpGroup()
    }

    /**
     * Modules, corresponding to source sets of the same KMP project.
     */
    private inner class KmpGroup() {
        private val modules = mutableListOf<KtModule>()
        private val sortedModules by lazy {
            topologicalSort(modules) { directDependsOnDependencies }.also {
                check(it.size == modules.size) { "The number of sorted modules doesn't match the number of registered modules" }
            }
        }

        private val zippedModules by lazy { sortedModules.zip(modules).toMap() }

        fun addModule(module: KtModule) {
            modules.add(module)
        }

        fun getUpdatedIndexOf(module: KtModule): Int {
            check(module in modules)
            if (modules.size == 1) return originalPositions[module]
                ?: error("Can't find position for module: $module")

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

    companion object {
        fun order(modules: List<KtModule>): List<KtModule> {
            if (modules.size < 2) return modules

            val sorter = KmpModuleSorter(modules)
            val sortedModules = sorter.sort()

            check(sortedModules.size == modules.size) {
                "The resulting number of modules (${sortedModules.size}) doesn't match the number of input modules ($modules.size)"
            }

            return sortedModules
        }
    }
}
