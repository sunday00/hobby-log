package net.grayfield.spb.hobbylog.domain.movie.repository;

import net.grayfield.spb.hobbylog.domain.movie.struct.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MovieRepository extends MongoRepository<Movie, String> {
}
