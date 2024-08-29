package net.grayfield.spb.hobbylog.domain.draw.repository;

import net.grayfield.spb.hobbylog.domain.draw.struct.Draw;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DrawRepository extends MongoRepository<Draw, String> {
}
