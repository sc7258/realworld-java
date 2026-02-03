package com.sc7258.realworldjava.users;

import com.sc7258.realworldjava.users.api.UserAndAuthenticationApi;
import com.sc7258.realworldjava.users.model.LoginUserRequest;
import com.sc7258.realworldjava.users.model.NewUserRequest;
import com.sc7258.realworldjava.users.model.UpdateUserRequest;
import com.sc7258.realworldjava.users.model.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UsersController implements UserAndAuthenticationApi {

    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<UserResponse> createUser(NewUserRequest newUserRequest) {
        UserResponse userResponse = userService.registerUser(newUserRequest.getUser());
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<UserResponse> login(LoginUserRequest loginUserRequest) {
        UserResponse userResponse = userService.login(loginUserRequest.getUser());
        return ResponseEntity.ok(userResponse);
    }

    @Override
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse userResponse = userService.getCurrentUser();
        return ResponseEntity.ok(userResponse);
    }

    @Override
    public ResponseEntity<UserResponse> updateCurrentUser(UpdateUserRequest updateUserRequest) {
        UserResponse userResponse = userService.updateUser(updateUserRequest.getUser());
        return ResponseEntity.ok(userResponse);
    }
}
