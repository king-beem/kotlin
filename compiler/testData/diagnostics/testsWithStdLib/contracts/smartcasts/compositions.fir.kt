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

fun testEqualsWithConstant(x: Any?) {
    if (isString(x) == true) {
        x.length
    }
    else {
        <!ARGUMENT_TYPE_MISMATCH!>x.<!UNRESOLVED_REFERENCE!>length<!><!>
    }
}

fun testNotEqualsWithConstant(x: Any?) {
    if (isString(x) != true) {
        <!ARGUMENT_TYPE_MISMATCH!>x.<!UNRESOLVED_REFERENCE!>length<!><!>
    }
    else {
        x.length
    }
}

fun unknownFunction(): Any? = 42

fun testEqualsWithUnknown(x: Any?) {
    if (isString(x) == unknownFunction()) {
        <!ARGUMENT_TYPE_MISMATCH!>x.<!UNRESOLVED_REFERENCE!>length<!><!>
    }
    else {
        <!ARGUMENT_TYPE_MISMATCH!>x.<!UNRESOLVED_REFERENCE!>length<!><!>
    }
    x.<!UNRESOLVED_REFERENCE!>length<!>
}

fun testNotEqualsWithUnknown(x: Any?) {
    if (isString(x) != unknownFunction()) {
        <!ARGUMENT_TYPE_MISMATCH!>x.<!UNRESOLVED_REFERENCE!>length<!><!>
    }
    else {
        <!ARGUMENT_TYPE_MISMATCH!>x.<!UNRESOLVED_REFERENCE!>length<!><!>
    }
    x.<!UNRESOLVED_REFERENCE!>length<!>
}

fun testEqualsWithVariable(x: Any?, b: Boolean) {
    if (isString(x) == b) {
        <!ARGUMENT_TYPE_MISMATCH!>x.<!UNRESOLVED_REFERENCE!>length<!><!>
    }
    else {
        <!ARGUMENT_TYPE_MISMATCH!>x.<!UNRESOLVED_REFERENCE!>length<!><!>
    }
}

fun testNotEqualsWithVariable(x: Any?, b: Boolean) {
    if (isString(x) != b) {
        <!ARGUMENT_TYPE_MISMATCH!>x.<!UNRESOLVED_REFERENCE!>length<!><!>
    }
    else {
        <!ARGUMENT_TYPE_MISMATCH!>x.<!UNRESOLVED_REFERENCE!>length<!><!>
    }
}
