package net.grayfield.spb.hobbylog.aop.catcher;

import graphql.GraphQLError;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @GraphQlExceptionHandler
    public GraphQLError handle(Exception ex, DataFetchingEnvironment ev) {
        log.error("EX: {}", ex.getMessage());
        log.error("EX: {}", Arrays.stream(ex.getStackTrace()).toList());
        log.error("EX: {}", ex.getClass().getName());

        var className = ex.getClass().getName();

        log.info("path: {}", ev.getExecutionStepInfo().getPath());
        log.info("locations: {}", ev.getField().getSourceLocation());

        if(className.equals(AuthorizationDeniedException.class.getName())) {
            return GraphQLError.newError()
                    .message("unauthorized")
                    .path(ev.getExecutionStepInfo().getPath())
                    .location(ev.getField().getSourceLocation())
                    .errorType(CustomErrorType.UNAUTHORIZED)
                    .build();
        }

        return GraphQLError.newError()
                .message(ex.getMessage())
                .path(ev.getExecutionStepInfo().getPath())
                .location(ev.getField().getSourceLocation())
                .errorType(CustomErrorType.INTERNAL_ERROR)
                .build();
    }

//    @GraphQlExceptionHandler
//    public GraphQLError validationExceptionHandle(ConstraintViolationException ex, DataFetchingEnvironment ev) {
//        log.error("EX: {}", ex.getMessage());
//        log.error("EX: {}", Arrays.stream(ex.getStackTrace()).toList());
//        log.error("EX: {}", ex.getClass().getName());
//
//        Map<String , Object> extension = new HashMap<>();
//        extension.put("code", 400);
//        extension.put("message", "InputError");
//
//        return GraphQLError.newError()
//                .message(ex.getMessage())
//                .path(ev.getExecutionStepInfo().getPath())
//                .location(ev.getField().getSourceLocation())
//                .errorType(CustomErrorType.BAD_REQUEST)
//                .extensions(extension)
//                .build();
//    }
}
