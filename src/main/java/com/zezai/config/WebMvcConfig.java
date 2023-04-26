package com.zezai.config;

import com.zezai.common.JacksonObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;


/*因为js对于Long型的数据会产生精度缺失,所以我们需要将服务端传来的Long型数据转换为String类型
操作:1.提供对象转换器JacksonObjectMapper
    2.在SpringMvc配置类中编辑新转换器,并将新转换器添加到mvc转换器容器converters中*/

@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");

    }

    //扩展mvc框架的消息转换器,其实就是修改默认的@Responsebody转JSON格式
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //新建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter=new MappingJackson2HttpMessageConverter();

        //设置对象转换器,底层使用Jackson将Java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());

        //将上面的消息转换器对象追加到mvc框架转换器容器中
        converters.add(0,messageConverter);
    }
}
