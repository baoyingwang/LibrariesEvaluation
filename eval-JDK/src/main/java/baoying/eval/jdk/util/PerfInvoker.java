package baoying.eval.jdk.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * java.util.concurrent.ScheduledThreadPoolExecutor should be considered in some
 * case. But it does NOT mean ScheduledThreadPoolExecutor could replace this
 * utility at all. Maybe corePoolSize=1 with scheduleAtFixedRate , is same with
 * this utility. I will try it later...20150105
 * 
 * ScheduledThreadPoolExecutor introduced more explexity, e.g. 1. corePoolSize -
 * the number of threads to keep in the pool, even if they are idle, unless
 * allowCoreThreadTimeOut is set how many threads should be used? 2.
 * scheduleAtFixedRate or scheduleWithFixedDelay is useful in some case. But pls
 * be clear its behavior before go ahead.
 * http://stackoverflow.com/questions/24649842
 * /scheduleatfixedrate-vs-schedulewithfixeddelay
 * 
 */
public class PerfInvoker {

	TimeUnit unit = TimeUnit.SECONDS;

	final int _ratePerSec;
	final int _durationInSec;
	final PerfInvokerCallback _callback;

	int _burstNumPerSec = 5;
	final int _sizePerBurst;
	final long _burstDurationInNano;
	boolean _enableConsoleOutput = false;

	public PerfInvoker(int ratePerSec, int duarationInSec, PerfInvokerCallback c) {
		this(ratePerSec, duarationInSec, c, false);

	}

	public PerfInvoker(int ratePerSec, int duarationInSec,
			PerfInvokerCallback c, boolean enableConsoleOutput) {

		// if(ratePerSec < 2){
		// throw new RuntimeException("min ratePerSec: 2 for now");
		// }

		this._ratePerSec = ratePerSec;
		this._durationInSec = duarationInSec;
		this._callback = c;

		if (_ratePerSec < 10) {
			_burstNumPerSec = _ratePerSec;
		} else if (_ratePerSec >= 10 && _ratePerSec < 100) {
			_burstNumPerSec = 5;
		} else if (_ratePerSec >= 100 && _ratePerSec < 1000) {
			_burstNumPerSec = 10;
		} else if (_ratePerSec >= 1000 && _ratePerSec < 10000) {
			_burstNumPerSec = 50;
		} else if (_ratePerSec >= 10000 && _ratePerSec < 100000) {
			_burstNumPerSec = 100;
		} else if (_ratePerSec >= 100000) {
			_burstNumPerSec = 1000;
		}

		this._sizePerBurst = ratePerSec / _burstNumPerSec;
		this._burstDurationInNano = unit.toNanos(1) / _burstNumPerSec;

		_enableConsoleOutput = enableConsoleOutput;
		if (_enableConsoleOutput) {
			System.out.println("execute parameters - ratePerSec:" + _ratePerSec
					+ ",durationInSec:" + _durationInSec + ",_sizePerBurst:"
					+ _sizePerBurst + ", burstDurationInNano:"
					+ _burstDurationInNano + ", _burstNumPerSec:"
					+ _burstNumPerSec);
		}
	}

	public void execute() throws InterruptedException {

		long start = System.nanoTime();

		int executedCounter = 0;
		for (int i = 0; i < _durationInSec; i++) {

			for (int j = 0; j < _burstNumPerSec; j++) {

				long burst_start = System.nanoTime();
				for (int k = 0; k < _sizePerBurst; k++) {
					executedCounter++;
					_callback.execute(executedCounter);
				}
				long burst_end = System.nanoTime();

				long burstSleep = _burstDurationInNano
						- (burst_end - burst_start);
				TimeUnit.NANOSECONDS.sleep(burstSleep);
			}
		}

		long end = System.nanoTime();
		double durationInS = ((end - start) / Math.pow(10, 9));

		double acturalAvgSpped = executedCounter / durationInS;

		if (_enableConsoleOutput) {
			System.out.println("invoke:" + executedCounter + " times");
			System.out.println("expected/acture rate:" + _ratePerSec + "/"
					+ acturalAvgSpped + " msg/sec,expected/actural duration:"
					+ _durationInSec + "/" + durationInS + " seconds");
		}
	}

	public static interface PerfInvokerCallback {

		// seq: 1 based
		void execute(long seq);
	}

	public static void main(String[] args) throws InterruptedException {

		int speed = 3;
		int duaration = 10;

		final AtomicInteger i = new AtomicInteger(0);

		new PerfInvoker(speed, duaration, new PerfInvokerCallback() {

			@Override
			public void execute(long seq) {
				i.incrementAndGet();
			}
		}).execute();

	}

}
