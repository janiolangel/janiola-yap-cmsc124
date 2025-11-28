class Evaluator {

    private val globals = Environment()
    private var environment = globals

    var isRepl: Boolean = false

    fun interpret(statement: Stmt?) {
        if (statement != null) execute(statement)
    }


    private fun execute(stmt: Stmt) {
        when (stmt) {
            is Stmt.Expression -> {
                // Evaluate expression but DO NOT print automatically
                evaluate(stmt.expression)
            }

            is Stmt.Print -> { //PRINT STATEMENT STEP 2
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

            is Expr.Grouping -> evaluate(expr.expression) //GROUPING STEP 2

            is Expr.Unary -> { //UNARY EXPRESSION STEP 2
                val right = evaluate(expr.right)
                when (expr.operator.type) {
                    TokenType.FLIP -> {
                        checkNumberOperand(expr.operator, right) //DETE
                        -(right as Double)
                    }
                    else -> "incorrect syntax"
                }
            }

            is Expr.Binary -> {
                val left = evaluate(expr.left)
                val right = evaluate(expr.right)

                when (expr.operator.type) {

                    TokenType.MIX -> {
                        if (left is Double && right is Double) return left + right
                        if (left is String && right is String) return left + right

                        // REMOVE THESE:
                        if (left is String && right is Double) return left + stringify(right)
                        if (left is Double && right is String) return stringify(left) + right

                        throw RuntimeError(expr.operator, "Mix requires two numbers or two strings.")
                    }


                    TokenType.TAKE_AWAY -> {

                        // number - number
                        if (left is Double && right is Double) {
                            return left - right
                        }

                        // string remove number
                        if (left is String && right is Double) {
                            val removeWhat = right.toInt().toString()
                            return left.replace(removeWhat, "")
                        }

                        // string remove string
                        if (left is String && right is String) {
                            return left.replace(right, "")
                        }

                        throw RuntimeError(expr.operator,
                            "Take away requires two numbers or a string and a removable value.")
                    }


                    TokenType.MULTIPLY -> {
                        checkNumberOperands(expr.operator, left, right)
                        return (left as Double) * (right as Double)
                    }

                    TokenType.DIVIDE -> {
                        checkNumberOperands(expr.operator, left, right)
                        if ((right as Double) == 0.0)
                            throw RuntimeError(expr.operator, "Division by zero.")
                        return (left as Double) / right
                    }

                    TokenType.GREATER -> {
                        checkNumberOperands(expr.operator, left, right)
                        return (left as Double) > (right as Double)
                    }

                    TokenType.LESS -> {
                        checkNumberOperands(expr.operator, left, right)
                        return (left as Double) < (right as Double)
                    }

                    TokenType.EQUAL_EQUAL -> {
                        return left == right
                    }

                    else -> null
                }
            }


            is Expr.Variable -> environment.get(expr.name)
        }
    }

    // -------- Helpers --------

    fun stringify(value: Any?): String {
        if (value is Double) {
            val text = value.toString()
            return if (text.endsWith(".0")) text.dropLast(2) else text
        }
        return value.toString()
    }

    //DETECT WHEN OPERANDS HAVE INCORRECT TYPE FOR OPERATORS

    private fun checkNumberOperand(operator: Token, operand: Any?) {
        if (operand is Double) return
        throw RuntimeError(operator, "Operand must be a number.")
    }

    private fun checkNumberOperands(operator: Token, left: Any?, right: Any?) {
        if (left is Double && right is Double) return
        throw RuntimeError(operator, "Operands must be numbers.")
    }
}
