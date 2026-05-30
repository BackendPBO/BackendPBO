package org.han.webtest.service;


import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.User;
import org.han.webtest.DTO.UserDashboardResponse;
import org.han.webtest.DTO.UserLoginRequest;
import org.han.webtest.model.UserModel;
import org.han.webtest.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.Cookie;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder encoder =  new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository, JwtService jwtService){
        this.userRepository = userRepository;
        this.jwtService = jwtService;

    }

    public UserModel createUsers(String username,String email,String password){
        String hash = encoder.encode(password);

        UserModel user = new UserModel(username,email,hash);
        return userRepository.save(user);
    }


    public UserDashboardResponse userLogin(
            UserLoginRequest req,
            HttpServletResponse res
    ) {

        try {

            UserModel checkEmail =
                    userRepository.findByEmail(req.getEmail())
                            .orElseThrow(() ->
                                    new RuntimeException("User not found")
                            );

            boolean isValid =
                    encoder.matches(
                            req.getPassword(),
                            checkEmail.getPassword()
                    );

            if (!isValid) {
                throw new RuntimeException("Password invalid");
            }

            String token = jwtService.generateToken(checkEmail.getEmail());


            Cookie cookie = new Cookie("token", token);

            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24);

            res.addCookie(cookie);

            return new UserDashboardResponse(
                    checkEmail.getId(),
                    checkEmail.getUsername(),
                    checkEmail.getEmail(),
                    checkEmail.getRole()
            );

        } catch (Exception e) {

            throw new RuntimeException(e.getMessage());
        }

    }


    public UserDashboardResponse getUserDashboard(UserModel user){
        return new UserDashboardResponse(user.getId(),user.getUsername(),user.getEmail(),user.getRole());
    }







}
