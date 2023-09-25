package dev.haato.sarah.visualization

import dev.haato.sarah.ast.Expression
import dev.haato.sarah.ast.SarahAbstractTree

object SarahASTVisualizer {

    private enum class TreeOrder(val parentChar: String, val childChar: String) {
        FIRST(TREE_CHARACTER, TREE_MIDDLE_CHARACTER),
        MIDDLE(TREE_CHARACTER, TREE_MIDDLE_CHARACTER),
        LAST(TREE_END_CHARACTER, TREE_EMPTY_CHARACTER)
    }

    fun build(tree: SarahAbstractTree): String = buildString {
        buildTree(tree.scope, "", TreeOrder.LAST)
    }

    private fun StringBuilder.buildTree(expression: Expression, identation: String, treeOrder: TreeOrder) {
        val currentIdentation = "$identation  ${treeOrder.parentChar}"
        val childIdentation = "$identation  ${treeOrder.childChar}"

        when (expression) {
            is Expression.ScopeExpression -> {
                expression.expressions.forEachIndexed { index, expr ->
                    val order = when (index) {
                        expression.expressions.size - 1 -> TreeOrder.LAST
                        0 -> TreeOrder.FIRST
                        else -> TreeOrder.MIDDLE
                    }

                    buildTree(expr, childIdentation, order)
                }
            }

            is Expression.LiteralExpression -> append("$currentIdentation  Literal: ${expression.value}\n")

            is Expression.NamespaceExpression -> {
                append("$currentIdentation Namespace\n")
                expression.path.forEachIndexed { index, expr ->
                    val order = when (index) {
                        expression.path.size - 1 -> TreeOrder.LAST
                        0 -> TreeOrder.FIRST
                        else -> TreeOrder.MIDDLE
                    }

                    buildTree(expr, childIdentation, order)
                }
            }

            is Expression.DeclareExpression -> {
                append("$currentIdentation ${expression.declarationType}\n")
                append("$childIdentation Type\n")
                buildTree(expression.type, childIdentation, TreeOrder.LAST)

                append("$childIdentation Value\n")
                buildTree(expression.expression, childIdentation, TreeOrder.LAST)
            }

            is Expression.BinaryExpression -> {
                append("$currentIdentation ${expression.operator}\n")

                buildTree(expression.left, childIdentation, TreeOrder.MIDDLE)
                buildTree(expression.right, childIdentation, TreeOrder.LAST)
            }

            is Expression.UnaryExpression -> {
                append("$currentIdentation ${expression.operator}\n")

                buildTree(expression.expression, childIdentation, TreeOrder.LAST)
            }

            is Expression.DecisionExpression -> {
                append("$currentIdentation If\n")

                append("$childIdentation Condition\n")
                buildTree(expression.condition, childIdentation, TreeOrder.LAST)

                append("$childIdentation Block\n")
                buildTree(expression.ifBlock, childIdentation, TreeOrder.LAST)
            }

            is Expression.CallExpression -> {
                append("$currentIdentation Call\n")

                buildTree(expression.callee, childIdentation, TreeOrder.LAST)

                append("$currentIdentation Parameters\n")

                expression.params.forEachIndexed { index, expr ->
                    val order = when (index) {
                        expression.params.size - 1 -> TreeOrder.LAST
                        0 -> TreeOrder.FIRST
                        else -> TreeOrder.MIDDLE
                    }

                    buildTree(expr, childIdentation, order)
                }
            }

            else ->
                throw NotImplementedError("Not implemented deserializer for expression ${expression.javaClass.name}")
        }
    }

    private const val TREE_EMPTY_CHARACTER = "\t"
    private const val TREE_CHARACTER = "├──"
    private const val TREE_MIDDLE_CHARACTER = "│"
    private const val TREE_END_CHARACTER = "└──"
}
