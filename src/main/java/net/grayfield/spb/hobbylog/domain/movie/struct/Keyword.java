package net.grayfield.spb.hobbylog.domain.movie.struct;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Keyword {
    private Long id;

    private String name;
}
