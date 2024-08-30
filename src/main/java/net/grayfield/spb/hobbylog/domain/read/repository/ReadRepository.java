package net.grayfield.spb.hobbylog.domain.read.repository;

import net.grayfield.spb.hobbylog.domain.read.struct.Read;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReadRepository extends MongoRepository<Read, String> {

}
