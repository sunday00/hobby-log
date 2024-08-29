package net.grayfield.spb.hobbylog.domain.share.struct;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Result {
    private String id;

    private Boolean success;

    private int status;

    private String message;
}
