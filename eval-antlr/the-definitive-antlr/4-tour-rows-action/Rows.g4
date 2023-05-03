grammar Rows;

@parser::members { // add members to generated RowsParser
    //baoywang: this is java grammar. That means if you hope other lang, you hope to change this g4, e.g. https://github.com/jszheng/py3antlr4book/blob/master/04-Rows-visitor/Rows.g4
    int col;
    public RowsParser(TokenStream input, int col) { // custom constructor
        this(input);
        this.col = col;
    }
}

file: (row NL)+ ;

// baoywang: refer https://github.com/antlr/antlr4/blob/master/doc/actions.md
// baoywang: lexer action does not support attribute, e.g. self.txt is good for lexer, but $STUFF.exe will fail(attribute references not allowed in lexer actions: $F_Digit.text)
row
locals [int i=0]
    : (   STUFF
          {
          $i++;
          if ( $i == col ) System.out.println($STUFF.text);
          }
      )+
    ;

TAB  :  '\t' -> skip ;   // match but don't pass to the parser
NL   :  '\r'? '\n' ;     // match and pass to the parser
STUFF:  ~[\t\r\n]+ ;     // match any chars except tab, newline