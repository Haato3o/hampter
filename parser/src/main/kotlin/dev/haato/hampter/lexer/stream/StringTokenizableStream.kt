package dev.haato.hampter.lexer.stream

class StringTokenizableStream(
    private val text: String
) : TokenizableStream {
    override val fileName: String = "example.ham"
    private var cursor: Int = 0
    private var currentColumn: Int = 0
    private var currentRow: Int = 0

    override fun peek(): Char? {
        return text.elementAtOrNull(cursor)
    }

    override fun consume(): Char? {
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

    override fun discard() {
        consume()
    }

    override fun isOver(): Boolean {
        return text.length <= cursor
    }

    override fun getCurrentRow() = currentRow

    override fun getCurrentColumn() = currentColumn

    @Suppress("EmptyFunctionBlock")
    override fun close() {}
}
