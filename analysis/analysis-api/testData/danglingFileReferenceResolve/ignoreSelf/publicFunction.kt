// DANGLING_FILE_RESOLUTION_MODE: IGNORE_SELF

class Foo() {
    fun call() {}

    private fun foo() {
        c<caret>all()
    }
}