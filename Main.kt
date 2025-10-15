// entry point
// provides REPL (Read-Eval-Print Loop)

fun main() {
    println("Lexical cmsc124_lab2.Parser (type 'exit' to quit)")
    val input = java.util.Scanner(System.`in`)

    while (true) {
        print("> ")
        if (!input.hasNextLine()) break
        val line = input.nextLine()
        if (line.trim() == "exit") break

        // Scanner (send and return)
        val scanner = Scanner(line)
        val tokens = scanner.scanTokens()

        // Parser
        val parser = Parser(tokens)
        val expression = parser.parse()

        // Print AST if parsing succeeded
        if (expression != null) {
            val printer = AstPrinter()
            println(printer.print(expression))
        }
    }
}
