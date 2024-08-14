package net.grayfield.spb.hobbylog.domain.share;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Result {
    private Long id;

    private Boolean success;
}
