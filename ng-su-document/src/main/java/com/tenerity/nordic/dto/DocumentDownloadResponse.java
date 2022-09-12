package com.tenerity.nordic.dto;

import org.springframework.http.MediaType;

import java.io.ByteArrayOutputStream;

public class DocumentDownloadResponse extends EntityResponse {
    private String filename;
    private MediaType contentType;
    private byte[] contentData;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public MediaType getContentType() {
        return contentType;
    }

    public void setContentType(MediaType contentType) {
        this.contentType = contentType;
    }

    public byte[] getContentData() {
        return contentData;
    }

    public void setContentData(byte[] contentData) {
        this.contentData = contentData;
    }
}
