package cn.net.aichain.edge.ms.foundation.os;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cn.hutool.log.LogFactory;
import cn.net.aichain.edge.ms.config.SystemProperties;
import cn.net.aichain.edge.ms.utils.IPUtil;

@Component
public class SystemLoader implements ApplicationRunner {
	@Autowired
	SystemProperties config;

	public void showinfo() {
		final StringBuilder sb = new StringBuilder();
		final Comparator<String> comparator = (obj1, obj2) -> obj1.compareTo(obj2);
		final Map<String, String> envs = new TreeMap<>(comparator);
		envs.putAll(System.getenv());
		final Set<String> keySet = envs.keySet();
		final Iterator<String> iter = keySet.iterator();
		while (iter.hasNext()) {
			final String key = iter.next();
			sb.append("\n").append(key + ":" + envs.get(key));
		}
		sb.append("\n").append(JSON.toJSONString(config));
		sb.append("\n").append(OSUtil.ver());
		sb.append("\n").append(OSUtil.path());
		sb.append("\n").append("MODE=" + config.getMode());
		sb.append("\n").append("ServerIP=" + IPUtil.getServerAddr());

		LogFactory.get().info(sb.toString());
	}

	public void initPath() {
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		initPath();
		System.getProperties().list(System.out);
		showinfo();
		final StringBuilder cmdmsg = new StringBuilder();
		cmdmsg.append("\n").append("java -jar riskassessment.jar");
		//cmdmsg.append("\n").append("java -jar ipfs.jar --system.mode=bootstrap");
		//cmdmsg.append("\n").append("java -jar ipfs.jar --system.mode=node --serverIp=yourIp --port=yourPort");
		//cmdmsg.append("\n").append("java -jar ipfs.jar --bootstrap-simplify-ipfs-restart");
		//cmdmsg.append("\n").append("java -jar ipfs.jar --node-simplify-ipfs-restart --serverIp=yourIp --port=yourPort");
		switch (config.getMode()) {
		default:
			LogFactory.get().info(cmdmsg.toString());
		}
	}

}