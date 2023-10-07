package dev.haato.hampter.visualization

import dev.haato.hampter.lexer.HampterLexer
import dev.haato.hampter.lexer.stream.StringTokenizableStream
import dev.haato.hampter.parser.HampterParser
import io.kotest.core.spec.style.ShouldSpec

class HampterASTVisualizerTest : ShouldSpec({

    context("build") {
        should("build tree") {
            val input = """
                namespace example.name;
                
                let test: int = 10 * (20 + 20) >> 2;
                let myBoolean: bool = true && false;
                const test2: String = "My string!";
                
                if (!myBoolean) {
                    print(test2);
                }
                
                max(test, 10);
            """.trimIndent()
            val stream = StringTokenizableStream(input)
            val tokens = HampterLexer(stream).tokenize()
            val tree = HampterParser(tokens).parse()

            println(HampterASTVisualizer.build(tree))
        }
    }
})
