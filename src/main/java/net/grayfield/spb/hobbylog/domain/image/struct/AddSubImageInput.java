package net.grayfield.spb.hobbylog.domain.image.struct;

import lombok.Data;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;
import org.springframework.lang.Nullable;

@Data
public class AddSubImageInput {
    private String id;
    private Category category;
    private String url;
    private String logAtStr;
    private int subId;

    @Nullable
    private Integer width;
}
