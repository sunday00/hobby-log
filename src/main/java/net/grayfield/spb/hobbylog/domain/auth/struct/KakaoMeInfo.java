package net.grayfield.spb.hobbylog.domain.auth.struct;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
class Profile {
    String nickname;
    String thumbnail_image_url;
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
