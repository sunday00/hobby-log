package net.grayfield.spb.hobbylog.domain.movie.struct;

import lombok.Data;

@Data
public class MovieUpdate {
    String id;
    String content;
    int ratings;
}
