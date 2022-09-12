package com.tenerity.nordic.security;

import com.tenerity.nordic.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jwt.Jwt;


public class StaticJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    Logger logger = LoggerFactory.getLogger(StaticJwtAuthenticationConverter.class);

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String username = jwt.getClaimAsString(Constants.JWT_USERNAME);
        String authority = "ROLE_" + username;
        logger.debug("Adding {} to user {}", authority, username);
        return new UsernamePasswordAuthenticationToken(username, "", AuthorityUtils.createAuthorityList(authority));
    }
}
