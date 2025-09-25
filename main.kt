package cmsc124_lab1

fun repl() {
    println("Lexical cmsc124_lab1.Scanner (type 'exit' to quit)")
    val input = java.util.Scanner(System.`in`)

    while (true) {
        print("> ")
        if (!input.hasNextLine()) break
        val line = input.nextLine()
        if (line.trim() == "exit") break

        val scanner = Scanner(line)
        val token = scanner.scanTokens()
        println(token)
        }
    }

fun main() {
    repl()
}
