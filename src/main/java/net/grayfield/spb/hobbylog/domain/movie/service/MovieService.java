package net.grayfield.spb.hobbylog.domain.movie.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.movie.struct.Genre;
import net.grayfield.spb.hobbylog.domain.movie.struct.MovieRawPage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieService {
    @Value("${tmdb.api_key}")
    private String tmdbApiKey;
    //https://api.themoviedb.org/3/search/movie?query=Jack+Reacher&api_key=API_KEY

    private final GenreService genreService;

    public void searchMovieFromTMDB(String search, Long page) throws JsonProcessingException {
        RestClient client = RestClient.builder()
                .baseUrl("https://api.themoviedb.org")
                .messageConverters(conv -> {
                    conv.addFirst(new MappingJackson2HttpMessageConverter());
                    conv.add(new StringHttpMessageConverter());
                })
                .build();

        MovieRawPage result = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/3/search/movie")
                        .queryParam("query", search.replace(" ", "+"))
                        .queryParam("api_key", tmdbApiKey)
                        .queryParam("page", page)
                        .build()
                )
                .retrieve()
                .body(MovieRawPage.class);

//        ObjectMapper mapper = new ObjectMapper();
//        log.info("{}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result));
        log.info("{}", result);

        List<Genre> genreList = this.genreService.getGenres().getGenres();
    }
}
