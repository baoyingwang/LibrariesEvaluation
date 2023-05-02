// this parser g4 file is NOT required for this example.
// add it here just for completeness
parser grammar ExGlobalVarParser;

options {
  tokenVocab = ExGlobalVarLexer;
}

entry :
  ACCESS_LIST DEC STANDARD DEC
;