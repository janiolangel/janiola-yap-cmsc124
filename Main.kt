import java.io.File

fun main(args: Array<String>) {         // determines whether to run REPL or execute a file
    val evaluator = Evaluator()

    when (args.size) {
        0 -> {                          // no args → interactive REPL mode
            evaluator.isRepl = true
            runRepl(evaluator)
        }
        1 -> {                          // one arg → execute file
            evaluator.isRepl = false
            runFile(args[0], evaluator)
        }
        else -> println("Usage: genz [script]")   // invalid args → show usage
    }
}

fun runFile(path: String, evaluator: Evaluator) {
    val source = File(path).readText()

    val scanner = Scanner(source)
    val tokens = scanner.scanTokens()

    val parser = Parser(tokens)
    val statement = parser.parse()

    try {
        evaluator.interpret(statement)
    } catch (err: RuntimeError) {
        println("[line ${err.token.line}] Runtime error: ${err.message}")
    }
}


private fun runRepl(evaluator: Evaluator) {
    var buffer = StringBuilder()
    var braceBalance = 0

    while (true) {
        val prompt = if (braceBalance > 0) ". " else "> "
        print(prompt)

        val line = readlnOrNull() ?: break
        val trimmed = line.trim()

        // skip empty lines outside block
        if (trimmed.isEmpty() && braceBalance == 0) continue

        buffer.append(line).append("\n")

        // update brace count for blocks3
        braceBalance += line.count { it == '{' }
        braceBalance -= line.count { it == '}' }

        // if inside a block → keep reading
        if (braceBalance > 0) continue

        // if we reach here, braces are balanced → time to run

        var source = buffer.toString().trim()
        buffer = StringBuilder() // Reset buffer

        // if it doesn't end in semicolon AND does not start with a statement
        // treat as an expression → append semicolon
        val lower = source.lowercase()

        val startsLikeStatement =
            lower.startsWith("remember ") ||
                    lower.startsWith("set ") ||
                    lower.startsWith("print ") ||
                    lower.startsWith("{") ||
                    lower.startsWith("}")

        if (!startsLikeStatement && !source.endsWith(";")) {
            source += ";"
        }
        // -------------------------------------------------------------------

        run(source, evaluator)
    }
}

private fun run(source: String, evaluator: Evaluator) {
    val scanner = Scanner(source)
    val tokens = scanner.scanTokens()

    val parser = Parser(tokens)
    val statement = parser.parse()

    try {
        evaluator.interpret(statement)
    } catch (err: RuntimeError) {
        println("[line ${err.token.line}] Runtime error: ${err.message}")
    }
}

