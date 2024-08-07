package net.grayfield.spb.hobbylog.domain.user.service;

import lombok.AllArgsConstructor;
import net.grayfield.spb.hobbylog.domain.auth.struct.KakaoMeInfo;
import net.grayfield.spb.hobbylog.domain.user.repository.UserRepository;
import net.grayfield.spb.hobbylog.domain.user.struct.Role;
import net.grayfield.spb.hobbylog.domain.user.struct.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findOneOrCreateByEmail(KakaoMeInfo info) {
        String email = info.getEmail();
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
}
