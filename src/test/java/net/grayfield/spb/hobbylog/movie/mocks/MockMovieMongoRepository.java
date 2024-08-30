package net.grayfield.spb.hobbylog.movie.mocks;

import net.grayfield.spb.hobbylog.domain.movie.repository.MovieRepository;
import net.grayfield.spb.hobbylog.domain.movie.struct.Movie;
import net.grayfield.spb.hobbylog.share.mocks.MockMongoRepository;

import java.util.Optional;

public class MockMovieMongoRepository extends MockMongoRepository<Movie, String> implements MovieRepository {
    @Override
    public Optional<Movie> findMovieByIdAndUserId(String id, String userid){
        Movie movie = new Movie();
        movie.setId(id);
        return Optional.of(movie);
    }
}
