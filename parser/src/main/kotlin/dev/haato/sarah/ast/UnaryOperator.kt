package dev.haato.sarah.ast

enum class UnaryOperator(
    val symbol: String,
    val precedence: Int
) {
    POSITIVE("+", 3),
    NEGATIVE("-", 3),
    DEREFERENCE("*", 3),
    ADDRESS_OF("&", 3),
    LOGICAL_NOT("!", 3),
    BINARY_NOT("~", 3);

    companion object {
        val unaryOperators = UnaryOperator.values().map { it.symbol }.toSet()

        fun bySymbol(symbol: String) = UnaryOperator.values().first { it.symbol == symbol }
    }
}