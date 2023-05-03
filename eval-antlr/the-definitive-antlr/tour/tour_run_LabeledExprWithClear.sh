source ~/.bashrc

antlr4 LabeledExprWithClear.g4 -Dlanguage=Python3 -o output_py -no-listener -visitor

python calcWithClear