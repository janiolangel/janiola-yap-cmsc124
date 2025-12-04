class Scanner(private val source: String) {
    private val tokens = mutableListOf<Token>()
    private var start = 0
    private var current = 0
    private var line = 1

    // all keyword keys are lowercase (case-insensitive)
    private val keywords = mapOf(
        // expressions
        "mix" to TokenType.MIX,
        "take away" to TokenType.TAKE_AWAY,
        "multiply" to TokenType.MULTIPLY,
        "divide" to TokenType.DIVIDE,
        "flip" to TokenType.FLIP,
        "check if" to TokenType.CHECK_IF,

        // statements
        "remember" to TokenType.REMEMBER,
        "set" to TokenType.SET,
        "print" to TokenType.PRINT,
        "as" to TokenType.AS,
        "to" to TokenType.TO,

//        // natural language sugar
//        "the" to TokenType.THE,
//        "value" to TokenType.VALUE,
//        "when" to TokenType.WHEN,
//        "you" to TokenType.YOU,

        // cookbook-style control flow (your choice)
        "when" to TokenType.IF,          // cookbook 'when' -> if
        "otherwise" to TokenType.ELSE,   // cookbook 'otherwise' -> else
        "repeat" to TokenType.WHILE,     // 'repeat' -> while
        "for" to TokenType.FOR,          // 'for' -> for
        "recipe" to TokenType.FUN,       // 'recipe' -> function
        "serve" to TokenType.RETURN,     // 'serve' -> return

        // connectors
        "and" to TokenType.AND,
        "from" to TokenType.FROM,
        "with" to TokenType.WITH,
        "or" to TokenType.OR,

        // boolean
        "true" to TokenType.TRUE,
        "false" to TokenType.FALSE,

    )

    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            start = current
            scanToken()
        }
        tokens.add(Token(TokenType.EOF, "", null, line))
        return tokens
    }

    private fun scanToken() {
        val c = advance()
        when {
            c.isWhitespace() -> {
                if (c == '\n') line++
            }
            c.isDigit() -> number()
            c == '"' -> string()
            c.isLetter() -> identifier()

            c == '{' -> addToken(TokenType.LEFT_BRACE)
            c == '}' -> addToken(TokenType.RIGHT_BRACE)
            c == '(' -> addToken(TokenType.LEFT_PAREN)
            c == ')' -> addToken(TokenType.RIGHT_PAREN)
            c == ';' -> addToken(TokenType.SEMICOLON)
            c == ',' -> addToken(TokenType.COMMA)

            c == '>' -> addToken(TokenType.GREATER)
            c == '<' -> addToken(TokenType.LESS)
            c == '=' && match('=') -> addToken(TokenType.EQUAL_EQUAL)

            else -> error("Unexpected character '$c'")
        }
    }

    private fun identifier() {
        while (peek().isLetterOrDigit() || peek() == '_') advance()

        val original = source.substring(start, current)
        var lower = original.lowercase()

        // handle two-word keywords like "take away", "check if"
        if (peek().isWhitespace()) {
            val saved = current
            skipWhitespace()
            val nextWordStart = current

            while (peek().isLetter()) advance()
            val nextWord = source.substring(nextWordStart, current).lowercase()

            val combined = "$lower $nextWord"
            if (keywords.containsKey(combined)) {
                lower = combined
            } else {
                current = saved
            }
        }


        val type = keywords[lower]
        if (type != null) addToken(type)
        else addToken(TokenType.IDENTIFIER, original) // case-sensitive
    }

    private fun skipWhitespace() {
        while (peek().isWhitespace() && peek() != '\n') advance()
    }

    private fun number() {
        while (peek().isDigit()) advance()
        if (peek() == '.' && peekNext().isDigit()) {
            advance()
            while (peek().isDigit()) advance()
        }
        val value = source.substring(start, current).toDouble()
        addToken(TokenType.NUMBER, value)
    }

    private fun string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++
            advance()
        }
        if (isAtEnd()) {
            error("Unterminated string.")
            return
        }
        advance()
        val value = source.substring(start + 1, current - 1)
        addToken(TokenType.STRING, value)
    }

    private fun advance(): Char {
        current++
        return source[current - 1]
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != expected) return false
        current++
        return true
    }

    private fun peek(): Char =
        if (isAtEnd()) '\u0000' else source[current]

    private fun peekNext(): Char =
        if (current + 1 >= source.length) '\u0000' else source[current + 1]

    private fun isAtEnd() = current >= source.length

    private fun addToken(type: TokenType, literal: Any? = null) {
        tokens.add(Token(type, source.substring(start, current), literal, line))
    }

    private fun error(message: String) {
        println("[Line $line] Error: $message")
    }
}
