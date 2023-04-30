# Introduction
- this is an example of java action

# Status - blocking at figure out the alst number(12345) as DEC
- it is parsed as ACL_NUM currently(but I hope it as DEC because this.enableDEC should take effect)

build this g4
- option1 - use VS code debug
- option2 - use cmd
  - antlr4-parse ex_global_var_combined.g4 entry -tree
  - OR antlr4-parse ex_global_var_combined.g4 entry -gui
```
FAREAST+baoywang@baoywang-homepc MINGW64 ~/OneDrive - Microsoft/ws/github/LibrariesEvaluation/eval-antlr/example_debug_java.action (master) 20:04 30/04/2023
$ antlr4-parse ex_global_var_combined.g4 entry -tree                                                                                                                        
access-list 51 standard 12345
^Z
line 1:24 mismatched input '12345' expecting DEC
(entry:1 access-list 51 standard 12345)
```