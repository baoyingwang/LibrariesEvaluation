from antlr4 import *
from ArrayInitListener import ArrayInitListener
from ArrayInitParser import ArrayInitParser

#/** Convert short array inits like {1,2,3} to "\u0001\u0002\u0003" */
class ShortToUnicodeString(ArrayInitListener):
    #/** Translate { to " */
    def enterInit(self, ctx):
        print('"');

    #/** Translate } to " */
    def exitInit(self, ctx):
        print('"')

    #/** Translate integers to 4-digit hexadecimal strings prefixed with \\u */
    def enterValue(self, ctx):
        #// Assumes no nested array initializers
        num = int(ctx.INT().getText())
        print( '\\u' + hex(num)[2:].zfill(4))
