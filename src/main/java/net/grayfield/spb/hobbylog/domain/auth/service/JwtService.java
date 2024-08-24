package net.grayfield.spb.hobbylog.domain.auth.service;

import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.user.struct.UserAuthentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.ExecutionGraphQlResponse;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.graphql.support.DefaultExecutionGraphQlResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Getter
    @Value("${jwt.ttl}")
    private Integer ttl;

    public String generateJwtToken(UserAuthentication user) {
        return Jwts.builder()
                .subject(user.getId())
                .claim("name", user.getName())
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + (ttl * 1000)))
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUserIdFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    public Mono<WebGraphQlResponse> errorResult (WebGraphQlRequest request, Exception ex, String path, SourceLocation location) {
        GraphQLError.Builder<?> errBuilder = GraphQLError.newError()
                .message(ex.getMessage())
                .location(location);

        if (path != null) {
            errBuilder.path(List.of(path));
        }

        GraphQLError err = errBuilder.build();

        ExecutionResult result = new ExecutionResultImpl(err);
        ExecutionGraphQlResponse egr = new DefaultExecutionGraphQlResponse(request.toExecutionInput(), result);
        return Mono.just(new WebGraphQlResponse(egr));
    }
}
