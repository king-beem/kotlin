/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.analysis.collectors

import org.jetbrains.kotlin.KtFakeSourceElementKind
import org.jetbrains.kotlin.fir.FirAnnotationContainer
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.PrivateForInline
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.declarations.builder.buildReceiverParameter
import org.jetbrains.kotlin.fir.expressions.*
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.visitors.FirDefaultVisitor
import org.jetbrains.kotlin.fir.whileAnalysing
import org.jetbrains.kotlin.jvm.specialization.annotations.Monomorphic
import org.jetbrains.kotlin.name.Name

abstract class AbstractDiagnosticCollectorVisitor(
    @set:PrivateForInline var context: CheckerContext,
) : FirDefaultVisitor<Unit, Nothing?>() {

    protected open fun shouldVisitDeclaration(declaration: FirDeclaration) = true
    protected open fun onDeclarationExit(declaration: FirDeclaration) {}

    protected open fun visitNestedElements(element: FirElement) {
        element.acceptChildren(this, null)
    }

    protected abstract fun checkElement(element: FirElement)

    override fun <@Monomorphic TE : FirElement>visitElement(element: TE, data: Nothing?) {
        if (element is FirAnnotationContainer) {
            visitAnnotationContainer(element, data)
            return
        }
        checkElement(element)
        visitNestedElements(element)
    }

    override fun visitAnnotationContainer(annotationContainer: FirAnnotationContainer, data: Nothing?) {
        withAnnotationContainer(annotationContainer) {
            checkElement(annotationContainer)
            visitNestedElements(annotationContainer)
        }
    }

    private fun visitJump(loopJump: FirLoopJump) {
        withAnnotationContainer(loopJump) {
            checkElement(loopJump)
            loopJump.target.labeledElement.takeIf { it is FirErrorLoop }?.accept(this, null)
        }
    }

    override fun visitBreakExpression(breakExpression: FirBreakExpression, data: Nothing?) {
        visitJump(breakExpression)
    }

    override fun visitContinueExpression(continueExpression: FirContinueExpression, data: Nothing?) {
        visitJump(continueExpression)
    }

    private fun visitClassAndChildren(klass: FirClass, type: ConeClassLikeType) {
        val receiverParameter = buildReceiverParameter {
            typeRef = buildResolvedTypeRef {
                this.type = type
            }
        }

        visitWithDeclarationAndReceiver(klass, (klass as? FirRegularClass)?.name, receiverParameter)
    }

    override fun visitRegularClass(regularClass: FirRegularClass, data: Nothing?) {
        withAnnotationContainer(regularClass) {
            visitClassAndChildren(regularClass, regularClass.defaultType())
        }
    }

    override fun visitAnonymousObjectExpression(anonymousObjectExpression: FirAnonymousObjectExpression, data: Nothing?) {
        anonymousObjectExpression.anonymousObject.accept(this, data)
    }

    override fun visitAnonymousObject(anonymousObject: FirAnonymousObject, data: Nothing?) {
        withAnnotationContainer(anonymousObject) {
            visitClassAndChildren(anonymousObject, anonymousObject.defaultType())
        }
    }

    override fun visitSimpleFunction(simpleFunction: FirSimpleFunction, data: Nothing?) {
        withAnnotationContainer(simpleFunction) {
            visitWithDeclarationAndReceiver(simpleFunction, simpleFunction.name, simpleFunction.receiverParameter)
        }
    }

    override fun visitConstructor(constructor: FirConstructor, data: Nothing?) {
        withAnnotationContainer(constructor) {
            visitWithDeclaration(constructor)
        }
    }

    override fun visitAnonymousFunctionExpression(anonymousFunctionExpression: FirAnonymousFunctionExpression, data: Nothing?) {
        visitAnonymousFunction(anonymousFunctionExpression.anonymousFunction, data)
    }

    override fun visitAnonymousFunction(anonymousFunction: FirAnonymousFunction, data: Nothing?) {
        withAnnotationContainer(anonymousFunction) {
            val labelName = anonymousFunction.label?.name?.let { Name.identifier(it) }
            visitWithDeclarationAndReceiver(
                anonymousFunction,
                labelName,
                anonymousFunction.receiverParameter
            )
        }
    }

    override fun visitProperty(property: FirProperty, data: Nothing?) {
        withAnnotationContainer(property) {
            visitWithDeclaration(property)
        }
    }

    override fun visitTypeAlias(typeAlias: FirTypeAlias, data: Nothing?) {
        withAnnotationContainer(typeAlias) {
            visitWithDeclaration(typeAlias)
        }
    }

    override fun visitPropertyAccessor(propertyAccessor: FirPropertyAccessor, data: Nothing?) {
        val property = context.containingDeclarations.last() as FirProperty
        withAnnotationContainer(propertyAccessor) {
            visitWithDeclarationAndReceiver(propertyAccessor, property.name, property.receiverParameter)
        }
    }

    override fun visitReceiverParameter(receiverParameter: FirReceiverParameter, data: Nothing?) {
        withAnnotationContainer(receiverParameter) {
            visitNestedElements(receiverParameter)
        }
    }

    override fun visitValueParameter(valueParameter: FirValueParameter, data: Nothing?) {
        withAnnotationContainer(valueParameter) {
            visitWithDeclaration(valueParameter)
        }
    }

    override fun visitEnumEntry(enumEntry: FirEnumEntry, data: Nothing?) {
        withAnnotationContainer(enumEntry) {
            visitWithDeclaration(enumEntry)
        }
    }

    override fun visitFile(file: FirFile, data: Nothing?) {
        withAnnotationContainer(file) {
            visitWithDeclaration(file)
        }
    }

    override fun visitAnonymousInitializer(anonymousInitializer: FirAnonymousInitializer, data: Nothing?) {
        visitWithDeclaration(anonymousInitializer)
    }

    override fun visitBlock(block: FirBlock, data: Nothing?) {
        withAnnotationContainer(block) {
            visitExpression(block, data)
        }
    }

    override fun visitTypeRef(typeRef: FirTypeRef, data: Nothing?) {
        if (typeRef.source != null && typeRef.source?.kind !is KtFakeSourceElementKind) {
            withTypeRefAnnotationContainer(typeRef) {
                checkElement(typeRef)
                visitNestedElements(typeRef)
            }
        }
    }

    override fun visitErrorTypeRef(errorTypeRef: FirErrorTypeRef, data: Nothing?) {
        visitResolvedTypeRef(errorTypeRef, data)
    }

    override fun visitResolvedTypeRef(resolvedTypeRef: FirResolvedTypeRef, data: Nothing?) {
        // Assuming no errors, the children of FirResolvedTypeRef (currently this can be FirAnnotationCalls) will also be present
        // as children in delegatedTypeRef. We should make sure those children are only visited once, otherwise diagnostics will be
        // collected twice: once through resolvedTypeRef's children and another through resolvedTypeRef.delegatedTypeRef's children.
        val resolvedTypeRefType = resolvedTypeRef.type
        if (resolvedTypeRefType is ConeErrorType) {
            super.visitResolvedTypeRef(resolvedTypeRef, data)
        }
        if (resolvedTypeRef.source?.kind is KtFakeSourceElementKind) return

        // Even though we don't visit the children of the resolvedTypeRef we still add it as an annotation container
        // and take care not to add the corresponding delegatedTypeRef. This is so that diagnostics will have access to
        // the FirResolvedTypeRef though the context, instead of, e.g., a FirUserTypeRef without cone types.
        withTypeRefAnnotationContainer(resolvedTypeRef) {
            if (resolvedTypeRefType !is ConeErrorType) {
                // We still need to check the resolvedTypeRef, since otherwise we couldn't report any diagnostics on them.
                checkElement(resolvedTypeRef)
            }
            resolvedTypeRef.delegatedTypeRef?.accept(this, data)
        }
    }

    override fun visitFunctionCall(functionCall: FirFunctionCall, data: Nothing?) {
        visitWithQualifiedAccessOrAnnotationCall(functionCall)
    }

    override fun visitQualifiedAccessExpression(qualifiedAccessExpression: FirQualifiedAccessExpression, data: Nothing?) {
        visitWithQualifiedAccessOrAnnotationCall(qualifiedAccessExpression)
    }

    override fun visitPropertyAccessExpression(propertyAccessExpression: FirPropertyAccessExpression, data: Nothing?) {
        visitWithQualifiedAccessOrAnnotationCall(propertyAccessExpression)
    }

    override fun visitAnnotationCall(annotationCall: FirAnnotationCall, data: Nothing?) {
        visitWithQualifiedAccessOrAnnotationCall(annotationCall)
    }

    override fun visitVariableAssignment(variableAssignment: FirVariableAssignment, data: Nothing?) {
        visitWithQualifiedAccessOrAnnotationCall(variableAssignment)
    }

    override fun visitGetClassCall(getClassCall: FirGetClassCall, data: Nothing?) {
        visitWithGetClassCall(getClassCall)
    }

    protected inline fun visitWithDeclaration(
        declaration: FirDeclaration,
        block: () -> Unit = { visitNestedElements(declaration) }
    ) {
        if (shouldVisitDeclaration(declaration)) {
            checkElement(declaration)
            withDeclaration(declaration) {
                block()
            }
            onDeclarationExit(declaration)
        }
    }

    private fun visitWithDeclarationAndReceiver(declaration: FirDeclaration, labelName: Name?, receiverParameter: FirReceiverParameter?) {
        visitWithDeclaration(declaration) {
            withLabelAndReceiverType(
                labelName,
                declaration,
                receiverParameter?.typeRef?.coneType
            ) {
                visitNestedElements(declaration)
            }
        }
    }

    private fun visitWithQualifiedAccessOrAnnotationCall(qualifiedAccessOrAnnotationCall: FirStatement) {
        return withQualifiedAccessOrAnnotationCall(qualifiedAccessOrAnnotationCall) {
            visitElement(qualifiedAccessOrAnnotationCall, null)
        }
    }

    private fun visitWithGetClassCall(getClassCall: FirGetClassCall) {
        return withGetClassCall(getClassCall) {
            visitElement(getClassCall, null)
        }
    }

    @OptIn(PrivateForInline::class)
    inline fun <R> withQualifiedAccessOrAnnotationCall(qualifiedAccessOrAnnotationCall: FirStatement, block: () -> R): R {
        val existingContext = context
        context = context.addQualifiedAccessOrAnnotationCall(qualifiedAccessOrAnnotationCall)
        try {
            return whileAnalysing(qualifiedAccessOrAnnotationCall) {
                block()
            }
        } finally {
            existingContext.dropQualifiedAccessOrAnnotationCall()
            context = existingContext
        }
    }


    @OptIn(PrivateForInline::class)
    inline fun <R> withGetClassCall(getClassCall: FirGetClassCall, block: () -> R): R {
        val existingContext = context
        context = context.addGetClassCall(getClassCall)
        try {
            return whileAnalysing(getClassCall) {
                block()
            }
        } finally {
            existingContext.dropGetClassCall()
            context = existingContext
        }
    }


    @OptIn(PrivateForInline::class)
    inline fun <R> withDeclaration(declaration: FirDeclaration, block: () -> R): R {
        val existingContext = context
        context = context.addDeclaration(declaration)
        try {
            return whileAnalysing(declaration) {
                block()
            }
        } finally {
            existingContext.dropDeclaration()
            context = existingContext
        }
    }


    @OptIn(PrivateForInline::class)
    inline fun <R> withLabelAndReceiverType(
        labelName: Name?,
        owner: FirDeclaration,
        type: ConeKotlinType?,
        block: () -> R
    ): R {
        val (implicitReceiverValue, implicitCompanionValues) = context.sessionHolder.collectImplicitReceivers(type, owner)
        val existingContext = context
        implicitCompanionValues.forEach { value ->
            context = context.addImplicitReceiver(null, value)
        }
        implicitReceiverValue?.let {
            context = context.addImplicitReceiver(labelName, it)
        }
        try {
            return block()
        } finally {
            context = existingContext
        }
    }


    @OptIn(PrivateForInline::class)
    inline fun <R> withAnnotationContainer(annotationContainer: FirAnnotationContainer, block: () -> R): R {
        val existingContext = context
        addSuppressedDiagnosticsToContext(annotationContainer)
        val notEmptyAnnotations = annotationContainer.annotations.isNotEmpty()
        if (notEmptyAnnotations) {
            context = context.addAnnotationContainer(annotationContainer)
        }
        return try {
            block()
        } finally {
            if (notEmptyAnnotations) {
                existingContext.dropAnnotationContainer()
            }
            context = existingContext
        }
    }

    private inline fun <R> withTypeRefAnnotationContainer(annotationContainer: FirTypeRef, block: () -> R): R {
        var containingTypeRef = context.annotationContainers.lastOrNull() as? FirResolvedTypeRef
        while (containingTypeRef != null && containingTypeRef.delegatedTypeRef != annotationContainer) {
            containingTypeRef = containingTypeRef.delegatedTypeRef as? FirResolvedTypeRef
        }
        return if (containingTypeRef != null) {
            block()
        } else {
            withAnnotationContainer(annotationContainer, block)
        }
    }

    @OptIn(PrivateForInline::class)
    fun addSuppressedDiagnosticsToContext(annotationContainer: FirAnnotationContainer) {
        val arguments = AbstractDiagnosticCollector.getDiagnosticsSuppressedForContainer(annotationContainer) ?: return
        context = context.addSuppressedDiagnostics(
            arguments,
            allInfosSuppressed = AbstractDiagnosticCollector.SUPPRESS_ALL_INFOS in arguments,
            allWarningsSuppressed = AbstractDiagnosticCollector.SUPPRESS_ALL_WARNINGS in arguments,
            allErrorsSuppressed = AbstractDiagnosticCollector.SUPPRESS_ALL_ERRORS in arguments
        ) as CheckerContext
    }
}
