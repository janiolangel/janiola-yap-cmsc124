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
    var buffer = StringBuilder()
    var braceBalance = 0

    while (true) {
        // show different prompts depending on mode
        val prompt = if (braceBalance > 0) ". " else "> "
        print(prompt)

        val line = readlnOrNull() ?: break

        if (line.isBlank() && braceBalance == 0) continue

        // add this line to the buffer
        buffer.append(line).append("\n")

        // count braces in this line
        braceBalance += line.count { it == '{' }
        braceBalance -= line.count { it == '}' }

        // only run when all braces matched
        if (braceBalance == 0) {
            val source = buffer.toString()
            buffer = StringBuilder()
            run(source, evaluator)
        }
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

