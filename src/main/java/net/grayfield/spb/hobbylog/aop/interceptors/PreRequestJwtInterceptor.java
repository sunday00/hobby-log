package net.grayfield.spb.hobbylog.aop.interceptors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.user.struct.Role;
import net.grayfield.spb.hobbylog.domain.user.struct.User;
import net.grayfield.spb.hobbylog.domain.user.struct.UserAuthentication;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PreRequestJwtInterceptor implements WebGraphQlInterceptor {
    @Override
    @NonNull
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        log.info("PreRequestJwtInterceptor");

        // TODO: create via request real user
        User user = new User();
        user.setId("qwe123");
        user.setUsername("sunday00");
        user.setProfileImage("");
        user.setEmail("sunday00@gmail.com");
        user.setVendor("kakao");
        user.setVendorId("6778");
        user.setRoles(List.of(Role.ROLE_USER));
        user.setIsActive(true);

        UserAuthentication authentication = new UserAuthentication(user);
        authentication.setAuthenticated(true);

        SecurityContextHolder.getContext().setAuthentication(authentication);


        return chain.next(request);
    }
}
