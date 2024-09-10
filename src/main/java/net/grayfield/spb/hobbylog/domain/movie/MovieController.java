package net.grayfield.spb.hobbylog.domain.movie;

import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.movie.service.MovieService;
import net.grayfield.spb.hobbylog.domain.movie.struct.*;
import net.grayfield.spb.hobbylog.domain.share.struct.Result;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    @PreAuthorize("hasRole('ROLE_WRITER')")
    @MutationMapping
    public Result logMovie (
            @Argument MovieInput movieInput,
            GraphQLContext context, DataFetchingEnvironment e
    ) {
        MovieRawDetail koDetailRaw = this.movieService.getMovieDetail(movieInput.getMovieId(), "ko-KR");
        MovieRawDetail enDetailRaw = this.movieService.getMovieDetail(movieInput.getMovieId(), "en-US");

        MovieRawCredit creditRaw = this.movieService.getMovieCredits(movieInput.getMovieId(), "ko-KR");
        MovieRawKeyword keywordRaw = this.movieService.getMovieKeywords(movieInput.getMovieId());

        this.movieService.storeRemote(movieInput.getMovieId(), movieInput.getRatings());

        //TODO: add review remote TMDB

        return this.movieService.store(
                movieInput,
                koDetailRaw, enDetailRaw, creditRaw, keywordRaw
        );
    }

    @PreAuthorize("hasRole('ROLE_WRITER')")
    @QueryMapping
    public MovieRawPage searchMovies (
            @Argument String search, @Argument Long page,
            DataFetchingEnvironment e
    ) {
        return this.movieService.searchMovieFromTMDB(search, page);
    }

    @QueryMapping
    public Movie getOneMovie(
            @Argument String id,
            GraphQLContext context, DataFetchingEnvironment e
    ) {
        return this.movieService.getOneMovie(id, e.getSelectionSet().getFields("subImages"));
    }

    @PreAuthorize("hasRole('ROLE_WRITER')")
    @MutationMapping
    public Result updateMovie (
            @Argument MovieInput movieInput,
            GraphQLContext context, DataFetchingEnvironment e
    ) {
        Movie movie = this.movieService.updateOneMovie(movieInput);

        this.movieService.storeRemote(movie.getMovieId(), movie.getRatings());

        return Result.builder().id(movie.getId()).success(true).build();
    }
}
