package com.tenerity.nordic.service;

import com.tenerity.nordic.client.NgAuthWebClient;
import com.tenerity.nordic.client.InternalWebClient;
import com.tenerity.nordic.client.SignicatWebClient;
import com.tenerity.nordic.client.dto.NgAuthLoginResponse;
import com.tenerity.nordic.dto.CustomerLoginRequest;
import com.tenerity.nordic.dto.LoginRequest;
import com.tenerity.nordic.dto.LoginResponse;
import com.tenerity.nordic.dto.LoginUserInfoResponse;
import com.tenerity.nordic.dto.ResetPasswordRequest;
import com.tenerity.nordic.dto.ResetPasswordResponse;
import com.tenerity.nordic.entity.User;
import com.tenerity.nordic.repository.UserRepository;
import com.tenerity.nordic.security.StaticJwtCreator;
import com.tenerity.nordic.util.AdminPanelUtils;
import com.tenerity.nordic.util.Constants;
import com.tenerity.nordic.util.CypherUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthenticationService {
    Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    @Value("${customJwt.key}")
    private String key;
    @Value("${customJwt.ivVector}")
    private String ivVector;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private InternalWebClient webClient;
    @Autowired
    private NgAuthWebClient ngAuthWebClient;
    @Autowired
    private SignicatWebClient signicatWebClient;
    @Autowired
    private StaticJwtCreator jwtCreator;

    public LoginResponse login(LoginRequest request) {
        LoginResponse response = new LoginResponse();
        User user = userRepository.findUserByUsername(request.getUsername());
        if (user == null) {
            log.debug(String.format("Cannot find user by given username: %s", request.getUsername()));
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setCode(Constants.ERROR_CODE_ENTITY_NOT_FOUND);
            response.setMessage(String.format("Cannot find user by given username: %s", request.getUsername()));
            return response;
        }

        var ngAuthResponse = loginUserKeycloak(request);
        if (ngAuthResponse == null) {
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setCode(Constants.ERROR_CODE_WRONG_PASSWORD);
            response.setMessage("Wrong password, please try again.");
            return response;
        }
        response.setAccessToken(ngAuthResponse.getAccessToken());
        response.setRefreshToken(ngAuthResponse.getRefreshToken());
        return response;
    }

    public LoginUserInfoResponse getLoginUserInfo(String accessToken) {
        LoginUserInfoResponse response = new LoginUserInfoResponse();

        Map<String,String> jwtMap = AdminPanelUtils.extractDataFromJWTToken(accessToken);
        String kid = jwtMap.get(Constants.JWT_KID);
        String memberId = jwtMap.get(Constants.JWT_MEMBER_ID);

        if (StringUtils.isBlank(kid)) {
            String username = jwtMap.get(Constants.JWT_USERNAME);
            response.setRole(username);
            response.setWorkspaceId(memberId);
            return response;
        }
        var user = userRepository.findById(UUID.fromString(memberId));
        if (!user.isPresent()) {
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setCode(Constants.ERROR_CODE_INVALID_INPUT);
            response.setMessage("accessToken is invalid");
            return response;
        }
        response.setRole(user.get().getRole().name());
        response.setData(AdminPanelUtils.convertUser(user.get()));
        return response;
    }

    public LoginResponse customerLogin(CustomerLoginRequest request) {
        LoginResponse response = validateCustomerLogin(request);
        if (response.getHttpCode() != null) {
            return response;
        }

        Map<String,Object> claims = new HashMap<>();
        claims.put(Constants.JWT_MEMBER_ID, request.getWorkspaceId());
        claims.put(Constants.JWT_USERNAME, "CUSTOMER");
        if (StringUtils.isNotBlank(request.getCustomerToken())) {
            var wsTokenRole = webClient.workspaceTokenLogin(request);
            if (StringUtils.isBlank(wsTokenRole)) {
                response.setHttpCode(HttpStatus.BAD_REQUEST.value());
                response.setCode(Constants.ERROR_CODE_OTP_LOGIN_INVALID);
                response.setMessage("Wrong OTP code, please try again.");
                return response;
            }
            claims.put(Constants.JWT_USERNAME, wsTokenRole);
        } else {
            String token = signicatWebClient.getAuthToken(request.getSignicatCode());
            if (token != null) {
                String nationalId = signicatWebClient.getUserNationalId(token);
                var isValid = webClient.isAuthorizedSignicatUser(request.getWorkspaceId(), nationalId);
                if (!isValid) {
                    response.setHttpCode(HttpStatus.BAD_REQUEST.value());
                    response.setCode(Constants.ERROR_CODE_BANKID_LOGIN_INVALID);
                    response.setMessage("Bankid login invalid, please try again.");
                    return response;
                }
            }
        }

        String customerToken = jwtCreator.createToken(claims);
        response.setAccessToken(customerToken);
        response.setRefreshToken(customerToken);
        return response;
    }

    public ResetPasswordResponse verifyToken(String token) {
        ResetPasswordResponse response = new ResetPasswordResponse();

        String id = CypherUtils.rsaDecryptWithIvKey(token, key, ivVector);
        try {
            UUID userId = UUID.fromString(id);
            userRepository.getById(userId);
        } catch (NullPointerException | IllegalArgumentException e) {
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setCode(Constants.ERROR_CODE_INVALID_INPUT);
            response.setMessage("Token invalid");
        }
        return response;
    }

    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        ResetPasswordResponse response = new ResetPasswordResponse();
        try {
            User entity;
            if (StringUtils.isNotBlank(request.getToken())) {
                String id = CypherUtils.rsaDecryptWithIvKey(request.getToken(), key, ivVector);
                try {
                    entity = userRepository.getById(UUID.fromString(id));
                } catch (NullPointerException | IllegalArgumentException e) {
                    response.setHttpCode(HttpStatus.BAD_REQUEST.value());
                    response.setCode(Constants.ERROR_CODE_INVALID_INPUT);
                    response.setMessage("Token invalid");
                    return response;
                }
            }
            else {
                if (request.getNewPassword().equals(request.getOldPassword())) {
                    response.setHttpCode(HttpStatus.BAD_REQUEST.value());
                    response.setCode(Constants.ERROR_CODE_DUPLICATED_PASSWORD);
                    response.setMessage("New password must not be the same like old password.");
                    return response;
                }

                entity = userRepository.getById(UUID.fromString(request.getId()));
                var login = new LoginRequest();
                login.setPassword(request.getOldPassword());
                login.setUsername(entity.getUsername());
                try {
                    var ngAuthResponse = loginUserKeycloak(login);
                    if (ngAuthResponse == null) {
                        response.setHttpCode(HttpStatus.BAD_REQUEST.value());
                        response.setCode(Constants.ERROR_CODE_WRONG_PASSWORD);
                        response.setMessage("Old password is not matched.");
                        return response;
                    }

                }catch (Exception ex){
                    response.setHttpCode(HttpStatus.BAD_REQUEST.value());
                    response.setCode(Constants.ERROR_CODE_WRONG_PASSWORD);
                    response.setMessage("Old password is not matched.");
                    return response;
                }
            }

            updatePasswordInKeyCloak(entity.getUsername(), request.getNewPassword());
        } catch (Exception e) {
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setCode(Constants.ERROR_CODE_RESET_PASSWORD_ERROR);
            response.setMessage(e.getMessage());
            return response;
        }
        return response;
    }

    private void updatePasswordInKeyCloak(String username, String newPassword) {
        String ngAuthToken = ngAuthWebClient.getNgAuthToken();
        if (ngAuthToken != null) {
            ngAuthWebClient.ngAuthUpdatePassword(username, newPassword, ngAuthToken);
        }
    }

    private NgAuthLoginResponse loginUserKeycloak(LoginRequest request) {
        String ngAuthToken = ngAuthWebClient.getNgAuthToken();
        if (ngAuthToken != null) {
            var ngAuthLoginResponse = ngAuthWebClient.ngAuthLogin(request.getUsername(), request.getPassword(), ngAuthToken);
            return ngAuthLoginResponse;
        }
        return null;
    }

    private LoginResponse validateCustomerLogin(CustomerLoginRequest request) {
        var response = new LoginResponse();
        if (request == null || request.getWorkspaceId() == null
                || (StringUtils.isBlank(request.getCustomerToken()) && StringUtils.isBlank(request.getSignicatCode()))) {
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setCode(Constants.ERROR_CODE_INVALID_INPUT);
            response.setMessage("Customer Login input invalid.");
            return response;
        }

        return response;
    }
}
