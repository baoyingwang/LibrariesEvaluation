grammar ex_global_var_combined;
// readme:
// this is a VS code debug example, Java action has to be used because the VS Extension use java to debug

@lexer::members {

boolean enableDEC = true;
boolean enableACL_NUM = false;

}

tokens {
  ACL_NUM_EXTENDED,
  ACL_NUM_OTHER,
  ACL_NUM_STANDARD
}

entry :
  ACCESS_LIST ACL_NUM STANDARD DEC
;

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
    System.out.println("ACL_NUM _type: " + this._type);
    this.enableDEC = true;
    this.enableACL_NUM = false;
    System.out.println("ACL_NUM _type: " + this._type);
  }

;

ACL_NUM
:
  F_Digit
  {this.enableACL_NUM}?

  F_Digit*
  {
    System.out.println("ACL_NUM _text: " + this._text);
    System.out.println("ACL_NUM _type: " + this._type);
    // https://www.antlr.org/api/Java/org/antlr/v4/runtime/Lexer.html
    int val = Integer.parseInt(this._text);
    System.out.println("ACL_NUM val: " + val);
    if (1 <= val && val <= 99)
        this._type = this.ACL_NUM_STANDARD;
    else if (100 <= val && val <= 199)
        this._type = this.ACL_NUM_EXTENDED;
    else
        this._type = this.ACL_NUM_OTHER;
    System.out.println("ACL_NUM _type: " + this._type);
    this.enableDEC = true;
    this.enableACL_NUM = false;
    System.out.println("ACL_NUM enableDEC: " + this.enableDEC);
    System.out.println("ACL_NUM enableACL_NUM: " + this.enableACL_NUM);
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
