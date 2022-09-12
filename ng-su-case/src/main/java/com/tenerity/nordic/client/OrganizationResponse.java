package com.tenerity.nordic.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tenerity.nordic.dto.EntityResponse;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OrganizationResponse extends EntityResponse {
    private List<OrganizationDto> organizations;

    public List<OrganizationDto> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<OrganizationDto> organizations) {
        this.organizations = organizations;
    }
}
