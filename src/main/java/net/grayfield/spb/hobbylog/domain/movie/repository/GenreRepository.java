package net.grayfield.spb.hobbylog.domain.movie.repository;

import net.grayfield.spb.hobbylog.domain.movie.struct.Genre;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GenreRepository extends MongoRepository<Genre, String> {}
