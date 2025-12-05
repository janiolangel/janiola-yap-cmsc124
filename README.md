1. How is a variable declared?
   Our implemented programming language is named StepScript to emphasize its step-by-step, recipe-like approach to coding. The language is designed for simplicity and ease of use, allowing both beginners and experienced programmers to engage with it comfortably. Variable declaration follows a natural and intuitive style. For example: "remember x as 10;" is similar to telling someone to remember an ingredient for later use. By keeping declarations this readable, StepScript allows beginners to focus on understanding the flow of their program rather than struggling with strict syntax rules.

2. What keywords are reserved by the programming language?
   a) Operation keywords: Mix, Take away from, Multiply, Divide, Flip, Check if
   b) Comparison operators: > , < , ==
   c) Control Flow Keywords:
         Conditionals: when, otherwise
         Loops: repeat, for
         Functions: recipe, serve
         Assignment: set, to
         Connectors: and, from, by, or
   d) Boolean Literals: true, false
   e) Other Structural Tokens: parentheses, braces, comma, semicolon

4. Is whitespace significant? Or is it bracketed like the C family of programming languages?
   Whitespace is not significant in StepScript. Instead of relying on indentation, the language uses clear keywords, parentheses for conditions, and braces for blocks. This keeps the structure easy to read while avoiding the rigid whitespace requirements found in some other languages.

5. How are comments styled? Are nested or docstring-style comments allowed?
   A single-line comment is written using a double slash (//), while a multi-line (block) comment is written using triple slashes (/* ... */).

7. How does it do loops and other common language constructs?
   StepScript uses readable, recipe-like phrasing for its control structures. It supports when and otherwise for conditionals, repeat for while loops, and for for counted loops.

8. Why is it this way? What is the motivation behind your choices?
   StepScript is inspired by cookbooks because they are clear, simple, and easy to follow. We designed the language to use natural instructions instead of complex symbols so beginners can learn without feeling overwhelmed. By keeping one clear way to express each idea, the language stays easy to read while still being powerful enough for real programs.
   
-------------------------------------------------------------------------------

StepScript Operation Overview
Addition = Mix
Subtraction = Take away
Multiplication = Multiply
Division =  Divide
Unary Minus = Flip
Inequality = Check if

1. Addition Example
Expression: 1 + 2
Step-by-step Evaluation:
   Step 1: Mix 1 and 2
   Result: 3

2. Subtraction Example
Expression: 10 - 4
Step-by-step Evaluation:
   Step 1: Take away 4 from 10
   Result: 6

3. Multiplication Example
Expression: 2 * 3 
Step-by-step Evaluation:
   Step 1: Multiply 2 and 3
   Result: 6

4. Division Example
Expression: 20 / 2
Step-by-Step Evaluation:
   Step 1: Divide 20 by 2 
   Result: 10

5. Unary Minus Example
Expression: 3
Step-by-Step Evaluation:
   Step 1: Flip 3
   Result: -3

6. Inequality Example
Expression: 5 > 3
Step-by-Step Evaluation:
   Step 1: Check if 5 > 3
   Result: true

-------------------------------------------------------------------------------

Grammar: 

<Program> ::= <Statement>*

<Statement> ::=
      <VarDecl>
    | <SetStmt>
    | <PrintStmt>
    | <IfStmt>
    | <WhileStmt>
    | <ForStmt>
    | <FunctionDecl>
    | <Block>
    | <ExprStmt>

<FunctionDecl> ::= "recipe" IDENTIFIER "(" <Parameters>? ")" <Block>

<Block> ::= "{" <Statement>* "}"

<Expr> ::= <LogicOr>

<Value> ::= NUMBER | STRING | TRUE | FALSE | IDENTIFIER | "(" <Expr> ")"

<StepOperations> ::=
        "Mix" <Value> "and" <Value>
      | "Take away" <Value> "from" <Value>
      | "Multiply" <Value> "and" <Value>
      | "Divide" <Value> "by" <Value>
      | "Flip" <Value>
      | "Check if" <Value> <Inequality> <Value>

<Inequality> ::= ">" | "<" | "=="

