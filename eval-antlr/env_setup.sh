############ install python related ####################
#refer <https://github.com/antlr/antlr4/blob/master/doc/getting-started.md> 
pip install antlr4-tools           #install python cmd/binary
pip install antlr4-python3-runtime #install python lib

# setup antlr4 jar file - in case you hope use it directly. It is not required to do this if you just use the python cmd and lib
# refer: https://www.antlr.org/download.html
# refer: https://blog.knoldus.com/testing-grammar-using-antlr4-testrig-grun/
antlr4_complete_jar="C:\OneDrive\ws\env\antlr\antlr-4.12.0-complete.jar"
if [[ ! -f "${antlr4_complete_jar}" ]]; then
    echo "ERROR: antlr4_complete_jar not found: ${antlr4_complete_jar}. Pls download and setup accordingly"
    return
fi
#use antlar4j rather than anlt4, because it is already installed here /c/Users/baoywang/AppData/Local/Programs/Python/Python311/Scripts/antlr4
#alias antlr4j="java -Xmx500M -cp \"${antlr4_complete_jar};.;./.antlr\" org.antlr.v4.Tool"
#alias grun="java -cp \"${antlr4_complete_jar};.;./.antlr\" org.antlr.v4.gui.TestRig"

