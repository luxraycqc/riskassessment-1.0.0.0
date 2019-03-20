package cn.net.aichain.edge.ms.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties(SystemProperties.class)
@PropertySource("classpath:config/system.properties")
public class SystemConfig {

}
