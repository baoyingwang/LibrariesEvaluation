# Introduction
- This is a lexer exepriment, to verify the global env usage.
- For this input: access-list 64915 standard 12345
  - 64915 is in the middle of 'access-list' and 'standard'
  - based on lexer, that means 
    - it will be parsed with EnableACL_NUM = True, EnableDEC = False
    - it will be parsed as ACL_NUM, not DEC
  - 12345 appears after 'standard', where DEC is already enabled.
    - it will be parsed as DEC
- We can see the output by run this script:
```
$ bash eval-antlr/example_global_var/app_ex_global_var_token_parse.sh 
ACCESS_LIST: access-list
ACL_NUM_OTHER: 64915
STANDARD: standard
DEC: 12345
```
- the magic is the semantic {«p»}?
  - Do not continue parsing past a predicate if «p» evaluates to false at runtime.
  - see 
    - https://github.com/antlr/antlr4/blob/master/doc/lexer-rules.md
    - https://github.com/antlr/antlr4/blob/master/doc/predicates.md
```
DEC
:
  F_Digit
  {self.enableDEC}?

  F_Digit*
;
```