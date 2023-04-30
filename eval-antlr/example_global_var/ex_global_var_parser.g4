// this parser g4 file is NOT required for this example.
// add it here just for completeness
parser grammar ex_global_var_parser;

options {
  tokenVocab = ex_global_var_lexer;
}

entry :
  ACCESS_LIST
;