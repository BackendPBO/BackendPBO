package org.han.webtest.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.han.webtest.model.UserModel;
import org.han.webtest.repository.UserRepository;
import org.han.webtest.service.JwtService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class MiddlewareConfig extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public MiddlewareConfig(JwtService jwtService, UserRepository userRepository){
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.startsWith("/user/login") ||
                path.startsWith("/user/signup") ||
                path.startsWith("/api/classes") ||
                path.startsWith("/h2-console")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = null;

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    token = cookie.getValue();
                }
            }
        }

        if (token == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("NO TOKEN PROVIDED");
            return;
        }

        try {
            String email = jwtService.verifyToken(token);
            UserModel user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Email Not Found"));
            request.setAttribute("user", user);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("INVALID TOKEN");
            return;
        }

        filterChain.doFilter(request, response);
    }
}