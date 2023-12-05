/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.low.level.api.fir.providers

import com.intellij.util.graph.DFSTBuilder
import com.intellij.util.graph.GraphGenerator
import com.intellij.util.graph.InboundSemiGraph
import com.intellij.util.graph.OutboundSemiGraph
import org.jetbrains.kotlin.analysis.project.structure.KtModule

interface LLFirModulePrecedenceOracle {
    fun order(modules: List<KtModule>): List<KtModule>
}

object KeepUnchangedModulePrecedenceOracle : LLFirModulePrecedenceOracle {
    override fun order(modules: List<KtModule>): List<KtModule> = modules
}

object KmpModulePrecedenceOracle : LLFirModulePrecedenceOracle {
    override fun order(modules: List<KtModule>): List<KtModule> {
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
    private val inEdges = mutableMapOf<KtModule, MutableList<KtModule>>()
    private val registeredModules = mutableListOf<KtModule>()
    private val sortedModules by lazy {
        val graph: OutboundSemiGraph<KtModule> = GraphGenerator.generate(DependsOnModuleGraph(inEdges))
        DFSTBuilder(graph).sortedNodes.filterNotNull().also {
            check(it.size == inEdges.size) { "The number of sorted modules doesn't match the amount of registered modules" }
        }
    }
    private val zippedModules by lazy {
        check(sortedModules.size == registeredModules.size) { "The number of sorted modules and registered modules doesn't match" }
        sortedModules.zip(registeredModules).toMap()
    }

    fun registerDependsOnEdges(module: KtModule) {
        registeredModules.add(module)
        inEdges.getOrPut(module) { mutableListOf() }
        module.directDependsOnDependencies.forEach { dependency ->
            inEdges.getOrPut(dependency) { mutableListOf() }.add(module)
        }
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

private class DependsOnModuleGraph(private val inEdges: Map<KtModule, List<KtModule>>) : InboundSemiGraph<KtModule> {
    override fun getNodes(): Collection<KtModule?> {
        return inEdges.keys
    }

    override fun getIn(n: KtModule?): Iterator<KtModule?> {
        if (n == null) return emptyList<KtModule>().iterator()
        return inEdges[n]?.iterator() ?: emptyList<KtModule>().iterator()
    }
}
