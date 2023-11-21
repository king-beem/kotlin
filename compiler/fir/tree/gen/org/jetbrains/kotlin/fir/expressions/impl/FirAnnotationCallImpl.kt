/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// This file was generated automatically. See compiler/fir/tree/tree-generator/Readme.md.
// DO NOT MODIFY IT MANUALLY.

@file:Suppress("DuplicatedCode", "unused")

package org.jetbrains.kotlin.fir.expressions.impl

import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.descriptors.annotations.AnnotationUseSiteTarget
import org.jetbrains.kotlin.fir.MutableOrEmptyList
import org.jetbrains.kotlin.fir.builder.toMutableOrEmpty
import org.jetbrains.kotlin.fir.expressions.*
import org.jetbrains.kotlin.fir.references.FirReference
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirTypeProjection
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.types.coneTypeOrNull
import org.jetbrains.kotlin.fir.visitors.*

internal class FirAnnotationCallImpl(
    override val source: KtSourceElement?,
    override var useSiteTarget: AnnotationUseSiteTarget?,
    override var annotationTypeRef: FirTypeRef,
    override var typeArguments: MutableOrEmptyList<FirTypeProjection>,
    override var argumentList: FirArgumentList,
    override var calleeReference: FirReference,
    override var argumentMapping: FirAnnotationArgumentMapping,
    override var annotationResolvePhase: FirAnnotationResolvePhase,
) : FirAnnotationCall() {
    @OptIn(UnresolvedExpressionTypeAccess::class)
    override val coneTypeOrNull: ConeKotlinType?
        get() = annotationTypeRef.coneTypeOrNull
    override val annotations: List<FirAnnotation>
        get() = emptyList()

    override fun <R, D> acceptChildren(visitor: FirVisitor<R, D>, data: D) {
        annotationTypeRef.accept(visitor, data)
        typeArguments.forEach { it.accept(visitor, data) }
        argumentList.accept(visitor, data)
        calleeReference.accept(visitor, data)
    }

    override fun <D> transformChildren(transformer: FirTransformer<D>, data: D): FirAnnotationCallImpl {
        transformAnnotationTypeRef(transformer, data)
        transformTypeArguments(transformer, data)
        argumentList = argumentList.transform(transformer, data)
        transformCalleeReference(transformer, data)
        return this
    }

    override fun <D> transformAnnotations(transformer: FirTransformer<D>, data: D): FirAnnotationCallImpl {
        return this
    }

    override fun <D> transformAnnotationTypeRef(transformer: FirTransformer<D>, data: D): FirAnnotationCallImpl {
        annotationTypeRef = annotationTypeRef.transform(transformer, data)
        return this
    }

    override fun <D> transformTypeArguments(transformer: FirTransformer<D>, data: D): FirAnnotationCallImpl {
        typeArguments.transformInplace(transformer, data)
        return this
    }

    override fun <D> transformCalleeReference(transformer: FirTransformer<D>, data: D): FirAnnotationCallImpl {
        calleeReference = calleeReference.transform(transformer, data)
        return this
    }

    override fun replaceConeTypeOrNull(newConeTypeOrNull: ConeKotlinType?) {}

    override fun replaceAnnotations(newAnnotations: List<FirAnnotation>) {}

    override fun replaceUseSiteTarget(newUseSiteTarget: AnnotationUseSiteTarget?) {
        useSiteTarget = newUseSiteTarget
    }

    override fun replaceAnnotationTypeRef(newAnnotationTypeRef: FirTypeRef) {
        annotationTypeRef = newAnnotationTypeRef
    }

    override fun replaceTypeArguments(newTypeArguments: List<FirTypeProjection>) {
        typeArguments = newTypeArguments.toMutableOrEmpty()
    }

    override fun replaceArgumentList(newArgumentList: FirArgumentList) {
        argumentList = newArgumentList
    }

    override fun replaceCalleeReference(newCalleeReference: FirReference) {
        calleeReference = newCalleeReference
    }

    override fun replaceArgumentMapping(newArgumentMapping: FirAnnotationArgumentMapping) {
        argumentMapping = newArgumentMapping
    }

    override fun replaceAnnotationResolvePhase(newAnnotationResolvePhase: FirAnnotationResolvePhase) {
        annotationResolvePhase = newAnnotationResolvePhase
    }
}
