package net.grayfield.spb.hobbylog.aop.interceptors;

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
import net.grayfield.spb.hobbylog.domain.user.struct.User;
import net.grayfield.spb.hobbylog.domain.user.struct.UserAuthentication;
import org.jetbrains.annotations.NotNull;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
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
        AtomicReference<Boolean> isGraphqlPath = new AtomicReference<>(false);
        AtomicReference<Integer> selectionSetSize = new AtomicReference<>(0);
        AtomicReference<SourceLocation> signLocation = new AtomicReference<>(SourceLocation.EMPTY);

        if(Objects.equals(request.getOperationName(), "IntrospectionQuery")) {
            isGraphqlPath.set(true);
        }

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
                    } else if (doc.getDefinitions().size() == 1 && (Boolean.TRUE.equals(isDocumentHasSignPath.get()))) {
                        return;
                    } else if (Boolean.TRUE.equals(isGraphqlPath.get()) || request.getUri().toString().startsWith("/upload")) {
                        return;
                    }

                    String jwtToken;

                    try {
                        jwtToken = Objects.requireNonNull(request.getHeaders()
                                .getFirst("Authorization"))
                                .replaceFirst("[b|B]earer ", "");
                    } catch (Exception ex) {
                        throw new AuthenticationCredentialsNotFoundException("NoJwtHeader");
                    }

                    String userId = this.jwtService.getUserIdFromJwtToken(jwtToken);

                    User user = this.userService.createUserSessionById(userId);

                    UserAuthentication authentication = new UserAuthentication(user);
                    authentication.setAuthenticated(true);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                })
                .onErrorResume(SignWithOtherException.class, ex ->
                    this.jwtService.errorResult(request, ex, "sign", signLocation.get())
                )
                .onErrorResume(AuthenticationCredentialsNotFoundException.class, ex ->
                        this.jwtService.errorResult(request, ex, "sign", signLocation.get())
                )
                .onErrorResume(Exception.class, ex ->
                    this.jwtService.errorResult(request, ex, null, signLocation.get())
                );
    }
}
