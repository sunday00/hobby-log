package net.grayfield.spb.hobbylog.domain.draw.repository;

import net.grayfield.spb.hobbylog.domain.draw.struct.Draw;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DrawRepository extends MongoRepository<Draw, String> {
    Optional<Draw> findByIdAndUserId(String id, String userId);
}
