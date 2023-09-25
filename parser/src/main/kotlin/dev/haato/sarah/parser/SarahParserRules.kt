package dev.haato.sarah.parser

import dev.haato.sarah.ast.DeclarationType
import dev.haato.sarah.ast.Expression
import dev.haato.sarah.ast.Operator
import dev.haato.sarah.ast.UnaryOperator
import dev.haato.sarah.lexer.token.SarahToken
import dev.haato.sarah.parser.Keywords.ELSE
import dev.haato.sarah.parser.Keywords.IF
import dev.haato.sarah.parser.Keywords.NAMESPACE
import dev.haato.sarah.parser.Keywords.STRUCT
import dev.haato.sarah.parser.Tokens.COLON
import dev.haato.sarah.parser.Tokens.COMMA
import dev.haato.sarah.parser.Tokens.EQUAL
import dev.haato.sarah.parser.Tokens.PARENTHESIS_END
import dev.haato.sarah.parser.Tokens.PARENTHESIS_START
import dev.haato.sarah.parser.Tokens.PERIOD
import dev.haato.sarah.parser.Tokens.QUESTION_MARK
import dev.haato.sarah.parser.Tokens.SCOPE_END
import dev.haato.sarah.parser.Tokens.SCOPE_START
import dev.haato.sarah.parser.Tokens.SEMI_COLON
import dev.haato.sarah.utils.DequeExtensions.tryPeek
import java.util.Deque

@Suppress("TooManyFunctions")
object SarahParserRules {

    @Suppress("LoopWithTooManyJumpStatements")
    fun Deque<SarahToken>.buildScopeExpression(): Expression.ScopeExpression {
        val scopeToken = peek().takeIf { it.value == SCOPE_START }?.also { remove() }
        val scopeStarted = scopeToken != null
        val expressions = mutableListOf<Expression>()

        while (!isEmpty()) {
            val expression = when (peek().value) {
                NAMESPACE -> buildNamespaceExpression()
                STRUCT -> buildStructureExpression()
                IF -> buildDecisionExpression()
                SCOPE_END -> if (scopeStarted) break else continue
                in DeclarationType.values -> buildDeclareExpression()
                else -> buildBinaryExpression()
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

    private fun Deque<SarahToken>.buildNamespaceExpression(): Expression.NamespaceExpression {
        val namespaceToken = remove()
        assert(namespaceToken.value == NAMESPACE)

        val expressions = mutableListOf<Expression.LiteralExpression>()

        do {
            when (peek().value) {
                PERIOD -> remove()
                else -> expressions.add(buildLiteralExpression())
            }
        } while (peek().value != SEMI_COLON)

        assert(remove().value == SEMI_COLON)

        return Expression.NamespaceExpression(
            path = expressions,
            metadata = namespaceToken.metadata
        )
    }

    private fun Deque<SarahToken>.buildLiteralExpression(): Expression.LiteralExpression {
        return remove().let {
            Expression.LiteralExpression(
                value = it.value,
                metadata = it.metadata
            )
        }
    }

    private fun Deque<SarahToken>.buildUnaryExpression(): Expression.UnaryExpression {
        val operatorToken = remove()
        val operator = UnaryOperator.bySymbol(operatorToken.value)
        val expression = buildBinaryExpression()

        return Expression.UnaryExpression(
            operator = operator,
            expression = expression,
            metadata = operatorToken.metadata
        )
    }

    private fun Deque<SarahToken>.buildDecisionExpression(): Expression.DecisionExpression {
        val ifToken = remove()
        assert(ifToken.value == IF)
        assert(remove().value == PARENTHESIS_START)

        val conditionExpression = buildBinaryExpression()

        assert(remove().value == PARENTHESIS_END)

        val ifScopeExpression = buildScopeExpression()

        val elseScopeExpression = tryPeek()?.takeIf { it.value == ELSE }
            ?.also { remove() }
            ?.let { buildScopeExpression() }

        return Expression.DecisionExpression(
            condition = conditionExpression,
            ifBlock = ifScopeExpression,
            elseBLock = elseScopeExpression,
            metadata = ifToken.metadata
        )
    }

    private fun Deque<SarahToken>.buildCallExpression(): Expression.CallExpression {
        val calleeNameExpression = buildLiteralExpression()

        assert(remove().value == PARENTHESIS_START)

        val parameters = mutableListOf<Expression>().apply {
            while (peek().value != PARENTHESIS_END) {
                add(buildBinaryExpression())

                if (peek().value == COMMA) {
                    remove()
                } else {
                    break
                }
            }
        }

        assert(remove().value == PARENTHESIS_END)

        peek().takeIf { it.value == SEMI_COLON }?.let { remove() }

        return Expression.CallExpression(
            callee = calleeNameExpression,
            params = parameters,
            metadata = calleeNameExpression.metadata
        )
    }

    private fun Deque<SarahToken>.buildFactorExpression(): Expression {
        return when (peek().value) {
            PARENTHESIS_START -> {
                remove()

                buildTermExpression(Operator.highestPrecedence).also {
                    assert(remove().value == PARENTHESIS_END)
                }
            }

            in UnaryOperator.unaryOperators -> buildUnaryExpression()

            else -> {
                val (currentToken, nextToken) = listOf(remove(), peek())

                addFirst(currentToken)

                when (nextToken.value) {
                    PARENTHESIS_START -> buildCallExpression()
                    else -> buildLiteralExpression()
                }
            }
        }
    }

    private fun Deque<SarahToken>.buildTermExpression(precedence: Int): Expression {
        val nextPrecedence = precedence - 1
        val leftExpression = if (nextPrecedence < 0) buildFactorExpression() else buildTermExpression(nextPrecedence)
        val operators = Operator.operatorsByPrecedence[precedence]?.toSet() ?: setOf()
        val operatorTokens = operators.map { it.symbol }

        if (tryPeek()?.value in operatorTokens) {
            val operator = Operator.bySymbol(remove().value)
            val rightExpression = if (nextPrecedence < 0) {
                buildFactorExpression()
            } else {
                buildTermExpression(
                    nextPrecedence
                )
            }

            return Expression.BinaryExpression(
                operator = operator,
                left = leftExpression,
                right = rightExpression,
                metadata = leftExpression.metadata
            )
        }

        return leftExpression
    }

    private fun Deque<SarahToken>.buildBinaryExpression(): Expression {
        return buildTermExpression(Operator.highestPrecedence)
    }

    private fun Deque<SarahToken>.buildStructureExpression(): Expression.StructureExpression {
        val structToken = remove()
        assert(structToken.value == STRUCT)
        val structName = buildLiteralExpression()

        assert(structToken.value == SCOPE_START)

        val fieldsExpressions = mutableListOf<Expression.FieldExpression>()

        do {
            fieldsExpressions.add(buildFieldExpression())
        } while (peek().value != SCOPE_END)

        return Expression.StructureExpression(
            name = structName,
            fields = fieldsExpressions,
            metadata = structToken.metadata
        )
    }

    private fun Deque<SarahToken>.buildFieldExpression(): Expression.FieldExpression {
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

    private fun Deque<SarahToken>.buildDeclareExpression(): Expression.DeclareExpression {
        val declareToken = peek()

        assert(remove().value in DeclarationType.values)

        val declarationType = DeclarationType.fromLabel(declareToken.value)
        val identifierExpression = buildLiteralExpression()

        // TODO: Make explicit type optional
        assert(remove().value == COLON)
        val typeExpression = buildLiteralExpression()

        val isNullable = peek().takeIf { it.value == QUESTION_MARK }?.also { remove() } != null

        assert(remove().value == EQUAL)

        val expression = buildBinaryExpression()

        assert(remove().value == SEMI_COLON)

        return Expression.DeclareExpression(
            declarationType = declarationType,
            identifier = identifierExpression,
            type = typeExpression,
            isNullable = isNullable,
            expression = expression,
            metadata = declareToken.metadata
        )
    }
}
