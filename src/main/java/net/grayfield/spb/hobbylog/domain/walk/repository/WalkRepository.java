package net.grayfield.spb.hobbylog.domain.walk.repository;

import net.grayfield.spb.hobbylog.domain.walk.struct.Walk;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface WalkRepository extends MongoRepository<Walk, String> {
    Optional<Walk> findByIdAndUserId(String id, String userId);
}
