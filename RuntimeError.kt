// REPORT CLEAR, HELPFUL ERROR MESSAGES THAT INCLUDE LINE NUMBERS
// TO BE PRINTED IN MAIN.KT
class RuntimeError(val token: Token, message: String) : RuntimeException(message)
