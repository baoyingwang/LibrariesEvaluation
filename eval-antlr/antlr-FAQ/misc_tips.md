# what's the problem - Exception: Could not deserialize ATN with version  (expected 4).
```
$ python app_ExGlobalVar_token_printer.py
Traceback (most recent call last):
  File "C:\Users\baoywang\OneDrive - Microsoft\ws\github\LibrariesEvaluation\eval-antlr\example_global_var\app_ExGlobalVar_token_printer.py", line 8, in <module>
    from ExGlobalVarLexer import ExGlobalVarLexer
  File "C:\Users\baoywang\OneDrive - Microsoft\ws\github\LibrariesEvaluation\eval-antlr\example_global_var/output\ExGlobalVarLexer.py", line 40, in <module>
    class ExGlobalVarLexer(Lexer):
  File "C:\Users\baoywang\OneDrive - Microsoft\ws\github\LibrariesEvaluation\eval-antlr\example_global_var/output\ExGlobalVarLexer.py", line 42, in ExGlobalVarLexer
    atn = ATNDeserializer().deserialize(serializedATN())
          ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
  File "C:\Users\baoywang\AppData\Local\Programs\Python\Python311\Lib\site-packages\antlr4\atn\ATNDeserializer.py", line 28, in deserialize
    self.checkVersion()
  File "C:\Users\baoywang\AppData\Local\Programs\Python\Python311\Lib\site-packages\antlr4\atn\ATNDeserializer.py", line 50, in checkVersion
    raise Exception("Could not deserialize ATN with version " + str(version) + " (expected " + str(SERIALIZED_VERSION) + ").")
Exception: Could not deserialize ATN with version  (expected 4).
```
pls check the lexer python antlr version(e.g. from generated ExGlobalVarLexer.py here 4.9.2), and your application antlr version(e.g. $ pip list | grep antlr, result: antlr4-python3-runtime         4.12.0).
- WARN: the antlr jar version between java and python should match if they are used interchangeably
- e.g. use antlr4j to generate the tokens, then use python lib to print the tokens(app_ExGlobalVar_token_printer.py). See app_ExGlobalVar_parse.sh
- Otherwise, you can meet: "Exception: Could not deserialize ATN with version  (expected 4)."