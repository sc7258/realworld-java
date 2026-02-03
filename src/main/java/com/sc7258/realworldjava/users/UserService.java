package com.sc7258.realworldjava.users;

import com.sc7258.realworldjava.security.JwtUtils;
import com.sc7258.realworldjava.users.entity.User;
import com.sc7258.realworldjava.users.model.LoginUser;
import com.sc7258.realworldjava.users.model.NewUser;
import com.sc7258.realworldjava.users.model.UpdateUser;
import com.sc7258.realworldjava.users.model.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Transactional
    public UserResponse registerUser(NewUser newUser) {
        if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Email is already taken");
        }
        if (userRepository.findByUsername(newUser.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Username is already taken");
        }

        User user = new User(
                newUser.getEmail(),
                newUser.getUsername(),
                passwordEncoder.encode(newUser.getPassword())
        );
        User savedUser = userRepository.save(user);

        return buildUserResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse login(LoginUser loginUser) {
        User user = userRepository.findByEmail(loginUser.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return buildUserResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        User user = getCurrentAuthenticatedUser();
        return buildUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(UpdateUser updateUser) {
        User currentUser = getCurrentAuthenticatedUser();

        if (StringUtils.hasText(updateUser.getEmail()) && !updateUser.getEmail().equals(currentUser.getEmail())) {
            userRepository.findByEmail(updateUser.getEmail()).ifPresent(u -> {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Email is already taken");
            });
            currentUser.setEmail(updateUser.getEmail());
        }
        if (StringUtils.hasText(updateUser.getUsername()) && !updateUser.getUsername().equals(currentUser.getUsername())) {
            userRepository.findByUsername(updateUser.getUsername()).ifPresent(u -> {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Username is already taken");
            });
            currentUser.setUsername(updateUser.getUsername());
        }
        if (StringUtils.hasText(updateUser.getPassword())) {
            currentUser.setPassword(passwordEncoder.encode(updateUser.getPassword()));
        }
        if (StringUtils.hasText(updateUser.getBio())) {
            currentUser.setBio(updateUser.getBio());
        }
        if (StringUtils.hasText(updateUser.getImage())) {
            currentUser.setImage(updateUser.getImage());
        }

        User updatedUser = userRepository.save(currentUser);
        return buildUserResponse(updatedUser);
    }

    private User getCurrentAuthenticatedUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private UserResponse buildUserResponse(User user) {
        String token = jwtUtils.generateToken(user);
        com.sc7258.realworldjava.users.model.User userModel = new com.sc7258.realworldjava.users.model.User();
        userModel.setEmail(user.getEmail());
        userModel.setUsername(user.getUsername());
        userModel.setBio(user.getBio());
        userModel.setImage(user.getImage());
        userModel.setToken(token);

        UserResponse userResponse = new UserResponse();
        userResponse.setUser(userModel);
        return userResponse;
    }
}
