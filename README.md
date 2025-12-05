# **StepScript Programming Language Specification**

**Creators:** 
---
Angel May Janiola & Mae Maricar Yap

---

## **Language Overview**

StepScript is a beginner-friendly programming language modeled after the clarity and flow of cookbook instructions. Its syntax emphasizes readability, step-by-step logic, and natural phrasing. Instead of symbolic expressions, StepScript uses intuitive keywords such as *Mix*, *Take away*, *Flip*, and *Check if* to guide new programmers through common computational operations.

The language supports variable declarations written in a conversational style (“remember x as 10;”), straightforward control flow structures, natural-language expressions, and recipe-like function definitions. Its goal is to enable learners to write programs without the cognitive load of strict or cryptic syntax.

---

## **Keywords**

The following words and tokens are reserved and cannot be used as identifiers.

### **Operation Keywords**

* **Mix** — Addition
* **Take away** — Subtraction
* **Multiply** — Multiplication
* **Divide** — Division
* **Flip** — Unary negation
* **Check if** — Inequality comparison

### **Comparison Operators**

* `>` — Greater than
* `<` — Less than
* `==` — Equality

### **Control Flow Keywords**

**Conditionals:**

* **when**, **otherwise**

**Loops:**

* **repeat**, **for**

**Functions:**

* **recipe** (function declaration)
* **serve** (return value)

**Assignment:**

* **set**, **to**

**Connectors:**

* **and**, **from**, **by**, **or**

### **Boolean Literals**

* **true**, **false**

### **Structural Tokens**

* Parentheses `( )` for grouping and conditions
* Braces `{ }` for blocks
* Comma `,`
* Semicolon `;`

---

## **Operators**

### **Arithmetic / Step Operations**

| Operation      | Keyword   | Example               |
| -------------- | --------- | --------------------- |
| Addition       | Mix       | `Mix 1 and 2`         |
| Subtraction    | Take away | `Take away 4 from 10` |
| Multiplication | Multiply  | `Multiply 2 and 3`    |
| Division       | Divide    | `Divide 20 by 2`      |
| Unary Minus    | Flip      | `Flip 3`              |

### **Comparison**

* `>`
* `<`
* `==`

### **Logic**

Expressed through **Check if** + comparison operator.

---

## **Literals**

* **Numbers:** e.g., `10`, `3.5`
* **Strings:** quoted text, e.g., `"Hello"`
* **Booleans:** `true`, `false`
* **Identifiers:** names for variables, functions, and parameters

---

## **Identifiers**

* Must start with a letter or underscore.
* Can contain letters, digits, or underscores.

---

## **Syntax Style**

### **General Structure**

* Whitespace **is** significant.
* Blocks use braces `{ ... }`.
* Statements end with semicolons `;`.
* Conditions use parentheses `( )`.

### **Variable Declaration**

Uses natural-language phrasing:

```
remember x as 10;
```

### **Functions**

Defined using the keyword **recipe**:

```
recipe add(a, b) {
    serve Mix a and b;
}
```

### **Control Flow**

**Conditional**

```
when (Check if x > 5) {
    ...
} otherwise {
    ...
}
```

**Loops**

```
repeat (Check if x < 10) {
    ...
}

for (i from 1 to 5) {
    ...
}
```

---

## **Design Rationale**

StepScript is built to mirror the clarity of cookbook recipes—step-by-step, concise, and readable. By using natural verbs and phrases instead of abstract symbols, beginners can focus on understanding algorithms rather than memorizing syntax. This reduces entry barriers for new programmers while preserving enough structure to write expressive, logical programs. The language intentionally uses one clear way to express each idea to minimize confusion and improve code readability.

---

## **Operation Examples**

### **1. Addition**

Expression: `1 + 2`
Evaluation:

```
Step 1: Mix 1 and 2 →
Result: 3
```

### **2. Subtraction**

Expression: `10 - 4`

```
Take away 4 from 10 →
Result: 6
```

### **3. Multiplication**

Expression: `2 * 3`

```
Multiply 2 and 3 →
Result: 6
```

### **4. Division**

Expression: `20 / 2`

```
Divide 20 by 2 →
Result: 10
```

### **5. Unary Minus**

Expression: `-3`

```
Flip 3 →
Result: -3
```

### **6. Inequality**

Expression: `5 > 3`

```
Check if 5 > 3 →
Result: true
```

---

## **Grammar Specification**

```
Program:
    Statement*

Statement:
      VarDecl
    | SetStmt
    | PrintStmt
    | IfStmt
    | WhileStmt
    | ForStmt
    | FunctionDecl
    | Block
    | ExprStmt

FunctionDecl:
    recipe IDENTIFIER (Parameters?) Block

Block:
    { Statement }

Expr:
    LogicOr

Value:
    NUMBER | STRING | TRUE | FALSE | IDENTIFIER | (Expr)

StepOperations:
      "Mix" <Value> "and" <Value>
    | "Take away" <Value> "from" <Value>
    | "Multiply" <Value> "and" <Value>
    | "Divide" <Value> "by" <Value>
    | "Flip" <Value>
    | "Check if" <Value> <Inequality> <Value>

Inequality:
    ">" | "<" | "=="
```

---
