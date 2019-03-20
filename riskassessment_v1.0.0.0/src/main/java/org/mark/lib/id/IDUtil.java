
/**  
* @Description: TODO(用一句话描述该文件做什么)
* @author markzgwu
* @date 2018年6月26日
* @version V1.0  
*/

package org.mark.lib.id;

import java.util.UUID;

import cn.hutool.log.LogFactory;

/**
 * @author markzgwu
 *
 */
public class IDUtil {
	static final SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);

	public static long getId() {
		return idWorker.nextId();
	}

	public static String getOrderIdByUUId(final String machineId) {
		int hashCodeV = UUID.randomUUID().toString().hashCode();
		if (hashCodeV < 0) {// 有可能是负数
			hashCodeV = -hashCodeV;
		}
		// 0 代表前面补充0
		// 4 代表长度为4
		// d 代表参数为正数型
		final String orderId = machineId + String.format("%015d", hashCodeV);
		//System.out.println(orderId);
		return orderId;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LogFactory.get().info("id=" + getId());
		LogFactory.get().info("uuid=" + getOrderIdByUUId("0"));
	}

}
