from enum import Enum, auto

class TokenType(Enum):
    # single-character tokens
    LEFT_PAREN = auto(); RIGHT_PAREN = auto()
    LEFT_BRACE = auto(); RIGHT_BRACE = auto()
    COMMA = auto(); DOT = auto(); MINUS = auto(); PLUS = auto()
    SEMICOLON = auto(); SLASH = auto(); STAR = auto()
    EQUAL = auto(); LESS = auto(); GREATER = auto(); BANG = auto()

    # multi-character operators
    EQUAL_EQUAL = auto()
    BANG_EQUAL = auto()
    LESS_EQUAL = auto()
    GREATER_EQUAL = auto()

    # literals
    STRING = auto()
    NUMBER = auto()

    EOF = auto()


class Token:
    def __init__(self, type_, lexeme, literal, line):
        self.type = type_
        self.lexeme = lexeme
        self.literal = literal
        self.line = line

    def __repr__(self):
        return f"Token(type={self.type}, lexeme='{self.lexeme}', literal={self.literal}, line={self.line})"
