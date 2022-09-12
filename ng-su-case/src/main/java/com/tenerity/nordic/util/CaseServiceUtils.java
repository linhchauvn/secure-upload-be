package com.tenerity.nordic.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenerity.nordic.client.UserDto;
import com.tenerity.nordic.dto.CaseDto;
import com.tenerity.nordic.dto.CaseManagementResponse;
import com.tenerity.nordic.dto.WorkspaceDto;
import com.tenerity.nordic.entity.Case;
import com.tenerity.nordic.entity.Workspace;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;
import java.util.stream.Collectors;

public final class CaseServiceUtils {

    public static CaseDto convertCase(Case entity) {
        if (entity == null) {
            return null;
        }
        CaseDto dto = new CaseDto();

        dto.setId(entity.getId());
        dto.setSuperOfficeId(entity.getSuperOfficeID());
        UserDto agentDto = new UserDto();
        agentDto.setId(entity.getAssignedAgent());
        dto.setAssignedAgent(agentDto);
        dto.setLastUpdated(entity.getLastUpdated());
        dto.setClientId(entity.getClientId());
        dto.setCustomerEmail(entity.getCustomerEmail());
        dto.setCode(entity.getCode());
        dto.setCustomerDOB(entity.getCustomerDOB());
        dto.setCustomerFirstName(entity.getCustomerFirstName());
        dto.setCustomerLastName(entity.getCustomerLastName());
        dto.setCustomerNationalId(entity.getCustomerNationalId());
        dto.setCustomerTokenHash(entity.getCustomerTokenHash());
        dto.setDocumentsExpunged(entity.getDocumentsExpunged());
        dto.setNeedAgentNotification(entity.getNeedAgentNotification());
        dto.setStatus(entity.getStatus().name());
        if (entity.getWorkspaces() != null) {
            dto.setWorkspaces(entity.getWorkspaces().stream().map(CaseServiceUtils::convertWorkspace).collect(Collectors.toList()));
        }
        return dto;
    }

    public static WorkspaceDto convertWorkspace(Workspace entity) {
        if (entity == null) {
            return null;
        }
        WorkspaceDto dto = new WorkspaceDto();
        dto.setId(entity.getId());
        dto.setBankIdLogin(entity.getBankIdLogin());
        dto.setBelongToCustomer(entity.getBelongToCustomer());
        dto.setCode(entity.getCode());
        dto.setCustomerTokenHash(entity.getCustomerTokenHash());
        dto.setDocumentIds(entity.getDocuments());
        dto.setLabel(entity.getLabel());
        dto.setThirdPartyId(entity.getThirdPartyId());
        dto.setThirdPartyRef(entity.getThirdPartyRef());
        if (entity.getCasee() != null) {
            dto.setCaseId(entity.getCasee().getId());
            dto.setSuperOfficeId(entity.getCasee().getSuperOfficeID());
        }
        dto.setLastAccess(entity.getLastAccess());

        return dto;
    }

    public static CaseManagementResponse validateUUID(String id) {
        CaseManagementResponse response = new CaseManagementResponse();
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

    public static String extractUserUUIDFromToken(String token) {
        if (StringUtils.isBlank(token) || !token.startsWith("Bearer")) {
            return null;
        }
        String[] chunks = token.replace("Bearer", "").trim().split("\\.");

        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode parentNode = mapper.readTree(payload);
            JsonNode memberIdNode = parentNode.at("/member_id");
            if (!memberIdNode.isMissingNode()) {
                return memberIdNode.asText();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean isInternal(String token, String internal){
        if (StringUtils.isBlank(token) || !token.startsWith("Basic")) {
            return false;
        }
        String chunk = token.replace("Basic", "").trim();
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunk));
        return  payload.equals(internal);
    }


    public static boolean isCustomerAccess(String accessToken) {
        return !accessToken.startsWith("Bearer");
    }

    public static String SHA256(String text) throws NoSuchAlgorithmException {
        if (null != text) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] byteData = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (int i = 0; i < byteData.length; i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        }

        return null;
    }
}
