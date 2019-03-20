/**
 * @Title: WebMessage.java
 * @Package org.gmssl.ca.message
 * @Description: TODO
 * @author zhengangwu
 * @date 2018年2月7日
 * @version V1.0
 */

package cn.net.aichain.edge.ms.message;

import com.alibaba.fastjson.JSON;

import java.util.*;

/**
 * @ClassName: WebMessage
 * @Description: TODO
 * @author zhengangwu
 * @date 2018年2月7日
 *
 */

public class WebMessage {
	public final String ver = "v1";
    public final String code;
    public final Map<String, Object> msg = new TreeMap<>();

    public WebMessage() {
        super();
        this.code = "OK";
    }
    
    public WebMessage(String code) {
        super();
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public Map<String, Object> getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this, true);
    }
}
