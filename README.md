1. How is a variable declared?
   In our implemented programming language, we have named it "StepScript" to highlight its step-by-step algorithm. StepScript is crafted with simplicity and ease of use in mind, ensuring that both beginners and experienced programmers can engage with it effectively. We prioritize simplicity in our syntax, allowing learners to grasp concepts quickly. For example, to declare a variable, we simply say, "Remember X as 10," which makes the process intuitive and easy to understand. With StepScript, our goal is to create an engaging learning environment where students can focus on developing their programming skills without being overwhelmed by complex rules or syntax. 

2. What keywords are reserved by the programming language?
   Operation keywords:
      - Mix, Take away from, Combine, Share with, Flip, Check if

   Comparison operators:
      > , < , ==

   Structural token:
      and

3. Is whitespace significant? Or is it bracketed like the C family of programming languages?
   No, the whitespace is insignificant. It is also not enclosed in brackets since it is just a phrase.
   This allows for a more natural and fluid expression of the concept.

4. How are comments styled? Are nested or docstring-style comments allowed?
   A single-line comment is written using a double slash (//), while a multi-line (block) comment is written using triple slashes (/* ... */).

7. How does it do loops and other common language constructs?
   It uses 'for' and 'while' loops to keep running while a condition is true and 'if' and 'else` for conditionals, allowing the program to branch into different paths, which together form the simplest and easiest core constructs for a language.

8. Why is it this way? What is the motivation behind your choices?
   We focus on readability and simplicity, choosing high-level features to eliminate unnecessary code. Its “one obvious way” approach makes it both beginner-friendly and powerful.



-------------------------------------------------------------------------------
GenZ Step-by-Step 
Operation
Verb Used
Addition
Mix
Subtraction
Take away
Multiplication
Combine
Division
Share
Unary Minus
Flip
Inequality
Check if … →

1. Addition Example
Expression: 1 + 2 + 3
Step-by-Step Evaluation:
Step 1: Mix 1 and 2 
Step 2: Mix 3 and 3  

2. Subtraction Example
Expression: 10 - 4 - 1
Step-by-Step Evaluation:
Step 1: Take away 4 from 10
Step 2: Take away 1 from 6 

3. Multiplication Example
Expression: 2 * 3 * 4
Step-by-Step Evaluation:
Step 1: Combine 2 and 3 
Step 2: Combine 6 and 4 

4. Division Example
Expression: 20 / 2 / 5
Step-by-Step Evaluation:
Step 1: Share 20 with 2 
Step 2: Share 10 with 5

5. Unary Minus Example
Expression: -(3 + 4)
Step-by-Step Evaluation:
Step 1: Mix 3 and 4 
Step 2: Flip '7'

6. Inequality Example
Expression: 5 > 3
Step-by-Step Evaluation:
Step 1: Check if 5 is greater than 3 → Yes, true


Grammar: 

Step1: Mix 1 and 2
Step2: Mix Step1 and 3
Step3: Flip Step2
Step4: Check if Step3 < 0

Interpreter:
- Parse "Mix 1 and 2" 
Parse "Mix Step1 and 3" 
Parse "Flip Step2" 

<Program> ::= <Step> | <Step> <Program>

<Step> ::= "Mix" <Value> "and" <Value>                               // addition
         | "Take away" <Value> "from" <Value>                        // subtraction
         | "Combine" <Value> "and" <Value>                           // multiplication
         | "Share" <Value> "with" <Value>                            // division
         | "Flip" <Value>                                            // unary minus
         | "Check if" <Value> <Inequality> <Value>                   // inequality

<Value> ::= number | result of previous step
<Inequality> ::= ">" | "<" | "=="

