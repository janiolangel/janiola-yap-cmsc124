// custom runtime error class
class RuntimeError(val token: Token, message: String) : RuntimeException(message)
