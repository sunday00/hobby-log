package net.grayfield.spb.hobbylog.domain.movie.struct;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;


@Data
class Item {
    Boolean adult;

    @JsonProperty("backdrop_path")
    String backdropPath;

    @JsonProperty("genre_ids")
    List<Integer> genreIds;

    Long id;

    @JsonProperty("original_language")
    String originalLanguage;

    @JsonProperty("original_title")
    String originalTitle;

    String overview;

    Float popularity;

    @JsonProperty("poster_path")
    String posterPath;

    @JsonProperty("release_date")
    String releaseDate;

    String title;

    Boolean video;

    @JsonProperty("vote_average")
    Float voteAverage;

    @JsonProperty("vote_count")
    Long voteCount;
}


@Data
public class MovieRawPage {
    private Long page;

    private List<Item> results;

    @JsonProperty("total_pages")
    private Long totalPages;

    @JsonProperty("total_results")
    private Long totalResults;
}
