package net.grayfield.spb.hobbylog.domain.auth.struct;

import lombok.Data;

@Data
public class KakaoTokenRes {
    private String access_token;
    private String refresh_token;
    private Integer expires_in;
}
