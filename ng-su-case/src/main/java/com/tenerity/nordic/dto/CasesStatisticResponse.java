package com.tenerity.nordic.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CasesStatisticResponse {
    private Long allCases;
    private Long newCases;
    private Long openCasesExceed72Hours;
    private Long openCasesExceed24Hours;
    private Long resolvedCases;

    public Long getAllCases() {
        return allCases;
    }

    public void setAllCases(Long allCases) {
        this.allCases = allCases;
    }

    public Long getNewCases() {
        return newCases;
    }

    public void setNewCases(Long newCases) {
        this.newCases = newCases;
    }

    public Long getOpenCasesExceed72Hours() {
        return openCasesExceed72Hours;
    }

    public void setOpenCasesExceed72Hours(Long openCasesExceed72Hours) {
        this.openCasesExceed72Hours = openCasesExceed72Hours;
    }

    public Long getOpenCasesExceed24Hours() {
        return openCasesExceed24Hours;
    }

    public void setOpenCasesExceed24Hours(Long openCasesExceed24Hours) {
        this.openCasesExceed24Hours = openCasesExceed24Hours;
    }

    public Long getResolvedCases() {
        return resolvedCases;
    }

    public void setResolvedCases(Long resolvedCases) {
        this.resolvedCases = resolvedCases;
    }
}
