package net.grayfield.spb.hobbylog.domain.share.struct;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Result {
    private Long id;

    private Boolean success;
}
