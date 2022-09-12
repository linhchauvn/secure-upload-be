package com.tenerity.nordic.security;

import com.tenerity.nordic.client.InternalWebClient;
import com.tenerity.nordic.client.UserDto;
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
    private InternalWebClient webClient;


    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String username = jwt.getClaimAsString(Constants.JWT_USERNAME);
        String memberID = jwt.getClaimAsString(Constants.JWT_MEMBER_ID);
        var user = webClient.getAgentById(UUID.fromString(memberID));
        if (user == null) {
            user = webClient.getAgentById(UUID.fromString(memberID));
        }
        String authority = "ROLE_UNKNOWN";
        if (user != null) {
            authority = "ROLE_" + getRole(user);
            logger.debug("Adding {} to user {}", authority, username);
        }

        return new UsernamePasswordAuthenticationToken(username, "", AuthorityUtils.createAuthorityList(authority));
    }

    private String getRole(UserDto user) {
        if (user.getOrganisation() != null) {
            return "THIRDPARTY";
        } else if (user.getIsAdmin()) {
            return "ADMIN";
        }
        return "AGENT";
    }
}