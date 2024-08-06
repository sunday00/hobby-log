package net.grayfield.spb.hobbylog.domain.user.struct;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public enum Role implements GrantedAuthority {
    GUEST(0), USER(1), ADMIN(2);

    private final Integer lv;

    Role(Integer lv) { this.lv = lv; }

    @Override
    public String getAuthority() {
        return this.name();
    }
}
