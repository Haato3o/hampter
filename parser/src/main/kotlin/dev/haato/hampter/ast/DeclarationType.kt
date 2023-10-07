package dev.haato.hampter.ast

enum class DeclarationType(val label: String) {
    CONSTANT("const"),
    VARIABLE("let");

    companion object {
        val values = DeclarationType.values().map { it.label }.toSet()

        fun fromLabel(label: String) = DeclarationType.values().first { label == it.label }
    }
}
