package cn.cotenite.ai.config

import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.nio.charset.StandardCharsets

/**
 * @Author  RichardYoung
 * @Description
 * @Date  2025/4/1 17:46
 */
@Configuration
class WebConfig:WebMvcConfigurer {
    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.removeIf { c: HttpMessageConverter<*>? -> c is StringHttpMessageConverter }
        val converter = StringHttpMessageConverter(StandardCharsets.UTF_8)
        converters.add(converter)
    }

}