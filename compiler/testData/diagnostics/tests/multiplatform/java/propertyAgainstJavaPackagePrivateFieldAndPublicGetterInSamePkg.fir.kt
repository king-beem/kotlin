// MODULE: m1-common
// FILE: common.kt
package com.example

expect class Foo {
    val foo: Int
}

// MODULE: m2-jvm()()(m1-common)
// FILE: jvm.kt
package com.example

actual typealias <!NO_ACTUAL_CLASS_MEMBER_FOR_EXPECTED_CLASS!>Foo<!> = JavaFoo

interface I {
    val foo: Int
}
// FILE: JavaFoo.java
package com.example;

public class JavaFoo implements I {
    final int foo = 1;

    @Override
    public int getFoo() {
        return 0;
    }
}
