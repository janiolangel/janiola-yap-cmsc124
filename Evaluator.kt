class Evaluator {

    fun evaluate(expr: Expr): Any? {
        return when (expr) {
            is Expr.Literal -> expr.value

            is Expr.Grouping -> evaluate(expr.expression)

            is Expr.Unary -> {
                val right = evaluate(expr.right)

                when (expr.operator.type) {
                    TokenType.FLIP -> {
                        checkNumberOperand(expr.operator, right)
                        -(right as Double)
                    }
                    else -> null
                }
            }

            is Expr.Binary -> {
                val left = evaluate(expr.left)
                val right = evaluate(expr.right)

                when (expr.operator.type) {
                    TokenType.MIX -> {
                        checkNumberOperands(expr.operator, left, right)
                        (left as Double) + (right as Double)
                    }

                    TokenType.TAKE_AWAY -> {
                        checkNumberOperands(expr.operator, left, right)
                        (right as Double) - (left as Double)
                    }

                    TokenType.COMBINE -> {
                        checkNumberOperands(expr.operator, left, right)
                        (left as Double) * (right as Double)
                    }

                    TokenType.SHARE -> {
                        checkNumberOperands(expr.operator, left, right)
                        if ((right as Double) == 0.0)
                            throw RuntimeError(expr.operator, "Division by zero.")
                        (left as Double) / right
                    }

                    TokenType.GREATER -> {
                        checkNumberOperands(expr.operator, left, right)
                        (left as Double) > (right as Double)
                    }

                    TokenType.LESS -> {
                        checkNumberOperands(expr.operator, left, right)
                        (left as Double) < (right as Double)
                    }

                    TokenType.EQUAL_EQUAL -> left == right

                    else -> null
                }
            }
        }
    }

    // --- Helpers ---
    private fun checkNumberOperand(operator: Token, operand: Any?) {
        if (operand is Double) return
        throw RuntimeError(operator, "Operand must be a number.")
    }

    private fun checkNumberOperands(operator: Token, left: Any?, right: Any?) {
        if (left is Double && right is Double) return
        throw RuntimeError(operator, "Operands must be numbers.")
    }
}
