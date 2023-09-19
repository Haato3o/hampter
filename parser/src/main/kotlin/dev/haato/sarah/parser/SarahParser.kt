package dev.haato.sarah.parser

import dev.haato.sarah.ast.Expression
import dev.haato.sarah.ast.Operator
import dev.haato.sarah.ast.Operator.Companion.highestPrecedence
import dev.haato.sarah.ast.Operator.Companion.operatorsByPrecedence
import dev.haato.sarah.ast.SarahAbstractTree
import dev.haato.sarah.ast.UnaryOperator
import dev.haato.sarah.ast.UnaryOperator.Companion.unaryOperators
import dev.haato.sarah.lexer.token.SarahToken
import dev.haato.sarah.parser.Keywords.ELSE
import dev.haato.sarah.parser.Keywords.IF
import dev.haato.sarah.parser.Keywords.NAMESPACE
import dev.haato.sarah.parser.Keywords.STRUCT
import dev.haato.sarah.parser.Tokens.COLON
import dev.haato.sarah.parser.Tokens.PARENTHESIS_END
import dev.haato.sarah.parser.Tokens.PARENTHESIS_START
import dev.haato.sarah.parser.Tokens.PERIOD
import dev.haato.sarah.parser.Tokens.QUESTION_MARK
import dev.haato.sarah.parser.Tokens.SCOPE_END
import dev.haato.sarah.parser.Tokens.SCOPE_START
import dev.haato.sarah.parser.Tokens.SEMI_COLON
import java.util.LinkedList
import java.util.Queue
import kotlin.math.exp

class SarahParser(
    private val tokens: List<SarahToken>
) {

    fun parse(): SarahAbstractTree {
        val tokensQueue: Queue<SarahToken> = LinkedList(tokens)

        return SarahAbstractTree(tokensQueue.buildScopeExpression())
    }

    private fun Queue<SarahToken>.buildScopeExpression(): Expression.ScopeExpression {
        val scopeToken = peek().takeIf { it.value == SCOPE_START }?.also { remove() }
        val scopeStarted = scopeToken != null
        val expressions = mutableListOf<Expression>()

        while (!isEmpty()) {
            val expression = when (peek().value) {
                NAMESPACE -> buildNamespaceExpression()
                STRUCT -> buildStructureExpression()
                IF -> buildDecisionExpression()
                SCOPE_END -> if (scopeStarted) break else continue
                else -> throw NotImplementedError("ERROR: '${peek().value}' keyword not implemented.")
            }

            expressions.add(expression)
        }

        if (scopeStarted) {
            assert(remove().value == PARENTHESIS_END)
        }

        return Expression.ScopeExpression(
            expressions = expressions,
            metadata = scopeToken?.metadata ?: expressions.first().metadata
        )
    }

    private fun Queue<SarahToken>.buildNamespaceExpression(): Expression.NamespaceExpression {
        val namespaceToken = remove()
        assert(namespaceToken.value == NAMESPACE)

        val expressions = mutableListOf<Expression.LiteralExpression>()

        do {
            when(peek().value) {
                PERIOD -> remove()
                else -> expressions.add(buildLiteralExpression())
            }
        } while(peek().value != SEMI_COLON)

        assert(remove().value == SEMI_COLON)

        return Expression.NamespaceExpression(
            path = expressions,
            metadata = namespaceToken.metadata
        )
    }

    private fun Queue<SarahToken>.buildLiteralExpression(): Expression.LiteralExpression {
        return remove().let {
            Expression.LiteralExpression(
                value = it.value,
                metadata = it.metadata
            )
        }
    }

    private fun Queue<SarahToken>.buildUnaryExpression(): Expression.UnaryExpression {
        val operatorToken = remove()
        val operator = UnaryOperator.bySymbol(operatorToken.value)
        val expression = buildBinaryExpression()

        return Expression.UnaryExpression(
            operator = operator,
            expression = expression,
            metadata = operatorToken.metadata
        )
    }

    private fun Queue<SarahToken>.buildDecisionExpression(): Expression.DecisionExpression {
        val ifToken = remove()
        assert(ifToken.value == IF)
        assert(remove().value == PARENTHESIS_START)

        val conditionExpression = buildBinaryExpression()

        assert(remove().value == PARENTHESIS_END)

        val ifScopeExpression = buildScopeExpression()

        val elseScopeExpression = peek().takeIf { it.value == ELSE }
            ?.also { remove() }
            ?.let { buildScopeExpression() }

        return Expression.DecisionExpression(
            condition = conditionExpression,
            ifBlock = ifScopeExpression,
            elseBLock = elseScopeExpression,
            metadata = ifToken.metadata
        )
    }

    private fun Queue<SarahToken>.buildFactorExpression(): Expression {
        return when(peek().value) {
            PARENTHESIS_START -> {
                remove()

                buildTermExpression(highestPrecedence).also {
                    assert(remove().value == PARENTHESIS_END)
                }
            }

            in unaryOperators -> buildUnaryExpression()

            else -> buildLiteralExpression()
        }
    }

    private fun Queue<SarahToken>.buildTermExpression(precedence: Int): Expression {
        val nextPrecedence = precedence - 1
        val leftExpression = if (nextPrecedence < 0) buildFactorExpression() else buildTermExpression(nextPrecedence)
        val operators = operatorsByPrecedence[precedence]?.toSet() ?: setOf()
        val operatorTokens = operators.map { it.symbol }

        if (peek().value in operatorTokens) {
            val operator = Operator.bySymbol(remove().value)
            val rightExpression = if (nextPrecedence < 0) buildFactorExpression() else buildTermExpression(nextPrecedence)

            return Expression.BinaryExpression(
                operator = operator,
                left = leftExpression,
                right = rightExpression,
                metadata = leftExpression.metadata
            )
        }

        return leftExpression
    }

    private fun Queue<SarahToken>.buildBinaryExpression(): Expression {
        return buildTermExpression(highestPrecedence)
    }

    private fun Queue<SarahToken>.buildStructureExpression(): Expression.StructureExpression {
        val structToken = remove()
        assert(structToken.value == STRUCT)
        val structName = buildLiteralExpression()

        assert(structToken.value == SCOPE_START)

        val fieldsExpressions = mutableListOf<Expression.FieldExpression>()

        do {
            fieldsExpressions.add(buildFieldExpression())
        } while(peek().value != SCOPE_END)

        return Expression.StructureExpression(
            name = structName,
            fields = fieldsExpressions,
            metadata = structToken.metadata
        )
    }

    private fun Queue<SarahToken>.buildFieldExpression(): Expression.FieldExpression {
        val fieldName = buildLiteralExpression()

        assert(remove().value == COLON)

        val fieldType = buildLiteralExpression()
        val isNullable = peek().takeIf { it.value == QUESTION_MARK }?.also { remove() } != null

        return Expression.FieldExpression(
            name = fieldName,
            type = fieldType,
            isNullable = isNullable,
            metadata = fieldName.metadata
        )
    }
}

