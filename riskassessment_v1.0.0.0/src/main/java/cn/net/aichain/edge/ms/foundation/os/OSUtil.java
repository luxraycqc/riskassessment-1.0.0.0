
/**  
* @Description: TODO(用一句话描述该文件做什么)
* @author markzgwu
* @date 2018年6月25日
* @version V1.0  
*/

package cn.net.aichain.edge.ms.foundation.os;

import java.io.*;
import java.util.*;

import cn.hutool.setting.dialect.Props;
import org.aspectj.util.FileUtil;

import cn.hutool.log.LogFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author markzgwu
 *
 */
public final class OSUtil {
	public static final CommandExecutor cmdexe = init();
	
	public static CommandExecutor init(){
		if (isWindows()) {
			return new NativeCommand4Windows();
		} else {
			return new NativeCommand4Linux();
		}
	}

	public static boolean isWindows() {
		boolean flag = false;
		if (System.getProperties().getProperty("os.name").toLowerCase().indexOf("windows") != -1) {
			flag = true;
		}
		return flag;
	}
	
	public static String os() {
		String os = "linux";
		if(isWindows()) {
			os = "windows";
		}
		return os;
	}
	
	public static String execute(final String command, final String[] envp, final String dir) {
		return cmdexe.execute(command, envp, dir);
	}
	
	public static String execute(final String command, final String dir) {
		return cmdexe.execute(command, null, dir);
	}
	
	public static String execute(final String command) {
		return cmdexe.execute(command);
	}
	
	public static String kill(final String programName) {
		return cmdexe.kill(programName);
	}
	
	public static String env(final String key) {
		return cmdexe.env(key);
	}
	
	public static String path() {
		return env("PATH");
	}
	
	public static String ver() {
		return cmdexe.ver();
	}
	
	public static String userHome() {
		return getProperty("user.home");
	}
	
	public static String getProperty(final String key) {
		return System.getProperty(key);
	}

	public static void copy(final String fromFilePath, final String toFilePath) throws IOException {
		final File fromFile = new File(fromFilePath);
		final File toFile = new File(toFilePath);
		if(fromFile.exists() && !toFile.exists()) {
			FileUtil.copyFile(fromFile, toFile);
			LogFactory.get().debug("COPY "+fromFilePath+" "+toFilePath);
		}
	}

	/**
	 * 执行python3程序，读取控制台输出结果，按行存入ArrayList
	 * @param fileName 主python文件，如“model_main.py”
	 * @param args 附加参数，多个以空格隔开
	 * @param filePath 主python文件所在目录相对路径，如“/python3Lib/algorithms”
	 * @return
	 */
	public static ArrayList<String> executePythonAndGetResult(final String fileName, final String args, final String filePath) {
		String[] cmd = {"python3", fileName};
		String[] fullcmd;
		if (args.length() > 0) {
			String[] argsArray = args.split(" ");
			int length = argsArray.length;
			fullcmd = new String[length + 2];
			System.arraycopy(cmd, 0, fullcmd, 0, 2);
			System.arraycopy(argsArray, 0, fullcmd, 2, length);
		} else {
			fullcmd = cmd;
		}
		Props props = new Props("config/application.properties");
		String pythonParentPath = props.getStr("pythonParentPath");
		//File dir = new File(OSUtil.class.getResource("/") + filePath);
		File dir = new File(pythonParentPath + filePath);
		try {
			LogFactory.get().info(Arrays.toString(fullcmd));
			LogFactory.get().info(dir.getAbsolutePath());
			Process process = Runtime.getRuntime().exec(fullcmd, null, dir);
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			ArrayList<String> resultList = new ArrayList<>();
			while ((line = br.readLine()) != null) {
				resultList.add(line);
			}
			br.close();
			int re = process.waitFor();
			if (re == 0) {
				return resultList;
			} else {
			    return new ArrayList<String>();
            }
		} catch (Exception e) {
			e.printStackTrace();
			ArrayList<String> ret = new ArrayList<>();
			ret.add(e.toString());
			return ret;
		}
	}
	
	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		System.out.println(OSUtil.class.getClassLoader().getResource(""));
//		LogFactory.get().info(userHome());
//		LogFactory.get().info(execute("c:&ver"));
//		LogFactory.get().info(execute("c:&dir"));
//		LogFactory.get().info(env("PATH"));
	}
}
