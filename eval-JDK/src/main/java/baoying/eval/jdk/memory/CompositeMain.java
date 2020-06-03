package baoying.eval.jdk.memory;

public class CompositeMain {
	
	DirectBufferMain direct = new DirectBufferMain();
	HeapEater heap = new HeapEater();
	ThreadNativeMain thread = new ThreadNativeMain();
	
	public static void main(String[] args) {
		
		CompositeMain m = new CompositeMain();
		m.usage();
		
		String command = "heap G 80";
		m.action(command);

	}
	
	public void action(String command){
		if(command.startsWith("thread " )){
			
			int threadNumberToBeCreated = Integer.parseInt(command.split(" ")[1]);
			thread.setupThreads(threadNumberToBeCreated);
			
		}else if(command.startsWith("direct " )){
			
			String specificCmd = command.substring("direct ".length()).trim();
			direct.action(specificCmd);
			
		}else if(command.startsWith("heap " )){
			
			String specificCmd = command.substring("heap ".length()).trim();
			heap.action(specificCmd);
		}
			
	}
	
	public void usage(){
		System.out.println("thread number - setup number threads which will siliently wait \n"
		+ "direct L MB/G MB/clean/gc \n"
		+ "heap L MB/G MB/clean/gc \n");
	}

}
