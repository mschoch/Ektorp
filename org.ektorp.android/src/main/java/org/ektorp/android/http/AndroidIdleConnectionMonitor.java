package org.ektorp.android.http;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.couchbase.httpclient.android.conn.ClientConnectionManager;

public class AndroidIdleConnectionMonitor {

	private final static long DEFAULT_IDLE_CHECK_INTERVAL = 30;

	private final static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1, new ThreadFactory() {

		private final AtomicInteger threadCount = new AtomicInteger(0);

		public Thread newThread(Runnable r) {
			Thread t = new Thread();
			t.setDaemon(true);
			t.setName(String.format("ektorp-idle-connection-monitor-thread-%s", threadCount.incrementAndGet()));
			return t;
		}
	});

	public static void monitor(ClientConnectionManager cm) {
		executorService.scheduleWithFixedDelay(new CleanupTask(cm), DEFAULT_IDLE_CHECK_INTERVAL, DEFAULT_IDLE_CHECK_INTERVAL, TimeUnit.SECONDS);
	}

	private static class CleanupTask implements Runnable {

		final ClientConnectionManager cm;

		CleanupTask(ClientConnectionManager cm) {
			this.cm = cm;
		}

		public void run() {
			cm.closeExpiredConnections();
		}

	}

}
