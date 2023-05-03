source ~/.bashrc

# g4 => java - default target lang is Java
antlr4 -o output ArrayInit.g4

javac -cp "${antlr4_complete_jar}" output/*java *java -d output
