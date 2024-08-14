package net.grayfield.spb.hobbylog.domain.share;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Map;

@Slf4j
public class StaticHelper {
    public static void logMap(Map raw) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            log.info("{}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(raw));
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("{}", e.getCause().toString());
            log.error("{}", Arrays.toString(e.getStackTrace()));
        }

    }
}
