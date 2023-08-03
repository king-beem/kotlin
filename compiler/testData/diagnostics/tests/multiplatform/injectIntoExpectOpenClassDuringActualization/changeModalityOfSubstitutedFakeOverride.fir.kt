// MODULE: m1-common
// FILE: common.kt

open class Base<T> {
    <!INCOMPATIBLE_MATCHING{JVM}!>open fun foo(t: T) {}<!>
}

<!INCOMPATIBLE_MATCHING{JVM}!>expect open class Foo : Base<String><!>

// MODULE: m2-jvm()()(m1-common)
// FILE: jvm.kt

actual open <!ACTUAL_CLASSIFIER_MUST_HAVE_THE_SAME_MEMBERS_AS_NON_FINAL_EXPECT_CLASSIFIER!>class Foo<!> : Base<String>() {
    <!MODALITY_OVERRIDE_IN_NON_FINAL_EXPECT_CLASSIFIER_ACTUALIZATION!>final<!> override fun foo(t: String) {}
}
