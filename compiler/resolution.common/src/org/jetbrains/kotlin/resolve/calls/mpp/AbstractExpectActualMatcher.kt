/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.mpp

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.mpp.*
import org.jetbrains.kotlin.resolve.multiplatform.ExpectActualMatchingCompatibility
import org.jetbrains.kotlin.types.model.KotlinTypeMarker
import org.jetbrains.kotlin.types.model.TypeSubstitutorMarker
import org.jetbrains.kotlin.utils.SmartList
import org.jetbrains.kotlin.utils.keysToMap
import org.jetbrains.kotlin.utils.zipIfSizesAreEqual

/**
 * This object is responsible for matching of expect-actual pairs.
 *
 * - If you want to report the diagnostics then the declarations needs to be checked after they are matched ([AbstractExpectActualChecker]
 *   is responsible for the checking)
 * - In all other cases you only need the "matching" data
 *
 * See `/docs/fir/k2_kmp.md` for details
 */
object AbstractExpectActualMatcher {
    fun getCallablesMatchingCompatibility(
        expectDeclaration: CallableSymbolMarker,
        actualDeclaration: CallableSymbolMarker,
        expectContainingClass: RegularClassSymbolMarker?,
        actualContainingClass: RegularClassSymbolMarker?,
        context: ExpectActualMatchingContext<*>,
    ): ExpectActualMatchingCompatibility = with (context) {
        val expectTypeParameters = expectContainingClass?.typeParameters.orEmpty()
        val actualTypeParameters = actualContainingClass?.typeParameters.orEmpty()
        val parentSubstitutor = (expectTypeParameters zipIfSizesAreEqual actualTypeParameters)
            ?.let { createExpectActualTypeParameterSubstitutor(it, parentSubstitutor = null) }
        getCallablesCompatibility(
            expectDeclaration,
            actualDeclaration,
            parentSubstitutor,
            expectContainingClass,
            actualContainingClass
        )
    }

    fun <T : DeclarationSymbolMarker> matchSingleExpectTopLevelDeclarationAgainstPotentialActuals(
        expectDeclaration: DeclarationSymbolMarker,
        actualDeclarations: List<DeclarationSymbolMarker>,
        context: ExpectActualMatchingContext<T>,
    ): DeclarationSymbolMarker? = with(context) {
        matchSingleExpectAgainstPotentialActuals(
            expectDeclaration,
            actualDeclarations,
            substitutor = null,
            expectClassSymbol = null,
            actualClassSymbol = null,
            mismatchedMembers = null,
        )
    }

    fun matchClassifiers(
        expectClassSymbol: RegularClassSymbolMarker,
        actualClassLikeSymbol: ClassLikeSymbolMarker,
        context: ExpectActualMatchingContext<*>,
    ): ExpectActualMatchingCompatibility = with(context) {
        // Can't check FQ names here because nested expected class may be implemented via actual typealias's expansion with the other FQ name
        check(expectClassSymbol.name == actualClassLikeSymbol.name) {
            "This function should be invoked only for declarations with the same name: $expectClassSymbol, $actualClassLikeSymbol"
        }
        check(actualClassLikeSymbol is RegularClassSymbolMarker || actualClassLikeSymbol is TypeAliasSymbolMarker) {
            "Incorrect actual classifier for $expectClassSymbol: $actualClassLikeSymbol"
        }
        ExpectActualMatchingCompatibility.MatchedSuccessfully
    }

    /**
     * Besides returning the matched declaration
     *
     * The function has an additional side effects:
     * - It adds mismatched members to `mismatchedMembers`
     * - It calls `onMatchedMembers` and `onMismatchedMembersFromClassScope` callbacks
     */
    context(ExpectActualMatchingContext<*>)
    internal fun matchSingleExpectAgainstPotentialActuals(
        expectMember: DeclarationSymbolMarker,
        actualMembers: List<DeclarationSymbolMarker>,
        substitutor: TypeSubstitutorMarker?,
        expectClassSymbol: RegularClassSymbolMarker?,
        actualClassSymbol: RegularClassSymbolMarker?,
        mismatchedMembers: MutableList<Pair<DeclarationSymbolMarker, Map<ExpectActualMatchingCompatibility.Mismatch, List<DeclarationSymbolMarker?>>>>?,
    ): DeclarationSymbolMarker? {
        val mapping = actualMembers.keysToMap { actualMember ->
            when (expectMember) {
                is CallableSymbolMarker -> getCallablesCompatibility(
                    expectMember,
                    actualMember as CallableSymbolMarker,
                    substitutor,
                    expectClassSymbol,
                    actualClassSymbol
                )

                is RegularClassSymbolMarker -> {
                    matchClassifiers(expectMember, actualMember as ClassLikeSymbolMarker, this@ExpectActualMatchingContext)
                }
                else -> error("Unsupported declaration: $expectMember ($actualMembers)")
            }
        }

        val matchedActualMembers = mutableListOf<DeclarationSymbolMarker>()
        val incompatibilityMap = mutableMapOf<ExpectActualMatchingCompatibility.Mismatch, MutableList<DeclarationSymbolMarker>>()
        for ((actualMember, compatibility) in mapping) {
            when (compatibility) {
                ExpectActualMatchingCompatibility.MatchedSuccessfully -> matchedActualMembers.add(actualMember)
                is ExpectActualMatchingCompatibility.Mismatch -> incompatibilityMap.getOrPut(compatibility) { SmartList() }.add(actualMember)
            }
        }

        if (matchedActualMembers.isNotEmpty()) {
            val actualMember = chooseFromMatchedActuals(matchedActualMembers)
            onMatchedMembers(expectMember, actualMember, expectClassSymbol, actualClassSymbol)
            return actualMember
        }

        mismatchedMembers?.add(expectMember to incompatibilityMap)
        onMismatchedMembersFromClassScope(expectMember, incompatibilityMap, expectClassSymbol, actualClassSymbol)
        return null
    }

    context(ExpectActualMatchingContext<*>)
    private fun getCallablesCompatibility(
        expectDeclaration: CallableSymbolMarker,
        actualDeclaration: CallableSymbolMarker,
        parentSubstitutor: TypeSubstitutorMarker?,
        expectContainingClass: RegularClassSymbolMarker?,
        actualContainingClass: RegularClassSymbolMarker?,
    ): ExpectActualMatchingCompatibility {
        checkCallablesInvariants(expectDeclaration, actualDeclaration)

        if (areEnumConstructors(expectDeclaration, actualDeclaration, expectContainingClass, actualContainingClass)) {
            return ExpectActualMatchingCompatibility.MatchedSuccessfully
        }

        val insideAnnotationClass = expectContainingClass?.classKind == ClassKind.ANNOTATION_CLASS

        if (expectDeclaration is FunctionSymbolMarker != actualDeclaration is FunctionSymbolMarker) {
            return ExpectActualMatchingCompatibility.CallableKind
        }

        val expectedReceiverType = expectDeclaration.extensionReceiverType
        val actualReceiverType = actualDeclaration.extensionReceiverType
        if ((expectedReceiverType != null) != (actualReceiverType != null)) {
            return ExpectActualMatchingCompatibility.ParameterShape
        }

        val expectedValueParameters = expectDeclaration.valueParameters
        val actualValueParameters = actualDeclaration.valueParameters
        if (!valueParametersCountCompatible(expectDeclaration, actualDeclaration, expectedValueParameters, actualValueParameters)) {
            return ExpectActualMatchingCompatibility.ParameterCount
        }

        val expectedTypeParameters = expectDeclaration.typeParameters
        val actualTypeParameters = actualDeclaration.typeParameters
        if (expectedTypeParameters.size != actualTypeParameters.size) {
            return ExpectActualMatchingCompatibility.FunctionTypeParameterCount
        }

        val substitutor = createExpectActualTypeParameterSubstitutor(
            (expectedTypeParameters zipIfSizesAreEqual actualTypeParameters)
                ?: error("expect/actual type parameters sizes are checked earlier"),
            parentSubstitutor
        )

        if (
            !areCompatibleTypeLists(
                expectedValueParameters.toTypeList(substitutor),
                actualValueParameters.toTypeList(createEmptySubstitutor()),
                insideAnnotationClass
            ) || !areCompatibleExpectActualTypes(
                expectedReceiverType?.let { substitutor.safeSubstitute(it) },
                actualReceiverType,
                parameterOfAnnotationComparisonMode = false
            )
        ) {
            return ExpectActualMatchingCompatibility.ParameterTypes
        }

        if (!areCompatibleTypeParameterUpperBounds(expectedTypeParameters, actualTypeParameters, substitutor)) {
            return ExpectActualMatchingCompatibility.FunctionTypeParameterUpperBounds
        }

        return ExpectActualMatchingCompatibility.MatchedSuccessfully
    }

    context(ExpectActualMatchingContext<*>)
    private fun valueParametersCountCompatible(
        expectDeclaration: CallableSymbolMarker,
        actualDeclaration: CallableSymbolMarker,
        expectValueParameters: List<ValueParameterSymbolMarker>,
        actualValueParameters: List<ValueParameterSymbolMarker>,
    ): Boolean {
        if (expectValueParameters.size == actualValueParameters.size) return true

        return if (expectDeclaration.isAnnotationConstructor() && actualDeclaration.isAnnotationConstructor()) {
            expectValueParameters.isEmpty() && actualValueParameters.all { it.hasDefaultValue }
        } else {
            false
        }
    }

    context(ExpectActualMatchingContext<*>)
    private fun chooseFromMatchedActuals(matchedActualDeclarations: Collection<DeclarationSymbolMarker>): DeclarationSymbolMarker {
        require(matchedActualDeclarations.isNotEmpty()) { "Expected non-empty actuals" }

        return matchedActualDeclarations.maxBy { getSortingPriorityForActual(it) }
    }

    /**
     * If actual is Java class, there might be two matched properties with the same name:
     * 1) from field;
     * 2) from method, which overrides Kotlin property.
     *
     * The field must always take precedence, if it has compatible visibility (and in Java only `public` can be compatible),
     * otherwise it must be deprioritized.
     */
    context(ExpectActualMatchingContext<*>)
    private fun getSortingPriorityForActual(actual: DeclarationSymbolMarker): Int {
        return if (actual is CallableSymbolMarker && actual.isJavaField) {
            if (actual.visibility == Visibilities.Public) 1 else -1
        } else 0
    }

    // ---------------------------------------- Utils ----------------------------------------

    context(ExpectActualMatchingContext<*>)
    private fun List<ValueParameterSymbolMarker>.toTypeList(substitutor: TypeSubstitutorMarker): List<KotlinTypeMarker> {
        return this.map { substitutor.safeSubstitute(it.returnType) }
    }
}
