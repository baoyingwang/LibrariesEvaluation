package baoying.eval.jdk.memory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DirectBufferMain {

	enum COMAND {
		L, // LOCAL eat in MB
		G, // GLOBAL eat in MB
		clean,
		gc
	}

	List<ByteBuffer> bag = new ArrayList<ByteBuffer>();

	public void eat(COMAND s, int xMB) {

		switch (s) {

		case L:
			ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 1024* xMB);
			break;
		case G:
			bag.add(ByteBuffer.allocateDirect(1024 * 1024 * xMB));

			long totalCapacity = 0;
			for (ByteBuffer b : bag) {
				totalCapacity += b.capacity();
			}
			System.out.println("Global total direct buffer:" + (totalCapacity/(1024*1024)) +" MB");
			break;
		default:
			System.out.println("only support G and L command");
		}
	}

	public static void main(String[] args) throws Exception {

		DirectBufferMain m = new DirectBufferMain();
		java.io.Console cons = System.console();

		while (true) {

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
	
	private void usage() {
		System.out.println("Eat local:" + COMAND.L.toString()
				+ " X , means declare a local reference which takes X MB. It will be cleaned on each minor GC.");
		System.out.println("Eat global:" + COMAND.G.toString()
				+ " X , means declare a global reference which takes X MB. It always has root reference");
		System.out.println("clean:" + COMAND.clean.toString()
				+ ", means clean the declared global space. Not required to clean the local one, since it will be cleaned automatically on every minr gc.");
		System.out.println("gc:" + COMAND.gc.toString());
		System.out.println("");
	}

}
