package dev.haato.sarah.visualization

import dev.haato.sarah.lexer.SarahLexer
import dev.haato.sarah.lexer.stream.StringTokenizableStream
import dev.haato.sarah.metadata.FileMetadata
import dev.haato.sarah.parser.SarahParser
import io.kotest.core.spec.style.ShouldSpec

class SarahASTVisualizerTest : ShouldSpec({

    context("build") {
        val metadata = FileMetadata(0, 0, "")
        should("build tree") {
            val input = """
                namespace example.name;
                
                let test: int = 10 * (20 + 20) >> 2;
                let myBoolean: bool = true && false;
                const test2: String = "My string!";
                
                if (!myBoolean) {
                    print(test2);
                }
            """.trimIndent()
            val stream = StringTokenizableStream(input)
            val tokens = SarahLexer(stream).tokenize()
            val tree = SarahParser(tokens).parse()

            println(SarahASTVisualizer.build(tree))
        }
    }
})
