class Scanner(private val source: String) {
    private val tokens = mutableListOf<Token>()
    private var start = 0
    private var current = 0
    private var line = 1

    // Multi-character TEXTO letter codes (map from TEXTO symbol to its Latin letter if you want)
    // Order matters: longer strings first to ensure greedy matching.
    private val textoMulti = listOf(
        "|_|_|", // W (three-part)
        "|_|",   // V (or part of W)
        "}-{" ,  // H
        "^^",    // M
        "/>",    // P
        "/@",    // Q
        "()",
        "?_",
        "><"
    )

    // Single-character TEXTO letter symbols
    private val textoSingle = listOf(
        "@", "#", "*", "]", "€", "£", "&", "+", "!", "^", "-", "/", ">", ";", "$", ".", "•", "?", "|", "<", "¥"
    )

    // Combined list for identifier-start matching (multi first)
    private val textoAllStarts: List<String> = (textoMulti + textoSingle).sortedByDescending { it.length }

    // TEXTO operators (single unicode characters)
    private val operatorSet = mapOf(
        '⊕' to TokenType.PLUS,
        '⊖' to TokenType.MINUS,
        '⊗' to TokenType.STAR,
        '÷' to TokenType.SLASH,
        '≡' to TokenType.EQUAL,        // note: equality-check vs assignment handled below
        '≠' to TokenType.BANG_EQUAL
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
        // Before consuming single chars, check if a TEXTO multi/single symbol starts here
        // If so, treat it as the start of an identifier (letters are TEXTO symbols)
        val textoMatch = matchTextoAt(current)
        if (textoMatch != null) {
            // start an identifier token beginning with this TEXTO symbol
            identifier()
            return
        }

        val c = advance()
        when (c) {
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '{' -> addToken(TokenType.LEFT_BRACE)
            '}' -> addToken(TokenType.RIGHT_BRACE)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            ';' -> addToken(TokenType.SEMICOLON)

            // TEXTO operators (single-char) — map to token types where appropriate
            in operatorSet.keys -> {
                val mapped = operatorSet[c]!!
                // Special handling: '≡' can be assignment or equality operator depending on next char (if double ≡≡ means equal-equal by your language convention)
                if (c == '≡') {
                    if (match('≡')) addToken(TokenType.EQUAL_EQUAL) else addToken(TokenType.EQUAL)
                } else {
                    // other operators are single-token
                    addToken(mapped)
                }
            }

            // Comments start: '~~' single-line or '~*' block comment
            '~' -> {
                if (match('~')) {
                    // single-line comment: skip until newline
                    while (peek() != '\n' && !isAtEnd()) advance()
                    // do not add any token — comment ignored
                } else if (match('*')) {
                    // multi-line docstring comment: ~* ... *~
                    while (!(peek() == '*' && peekNext() == '~') && !isAtEnd()) {
                        if (peek() == '\n') line++
                        advance()
                    }
                    if (!isAtEnd()) {
                        // consume '*~'
                        advance()
                        advance()
                    }
                    // do not add any token — comment ignored
                } else {
                    // stray '~' — treat as unexpected or ignore silently
                    error("Unexpected character: $c")
                }
            }


            // Strings
            '"' -> string()

            // Numbers
            in '0'..'9' -> number()

            // whitespace
            ' ', '\r', '\t' -> { /* ignore */ }
            '\n' -> line++

            else -> {
                // English letters are not allowed — only TEXTO symbols
                error("Unexpected character: $c")
            }
        }
    }

    // Called when we know the identifier should start at current (no char consumed yet).
    // Consumes consecutive TEXTO symbols, ASCII letters/digits/'_' to form an identifier.
    private fun identifier() {
        // We assume start is set to the beginning (current hasn't moved)
        while (!isAtEnd()) {
            // if digit -> consume
            if (peek().isDigit()) {
                advance()
                continue
            }
            // if underscore or ASCII letter -> consume single char
            if (peek() == '_' || peek().isLetter()) {
                advance()
                continue
            }
            // try to match any TEXTO symbol starting at current
            val matched = matchTextoAt(current)
            if (matched != null) {
                // advance by matched length
                advanceBy(matched.length)
                continue
            }
            break
        }

        val text = source.substring(start, current)
        val type = keywords[text] ?: TokenType.IDENTIFIER
        addToken(type)
    }

    // Called when we've already advanced one ASCII letter (e.g., 'a' consumed)
    // start must point to beginning of token substring (so start <= current-1)
    private fun identifierFromGivenAdvance() {
        while (!isAtEnd()) {
            if (peek().isDigit()) {
                advance()
                continue
            }
            if (peek() == '_' || peek().isLetter()) {
                advance()
                continue
            }
            val matched = matchTextoAt(current)
            if (matched != null) {
                advanceBy(matched.length)
                continue
            }
            break
        }
        val text = source.substring(start, current)
        val type = keywords[text] ?: TokenType.IDENTIFIER
        addToken(type)
    }

    // Try to match any TEXTO multi or single symbol at given index.
    // Returns the matched symbol string (greedy longest match) or null.
    private fun matchTextoAt(index: Int): String? {
        if (index >= source.length) return null
        for (sym in textoAllStarts) {
            if (index + sym.length <= source.length && source.substring(index, index + sym.length) == sym) {
                return sym
            }
        }
        return null
    }

    // Advance by n characters (n >= 1)
    private fun advanceBy(n: Int) {
        repeat(n) { advance() }
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

    private fun peek(): Char {
        if (isAtEnd()) return '\u0000'
        return source[current]
    }

    private fun peekNext(): Char {
        if (current + 1 >= source.length) return '\u0000'
        return source[current + 1]
    }

    private fun isAtEnd(): Boolean = current >= source.length

    private fun addToken(type: TokenType, literal: Any? = null) {
        val text = source.substring(start, current)
        tokens.add(Token(type, text, literal, line))
    }

    // ---- String handling ----
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

    // ---- Number handling ----
    private fun number() {
        while (peek().isDigit()) advance()

        if (peek() == '.' && peekNext().isDigit()) {
            advance() // consume '.'
            while (peek().isDigit()) advance()
        }

        val value = source.substring(start, current).toDouble()
        addToken(TokenType.NUMBER, value)
    }

    // ---- Error handling ----
    private fun error(message: String) {
        println("[Line $line] Error: $message")
    }
}
