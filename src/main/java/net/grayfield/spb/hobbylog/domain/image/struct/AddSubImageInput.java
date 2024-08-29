package net.grayfield.spb.hobbylog.domain.image.struct;

import lombok.Data;

@Data
public class AddSubImageInput {
    private String id;
    private String folder;
    private String url;
    private int subId;
}
