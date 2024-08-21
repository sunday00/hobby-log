package net.grayfield.spb.hobbylog.domain.share;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.user.struct.UserAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Map;

@Slf4j
public class StaticHelper {
    public static String getUserId () {
        UserAuthentication authentication = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return authentication.getId();
    }

    public static String getUserEmail () {
        UserAuthentication authentication = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return authentication.getUserEmail();
    }

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
