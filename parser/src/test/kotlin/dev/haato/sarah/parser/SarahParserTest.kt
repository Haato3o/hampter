package dev.haato.sarah.parser

import dev.haato.sarah.lexer.SarahLexer
import dev.haato.sarah.lexer.stream.StringTokenizableStream
import io.kotest.core.spec.style.ShouldSpec

class SarahParserTest : ShouldSpec({
    context("parse") {

//        should("convert namespace tokens into AST") {
//            val input = """
//                namespace example.program;
//            """.trimIndent()
//            val stream = StringTokenizableStream(input)
//            val tokens = SarahLexer(stream).tokenize()
//            val ast = SarahParser(tokens).parse()
//            val metadata = FileMetadata(0, 0, "")
//            val expected = SarahAbstractTree(
//                scope = Expression.ScopeExpression(
//                    expressions = listOf(
//                        Expression.NamespaceExpression(
//                            path = listOf(
//                                Expression.LiteralExpression(
//                                    value = "example",
//                                    metadata = metadata
//                                ),
//                                Expression.LiteralExpression(
//                                    value = "program",
//                                    metadata = metadata
//                                )
//                            ),
//                            metadata = metadata
//                        )
//                    ),
//                    metadata = metadata
//                ),
//            )
//
//
//        }

        should("convert tokens into an abstract syntax tree") {
            val input = """
                namespace example;
                
                let myVariable: int = 10 + 20;
                let myOtherVariable: double = myVariable * 2 / 10;
                
                if (myOtherVariable > 5) {
                    print("Hello world!");
                }
            """.trimIndent()
            val stream = StringTokenizableStream(input)
            val tokens = SarahLexer(stream).tokenize()
            val tree = SarahParser(tokens).parse()

            println(tree)
        }
    }
})
