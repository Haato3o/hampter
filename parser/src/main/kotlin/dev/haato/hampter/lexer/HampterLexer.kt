package dev.haato.hampter.lexer

import dev.haato.hampter.lexer.stream.TokenizableStream
import dev.haato.hampter.lexer.token.HampterToken
import dev.haato.hampter.metadata.FileMetadata

class HampterLexer(
    private val stream: TokenizableStream
) {

    fun tokenize(): List<HampterToken> {
        val tokens: MutableList<HampterToken> = mutableListOf()

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
                    HampterToken(
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
