/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.kapt4

import com.intellij.lang.jvm.types.JvmType
import com.intellij.psi.JvmPsiConversionHelper
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiType
import com.intellij.psi.util.PsiTypesUtil
import org.jetbrains.kotlin.asJava.classes.KtLightClass

internal class ParameterInfo(
    val flags: Long,
    val name: String,
    val type: PsiType,
    val visibleAnnotations: List<PsiAnnotation>,
    val invisibleAnnotations: List<PsiAnnotation>
)

internal fun PsiMethod.getParametersInfo(
    containingClass: KtLightClass,
    isInnerClassMember: Boolean
): List<ParameterInfo> {
    val typeConverter = JvmPsiConversionHelper.getInstance(project)
    return this.parameters.map {
        ParameterInfo(0, it.name ?: "<no name provided>", typeConverter.convertType(it.type), emptyList(), emptyList())
    }
//    val localVariables = this.localVariables ?: emptyList()
//    val parameters = this.parameters ?: emptyList()
//    val isStatic = isStatic(access)
//    val isJvmOverloads = false // this.isJvmOverloadsGenerated()
//
//
//
//    // First and second parameters in enum constructors are synthetic, we should ignore them
//    val isEnumConstructor = (name == "<init>") && containingClass.isEnum()
//    val startParameterIndex =
//        if (isEnumConstructor) 2 else
//            if (isInnerClassMember && name == "<init>") 1 else 0
//
//    val parameterTypes = Type.getArgumentTypes(desc)
//
//    val parameterInfos = ArrayList<ParameterInfo>(parameterTypes.size - startParameterIndex)
//    for (index in startParameterIndex..parameterTypes.lastIndex) {
//        val type = parameterTypes[index]
//
//        // Use parameters only in case of the abstract methods (it hasn't local variables table)
//        var name: String? = if (isAbstract(this.access) && this.name != "<init>")
//            parameters.getOrNull(index - startParameterIndex)?.name
//        else
//            null
//
//        val localVariableIndexOffset = when {
//            isStatic -> 0
//            isJvmOverloads -> 0
//            else -> 1
//        }
//
//        // @JvmOverloads constructors and ordinary methods don't have "this" local variable
//        name = name ?: localVariables.getOrNull(index + localVariableIndexOffset)?.name
//                ?: originalDescriptor.valueParameters.getOrNull(index)?.name?.identifierOrNullIfSpecial
//        if (name == null || name.startsWith("<") && name.endsWith(">")) {
//            name = "p${index - startParameterIndex}"
//        }
//
//        val indexForAnnotation = index - startParameterIndex
//        val visibleAnnotations = visibleParameterAnnotations?.get(indexForAnnotation)
//        val invisibleAnnotations = invisibleParameterAnnotations?.get(indexForAnnotation)
//        parameterInfos += ParameterInfo(0, name, type, visibleAnnotations, invisibleAnnotations)
//    }
//    return parameterInfos
}
