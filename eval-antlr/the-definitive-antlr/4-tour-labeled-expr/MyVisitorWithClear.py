__author__ = 'jszheng' # add 'clear' based on https://github.com/jszheng/py3antlr4book/blob/master/04-Calc/MyVisitor.py

from LabeledExprWithClearVisitor import LabeledExprWithClearVisitor
from LabeledExprWithClearParser import LabeledExprWithClearParser


class MyVisitorWithClear(LabeledExprWithClearVisitor):
    def __init__(self):
        self.memory = {}

    def visitAssign(self, ctx):
        name = ctx.ID().getText()
        value = self.visit(ctx.expr())
        self.memory[name] = value
        return value

    def visitPrintExpr(self, ctx):
        value = self.visit(ctx.expr())
        print(value)
        return 0

    def visitInt(self, ctx):
        return ctx.INT().getText()

    def visitId(self, ctx):
        name = ctx.ID().getText()
        if name in self.memory:
            return self.memory[name]
        return 0

    def visitMulDiv(self, ctx):
        left = int(self.visit(ctx.expr(0)))
        right = int(self.visit(ctx.expr(1)))
        if ctx.op.type == LabeledExprWithClearParser.MUL:
            return left * right
        return left / right

    def visitAddSub(self, ctx):
        left = int(self.visit(ctx.expr(0)))
        right = int(self.visit(ctx.expr(1)))
        if ctx.op.type == LabeledExprWithClearParser.ADD:
            return left + right
        return left - right

    def visitParens(self, ctx):
        return self.visit(ctx.expr())

    def visitClear(self, ctx):
        self.memory = {}
        return 0