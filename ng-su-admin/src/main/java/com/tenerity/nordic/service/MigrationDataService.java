package com.tenerity.nordic.service;

import com.tenerity.nordic.client.NgAuthWebClient;
import com.tenerity.nordic.entity.User;
import com.tenerity.nordic.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MigrationDataService {
    Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NgAuthWebClient ngAuthWebClient;

    public void syncUserDataToKeycloak() {
        logger.info("syncUserDataToKeycloak starting...");
        var users = userRepository.findAll();
        var authToken = ngAuthWebClient.getNgAuthToken();
        for (User u : users) {
            if (StringUtils.isBlank(u.getUsername()) || StringUtils.isBlank(u.getEmailAddress()) || !u.getEmailAddress().contains("@")) {
                logger.info(String.format("Username %s is invalid. Skipped!", u.getUsername()));
                continue;
            }
            ngAuthWebClient.ngAuthCreateUser(u.getEmailAddress(), u.getUsername(), "kkkkkk", u.getId().toString(), authToken, false);
        }
    }

    public void deleteKeyCloakUser() {
//        var authToken = ngAuthWebClient.getNgAuthToken();
//        var memberIds = ngAuthWebClient.getAllUsers(authToken);
//        memberIds.forEach(id -> {
//            ngAuthWebClient.ngAuthDeleteUser(id, authToken);
//        });
    }
}
