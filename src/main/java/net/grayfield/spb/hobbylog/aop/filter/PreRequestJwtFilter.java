package net.grayfield.spb.hobbylog.aop.filter;

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
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Order(1)
@Component
@RequiredArgsConstructor
public class PreRequestJwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        String rawToken = req.getHeader("Authorization");
        if (rawToken == null) {
            User user = new User();
            user.setRoles(List.of(Role.ROLE_GUEST));

            UserAuthentication authentication = new UserAuthentication(user);
            authentication.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(req, res);
            return;
        }

        String token = rawToken.replaceFirst("[b|B]earer ", "");
        String userId = this.jwtService.getUserIdFromJwtToken(token);
        User user = this.userService.createUserSessionById(userId);

        UserAuthentication authentication = new UserAuthentication(user);
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(req, res);
    }
}
