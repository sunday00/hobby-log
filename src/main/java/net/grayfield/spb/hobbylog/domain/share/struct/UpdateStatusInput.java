package net.grayfield.spb.hobbylog.domain.share.struct;

import lombok.Data;

@Data
public class UpdateStatusInput {
    private Category category;

    private Long id;

    private Status status;
}
