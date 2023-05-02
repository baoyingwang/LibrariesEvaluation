lexer grammar ExGlobalVarLexer;

@members {
enableDEC = True
enableACL_NUM = False
}

tokens {
  ACL_NUM_EXTENDED,
  ACL_NUM_OTHER,
  ACL_NUM_STANDARD
}

// The antlr4-parser does not support to split parser and lexer into diff files
//  - Exception in thread "main" java.lang.IllegalStateException: A lexer interpreter can only be created for a lexer or combined grammar.
// e.g.
//options {
//  tokenVocab = ex_global_var_lexer;
//}

ACCESS_LIST
:
  'access-list'
  {
    self.enableACL_NUM = True
    self.enableDEC = False
  }

;

STANDARD
:
  'standard'
  { 
    self.enableDEC = True
    self.enableACL_NUM = False
  }

;

ACL_NUM
:
  F_Digit
  {self.enableACL_NUM}?

  F_Digit*
  {
    val = int(self.text)
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

DEC
:
  F_Digit
  {self.enableDEC}?

  F_Digit*
;

fragment
F_Digit
:
  '0' .. '9'
;

WS
:
  [ \t\r\n]+ -> skip 
;
