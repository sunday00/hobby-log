package net.grayfield.spb.hobbylog.domain.movie.struct;

import lombok.Data;

import java.util.List;

@Data
public class MovieRawKeyword {
    private Long id;
    private List<Keyword> keywords;
}
