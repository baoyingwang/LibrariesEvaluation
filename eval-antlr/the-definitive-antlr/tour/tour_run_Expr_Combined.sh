source ~/.bashrc

# g4 => java - default target lang is Java
antlr4 Expr.g4 -o output

javac -cp "${antlr4_complete_jar}" output/Expr*java ExprJoyRide.java -d output
#javac -cp "${antlr4_complete_jar}" output/*java -d output

#skip grun gui to avoid blocking
#grun Expr prog -gui t.expr # launches

java -cp "${antlr4_complete_jar};.;./.antlr;./output" ExprJoyRide t.expr