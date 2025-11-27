enum class TokenType {
    // Structure
    LEFT_PAREN, RIGHT_PAREN,

    // Gen Z verbs
    MIX, TAKE_AWAY, COMBINE, SHARE, FLIP, CHECK_IF,

    // Connectors
    AND, FROM, WITH,

    // Comparison
    GREATER, LESS, EQUAL_EQUAL,

    // Literals
    STRING, NUMBER,

    EOF
}

data class Token(
    val type: TokenType,
    val lexeme: String,
    val literal: Any?,
    val line: Int
)
