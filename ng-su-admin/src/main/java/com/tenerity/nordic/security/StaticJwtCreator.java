package com.tenerity.nordic.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Service
public class StaticJwtCreator {
    Logger logger = LoggerFactory.getLogger(StaticJwtCreator.class);

    @Value("${customJwt.issuer}")
    String issuer;

    @Value("${customJwt.hmacsecret}")
    String secret;

    public String createToken(Map<String,Object> payloadClaims) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer(issuer)
                    .withSubject("static-jwt")
                    .withExpiresAt(timeOffset(60 * 60 * 12))
                    .withPayload(payloadClaims)
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    private Date timeOffset(int seconds) {
        Date current = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.add(Calendar.SECOND, seconds);
        return calendar.getTime();
    }
}
