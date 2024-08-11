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

    private String thumbnail;

    private String director;

    private List<String> actors;

    private String mainGenre;

    private List<String> keywords;

    private String synopsis;

    private String contents;

    private int stars;
}
