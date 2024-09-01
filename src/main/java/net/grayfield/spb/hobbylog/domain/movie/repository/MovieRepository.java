package net.grayfield.spb.hobbylog.domain.movie.repository;

import net.grayfield.spb.hobbylog.domain.movie.struct.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MovieRepository extends MongoRepository<Movie, String> {
    Optional<Movie> findMovieById(String id);

    Optional<Movie> findMovieByIdAndUserId(String id, String userId);
}
