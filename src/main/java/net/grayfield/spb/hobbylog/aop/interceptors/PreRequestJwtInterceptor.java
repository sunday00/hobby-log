package net.grayfield.spb.hobbylog.aop.interceptors;

import com.fasterxml.jackson.core.JsonProcessingException;
import graphql.*;
import graphql.language.Document;
import graphql.language.Field;
import graphql.language.SelectionSet;
import graphql.language.SourceLocation;
import graphql.parser.Parser;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.aop.catcher.SignWithOtherException;
import net.grayfield.spb.hobbylog.domain.auth.service.JwtService;
import net.grayfield.spb.hobbylog.domain.user.service.UserService;
import net.grayfield.spb.hobbylog.domain.user.struct.Role;
import net.grayfield.spb.hobbylog.domain.user.struct.User;
import net.grayfield.spb.hobbylog.domain.user.struct.UserAuthentication;
import org.jetbrains.annotations.NotNull;
import org.springframework.graphql.ExecutionGraphQlResponse;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.graphql.support.DefaultExecutionGraphQlResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@RequiredArgsConstructor
public class PreRequestJwtInterceptor implements WebGraphQlInterceptor {
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    @NonNull
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, @NotNull Chain chain) {
        AtomicReference<Boolean> isDocumentHasSignPath = new AtomicReference<>(false);
        AtomicReference<Integer> selectionSetSize = new AtomicReference<>(0);
        AtomicReference<SourceLocation> signLocation = new AtomicReference<>(SourceLocation.EMPTY);

        Document doc = Parser.parse(request.getDocument());
        doc.getDefinitions().forEach(d -> {
            List<SelectionSet> s = d.getNamedChildren().getChildren("selectionSet");
            SelectionSet f = s.getFirst();
            f.getSelections().forEach(tss -> {
                Field ss = (Field) tss;
                String name = ss.getName();

                selectionSetSize.set(selectionSetSize.get() + f.getSelections().size());

                if (name.equals("sign")) {
                    isDocumentHasSignPath.set(true);
                    signLocation.set(ss.getSourceLocation());
                }
            });
        });

        return chain.next(request)
                .doFirst(() -> {
                    if ((doc.getDefinitions().size() > 1 || selectionSetSize.get() > 1) && Boolean.TRUE.equals(isDocumentHasSignPath.get())) {
                        throw new SignWithOtherException("Sign method should triggered Alone.");
                    } else if (doc.getDefinitions().size() == 1 && Boolean.TRUE.equals(isDocumentHasSignPath.get())) {
                        return;
                    }

                    String jwtToken = Objects.requireNonNull(request.getHeaders().getFirst("Authorization")).replaceFirst("[b|B]aerer ", "");
                    log.info("jwtToken: {}", jwtToken);

                    String userId = this.jwtService.getUserIdFromJwtToken(jwtToken);
                    log.info("userId: {}", userId);

                    User user = this.userService.getUserFromSession(userId);

                    if (user == null) {
                        user = this.userService.findOneById(userId);
                        try {
                            this.userService.createUserSession(user);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    
                    UserAuthentication authentication = new UserAuthentication(user);
                    authentication.setAuthenticated(true);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                })
                .onErrorResume(SignWithOtherException.class, ex -> {
                    GraphQLError err = GraphQLError.newError()
                            .message(ex.getMessage())
                            .path(List.of("sign"))
                            .location(signLocation.get())
                            .build();
                    ExecutionResult result = new ExecutionResultImpl(err);
                    ExecutionGraphQlResponse egr = new DefaultExecutionGraphQlResponse(request.toExecutionInput(), result);
                    return Mono.just(new WebGraphQlResponse(egr));
                })
                .onErrorResume(ex -> {
                    GraphQLError err = GraphQLError.newError()
                            .message(ex.getMessage())
                            .location(signLocation.get())
                            .build();
                    ExecutionResult result = new ExecutionResultImpl(err);
                    ExecutionGraphQlResponse egr = new DefaultExecutionGraphQlResponse(request.toExecutionInput(), result);
                    return Mono.just(new WebGraphQlResponse(egr));
                });
    }
}
