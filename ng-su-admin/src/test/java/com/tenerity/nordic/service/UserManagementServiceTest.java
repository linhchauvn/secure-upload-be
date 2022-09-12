package com.tenerity.nordic.service;

import com.tenerity.nordic.client.NgAuthWebClient;
import com.tenerity.nordic.dto.AdminPanelSearchRequest;
import com.tenerity.nordic.dto.UserDto;
import com.tenerity.nordic.dto.UserManagementRequest;
import com.tenerity.nordic.entity.User;
import com.tenerity.nordic.enums.UserRole;
import com.tenerity.nordic.repository.OrganizationRepository;
import com.tenerity.nordic.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserManagementServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private OrganizationRepository organizationRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserNotificationService userNotificationService;
    @Mock
    private NgAuthWebClient ngAuthWebClient;
    @InjectMocks
    private UserManagementService userManagementService;

    @Test
    void getAllAgents_happyCase() {
        var user = new User();
        user.setId(UUID.randomUUID());
        when(userRepository.findAllByRoleIn(Arrays.asList(UserRole.ADMIN, UserRole.AGENT))).thenReturn(Arrays.asList(user));

        var res = userManagementService.getAllAgents();
        assertNotNull(res);
        assertEquals(1, res.getData().size());
    }

    @Test
    void searchUser_happyCase() {
        var user = new User();
        user.setId(UUID.randomUUID());
        Page<User> searchRes = new PageImpl<>(Arrays.asList(user));
        when(userRepository.findAllByRoleIn(any(), any())).thenReturn(searchRes);

        var request = new AdminPanelSearchRequest();
        request.setPage(0);
        request.setSize(10);
        var res = userManagementService.searchUser(request, Arrays.asList(UserRole.ADMIN, UserRole.AGENT));
        assertNotNull(res);
        assertEquals(1, res.getTotalItem());
    }

    @Test
    void findAgentById_happyCase() {
        var id = UUID.randomUUID();
        var user = new User();
        user.setId(id);
        when(userRepository.findUserByIdAndRoleIn(id, Arrays.asList(UserRole.ADMIN, UserRole.AGENT))).thenReturn(user);
        var res = userManagementService.findAgentById(id.toString());
        assertNotNull(res);
        assertEquals(id, ((UserDto) res.getData()).getId());
    }

    @Test
    void findThirdPartyUserById_happyCase() {
        var id = UUID.randomUUID();
        var user = new User();
        user.setId(id);
        when(userRepository.findUserByIdAndRole(id, UserRole.THIRDPARTY)).thenReturn(user);
        var res = userManagementService.findThirdPartyUserById(id.toString());
        assertNotNull(res);
        assertEquals(id, ((UserDto) res.getData()).getId());
    }


    @Test
    void createUser_happyCase() {
        var req = new UserManagementRequest();
        req.setUsername("username");
        req.setPassword("password");
        req.setIsAdmin(true);
        req.setEmailAddress("email");
        req.setIsOutOfOffice(false);
        var user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("username");
        user.setPassword("password");
        user.setRole(UserRole.ADMIN);
        user.setEmailAddress("email");
        when(userRepository.save(any())).thenReturn(user);
        when(ngAuthWebClient.ngAuthCreateUser(any(), any(), any(), any(), any(), any())).thenReturn("authUserId");
        var res = userManagementService.createUser(req, false);
        assertNotNull(res);
        assertEquals(req.getUsername(), ((UserDto) res.getData()).getUsername());
    }

    @Test
    void updateUser_happyCase() {
        String accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9." +
                "eyJtZW1iZXJfaWQiOiJmZWJjNzEyMC0wMGZhLTQ3YjktODY2OC02NTNjOWM2Mjg2OTgiLCJzdWIiOiJzdGF0aWMtand0IiwiaXNzIjoic2VjdXJlLnVwbG9hZCIsInByZWZlcnJlZF91c2VybmFtZSI6IkNVU1RPTUVSIiwiZXhwIjoxNjQyMjY1MTI2fQ." +
                "rW-qkT3nkvuMhuZooSxoIShKL97FfeznnmRr-AR1_iI";
        var id = UUID.fromString("febc7120-00fa-47b9-8668-653c9c628698");
        var req = new UserManagementRequest();
        req.setUsername("username");
        var user = new User();
        user.setId(id);
        user.setUsername("username");
        when(userRepository.getById(id)).thenReturn(user);
        when(userRepository.save(any())).thenReturn(user);
        when(ngAuthWebClient.getNgAuthToken()).thenReturn(accessToken);
        when(ngAuthWebClient.ngAuthUpdateUsername(any(), any(), any())).thenReturn(true);
//        when(ngAuthWebClient.ngAuthUpdatePassword(any(), any(), any())).thenReturn("OK");
        var res = userManagementService.updateUser(id.toString(), req, true);
        assertNotNull(res);
        assertEquals(req.getUsername(), ((UserDto) res.getData()).getUsername());
    }

    @Test
    void deleteUserById_happyCase() {
        var id = UUID.randomUUID();
        var res = userManagementService.deleteUserById(id.toString());
        assertNotNull(res);
        assertNull(res.getMessage());
    }
}
