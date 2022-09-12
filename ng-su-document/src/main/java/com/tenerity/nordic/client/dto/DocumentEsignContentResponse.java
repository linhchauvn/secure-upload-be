package com.tenerity.nordic.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@Deprecated
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DocumentEsignContentResponse {
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
