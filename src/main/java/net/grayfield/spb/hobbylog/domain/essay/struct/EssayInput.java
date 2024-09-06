package net.grayfield.spb.hobbylog.domain.essay.struct;

import lombok.Data;
import net.grayfield.spb.hobbylog.domain.share.struct.Status;
import org.springframework.lang.Nullable;

@Data
public class EssayInput {
    private String id;
    private String title;
    private String content;
    private WritingType writingType;
    private String thumbnail;

    @Nullable
    private String seriesKey;

    @Nullable
    private String seriesName;

    @Nullable
    private Status status;

    @Nullable
    private String logAtStr;
}
