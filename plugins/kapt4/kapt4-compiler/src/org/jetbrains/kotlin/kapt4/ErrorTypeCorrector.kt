/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.kapt4

import com.sun.tools.javac.tree.JCTree
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.types.*

private typealias SubstitutionMap = Map<String, Pair<KtTypeParameter, KtTypeProjection>>

context(Kapt4ContextForStubGeneration)
class ErrorTypeCorrector(
    private val converter: Kapt4StubGenerator,
    private val typeKind: TypeKind,
    file: KtFile
) {
    private val defaultType = treeMaker.FqName(Object::class.java.name)

    private val aliasedImports = mutableMapOf<String, JCTree.JCExpression>().apply {
        for (importDirective in file.importDirectives) {
            if (importDirective.isAllUnder) continue

            val aliasName = importDirective.aliasName ?: continue
            val importedFqName = importDirective.importedFqName ?: continue
// TODO
//            val importedReference = getReferenceExpression(importDirective.importedReference)
//                ?.let { bindingContext[BindingContext.REFERENCE_TARGET, it] }
//
//            if (importedReference is CallableDescriptor) continue
//
//            this[aliasName] = treeMaker.FqName(importedFqName)
        }
    }

    enum class TypeKind {
        RETURN_TYPE, METHOD_PARAMETER_TYPE, SUPER_TYPE, ANNOTATION
    }

    fun convert(type: KtTypeElement, substitutions: SubstitutionMap): JCTree.JCExpression {
        return when (type) {
            is KtUserType -> convertUserType(type, substitutions)
            is KtNullableType -> convert(type.innerType ?: return defaultType, substitutions)
            is KtFunctionType -> convertFunctionType(type, substitutions)
            else -> defaultType
        }
    }

    private fun convert(typeReference: KtTypeReference?, substitutions: SubstitutionMap): JCTree.JCExpression {
        val type = typeReference?.typeElement ?: return defaultType
        return convert(type, substitutions)
    }

    private fun convert(type: SimpleType): JCTree.JCExpression {
        // TODO now the raw Java type is returned. In future we need to properly convert all type parameters
        // return treeMaker.Type(KaptTypeMapper.mapType(type))
        TODO()
    }

    private fun convertUserType(type: KtUserType, substitutions: SubstitutionMap): JCTree.JCExpression {
        TODO()
//        val target = bindingContext[BindingContext.REFERENCE_TARGET, type.referenceExpression]
//
//        val baseExpression: JCTree.JCExpression
//
//        when (target) {
//            is TypeAliasDescriptor -> {
//                val typeAlias = target.source.getPsi() as? KtTypeAlias
//                val actualType = typeAlias?.getTypeReference() ?: return convert(target.expandedType)
//                return convert(actualType, typeAlias.getSubstitutions(type))
//            }
//
//            is ClassConstructorDescriptor -> {
//                val asmType = KaptTypeMapper.mapType(target.constructedClass.defaultType, TypeMappingMode.GENERIC_ARGUMENT)
//
//                baseExpression = converter.treeMaker.Type(asmType)
//            }
//
//            is ClassDescriptor -> {
//                // We only get here if some type were an error type. In other words, 'type' is either an error type or its argument,
//                // so it's impossible it to be unboxed primitive.
//                val asmType = KaptTypeMapper.mapType(target.defaultType, TypeMappingMode.GENERIC_ARGUMENT)
//
//                baseExpression = converter.treeMaker.Type(asmType)
//            }
//
//            else -> {
//                val referencedName = type.referencedName ?: return defaultType
//                val qualifier = type.qualifier
//
//                if (qualifier == null) {
//                    if (referencedName in substitutions) {
//                        val (typeParameter, projection) = substitutions.getValue(referencedName)
//                        return convertTypeProjection(projection, typeParameter.variance, emptyMap())
//                    }
//
//                    aliasedImports[referencedName]?.let { return it }
//                }
//
//                baseExpression = when {
//                    qualifier != null -> {
//                        val qualifierType = convertUserType(qualifier, substitutions)
//                        if (qualifierType === defaultType) return defaultType // Do not allow to use 'defaultType' as a qualifier
//                        treeMaker.Select(qualifierType, treeMaker.name(referencedName))
//                    }
//
//                    else -> treeMaker.SimpleName(referencedName)
//                }
//            }
//        }
//
//        val arguments = type.typeArguments
//        if (arguments.isEmpty()) return baseExpression
//
//        val typeReference = PsiTreeUtil.getParentOfType(type, KtTypeReference::class.java, true)
//        val kotlinType = bindingContext[BindingContext.TYPE, typeReference] ?: ErrorUtils.createErrorType(ErrorTypeKind.KAPT_ERROR_TYPE)
//
//        val typeSystem = SimpleClassicTypeSystemContext
//        val typeMappingMode = when (typeKind) {
//            //TODO figure out if the containing method is an annotation method
//            RETURN_TYPE -> typeSystem.getOptimalModeForReturnType(kotlinType, false)
//            METHOD_PARAMETER_TYPE -> typeSystem.getOptimalModeForValueParameter(kotlinType)
//            SUPER_TYPE -> TypeMappingMode.SUPER_TYPE
//            ANNOTATION -> TypeMappingMode.DEFAULT // see genAnnotation in org/jetbrains/kotlin/codegen/AnnotationCodegen.java
//        }.updateArgumentModeFromAnnotations(kotlinType, typeSystem)
//
//        val typeParameters = (target as? ClassifierDescriptor)?.typeConstructor?.parameters
//        return treeMaker.TypeApply(baseExpression, mapJListIndexed(arguments) { index, projection ->
//            val typeParameter = typeParameters?.getOrNull(index)
//            val typeArgument = kotlinType.arguments.getOrNull(index)
//
//            val variance = if (typeArgument != null && typeParameter != null) {
//                KotlinTypeMapper.getVarianceForWildcard(typeParameter, typeArgument, typeMappingMode)
//            } else {
//                null
//            }
//
//            convertTypeProjection(projection, variance, substitutions)
//        })
    }

    private fun convertTypeProjection(
        projection: KtTypeProjection,
        variance: Variance?,
        substitutions: SubstitutionMap
    ): JCTree.JCExpression {
        TODO()
//        fun unbounded() = treeMaker.Wildcard(treeMaker.TypeBoundKind(BoundKind.UNBOUND), null)
//
//        // Use unbounded wildcard when a generic argument can't be resolved
//        val argumentType = projection.typeReference ?: return unbounded()
//        val argumentExpression by lazy { convert(argumentType, substitutions) }
//
//        if (variance === Variance.INVARIANT) {
//            return argumentExpression
//        }
//
//        val projectionKind = projection.projectionKind
//
//        return when {
//            projectionKind === KtProjectionKind.STAR -> treeMaker.Wildcard(treeMaker.TypeBoundKind(BoundKind.UNBOUND), null)
//            projectionKind === KtProjectionKind.IN || variance === Variance.IN_VARIANCE ->
//                treeMaker.Wildcard(treeMaker.TypeBoundKind(BoundKind.SUPER), argumentExpression)
//
//            projectionKind === KtProjectionKind.OUT || variance === Variance.OUT_VARIANCE ->
//                treeMaker.Wildcard(treeMaker.TypeBoundKind(BoundKind.EXTENDS), argumentExpression)
//
//            else -> argumentExpression // invariant
//        }
    }

    private fun convertFunctionType(type: KtFunctionType, substitutions: SubstitutionMap): JCTree.JCExpression {
        TODO()
//        val receiverType = type.receiverTypeReference
//        var parameterTypes = mapJList(type.parameters) { convert(it.typeReference, substitutions) }
//        val returnType = convert(type.returnTypeReference, substitutions)
//
//        if (receiverType != null) {
//            parameterTypes = parameterTypes.prepend(convert(receiverType, substitutions))
//        }
//
//        parameterTypes = parameterTypes.append(returnType)
//
//        val treeMaker = converter.treeMaker
//        return treeMaker.TypeApply(treeMaker.SimpleName("Function" + (parameterTypes.size - 1)), parameterTypes)
    }

    private fun KtTypeParameterListOwner.getSubstitutions(actualType: KtUserType): SubstitutionMap {
        TODO()
//        val arguments = actualType.typeArguments
//
//        if (typeParameters.size != arguments.size) {
//            val kaptContext = converter.kaptContext
//            val error = kaptContext.kaptError("${typeParameters.size} parameters are expected but ${arguments.size} passed")
//            kaptContext.compiler.log.report(error)
//            return emptyMap()
//        }
//
//        val substitutionMap = mutableMapOf<String, Pair<KtTypeParameter, KtTypeProjection>>()
//
//        typeParameters.forEachIndexed { index, typeParameter ->
//            val name = typeParameter.name ?: return@forEachIndexed
//            substitutionMap[name] = Pair(typeParameter, arguments[index])
//        }
//
//        return substitutionMap
    }
}

fun KotlinType.containsErrorTypes(allowedDepth: Int = 10): Boolean {
    // Need to limit recursion depth in case of complex recursive generics
    if (allowedDepth <= 0) {
        return false
    }

    if (this.isError) return true
    if (this.arguments.any { !it.isStarProjection && it.type.containsErrorTypes(allowedDepth - 1) }) return true
    return false
}

