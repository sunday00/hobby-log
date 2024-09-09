package net.grayfield.spb.hobbylog.aop.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.auth.service.JwtService;
import net.grayfield.spb.hobbylog.domain.user.service.UserService;
import net.grayfield.spb.hobbylog.domain.user.struct.Role;
import net.grayfield.spb.hobbylog.domain.user.struct.User;
import net.grayfield.spb.hobbylog.domain.user.struct.UserAuthentication;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Order(1)
@Component
@RequiredArgsConstructor
public class PreRequestJwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;

    private boolean isSkipQuery (ReadableRequestBodyWrapper wrapper) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(wrapper.getRequestBody(), new TypeReference<>() {});

        boolean unNeedle = map.get("query").toString().contains("mutation");

        List<String> query = Arrays.stream(map.get("query").toString()
                .replaceAll("\\{", " ")
                .replaceAll("\\}", " ")
                .replaceAll("\\(", " ")
                .replaceAll("\\)", " ")
                .replaceAll("(?U)\\s+", " ")
                .split(" ")).toList();

        Set<String> needle = Set.of("query", "sign", "code:", "accessToken");

        return query.containsAll(needle) && !unNeedle;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest req, @NotNull HttpServletResponse res, @NotNull FilterChain chain) throws ServletException, IOException {
        ReadableRequestBodyWrapper wrapper =
                new ReadableRequestBodyWrapper(req);

        wrapper.setAttribute("requestBody", wrapper.getRequestBody());

        String rawToken = req.getHeader("Authorization");
        if (rawToken == null || this.isSkipQuery(wrapper)) {
            User user = new User();
            user.setRoles(List.of(Role.ROLE_GUEST));

            UserAuthentication authentication = new UserAuthentication(user);
            authentication.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(wrapper, res);
            return;
        }

        String token = rawToken.replaceFirst("[b|B]earer ", "");
        String userId = this.jwtService.getUserIdFromJwtToken(token);
        User user = this.userService.createUserSessionById(userId);

        UserAuthentication authentication = new UserAuthentication(user);
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(wrapper, res);
    }
}
