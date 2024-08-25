package net.grayfield.spb.hobbylog.domain.essay.repository;

import net.grayfield.spb.hobbylog.domain.essay.struct.Essay;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EssayRepository extends MongoRepository<Essay, String> {
}
