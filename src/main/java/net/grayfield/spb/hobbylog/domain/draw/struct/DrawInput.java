package net.grayfield.spb.hobbylog.domain.draw.struct;

import lombok.Data;
import net.grayfield.spb.hobbylog.domain.share.struct.Status;
import org.springframework.lang.Nullable;

@Data
public class DrawInput {
    private String id;
    private String title;
    private String content;
    private String thumbnail;

    private String mainImage;
    private DrawType drawType;

    @Nullable
    private Status status;

    @Nullable
    private String logAtStr;
}
