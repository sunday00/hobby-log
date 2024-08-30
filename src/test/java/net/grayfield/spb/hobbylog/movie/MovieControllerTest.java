package net.grayfield.spb.hobbylog.movie;

import net.grayfield.spb.hobbylog.domain.movie.MovieController;
import net.grayfield.spb.hobbylog.domain.movie.struct.MovieInput;
import net.grayfield.spb.hobbylog.movie.mocks.MockMovieService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

@SpringBootTest
@AutoConfigureGraphQlTester
class MovieControllerTest {
    @Test
    void logMovieTest() {
        MockMovieService service = new MockMovieService();

        MovieController movieController = new MovieController(service);

        MovieInput movieInput = new MovieInput();
        movieInput.setMovieId(1L);
        movieInput.setRatings(10);

        movieController.logMovie(movieInput, null, null);

        System.out.println(service.called);

        Assertions.assertArrayEquals(
                service.called.toArray(),
                List.of("detail", "detail", "credit", "keyword", "storeRemote", "store").toArray()
        );
    }
}
