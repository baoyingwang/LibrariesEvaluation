package baoying.eval.jdk.memory;

public class HeapEater {

	enum COMAND {
		L, // LOCAL eat in MB
		G, // GLOBAL eat in MB
		clean,
		gc
	}

	java.util.List<byte[]> bag = new java.util.ArrayList<byte[]>();

	public void eat(COMAND s, int xMB) {

		switch (s) {

		case L:
			//multi blocks to simulate real life scenariors
			for(int i =0; i< xMB; i++){
				byte[] x = new byte[ 1024 * 1024];
			}
			
			printHeap();
			break;
		case G:
			byte[] x = new byte[xMB * 1024 * 1024];
			bag.add(x);

			long totalCapacity = 0;
			for (byte[] b : bag) {
				totalCapacity += b.length;
			}
			System.out.println("Global total heap:" + (totalCapacity/(1024*1024)) +" MB");
			printHeap();
			break;
		default:
			System.out.println("only support G and L command");
		}
	}

	public static void main(String[] args) throws Exception {

		HeapEater m = new HeapEater();
		java.io.Console cons = System.console();

		while (true) {

			m.usage();
			String input = cons.readLine();
			m.action(input);

		}
	}
	
	public void action(String commandInput){
		if (commandInput.startsWith(COMAND.G.toString() + " ")) {

			int numInKB = Integer.parseInt(commandInput.split(" ")[1]);
			eat(COMAND.G, numInKB);
		} else if (commandInput.startsWith(COMAND.L.toString() + " ")) {

			int numInKB = Integer.parseInt(commandInput.split(" ")[1]);
			eat(COMAND.L, numInKB);
		} else if (commandInput.startsWith(COMAND.clean.toString() + " ")) {

			bag.clear();
		}else if (commandInput.startsWith(COMAND.gc.toString())) {

			System.gc();
		}
	}
	
	public void usage() {
		System.out.println("Eat local:" + COMAND.L.toString()
				+ " X , means declare a local reference which takes X MB. It will be cleaned on each minor GC.");
		System.out.println("Eat global:" + COMAND.G.toString()
				+ " X , means declare a global reference which takes X MB. It always has root reference");
		System.out.println("clean:" + COMAND.clean.toString()
				+ ", means clean the declared global space. Not required to clean the local one, since it will be cleaned automatically on every minr gc.");
		System.out.println("gc:" + COMAND.gc.toString());
		System.out.println("");
	}

	public static void printHeap() {

		int mb = 1024 * 1024;

		System.out.println("*******Heap stats in MB************");
		Runtime runtime = Runtime.getRuntime();
		System.out.println("Used:" + (runtime.totalMemory() - runtime.freeMemory()) / mb);
		System.out.println("Free:" + runtime.freeMemory() / mb);
		System.out.println("Total:" + runtime.totalMemory() / mb);
		System.out.println("Max:" + runtime.maxMemory() / mb);

	}
}
