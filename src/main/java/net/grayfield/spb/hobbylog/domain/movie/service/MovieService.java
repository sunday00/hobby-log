package net.grayfield.spb.hobbylog.domain.movie.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.movie.repository.MovieRepository;
import net.grayfield.spb.hobbylog.domain.movie.struct.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
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

    private final RestClient movieBaseClient;
    private final MovieRepository movieRepository;

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

    public MovieStoreResult store(
            MovieRawDetail koDetailRaw, MovieRawDetail enDetailRaw, MovieRawCredit creditRaw, MovieRawKeyword keywordRaw,
            String content, Integer stars
    ) {
        try {
            List<Crew> directors = creditRaw.getCrews().stream().filter(c -> c.getJob().equals("Director")).findAny().stream().toList();
            List<Casting> castings = creditRaw.getCasts().stream().filter(c -> c.getDepartment().equals("Acting")).findAny().stream().toList();

            //TODO: genres from mongodb first

            Movie movie = new Movie();
            movie.setAdult(koDetailRaw.getAdult());
            movie.setOriginalTitle(enDetailRaw.getOriginalTitle());
            movie.setThumbnail(koDetailRaw.getPosterPath());
            movie.setBudget(koDetailRaw.getBudget());
            movie.setRevenue(koDetailRaw.getRevenue());
            movie.setOriginalCountry(enDetailRaw.getOriginalCountry());
            movie.setLanguage(koDetailRaw.getOriginalLanguage());
            movie.setDirectors(directors);
            movie.setActors(castings);
            movie.setGenres(koDetailRaw.getGenres().stream().map(Genre::getName).toList());

            return MovieStoreResult.builder().success(true).build();
        } catch (Exception ex) {
            return MovieStoreResult.builder().id(0L).success(false).build();
        }
    }
}
