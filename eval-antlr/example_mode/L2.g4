lexer grammar L;
AND : '&' -> mode(STR);
mode STR;
MASK : '&' -> popMode;
