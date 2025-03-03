// MODULE: m1-common
// FILE: common.kt
expect class Foo {
    val <!AMBIGUOUS_ACTUALS{JVM}!>foo<!>: Int
}

// MODULE: m2-jvm()()(m1-common)
// FILE: jvm.kt
actual typealias Foo = JavaFoo

interface I {
    val foo: Int
}

// FILE: JavaFoo.java
public class JavaFoo implements I {
    public final int foo = 1;

    @Override
    public int getFoo() {
        return 0;
    }
}
