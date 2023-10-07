package dev.haato.hampter.parser

import dev.haato.hampter.ast.HampterAbstractTree
import dev.haato.hampter.lexer.token.HampterToken
import dev.haato.hampter.parser.HampterParserRules.buildScopeExpression
import java.util.Deque
import java.util.LinkedList

class HampterParser(
    private val tokens: List<HampterToken>
) {

    fun parse(): HampterAbstractTree {
        val tokensQueue: Deque<HampterToken> = LinkedList(tokens)

        return HampterAbstractTree(tokensQueue.buildScopeExpression())
    }
}
