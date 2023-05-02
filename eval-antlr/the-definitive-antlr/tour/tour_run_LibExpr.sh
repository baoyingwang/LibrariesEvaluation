source ~/.bashrc

# g4 => java - default target lang is Java
antlr4 LibExpr.g4 -o output # automatically pulls in CommonLexerRules.g4

javac -cp "${antlr4_complete_jar}" output/LibExpr*java -d output
#javac -cp "${antlr4_complete_jar}" output/*java -d output

#skip grun gui to avoid blocking
#grun LibExpr prog -tree < t.expr
#grun LibExpr prog -gui < t.expr

java -cp "${antlr4_complete_jar};.;./.antlr;./output" ExprJoyRide t.expr