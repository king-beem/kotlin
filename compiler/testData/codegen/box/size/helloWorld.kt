// TARGET_BACKEND: WASM

// RUN_THIRD_PARTY_OPTIMIZER
// WASM_DCE_EXPECTED_OUTPUT_SIZE: wasm 38_992
// WASM_DCE_EXPECTED_OUTPUT_SIZE:  mjs  4_552
// WASM_EXPECTED_OUTPUT_SIZE_AFTER_OPTIMIZER: 9_847

fun box(): String {
    println("Hello, World!")
    return "OK"
}
