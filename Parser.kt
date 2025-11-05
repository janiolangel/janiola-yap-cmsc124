sealed class Expr {
    data class Binary(val left: Expr, val operator: Token, val right: Expr) : Expr()
    data class Grouping(val expression: Expr) : Expr()
    data class Literal(val value: Any?) : Expr()
    data class Unary(val operator: Token, val right: Expr) : Expr()
}

class Parser(private val tokens: List<Token>) {
    private var current = 0

    fun parse(): Expr? = try {
        step()
    } catch (e: ParseError) {
        null
    }

    // Grammar:
    // <Program> ::= <Step>
    // <Step> ::= "Mix" <Value> "and" <Value>
    //          | "Take away" <Value> "from" <Value>
    //          | "Combine" <Value> "and" <Value>
    //          | "Share" <Value> "with" <Value>
    //          | "Flip" <Value>
    //          | "Check if" <Value> <Inequality> <Value>
    // <Value> ::= NUMBER | STRING | "(" <Step> ")"

    private fun step(): Expr {
        return when {
            match(TokenType.MIX) -> {
                val operator = previous() // 👈 store MIX token
                val left = value()
                consume(TokenType.AND, "Expect 'and' after first value.")
                val right = value()
                Expr.Binary(left, operator, right)
            }

            match(TokenType.TAKE_AWAY) -> {
                val operator = previous()
                val left = value()
                consume(TokenType.FROM, "Expect 'from' after first value.")
                val right = value()
                Expr.Binary(left, operator, right)
            }

            match(TokenType.COMBINE) -> {
                val operator = previous()
                val left = value()
                consume(TokenType.AND, "Expect 'and' after first value.")
                val right = value()
                Expr.Binary(left, operator, right)
            }

            match(TokenType.SHARE) -> {
                val operator = previous()
                val left = value()
                consume(TokenType.WITH, "Expect 'with' after first value.")
                val right = value()
                Expr.Binary(left, operator, right)
            }

            match(TokenType.FLIP) -> {
                val operator = previous()
                val right = value()
                Expr.Unary(operator, right)
            }

            match(TokenType.CHECK_IF) -> {
                val operator = previous()
                val left = value()
                val comp = if (match(TokenType.GREATER, TokenType.LESS, TokenType.EQUAL_EQUAL)) previous()
                else throw error(peek(), "Expect comparison operator after 'Check if'.")
                val right = value()
                Expr.Binary(left, comp, right)
            }

            else -> value()
        }
    }


    private fun value(): Expr {
        if (match(TokenType.NUMBER, TokenType.STRING))
            return Expr.Literal(previous().literal)

        if (match(TokenType.LEFT_PAREN)) {
            val expr = step()
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.")
            return Expr.Grouping(expr)
        }

        throw error(peek(), "Expect value.")
    }

    // --- helpers ---
    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun check(type: TokenType): Boolean =
        !isAtEnd() && peek().type == type

    private fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    private fun isAtEnd() = peek().type == TokenType.EOF

    private fun peek() = tokens[current]
    private fun previous() = tokens[current - 1]

    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()
        throw error(peek(), message)
    }

    private fun error(token: Token, message: String): ParseError {
        println("[Line ${token.line}] Error at '${token.lexeme}': $message")
        return ParseError()
    }

    private class ParseError : RuntimeException()
}

class AstPrinter {
    fun print(expr: Expr): String {
        return when (expr) {
            is Expr.Binary -> parenthesize(expr.operator.lexeme, expr.left, expr.right)
            is Expr.Grouping -> parenthesize("group", expr.expression)
            is Expr.Literal -> expr.value?.toString() ?: "nil"
            is Expr.Unary -> parenthesize(expr.operator.lexeme, expr.right)
        }
    }

    private fun parenthesize(name: String, vararg exprs: Expr): String {
        val builder = StringBuilder()
        builder.append("(").append(name)
        for (expr in exprs) {
            builder.append(" ")
            builder.append(print(expr))
        }
        builder.append(")")
        return builder.toString()
    }
}
