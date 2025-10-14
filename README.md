Texto is a symbol-based alphabet where each letter (A-Z) is represented by a corresponding symbol or special character. This encoding system allows text to be written using symbols like @, #, *, and other characters instead of standard letters.

TEXTO CODES: 
A = @      N = -
B = #      O = ()
C = *      P = />
D = ]      Q = /~
E = €      R = ;
F = £      S = $
G = &      T = .,
H = }-{    U = /
I = •      V = |_|
J = ?      W = |_|_|
K = +      X = ><
L = !      Y = ¥
M = ^^     Z = ?_

OPERATORS:
⊕ (addition)    
⊖ (subtraction) 
⊗ (multiplication) 
÷ (division) 
≡ (equal sign)
≠ (not equal)

1. How is a variable declared?
   A variable is declared using the “=” sign. Example: `@ = 5` (a = 5)

2. What keywords are reserved by the programming language?
   `var`, `if`, `else`, `while`, `for`, `return`

3. Is whitespace significant? Or is it bracketed like the C family of programming languages?
   No, whitespace is insignificant. The lexer skips it, and the parser relies on clear symbols (`{}`, `end`, `;`)
   for easier implementation.

5. How are comments styled? Are nested or docstring-style comments allowed?
   Single-line comments use `~~`, and docstring-style comments use `~* *~`. Nested comments are not allowed.

6. How does it do loops and other common language constructs?
   It uses familiar constructs like `for`, `while`, and `if-else`. Example:
   `for (i = 0; i < 10; i++) { print(i) }`

7. Why is it this way? What is the motivation behind your choices?
   The language was designed to be simple and easy for the lexer to tokenize while maintaining readability. Its structure follows Kotlin’s
   logical style for clarity and efficiency. At the same time, using symbols makes it feel like a fun, secret language that remains
   understandable to those familiar with its patterns.


Would you like me to rewrite this version next using your **symbolic code textos** (like `|_| @;` for `var` and `!?` for `if`)?
