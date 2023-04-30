antlr4_complete_jar="C:\Users\baoywang\.vscode\extensions\mike-lischke.vscode-antlr4-2.3.1\antlr\antlr-4.9.2-complete.jar"
MYSCRIPTDIR=$(dirname $0)
# convert to an absolute path
MYDIR=$(cd $MYSCRIPTDIR ; /bin/pwd)
cd "$MYDIR"

outputDir=".antlr"
if [[ -z "${outputDir}" ]]; then
    echo "ERROR: outputDir is empty, use default value: .antlr"
    return
fi

rm ${outputDir}/*
cd ${outputDir}
java -Xmx500M -cp "${antlr4_complete_jar};." org.antlr.v4.Tool ../ex_global_varLexer.g4  
java -Xmx500M -cp "${antlr4_complete_jar};." org.antlr.v4.Tool ../ex_global_varParser.g4 
echo "build g4 to java done"

javac -cp "${antlr4_complete_jar}" *java
echo "build java done"

java -cp "${antlr4_complete_jar};." org.antlr.v4.gui.TestRig ex_global_var entry -tree "$MYDIR/input.txt"