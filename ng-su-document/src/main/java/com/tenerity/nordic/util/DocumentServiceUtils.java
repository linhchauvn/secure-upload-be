package com.tenerity.nordic.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenerity.nordic.dto.DocumentDto;
import com.tenerity.nordic.dto.DocumentListResponse;
import com.tenerity.nordic.dto.DocumentManagementResponse;
import com.tenerity.nordic.entity.Document;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import java.util.Base64;
import java.util.UUID;

public final class DocumentServiceUtils {

    public static DocumentDto convertDocument(Document entity) {
        if (entity == null) {
            return null;
        }
        DocumentDto dto = new DocumentDto();
        dto.setId(entity.getId());
        dto.setContentType(entity.getContentType());
        dto.setFilename(entity.getFilename());
        dto.setFileUrl(entity.getFilePath());
        dto.setUploadTime(entity.getUploadTime());
        dto.setESigned(entity.getESigned());
        dto.setDocumentType(entity.getKey());
        dto.setLabel(entity.getLabel());
        dto.setMarkAsRead(entity.getMarkAsRead());
        dto.setNeedESignature(entity.getNeedESignature());
        dto.setOriginatorRef(entity.getOriginatorRef());
        dto.setOriginatorType(entity.getOriginatorType());
        dto.setSignicatRequestId(entity.getSignicatRequestId());
        dto.setSignicatTaskId(entity.getSignicatTaskId());
        dto.setCaseId(entity.getCaseId());
        return dto;
    }

    public static DocumentManagementResponse validateUUID(String id) {
        DocumentManagementResponse response = new DocumentManagementResponse();
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

    public static DocumentListResponse validateCaseId(String caseId) {
        DocumentListResponse response = new DocumentListResponse();
        try {
            UUID.fromString(caseId);
        } catch (NullPointerException | IllegalArgumentException e) {
            response.setHttpCode(HttpStatus.BAD_REQUEST.value());
            response.setCode(Constants.ERROR_CODE_INVALID_INPUT);
            response.setMessage("Invalid caseId format");
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

    public static boolean isCustomer(String token) {
        if (StringUtils.isBlank(token) || !token.startsWith("Bearer")) {
            return false;
        }
        String[] chunks = token.replace("Bearer", "").trim().split("\\.");

        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode parentNode = mapper.readTree(payload);
            JsonNode memberIdNode = parentNode.at("/preferred_username");
            if (!memberIdNode.isMissingNode() && "CUSTOMER".equals(memberIdNode.asText())) {
                return true;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return false;
    }

}
