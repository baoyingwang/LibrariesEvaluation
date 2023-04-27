# Introduction
Just add the Expr.g4 file https://github.com/antlr/antlr4/blob/master/doc/getting-started.md
and play below locally
```
$ antlr4-parse Expr.g4 prog -tree
10+20*30
^Z
(prog:1 (expr:2 (expr:3 10) + (expr:1 (expr:3 20) * (expr:3 30))) <EOF>)

$ antlr4-parse Expr.g4 prog -gui
10+20*30
^Z
```