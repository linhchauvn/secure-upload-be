package com.tenerity.nordic.security;

import com.tenerity.nordic.repository.UserRepository;
import com.tenerity.nordic.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public class OAuthJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    Logger logger = LoggerFactory.getLogger(OAuthJwtAuthenticationConverter.class);

    @Autowired
    private UserRepository userRepository;


    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String username = jwt.getClaimAsString(Constants.JWT_USERNAME);
        String memberID = jwt.getClaimAsString(Constants.JWT_MEMBER_ID);
        var user = userRepository.findById(UUID.fromString(memberID));
        String authority = "ROLE_UNKNOWN";
        if (user.isPresent()) {
            authority = "ROLE_" + user.get().getRole().name();
            logger.debug("Adding {} to user {}", authority, username);
        }

        return new UsernamePasswordAuthenticationToken(username, "", AuthorityUtils.createAuthorityList(authority));
    }
}