package com.example.SpringBoot_Normal_Authetication.Service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	
	// read secret & expiration from application.properties
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }


	public String generateToken(String email) {
		return Jwts.builder()
				 .setSubject(email)
	                .setIssuedAt(new Date(System.currentTimeMillis()))
	                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
	                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
	                .compact();
	}
	
	public boolean validateToken(String token, String email) {
	    try {
	        String tokenEmail = Jwts.parser()
	                .setSigningKey(getSigningKey())
	                .build()
	                .parseClaimsJws(token)
	                .getBody()
	                .getSubject();

	        return tokenEmail.equals(email); // subject must match email
	    } catch (Exception e) {
	        return false; 
	    }
	}

	
}
