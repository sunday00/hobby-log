package net.grayfield.spb.hobbylog.configs;

import graphql.analysis.MaxQueryDepthInstrumentation;
import graphql.scalars.ExtendedScalars;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.graphql.GraphQlSourceBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Slf4j
@Configuration
public class GraphqlConfig {
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
//                .scalar(GraphqlCustomSchema.DateScalar)
                .scalar(ExtendedScalars.GraphQLLong)
                .scalar(ExtendedScalars.GraphQLBigDecimal)
            ;
    }

    @Bean
    public MaxQueryDepthInstrumentation maxQueryDepthInstrumentation() {
        return new MaxQueryDepthInstrumentation(15);
    }

    @Bean
    GraphQlSourceBuilderCustomizer inspectionCustomizer() {
        return source ->
                source.inspectSchemaMappings(report -> log.info(report.toString()));
    }
}
