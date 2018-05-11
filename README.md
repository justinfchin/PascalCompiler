# PascalCompiler
- written in Java
- book used
    - Crafting a Compiler by Fischer Cytron and LeBlanc
## Background
### Compiler.java
    - main program used to compile your Pascal source code
### Lexer.java
    - aka lexical analyzer, scanner, tokenzierr
    - reads source code 1 char at a time and groups them into tokens
        - tokens are identifiers, integers, keywords, and delimeters
    - eliminates unneeded info (like comments)
    - Only Arithmetic and Relational Operators, no Bitwise or Boolean
    - Notes:
        - used HashMap instead of HashTable because did not need thread synchroniziation
        - HashMap also allows for null 
### keywords.txt
    - list of pascal keywords the compiler understands
    - [list used](https://www.freepascal.org/docs-html/ref/refsu1.html)
### Token.java
    - class to create objects that have a type and a value
### Parser.java
    - takes the encoded tokens (often as integers) for syntactic analysis
    - understands the syntax of the language 
    - creates a tree of nodes called the Abstract Syntax Tree (AST) 
### Optimizer.java
    - for our purposes this was not used
### SymbolTable.java
    -
