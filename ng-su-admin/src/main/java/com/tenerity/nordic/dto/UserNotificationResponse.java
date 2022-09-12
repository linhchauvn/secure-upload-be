package com.tenerity.nordic.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserNotificationResponse extends EntityResponse {
    private List<UserNotificationDto> results = new ArrayList<>();
    private Long totalItem;
    private Integer totalPage;

    public List<UserNotificationDto> getResults() {
        return results;
    }

    public void setResults(List<UserNotificationDto> results) {
        this.results = results;
    }

    public Long getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(Long totalItem) {
        this.totalItem = totalItem;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }
}
