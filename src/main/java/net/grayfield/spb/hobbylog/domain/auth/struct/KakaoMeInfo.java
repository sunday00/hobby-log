package net.grayfield.spb.hobbylog.domain.auth.struct;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
class Profile {
    String nickname;

    @JsonProperty("thumbnail_image_url")
    String thumbnailImage;
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

    public String getEmail() {
        return this.kakaoAccount.getEmail();
    }

    public String getNickname() {
        return this.kakaoAccount.getProfile().getNickname();
    }

    public String getThumbnailImage() {
        return this.kakaoAccount.getProfile().getThumbnailImage();
    }
}
