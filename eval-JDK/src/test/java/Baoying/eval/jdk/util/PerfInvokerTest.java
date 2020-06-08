package baoying.eval.jdk.util;

import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.junit.Test;


public class PerfInvokerTest {

	@Test
	public void testVariousParamters() throws InterruptedException {

		testVariousParamters(1, 3);
		testVariousParamters(2, 3);
		testVariousParamters(5, 3);
		testVariousParamters(7, 3);
		testVariousParamters(9, 3);
		testVariousParamters(10, 3);
		testVariousParamters(30, 3);
		testVariousParamters(100, 3);
		testVariousParamters(1000, 3);
		testVariousParamters(10000, 3);
	}

	public void testVariousParamters(int speed, int duaration)
			throws InterruptedException {

		final AtomicInteger i = new AtomicInteger(0);

		new PerfInvoker(speed, duaration, new PerfInvoker.PerfInvokerCallback() {

			@Override
			public void execute(long seq) {
				i.incrementAndGet();
			}
		}).execute();

		Assert.assertEquals("parameters - speed:" + speed + ", duaration:"
				+ duaration, speed * duaration, i.get());
	}
}
