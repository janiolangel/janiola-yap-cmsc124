class Scanner(private val source: String) {
    private val tokens = mutableListOf<Token>()
    private var start = 0
    private var current = 0
    private var line = 1

    // All keyword keys are lowercase (for case-insensitive match)
    private val keywords = mapOf(
        "mix" to TokenType.MIX,
        "take away" to TokenType.TAKE_AWAY,
        "combine" to TokenType.COMBINE,
        "share" to TokenType.SHARE,
        "flip" to TokenType.FLIP,
        "check if" to TokenType.CHECK_IF,

        "remember" to TokenType.REMEMBER,
        "set" to TokenType.SET,
        "print" to TokenType.PRINT,
        "as" to TokenType.AS,
        "to" to TokenType.TO,

        "and" to TokenType.AND,
        "from" to TokenType.FROM,
        "with" to TokenType.WITH
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

            c == '(' -> addToken(TokenType.LEFT_PAREN)
            c == ')' -> addToken(TokenType.RIGHT_PAREN)
            c == '{' -> addToken(TokenType.LEFT_BRACE)
            c == '}' -> addToken(TokenType.RIGHT_BRACE)
            c == ';' -> addToken(TokenType.SEMICOLON)

            c == '>' -> addToken(TokenType.GREATER)
            c == '<' -> addToken(TokenType.LESS)
            c == '=' && match('=') -> addToken(TokenType.EQUAL_EQUAL)

            else -> error("Unexpected character '$c'")
        }
    }

    private fun identifier() {
        // read first word (original case)
        while (peek().isLetterOrDigit() || peek() == '_') advance()
        val original = source.substring(start, current)
        var lower = original.lowercase()

        // Check for merged two-word keywords: "take away", "check if"
        if (peek().isWhitespace()) {
            val saved = current
            skipWhitespace()
            val nextWordStart = current
            while (peek().isLetter()) advance()
            val nextOriginal = source.substring(nextWordStart, current)
            val nextLower = nextOriginal.lowercase()

            if (nextLower.isNotEmpty()) {
                val combinedLower = "$lower $nextLower"
                if (keywords.containsKey(combinedLower)) {
                    lower = combinedLower
                } else {
                    // not a known pair -> revert
                    current = saved
                }
            } else {
                current = saved
            }
        }

        val keywordType = keywords[lower]
        if (keywordType != null) {
            addToken(keywordType)
        } else {
            // Not a keyword => IDENTIFIER (case-sensitive name)
            addToken(TokenType.IDENTIFIER, original)
        }
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
        advance() // closing quote

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
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

    private fun error(message: String) {
        println("[Line $line] Error: $message")
    }
}
