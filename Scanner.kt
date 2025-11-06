class Scanner(private val source: String) {
    private val tokens = mutableListOf<Token>()
    private var start = 0
    private var current = 0
    private var line = 1

    // Map of new keywords
    private val keywords = mapOf(
        "Mix" to TokenType.MIX,                // addition
        "Take away" to TokenType.TAKE_AWAY,    // subtraction
        "Combine" to TokenType.COMBINE,        // multiplication
        "Share" to TokenType.SHARE,            // division
        "Flip" to TokenType.FLIP,              // unary minus
        "Check if" to TokenType.CHECK_IF,      // inequality
        "and" to TokenType.AND,                // connector used in addition/multiplication operations
        "from" to TokenType.FROM,              // connector used in subtraction operation
        "with" to TokenType.WITH,              // connector used in division operation
        ">" to TokenType.GREATER,              // greater-than operator (used with Check if)
        "<" to TokenType.LESS,                 // less-than operator (used with Check if)
        "==" to TokenType.EQUAL_EQUAL          // equality operator (used with Check if)
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
        var text = source.substring(start, current)

        // check if next part creates a two-word keyword (like "Take away" or "Check if")
        if (peek().isWhitespace()) {
            val saved = current
            skipWhitespace()
            val nextWord = readNextWord()
            val twoWord = "$text $nextWord"
            if (keywords.containsKey(twoWord)) {
                text = twoWord
            } else {
                current = saved // revert if not a known pair
            }
        }

        val type = keywords[text] ?: TokenType.IDENTIFIER
        addToken(type)
    }

    private fun skipWhitespace() {
        while (peek().isWhitespace() && peek() != '\n') advance()
    }

    private fun readNextWord(): String {
        val startWord = current
        while (peek().isLetter()) advance()
        return source.substring(startWord, current)
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

