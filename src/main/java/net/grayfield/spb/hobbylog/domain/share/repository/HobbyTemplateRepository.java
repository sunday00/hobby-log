package net.grayfield.spb.hobbylog.domain.share.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.gallery.struct.Gallery;
import net.grayfield.spb.hobbylog.domain.movie.struct.Movie;
import net.grayfield.spb.hobbylog.domain.share.struct.BaseSchema;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;
import net.grayfield.spb.hobbylog.domain.share.struct.Status;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HobbyTemplateRepository {
    private final MongoTemplate mongoTemplate;

    public BaseSchema updateStatus (Category category, Long id, Status status) {
        Query query = new Query(Criteria.where("id").is(id));
        Update update = new Update();
        update.set("status", status);

        switch (category) {
            case MOVIE -> {
                return this.mongoTemplate.findAndModify(query, update, Movie.class);
            }

            case GALLERY -> {
                return this.mongoTemplate.findAndModify(query, update, Gallery.class);
            }

            default -> {
                return null;
            }
        }
    }
}
