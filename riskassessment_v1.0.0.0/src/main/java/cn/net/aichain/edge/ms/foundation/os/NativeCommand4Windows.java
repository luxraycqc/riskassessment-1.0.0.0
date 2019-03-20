package cn.net.aichain.edge.ms.foundation.os;

import java.io.File;
import java.io.IOException;

import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.log.LogFactory;

public class NativeCommand4Windows implements CommandExecutor {
	public static final String CMDPREFIX = "cmd /c ";
	public static final String ENVPREFIX = "cmd /c c:&echo ";

	public NativeCommand4Windows() {
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
	
	public String kill(final String programName) {
		final String killcmd = "taskkill /IM "+programName+".exe";
		RuntimeUtil.execForStr(killcmd);
		return killcmd;
	}

	public String exe(final String commandStr) {
		return RuntimeUtil.execForStr(CMDPREFIX + commandStr);
	}
	
	String exe(final String command, final String[] envp, final String dir) {
		final StringBuilder sb = new StringBuilder();
		final String[] cmdA = { "cmd", "/c", command };
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ipfs.ms.module.os.CommandExecutor#env(java.lang.String)
	 */
	@Override
	public String env(final String key) {
		return System.getenv(key);
	}

	@Override
	public String ver() {
		return exe("ver");
	}
}