package com.tenerity.nordic.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.tenerity.nordic.enums.CaseStatistic;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class CaseSearchRequest {
    private String keyword;
    private String agentId;
    private String thirdPartyId;
    private Integer page;
    private Integer size;
    private SortColumn sortColumn;
    private CaseStatistic caseStatistic;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateFrom;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTo;


    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getThirdPartyId() {
        return thirdPartyId;
    }

    public void setThirdPartyId(String thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public SortColumn getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(SortColumn sortColumn) {
        this.sortColumn = sortColumn;
    }

    public CaseStatistic getCaseStatistic() {
        return caseStatistic;
    }

    public void setCaseStatistic(CaseStatistic caseStatistic) {
        this.caseStatistic = caseStatistic;
    }

    public LocalDateTime getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDateTime dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDateTime getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDateTime dateTo) {
        this.dateTo = dateTo;
    }
}
