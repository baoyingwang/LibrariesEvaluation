# in combined g4, use lexer::, paser:: to tell the member location
@lexer::members {
boolean enableDEC = true;
boolean enableACL_NUM = false;
}

# visitor(you have to call visit otherwise no visit) and listener(as callback on call nodes)
refer : https://github.com/antlr/antlr4/blob/master/doc/listeners.md
refer : https://stackoverflow.com/questions/20714492/antlr4-listeners-and-visitors-which-to-implement
```
The biggest difference between the listener and visitor mechanisms is 
that listener methods are called by the ANTLR-provided walker object, 
whereas visitor methods must walk their children with explicit visit calls. 
Forgetting to invoke visit on a node’s children means 
those subtrees don’t get visited.
-- https://learning.oreilly.com/library/view/the-definitive-antlr/9781941222621/f_0028.xhtml#sec.tour-java
```

# action - attribute references is supported in parser, but not supported in lexer
- for the parser example, see tour/Rows.g4(copied below) of the definitive antlr4
```
// from https://learning.oreilly.com/library/view/the-definitive-antlr/9781941222621/f_0029.xhtml#sec.actions-during-parse
grammar Rows;

@parser::members { // add members to generated RowsParser
    int col;
    public RowsParser(TokenStream input, int col) { // custom constructor
        this(input);
        this.col = col;
    }
}

file: (row NL)+ ;

row
locals [int i=0]
    : (   STUFF
          {
          $i++;
          if ( $i == col ) System.out.println($STUFF.text);
          }
      )+
    ;

TAB  :  '\t' -> skip ;   // match but don't pass to the parser
NL   :  '\r'? '\n' ;     // match and pass to the parser
STUFF:  ~[\t\r\n]+ ;     // match any chars except tab, newline
```
- for the failure with lexer, see below
```
FAREAST+baoywang@baoywang-homepc MINGW64 ~/OneDrive - Microsoft/ws/github/LibrariesEvaluation/eval-antlr/example_global_var (master) 17:05 03/05/2023
$ sh app_ExGlobalVar_token_printer.sh 
error(128): ExGlobalVarLexer.g4:48:15: attribute references not allowed in lexer actions: $F_Digit.text
ACCESS_LIST: access-list
ACL_NUM_OTHER: 64915
STANDARD: standard
DEC: 12345

ACL_NUM
:
  F_Digit
  {self.enableACL_NUM}?

  F_Digit*
  {
    val = int($F_Digit.text)
    if (1 <= val <= 99):
        self.type = self.ACL_NUM_STANDARD
    elif (100 <= val <= 199):
        self.type = self.ACL_NUM_EXTENDED
    else:
        self.type = self.ACL_NUM_OTHER
    self.enableDEC = True
    self.enableACL_NUM = False
  }
;
```

# mode - refer "Island Grammars: Dealing with Different Formats in the Same File" - chapter 4 "the definitive antlr4" 2nd
- refer: chapter 12 https://learning.oreilly.com/library/view/the-definitive-antlr/9781941222621/f_0076.xhtml
  - below examples are good, and pls read the g4 file, and the related pages in book
  - example [chapter 4 tour/XMLLexer.g4 - github](https://github.com/jszheng/py3antlr4book/blob/master/04-modechange/XMLLexer.g4)
  - example [chapter 12 lexmagic/ModeTagsLexer.g4- github](https://github.com/jszheng/py3antlr4book/blob/master/12-sea_of_text/ModeTagsLexer.g4) - switch by mode(M)
  - example [lexmagic/XMLLexer.g4- media.pragprog.com](http://media.pragprog.com/titles/tpantlr2/code/lexmagic/XMLLexer.g4) - switch by popMode(M)
    - "Unlike the ModeTagsLexer grammar that used just the mode command, we’re using pushMode (and popMode in a moment). By pushing the mode, the lexer can pop the mode to return to the “invoking” mode. This is useful for nested mode switches, though we’re not doing that here."

- TODO: Which one to be used - mode(M), or pushMode(M) to enter a mode? mode(M), popMode to leave current mode?
- Lexer are split into multi sections based on mode. All are in default mode, if no any additional mode is defined
- A stack is used to tell the mode usage
    - Top of stack - current mode
    - Use pushMode(M), mode(M) to switch to another mode
        - pushMode(M)
          - This is the same as mode except that it pushes the current mode onto a stack as well as setting the mode M. It should be used in conjunction with popMode.From <https://learning.oreilly.com/library/view/the-definitive-antlr/9781941222621/f_0098.xhtml> 
        - mode(M) : Switching modes with mode changes the current stack top. 
          - After matching this token, switch the lexer to mode M. The next time the lexer tries to match a token, it will look only at rules in mode M. M can be a mode name from the same grammar or an integer literal. See grammar Strings earlier. From <https://learning.oreilly.com/library/view/the-definitive-antlr/9781941222621/f_0098.xhtml> 


# "Rewriting the Input Stream" - chapter 4 "the definitive antlr4" 2nd
- tour/InsertSerialIDListener.java
- "TokenStreamRewriter is a powerful and extremely efficient means of manipulating a token stream."

# skip and channel hidden
- channel(HIDDEN) can be processed later, but skip is discarded completely
    - see below "for channel(HIDDEN), how can I access them later?"
- how chatgpt explains it
  - In antlr4 lexer, what does 'channel ( HIDDEN )' mean?
  - so, looks like similar with "-> skip"
  - for channel(HIDDEN), how can I access them later?
```
In antlr4 lexer, what does 'channel ( HIDDEN )' mean
ChatGPT
In ANTLR4, the channel(HIDDEN) lexer rule attribute is used to specify that certain tokens should be skipped by the parser and not included in the parse tree.
When you specify channel(HIDDEN) in a lexer rule, it tells ANTLR4 to assign the matching tokens to a special channel called the "hidden channel". This channel is separate from the default channel that is used for regular tokens.
By default, the parser ignores all tokens on the hidden channel and does not include them in the parse tree. This can be useful for ignoring whitespace, comments, or other tokens that are not relevant to the meaning of the input.
For example, suppose you have a lexer rule for whitespace:

cssCopy code
WS : [ \t\r\n]+ -> channel(HIDDEN) ;
This rule matches any sequence of whitespace characters and assigns the matching tokens to the hidden channel. The -> channel(HIDDEN) syntax specifies that the matched tokens should be sent to the hidden channel rather than the default channel.
When the parser encounters whitespace in the input, it will ignore the whitespace tokens on the hidden channel and continue parsing with the remaining tokens on the default channel. This can simplify the parser logic and make the parse tree more compact.
In summary, the channel(HIDDEN) lexer rule attribute is used to specify that certain tokens should be skipped by the parser and not included in the parse tree. This can be useful for ignoring whitespace, comments, or other tokens that are not relevant to the meaning of the input.

so, looks like similar with "-> skip"
ChatGPT
Yes, the channel(HIDDEN) lexer rule attribute is similar in functionality to the -> skip lexer rule action.
When you use -> skip in a lexer rule, it tells ANTLR4 to skip the matching tokens and not create any tokens for them. This is similar to assigning the matching tokens to the hidden channel, because both techniques result in the tokens being skipped by the parser and not included in the parse tree.
The main difference between channel(HIDDEN) and -> skip is that channel(HIDDEN) allows you to assign the matching tokens to a separate channel, whereas -> skip simply discards the matching tokens without assigning them to any channel.
In general, you should use channel(HIDDEN) when you want to skip tokens that may be relevant to other parts of your application, such as code generators or syntax highlighters. By assigning the tokens to the hidden channel, you can still access them later if necessary. On the other hand, you should use -> skip when you want to completely discard the matching tokens and never use them again.

for channel(HIDDEN), how can I access them later?
ChatGPT

To access the tokens that have been assigned to the hidden channel in ANTLR4, you can use a custom listener or visitor that listens to all token events, including those on the hidden channel.
The TokenStream interface in ANTLR4 provides a method called getTokenSource(), which returns the TokenSource object used to generate the tokens. You can use the TokenSource object to retrieve all tokens from the input, including those on the hidden channel.
Here's an example of how you can access the tokens on the hidden channel in a custom listener:

javaCopy code
public class MyListener extends MyGrammarBaseListener
{ 
    @OverridepublicvoidvisitTerminal(TerminalNode node)
    { 
        Tokentoken=node.getSymbol(); 
        if(token.getChannel() == Lexer.HIDDEN) 
        { 
            // Do something with the hidden token
        } 
        // Process regular tokens as usual} 
    }
In this example, the visitTerminal() method is called for every terminal node in the parse tree, including those on the hidden channel. We can retrieve the Token object associated with the terminal node using the getSymbol() method. If the token was assigned to the hidden channel, we can check its channel number using the getChannel() method. If the channel number matches Lexer.HIDDEN, then we know that the token was on the hidden channel.
You can modify this example to perform any custom processing you need on the hidden tokens. Note that if you are using multiple channels in your lexer, you may need to modify the if statement to match the channel number of your hidden channel.

From <https://chat.openai.com/c/3d601309-43f9-4ee6-98cf-4cd452602811> 
```

# some tips
From <https://www.cnblogs.com/rainforwind/articles/15399735.html> 
- more, mode, 属性, returns，type，channel 等语法参考官方文档，因为文档写的比较绕，所以补充几句。
	-  more 用在词法定义中，代表本条规则不规约，将其匹配到的内容合并到后续执行中第一个非more且非skip的规则结果中。
	- mode、pushMode、PopMode 用在词法定义中，代表模式切换（缺省为DEFAULT_MODE）
	-  语法规则的头部（冒号之前的部分）不止可以有规则名称，还可以声明属性、返回值(returns)、局部变量(locals)，属性和返回值可以在其他规则引用该规则是使用。
	-  词法分析中，type可以声明实际规约到哪个token，在一个token有多种case的情况下，就不需要用或的关系来一次性写全，而是可以随时补充。
	-  channel，将分析结果发送到其他通道，可以处理注释等内容。
