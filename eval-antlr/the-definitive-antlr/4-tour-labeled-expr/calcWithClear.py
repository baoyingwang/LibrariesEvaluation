__author__ = 'jszheng' # this is based on https://github.com/jszheng/py3antlr4book/blob/master/04-Calc/calc.py, adding clear functionality

import sys
from antlr4 import *
from antlr4.InputStream import InputStream
from LabeledExprWithClearLexer import LabeledExprWithClearLexer
from LabeledExprWithClearParser import LabeledExprWithClearParser
from MyVisitorWithClear import MyVisitorWithClear

if __name__ == '__main__':
    if len(sys.argv) > 1:
        input_stream = FileStream(sys.argv[1])
    else:
        input_stream = InputStream(sys.stdin.readline())

    lexer = LabeledExprWithClearLexer(input_stream)
    token_stream = CommonTokenStream(lexer)
    parser = LabeledExprWithClearParser(token_stream)
    tree = parser.prog()

    #lisp_tree_str = tree.toStringTree(recog=parser)
    #print(lisp_tree_str)

    visitor = MyVisitorWithClear()
    visitor.visit(tree)
