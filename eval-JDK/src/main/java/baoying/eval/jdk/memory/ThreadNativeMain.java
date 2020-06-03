package baoying.eval.jdk.memory;

import java.io.Console;

public class ThreadNativeMain {
	public static void main(String[] args) {
		Console cons = System.console();
		if (cons == null) {
			System.err.println("cannot get console");
			System.exit(1);
		}
		new ThreadNativeMain().interactivelySetupThreads(cons);
	}

	public void interactivelySetupThreads(Console cons) {
		while (true) {

			int activeCount = java.lang.Thread.activeCount();
			System.out.println("active count - before :" + activeCount);

			final int threadNumberToBeCreated;
			System.out.print("input thread number to be created(q for exit):");
			String input = cons.readLine();
			if (input.equals("q")) {
				System.exit(0);
			}
			threadNumberToBeCreated = Integer.parseInt(input);
			setupThreads(threadNumberToBeCreated);
			
			System.out.println("active count - after:" + java.lang.Thread.activeCount());
		}
	}

	public void setupThreads(int threadNumberToBeCreated) {

		int activeCount = java.lang.Thread.activeCount();

		for (int i = 0; i < threadNumberToBeCreated; i++) {
			
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					silentWait(Integer.MAX_VALUE);
				}
			}, "ThreadNativeInvestigation-"+String.valueOf(i));
			t.start();
			System.out.println("creating  #" + (activeCount + i + 1) + " thread, tid:"+t.getId());
		}


	}

	private void silentWait(int milli) {
		try {
			synchronized (this) {
				this.wait(milli);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
