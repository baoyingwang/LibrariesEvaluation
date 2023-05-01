parser grammar ExJavaActionParser;

options {
  tokenVocab = ExJavaActionLexer;
}

entry :
  ACCESS_LIST (ACL_NUM_EXTENDED|ACL_NUM_OTHER|ACL_NUM_STANDARD) STANDARD DEC
;
