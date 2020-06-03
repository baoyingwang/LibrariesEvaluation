package baoying.eval.jdk.memory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CompositeMonitorCommandFile {

	static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss");

	private volatile boolean started = false;

	public synchronized void beginMonitoring() {
		if (started) {
			return;
		}

		started = true;
		monitorInItsOwnThread();

	}

	private void monitorInItsOwnThread() {

		new Thread(new Runnable() {
			public void run() {
				try {
					CompositeMain m = new CompositeMain();
					String monitoredFile = System.getProperty("composite.monitor.file");
					if (monitoredFile == null) {
						System.out.println("error: composite.monitor.file is not defined");
						return;
					}

					while (true) {

						File f = new File(monitoredFile);
						if(!f.exists()){
							Thread.sleep(2 * 1000);
							continue;
						}
						
						FileInputStream fis = new FileInputStream(f);

						// Construct BufferedReader from InputStreamReader
						BufferedReader br = new BufferedReader(new InputStreamReader(fis));

						String line = null;
						while ((line = br.readLine()) != null) {
							if (line.trim().equals("") || line.trim().startsWith("#")) {
								continue;
							}
							System.out.println("command to be executed:" + line);
							m.action(line);
						}

						br.close();
						f.renameTo(new File(monitoredFile + ".executed." + formatter.format(new Date())));
					}
				} catch (Exception e) {
					System.out.println("error : monitor thread exit"+ e.getMessage());
					e.printStackTrace();
					
				}
			}
		}).start();

	}

	public static void main(String[] args) throws Exception {

		CompositeMonitorCommandFile mf = new CompositeMonitorCommandFile();
		mf.beginMonitoring();
	}

}
