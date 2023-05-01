# How To
# Option1 - use intellij (community version can work) - preferred
- limitation: don't support {action} on target language
- install G4 plugin as indicated by intellij
- click 'ANTLR Preview'(from the bottom) to show the related windows out, and start debug by text input for file input
  - it will also show the Parse tree, or show the Hierarchy, profiler etc
- refer
  - https://stackoverflow.com/questions/69081085/how-can-we-test-antlr-lexer-rules-using-intellij-antlr-v4-plugin
  - refer https://www.youtube.com/watch?v=0A2-BquvxMU ANTLR4 Intellij Plugin -- Parser Preview, Parse Tree, and Profiling


# option 2 - use the VS code antlr plugin - ANTLR4 grammar syntax support
- limitation: don't support {action} on target language
- refer
  - plugin home page: https://github.com/mike-lischke/vscode-antlr4
  - plugin debug: https://github.com/mike-lischke/vscode-antlr4/blob/master/doc/grammar-debugging.md

- how to setup
1. copy the launch_example.json to .vscode/launch.json
1. change the g4 file, input txt, start rule etc accordingly
1. open the Run and Debug, you will find this example debug profile.

output
1. the DEBUG CONSOLE windows, you can find below tokens list, and parse tree(text)
1. a new parse tree GUI window
1. debug breakpoints can be setup
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