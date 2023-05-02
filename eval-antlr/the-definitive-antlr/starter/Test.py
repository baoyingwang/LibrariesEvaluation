import sys
import os
from antlr4 import *
from antlr4.tree.Trees import Trees
#dir_path = os.path.dirname(os.path.realpath(__file__))
dir_path = "."
#sys.path.insert(0, f"../output")
sys.path.insert(0, f"{dir_path}/output")
sys.path.insert(0, f"{dir_path}/output_py")

from ArrayInitLexer import ArrayInitLexer
from ArrayInitParser import ArrayInitParser

input_stream = InputStream(sys.stdin.readline())
lexer = ArrayInitLexer(input_stream)
tokens = CommonTokenStream(lexer)

parser = ArrayInitParser(tokens)
tree = parser.init() # begin parsing at init rule

# https://stackoverflow.com/questions/21923866/using-antlr-with-python-with-code-migrated-from-java
print(Trees.toStringTree(tree, None, parser))


