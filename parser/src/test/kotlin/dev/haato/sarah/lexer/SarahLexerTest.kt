package dev.haato.sarah.lexer

import dev.haato.sarah.lexer.stream.StringTokenizableStream
import dev.haato.sarah.lexer.token.SarahToken
import dev.haato.sarah.metadata.FileMetadata
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainInOrder

class SarahLexerTest : ShouldSpec({

    context("tokenize") {
        val fileName = "example.srh"

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
            val lexer = SarahLexer(stream)
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
                SarahToken(value = "namespace", metadata = FileMetadata(0, 0, fileName)),
                SarahToken(value = "example", metadata = FileMetadata(10, 0, fileName)),
                SarahToken(value = ";", metadata = FileMetadata(17, 0, fileName)),
                SarahToken(value = "struct", metadata = FileMetadata(0, 2, fileName)),
                SarahToken(value = "Person", metadata = FileMetadata(7, 2, fileName)),
                SarahToken(value = "{", metadata = FileMetadata(14, 2, fileName)),
                SarahToken(value = "name", metadata = FileMetadata(16, 2, fileName)),
                SarahToken(value = ":", metadata = FileMetadata(20, 2, fileName)),
                SarahToken(value = "String", metadata = FileMetadata(22, 2, fileName)),
                SarahToken(value = ",", metadata = FileMetadata(28, 2, fileName)),
                SarahToken(value = "age", metadata = FileMetadata(30, 2, fileName)),
                SarahToken(value = ":", metadata = FileMetadata(33, 2, fileName)),
                SarahToken(value = "Int", metadata = FileMetadata(35, 2, fileName)),
                SarahToken(value = "}", metadata = FileMetadata(39, 2, fileName)),
            )
            val stream = StringTokenizableStream(input)
            val lexer = SarahLexer(stream)

            lexer.tokenize() shouldContainInOrder expected
        }
    }
})
