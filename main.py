from scanner import Scanner

def repl():
    print("Lexical Scanner (type 'exit' to quit)")
    while True:
        try:
            line = input("> ")
            if line.strip() == "exit":
                break
            scanner = Scanner(line)
            tokens = scanner.scan_tokens()
            for token in tokens:
                print(token)
        except EOFError:
            break

if __name__ == "__main__":
    repl()
