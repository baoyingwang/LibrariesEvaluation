# How To
0. npm install antlr4ts (TODO double confirm whether this is mandatory)
  - https://github.com/mike-lischke/vscode-antlr4/blob/HEAD/doc/parser-generation.md
1. copy the launch_example.json to .vscode/launch.json
2. change the g4 file, input txt, start rule etc accordingly
3. open the Run and Debug, you will find this example debug profile.

output
1. the DEBUG CONSOLE windows, you can find below tokens list, and parse tree(text)
2. a new parse tree GUI window
3. debug breakpoints can be setup
```
Tokens:
[@0,0:1='10',<8>,1:0]
[@1,3:3='+',<3>,1:3]
[@2,5:6='20',<8>,1:5]
[@3,8:8='+',<3>,1:8]
[@4,10:11='30',<8>,1:10]
[@5,13:13='*',<1>,1:13]
[@6,15:16='40',<8>,1:15]
[@7,17:16='<EOF>',<-1>,1:17]
Parse Tree:
prog (
 expr (
  expr (
   expr (
    "10"
   )
   "+"
   expr (
    "20"
   )
  )
  "+"
  expr (
   expr (
    "30"
   )
   "*"
   expr (
    "40"
   )
  )
 )
)


```
# Note 
the  Expr.g4 file is from https://github.com/antlr/antlr4/blob/master/doc/getting-started.md

# Refer
- https://github.com/mike-lischke/vscode-antlr4/blob/master/doc/grammar-debugging.md
- https://github.com/mike-lischke/vscode-antlr4/issues/65