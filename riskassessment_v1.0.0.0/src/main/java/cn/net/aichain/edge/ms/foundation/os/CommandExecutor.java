package cn.net.aichain.edge.ms.foundation.os;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public interface CommandExecutor {
	String ver();
	String env(final String key);
	String kill(final String programName);
	String execute(final String command);
	String execute(final String command,final String[] envp, final String dir);
	
	default String stream2string(final InputStream is) throws IOException {
		final LineNumberReader br = new LineNumberReader(new InputStreamReader(is));
		final StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line).append("\n");
		}
		return sb.toString();
	}
}
