package net.grayfield.spb.hobbylog.domain.movie.struct;

import lombok.*;
import net.grayfield.spb.hobbylog.domain.share.BaseSchema;
import net.grayfield.spb.hobbylog.domain.share.Category;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
@EqualsAndHashCode(callSuper = true)
public class Movie extends BaseSchema {
    Movie () {
        this.category = Category.MOVIE;
    }

    private Boolean adult;

    private String originalTitle;

    private String thumbnail;

    private Long budget;

    private Long revenue;

    private List<String> originalCountry;

    private String language;

    private String director;

    private List<String> actors;

    private List<Genre> genres;

    private List<String> keywords;

    private String synopsis;

    private String originalSynopsis;

    private String contents;

    private int stars;

    private float popularity;

    private float voteAverage;

    private Long voteCount;

    private List<String> productions;

    private String releaseDate;

    private int runtime;

    private String tagline;
}
