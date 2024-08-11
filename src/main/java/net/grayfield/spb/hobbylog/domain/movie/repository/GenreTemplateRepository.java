package net.grayfield.spb.hobbylog.domain.movie.repository;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import net.grayfield.spb.hobbylog.domain.movie.struct.Genre;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GenreTemplateRepository {
    private final MongoTemplate mongoTemplate;

    public BulkWriteResult upsertGenres (List<Genre> genres) {
        BulkOperations bulkOps = this.mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Genre.class);

        genres.forEach(genre -> {
            Query query = new Query(Criteria.where("id").is(genre.getId()));
            Update update = new Update().set("name", genre.getName());

            bulkOps.upsert(query, update);
        });

        return bulkOps.execute();
    }
}
