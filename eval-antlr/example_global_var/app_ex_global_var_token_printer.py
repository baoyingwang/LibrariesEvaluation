import sys
import os
from antlr4 import *
dir_path = os.path.dirname(os.path.realpath(__file__))
#sys.path.insert(0, f"../output")
sys.path.insert(0, f"{dir_path}/output")

from ex_global_var_lexer import ex_global_var_lexer

# parse the g4 to python target firstly, by parsh.sh (antlr4 -Dlanguage=Python3 -o output ex_global_var_combined.g4)

input_text = "access-list 64915 standard 12345"

# Create a CharStream from the input text
input_stream = InputStream(input_text)

ex_global_var_lexer = ex_global_var_lexer(input_stream)

# Iterate over the tokens produced by the lexer
for token in ex_global_var_lexer.getAllTokens():
    # Print the token type and text
    print(f"{ex_global_var_lexer.symbolicNames[token.type]}: {token.text}")
