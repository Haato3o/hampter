package dev.haato.hampter.parser

import dev.haato.hampter.lexer.HampterLexer
import dev.haato.hampter.lexer.stream.StringTokenizableStream
import io.kotest.core.spec.style.ShouldSpec

class HampterParserTest : ShouldSpec({
    context("parse") {

//        should("convert namespace tokens into AST") {
//            val input = """
//                namespace example.program;
//            """.trimIndent()
//            val stream = StringTokenizableStream(input)
//            val tokens = HampterLexer(stream).tokenize()
//            val ast = HampterParser(tokens).parse()
//            val metadata = FileMetadata(0, 0, "")
//            val expected = HampterAbstractTree(
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
            val tokens = HampterLexer(stream).tokenize()
            val tree = HampterParser(tokens).parse()

            println(tree)
        }
    }
})
