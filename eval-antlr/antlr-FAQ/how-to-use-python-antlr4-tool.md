# Introduction
python has related binary
- install antler4 command/binary
  - From <https://github.com/antlr/antlr4/blob/master/doc/getting-started.md> 
  - pip install antlr4-tools

- limitation
  - antlr4-parse only supports lexer or combined
  - note: no such limitation on python antlr4 binary, (need generate token file firstly)
    - antlr4 -Dlanguage=Python3 -o output ExJavaActionParser.g4

# install lib for script if you need it
pip install antlr4-python3-runtime

# Example to generate target lang for program
antlr4 -Dlanguage=Python3 -o output ex_global_var_lexer.g4

# Examples to generate tokens/tree/GUI by antlr4-parse(python tool) for quich check
below is the basic usage
```
$ which antlr4-parse
/c/Users/baoywang/AppData/Local/Programs/Python/Python311/Scripts/antlr4-parse

$ antlr4-parse Expr.g4 prog -tokens
1 + 20 + 3 * 4
^Z (for windows), ^D ( for linux)

$ antlr4-parse Expr.g4 prog -tokens < input.ext
1 + 20 + 3 * 4
^Z (for windows), ^D ( for linux)
```

## generate tokens
```
$ antlr4-parse Expr.g4 prog -tokens < input.txt
[@0,0:1='10',<INT>,1:0]
[@1,3:3='+',<'+'>,1:3]
[@2,5:6='20',<INT>,1:5]
[@3,8:8='+',<'+'>,1:8]
[@4,10:11='30',<INT>,1:10]
[@5,13:13='*',<'*'>,1:13]
[@6,15:16='40',<INT>,1:15]
[@7,17:16='<EOF>',<EOF>,1:17]
```

## generate tree
```
$ antlr4-parse Expr.g4 prog -tree < input.txt
(prog:1 (expr:2 (expr:2 (expr:3 10) + (expr:3 20)) + (expr:1 (expr:3 30) * (expr:3 40))) <EOF>)
```

## generate GUI
```
$ antlr4-parse Expr.g4 prog -gui < input.txt  
```

# Limitation
the python antlr4-parse only works on lexer g4 or combined
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