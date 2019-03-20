package cn.net.aichain.edge.ms.foundation.os;

import java.io.File;
import java.io.IOException;

import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.log.LogFactory;

public class NativeCommand4Linux implements CommandExecutor {
	public static final String CMDPREFIX = "/bin/sh -c ";
	public static final String ENVPREFIX = "/bin/sh -c echo ";
	
	public NativeCommand4Linux() {
		super();
	}

	public String execute(final String command) {
		return filterCode(exe(command));
	}
	
	public String execute(final String command, final String[] envp, final String dir) {
		return filterCode(exe(command, envp, dir));
	}

	public String filterCode(final String str) {
		return str;
	}
	
	public String exe(final String commandStr) {
		return RuntimeUtil.execForStr(CMDPREFIX+commandStr);
	}

	String exe(final String command, final String[] envp, final String dir) {
		final StringBuilder sb = new StringBuilder();
		final String[] cmdA = { "/bin/sh", "-c", command };
		LogFactory.get().info(command);
		Process process;
		try {
			process = Runtime.getRuntime().exec(cmdA, envp, new File(dir + File.separator));
			sb.append(stream2string(process.getInputStream()));
			sb.append(stream2string(process.getErrorStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see com.ipfs.ms.module.os.CommandExecutor#env(java.lang.String)
	 */
	@Override
	public String env(String key) {
		return System.getenv(key);
	}
	
	@Override
	public String ver() {
		return exe("uname -a");
	}

	@Override
	public String kill(final String programName) {
		final String killcmd = "killall "+programName;
		RuntimeUtil.execForStr(killcmd);
		return killcmd;
	}

}