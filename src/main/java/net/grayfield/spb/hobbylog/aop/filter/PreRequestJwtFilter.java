package net.grayfield.spb.hobbylog.aop.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.language.Document;
import graphql.language.Field;
import graphql.language.SelectionSet;
import graphql.parser.Parser;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.aop.catcher.SignWithOtherException;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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

        List<String> ableToSkipQuery = List.of("sign", "testQuery");

        AtomicBoolean hasSignQuery = new AtomicBoolean(false);
        AtomicBoolean hasOnlyAbleToSkipJwt = new AtomicBoolean(true);
        AtomicInteger selectFields = new AtomicInteger();

        Document doc = Parser.parse(map.get("query").toString());
        doc.getDefinitions().forEach(definition -> {
            List<SelectionSet> selectionSets = definition.getNamedChildren().getChildren("selectionSet");
            selectionSets.forEach(selectionSet -> selectionSet.getSelections().forEach(selection -> {
                Field field = (Field) selection;
                if( field.getName().equals("sign")) hasSignQuery.set(true);
                if( !ableToSkipQuery.contains(field.getName()) ) hasOnlyAbleToSkipJwt.set(false);
                selectFields.getAndIncrement();
            }));
        });

        if(hasSignQuery.get() && selectFields.get() > 1) {
            throw new SignWithOtherException("Sign method should triggered Alone.");
        }

        return hasOnlyAbleToSkipJwt.get();
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

            this.setUserSecurityContext(user);

            chain.doFilter(wrapper, res);
            return;
        }

        try {
            String token = rawToken.replaceFirst("[b|B]earer ", "");
            String userId = this.jwtService.getUserIdFromJwtToken(token);
            User user = this.userService.createUserSessionById(userId);

            this.setUserSecurityContext(user);
        } catch (Exception ex) {
            log.error(ex.getMessage());

            User user = new User();
            user.setRoles(List.of(Role.ROLE_GUEST));
            this.setUserSecurityContext(user);
        }

        chain.doFilter(wrapper, res);
    }

    private void setUserSecurityContext(User user) {
        UserAuthentication authentication = new UserAuthentication(user);
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
