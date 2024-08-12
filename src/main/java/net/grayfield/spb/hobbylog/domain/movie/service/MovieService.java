package net.grayfield.spb.hobbylog.domain.movie.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.movie.struct.MovieRawDetail;
import net.grayfield.spb.hobbylog.domain.movie.struct.MovieRawPage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieService {
    @Value("${tmdb.api_key}")
    private String tmdbApiKey;

    @Value("${tmdb.api_token}")
    private String tmdbApiToken;

    private final RestClient movieBaseClient;

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
        MovieRawDetail result = new MovieRawDetail();

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
}
