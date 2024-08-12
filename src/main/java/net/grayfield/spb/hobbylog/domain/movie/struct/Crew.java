package net.grayfield.spb.hobbylog.domain.movie.struct;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Crew {
    private Long id;

    private Boolean adult;

    private int gender;

    private String name;

    @JsonProperty("original_name")
    private String originalName;

    private Float popularity;

    @JsonProperty("profile_path")
    private String profilePath;

    @JsonProperty("credit_id")
    private String creditId;

    private String department;

    private String job;
}
