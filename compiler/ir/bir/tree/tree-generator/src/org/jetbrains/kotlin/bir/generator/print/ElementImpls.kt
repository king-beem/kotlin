/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */


package org.jetbrains.kotlin.bir.generator.print

import com.squareup.kotlinpoet.*
import org.jetbrains.kotlin.bir.generator.Packages
import org.jetbrains.kotlin.bir.generator.elementBaseType
import org.jetbrains.kotlin.bir.generator.model.ListField
import org.jetbrains.kotlin.bir.generator.model.Model
import org.jetbrains.kotlin.bir.generator.model.SingleField
import org.jetbrains.kotlin.generators.tree.ImplementationKind
import java.io.File

fun printElementImpls(generationPath: File, model: Model) = sequence {
    for (element in model.elements.filter { it.isLeaf }) {
        val elementType = TypeSpec.classBuilder(element.elementImplName).apply {
            addTypeVariables(element.params.map { it.toPoet() })

            if (element.kind == ImplementationKind.Interface || element.kind == ImplementationKind.SealedInterface) {
                superclass(elementBaseType.toPoet())
                addSuperinterface(element.toPoetSelfParameterized())
            } else {
                superclass(element.toPoetSelfParameterized())
            }

            val ctor = FunSpec.constructorBuilder()

            val allFields = element.allFields
            allFields.forEach { field ->
                val poetType = field.type.toPoet().copy(nullable = field.nullable)

                if (field.passViaConstructorParameter) {
                    ctor.addParameter(field.name, poetType)
                }

                addProperty(PropertySpec.builder(field.name, poetType).apply {
                    mutable(field.mutable)
                    addModifiers(KModifier.OVERRIDE)

                    if (field.needsDescriptorApiAnnotation) {
                        addAnnotation(
                            AnnotationSpec
                                .builder(descriptorApiAnnotation)
                                .useSiteTarget(AnnotationSpec.UseSiteTarget.PROPERTY)
                                .build()
                        )
                    }

                    if (field is ListField && field.isChild && !field.passViaConstructorParameter) {
                        initializer("BirChildElementList(this)")
                    } else if (field is SingleField && field.isChild && field.mutable) {
                        addProperty(
                            PropertySpec.builder(field.backingFieldName, poetType)
                                .mutable(true)
                                .addModifiers(KModifier.PRIVATE)
                                .apply {
                                    if (field.initializeToThis) initializer("this") else initializer("%N", field.name)
                                }
                                .build()
                        )
                        getter(
                            FunSpec.getterBuilder()
                                .addCode("return ${field.backingFieldName}")
                                .build()
                        )
                    } else {
                        if (field.initializeToThis) initializer("this") else initializer("%N", field.name)
                    }

                    if (field is SingleField && field.isChild && field.mutable) {
                        setter(
                            FunSpec.setterBuilder()
                                .addParameter(ParameterSpec("value", poetType))
                                .apply {
                                    addCode("if (${field.backingFieldName} != value) {\n")
                                    addCode("    replaceChild(${field.backingFieldName}, value)\n")
                                    addCode("    ${field.backingFieldName} = value\n")
                                    addCode("}\n")
                                }.build()
                        )
                    }
                }.build())
            }

            if (allFields.any { it.needsDescriptorApiAnnotation }) {
                ctor.addAnnotation(descriptorApiAnnotation)
            }

            val allChildren = allFields.filter { it.isChild }
            allChildren.forEachIndexed { fieldIndex, child ->
                if (child is SingleField) {
                    ctor.addCode("initChild(${child.backingFieldName})\n")
                }
            }


            primaryConstructor(ctor.build())
        }.build()

        yield(printTypeCommon(generationPath, element.elementImplName.packageName, elementType))
    }
}

private val descriptorApiAnnotation = ClassName("org.jetbrains.kotlin.ir", "ObsoleteDescriptorBasedAPI")
private val elementAccept = MemberName(Packages.tree + ".traversal", "accept", true)
