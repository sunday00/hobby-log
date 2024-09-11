package net.grayfield.spb.hobbylog.configs;

import lombok.RequiredArgsConstructor;
import net.grayfield.spb.hobbylog.aop.filter.PreRequestJwtFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecureConfig {
    @Value("${spring.security.cors.origin}")
    private String corsOrigin;

    private final PreRequestJwtFilter preRequestJwtFilter;

    CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setAllowedMethods(Collections.singletonList("*"));
        config.setAllowedOriginPatterns(Collections.singletonList(corsOrigin));
        config.setAllowCredentials(true);

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(config -> config.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(
                requests ->
                    requests
                        .requestMatchers("/graphql", "/graphiql", "/graphql/**").permitAll()
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/images/default/**").permitAll()
                        .requestMatchers("/images/upload/**").permitAll()
                        .anyRequest().denyAll()
            )
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(this.preRequestJwtFilter, UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
