package net.grayfield.spb.hobbylog.domain.auth.struct;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
class Profile {
    String nickname;

}

@Data
class KakaoAccount {
    @JsonProperty("profile")
    Profile profile;

    String email;
}

@Data
public class KakaoMeInfo {
    String id;

    @JsonProperty("kakao_account")
    KakaoAccount kakaoAccount;
}


//{
//    id=2953955725,
//    kakao_account={
//            profile={
//                    nickname=무한일요일(김종현),
//                    is_default_nickname=false
//            },
//            has_email=true,
//            email=grayfield00@naver.com
//    }
//}
