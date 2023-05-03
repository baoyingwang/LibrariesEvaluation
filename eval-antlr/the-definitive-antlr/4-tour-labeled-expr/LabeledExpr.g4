grammar LabeledExpr; // rename to distinguish from Expr.g4

prog:   stat+ ;

stat:   expr NEWLINE                # printExpr
    |   ID '=' expr NEWLINE         # assign // baoywang: cache the value for later use by visitor
    |   NEWLINE                     # blank
    ;

expr:   expr op=('*'|'/') expr      # MulDiv //baoywang: this is before the +/-, to ensure the priority
    |   expr op=('+'|'-') expr      # AddSub
    |   INT                         # int
    |   ID                          # id  // baoywang: get the value from cache (populated by the # assign visit)
    |   '(' expr ')'                # parens
    ;

MUL :   '*' ; // assigns token name to '*' used above in grammar
DIV :   '/' ;
ADD :   '+' ;
SUB :   '-' ;
ID  :   [a-zA-Z]+ ;      // match identifiers
INT :   [0-9]+ ;         // match integers
NEWLINE:'\r'? '\n' ;     // return newlines to parser (is end-statement signal)
WS  :   [ \t]+ -> skip ; // toss out whitespace