package dev.haato.sarah.lexer.stream

class TokenizableStream(
    val fileName: String,
    private val text: String
) {
    private var cursor: Int = 0
    private var currentColumn: Int = 0
    private var currentRow: Int = 0

    fun peek(): Char? {
        return text.elementAtOrNull(cursor)
    }

    fun consume(): Char? {
        return text.elementAtOrNull(cursor++)?.also {
            when (it) {
                '\n' -> {
                    currentRow++
                    currentColumn = 0
                }

                else -> {
                    currentColumn++
                }
            }
        }
    }

    fun discard() {
        consume()
    }

    fun isOver(): Boolean {
        return text.length <= cursor
    }

    fun getCurrentRow() = currentRow

    fun getCursorPosition() = currentColumn
}
