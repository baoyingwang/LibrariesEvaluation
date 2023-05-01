lexer grammar ExJavaActionLexer;

@members {
boolean enableDEC = true;
boolean enableACL_NUM = false;
}

tokens {
  ACL_NUM_EXTENDED,
  ACL_NUM_OTHER,
  ACL_NUM_STANDARD
}

// lexer
ACCESS_LIST
:
  'access-list'
  {
    this.enableACL_NUM = true;
    this.enableDEC = false;
  }

;

STANDARD
:
  'standard'
  { 
    //System.out.println("ACL_NUM _type: " + this._type);
    this.enableDEC = true;
    this.enableACL_NUM = false;
    //System.out.println("ACL_NUM _type: " + this._type);
  }

;

ACL_NUM
:
  F_Digit
  {this.enableACL_NUM}?

  F_Digit*
  {
    //System.out.println("ACL_NUM _text: " + this._text); //null
    //System.out.println("ACL_NUM _type: " + this._type); //0
    //System.out.println("ACL_NUM getText(): " + this.getText()); //51
    // https://www.antlr.org/api/Java/org/antlr/v4/runtime/Lexer.html

    int val = Integer.parseInt(this.getText());
    if (1 <= val && val <= 99)
        this._type = this.ACL_NUM_STANDARD;
    else if (100 <= val && val <= 199)
        this._type = this.ACL_NUM_EXTENDED;
    else
        this._type = this.ACL_NUM_OTHER;
    
    this.enableDEC = true;
    this.enableACL_NUM = false;
    //System.out.println("ACL_NUM enableDEC: " + this.enableDEC);
    //System.out.println("ACL_NUM enableACL_NUM: " + this.enableACL_NUM);
  }
;

DEC
:
  F_Digit
  {this.enableDEC}?

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
