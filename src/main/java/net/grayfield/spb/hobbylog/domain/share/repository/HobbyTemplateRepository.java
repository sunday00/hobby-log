package net.grayfield.spb.hobbylog.domain.share.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.draw.struct.Draw;
import net.grayfield.spb.hobbylog.domain.essay.struct.Essay;
import net.grayfield.spb.hobbylog.domain.gallery.struct.Gallery;
import net.grayfield.spb.hobbylog.domain.movie.struct.Movie;
import net.grayfield.spb.hobbylog.domain.read.struct.Read;
import net.grayfield.spb.hobbylog.domain.share.StaticHelper;
import net.grayfield.spb.hobbylog.domain.share.struct.BaseSchema;
import net.grayfield.spb.hobbylog.domain.share.struct.Category;
import net.grayfield.spb.hobbylog.domain.share.struct.Status;
import net.grayfield.spb.hobbylog.domain.walk.struct.Walk;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.UnionWithOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HobbyTemplateRepository {
    private final MongoTemplate mongoTemplate;

    public BaseSchema updateStatus (Category category, String id, Status status) {
        String userId = StaticHelper.getUserId();

        Query query = new Query(
                Criteria.where("id").is(id).and("userId").is(userId)
        );
        Update update = new Update();
        update.set("status", status);

        switch (category) {
            case MOVIE -> {
                return this.mongoTemplate.findAndModify(query, update, Movie.class);
            }

            case GALLERY -> {
                return this.mongoTemplate.findAndModify(query, update, Gallery.class);
            }

            case ESSAY -> {
                return this.mongoTemplate.findAndModify(query, update, Essay.class);
            }

            case WALK -> {
                return this.mongoTemplate.findAndModify(query, update, Walk.class);
            }

            case DRAW -> {
                return this.mongoTemplate.findAndModify(query, update, Draw.class);
            }

            case READ -> {
                return this.mongoTemplate.findAndModify(query, update, Read.class);
            }

            default -> {
                return null;
            }
        }
    }

    public List<BaseSchema> findByMonth(String yyyy, String mm, @Nullable String userId) {
        String myId = StaticHelper.getUserId();

        LocalDateTime baseD = LocalDateTime.parse(yyyy + "-" + mm + "-01T00:00:00");

        /*
            between date is now prev month to current month.
            because, on first week on current month,
            main page will be too much clean and no data.

            so, I decided to show logged hobby during two month.
        */
        LocalDateTime startD = baseD.minusMonths(1);
        LocalDateTime endD = baseD.plusMonths(1);

        Criteria criteria = new Criteria();

        if(userId != null && userId.equals("my")) {
            criteria.andOperator(
                    Criteria.where("logAt").gte(startD),
                    Criteria.where("logAt").lt(endD),
                    Criteria.where("status").is(Status.ACTIVE),
                    Criteria.where("userId").is(myId)
            );
        } else if (userId != null) {
            criteria.andOperator(
                    Criteria.where("logAt").gte(startD),
                    Criteria.where("logAt").lt(endD),
                    Criteria.where("status").is(Status.ACTIVE),
                    Criteria.where("userId").is(userId)
            );
        } else {
            criteria.andOperator(
                    Criteria.where("logAt").gte(startD),
                    Criteria.where("logAt").lt(endD),
                    Criteria.where("status").is(Status.ACTIVE)
            );
        }

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project("id", "userId", "title", "category", "seriesName", "thumbnail", "ratings",  "logAt", "status"),
                UnionWithOperation.unionWith("movie"),
                UnionWithOperation.unionWith("walk"),
                UnionWithOperation.unionWith("draw"),
                UnionWithOperation.unionWith("read"),
                UnionWithOperation.unionWith("essay"),
                Aggregation.match(criteria),
                Aggregation.sort(Sort.Direction.DESC, "logAt")
                );

        AggregationResults<BaseSchema> results = mongoTemplate.aggregate(aggregation, "gallery", BaseSchema.class);

        return results.getMappedResults();
    }

    public List<BaseSchema> findNonActiveByMonth(String yyyy, String mm) {
        String userId = StaticHelper.getUserId();

        LocalDateTime startD = LocalDateTime.parse(yyyy + "-" + mm + "-01T00:00:00");
        LocalDateTime endD = startD.plusMonths(1);

        Criteria criteria = new Criteria()
                .andOperator(
                        Criteria.where("logAt").gte(startD),
                        Criteria.where("logAt").lt(endD),
                        Criteria.where("userId").is(userId),
                        Criteria.where("status").ne(Status.ACTIVE)
                );

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project("id", "userId", "title", "category", "seriesName", "thumbnail", "ratings",  "logAt", "status"),
                UnionWithOperation.unionWith("movie"),
                UnionWithOperation.unionWith("walk"),
                UnionWithOperation.unionWith("draw"),
                UnionWithOperation.unionWith("read"),
                UnionWithOperation.unionWith("essay"),
                Aggregation.match(criteria),
                Aggregation.sort(Sort.Direction.DESC, "logAt")
        );

        AggregationResults<BaseSchema> results = mongoTemplate.aggregate(aggregation, "gallery", BaseSchema.class);

        return results.getMappedResults();
    }

    public List<BaseSchema> findByYearAndCategory(String yyyy, Category category) {
//        String userId = StaticHelper.getUserId();

        LocalDateTime startD = LocalDateTime.parse(yyyy + "-01-01T00:00:00");
        LocalDateTime endD = startD.plusYears(1);

        Criteria criteria = new Criteria()
                .andOperator(
                        Criteria.where("logAt").gte(startD),
                        Criteria.where("logAt").lt(endD),
//                        Criteria.where("userId").is(userId),
                        Criteria.where("status").is(Status.ACTIVE)
                );

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project("id", "userId", "title", "category", "seriesName", "thumbnail", "ratings",  "logAt", "status"),
                Aggregation.match(criteria),
                Aggregation.sort(Sort.Direction.DESC, "logAt")
        );

        AggregationResults<BaseSchema> results = mongoTemplate.aggregate(aggregation, category.toString().toLowerCase(), BaseSchema.class);

        return results.getMappedResults();
    }

    public List<BaseSchema> searchHobby(String search, Long page) {
        Criteria criteria = new Criteria()
                .andOperator(
                        Criteria.where("status").is(Status.ACTIVE),
                        new Criteria().orOperator(
                            Criteria.where("title").regex(search, "i"),
                            Criteria.where("seriesName").regex(search, "i")
                        )
                );

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project("id", "userId", "title", "category", "seriesName", "thumbnail", "ratings",  "logAt", "status"),
                UnionWithOperation.unionWith("movie"),
                UnionWithOperation.unionWith("walk"),
                UnionWithOperation.unionWith("draw"),
                UnionWithOperation.unionWith("read"),
                UnionWithOperation.unionWith("essay"),
                Aggregation.match(criteria),
                Aggregation.skip((page - 1) * 20),
                Aggregation.limit(20),
                Aggregation.sort(Sort.Direction.DESC, "logAt")
        );

        AggregationResults<BaseSchema> results = mongoTemplate.aggregate(aggregation, "gallery", BaseSchema.class);

        return results.getMappedResults();
    }

    public BaseSchema deleteOneHobby(Category category, String id) {
        String userId = StaticHelper.getUserId();

        Query query = new Query(
                Criteria.where("id").is(id).and("userId").is(userId)
        );
        switch (category) {
            case MOVIE -> {
                return this.mongoTemplate.findAndRemove(query, Movie.class);
            }

            case GALLERY -> {
                return this.mongoTemplate.findAndRemove(query, Gallery.class);
            }

            case ESSAY -> {
                return this.mongoTemplate.findAndRemove(query, Essay.class);
            }

            case WALK -> {
                return this.mongoTemplate.findAndRemove(query, Walk.class);
            }

            case DRAW -> {
                return this.mongoTemplate.findAndRemove(query, Draw.class);
            }

            case READ -> {
                return this.mongoTemplate.findAndRemove(query, Read.class);
            }

            default -> {
                return null;
            }
        }
    }
}
