package com.tenerity.nordic.service;

import com.tenerity.nordic.client.InternalWebClient;
import com.tenerity.nordic.dto.AdminPanelSearchRequest;
import com.tenerity.nordic.dto.OrganizationDto;
import com.tenerity.nordic.dto.OrganizationManagementRequest;
import com.tenerity.nordic.entity.Organization;
import com.tenerity.nordic.repository.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrganizationManagementServiceTest {

    @Mock
    private OrganizationRepository organizationRepository;
    @Mock
    private InternalWebClient webClient;
    @InjectMocks
    private OrganizationManagementService organizationManagementService;

    @Test
    void getAllThirdParties_happyCase() {
        var organization = new Organization();
        organization.setId(UUID.randomUUID());
        when(organizationRepository.findAll()).thenReturn(Arrays.asList(organization));

        var res = organizationManagementService.getAllThirdParties();
        assertNotNull(res);
        assertEquals(1, res.getData().size());
    }

    @Test
    void searchClient_happyCase() {
        var organization = new Organization();
        organization.setId(UUID.randomUUID());
        Page<Organization> searchRes = new PageImpl<Organization>(Arrays.asList(organization));
        when(organizationRepository.findAll(any(Pageable.class))).thenReturn(searchRes);

        var request = new AdminPanelSearchRequest();
        request.setPage(0);
        request.setSize(10);
        var res = organizationManagementService.searchOrganization(request);
        assertNotNull(res);
        assertEquals(1, res.getTotalItem());
    }

    @Test
    void findOrganizationById_happyCase() {
        var id = UUID.randomUUID();
        var organization = new Organization();
        organization.setId(id);
        when(organizationRepository.getById(id)).thenReturn(organization);
        var res = organizationManagementService.findOrganizationById(id.toString());
        assertNotNull(res);
        assertEquals(id, ((OrganizationDto) res.getData()).getId());
    }

    @Test
    void createOrg_happyCase() {
        var req = new OrganizationManagementRequest();
        req.setName("name");
        req.setLocale("sv");
        req.setEmailAddress("email");
        var org = new Organization();
        org.setName("name");
        when(organizationRepository.save(any())).thenReturn(org);
        var res = organizationManagementService.createOrganization(req);
        assertNotNull(res);
        assertEquals(req.getName(), ((OrganizationDto) res.getData()).getName());
    }

    @Test
    void updateOrganization_happyCase() {
        var id = UUID.randomUUID();
        var req = new OrganizationManagementRequest();
        req.setName("name");
        var org = new Organization();
        org.setId(id);
        org.setName("name");
        when(organizationRepository.getById(id)).thenReturn(org);
        when(organizationRepository.save(any())).thenReturn(org);
        var res = organizationManagementService.updateOrganization(id.toString(), req);
        assertNotNull(res);
        assertEquals(req.getName(), ((OrganizationDto) res.getData()).getName());
    }

    @Test
    void deleteOrganizationById_happyCase() {
        var id = UUID.randomUUID();
        var res = organizationManagementService.deleteOrganizationById(id.toString());
        assertNotNull(res);
        assertNull(res.getMessage());
    }
}
