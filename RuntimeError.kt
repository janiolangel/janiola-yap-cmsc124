// custom runtime error class for Gen Z language
class RuntimeError(val token: Token, message: String) : RuntimeException(message)
