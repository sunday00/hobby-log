package net.grayfield.spb.hobbylog.domain.share.struct;

import lombok.Data;

@Data
public class UpdateStatusInput {
    private Category category;

    private String id;

    private Status status;
}
