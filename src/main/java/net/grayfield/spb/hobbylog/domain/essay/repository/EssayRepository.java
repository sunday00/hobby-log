package net.grayfield.spb.hobbylog.domain.essay.repository;

import net.grayfield.spb.hobbylog.domain.essay.struct.Essay;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface EssayRepository extends MongoRepository<Essay, String> {
    Optional<Essay> findByIdAndUserId(String id, String userId);
}
