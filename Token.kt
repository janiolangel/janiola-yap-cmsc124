enum class TokenType {
    // Structure
    LEFT_PAREN, RIGHT_PAREN,
    LEFT_BRACE, RIGHT_BRACE,
    SEMICOLON,

    // Gen Z verbs (expressions)
    MIX, TAKE_AWAY, COMBINE, SHARE, FLIP, CHECK_IF,

    // Statement keywords
    REMEMBER, SET, PRINT, AS, TO,

    // Connectors for expressions
    AND, FROM, WITH,

    // Comparison
    GREATER, LESS, EQUAL_EQUAL,

    // Literals / identifiers
    IDENTIFIER,
    STRING,
    NUMBER,

    EOF
}

data class Token(
    val type: TokenType,
    val lexeme: String,
    val literal: Any?,
    val line: Int
)
