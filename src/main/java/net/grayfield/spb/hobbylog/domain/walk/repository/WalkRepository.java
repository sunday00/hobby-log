package net.grayfield.spb.hobbylog.domain.walk.repository;

import net.grayfield.spb.hobbylog.domain.walk.struct.Walk;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WalkRepository extends MongoRepository<Walk, String> {
}
