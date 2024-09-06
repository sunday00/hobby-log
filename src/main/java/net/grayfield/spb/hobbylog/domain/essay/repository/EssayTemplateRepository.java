package net.grayfield.spb.hobbylog.domain.essay.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.domain.essay.struct.Essay;
import net.grayfield.spb.hobbylog.domain.essay.struct.Series;
import net.grayfield.spb.hobbylog.domain.share.repository.CustomProjectAggregationOperation;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class EssayTemplateRepository {
    private final MongoTemplate mongoTemplate;

    public Essay getOneEssayByIdWithSeries (String id) {
        String lookupQuery = "{" +
                "$lookup: {" +
                    "from: 'essay'," +
                    "as: 'series'," +
                    "let: { seriesKey: '$seriesKey' }," +
                    "pipeline: [" +
                        "{ $match: { " +
                            "$expr: { $eq: [ '$$seriesKey', '$seriesKey' ] } } " +
                        "}" +
                        "{ $project: { " +
                            "_id: 1, title: 1, seriesKey: 1, seriesName:1, logAt: 1  } " +
                        "}" +
                    "]" +
                "}" +
            "}";


        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_id").is(id)),
                new CustomProjectAggregationOperation(lookupQuery)
        );

        AggregationResults<Essay> results = mongoTemplate.aggregate(aggregation, "essay", Essay.class);
        return results.getUniqueMappedResult();
    }

    public List<Series> getSeriesListByKeyword(String search) {
        BasicQuery query = new BasicQuery(
                "{ $and: [" +
                            "{ $where: \"this._id == this.seriesKey\" }," +
                            "{ seriesName: { $regex: \"" + search + "\" } } " +
                        "]  " +
                    "}",
                "{ _id: 1, seriesKey: 1, seriesName:1, title: 1, logAt: 1 }"
        );

        return this.mongoTemplate.find(query, Series.class, "essay");
    }
}
