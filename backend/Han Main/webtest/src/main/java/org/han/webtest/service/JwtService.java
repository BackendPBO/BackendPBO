package org.han.webtest.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.han.webtest.model.UserModel;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private final String SECRET = "super-secret-key-super-secret-key";

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(String email){
        return Jwts.builder().
                setSubject(email).
                setIssuedAt(new Date()).
                setExpiration(new Date(System.currentTimeMillis() + (60 * 60 * 1000))).
                signWith(key, SignatureAlgorithm.HS256).
                compact();
    }

    public String verifyToken(String token){
        return Jwts.parserBuilder().
                setSigningKey(key).
                build().
                parseClaimsJws(token).
                getBody().
                getSubject();

    }


}
