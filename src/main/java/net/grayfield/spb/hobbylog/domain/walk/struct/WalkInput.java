package net.grayfield.spb.hobbylog.domain.walk.struct;

import lombok.Data;
import net.grayfield.spb.hobbylog.domain.share.struct.Status;
import org.springframework.lang.Nullable;

@Data
public class WalkInput {
    private String id;
    private String title;
    private String content;
    private String thumbnail;

    private int steps;
    private int distance;
    private int duration;

    @Nullable
    private Status status;

    @Nullable
    private String logAtStr;
}
