package net.grayfield.spb.hobbylog.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

@Configuration
public class MovieClient {
    @Bean
    RestClient movieBaseClient() {
        return RestClient.builder()
            .baseUrl("https://api.themoviedb.org")
            .messageConverters(conv -> {
                conv.addFirst(new MappingJackson2HttpMessageConverter());
                conv.add(new StringHttpMessageConverter());
            })
            .build();
    }
}
