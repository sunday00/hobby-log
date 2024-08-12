package net.grayfield.spb.hobbylog.domain.movie;

import com.fasterxml.jackson.core.JsonProcessingException;
import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.movie.service.MovieService;
import net.grayfield.spb.hobbylog.domain.movie.struct.MovieRawDetail;
import net.grayfield.spb.hobbylog.domain.movie.struct.MovieRawPage;
import net.grayfield.spb.hobbylog.domain.share.Result;
import net.grayfield.spb.hobbylog.domain.user.struct.Role;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @MutationMapping
    public Result logMovie (@Argument Long id, GraphQLContext context, DataFetchingEnvironment e) throws JsonProcessingException {
        MovieRawDetail koDetail = this.movieService.getMovieDetail(id, "ko-KR");
        MovieRawDetail enDetail = this.movieService.getMovieDetail(id, "en-US");

        // TODO: get credit https://developer.themoviedb.org/reference/movie-credits
        // TODO: get keywords


        return Result.builder()
                .success(true)
                .build();
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @QueryMapping
    public MovieRawPage searchMovies (@Argument String search, @Argument Long page, DataFetchingEnvironment e) throws JsonProcessingException {
        return this.movieService.searchMovieFromTMDB(search, page);
    }


}
