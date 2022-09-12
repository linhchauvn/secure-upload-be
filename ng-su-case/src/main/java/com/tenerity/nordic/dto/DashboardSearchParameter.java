package com.tenerity.nordic.dto;

import com.tenerity.nordic.enums.CaseStatistic;
import com.tenerity.nordic.enums.CaseStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class DashboardSearchParameter {
    private String keyword;
    private List<UUID> agentFilteredIds;
    private List<UUID> clientFilteredIds;
    private UUID agentId;
    private UUID thirdPartyId;
    private CaseStatus status;
    private LocalDateTime timeFrom;
    private LocalDateTime timeTo;
    private CaseStatistic caseStatistic;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<UUID> getAgentFilteredIds() {
        return agentFilteredIds;
    }

    public void setAgentFilteredIds(List<UUID> agentFilteredIds) {
        this.agentFilteredIds = agentFilteredIds;
    }

    public List<UUID> getClientFilteredIds() {
        return clientFilteredIds;
    }

    public void setClientFilteredIds(List<UUID> clientFilteredIds) {
        this.clientFilteredIds = clientFilteredIds;
    }

    public UUID getAgentId() {
        return agentId;
    }

    public void setAgentId(UUID agentId) {
        this.agentId = agentId;
    }

    public UUID getThirdPartyId() {
        return thirdPartyId;
    }

    public void setThirdPartyId(UUID thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }

    public CaseStatus getStatus() {
        return status;
    }

    public void setStatus(CaseStatus status) {
        this.status = status;
    }

    public LocalDateTime getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(LocalDateTime timeFrom) {
        this.timeFrom = timeFrom;
    }

    public LocalDateTime getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(LocalDateTime timeTo) {
        this.timeTo = timeTo;
    }

    public CaseStatistic getCaseStatistic() {
        return caseStatistic;
    }

    public void setCaseStatistic(CaseStatistic caseStatistic) {
        this.caseStatistic = caseStatistic;
    }
}
