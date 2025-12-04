/*
 * LAB 5
 * Functions
 */

enum class TokenType {
    // Structure
    LEFT_PAREN, RIGHT_PAREN,
    LEFT_BRACE, RIGHT_BRACE,
    SEMICOLON, COMMA,

    // Gen Z verbs (expressions)
    MIX, TAKE_AWAY, MULTIPLY, DIVIDE, FLIP, CHECK_IF,

    // Statement keywords (existing + new cookbook)
    REMEMBER, SET, PRINT, AS, TO,
    THE, VALUE, WHEN, YOU,

    // New cookbook-style control flow keywords
    IF, ELSE,           // mapped from "when" / "otherwise"
    WHILE, FOR,         // "repeat" / "for"
    FUN, RETURN,        // "recipe" / "serve"

    // Connectors for expressions
    AND, FROM, WITH, OR,

    // Comparison
    GREATER, LESS, EQUAL_EQUAL,

    // Literals / identifiers
    IDENTIFIER,
    STRING,
    NUMBER,
    TRUE,
    FALSE,

    EOF
}

data class Token(
    val type: TokenType,
    val lexeme: String,
    val literal: Any?,
    val line: Int
)
