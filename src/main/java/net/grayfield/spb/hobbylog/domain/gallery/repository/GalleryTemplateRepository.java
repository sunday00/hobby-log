package net.grayfield.spb.hobbylog.domain.gallery.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.internal.helper.WordUtils;
import net.grayfield.spb.hobbylog.domain.gallery.struct.Gallery;
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
import java.util.Arrays;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GalleryTemplateRepository {
    private final MongoTemplate mongoTemplate;

    public String upsertGallery(final Gallery gallery) {
        String userId = StaticHelper.getUserId();

        Query query = new Query(
                Criteria.where("logAt").is(gallery.getLogAt())
                        .and("userId").is(userId)
        );
        Update update = new Update();

        update.set("category", Category.GALLERY);
        update.set("title", gallery.getTitle());
        update.set("thumbnail", gallery.getThumbnail());
        update.set("ratings", gallery.getRatings());
        update.set("logAt", gallery.getLogAt());
        update.set("status", Status.DRAFT);

        Arrays.stream(Gallery.class.getDeclaredFields()).toList().forEach(f -> {
            try {
                if(f.getName().equals("id")) return;

                String methodName = "get" + WordUtils.capitalize(f.getName());
                Method method = Gallery.class.getMethod(methodName);
                update.set(f.getName(), method.invoke(gallery));
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                log.error(e.getMessage());
            }
        });

        mongoTemplate.upsert(query, update, Gallery.class);

        return mongoTemplate.find(query, Gallery.class).getFirst().getId();
    }
}
