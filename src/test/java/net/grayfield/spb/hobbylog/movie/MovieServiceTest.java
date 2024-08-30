package net.grayfield.spb.hobbylog.movie;

import net.grayfield.spb.hobbylog.domain.movie.service.MovieService;
import net.grayfield.spb.hobbylog.domain.movie.struct.*;
import net.grayfield.spb.hobbylog.movie.mocks.MockMovieMongoRepository;
import net.grayfield.spb.hobbylog.movie.mocks.MockMovieTemplateRepository;
import net.grayfield.spb.hobbylog.share.mocks.MockFileSystemService;
import net.grayfield.spb.hobbylog.share.mocks.MockImageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;

import java.util.List;

@SpringBootTest
@AutoConfigureGraphQlTester
class MovieServiceTest {
    @Test
    void storeTest() {
        MockFileSystemService fileSystemService = new MockFileSystemService();
        MockImageService imageService = new MockImageService();
        RestClient movieRestClient = RestClient.builder().baseUrl("127.0.0.1").build();
        MockMovieMongoRepository movieRepository = new MockMovieMongoRepository();
        MockMovieTemplateRepository movieTemplateRepository = new MockMovieTemplateRepository(null);

        MovieService movieService = new MovieService(
                fileSystemService, imageService, movieRestClient,
                movieRepository, movieTemplateRepository
        );

        MovieInput movieInput = new MovieInput();
        movieInput.setMovieId(1L);
        movieInput.setRatings(10);

        MovieRawDetail detailRaw = new MovieRawDetail();
        detailRaw.setMovieId(1L);
        detailRaw.setTitle("test");
        detailRaw.setGenres(List.of());
        detailRaw.setProductionCompanies(List.of());
        detailRaw.setOriginalCountry(List.of());

        MovieRawCredit credit = new MovieRawCredit();
        credit.setCasts(List.of());
        credit.setCrews(List.of());

        MovieRawKeyword keyword = new MovieRawKeyword();
        keyword.setKeywords(List.of());

        movieService.<Movie>store(movieInput, detailRaw, detailRaw, credit, keyword);

        System.out.println("path::: " + movieTemplateRepository.called.getFirst());
        Assertions.assertEquals("abc/def/ghi", fileSystemService.path);
        Assertions.assertEquals("abc/def/ghi/movie1.png", imageService.path);
        Assertions.assertEquals("upsert movie via templateRepository", movieTemplateRepository.called.getFirst());
    }
}
