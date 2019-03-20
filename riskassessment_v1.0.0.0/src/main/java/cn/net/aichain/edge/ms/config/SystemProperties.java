package cn.net.aichain.edge.ms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import cn.net.aichain.edge.ms.config.plugin.PluginConfig;

import javax.validation.constraints.NotNull;

@Validated
@ConfigurationProperties("system")
public class SystemProperties {

	private String mode;
	private String serverIp;
	private String port;

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	@NotNull
	private String version;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@NotNull
	private final PythonLib pythonLib = new PythonLib();

	public PythonLib getPythonLib() {
		return pythonLib;
	}

	public static class PythonLib {
		private String home;

		public String getHome() {
			return home;
		}

		public void setHome(String home) {
			this.home = home;
		}

		String cmd;

		public String getCmd() {
			return cmd;
		}

		public void setCmd(String cmd) {
			this.cmd = cmd;
		}

	}

	@NotNull
	private final PluginConfig pluginConfig = new PluginConfig();

	public PluginConfig getPluginConfig() {
		return pluginConfig;
	}
	
}
