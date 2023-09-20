package dev.haato.sarah.parser

import dev.haato.sarah.lexer.SarahLexer
import dev.haato.sarah.lexer.stream.StringTokenizableStream
import io.kotest.core.spec.style.ShouldSpec

class SarahParserTest : ShouldSpec({
    context("parse") {
        val fileName = "example.kt"
        should("convert tokens into an abstract syntax tree") {
            val input = """
                runFunction(10, anotherFunction("poggies"), true || true && false);
            """.trimIndent()
            val stream = StringTokenizableStream(fileName, input)
            val tokens = SarahLexer(stream).tokenize()
            val tree = SarahParser(tokens).parse()

            println(tree)

        }
    }
})