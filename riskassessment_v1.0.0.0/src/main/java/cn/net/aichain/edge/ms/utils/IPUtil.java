/**
 * @Title: IPUtil.java
 * @Package com.cnbmtech.cdwpcore.sapclient.utils
 * @Description: TODO(用一句话描述该文件做什么)
 * @author markzgwu
 * @date 2017年12月11日
 * @version V1.0
 */

package cn.net.aichain.edge.ms.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.alibaba.fastjson.JSON;

import cn.hutool.core.util.NetUtil;
import cn.hutool.log.LogFactory;

public final class IPUtil {

	public static String getServerAddr() {
		return getServerAddrOpt();
	}

	public static String getServerAddrByJDK() {
		String addr = "localhost";
		try {
			addr = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			LogFactory.get().error(e);
		} // 获得本机IP
		return addr;
	}

	public static String getServerAddrByHutool() {
		return NetUtil.getLocalhostStr();
	}

	public static String getServerAddrOpt() {
		final List<String> addrs = getServerAddrByMultiNetCard();
		return addrs.get(addrs.size()-1);
	}
	
	public static List<String> getServerAddrByMultiNetCard() {
		final List<String> addrs = new ArrayList<>();
		try {
			Enumeration<NetworkInterface> interfaces = null;
			interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface ni = interfaces.nextElement();
				Enumeration<InetAddress> addresss = ni.getInetAddresses();
				while (addresss.hasMoreElements()) {
					InetAddress nextElement = addresss.nextElement();
					String hostAddress = nextElement.getHostAddress();
					System.out.println("本机IP地址为：" + hostAddress);
					if(!hostAddress.contains("127.0.0.1") && !hostAddress.contains("0:0:0:0:0:0:0:1") && hostAddress.length()<=15) {
						addrs.add(hostAddress);
					}
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return addrs;
	}

	public static void main(String[] args) {
		LogFactory.get().info(getServerAddr());
		LogFactory.get().info(JSON.toJSONString(getServerAddrByMultiNetCard()));
	}

}
