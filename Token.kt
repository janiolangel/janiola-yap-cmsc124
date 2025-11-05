// fixed list of possible token types
enum class TokenType {
    // Structure
    LEFT_PAREN, RIGHT_PAREN,
    COMMA, SEMICOLON,

    // new verbs
    MIX, TAKE_AWAY, COMBINE, SHARE, FLIP, CHECK_IF,

    // Connectors
    AND, FROM, WITH,

    // Comparison
    GREATER, LESS, EQUAL_EQUAL,

    // Literals
    IDENTIFIER, STRING, NUMBER,

    EOF
}

// the actual data
data class Token(
    val type: TokenType,
    val lexeme: String,
    val literal: Any?,
    val line: Int
)
