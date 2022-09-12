package com.tenerity.nordic.service;

import com.tenerity.nordic.client.InternalWebClient;
import com.tenerity.nordic.dto.AdminPanelSearchRequest;
import com.tenerity.nordic.dto.ClientDto;
import com.tenerity.nordic.dto.ClientManagementRequest;
import com.tenerity.nordic.entity.Client;
import com.tenerity.nordic.repository.ClientRepository;
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
public class ClientManagementServiceTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private InternalWebClient webClient;
    @InjectMocks
    private ClientManagementService clientManagementService;

    @Test
    void getAllClients_happyCase() {
        var client = new Client();
        client.setId(UUID.randomUUID());
        when(clientRepository.findAll()).thenReturn(Arrays.asList(client));

        var clients = clientManagementService.getAllClients();
        assertNotNull(clients);
        assertEquals(1, clients.getData().size());
    }

    @Test
    void searchClient_happyCase() {
        var client = new Client();
        client.setId(UUID.randomUUID());
        Page<Client> searchRes = new PageImpl<Client>(Arrays.asList(client));
        when(clientRepository.findAll(any(Pageable.class))).thenReturn(searchRes);

        var request = new AdminPanelSearchRequest();
        request.setPage(0);
        request.setSize(10);
        var clients = clientManagementService.searchClient(request);
        assertNotNull(clients);
        assertEquals(1, clients.getTotalItem());
    }

    @Test
    void findClientById_happyCase() {
        var id = UUID.randomUUID();
        var client = new Client();
        client.setId(id);
        when(clientRepository.getById(id)).thenReturn(client);
        var clients = clientManagementService.findClientById(id.toString());
        assertNotNull(clients);
        assertEquals(id, ((ClientDto)clients.getData()).getId());
    }

    @Test
    void createClient_happyCase() {
        var req = new ClientManagementRequest();
        req.setName("name");
        req.setLocale("sv");
        req.setBrandColour("red");
        var client = new Client();
        client.setName("name");
        when(clientRepository.save(any())).thenReturn(client);
        var res = clientManagementService.createClient(req);
        assertNotNull(res);
        assertEquals(req.getName(), ((ClientDto)res.getData()).getName());
    }

    @Test
    void updateClient_happyCase() {
        var id = UUID.randomUUID();
        var req = new ClientManagementRequest();
        req.setName("name");
        var client = new Client();
        client.setId(id);
        client.setName("name");
        when(clientRepository.getById(id)).thenReturn(client);
        when(clientRepository.save(any())).thenReturn(client);
        var res = clientManagementService.updateClient(id.toString(), req);
        assertNotNull(res);
        assertEquals(req.getName(), ((ClientDto)res.getData()).getName());
    }

    @Test
    void deleteClientById_happyCase() {
        var id = UUID.randomUUID();
        var res = clientManagementService.deleteClientById(id.toString());
        assertNotNull(res);
        assertNull(res.getMessage());
    }
}
