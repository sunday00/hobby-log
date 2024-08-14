package net.grayfield.spb.hobbylog.domain.movie.struct;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Casting {
    private Long id;

    private Boolean adult;

    private int gender;

    @JsonProperty("known_for_department")
    private String department;

    private String name;

    @JsonProperty("original_name")
    private String originalName;

    private Float popularity;

    @JsonProperty("profile_path")
    private String profilePath;

    @JsonProperty("cast_id")
    private Long castId;

    private String character;

    @JsonProperty("credit_id")
    private String creditId;

    private int order;
}
