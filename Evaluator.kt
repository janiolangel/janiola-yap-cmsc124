class Evaluator {

    private val globals = Environment()
    private var environment = globals

    fun interpret(statements: List<Stmt>) {
        for (stmt in statements) {
            execute(stmt)
        }
    }

    private fun execute(stmt: Stmt) {
        when (stmt) {
            is Stmt.Expression -> {
                evaluate(stmt.expression) // ignore result
            }
            is Stmt.Print -> {
                val value = evaluate(stmt.expression)
                println(stringify(value))
            }
            is Stmt.Var -> {
                val value = evaluate(stmt.initializer)
                environment.define(stmt.name.lexeme, value)
            }
            is Stmt.Set -> {
                val value = evaluate(stmt.value)
                environment.assign(stmt.name, value)
            }
            is Stmt.Block -> {
                executeBlock(stmt.statements, Environment(environment))
            }
        }
    }

    fun executeBlock(statements: List<Stmt>, newEnv: Environment) {
        val previous = environment
        try {
            environment = newEnv
            for (stmt in statements) {
                execute(stmt)
            }
        } finally {
            environment = previous
        }
    }

    // -------- Expressions --------

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
                        // number + number OR string + string
                        if (left is Double && right is Double) return left + right
                        if (left is String && right is String) return left + right
                        throw RuntimeError(
                            expr.operator,
                            "Mix requires two numbers or two strings."
                        )
                    }

                    TokenType.TAKE_AWAY -> {
                        checkNumberOperands(expr.operator, left, right)
                        (left as Double) - (right as Double)
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

                    TokenType.EQUAL_EQUAL -> {
                        left == right
                    }

                    else -> null
                }
            }

            is Expr.Variable -> environment.get(expr.name)
        }
    }

    // -------- Helpers --------

    fun stringify(value: Any?): String {
        if (value == null) return "nil"
        if (value is Double) {
            val text = value.toString()
            return if (text.endsWith(".0")) text.dropLast(2) else text
        }
        return value.toString()
    }

    private fun checkNumberOperand(operator: Token, operand: Any?) {
        if (operand is Double) return
        throw RuntimeError(operator, "Operand must be a number.")
    }

    private fun checkNumberOperands(operator: Token, left: Any?, right: Any?) {
        if (left is Double && right is Double) return
        throw RuntimeError(operator, "Operands must be numbers.")
    }
}
