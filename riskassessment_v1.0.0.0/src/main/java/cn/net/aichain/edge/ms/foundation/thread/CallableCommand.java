package cn.net.aichain.edge.ms.foundation.thread;

import java.util.concurrent.Callable;

import cn.net.aichain.edge.ms.foundation.os.OSUtil;

public final class CallableCommand implements Callable<String> {
	final String command;
	final String workDir;
	public CallableCommand(final String command,final String workDir) {
		this.command = command;
		this.workDir = workDir;
	}

	@Override
	public String call() throws Exception {
		return OSUtil.execute(command, workDir);
	}

}
