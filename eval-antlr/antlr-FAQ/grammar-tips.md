# in combined g4, use lexer::, paser:: to tell the member location
@lexer::members {
boolean enableDEC = true;
boolean enableACL_NUM = false;
}

# visitor(you have to call visit otherwise no visit) and listener(as callback on call nodes)
refer : https://github.com/antlr/antlr4/blob/master/doc/listeners.md
refer : https://stackoverflow.com/questions/20714492/antlr4-listeners-and-visitors-which-to-implement
```
The biggest difference between the listener and visitor mechanisms is 
that listener methods are called by the ANTLR-provided walker object, 
whereas visitor methods must walk their children with explicit visit calls. 
Forgetting to invoke visit on a node’s children means 
those subtrees don’t get visited.
-- https://learning.oreilly.com/library/view/the-definitive-antlr/9781941222621/f_0028.xhtml#sec.tour-java
```

# action - attribute references is supported in parser, but not supported in lexer
- for the parser example, see tour/Rows.g4(copied below) of the definitive antlr4
```
// from https://learning.oreilly.com/library/view/the-definitive-antlr/9781941222621/f_0029.xhtml#sec.actions-during-parse
grammar Rows;

@parser::members { // add members to generated RowsParser
    int col;
    public RowsParser(TokenStream input, int col) { // custom constructor
        this(input);
        this.col = col;
    }
}

file: (row NL)+ ;

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
```
- for the failure with lexer, see below
```
FAREAST+baoywang@baoywang-homepc MINGW64 ~/OneDrive - Microsoft/ws/github/LibrariesEvaluation/eval-antlr/example_global_var (master) 17:05 03/05/2023
$ sh app_ExGlobalVar_token_printer.sh 
error(128): ExGlobalVarLexer.g4:48:15: attribute references not allowed in lexer actions: $F_Digit.text
ACCESS_LIST: access-list
ACL_NUM_OTHER: 64915
STANDARD: standard
DEC: 12345

ACL_NUM
:
  F_Digit
  {self.enableACL_NUM}?

  F_Digit*
  {
    val = int($F_Digit.text)
    if (1 <= val <= 99):
        self.type = self.ACL_NUM_STANDARD
    elif (100 <= val <= 199):
        self.type = self.ACL_NUM_EXTENDED
    else:
        self.type = self.ACL_NUM_OTHER
    self.enableDEC = True
    self.enableACL_NUM = False
  }
;
```