// FIR_IDENTICAL
// MODULE: m1-common
// FILE: common.kt
interface I {
    val foo: Int
}

expect class Foo : I {
    override val foo: Int
}

// MODULE: m2-jvm()()(m1-common)
// FILE: jvm.kt
actual typealias Foo = JavaFoo

// FILE: JavaFoo.java
public final class JavaFoo implements I {
    @Override
    public int getFoo() {
        return 0;
    }
}
