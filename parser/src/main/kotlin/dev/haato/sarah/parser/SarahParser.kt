package dev.haato.sarah.parser

import dev.haato.sarah.ast.SarahAbstractTree
import dev.haato.sarah.lexer.token.SarahToken
import dev.haato.sarah.parser.SarahParserRules.buildScopeExpression
import java.util.Deque
import java.util.LinkedList

class SarahParser(
    private val tokens: List<SarahToken>
) {

    fun parse(): SarahAbstractTree {
        val tokensQueue: Deque<SarahToken> = LinkedList(tokens)

        return SarahAbstractTree(tokensQueue.buildScopeExpression())
    }
}
