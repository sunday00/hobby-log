package net.grayfield.spb.hobbylog.domain.auth;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.auth.service.KakaoService;
import net.grayfield.spb.hobbylog.domain.auth.struct.Auth;
import net.grayfield.spb.hobbylog.domain.auth.struct.KakaoMeInfo;
import net.grayfield.spb.hobbylog.domain.auth.struct.KakaoTokenRes;
import net.grayfield.spb.hobbylog.domain.user.service.UserService;
import net.grayfield.spb.hobbylog.domain.user.struct.User;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;


@Slf4j
@Controller
@AllArgsConstructor
public class AuthController {
    private final KakaoService kakaoService;
    private final UserService userService;

    @QueryMapping
    public Auth sign (@Argument String code) {
        KakaoTokenRes tokenRes = kakaoService.getKakaoToken(code);

        assert tokenRes != null;
        String kakaoToken = tokenRes.getAccess_token();

        log.info("{}", kakaoToken);

        KakaoMeInfo meRes = kakaoService.getKakaoMeInfo(kakaoToken);

        log.info("{}", meRes);

        User user = userService.findOneOrCreateByEmail(meRes);

        log.info("{}", user);

        Auth auth = new Auth();
        auth.setAccessToken("111");

        return auth;
    }
}
