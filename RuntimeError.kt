// Custom runtime error class for your Gen Z language
class RuntimeError(val token: Token, message: String) : RuntimeException(message)
