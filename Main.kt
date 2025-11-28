import java.io.File

fun main(args: Array<String>) {
    val evaluator = Evaluator()

    when (args.size) {
        0 -> runRepl(evaluator)
        1 -> runFile(args[0], evaluator)
        else -> {
            println("Usage: genz-language [script]")
        }
    }
}

private fun runRepl(evaluator: Evaluator) {
    while (true) {
        print("> ")
        val line = readlnOrNull() ?: break
        if (line.isBlank()) continue
        run(line, evaluator)
    }
}

private fun runFile(path: String, evaluator: Evaluator) {
    val source = File(path).readText()
    run(source, evaluator)
}

private fun run(source: String, evaluator: Evaluator) {
    val scanner = Scanner(source)
    val tokens = scanner.scanTokens()

    val parser = Parser(tokens)
    val statements = parser.parse()

    try {
        evaluator.interpret(statements)
    } catch (err: RuntimeError) {
        println("[line ${err.token.line}] Runtime error: ${err.message}")
    }
}
