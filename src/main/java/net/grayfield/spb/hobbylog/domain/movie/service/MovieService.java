package net.grayfield.spb.hobbylog.domain.movie.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.image.ImageService;
import net.grayfield.spb.hobbylog.domain.movie.repository.MovieRepository;
import net.grayfield.spb.hobbylog.domain.movie.repository.MovieTemplateRepository;
import net.grayfield.spb.hobbylog.domain.movie.struct.*;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;
import net.grayfield.spb.hobbylog.domain.share.struct.Result;
import net.grayfield.spb.hobbylog.domain.share.struct.Status;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieService {
    @Value("${tmdb.api_key}")
    private String tmdbApiKey;

    @Value("${tmdb.api_token}")
    private String tmdbApiToken;

    private final ImageService imageService;
    private final RestClient movieBaseClient;
    private final MovieRepository movieRepository;
    private final MovieTemplateRepository movieTemplateRepository;

    public MovieRawPage searchMovieFromTMDB(String search, Long page) {
        return movieBaseClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/3/search/movie")
                        .queryParam("query", search.replace(" ", "+"))
                        .queryParam("language", "ko")
                        .queryParam("api_key", tmdbApiKey)
                        .queryParam("page", page)
                        .build()
                )
                .retrieve()
                .body(MovieRawPage.class);
    }

    public MovieRawDetail getMovieDetail(Long movieId, String language) {
        MovieRawDetail result = null;

        try {
            result = this.movieBaseClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/3/movie/" + movieId)
                            .queryParam("language", language)
                            .build()
                    )
                    .header("Authorization", "Bearer " + tmdbApiToken)
                    .retrieve()
                    .body(MovieRawDetail.class);
        } catch (Exception e) {
            log.error(Arrays.toString(e.getStackTrace()));
        }

        return result;
    }

    public MovieRawCredit getMovieCredits(Long movieId, String language) {
        MovieRawCredit result = null;

        try {
            result = this.movieBaseClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/3/movie/" + movieId + "/credits")
                            .queryParam("language", language)
                            .build()
                    )
                    .header("Authorization", "Bearer " + tmdbApiToken)
                    .retrieve()
                    .body(MovieRawCredit.class);
        } catch (Exception ex) {
            log.error("{}", Arrays.toString(ex.getStackTrace()));
            log.error(String.valueOf(ex.getCause()));
        }
        return result;
    }

    public MovieRawKeyword getMovieKeywords(Long movieId) {
        return this.movieBaseClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/3/movie/" + movieId + "/keywords")
                        .build()
                )
                .header("Authorization", "Bearer " + tmdbApiToken)
                .retrieve()
                .body(MovieRawKeyword.class);
    }

    public Result store(
            Long id,
            MovieRawDetail koDetailRaw, MovieRawDetail enDetailRaw, MovieRawCredit creditRaw, MovieRawKeyword keywordRaw,
            String content, Integer ratings
    ) {
        try {
            List<Crew> directors = creditRaw.getCrews().stream().filter(c -> c.getJob().equals("Director")).toList();
            List<Casting> castings = creditRaw.getCasts().stream().filter(c -> c.getDepartment().equals("Acting")).toList();
            List<String> genres = koDetailRaw.getGenres().stream().map(Genre::getName).toList();
            List<String> keywords = keywordRaw.getKeywords().stream().map(Keyword::getName).toList();
            List<String> productions = enDetailRaw.getProductionCompaniesName();

            Movie movie = new Movie();
            movie.setId(id);

            String localPosterImage = this.imageService.storeFromUrl(
                    "https://image.tmdb.org/t/p/w200"
                        + koDetailRaw.getPosterPath(),
                    movie
            );

            movie.setCategory(Category.MOVIE);
            movie.setAdult(koDetailRaw.getAdult());
            movie.setTitle(koDetailRaw.getTitle());
            movie.setOriginalTitle(enDetailRaw.getOriginalTitle());
            movie.setThumbnail(localPosterImage);
            movie.setBudget(koDetailRaw.getBudget());
            movie.setRevenue(koDetailRaw.getRevenue());
            movie.setOriginalCountry(enDetailRaw.getOriginalCountry());
            movie.setLanguage(koDetailRaw.getOriginalLanguage());
            movie.setDirectors(directors);
            movie.setActors(castings);
            movie.setGenres(genres);
            movie.setKeywords(keywords);
            movie.setSynopsis(koDetailRaw.getOverview());
            movie.setOriginalSynopsis(enDetailRaw.getOverview());
            movie.setContents(content);
            movie.setRatings(ratings);
            movie.setPopularity(koDetailRaw.getPopularity());
            movie.setVoteAverage(koDetailRaw.getVoteAverage());
            movie.setVoteCount(koDetailRaw.getVoteCount());
            movie.setReleaseDate(koDetailRaw.getReleaseDate());
            movie.setProductions(productions);
            movie.setRuntime(koDetailRaw.getRuntime());
            movie.setTagline(koDetailRaw.getTagline());
            movie.setOriginalTagline(enDetailRaw.getTagline());
            movie.setStatus(Status.DRAFT);

            this.movieTemplateRepository.upsertMovie(movie);

            return Result.builder().id(id).success(true).build();
        } catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(Arrays.toString(ex.getStackTrace()));
            return Result.builder().id(0L).success(false).build();
        }
    }

    public void storeRemote(Long id, Integer ratings) {
        float rating = ratings.floatValue() / 10;
        float underOne = rating - (ratings / 10);

        if (underOne < 0.3) {
            rating = ratings / 10;
        } else if (underOne < 0.7) {
            rating = ratings / 10 + 0.5f;
        } else {
            rating = ratings / 10 + 1f;
        }

        this.movieBaseClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/3/movie/" + id + "/rating")
                        .build()
                )
                .body(Map.of("value", rating))
                .header("Authorization", "Bearer " + tmdbApiToken)
                .retrieve()
            ;
    }
}
