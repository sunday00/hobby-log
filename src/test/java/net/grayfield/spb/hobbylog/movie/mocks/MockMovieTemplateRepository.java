package net.grayfield.spb.hobbylog.movie.mocks;

import net.grayfield.spb.hobbylog.domain.movie.repository.MovieTemplateRepository;
import net.grayfield.spb.hobbylog.domain.movie.struct.Movie;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;

public class MockMovieTemplateRepository extends MovieTemplateRepository {
    public List<String> called;

    public MockMovieTemplateRepository(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
        this.called = new ArrayList<String>();
    }

    @Override
    public String upsertMovie(final Movie movie) {
        this.called.add("upsert movie via templateRepository");
        return "upsert";
    }
}
