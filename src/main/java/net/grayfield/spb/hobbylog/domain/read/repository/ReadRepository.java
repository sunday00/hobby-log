package net.grayfield.spb.hobbylog.domain.read.repository;

import net.grayfield.spb.hobbylog.domain.read.struct.Read;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ReadRepository extends MongoRepository<Read, String> {
    Optional<Read> findByIdAndUserId(String id, String userId);
}
