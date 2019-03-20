
    /**  
    * @Description: TODO(用一句话描述该文件做什么)
    * @author markzgwu
    * @date 2018年4月28日
    * @version V1.0  
    */
    
package cn.net.aichain.edge.ms.utils;

import cn.hutool.core.date.DateTime;

/**
 * @author markzgwu
 *
 */
public class TimeUtil {

	public static String now() {
		final DateTime now = DateTime.now();
		final String key = now.toString();
		return key;
	}
	
}
