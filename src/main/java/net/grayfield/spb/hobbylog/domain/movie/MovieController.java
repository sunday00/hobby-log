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

    @PreAuthorize("hasRole('ROLE_USER')")
    @MutationMapping
    public Result logMovie (
            @Argument Long movieId, @Argument String content, @Argument Integer ratings,
            GraphQLContext context, DataFetchingEnvironment e
    ) {
        MovieRawDetail koDetailRaw = this.movieService.getMovieDetail(movieId, "ko-KR");
        MovieRawDetail enDetailRaw = this.movieService.getMovieDetail(movieId, "en-US");

        MovieRawCredit creditRaw = this.movieService.getMovieCredits(movieId, "ko-KR");
        MovieRawKeyword keywordRaw = this.movieService.getMovieKeywords(movieId);

        this.movieService.storeRemote(movieId, ratings);

        //TODO: add review remote TMDB

        return this.movieService.store(
                movieId,
                koDetailRaw, enDetailRaw, creditRaw, keywordRaw,
                content, ratings
        );
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @QueryMapping
    public MovieRawPage searchMovies (
            @Argument String search, @Argument Long page,
            DataFetchingEnvironment e
    ) {
        return this.movieService.searchMovieFromTMDB(search, page);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @QueryMapping
    public Movie getOneMovie(
            @Argument String id,
            GraphQLContext context, DataFetchingEnvironment e
    ) {
        return this.movieService.getOneMovie(id);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @MutationMapping
    public Result updateMovie (
            @Argument MovieUpdate movieUpdate,
            GraphQLContext context, DataFetchingEnvironment e
    ) {
        Movie movie = this.movieService.updateOneMovie(movieUpdate);

        this.movieService.storeRemote(movie.getMovieId(), movieUpdate.getRatings());

        return Result.builder().id(movieUpdate.getId()).success(true).build();
    }
}
