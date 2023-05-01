antlr4_complete_jar="C:\Users\baoywang\.vscode\extensions\mike-lischke.vscode-antlr4-2.3.1\antlr\antlr-4.9.2-complete.jar"
MYSCRIPTDIR=$(dirname $0)
# convert to an absolute path
MYDIR=$(cd $MYSCRIPTDIR ; /bin/pwd)
cd "$MYDIR"

outputDirName=".antlr"
if [[ -z "${outputDirName}" ]]; then
    echo "ERROR: outputDirName is empty. Normally use default value: .antlr"
    return
fi
pwd
rm ${outputDirName}/*

# TODO for some reason, the alias: antlr4j, and grun does not within this shell. It works when executing from git-bash directly
#antlr4j "ExJavaActionLexer.g4"  -o "${outputDirName}"
#antlr4j "ExJavaActionParser.g4" -o "${outputDirName}"
java -Xmx500M -cp "${antlr4_complete_jar};." org.antlr.v4.Tool "ExJavaActionLexer.g4"  -o "${outputDirName}"
java -Xmx500M -cp "${antlr4_complete_jar};." org.antlr.v4.Tool "ExJavaActionParser.g4" -o "${outputDirName}"
echo "build g4 to java done"

javac -cp "${antlr4_complete_jar}" "${outputDirName}/*java"
echo "build java done"

echo "tree:"
#grun ExJavaAction entry -tree "input.txt"
java -cp "${antlr4_complete_jar};.;./.antlr" org.antlr.v4.gui.TestRig ExJavaAction entry -tree "input.txt"

echo "tokens:"
#grun ExJavaAction entry -tokens "input.txt"
java -cp "${antlr4_complete_jar};.;./.antlr" org.antlr.v4.gui.TestRig ExJavaAction entry -tokens "input.txt"