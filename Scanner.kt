class Scanner(private val source: String) {
    private val tokens = mutableListOf<Token>()
    private var start = 0
    private var current = 0
    private var line = 1

    // Map of Gen Z keywords
    private val keywords = mapOf(
        "Mix" to TokenType.MIX,
        "Take" to TokenType.TAKE_AWAY,      // 'Take away'
        "away" to TokenType.TAKE_AWAY,
        "Combine" to TokenType.COMBINE,
        "Share" to TokenType.SHARE,
        "Flip" to TokenType.FLIP,
        "Check" to TokenType.CHECK_IF,      // 'Check if'
        "if" to TokenType.CHECK_IF,
        "and" to TokenType.AND,
        "from" to TokenType.FROM,
        "with" to TokenType.WITH,
        ">" to TokenType.GREATER,
        "<" to TokenType.LESS,
        "==" to TokenType.EQUAL_EQUAL
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
            c == '>' -> addToken(TokenType.GREATER)
            c == '<' -> addToken(TokenType.LESS)
            else -> error("Unexpected character '$c'")
        }
    }

    private fun identifier() {
        while (peek().isLetter()) advance()
        val text = source.substring(start, current)
        val type = keywords[text] ?: TokenType.IDENTIFIER
        addToken(type)
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
        advance() // closing quote
        val value = source.substring(start + 1, current - 1)
        addToken(TokenType.STRING, value)
    }

    private fun advance(): Char {
        current++
        return source[current - 1]
    }

    private fun peek(): Char =
        if (isAtEnd()) '\u0000' else source[current]

    private fun peekNext(): Char =
        if (current + 1 >= source.length) '\u0000' else source[current + 1]

    private fun isAtEnd() = current >= source.length

    private fun addToken(type: TokenType, literal: Any? = null) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

    private fun error(message: String) {
        println("[Line $line] Error: $message")
    }
}
