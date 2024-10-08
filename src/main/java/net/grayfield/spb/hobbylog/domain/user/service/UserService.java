package net.grayfield.spb.hobbylog.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import graphql.GraphqlErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.grayfield.spb.hobbylog.aop.catcher.CustomErrorType;
import net.grayfield.spb.hobbylog.domain.auth.struct.KakaoMeInfo;
import net.grayfield.spb.hobbylog.domain.user.repository.UserRepository;
import net.grayfield.spb.hobbylog.domain.user.struct.Role;
import net.grayfield.spb.hobbylog.domain.user.struct.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.ttl}")
    private Integer ttl;

    public User findOneOrCreateByEmail(KakaoMeInfo info) {
        String email = info.getEmail();

        // TODO BLOCK USER by email
        User exists = userRepository.findByEmail(email);

        if(exists != null) return exists;

        // TODO: store thumbnail image to storage

        User user = new User();
        user.setEmail(email);
        user.setUsername(info.getNickname());
        user.setProfileImage(info.getThumbnailImage());
        user.setRoles(List.of(Role.ROLE_USER));
        user.setVendor("kakao");
        user.setVendorId(info.getId());
        user.setIsActive(true);

        return userRepository.save(user);
    }

    public User findOneById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    public void createUserSession(User user) throws JsonProcessingException {
        log.debug("currently create to redis");

        try {
            String key = "session:" + user.getId();
            this.redisTemplate.opsForValue().set(key, user);
            this.redisTemplate.expire(key, Duration.ofSeconds(ttl));
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error(String.valueOf(e.getCause()));
        }
    }

    public User getUserFromSession(String userId) {
        String key = "session:" + userId;
        return (User) this.redisTemplate.opsForValue().get(key);
    }

    public User createUserSessionById(String userId) {
        User user = this.getUserFromSession(userId);

        if (user == null) {
            user = this.findOneById(userId);
            try {
                this.createUserSession(user);
            } catch (JsonProcessingException e) {
                log.error(e.getMessage());
            }
        }

        return user;
    }
}
