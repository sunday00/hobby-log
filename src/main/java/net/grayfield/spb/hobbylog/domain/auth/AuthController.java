package net.grayfield.spb.hobbylog.domain.auth;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.auth.service.JwtService;
import net.grayfield.spb.hobbylog.domain.auth.service.KakaoService;
import net.grayfield.spb.hobbylog.domain.auth.struct.Auth;
import net.grayfield.spb.hobbylog.domain.auth.struct.KakaoMeInfo;
import net.grayfield.spb.hobbylog.domain.auth.struct.KakaoTokenRes;
import net.grayfield.spb.hobbylog.domain.user.service.UserService;
import net.grayfield.spb.hobbylog.domain.user.struct.User;
import net.grayfield.spb.hobbylog.domain.user.struct.UserAuthentication;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@AllArgsConstructor
public class AuthController {
    private final KakaoService kakaoService;
    private final UserService userService;
    private final JwtService jwtService;

    @QueryMapping
    public Auth sign (@Argument String code) {
        KakaoTokenRes tokenRes = kakaoService.getKakaoToken(code);

        assert tokenRes != null;
        String kakaoToken = tokenRes.getAccess_token();

        KakaoMeInfo meRes = kakaoService.getKakaoMeInfo(kakaoToken);

        User user = userService.findOneOrCreateByEmail(meRes);

        log.info("{}", user);

        UserAuthentication userAuthentication = new UserAuthentication(user);
        String accessToken = jwtService.generateJwtToken(userAuthentication);

        Auth auth = new Auth();
        auth.setAccessToken(accessToken);

        return auth;
    }
}
