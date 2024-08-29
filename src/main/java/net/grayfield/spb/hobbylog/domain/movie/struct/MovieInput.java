package net.grayfield.spb.hobbylog.domain.movie.struct;

import lombok.Data;
import net.grayfield.spb.hobbylog.domain.share.struct.Status;
import org.springframework.lang.Nullable;

@Data
public class MovieInput {
    String id;
    Long movieId;
    String content;
    int ratings;

    @Nullable
    String logAtStr;

    @Nullable
    Status status;
}
