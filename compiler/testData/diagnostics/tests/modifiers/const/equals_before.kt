// FIR_IDENTICAL
// !LANGUAGE: -IntrinsicConstEvaluation

const val TRUE = true
const val STR = "str"

const val equalsWithNull1 = <!CONST_VAL_WITH_NON_CONST_INITIALIZER, SENSELESS_COMPARISON!>1 == null<!>
const val equalsWithNull2 = <!CONST_VAL_WITH_NON_CONST_INITIALIZER, SENSELESS_COMPARISON!>null == null<!>
const val equalsWithNull3 = <!CONST_VAL_WITH_NON_CONST_INITIALIZER, SENSELESS_COMPARISON!>TRUE == null<!>
const val equalsWithNull4 = <!CONST_VAL_WITH_NON_CONST_INITIALIZER, SENSELESS_COMPARISON!>STR == null<!>
