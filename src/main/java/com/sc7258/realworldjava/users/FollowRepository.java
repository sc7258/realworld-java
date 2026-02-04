package com.sc7258.realworldjava.users;

import com.sc7258.realworldjava.users.entity.Follow;
import com.sc7258.realworldjava.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findByFollower(User follower);
    Optional<Follow> findByFollowerAndFollowed(User follower, User followed);
    boolean existsByFollowerAndFollowed(User follower, User followed);
}
