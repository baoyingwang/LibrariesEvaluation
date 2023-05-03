# compile
antlr4 Rows.g4 -o output
javac -d output -cp "${antlr4_complete_jar};.;./output" output/*java *java

# run
java -cp "${antlr4_complete_jar};.;./output" Col 1 < t.rows
java -cp "${antlr4_complete_jar};.;./output" Col 2 < t.rows 