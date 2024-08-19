package net.grayfield.spb.hobbylog.domain.image.struct;

import lombok.Data;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;

@Data
public class AddSubImageInput {
    private Category category;
    private Long id;
    private String url;
    private int serial;
}
