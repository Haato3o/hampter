package dev.haato.sarah.lexer.stream

object TokenizableStreamFactory {

    fun fromText(text: String): TokenizableStream {
        return StringTokenizableStream(DEFAULT_NAME, text)
    }

    fun fromFile(path: String): TokenizableStream {
        return TokenizableFileStream(path)
    }

    private const val DEFAULT_NAME = "stream"
}