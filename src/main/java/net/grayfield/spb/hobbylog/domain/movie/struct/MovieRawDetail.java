package net.grayfield.spb.hobbylog.domain.movie.struct;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
class BelongsCollection {
    private Long id;
    private String name;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("backdrop_path")
    private String backdropPath;
}

@Data
class Production  {
    private Long id;

    private String name;

    @JsonProperty("logo_path")
    private String logoPath;

    @JsonProperty("origin_country")
    private String originCountry;
}

@Data
class ProductionCountry {
    @JsonProperty("iso_3166_1")
    private String name;

    @JsonProperty("name")
    private String fullname;
}

@Data
class SpokenLanguages {
    @JsonProperty("iso_639_1")
    private String name;

    @JsonProperty("english_name")
    private String country;

    @JsonProperty("name")
    private String language;
}

@Data
public class MovieRawDetail {
    private Long id;

    @JsonProperty("imdb_id")
    private String imdbId;

    private Boolean adult;

    private Boolean video;

    @JsonProperty("backdrop_path")
    private String backdropPath;

    @JsonProperty("belongs_to_collection")
    private BelongsCollection belongsCollection;

    private Long budget;

    private Long revenue;

    private List<Genre> genres;

    private String homepage;

    @JsonProperty("origin_country")
    private List<String> originalCountry;

    @JsonProperty("original_language")
    private String originalLanguage;

    private String title;

    @JsonProperty("original_title")
    private String originalTitle;

    private String overview;

    private float popularity;

    @JsonProperty("vote_average")
    private float voteAverage;

    @JsonProperty("vote_count")
    private Long voteCount;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("production_companies")
    private List<Production> productionCompanies;

    @JsonProperty("release_date")
    private String releaseDate;

    private Long runtime;

    @JsonProperty("spoken_languages")
    private List<SpokenLanguages> spokenLanguages;

    private String status;

    private String tagline;
}
