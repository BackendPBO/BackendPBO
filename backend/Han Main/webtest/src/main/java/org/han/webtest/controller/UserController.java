package org.han.webtest.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.han.webtest.DTO.UserDashboardResponse;
import org.han.webtest.DTO.UserLoginRequest;
import org.han.webtest.model.UserModel;
import org.han.webtest.repository.UserRepository;
import org.han.webtest.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")

public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository){
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/signup")
    public UserModel createUser(@RequestBody UserModel user){
        UserModel newUser = userService.createUsers(user.getUsername(), user.getEmail(), user.getPassword());

        if (user.getRole() != null) {
            newUser.setRole(user.getRole());
            return userRepository.save(newUser);
        }

        return newUser;
    }

    @PostMapping("/login")
    public UserDashboardResponse userLogin(@RequestBody UserLoginRequest req, HttpServletResponse res){
        return userService.userLogin(req, res);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> userLogout(HttpServletResponse response){
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok("LOGOUT SUCCESS");
    }
}
