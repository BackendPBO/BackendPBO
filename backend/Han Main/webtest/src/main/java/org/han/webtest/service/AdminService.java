package org.han.webtest.service;

import org.han.webtest.DTO.UserDashboardResponse;
import org.han.webtest.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private  final UserRepository userRepository;

    public AdminService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public List<UserDashboardResponse> getAllUser(){
        return userRepository.findAll().stream().map
                (user-> new UserDashboardResponse
                        (user.getId(), user.getUsername(), user.getEmail(), user.getRole()))
                .toList();
    }
}
