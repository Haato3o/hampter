package dev.haato.sarah.lexer.stream

import java.io.BufferedReader
import java.io.File

class TokenizableFileStream(
    filePath: String
) : TokenizableStream {

    private val stream: BufferedReader = File(filePath).bufferedReader()
    private var nextChar: Char? = null
    private var currentChar: Char? = null
    private var currentRow = 0
    private var currentColumn = 0
    override val fileName: String = File(filePath).name

    init {
        repeat(2) { consume() }
    }

    override fun peek(): Char? {
        return nextChar
    }

    override fun consume(): Char? {
        currentChar = nextChar
        nextChar = readNextChar()

        return currentChar?.also {
            when (it) {
                '\n' -> {
                    currentRow++
                    currentColumn = 0
                }

                else -> currentColumn++
            }
        }
    }

    override fun discard() {
        consume()
    }

    override fun isOver(): Boolean {
        return currentChar == null
    }

    override fun getCurrentRow() = currentRow

    override fun getCurrentColumn() = currentColumn

    private fun readNextChar(): Char? {
        return when (val charCode = stream.read()) {
            INVALID_CHAR -> null
            else -> charCode.toChar()
        }
    }

    override fun close() {
        stream.close()
    }

    companion object {
        private const val INVALID_CHAR = -1
    }
}
