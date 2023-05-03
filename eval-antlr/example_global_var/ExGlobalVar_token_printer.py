from antlr4 import *
from ExGlobalVarLexer import ExGlobalVarLexer

# parse the g4 to python target firstly, by parsh.sh (antlr4 -Dlanguage=Python3 -o output ex_global_var_combined.g4)

input_text = "access-list 64915 standard 12345"

# Create a CharStream from the input text
input_stream = InputStream(input_text)

exGlobalVarLexer = ExGlobalVarLexer(input_stream)

# Iterate over the tokens produced by the lexer
for token in exGlobalVarLexer.getAllTokens():
    # Print the token type and text
    print(f"{exGlobalVarLexer.symbolicNames[token.type]}: {token.text}")
