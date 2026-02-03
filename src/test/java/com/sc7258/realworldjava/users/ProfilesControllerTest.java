package com.sc7258.realworldjava.users;

import com.sc7258.realworldjava.users.entity.Follow;
import com.sc7258.realworldjava.users.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProfilesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRepository followRepository;

    private User userA;
    private User userB;

    @BeforeEach
    void setUp() {
        userA = userRepository.save(new User("userA@test.com", "userA", "password"));
        userB = userRepository.save(new User("userB@test.com", "userB", "password"));
    }

    // 1. 프로필 조회 테스트
    @Test
    void getProfile_unauthenticated() throws Exception {
        mockMvc.perform(get("/api/profiles/" + userA.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profile.username").value(userA.getUsername()))
                .andExpect(jsonPath("$.profile.following").value(false));
    }

    @Test
    @WithMockUser("userB")
    void getProfile_authenticated_not_following() throws Exception {
        mockMvc.perform(get("/api/profiles/" + userA.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profile.username").value(userA.getUsername()))
                .andExpect(jsonPath("$.profile.following").value(false));
    }

    @Test
    @WithMockUser("userB")
    void getProfile_authenticated_following() throws Exception {
        followRepository.save(new Follow(userB, userA));
        mockMvc.perform(get("/api/profiles/" + userA.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profile.username").value(userA.getUsername()))
                .andExpect(jsonPath("$.profile.following").value(true));
    }

    @Test
    void getProfile_not_found() throws Exception {
        mockMvc.perform(get("/api/profiles/nonexistentuser"))
                .andExpect(status().isNotFound());
    }

    // 2. 사용자 팔로우 테스트
    @Test
    @WithMockUser("userB")
    void followUser_success() throws Exception {
        mockMvc.perform(post("/api/profiles/" + userA.getUsername() + "/follow"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profile.username").value(userA.getUsername()))
                .andExpect(jsonPath("$.profile.following").value(true));
    }

    @Test
    void followUser_unauthenticated() throws Exception {
        mockMvc.perform(post("/api/profiles/" + userA.getUsername() + "/follow"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("userB")
    void followUser_not_found() throws Exception {
        mockMvc.perform(post("/api/profiles/nonexistentuser/follow"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("userA")
    void followUser_self() throws Exception {
        mockMvc.perform(post("/api/profiles/" + userA.getUsername() + "/follow"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser("userB")
    void followUser_already_following() throws Exception {
        followRepository.save(new Follow(userB, userA));
        mockMvc.perform(post("/api/profiles/" + userA.getUsername() + "/follow"))
                .andExpect(status().isUnprocessableEntity());
    }

    // 3. 사용자 언팔로우 테스트
    @Test
    @WithMockUser("userB")
    void unfollowUser_success() throws Exception {
        followRepository.save(new Follow(userB, userA));
        mockMvc.perform(delete("/api/profiles/" + userA.getUsername() + "/follow"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profile.username").value(userA.getUsername()))
                .andExpect(jsonPath("$.profile.following").value(false));
    }

    @Test
    void unfollowUser_unauthenticated() throws Exception {
        mockMvc.perform(delete("/api/profiles/" + userA.getUsername() + "/follow"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("userB")
    void unfollowUser_not_following() throws Exception {
        // 멱등성: 아무 일도 일어나지 않고 성공 응답
        mockMvc.perform(delete("/api/profiles/" + userA.getUsername() + "/follow"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profile.username").value(userA.getUsername()))
                .andExpect(jsonPath("$.profile.following").value(false));
    }
}
