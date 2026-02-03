package com.sc7258.realworldjava.users;

import com.sc7258.realworldjava.api.ProfileApi;
import com.sc7258.realworldjava.model.ProfileResponse;
import com.sc7258.realworldjava.users.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProfilesController implements ProfileApi {

    private final ProfileService profileService;
    private final UserRepository userRepository;

    public ProfilesController(ProfileService profileService, UserRepository userRepository) {
        this.profileService = profileService;
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<ProfileResponse> getProfileByUsername(String username) {
        User currentUser = getCurrentUserFromSecurityContext();
        ProfileResponse profileResponse = profileService.getProfile(username, currentUser);
        return ResponseEntity.ok(profileResponse);
    }

    @Override
    public ResponseEntity<ProfileResponse> followUserByUsername(String username) {
        User currentUser = getCurrentUserFromSecurityContext();
        if (currentUser == null) {
            // 이 경우는 Spring Security 설정에 의해 거부되지만, 방어적으로 코드를 작성합니다.
            return ResponseEntity.status(401).build();
        }
        ProfileResponse profileResponse = profileService.followUser(username, currentUser);
        return ResponseEntity.ok(profileResponse);
    }

    @Override
    public ResponseEntity<ProfileResponse> unfollowUserByUsername(String username) {
        User currentUser = getCurrentUserFromSecurityContext();
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        ProfileResponse profileResponse = profileService.unfollowUser(username, currentUser);
        return ResponseEntity.ok(profileResponse);
    }

    private User getCurrentUserFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        return userRepository.findByUsername(principal.getUsername()).orElse(null);
    }
}
