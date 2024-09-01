package net.grayfield.spb.hobbylog.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticConfig implements WebMvcConfigurer {

    @Value("${storage.path}")
    private String storagePath;

    @Value("${spring.security.cors.origin}")
    private String corsOrigin;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:"+storagePath)
                .addResourceLocations("file:"+storagePath)
                .setCachePeriod(100)
        ;

        registry.addResourceHandler("/images/default/**")
                .addResourceLocations("classpath:"+storagePath+"images/default/")
                .addResourceLocations("file:"+storagePath+"images/default/")
                .setCachePeriod(100)
        ;

        registry.addResourceHandler("/images/upload/**")
                .addResourceLocations("classpath:"+storagePath+"images/upload/")
                .addResourceLocations("file:"+storagePath+"images/upload/")
                .setCachePeriod(100)
        ;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(corsOrigin)
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowCredentials(true)
        ;
    }
}
