# in combined g4, use lexer::, paser:: to tell the member location
@lexer::members {
boolean enableDEC = true;
boolean enableACL_NUM = false;
}

# visitor(you have to call visit otherwise no visit) and listener(as callback)
refer : https://github.com/antlr/antlr4/blob/master/doc/listeners.md
refer : https://stackoverflow.com/questions/20714492/antlr4-listeners-and-visitors-which-to-implement
