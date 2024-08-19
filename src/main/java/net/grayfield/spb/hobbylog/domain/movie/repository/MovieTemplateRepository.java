package net.grayfield.spb.hobbylog.domain.movie.repository;

import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.internal.helper.WordUtils;
import net.grayfield.spb.hobbylog.domain.movie.struct.Movie;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;
import net.grayfield.spb.hobbylog.domain.share.struct.Status;
import net.grayfield.spb.hobbylog.domain.user.struct.UserAuthentication;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public UpdateResult upsertMovie(final Movie movie) {
        UserAuthentication authentication = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getId();

        Query query = new Query(Criteria.where("id").is(movie.getId()));
        Update update = new Update();

        update.set("id", movie.getId());
        update.set("userId", userId);
        update.set("category", Category.MOVIE);
        update.set("title", movie.getTitle());
        update.set("thumbnail", movie.getThumbnail());
        update.set("ratings", movie.getRatings());
        update.set("logAt", LocalDateTime.now(ZoneOffset.UTC));
        update.set("status", Status.DRAFT);

        Arrays.stream(Movie.class.getDeclaredFields()).toList().forEach(f -> {
            try {
                String methodName = "get" + WordUtils.capitalize(f.getName());
                Method method = Movie.class.getMethod(methodName);
                update.set(f.getName(), method.invoke(movie));
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                log.error(e.getMessage());
            }
        });

        return mongoTemplate.upsert(query, update, Movie.class);
    }
}
