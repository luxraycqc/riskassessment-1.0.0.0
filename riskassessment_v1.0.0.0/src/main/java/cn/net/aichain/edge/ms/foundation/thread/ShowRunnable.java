package cn.net.aichain.edge.ms.foundation.thread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import cn.hutool.log.LogFactory;

public final class ShowRunnable implements Runnable {
	public ShowRunnable() {
		LogFactory.get().info("Starting " + ShowRunnable.class);
	}

	@Override
	public void run() {
			Future<String> ret;
			try {
				while ((ret = ThreadUtil.cService.take()) != null) {
					System.out.println(ret.get());
					Thread.sleep(1000);
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
	}

}
