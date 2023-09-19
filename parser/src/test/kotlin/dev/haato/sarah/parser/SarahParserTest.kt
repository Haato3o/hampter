package dev.haato.sarah.parser

import dev.haato.sarah.lexer.SarahLexer
import dev.haato.sarah.lexer.stream.TokenizableStream
import io.kotest.core.spec.style.ShouldSpec

class SarahParserTest : ShouldSpec({
    context("parse") {
        val fileName = "example.kt"
        should("convert tokens into an abstract syntax tree") {
            val input = """
                if (variable > *variablePtr) {} else {}
            """.trimIndent()
            val stream = TokenizableStream(fileName, input)
            val tokens = SarahLexer(stream).tokenize()
            val tree = SarahParser(tokens).parse()

            println(tree)

        }
    }
})