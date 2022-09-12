package com.tenerity.nordic.enums;

public enum CaseDashboardSortColumn {
    SUPER_OFFICE_ID("superOfficeID"),
    ASSIGNED_AGENT("assignedAgent.emailAddress"),
    CLIENT("client.name"),
    CUSTOMER_EMAIL("customerEmail"),
    LAST_UPDATED("lastUpdated");

    private String val;

    private CaseDashboardSortColumn(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }
}
