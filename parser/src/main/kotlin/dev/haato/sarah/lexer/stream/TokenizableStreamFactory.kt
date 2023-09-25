package dev.haato.sarah.lexer.stream

object TokenizableStreamFactory {

    fun fromText(text: String): TokenizableStream {
        return StringTokenizableStream(text)
    }

    fun fromFile(path: String): TokenizableStream {
        return TokenizableFileStream(path)
    }
}
