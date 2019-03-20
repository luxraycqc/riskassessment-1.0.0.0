package cn.net.aichain.edge.ms.foundation.thread;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class ThreadUtil {
	private ThreadUtil() {
		
	}
	
	public static final ExecutorService pool = Executors.newCachedThreadPool();
	public static final CompletionService<String> cService = new ExecutorCompletionService<>(pool);

	static {
		pool.execute(new ShowRunnable());
	}
	
	public static void run(final String command, final String dir) {
		cService.submit(new CallableCommand(command, dir));
	}

	public static String info() {
		return pool.toString() + "\n" + cService.toString();
	}

	public static String show() throws InterruptedException, ExecutionException {
		final Future<String> ret = cService.take();
		final StringBuilder sb = new StringBuilder();
		if (ret != null) {
			sb.append("\n").append(ret.get());
		} else {
			sb.append("\n").append("waiting");
		}
		return sb.toString();
	}

	public static void shutdown() {
		pool.shutdownNow();
	}

}
