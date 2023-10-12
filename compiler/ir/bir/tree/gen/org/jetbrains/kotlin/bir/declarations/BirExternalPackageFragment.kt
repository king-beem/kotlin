/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// This file was generated automatically. See compiler/ir/ir.tree/tree-generator/ReadMe.md.
// DO NOT MODIFY IT MANUALLY.

package org.jetbrains.kotlin.bir.declarations

import org.jetbrains.kotlin.bir.BirElementVisitor
import org.jetbrains.kotlin.bir.accept
import org.jetbrains.kotlin.bir.symbols.BirExternalPackageFragmentSymbol
import org.jetbrains.kotlin.serialization.deserialization.descriptors.DeserializedContainerSource

/**
 * A non-leaf IR tree element.
 *
 * Generated from: [org.jetbrains.kotlin.bir.generator.BirTree.externalPackageFragment]
 */
abstract class BirExternalPackageFragment : BirPackageFragment() {
    abstract override val symbol: BirExternalPackageFragmentSymbol

    abstract val containerSource: DeserializedContainerSource?

    override fun <D> acceptChildren(visitor: BirElementVisitor<D>, data: D) {
        declarations.forEach { it.accept(data, visitor) }
    }
}
