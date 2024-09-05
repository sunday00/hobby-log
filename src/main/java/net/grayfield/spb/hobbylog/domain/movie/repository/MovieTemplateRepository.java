package net.grayfield.spb.hobbylog.domain.movie.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.internal.helper.WordUtils;
import net.grayfield.spb.hobbylog.domain.movie.struct.Movie;
import net.grayfield.spb.hobbylog.domain.share.StaticHelper;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;
import net.grayfield.spb.hobbylog.domain.share.struct.Status;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MovieTemplateRepository {
    private final MongoTemplate mongoTemplate;

    public String upsertMovie(final Movie movie) {
        String userId = StaticHelper.getUserId();

        Query query = new Query(
                Criteria.where("movieId").is(movie.getMovieId())
                        .and("userId").is(userId)
                        .and("logAt").is(movie.getLogAt())
        );

        if (movie.getId() != null) {
            query = new Query(
                    Criteria.where("id").is(movie.getId())
                            .and("userId").is(userId)
            );
        }

        Update update = new Update();

        update.set("category", Category.MOVIE);
        update.set("title", movie.getTitle());
        update.set("thumbnail", movie.getThumbnail());
        update.set("ratings", movie.getRatings());
        update.set("logAt", movie.getLogAt());
        update.set("status", movie.getStatus());

        Arrays.stream(Movie.class.getDeclaredFields()).toList().forEach(f -> {
            try {
                if(f.getName().equals("id")) return;

                String methodName = "get" + WordUtils.capitalize(f.getName());
                Method method = Movie.class.getMethod(methodName);
                update.set(f.getName(), method.invoke(movie));
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                log.error(e.getMessage());
            }
        });

        mongoTemplate.upsert(query, update, Movie.class);

        return mongoTemplate.find(query, Movie.class).getFirst().getId();
    }
}
