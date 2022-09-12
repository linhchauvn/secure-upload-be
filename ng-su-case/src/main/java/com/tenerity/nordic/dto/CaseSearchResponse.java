package com.tenerity.nordic.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CaseSearchResponse extends EntityResponse{
    private List<CaseSearchDto> results = new ArrayList<>();
    private Integer totalItem = 0;
    private Integer totalPage = 0;

    public List<CaseSearchDto> getResults() {
        return results;
    }

    public void setResults(List<CaseSearchDto> results) {
        this.results = results;
    }

    public Integer getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(Integer totalItem) {
        this.totalItem = totalItem;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }
}
