package net.grayfield.spb.hobbylog.domain.movie.struct;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class MovieRawCredit {
    private Long id;

    @JsonProperty("cast")
    private List<Casting> casts;

    @JsonProperty("crew")
    private List<Crew> crews;
}
