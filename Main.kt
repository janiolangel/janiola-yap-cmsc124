fun main() {
    while (true) {
        print("> ")
        val line = readlnOrNull() ?: break
        if (line.isBlank()) continue
        run(line)
    }
}

fun run(source: String) {
    val scanner = Scanner(source)
    val tokens = scanner.scanTokens()

    val parser = Parser(tokens)
    val expression = parser.parse()

    if (expression != null) {
        val evaluator = Evaluator()
        try {
            val result = evaluator.evaluate(expression)
            println(result)
        } catch (err: RuntimeError) {
            println("[line ${err.token.line}] Runtime error: ${err.message}")
        }
    } else {
        println("Parsing failed — expression is null.")
    }
}
