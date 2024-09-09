package net.grayfield.spb.hobbylog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@ServletComponentScan
@EnableAsync
@EnableCaching
@SpringBootApplication
public class HobbyLogApplication {

    public static void main(String[] args) {
        SpringApplication.run(HobbyLogApplication.class, args);
    }

}
