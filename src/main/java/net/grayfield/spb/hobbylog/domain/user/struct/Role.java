package net.grayfield.spb.hobbylog.domain.user.struct;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public enum Role implements GrantedAuthority {
    ROLE_GUEST(0), ROLE_USER(1), ROLE_WRITER(2), ROLE_ADMIN(3) ;

    private final Integer lv;

    Role(Integer lv) { this.lv = lv; }

    @Override
    public String getAuthority() {
        return this.name();
    }
}
