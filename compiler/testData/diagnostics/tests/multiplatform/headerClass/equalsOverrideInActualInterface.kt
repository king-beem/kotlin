// MODULE: m1-common
expect interface Base

// MODULE: m1-jvm()()(m1-common)
<!ACTUAL_CLASSIFIER_MUST_HAVE_THE_SAME_MEMBERS_AS_NON_FINAL_EXPECT_CLASSIFIER!>actual interface Base<!> {
    override fun <!MODALITY_OVERRIDE_IN_NON_FINAL_EXPECT_CLASSIFIER_ACTUALIZATION!>equals<!>(other: Any?): Boolean
}
