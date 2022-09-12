package com.tenerity.nordic.client.dto;

public class EmailMessage {
    private String subject;
    private String htmlPart;
    private String senderName = "Tenerity Communications";

    public EmailMessage(String subject, String htmlPart) {
        this.subject = subject;
        this.htmlPart = htmlPart;
    }
}
