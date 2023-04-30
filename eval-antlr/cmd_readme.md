# parse and get the parse tree by antlr4-parse
- GUI: 
  - antlr4-parse Expr.g4 prog -gui
- OR Text:
```
$ antlr4-parse Expr.g4 prog -tree
10+20*30
^Z (for windows), or ^D(for linux)
(prog:1 (expr:2 (expr:3 10) + (expr:1 (expr:3 20) * (expr:3 30))) <EOF>)
```
source https://github.com/antlr/antlr4/blob/master/doc/getting-started.md

# tip for antlr4-parse: it can only handle lexer or combined.
e.g. it throws error like below when provide the parser g4 file as parameter. In the parser g4, it link with lexer by option: tokenVocab = ex_global_var_lexer
```
FAREAST+baoywang@baoywang-homepc MINGW64 ~/OneDrive - Microsoft/ws/github/LibrariesEvaluation/eval-antlr/example_global_var (master) 17:38 30/04/2023
$ antlr4-parse ex_global_var_parser.g4 entry -tree
access-list 64915 standard 12345
^Z
warning(125): ex_global_var_parser.g4:8:2: implicit definition of token ACCESS_LIST in parser
Exception in thread "main" java.lang.IllegalStateException: A lexer interpreter can only be created for a lexer or combined grammar.
        at org.antlr.v4.tool.Grammar.createLexerInterpreter(Grammar.java:1319)
        at org.antlr.v4.gui.Interpreter.interp(Interpreter.java:167)
        at org.antlr.v4.gui.Interpreter.main(Interpreter.java:277)
```

# VS code antlr plugin - ANTLR4 grammar syntax support
- home page: https://github.com/mike-lischke/vscode-antlr4
  - debug: https://github.com/mike-lischke/vscode-antlr4/blob/master/doc/grammar-debugging.md