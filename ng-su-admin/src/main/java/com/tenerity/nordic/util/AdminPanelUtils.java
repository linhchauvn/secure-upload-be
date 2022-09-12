package com.tenerity.nordic.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenerity.nordic.dto.AdminPanelManagementResponse;
import com.tenerity.nordic.dto.AdminPanelSearchRequest;
import com.tenerity.nordic.dto.AdminPanelSearchResponse;
import com.tenerity.nordic.dto.AdminPanelSortColumn;
import com.tenerity.nordic.dto.ClientDto;
import com.tenerity.nordic.dto.OrganizationDto;
import com.tenerity.nordic.dto.SortColumn;
import com.tenerity.nordic.dto.UserDto;
import com.tenerity.nordic.entity.Client;
import com.tenerity.nordic.entity.Organization;
import com.tenerity.nordic.entity.User;
import com.tenerity.nordic.enums.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class AdminPanelUtils {

    public static AdminPanelSearchResponse validateSearchRequest(AdminPanelSearchRequest searchRequest) {
        StringBuilder msg = new StringBuilder();
        if (searchRequest.getPage() == null) {
            msg.append("page is required. ");
        }
        if (searchRequest.getSize() == null) {
            msg.append("size is required. ");
        }

        AdminPanelSearchResponse response = new AdminPanelSearchResponse();
        if (msg.length() > 0) {
            response.setMessage(msg.toString());
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setCode(Constants.ERROR_CODE_INVALID_INPUT);
        }
        return response;
    }

    public static AdminPanelManagementResponse validateUUID(String id) {
        AdminPanelManagementResponse response = new AdminPanelManagementResponse();
        try {
            UUID.fromString(id);
        } catch (NullPointerException | IllegalArgumentException e) {
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setCode(Constants.ERROR_CODE_INVALID_INPUT);
            response.setMessage("Invalid id format");
            return response;
        }
        return response;
    }

    public static Pageable buildPagingAndSorting(Integer page, Integer size, SortColumn sortColumn) {
        Sort sort = Sort.unsorted();
        if (sortColumn != null) {
            AdminPanelSortColumn sortColumnMap = AdminPanelSortColumn.valueOf(sortColumn.getColumnName());
            sort = Sort.by(sortColumn.getDescending() ? Sort.Direction.DESC : Sort.Direction.ASC, sortColumnMap.getVal());
        }
        return PageRequest.of(page, size, sort);
    }

    public static UserDto convertUser(User user) {
        if (user == null) {
            return null;
        }

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmailAddress(user.getEmailAddress());
        dto.setIsOutOfOffice(user.getOOO());
        dto.setIsAdmin(setIsAdmin(user.getRole()));
        dto.setLocale(user.getLocale());
        dto.setMustChangePassword(user.getMustChangePassword());
        dto.setName(user.getName());
        dto.setOrganisation(convertOrganization(user.getOrganization()));
        dto.setUsername(user.getUsername());
        return dto;
    }

    public static ClientDto convertClient(Client entity) {
        if (entity == null) {
            return null;
        }

        ClientDto dto = new ClientDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLocale(entity.getLocale());
        dto.setBrandColour(entity.getNavColour());
        return dto;
    }

    public static OrganizationDto convertOrganization(Organization organization) {
        if (organization == null) {
            return null;
        }

        OrganizationDto dto = new OrganizationDto();
        dto.setId(organization.getId());
        dto.setLocale(organization.getLocale());
        dto.setEmailAddress(organization.getEmailAddress());
        dto.setName(organization.getName());
        return dto;
    }

    public static Map<String, String> extractDataFromJWTToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }

        Map<String, String> map = new HashMap<>();

        String[] chunks = token.replace("Bearer", "").trim().split("\\.");

        Base64.Decoder decoder = Base64.getUrlDecoder();
        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode headerNode = mapper.readTree(header);
            JsonNode kidNode = headerNode.at("/kid");
            if (!kidNode.isMissingNode()) {
                map.put(Constants.JWT_KID, kidNode.asText());
            }

            JsonNode parentNode = mapper.readTree(payload);
            JsonNode issNode = parentNode.at("/iss");
            if (!issNode.isMissingNode()) {
                map.put(Constants.JWT_ISSUER, issNode.asText());
            }
            JsonNode memberIdNode = parentNode.at("/member_id");
            if (!memberIdNode.isMissingNode()) {
                map.put(Constants.JWT_MEMBER_ID, memberIdNode.asText());
            }
            JsonNode usernameNode = parentNode.at("/preferred_username");
            if (!usernameNode.isMissingNode()) {
                map.put(Constants.JWT_USERNAME, usernameNode.asText());
            }
            JsonNode emailNode = parentNode.at("/email");
            if (!emailNode.isMissingNode()) {
                map.put(Constants.JWT_EMAIL, emailNode.asText());
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return map;
    }

    private static Boolean setIsAdmin(UserRole role) {
        if (UserRole.ADMIN == role) {
            return Boolean.TRUE;
        }
        if (UserRole.AGENT == role) {
            return false;
        }
        return null;
    }
}
