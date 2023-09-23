package dev.haato.sarah.lexer

import dev.haato.sarah.lexer.stream.TokenizableStream
import dev.haato.sarah.lexer.token.SarahToken
import dev.haato.sarah.metadata.FileMetadata

class SarahLexer(
    private val stream: TokenizableStream
) {

    fun tokenize(): List<SarahToken> {
        val tokens: MutableList<SarahToken> = mutableListOf()

        stream.use {
            while (!it.isOver()) {
                val metadata = stream.buildMetadata()
                val currentTokenValue = when (stream.peek()) {
                    in scopeTokens -> stream.readSingleChar()
                    in specialTokens -> stream.readCharWhile(specialTokens)
                    '"' -> stream.readString()
                    in discardTokens -> {
                        stream.discard()
                        continue
                    }
                    else -> stream.readCharSequenceUntil(invalidStringTokens)
                }

                tokens.add(
                    SarahToken(
                        value = currentTokenValue,
                        metadata = metadata
                    )
                )
            }
        }

        return tokens
    }

    private fun TokenizableStream.readSingleChar(): String {
        return consume().toString()
    }

    private fun TokenizableStream.readCharWhile(continueTokens: Set<Char>): String {
        return buildString {
            do {
                when (peek()) {
                    in continueTokens -> append(consume())
                    else -> return@buildString
                }
            } while (peek() != null)
        }
    }

    private fun TokenizableStream.readString(): String {
        discard()
        val string = "\"${readCharSequenceUntil(setOf('"'))}\""
        discard()

        return string
    }

    private fun TokenizableStream.readCharSequenceUntil(stopTokens: Set<Char>): String {
        return buildString {
            do {
                when (peek()) {
                    in stopTokens -> return@buildString
                    else -> append(consume())
                }
            } while (peek() != null)
        }
    }

    private fun TokenizableStream.buildMetadata() = FileMetadata(
        line = getCurrentRow().toLong(),
        column = getCurrentColumn().toLong(),
        fileName = fileName
    )

    companion object {
        private val endOfStringTokens = setOf('\n', '\r', ' ')
        private val scopeTokens = setOf('{', '}', '(', ')', '[', ']')
        private val specialTokens =
            setOf(':', '+', '*', '/', '-', '>', '<', '=', '!', ';', '^', '&', '|', '.', ',', '?', '~')
        private val discardTokens = setOf('\t') + endOfStringTokens
        private val invalidStringTokens = endOfStringTokens + scopeTokens + specialTokens
    }
}
