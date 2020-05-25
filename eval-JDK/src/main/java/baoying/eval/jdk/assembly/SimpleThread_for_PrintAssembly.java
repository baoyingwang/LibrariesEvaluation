package baoying.eval.jdk.assembly;

//print assembly
//C:\baoying.wang\program\jdk1.8.0_131_x86\bin\java -cp . -XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly -Xcomp -XX:CompileCommand=dontinline,basic/SimpleThreadForAssembly.* -XX:CompileCommand=compileonly,basic/SimpleThreadForAssembly.* basic.SimpleThreadForAssembly > SimpleThreadForAssembly.assembly.only.thisClass.txt
//C:\baoying.wang\program\jdk1.8.0_131_x86\bin\java -cp . -XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly -Xcomp -XX:CompileCommand=dontinline,basic/SimpleThreadForAssembly.* -XX:CompileCommand=compileonly,basic/SimpleThreadForAssembly.* -XX:CompileCommand=dontinline,java/lang/Thread.* -XX:CompileCommand=compileonly,java/lang/Thread.* basic.SimpleThreadForAssembly > SimpleThreadForAssembly.assembly.thisClass.and.Thread.txt
//javap -verbose basic.SimpleThreadForAssembly > SimpleThreadForAssembly.bytecode.txt
public class SimpleThread_for_PrintAssembly {

	public void execute(){
		
		Thread t1 = new Thread(new Runnable(){

			@Override
			public void run() {
				System.out.println("nothing, but print something in a thread");
				
			}});
		t1.start();
	}
	public static void main(String[] args){
		
		new baoying.eval.jdk.assembly.SimpleThread_for_PrintAssembly().execute();
	}
}
