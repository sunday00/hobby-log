package net.grayfield.spb.hobbylog.domain.movie;

import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.user.struct.Role;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class MovieController {

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
    public Integer testRole (DataFetchingEnvironment e) {
        return 1;
    }
}
