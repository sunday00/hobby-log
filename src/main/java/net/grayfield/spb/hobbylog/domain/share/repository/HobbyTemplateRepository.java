package net.grayfield.spb.hobbylog.domain.share.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.gallery.struct.Gallery;
import net.grayfield.spb.hobbylog.domain.movie.struct.Movie;
import net.grayfield.spb.hobbylog.domain.share.StaticHelper;
import net.grayfield.spb.hobbylog.domain.share.struct.BaseSchema;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;
import net.grayfield.spb.hobbylog.domain.share.struct.Status;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.UnionWithOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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

    public List<BaseSchema> findByMonth(String yyyy, String mm) {
        String userId = StaticHelper.getUserId();

        LocalDateTime baseD = LocalDateTime.parse(yyyy + "-" + mm + "-01T00:00:00");

        /*
            between date is now prev month to current month.
            because, on first week on current month,
            main page will be too much clean and no data.

            so, I decided to show logged hobby during two month.
        */
        LocalDateTime startD = baseD.minusMonths(1);
        LocalDateTime endD = startD.plusMonths(1);

        Criteria criteria = new Criteria()
                .andOperator(
                        Criteria.where("logAt").gte(startD),
                        Criteria.where("logAt").lt(endD),
                        Criteria.where("status").is(Status.ACTIVE),
                        Criteria.where("userId").is(userId)
                )
            ;

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project("id", "userId", "title", "category", "thumbnail", "ratings",  "logAt", "status"),
                UnionWithOperation.unionWith("movie"),
                Aggregation.match(criteria)
                );

        AggregationResults<BaseSchema> results = mongoTemplate.aggregate(aggregation, "gallery", BaseSchema.class);

        log.info("results: {}", results.getMappedResults());

        return results.getMappedResults();
    }
}
