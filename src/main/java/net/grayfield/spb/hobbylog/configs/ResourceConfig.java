package net.grayfield.spb.hobbylog.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;

@Configuration
public class ResourceConfig {
    @Bean
    public String CLASS_PATH () throws FileNotFoundException {
        return ResourceUtils.getFile("classpath:static").toString();
    }
}
