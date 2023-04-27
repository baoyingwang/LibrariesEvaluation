MYSCRIPTDIR=$(dirname $0)
# convert to an absolute path
MYDIR=$(cd $MYSCRIPTDIR ; /bin/pwd)
cd "$MYDIR"

# step 1 - define the g4 file - ex_global_var_lexer.g4

# step 2 - generate the lexer - https://github.com/antlr/antlr4/blob/master/doc/python-target.md
antlr4 -Dlanguage=Python3 -o output ex_global_var_lexer.g4

# step 3 - print the tokens, by parsing the input 
python app_ex_global_var_token_printer.py

# reference
#https://github.com/antlr/antlr4/blob/master/doc/getting-started.md

# https://github.com/antlr/antlr4/blob/master/doc/csharp-target.md
#antlr4 -Dlanguage=CSharp ex_global_var_lexer.g4