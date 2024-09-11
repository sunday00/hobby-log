package net.grayfield.spb.hobbylog.schedule.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.image.struct.ImageEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ImageBatchTemplateRepository {
    private final MongoTemplate mongoTemplate;

    public boolean isExistsByCategoryAndPath (String path) {
        Query query = new Query(Criteria.where("path").is(path));
        List<ImageEntity> images = mongoTemplate.find(query, ImageEntity.class, "imagesEntity");
        return !images.isEmpty();
    }
}
