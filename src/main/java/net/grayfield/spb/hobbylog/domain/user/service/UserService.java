package net.grayfield.spb.hobbylog.domain.user.service;

import lombok.AllArgsConstructor;
import net.grayfield.spb.hobbylog.domain.auth.struct.KakaoMeInfo;
import net.grayfield.spb.hobbylog.domain.user.repository.UserRepository;
import net.grayfield.spb.hobbylog.domain.user.struct.User;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findOneOrCreateByEmail(KakaoMeInfo info) {
        String email = info.getEmail();
        User exists = userRepository.findByEmail(email);

        if(exists != null) return exists;

        User user = new User();
        user.setEmail(email);
        user.setUsername(info.getNickname());
        user.setProfileImage(info.getThumbnailImage());
        user.setVendor("kakao");
        user.setVendorId(info.getId());

        return userRepository.save(user);
    }
}
