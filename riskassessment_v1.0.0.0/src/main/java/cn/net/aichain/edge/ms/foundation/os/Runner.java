package cn.net.aichain.edge.ms.foundation.os;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.log.LogFactory;

public final class Runner {

	public static String exe(final String cmd) {
		return exe2string(cmd);
	}
	
	public static String exe2string(final String cmd) {
		final StringBuilder sb = new StringBuilder();
		try {
			// 执行pg命令
			Process process = Runtime.getRuntime().exec(cmd);
			BufferedReader br = new BufferedReader(
					new InputStreamReader(process.getInputStream(), Charset.forName("GBK")));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append("\n");
				sb.append(line);
			}
		} catch (IOException e) {
			LogFactory.get().error(e.getLocalizedMessage());
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		LogFactory.get().info("\n"+System.getProperties());
		LogFactory.get().info("\n"+System.getProperty("java.io.tmpdir"));
		
		String[] cmds = new String[2];
		cmds[0] = "cmd /c c:&dir";
		cmds[1] = "cmd /c c:&ver";
		//cmds[2] = "D:\\work\\env\\ipfs\\go-ipfs\\ipfs";
		//cmds[3] = "cmd /c c:&echo %IPFS_HOME%";
		for (String cmd : cmds) {
			LogFactory.get().info((exe(cmd)));
			LogFactory.get().info("\n"+(RuntimeUtil.execForStr(cmd)));
		}
		
	}

}
