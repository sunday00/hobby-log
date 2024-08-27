package net.grayfield.spb.hobbylog.domain.movie.struct;

import lombok.Data;

@Data
public class MovieInput {
    String id;
    Long movieId;
    String content;
    int ratings;
    String logAtStr;
}
