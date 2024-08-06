package net.grayfield.spb.hobbylog.domain.user.struct;

import lombok.Getter;

@Getter
public enum Role {
    GUEST(0), USER(1), ADMIN(2);

    private final Integer lv;

    Role(Integer lv) { this.lv = lv; }
}
