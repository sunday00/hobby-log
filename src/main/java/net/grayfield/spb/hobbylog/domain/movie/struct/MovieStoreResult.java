package net.grayfield.spb.hobbylog.domain.movie.struct;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MovieStoreResult {
    private Boolean success;
    private Long id;
}
