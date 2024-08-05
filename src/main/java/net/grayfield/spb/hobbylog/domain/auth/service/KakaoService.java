package net.grayfield.spb.hobbylog.domain.auth.service;

import net.grayfield.spb.hobbylog.domain.auth.struct.KakaoMeInfo;
import net.grayfield.spb.hobbylog.domain.auth.struct.KakaoTokenRes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class KakaoService {
    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    public KakaoTokenRes getKakaoToken(String code) {
        RestClient tokenClient = RestClient.builder()
                .baseUrl("https://kauth.kakao.com")
                .messageConverters(conv -> conv.addFirst(new MappingJackson2HttpMessageConverter()))
                .build();

        return tokenClient.post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(
                        CollectionUtils.toMultiValueMap(Map.of(
                                "code", List.of(code),
                                "client_id", List.of(clientId),
                                "grant_type", List.of("authorization_code"),
                                "redirect_uri", List.of(redirectUri)
                        ))
                )
                .retrieve()
                .body(KakaoTokenRes.class);
    }

    public KakaoMeInfo getKakaoMeInfo(String token) {
        RestClient meClient = RestClient.builder()
                .baseUrl("https://kapi.kakao.com")
                .messageConverters(conv -> {
                    conv.addFirst(new MappingJackson2HttpMessageConverter());
                    conv.add(new StringHttpMessageConverter());
                })
                .build();

        return meClient.get()
                .uri("/v2/user/me")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(KakaoMeInfo.class);
    }
}
