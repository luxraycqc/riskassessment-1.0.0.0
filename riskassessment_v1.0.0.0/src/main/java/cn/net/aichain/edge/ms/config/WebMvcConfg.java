package cn.net.aichain.edge.ms.config;


import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//@Configuration
//@EnableWebMvc
public class WebMvcConfg implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // registry.addResourceHandler("/public/**").addResourceLocations("classpath:/public/");
        // registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

    /**
     * 添加自定义的Converters和Formatters.
     */
//    public class WebMvcConfg extends WebMvcConfigurationSupport {
//    @Override
//    protected  void addFormatters(FormatterRegistry registry) {
//         registry.addConverter(new StringToDateConverter());
//    }

    /**
     * 修改自定义消息转换器
     *
     * @param converters 消息转换器列表
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 删除旧的MappingJackson2HttpMessageConverter
        Iterator<HttpMessageConverter<?>> iterator = converters.iterator();
        while(iterator.hasNext()){
            HttpMessageConverter<?> converter = iterator.next();
            if(converter instanceof MappingJackson2HttpMessageConverter){
                iterator.remove();
            }
        }
        // 构造新的fastJsonHttpMessageConverter
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        //自定义配置
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        // 设置：DisableCircularReferenceDetect，让对象属性完全显示，避免：attendance:{"person":{"$ref":"$[7].person"}},
        fastJsonConfig.setSerializerFeatures(SerializerFeature.QuoteFieldNames,
                SerializerFeature.WriteEnumUsingToString,
                /*SerializerFeature.WriteMapNullValue,*/
                SerializerFeature.WriteDateUseDateFormat,
                SerializerFeature.DisableCircularReferenceDetect);

        SerializeConfig serializeConfig = SerializeConfig.globalInstance;
        serializeConfig.put(BigInteger.class, ToStringSerializer.instance);
        serializeConfig.put(Long.class, ToStringSerializer.instance);
        serializeConfig.put(Long.TYPE, ToStringSerializer.instance);
        fastJsonConfig.setSerializeConfig(serializeConfig);
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        // 处理异常： IllegalArgumentException: 'Content-Type' cannot contain wildcard type '
        fastJsonHttpMessageConverter.setSupportedMediaTypes(createSupportMediaTypes());
        // 添加转换器
        converters.add(fastJsonHttpMessageConverter);
    }

    
    public List<MediaType> createSupportMediaTypes(){
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(MediaType.ALL);
       return  supportedMediaTypes;
    }
}