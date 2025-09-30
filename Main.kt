// entry point
// provides REPL (Read-Eval-Print Loop)

fun main() {
    println("Lexical cmsc124_lab1.Scanner (type 'exit' to quit)")
    val input = java.util.Scanner(System.`in`)

    while (true) {
        print("> ")
        if (!input.hasNextLine()) break
        val line = input.nextLine()
        if (line.trim() == "exit") break

        // send and return
        val scanner = Scanner(line)
        val tokens = scanner.scanTokens()

        // join tokens with newlines
        println(tokens.joinToString("\n"))
    }
}

