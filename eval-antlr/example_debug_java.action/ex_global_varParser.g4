parser grammar ex_global_varParser;

options {
  tokenVocab = ex_global_varLexer;
}

entry :
  ACCESS_LIST (ACL_NUM_EXTENDED|ACL_NUM_OTHER|ACL_NUM_STANDARD) STANDARD DEC
;
