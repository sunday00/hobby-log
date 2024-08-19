package net.grayfield.spb.hobbylog.domain.movie.struct;

import lombok.*;
import net.grayfield.spb.hobbylog.domain.share.struct.BaseSchema;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
@EqualsAndHashCode(callSuper = true)
public class Movie extends BaseSchema {
    public Movie () {
        this.category = Category.MOVIE;
    }

    private Boolean adult;

    private String originalTitle;

    private Long budget;

    private Long revenue;

    private List<String> originalCountry;

    private String language;

    private List<Crew> directors;

    private List<Casting> actors;

    private List<String> genres;

    private List<String> keywords;

    private String synopsis;

    private String originalSynopsis;

    private String contents;

    private float popularity;

    private float voteAverage;

    private Long voteCount;

    private List<String> productions;

    private String releaseDate;

    private int runtime;

    private String tagline;

    private String originalTagline;
}
