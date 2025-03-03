/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.sir

sealed interface SirKotlinOrigin : SirOrigin.Foreign {
    val fqName: List<String>

    override val path: List<String>
        get() = fqName

    interface Function : SirKotlinOrigin {
        val parameters: List<Parameter>
        val returnType: Type
    }

    interface Parameter {
        val name: String
        val type: Type
    }

    interface Type {
        val name: String
    }
}