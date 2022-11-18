// !LANGUAGE: +AllowContractsForCustomFunctions +UseReturnsEffect
// !OPT_IN: kotlin.contracts.ExperimentalContracts
// !DIAGNOSTICS: -INVISIBLE_REFERENCE -INVISIBLE_MEMBER

import kotlin.contracts.*

fun isString(x: Any?): Boolean {
    contract {
        returns(true) implies (x is String)
    }
    return x is String
}

fun exhaustive(x: Any?) {
    when (isString(x)) {
        true -> x.length
        false -> <!ARGUMENT_TYPE_MISMATCH!>x.<!UNRESOLVED_REFERENCE!>length<!><!>
    }

    when(!isString(x)) {
        true -> <!ARGUMENT_TYPE_MISMATCH!>x.<!UNRESOLVED_REFERENCE!>length<!><!>
        false -> x.length
    }
}

fun smartcastInElse(x: Any?) {
    when (isString(x)) {
        false -> <!ARGUMENT_TYPE_MISMATCH!>x.<!UNRESOLVED_REFERENCE!>length<!><!>
        else -> x.length
    }

    when (!isString(x)) {
        true -> <!ARGUMENT_TYPE_MISMATCH!>x.<!UNRESOLVED_REFERENCE!>length<!><!>
        else -> x.length
    }
}
