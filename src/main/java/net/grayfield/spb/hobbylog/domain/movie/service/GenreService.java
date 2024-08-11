package net.grayfield.spb.hobbylog.domain.movie.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.movie.repository.GenreRepository;
import net.grayfield.spb.hobbylog.domain.movie.repository.GenreTemplateRepository;
import net.grayfield.spb.hobbylog.domain.movie.struct.GenreResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    @Value("${tmdb.api_key}")
    private String tmdbApiKey;

    @Value("${tmdb.api_token}")
    private String tmdbApiToken;

    private final GenreRepository genreRepository;
    private final GenreTemplateRepository genreTemplateRepository;

    public GenreResponse getGenres () {
        RestClient client = RestClient.builder()
                .baseUrl("https://api.themoviedb.org")
                .messageConverters(conv -> {
                    conv.addFirst(new MappingJackson2HttpMessageConverter());
                    conv.add(new StringHttpMessageConverter());
                })
                .build();

        GenreResponse result = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/3/genre/movie/list")
                        .queryParam("language", "en")
                        .build()
                )
                .header("Authorization", "Bearer " + tmdbApiToken)
                .retrieve()
                .body(GenreResponse.class);

        // TODO: store mongo

        assert result != null;

        this.genreTemplateRepository.upsertGenres(result.getGenres());
        // get genre from mongo
        // when null, find from this api.

        return result;
    }
}
