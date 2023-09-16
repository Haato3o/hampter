package dev.haato.sarah.ast

import dev.haato.sarah.metadata.FileMetadata

sealed class Expression {
    abstract val metadata: FileMetadata

    data class LiteralExpression(
        val value: String,
        override val metadata: FileMetadata
    ) : Expression()

    data class ScopeExpression(
        val expressions: List<Expression>,
        override val metadata: FileMetadata
    ) : Expression()

    data class FieldExpression(
        val name: LiteralExpression,
        val type: LiteralExpression,
        override val metadata: FileMetadata
    ) : Expression()

    data class FunctionExpression(
        val name: LiteralExpression,
        val params: List<FieldExpression>,
        val returnType: LiteralExpression,
        val scope: ScopeExpression,
        override val metadata: FileMetadata
    ) : Expression()

    data class DeclareExpression(
        val identifier: LiteralExpression,
        val type: LiteralExpression,
        val isNullable: Boolean,
        val expression: Expression,
        override val metadata: FileMetadata
    ) : Expression()

    data class DecisionExpression(
        val condition: Expression,
        val ifBlock: ScopeExpression,
        val elseBLock: ScopeExpression,
        override val metadata: FileMetadata
    ) : Expression()

    data class CallExpression(
        val callee: LiteralExpression,
        val params: List<Expression>,
        override val metadata: FileMetadata
    ) : Expression()

    data class ArithmeticExpression(
        val expression: Expression,
        override val metadata: FileMetadata
    ) : Expression()

    data class StructureExpression(
        val name: LiteralExpression,
        val fields: List<FieldExpression>,
        override val metadata: FileMetadata
    ) : Expression()
}
