# Introduction
- this is an example of java action

# how to

prefer: 
1. intellij(to be figured out how to debug) or vscode if no action
2. antlr4j/grun if there is lang action
3. dont use 'antlr4-parse' which has limitation. It is just a wrapper on antlar4j/grun jar files

- option1 - VS code
- option2 - intellij
- option3 - use cmd
  - antlr4-parse ex_global_var_combined.g4 entry -tree
  - OR antlr4-parse ex_global_var_combined.g4 entry -gui
```
FAREAST+baoywang@baoywang-homepc MINGW64 ~/OneDrive - Microsoft/ws/github/LibrariesEvaluation/eval-antlr/example_debug_java.action (master) 20:04 30/04/2023
$ antlr4-parse ex_global_var_combined.g4 entry -tree                                                                                                                        
access-list 51 standard 12345
^Z
line 1:24 mismatched input '12345' expecting DEC
(entry:1 access-list 51 standard 12345)
```
- option4 - antlr4j, grun - this better because the exceptions can be printed on the console. The VS code and intellij surpressed them for some reason.
```
$ tail -5 ~/.bashrc
# https://blog.knoldus.com/testing-grammar-using-antlr4-testrig-grun/
antlr4_complete_jar="C:\Users\baoywang\.vscode\extensions\mike-lischke.vscode-antlr4-2.3.1\antlr\antlr-4.9.2-complete.jar"
#use antlar4j rather than anlt4, because it is already installed here /c/Users/baoywang/AppData/Local/Programs/Python/Python311/Scripts/antlr4
alias antlr4j="java -Xmx500M -cp \"${antlr4_complete_jar}\" org.antlr.v4.Tool"
alias grun="java -cp \"${antlr4_complete_jar}:.\" org.antlr.v4.gui.TestRig"

FAREAST+baoywang@baoywang-homepc MINGW64 ~/OneDrive - Microsoft/ws/github/LibrariesEvaluation/eval-antlr/example_debug_java.action/output (master) 21:28 30/04/2023
$ java -cp "${antlr4_complete_jar};." org.antlr.v4.runtime.misc.TestRig ex_global_var_parser entry -tree
Warning: TestRig moved to org.antlr.v4.gui.TestRig; calling automatically
Can't load ex_global_var_parser as lexer or parser
```
generate and comptile
```
antlr4j ex_global_varLexer.g4 -o output
antlr4j ex_global_varParser.g4 -o output

cd output
javac -cp "${antlr4_complete_jar}" *java

grun ex_global_var entry -tree 
#OR java -cp "${antlr4_complete_jar};." org.antlr.v4.gui.TestRig ex_global_var_parser entry -tree
```

# Trouble Shooting
## Exception in thread "main" java.lang.ClassCastException: class ex_global_var_parser
- what's the reason: The original g4 file definition is not standard(ex_global_var_lexer, ex_global_var_parser).
- how to fix: Changed it to ex_global_varLexer, and ex_global_varParser. The input parameter is changed to grun ex_global_var entry -tree 
  - because the grun will add the 'Lexer', 'Parser' surffix.
  - see https://stackoverflow.com/questions/73195856/antlr-testrig-classcastexception
```
FAREAST+baoywang@baoywang-homepc MINGW64 ~/OneDrive - Microsoft/ws/github/LibrariesEvaluation/eval-antlr/example_debug_java.action/output (master) 21:33 30/04/2023
$ grun ex_global_var_parser entry -tree 
Exception in thread "main" java.lang.ClassCastException: class ex_global_var_parser
        at java.base/java.lang.Class.asSubclass(Unknown Source)
        at org.antlr.v4.gui.TestRig.process(TestRig.java:135)
        at org.antlr.v4.gui.TestRig.main(TestRig.java:119)
```

## null on this._text
https://github.com/antlr/antlr4/blob/master/doc/actions.md

- based on further test, use this.GetText() to get the exact text value(this._text is null) for java, in {action}
  - for python, use self.text
- same problem on others, but no anwser - https://github.com/antlr/antlr4/issues/1946
- author replied, but no exact solution yet - https://github.com/antlr/antlr4/issues/73


ANTLR4 translates such pseudo code into target language code to allow accessing token properties (e.g. in C++ this becomes: INT->getText() == "0").
- from https://stackoverflow.com/questions/56958161/antlr4-how-to-pass-current-tokens-value-to-lexers-predicate

```
ACL_NUM
:
  F_Digit
  {this.enableACL_NUM}?

  F_Digit*
  {
    System.out.println("ACL_NUM _text: " + this._text);
    System.out.println("ACL_NUM _type: " + this._type);
    // https://www.antlr.org/api/Java/org/antlr/v4/runtime/Lexer.html
    //int val = Integer.parseInt(this._text);
    int val = Integer.parseInt($ACL_NUM.text);
    System.out.println("ACL_NUM val: " + val);
    if (1 <= val && val <= 99)
        this._type = this.ACL_NUM_STANDARD;
    else if (100 <= val && val <= 199)

FAREAST+baoywang@baoywang-homepc MINGW64 ~/OneDrive - Microsoft/ws/github/LibrariesEvaluation/eval-antlr/example_debug_java.action/output (master) 21:42 30/04/2023
$ grun ex_global_var entry -tree 
access-list 51 standard 12345
^Z
ACL_NUM _text: null
ACL_NUM _type: 0
Exception in thread "main" java.lang.NumberFormatException: Cannot parse null string
        at java.base/java.lang.Integer.parseInt(Unknown Source)
        at java.base/java.lang.Integer.parseInt(Unknown Source)
        at ex_global_varLexer.ACL_NUM_action(ex_global_varLexer.java:153)
        at ex_global_varLexer.action(ex_global_varLexer.java:120)
        at org.antlr.v4.runtime.atn.LexerCustomAction.execute(LexerCustomAction.java:97)
        at org.antlr.v4.runtime.atn.LexerActionExecutor.execute(LexerActionExecutor.java:168)
        at org.antlr.v4.runtime.atn.LexerATNSimulator.accept(LexerATNSimulator.java:366)
        at org.antlr.v4.runtime.atn.LexerATNSimulator.failOrAccept(LexerATNSimulator.java:299)
        at org.antlr.v4.runtime.atn.LexerATNSimulator.execATN(LexerATNSimulator.java:230)
        at org.antlr.v4.runtime.atn.LexerATNSimulator.match(LexerATNSimulator.java:114)
        at org.antlr.v4.runtime.Lexer.nextToken(Lexer.java:141)
        at org.antlr.v4.runtime.BufferedTokenStream.fetch(BufferedTokenStream.java:169)
        at org.antlr.v4.runtime.BufferedTokenStream.fill(BufferedTokenStream.java:485)
        at org.antlr.v4.gui.TestRig.process(TestRig.java:174)
        at org.antlr.v4.gui.TestRig.process(TestRig.java:158)
        at org.antlr.v4.gui.TestRig.main(TestRig.java:119)

$ java -cp "${antlr4_complete_jar};." org.antlr.v4.gui.TestRig ex_global_var entry -tree ../input.txt
ACL_NUM _text: null
ACL_NUM _type: 0
Exception in thread "main" java.lang.NumberFormatException: Cannot parse null string
        at java.base/java.lang.Integer.parseInt(Unknown Source)
        at java.base/java.lang.Integer.parseInt(Unknown Source)
        at ex_global_varLexer.ACL_NUM_action(ex_global_varLexer.java:153)
        at ex_global_varLexer.action(ex_global_varLexer.java:120)
        at org.antlr.v4.runtime.atn.LexerCustomAction.execute(LexerCustomAction.java:97)
        at org.antlr.v4.runtime.atn.LexerActionExecutor.execute(LexerActionExecutor.java:168)
        at org.antlr.v4.runtime.atn.LexerATNSimulator.accept(LexerATNSimulator.java:366)
        at org.antlr.v4.runtime.atn.LexerATNSimulator.failOrAccept(LexerATNSimulator.java:299)
        at org.antlr.v4.runtime.atn.LexerATNSimulator.execATN(LexerATNSimulator.java:230)
        at org.antlr.v4.runtime.atn.LexerATNSimulator.match(LexerATNSimulator.java:114)
        at org.antlr.v4.runtime.Lexer.nextToken(Lexer.java:141)
        at org.antlr.v4.runtime.BufferedTokenStream.fetch(BufferedTokenStream.java:169)
        at org.antlr.v4.runtime.BufferedTokenStream.fill(BufferedTokenStream.java:485)
        at org.antlr.v4.gui.TestRig.process(TestRig.java:174)
        at org.antlr.v4.gui.TestRig.process(TestRig.java:166)
        at org.antlr.v4.gui.TestRig.main(TestRig.java:119)
```