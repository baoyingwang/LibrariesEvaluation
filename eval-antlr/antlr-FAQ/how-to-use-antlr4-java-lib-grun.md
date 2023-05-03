# Introduction
Antlr4 is based on java. We can use the related lib directly with jdk

- antlr4 Data.g4 -o output
- javac -d output -cp "${antlr4_complete_jar};.;./output" output/*java *java
- grun  Data file -tree < t.data

# how to setup
I setup below alias
```
# https://blog.knoldus.com/testing-grammar-using-antlr4-testrig-grun/
antlr4_complete_jar="C:\Users\baoywang\OneDrive - Microsoft\ws\env\antlr\antlr-4.12.0-complete.jar"
#use antlar4j rather than anlt4, because it is already installed here /c/Users/baoywang/AppData/Local/Programs/Python/Python311/Scripts/antlr4
alias antlr4j="java -Xmx500M -cp \"${antlr4_complete_jar};.;./.antlr;./output\" org.antlr.v4.Tool"
alias grun="java -cp \"${antlr4_complete_jar};.;./.antlr;./output\" org.antlr.v4.gui.TestRig"
```

# how to use 
# compile the g4 to target language for later program
- specify the target language(default Java): -Dlanguage=Python3
  - refer: https://stackoverflow.com/questions/73070369/how-do-i-install-antlr4-for-python3-on-windows

```
$ antlr4j
ANTLR Parser Generator  Version 4.9.2
 -o ___              specify output directory where all output is generated
 -lib ___            specify location of grammars, tokens files
 -atn                generate rule augmented transition network diagrams
 -encoding ___       specify grammar file encoding; e.g., euc-jp
 -message-format ___ specify output style for messages in antlr, gnu, vs2005
 -long-messages      show exception details when available for errors and warnings
 -listener           generate parse tree listener (default)
 -no-listener        don't generate parse tree listener
 -visitor            generate parse tree visitor
 -no-visitor         don't generate parse tree visitor (default)
 -package ___        specify a package/namespace for the generated code
 -depend             generate file dependencies
 -D<option>=value    set/override a grammar-level option
 -Werror             treat warnings as errors
 -XdbgST             launch StringTemplate visualizer on generated code
 -XdbgSTWait         wait for STViz to close before continuing
 -Xforce-atn         use the ATN simulator for all predictions
 -Xlog               dump lots of logging info to antlr-timestamp.log
 -Xexact-output-dir  all output goes into -o dir regardless of paths/package

e.g. default target as Java
cd example_java.action
antlr4j "ExJavaActionLexer.g4"  -o "output"
antlr4j "ExJavaActionParser.g4" -o "output"

e.g. 
cd example_global_var
antlr4j "ExGlobalVarLexer.g4"  -o "output" -Dlanguage=Python3 
```

## generate token/tree/GUI
```
$ grun
java org.antlr.v4.gui.TestRig GrammarName startRuleName
  [-tokens] [-tree] [-gui] [-ps file.ps] [-encoding encodingname]
  [-trace] [-diagnostics] [-SLL]
  [input-filename(s)]
Use startRuleName='tokens' if GrammarName is a lexer grammar.
Omitting input-filename makes rig read from stdin.

e.g. grun ExJavaAction entry -tokens "input.txt"
```

