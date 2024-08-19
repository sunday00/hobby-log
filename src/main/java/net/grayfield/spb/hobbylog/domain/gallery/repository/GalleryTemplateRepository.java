package net.grayfield.spb.hobbylog.domain.gallery.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.internal.helper.WordUtils;
import net.grayfield.spb.hobbylog.domain.gallery.struct.Gallery;
import net.grayfield.spb.hobbylog.domain.share.service.MongoAutoIncIdService;
import net.grayfield.spb.hobbylog.domain.share.StaticHelper;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;
import net.grayfield.spb.hobbylog.domain.share.struct.Status;
import org.bson.BsonValue;
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
    private final MongoAutoIncIdService mongoAutoIncIdService;

    public Long upsertGallery(final Gallery gallery) {
        String userId = StaticHelper.getUserId();

        Query query = new Query(Criteria.where("logAt").is(gallery.getLogAt()));
        Update update = new Update();

        Gallery current;

        if(gallery.getId() != null) {
            query = new Query(Criteria.where("id").is(gallery.getId()));
            current = mongoTemplate.findOne(query, Gallery.class);
        } else {
            current = mongoTemplate.findOne(query, Gallery.class);
            if(current != null) {
                update.set("id", current.getId());
            } else {
                update.set("id", this.mongoAutoIncIdService.getNextValue());
            }
        }

        update.set("id", gallery.getId());
        update.set("userId", userId);
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

        BsonValue result = mongoTemplate.upsert(query, update, Gallery.class).getUpsertedId();

        if (result != null) {
            return result.asNumber().longValue();
        } else {
            return current != null ? current.getId() : 1L;
        }
    }
}
