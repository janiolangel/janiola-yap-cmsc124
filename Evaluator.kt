class ReturnException(val value: Any?) : RuntimeException(null, null, false, false)

interface GCallable {
    fun arity(): Int
    fun call(evaluator: Evaluator, arguments: List<Any?>): Any?
}

class GFunction(private val declaration: Stmt.Function, private val closure: Environment) : GCallable {
    override fun arity() = declaration.params.size

    override fun call(evaluator: Evaluator, arguments: List<Any?>): Any? {
        val env = Environment(closure)
        for (i in declaration.params.indices) {
            env.define(declaration.params[i].lexeme, arguments[i])
        }
        try {
            evaluator.executeBlock(declaration.body, env)
        } catch (ret: ReturnException) {
            return ret.value
        }
        return null
    }

    override fun toString(): String {
        return "<recipe ${declaration.name.lexeme}>"
    }
}

class NativeClock : GCallable {
    override fun arity() = 0
    override fun call(evaluator: Evaluator, arguments: List<Any?>): Any? {
        return System.currentTimeMillis() / 1000.0
    }

    override fun toString(): String = "<native clock>"
}

class NativePrint : GCallable {
    override fun arity() = 1
    override fun call(evaluator: Evaluator, arguments: List<Any?>): Any? {
        println(evaluator.stringify(arguments[0]))
        return null
    }

    override fun toString(): String = "<native print>"
}

class Evaluator {

    private val globals = Environment()
    private var environment = globals

    var isRepl: Boolean = false

    init {
        // Add native functions
        globals.define("clock", NativeClock())
        globals.define("print", NativePrint())
    }

    fun interpret(statement: Stmt?) {
        if (statement != null) execute(statement)
    }

    private fun execute(stmt: Stmt) {
        when (stmt) {
            is Stmt.Expression -> {
                // Evaluate expression but DO NOT print automatically
                evaluate(stmt.expression)
            }

            is Stmt.Print -> { //PRINT STATEMENT
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
            is Stmt.If -> {
                val cond = evaluate(stmt.condition)
                if (isTruthy(cond)) {
                    execute(stmt.thenBranch)
                } else {
                    if (stmt.elseBranch != null) execute(stmt.elseBranch)
                }
            }
            is Stmt.While -> {
                while (isTruthy(evaluate(stmt.condition))) {
                    execute(stmt.body)
                }
            }
            is Stmt.Function -> {
                val function = GFunction(stmt, environment)
                environment.define(stmt.name.lexeme, function)
            }
            is Stmt.Return -> {
                val value = if (stmt.value != null) evaluate(stmt.value) else null
                throw ReturnException(value)
            }

            else -> {}
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

            is Expr.Grouping -> evaluate(expr.expression) //GROUPING

            is Expr.Unary -> { //UNARY
                val right = evaluate(expr.right)
                when (expr.operator.type) {
                    TokenType.FLIP -> {
                        checkNumberOperand(expr.operator, right)
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

                        if (left is String && right is Double) return left + stringify(right)
                        if (left is Double && right is String) return stringify(left) + right

                        // Add this for any other type combinations
                        if (left is String) return left + stringify(right)
                        if (right is String) return stringify(left) + right

                        throw RuntimeError(expr.operator, "Mix requires compatible types.")
                    }

                    TokenType.TAKE_AWAY -> {
                        if (left is Double && right is Double) {
                            return left - right
                        }
                        if (left is String && right is Double) {
                            val removeWhat = right.toInt().toString()
                            return left.replace(removeWhat, "")
                        }
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

            is Expr.Logical -> {
                val left = evaluate(expr.left)
                if (expr.operator.type == TokenType.OR) {
                    if (isTruthy(left)) return left
                } else { // AND
                    if (!isTruthy(left)) return left
                }
                return evaluate(expr.right)
            }

            is Expr.Call -> {
                val callee = evaluate(expr.callee)
                val arguments = expr.arguments.map { evaluate(it) }

                if (callee !is GCallable) {
                    throw RuntimeError(expr.paren, "Can only call functions and native callables.")
                }
                if (arguments.size != callee.arity()) {
                    throw RuntimeError(expr.paren,
                        "Expected ${callee.arity()} arguments but got ${arguments.size}.")
                }
                return callee.call(this, arguments)
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

    private fun isTruthy(value: Any?): Boolean {
        if (value == null) return false
        if (value is Boolean) return value
        return true
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
