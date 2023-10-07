package dev.haato.hampter.ast

enum class Operator(
    val symbol: String,
    val precedence: Int
) {
    MUL("*", 1),
    DIV("/", 1),
    MOD("%", 1),

    ADD("+", 2),
    SUB("-", 2),

    BINARY_SHIFT_LEFT("<<", 3),
    BINARY_SHIFT_RIGHT(">>", 3),

    LESSER("<", 4),
    GREATER(">", 4),
    LEQ("<=", 4),
    GREQ("<", 4),
    EQUAL("==", 4),
    NEQUAL("!=", 4),

    BINARY_AND("&", 5),
    BINARY_XOR("^", 6),
    BINARY_OR("|", 7),

    LOGICAL_AND("&&", 8),
    LOGICAL_OR("||", 9),

    TERNARY_IF("?", 10),
    TERNARY_ELSE(":", 10),
    NULLABLE("?:", 10);

    companion object {
        val operators = Operator.values().map { it.symbol }.toSet()
        val operatorsByPrecedence = Operator.values().groupBy { it.precedence }
        val highestPrecedence = Operator.values().maxBy { it.precedence }.precedence

        fun bySymbol(symbol: String) = Operator.values().first { it.symbol == symbol }
    }
}
