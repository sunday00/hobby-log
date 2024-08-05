package net.grayfield.spb.hobbylog.domain.auth;

import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.auth.struct.Auth;
import net.grayfield.spb.hobbylog.domain.auth.struct.KakaoMeInfo;
import net.grayfield.spb.hobbylog.domain.auth.struct.KakaoTokenRes;
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
public class AuthController {
    @QueryMapping
    public Auth sign (@Argument String code) {

        RestClient tokenClient = RestClient.builder()
                .baseUrl("https://kauth.kakao.com")
                .messageConverters(conv -> conv.addFirst(new MappingJackson2HttpMessageConverter()))
                .build();

        KakaoTokenRes tokenRes = tokenClient.post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(
                        CollectionUtils.toMultiValueMap(Map.of(
                                "code", List.of(code),
                                "client_id", List.of("c3359f9b1c78173adb140522ddaeca54"),
                                "grant_type", List.of("authorization_code"),
                                "redirect_uri", List.of("http://localhost:3000/auth/callback")
                        ))
                )
                .retrieve()
                .body(KakaoTokenRes.class);

        log.info("{}", tokenRes);

        RestClient meClient = RestClient.builder()
                .baseUrl("https://kapi.kakao.com")
                .messageConverters(conv -> {
                    conv.addFirst(new MappingJackson2HttpMessageConverter());
                    conv.add(new StringHttpMessageConverter());
                })
                .build();

        assert tokenRes != null;
        String kakaoToken = tokenRes.getAccess_token();

        KakaoMeInfo meRes = meClient.get()
                .uri("/v2/user/me")
                .header("Authorization", "Bearer " + kakaoToken)
                .retrieve()
                .body(KakaoMeInfo.class)
            ;

        log.info("{}", meRes);

        Auth auth = new Auth();
        auth.setAccessToken("111");

        return auth;
    }
}
