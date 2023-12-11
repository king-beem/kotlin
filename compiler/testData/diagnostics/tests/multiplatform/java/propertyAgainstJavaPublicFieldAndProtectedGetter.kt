// FIR_IDENTICAL
// MODULE: m1-common
// FILE: common.kt
expect class Foo {
    val <!AMBIGUOUS_ACTUALS{JVM}!>foo<!>: Int
}

// MODULE: m2-jvm()()(m1-common)
// FILE: jvm.kt
actual typealias Foo = JavaFoo

abstract class A {
    protected abstract val foo: Int
}

// FILE: JavaFoo.java
public class JavaFoo extends A {
    public final int foo = 1;

    @Override
    protected int getFoo() {
        return 0;
    }
}
