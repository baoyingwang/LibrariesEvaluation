java  -cp "${antlr4_complete_jar};.;output" Test
#e.g.
#$ java  -cp "${antlr4_complete_jar};.;output" Test
#{1,{2,3},4}
#^Z
#(init { (value 1) , (value (init { (value 2) , (value 3) })) , (value 4) })


java  -cp "${antlr4_complete_jar};.;output" Translate
#e.g.
#$ java  -cp "${antlr4_complete_jar};.;output" Translate
#{1,2,3}
#^Z
#"\u0001\u0002\u0003"