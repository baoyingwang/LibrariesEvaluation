MYSCRIPTDIR=$(dirname $0)
# convert to an absolute path
MYDIR=$(cd $MYSCRIPTDIR ; /bin/pwd)
cd "$MYDIR"

# step 1 - define the g4 file - ExGlobalVarLexer.g4

# step 2 - generate the lexer - https://github.com/antlr/antlr4/blob/master/doc/python-target.md
antlr4 -Dlanguage=Python3 -o output ExGlobalVarLexer.g4
#OR antlr4j "ExGlobalVarLexer.g4"  -o "output" -Dlanguage=Python3 #the jar version should match with python wrapped version. pls check from the output lexer, e.g. # Generated from ExGlobalVarLexer.g4 by ANTLR 4.12.0

# step 3 - print the tokens, by parsing the input 
python ExGlobalVar_token_printer.py

# reference
#https://github.com/antlr/antlr4/blob/master/doc/getting-started.md

# https://github.com/antlr/antlr4/blob/master/doc/csharp-target.md
#antlr4 -Dlanguage=CSharp ExGlobalVarLexer.g4