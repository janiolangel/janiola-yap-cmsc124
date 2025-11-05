// fixed list of possible token types
enum class TokenType {
    // Structure
    LEFT_PAREN, RIGHT_PAREN,
    COMMA, SEMICOLON,

    // Gen Z verbs (keywords)
    MIX,          // addition
    TAKE_AWAY,    // subtraction
    COMBINE,      // multiplication
    SHARE,        // division
    FLIP,         // unary minus
    CHECK_IF,     // inequality intro word

    // Connectors
    AND, FROM, WITH,

    // Comparison operators
    GREATER, LESS, EQUAL_EQUAL,

    // Literals
    IDENTIFIER, STRING, NUMBER,

    EOF
}

// token data class
data class Token(
    val type: TokenType,
    val lexeme: String,
    val literal: Any?,
    val line: Int
)
