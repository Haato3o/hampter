package dev.haato.sarah.parser

object Keywords {
    const val NAMESPACE = "namespace"
    const val LET = "let"
    const val FUN = "fun"
    const val STRUCT = "struct"
    const val IF = "if"
    const val ELSE = "else"
}

object Tokens {
    const val SEMI_COLON = ";"
    const val PERIOD = "."
    const val COLON = ":"
    const val SCOPE_START = "{"
    const val SCOPE_END = "}"
    const val PARENTHESIS_START = "("
    const val PARENTHESIS_END = ")"
    const val QUESTION_MARK = "?"
    const val ARROW = "->"
    const val COMMA = ","
    const val EQUAL = "="
}
