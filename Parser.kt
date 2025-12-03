sealed class Expr {
    data class Binary(val left: Expr, val operator: Token, val right: Expr) : Expr()
    data class Grouping(val expression: Expr) : Expr()
    data class Literal(val value: Any?) : Expr()
    data class Unary(val operator: Token, val right: Expr) : Expr()
    data class Variable(val name: Token) : Expr()
    data class Call(val callee: Expr, val paren: Token, val arguments: List<Expr>) : Expr()
    data class Logical(val left: Expr, val operator: Token, val right: Expr) : Expr()
}

sealed class Stmt {
    data class Expression(val expression: Expr) : Stmt()
    data class Print(val expression: Expr) : Stmt()
    data class Var(val name: Token, val initializer: Expr) : Stmt()
    data class Set(val name: Token, val value: Expr) : Stmt()
    data class Block(val statements: List<Stmt>) : Stmt()
    data class If(val condition: Expr, val thenBranch: Stmt, val elseBranch: Stmt?) : Stmt()
    data class While(val condition: Expr, val body: Stmt) : Stmt()
    data class For(val initializer: Stmt?, val condition: Expr?, val increment: Expr?, val body: Stmt) : Stmt()
    data class Function(val name: Token, val params: List<Token>, val body: List<Stmt>) : Stmt()
    data class Return(val keyword: Token, val value: Expr?) : Stmt()
}

class Parser(private val tokens: List<Token>) {
    private var current = 0

    fun parse(): Stmt? {
        return try {
            declaration()   // returns ONE statement
        } catch (e: ParseError) {
            null
        }
    }

    // ------ Declarations & Statements ------

    private fun declaration(): Stmt {
        return try {
            if (match(TokenType.REMEMBER)) return rememberDeclaration()
            if (match(TokenType.FUN)) return function("function")
            statement()
        } catch (e: ParseError) {
            synchronize()
            Stmt.Expression(Expr.Literal(null))
        }
    }

    private fun rememberDeclaration(): Stmt {
        val name = consume(TokenType.IDENTIFIER, "Expect variable name after 'remember'.")
        consume(TokenType.AS, "Expect 'as' after variable name.")
        val initializer = expression()
        consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.")
        return Stmt.Var(name, initializer)
    }

    private fun function(kind: String): Stmt.Function {
        val name = consume(TokenType.IDENTIFIER, "Expect $kind name.")
        consume(TokenType.LEFT_PAREN, "Expect '(' after $kind name.")
        val parameters = mutableListOf<Token>()
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                if (parameters.size >= 255) throw error(peek(), "Can't have more than 255 parameters.")
                parameters.add(consume(TokenType.IDENTIFIER, "Expect parameter name."))
            } while (match(TokenType.COMMA))
        }
        consume(TokenType.RIGHT_PAREN, "Expect ')' after parameters.")
        consume(TokenType.LEFT_BRACE, "Expect '{' before $kind body.")
        val body = block()
        return Stmt.Function(name, parameters, body)
    }

    private fun statement(): Stmt {
        return when {
            match(TokenType.PRINT) -> printStatement()
            match(TokenType.SET) -> setStatement()
            match(TokenType.LEFT_BRACE) -> Stmt.Block(block())
            match(TokenType.IF) -> ifStatement()
            match(TokenType.WHILE) -> whileStatement()
            match(TokenType.FOR) -> forStatement()
            match(TokenType.RETURN) -> returnStatement()
            else -> exprStatement()
        }
    }

    private fun printStatement(): Stmt {
        val value = expression()
        consume(TokenType.SEMICOLON, "Expect ';' after value.")
        return Stmt.Print(value)
    }

    private fun setStatement(): Stmt {
        val name = consume(TokenType.IDENTIFIER, "Expect variable name after 'set'.")
        consume(TokenType.TO, "Expect 'to' after variable name.")

        // Optional sugar: "the value when you"
        if (match(TokenType.THE)) {
            consume(TokenType.VALUE, "Expect 'value' after 'the'.")
            consume(TokenType.WHEN, "Expect 'when' after 'value'.")
            consume(TokenType.YOU, "Expect 'you' after 'when'.")
        }

        val value = expression()
        consume(TokenType.SEMICOLON, "Expect ';' after set statement.")

        return Stmt.Set(name, value)
    }

    private fun block(): List<Stmt> {
        val statements = mutableListOf<Stmt>()
        while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration())
        }
        consume(TokenType.RIGHT_BRACE, "Expect '}' after block.")
        return statements
    }

    private fun exprStatement(): Stmt {
        val expr = expression()
        consume(TokenType.SEMICOLON, "Expect ';' after expression.")
        return Stmt.Expression(expr)
    }

    private fun ifStatement(): Stmt {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'when'.")
        val condition = expression()
        consume(TokenType.RIGHT_PAREN, "Expect ')' after if condition.")
        val thenBranch = statement()
        var elseBranch: Stmt? = null
        if (match(TokenType.ELSE)) {
            elseBranch = statement()
        }
        return Stmt.If(condition, thenBranch, elseBranch)
    }

    private fun whileStatement(): Stmt {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'repeat'.")
        val condition = expression()
        consume(TokenType.RIGHT_PAREN, "Expect ')' after condition.")
        val body = statement()
        return Stmt.While(condition, body)
    }

    private fun forStatement(): Stmt {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'for'.")
        val initializer: Stmt? = when {
            match(TokenType.SEMICOLON) -> null
            match(TokenType.REMEMBER) -> rememberDeclaration()
            else -> exprStatement()
        }
        var condition: Expr? = null
        if (!check(TokenType.SEMICOLON)) condition = expression()
        consume(TokenType.SEMICOLON, "Expect ';' after loop condition.")
        var increment: Expr? = null
        if (!check(TokenType.RIGHT_PAREN)) increment = expression()
        consume(TokenType.RIGHT_PAREN, "Expect ')' after for clauses.")
        val body = statement()

        // Desugar into while:
        var blockStmts = mutableListOf<Stmt>()
        if (initializer != null) blockStmts.add(initializer)
        val whileBodyStmts = mutableListOf<Stmt>()
        whileBodyStmts.add(body)
        if (increment != null) whileBodyStmts.add(Stmt.Expression(increment))
        val whileBody = Stmt.Block(whileBodyStmts)
        val cond = condition ?: Expr.Literal(true)
        blockStmts.add(Stmt.While(cond, whileBody))
        return Stmt.Block(blockStmts)
    }

    private fun returnStatement(): Stmt {
        val keyword = previous()
        var value: Expr? = null
        if (!check(TokenType.SEMICOLON)) {
            value = expression()
        }
        consume(TokenType.SEMICOLON, "Expect ';' after return value.")
        return Stmt.Return(keyword, value)
    }

    // ------ Expressions ------

    private fun expression(): Expr = assignment()

    private fun assignment(): Expr {
        val expr = logicOr()
        if (match(TokenType.EQUAL_EQUAL)) {
            // keep equality where it belongs — this grammar uses EQUAL_EQUAL as comparison; assignment uses 'set' stmt
        }
        return expr
    }

    private fun logicOr(): Expr {
        var expr = logicAnd()
        while (match(TokenType.OR)) {
            val operator = previous()
            val right = logicAnd()
            expr = Expr.Logical(expr, operator, right)
        }
        return expr
    }

    private fun logicAnd(): Expr {
        var expr = step()
        while (match(TokenType.AND)) {
            val operator = previous()
            val right = step()
            expr = Expr.Logical(expr, operator, right)
        }
        return expr
    }

    // <Step> ::= "Mix" <Value> "and" <Value>
    //          | "Take away" <Value> "from" <Value>
    //          | "Multiply" <Value> "and" <Value>
    //          | "Divide" <Value> "with" <Value>
    //          | "Flip" <Value>
    //          | "Check if" <Value> <Inequality> <Value>
    //          | <Value>

    private fun step(): Expr { //OPERATOR PRECEDENCE
        return when {
            match(TokenType.MIX) -> { //BINARY EXPRESSION STEP 1
                val operator = previous()
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
                Expr.Binary(right, operator, left)
            }
            match(TokenType.MULTIPLY) -> {
                val operator = previous()
                val left = value()
                consume(TokenType.AND, "Expect 'and' after first value.")
                val right = value()
                Expr.Binary(left, operator, right)
            }
            match(TokenType.DIVIDE) -> {
                val operator = previous()
                val left = value()
                consume(TokenType.WITH, "Expect 'with' after first value.")
                val right = value()
                Expr.Binary(left, operator, right)
            }
            match(TokenType.FLIP) -> {  //UNARY EXPRESSIONS STEP 1
                val operator = previous()
                val right = value()
                Expr.Unary(operator, right)
            }
            match(TokenType.CHECK_IF) -> {
                val left = value()
                val comp = if (match(TokenType.GREATER, TokenType.LESS, TokenType.EQUAL_EQUAL))
                    previous()
                else
                    throw error(peek(), "Expect comparison operator after 'Check if'.")
                val right = value()
                Expr.Binary(left, comp, right)
            }
            else -> call()
        }
    }

    private fun call(): Expr {
        var expr = value()
        while (true) {
            if (match(TokenType.LEFT_PAREN)) {
                expr = finishCall(expr)
            } else break
        }
        return expr
    }

    private fun finishCall(callee: Expr): Expr {
        val arguments = mutableListOf<Expr>()
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                if (arguments.size >= 255) throw error(peek(), "Can't have more than 255 arguments.")
                arguments.add(expression())
            } while (match(TokenType.COMMA))
        }
        val paren = consume(TokenType.RIGHT_PAREN, "Expect ')' after arguments.")
        return Expr.Call(callee, paren, arguments)
    }

    private fun value(): Expr {     // LITERAL VALUES
        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return Expr.Literal(previous().literal)
        }

        if (match(TokenType.IDENTIFIER)) {
            return Expr.Variable(previous())
        }

        if (match(TokenType.LEFT_PAREN)) { //GROUPING STEP 1
            val expr = expression()
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.")
            return Expr.Grouping(expr)
        }

        throw error(peek(), "Expect value.")
    }

    // ------ helpers ------

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

    private fun synchronize() {
        advance()

        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMICOLON) return

            when (peek().type) {
                TokenType.REMEMBER,
                TokenType.PRINT,
                TokenType.SET,
                TokenType.FUN,
                TokenType.IF -> return
                else -> {}
            }

            advance()
        }
    }
}

class AstPrinter {
    fun print(expr: Expr): String {
        return when (expr) {
            is Expr.Binary -> parenthesize(expr.operator.lexeme, expr.left, expr.right)
            is Expr.Grouping -> parenthesize("group", expr.expression)
            is Expr.Literal -> expr.value?.toString() ?: "nil"
            is Expr.Unary -> parenthesize(expr.operator.lexeme, expr.right)
            is Expr.Variable -> expr.name.lexeme
            is Expr.Call -> parenthesize("call", expr.callee, *expr.arguments.toTypedArray())
            is Expr.Logical -> parenthesize(expr.operator.lexeme, expr.left, expr.right)
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
