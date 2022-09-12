package com.tenerity.nordic.service;

import com.tenerity.nordic.client.InternalWebClient;
import com.tenerity.nordic.client.NgAuthWebClient;
import com.tenerity.nordic.client.SignicatWebClient;
import com.tenerity.nordic.client.dto.NgAuthLoginResponse;
import com.tenerity.nordic.dto.CustomerLoginRequest;
import com.tenerity.nordic.dto.LoginRequest;
import com.tenerity.nordic.dto.LoginResponse;
import com.tenerity.nordic.dto.ResetPasswordRequest;
import com.tenerity.nordic.entity.User;
import com.tenerity.nordic.repository.UserRepository;
import com.tenerity.nordic.security.StaticJwtCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private InternalWebClient webClient;
    @Mock
    private NgAuthWebClient ngAuthWebClient;
    @Mock
    private SignicatWebClient signicatWebClient;
    @Mock
    private StaticJwtCreator jwtCreator;
    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void login_happyCase() {
        String username = "username";
        String password = "password";
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        var user = new User();
        user.setUsername(username);
        user.setPassword(password);
        when(userRepository.findUserByUsername(any())).thenReturn(user);
        when(ngAuthWebClient.getNgAuthToken()).thenReturn("ngAuthToken");
        var ngAuthLoginResponse = new NgAuthLoginResponse();
        ngAuthLoginResponse.setAccessToken(accessToken);
        ngAuthLoginResponse.setRefreshToken(refreshToken);
        when(ngAuthWebClient.ngAuthLogin(any(),any(),any())).thenReturn(ngAuthLoginResponse);

        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);
        LoginResponse response = authenticationService.login(request);
        assertNotNull(response);
        assertEquals(accessToken, response.getAccessToken());
        assertEquals(refreshToken, response.getRefreshToken());
    }

    @Test
    void getLoginUserInfo_happyCase() {
        String workspaceId = "febc7120-00fa-47b9-8668-653c9c628698";
        String accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9." +
                "eyJtZW1iZXJfaWQiOiJmZWJjNzEyMC0wMGZhLTQ3YjktODY2OC02NTNjOWM2Mjg2OTgiLCJzdWIiOiJzdGF0aWMtand0IiwiaXNzIjoic2VjdXJlLnVwbG9hZCIsInByZWZlcnJlZF91c2VybmFtZSI6IkNVU1RPTUVSIiwiZXhwIjoxNjQyMjY1MTI2fQ." +
                "rW-qkT3nkvuMhuZooSxoIShKL97FfeznnmRr-AR1_iI";

        var response = authenticationService.getLoginUserInfo(accessToken);
        assertNotNull(response);
        assertEquals("CUSTOMER", response.getRole());
        assertEquals(workspaceId, response.getWorkspaceId());
    }

    @Test
    void customerLogin_happyCase() {
        String workspaceId = "febc7120-00fa-47b9-8668-653c9c628698";
        String customerToken = "123456";
        String accessToken = "accessToken";
        when(webClient.workspaceTokenLogin(any())).thenReturn("CUSTOMER");
        when(jwtCreator.createToken(any())).thenReturn(accessToken);

        var request = new CustomerLoginRequest();
        request.setWorkspaceId(workspaceId);
        request.setCustomerToken(customerToken);
        LoginResponse response = authenticationService.customerLogin(request);
        assertNotNull(response);
        assertEquals(accessToken, response.getAccessToken());
        assertEquals(accessToken, response.getRefreshToken());
    }

    @Test
    void verifyToken_happyCase() {
        String workspaceId = "febc7120-00fa-47b9-8668-653c9c628698";
        String customerToken = "123456";
        String accessToken = "accessToken";
        when(webClient.workspaceTokenLogin(any())).thenReturn("CUSTOMER");
        when(jwtCreator.createToken(any())).thenReturn(accessToken);

        var request = new CustomerLoginRequest();
        request.setWorkspaceId(workspaceId);
        request.setCustomerToken(customerToken);
        LoginResponse response = authenticationService.customerLogin(request);
        assertNotNull(response);
        assertEquals(accessToken, response.getAccessToken());
        assertEquals(accessToken, response.getRefreshToken());
    }

    @Test
    void resetPassword_happyCase() {
        ReflectionTestUtils.setField(authenticationService, "key", "Jmnv6Rh1PXJsyJU9");
        ReflectionTestUtils.setField(authenticationService, "ivVector", "qy10NEOvj5voU794");
        String id = "febc7120-00fa-47b9-8668-653c9c628698";
        String token = "gUW0CCP5Q+r612nBAQVIy7ExbHNULftjO1MJxUhMERFUteRDHOkycWKma8VmbAz8";
        String emailAddress = "resetpwd@austrax.com";
        String newPassword = "newPassword";
        when(userRepository.getById(UUID.fromString(id))).thenReturn(new User());

        var request = new ResetPasswordRequest();
        request.setEmailAddress(emailAddress);
        request.setToken(token);
        request.setNewPassword(newPassword);
        var response = authenticationService.resetPassword(request);
        assertNotNull(response);
        assertNull(response.getMessage());
    }
}
