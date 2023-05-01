# Introduction
how to print tokens/tree/GUI on provided lexer or parser?
there are several options
- use antlr4-parse(python tool) - only works on lexer g4 or combined
  - this is commandline tool for quick start
  - see antlr-FAQ/how-to-print-tokens-tree.md
- use Intellij Antlr Preview feature(not supporting {action})
  - This is a very straight forward
  - The .antlr directory contains the generated java target by default(TODO - anyway to change the default target?)
- use VSCode plugin: ANTLR4 grammar syntax support while debug (not support {action})
  - tree will be generated while debug
  - input is configured in .vscode/launch.json
  - see example_debug/example_debug_readme.md for more details, e.g. how to setup  .vscode/launch.json
- use antlr4 java lib
  - see antlr-FQA/how-to-use-antlr4-java-lib-grun.md

