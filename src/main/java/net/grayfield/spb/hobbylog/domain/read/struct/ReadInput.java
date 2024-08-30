package net.grayfield.spb.hobbylog.domain.read.struct;

import lombok.Data;
import net.grayfield.spb.hobbylog.domain.share.struct.Status;
import org.springframework.lang.Nullable;

@Data
public class ReadInput {
    private String id;
    private String writer;
    private String title;
    private String overview;
    private String content;
    private ReadType readType;
    private String thumbnail;
    private int ratings;

    @Nullable
    private Status status;

    @Nullable
    private String logAtStr;
}
