package dev.haato.sarah.ast

enum class Arithmetic(val symbol: String) {
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/"),
    AND("&"),
    OR("||"),
    XOR("^"),
    SHIFT_LEFT("<<"),
    SHIFT_RIGHT(">>"),
    NOT("!")
}
