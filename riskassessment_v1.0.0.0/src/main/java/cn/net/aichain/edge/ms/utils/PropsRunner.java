package cn.net.aichain.edge.ms.utils;

import java.io.File;

import cn.hutool.setting.dialect.Props;

public final class PropsRunner {

	public static void main(String[] args) {
		final Props PROPS = new Props("config" + File.separator + "system.properties");
		System.out.println(PROPS.toString());
	}

}
