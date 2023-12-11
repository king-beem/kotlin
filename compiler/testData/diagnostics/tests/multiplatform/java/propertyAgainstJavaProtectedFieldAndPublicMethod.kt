// MODULE: m1-common
// FILE: common.kt
interface I {
    val foo: Int
}

expect class Foo : I {
    override val <!AMBIGUOUS_ACTUALS{JVM}!>foo<!>: Int
}

// MODULE: m2-jvm()()(m1-common)
// FILE: jvm.kt
actual typealias Foo = JavaFoo

// FILE: JavaFoo.java
public class JavaFoo implements I {
    protected final int foo = 1;

    @Override
    public int getFoo() {
        return 0;
    }
}
