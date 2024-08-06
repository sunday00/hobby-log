package net.grayfield.spb.hobbylog.domain.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import net.grayfield.spb.hobbylog.domain.user.struct.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
}
