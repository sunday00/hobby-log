package net.grayfield.spb.hobbylog.domain.auth.struct;

import lombok.Data;

@Data
public class Auth {
    private String accessToken;
    // TODO: refresh token
}
