package dev.haato.hampter.lexer.stream

import java.io.Closeable

interface TokenizableStream : Closeable {

    val fileName: String

    fun peek(): Char?
    fun consume(): Char?
    fun discard()
    fun isOver(): Boolean
    fun getCurrentRow(): Int
    fun getCurrentColumn(): Int
}
