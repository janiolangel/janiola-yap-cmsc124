1. How is a variable declared?
   
For our implemented language, we kept it simple and straightforward just like python. We make a variable just by assigning a value with `=`. For example, `x = 10` is a number, then `x = "hi"` becomes a string.

 
2. What keywords are reserved by the programming language?
   
Keywords such as `if - else if - else`, `for`, `while`, `function`, `class`, and `return`.

 
3. Is whitespace significant? Or is it bracketed like the C family of programming languages?
   
No, the whitespace is insignificant. The lexer will just skip it and the parser relies on clear symbols ({}, end, ;) for easier implementation. 


4. How are comments styled? Are nested or docstring-style comments allowed?
   
A single-line comment is written using a double slash (//), while a multi-line (block) comment is written using triple slashes (/* ... */).


5. How does it do loops and other common language constructs?
   
It uses 'for' and 'while' loops to keep running while a condition is true and 'if' and 'else` for conditionals, allowing the program to branch into different paths, which together form the simplest and easiest core constructs for a language.


6. Why is it this way? What is the motivation behind your choices?

We focus on readability and simplicity, choosing high-level features to eliminate unnecessary code. Its “one obvious way” approach makes it both beginner-friendly and powerful.

   
