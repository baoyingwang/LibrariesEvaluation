package baoying.eval.jdk.assembly;

//print assembly, by
//     java -cp . -XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly -Xcomp -XX:CompileCommand=dontinline,basic/SimpleSum.* -XX:CompileCommand=compileonly,basic/SimpleSum.* basic.SimpleSum
//print bytecode, by:
//     javap -verbose basic.SimpleSum
public class SimpleSum_for_PrintAssembly {

	public static int addTwo(int a, int b){
		return a + b;
	}
	
	public static int addThree(int a, int b, int c){
		return a + b + c;
	}
	
	public static void main(String[] args){
		
		int a = 1;
		int b = 2;
		addTwo(a, b);
		
		int x = 10;
		int y =20;
		int z= 30;
		addThree(x, y, z);
	}

}
