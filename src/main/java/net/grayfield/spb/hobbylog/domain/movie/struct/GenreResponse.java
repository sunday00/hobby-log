package net.grayfield.spb.hobbylog.domain.movie.struct;

import lombok.Data;

import java.util.List;

@Data
public class GenreResponse {
    private List<Genre> genres;
}
