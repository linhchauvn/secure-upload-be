package com.tenerity.nordic.service;

import com.tenerity.nordic.client.NgAuthWebClient;
import com.tenerity.nordic.client.dto.CommunicationRequest;
import com.tenerity.nordic.dto.DocumentEmailRequest;
import com.tenerity.nordic.dto.ResetPasswordRequest;
import com.tenerity.nordic.dto.WorkspaceDeletionRequest;
import com.tenerity.nordic.entity.User;
import com.tenerity.nordic.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommunicationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private NgAuthWebClient webClient;
    @InjectMocks
    private CommunicationService communicationService;

    @Test
    void forgetPassword_happyCase() {
        ReflectionTestUtils.setField(communicationService, "key", "Jmnv6Rh1PXJsyJU9");
        ReflectionTestUtils.setField(communicationService, "ivVector", "qy10NEOvj5voU794");
        ReflectionTestUtils.setField(communicationService, "resetPasswordUrl", "localhost:8081/forgotpassword?token=%s");
        String resetPasswordUrl = "localhost:8081/forgotpassword?token=gUW0CCP5Q%2Br612nBAQVIy7ExbHNULftjO1MJxUhMERFUteRDHOkycWKma8VmbAz8";
        String email = "email@tenerity.com";
        UUID id = UUID.fromString("febc7120-00fa-47b9-8668-653c9c628698");
        var user = new User();
        user.setId(id);
        user.setEmailAddress(email);
        when(userRepository.findUserByEmailAddress(email)).thenReturn(Optional.of(user));

        var req = new ResetPasswordRequest();
        req.setEmailAddress(email);
        var res = communicationService.forgetPassword(req);
        assertNotNull(res);
        assertNull(res.getMessage());
        ArgumentCaptor<CommunicationRequest> argument = ArgumentCaptor.forClass(CommunicationRequest.class);
        verify(webClient).sendingEmail(argument.capture());
        assertEquals(resetPasswordUrl, argument.getValue().getMessageConfiguration().getMergeFields().get("reset_password_link"));
    }

    @Test
    void caseUpdateEmail_happyCase() {
        ReflectionTestUtils.setField(communicationService, "caseDetailUrl", "http://localhost/case/%s");
        String email = "email@tenerity.com";
        UUID id = UUID.fromString("febc7120-00fa-47b9-8668-653c9c628698");
        String url = "http://localhost/case/febc7120-00fa-47b9-8668-653c9c628698";

        var req = new DocumentEmailRequest();
        req.setCaseId(id.toString());
        req.setCustomerEmail(email);
        req.setTemplateName("caseUpdateTemplateName");
        var res = communicationService.caseUpdateEmail(req);
        assertNotNull(res);
        ArgumentCaptor<CommunicationRequest> argument = ArgumentCaptor.forClass(CommunicationRequest.class);
        verify(webClient).sendingEmail(argument.capture());
        assertEquals(url, argument.getValue().getMessageConfiguration().getMergeFields().get("url"));
        assertEquals(email, argument.getValue().getMessageConfiguration().getMergeFields().get("email"));
    }

    @Test
    void triggerWorkspaceDeletionEmail_happyCase() {
        String workspaceLabel = "workspaceLabel";
        String workspaceUrl = "workspaceUrl";
        String customerEmail = "customerEmail";
        String caseUrl = "caseUrl";
        String organizationName = "organizationName";


        var req = new WorkspaceDeletionRequest();
        req.setWorkspaceLabel(workspaceLabel);
        req.setWorkspaceUrl(workspaceUrl);
        req.setCustomerEmail(customerEmail);
        req.setCaseUrl(caseUrl);
        req.setOrganizationName(organizationName);
        var res = communicationService.triggerWorkspaceDeletionEmail(req);
        assertNotNull(res);
        ArgumentCaptor<CommunicationRequest> argument = ArgumentCaptor.forClass(CommunicationRequest.class);
        verify(webClient).sendingEmail(argument.capture());
        assertEquals(workspaceLabel, argument.getValue().getMessageConfiguration().getMergeFields().get("workspaceLabel"));
        assertEquals(workspaceUrl, argument.getValue().getMessageConfiguration().getMergeFields().get("workspaceUrl"));
        assertEquals(customerEmail, argument.getValue().getMessageConfiguration().getMergeFields().get("customerEmail"));
        assertEquals(caseUrl, argument.getValue().getMessageConfiguration().getMergeFields().get("caseUrl"));
        assertEquals(organizationName, argument.getValue().getMessageConfiguration().getMergeFields().get("organizationName"));
    }
}
