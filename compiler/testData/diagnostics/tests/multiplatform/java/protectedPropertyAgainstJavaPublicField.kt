// FIR_IDENTICAL
// MODULE: m1-common
// FILE: common.kt
expect open class Foo {
    final protected val foo: Int
}

// MODULE: m2-jvm()()(m1-common)
// FILE: jvm.kt
actual typealias Foo = JavaFoo

// FILE: JavaFoo.java
public class JavaFoo {
    public final int foo = 1;
}
