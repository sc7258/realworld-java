package com.sc7258.realworldjava.users;

import com.sc7258.realworldjava.users.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // 테스트 후 데이터 롤백
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void registerUser_success() throws Exception {
        // Given
        long timestamp = System.currentTimeMillis();
        String username = "testuser" + timestamp;
        String email = "test" + timestamp + "@test.com";
        String password = "password";

        String requestBody = String.format("{\"user\":{\"username\":\"%s\", \"email\":\"%s\", \"password\":\"%s\"}}", username, email, password);

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.email").value(email))
                .andExpect(jsonPath("$.user.username").value(username))
                .andExpect(jsonPath("$.user.token").isNotEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"user\":{\"username\":\"user1\", \"email\":\"email1@email.com\"}}", // password missing
            "{\"user\":{\"username\":\"user2\", \"password\":\"pass2\"}}",       // email missing
            "{\"user\":{\"email\":\"email3@email.com\", \"password\":\"pass3\"}}"  // username missing
    })
    void registerUser_fail_when_required_field_is_missing(String requestBody) throws Exception {
        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void registerUser_fail_when_email_or_username_is_duplicate() throws Exception {
        // Given: 기존 사용자 생성
        userRepository.save(new User("existing@test.com", "existinguser", "password"));

        // Case 1: 중복된 이메일
        String requestBody1 = "{\"user\":{\"username\":\"newuser\", \"email\":\"existing@test.com\", \"password\":\"password\"}}";
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody1))
                .andExpect(status().isUnprocessableEntity());

        // Case 2: 중복된 사용자 이름
        String requestBody2 = "{\"user\":{\"username\":\"existinguser\", \"email\":\"new@test.com\", \"password\":\"password\"}}";
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody2))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void login_success() throws Exception {
        // Given: 테스트용 사용자 생성
        String email = "login@test.com";
        String password = "loginpass";
        userRepository.save(new User(email, "loginuser", passwordEncoder.encode(password)));

        String requestBody = String.format("{\"user\":{\"email\":\"%s\", \"password\":\"%s\"}}", email, password);

        // When & Then
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value(email))
                .andExpect(jsonPath("$.user.token").isNotEmpty());
    }

    @Test
    void login_fail_when_credentials_are_invalid() throws Exception {
        // Given: 테스트용 사용자 생성
        String email = "loginfail@test.com";
        String password = "loginfailpass";
        userRepository.save(new User(email, "loginfailuser", passwordEncoder.encode(password)));

        // Case 1: 존재하지 않는 이메일
        String requestBody1 = "{\"user\":{\"email\":\"nonexistent@test.com\", \"password\":\"password\"}}";
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody1))
                .andExpect(status().isUnauthorized());

        // Case 2: 잘못된 비밀번호
        String requestBody2 = String.format("{\"user\":{\"email\":\"%s\", \"password\":\"wrongpassword\"}}", email);
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody2))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("currentuser")
    void getCurrentUser_success() throws Exception {
        // Given
        userRepository.save(new User("current@test.com", "currentuser", "password"));

        // When & Then
        mockMvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username").value("currentuser"))
                .andExpect(jsonPath("$.user.email").value("current@test.com"))
                .andExpect(jsonPath("$.user.token").isNotEmpty());
    }

    @Test
    void getCurrentUser_fail_when_not_authenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("updateuser")
    void updateUser_success() throws Exception {
        // Given
        userRepository.save(new User("update@test.com", "updateuser", passwordEncoder.encode("password")));
        String newEmail = "new-update@test.com";
        String newBio = "new bio";
        String requestBody = String.format("{\"user\":{\"email\":\"%s\", \"bio\":\"%s\"}}", newEmail, newBio);

        // When & Then
        mockMvc.perform(put("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value(newEmail))
                .andExpect(jsonPath("$.user.bio").value(newBio))
                .andExpect(jsonPath("$.user.token").isNotEmpty());
    }
}
