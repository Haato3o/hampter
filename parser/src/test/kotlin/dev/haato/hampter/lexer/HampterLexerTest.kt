package dev.haato.hampter.lexer

import dev.haato.hampter.lexer.stream.StringTokenizableStream
import dev.haato.hampter.lexer.token.HampterToken
import dev.haato.hampter.metadata.FileMetadata
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainInOrder

class HampterLexerTest : ShouldSpec({

    context("tokenize") {
        val fileName = "example.ham"

        should("tokenize simple code") {
            val input = """
                namespace example;
                
                fun getStringLength(string: String) -> Int {
                    return string.length;
                }
            """.trimIndent()
            val expectedTokens = listOf(
                "namespace", "example", ";",
                "fun", "getStringLength", "(", "string", ":", "String", ")", "->", "Int", "{",
                "return", "string", ".", "length", ";",
                "}"
            )
            val stream = StringTokenizableStream(input)
            val lexer = HampterLexer(stream)
            val result = lexer.tokenize()
            val resultTokens = result.map { it.value }

            resultTokens shouldContainInOrder expectedTokens
        }

        should("generate meaninful metadata") {
            val input = """
                namespace example;
                
                struct Person { name: String, age: Int }
            """.trimIndent()
            val expected = listOf(
                HampterToken(value = "namespace", metadata = FileMetadata(0, 0, fileName)),
                HampterToken(value = "example", metadata = FileMetadata(10, 0, fileName)),
                HampterToken(value = ";", metadata = FileMetadata(17, 0, fileName)),
                HampterToken(value = "struct", metadata = FileMetadata(0, 2, fileName)),
                HampterToken(value = "Person", metadata = FileMetadata(7, 2, fileName)),
                HampterToken(value = "{", metadata = FileMetadata(14, 2, fileName)),
                HampterToken(value = "name", metadata = FileMetadata(16, 2, fileName)),
                HampterToken(value = ":", metadata = FileMetadata(20, 2, fileName)),
                HampterToken(value = "String", metadata = FileMetadata(22, 2, fileName)),
                HampterToken(value = ",", metadata = FileMetadata(28, 2, fileName)),
                HampterToken(value = "age", metadata = FileMetadata(30, 2, fileName)),
                HampterToken(value = ":", metadata = FileMetadata(33, 2, fileName)),
                HampterToken(value = "Int", metadata = FileMetadata(35, 2, fileName)),
                HampterToken(value = "}", metadata = FileMetadata(39, 2, fileName)),
            )
            val stream = StringTokenizableStream(input)
            val lexer = HampterLexer(stream)

            lexer.tokenize() shouldContainInOrder expected
        }
    }
})
