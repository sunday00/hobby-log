package net.grayfield.spb.hobbylog.domain.movie;

import com.fasterxml.jackson.core.JsonProcessingException;
import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.movie.service.MovieService;
import net.grayfield.spb.hobbylog.domain.user.struct.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @MutationMapping
    public Role testRole (GraphQLContext context, DataFetchingEnvironment e) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("{}", authentication);

        authentication.getAuthorities().forEach(a -> log.info("{}", a.getAuthority()));


        return Role.ROLE_USER;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @QueryMapping
    public List<String> searchMovies (@Argument String search, @Argument Long page, DataFetchingEnvironment e) throws JsonProcessingException {
        this.movieService.searchMovieFromTMDB(search, page);
        return List.of("");
    }
}
