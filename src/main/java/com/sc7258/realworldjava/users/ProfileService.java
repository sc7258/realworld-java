package com.sc7258.realworldjava.users;

import com.sc7258.realworldjava.model.Profile;
import com.sc7258.realworldjava.model.ProfileResponse;
import com.sc7258.realworldjava.users.entity.Follow;
import com.sc7258.realworldjava.users.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    public ProfileService(UserRepository userRepository, FollowRepository followRepository) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(String username, User currentUser) {
        User profileUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));

        boolean following = isFollowing(currentUser, profileUser);

        return buildProfileResponse(profileUser, following);
    }

    @Transactional
    public ProfileResponse followUser(String username, User follower) {
        User followedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));

        if (follower.getId().equals(followedUser.getId())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "You can't follow yourself");
        }

        followRepository.findByFollowerAndFollowed(follower, followedUser).ifPresent(follow -> {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "You are already following this user");
        });

        Follow follow = new Follow(follower, followedUser);
        followRepository.save(follow);

        return buildProfileResponse(followedUser, true);
    }

    @Transactional
    public ProfileResponse unfollowUser(String username, User follower) {
        User followedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));

        followRepository.findByFollowerAndFollowed(follower, followedUser)
                .ifPresent(followRepository::delete);

        return buildProfileResponse(followedUser, false);
    }

    @Transactional(readOnly = true)
    public boolean isFollowing(User currentUser, User profileUser) {
        return (currentUser != null) && followRepository.existsByFollowerAndFollowed(currentUser, profileUser);
    }

    public ProfileResponse buildProfileResponse(User profileUser, boolean following) {
        Profile profile = new Profile();
        profile.setUsername(profileUser.getUsername());
        profile.setBio(profileUser.getBio());
        profile.setImage(profileUser.getImage());
        profile.setFollowing(following);

        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setProfile(profile);
        return profileResponse;
    }
}
