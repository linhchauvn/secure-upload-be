package com.tenerity.nordic.dto;

public enum AdminPanelSortColumn {
    EMAIL_ADDRESS("emailAddress"),
    USERNAME("username"),
    IS_ADMIN("role"),
    IS_OUT_OF_OFFICE("isOOO"),
    ORGANIZATION_NAME("organization.name"),
    NAME("name"),
    LOCALE("locale"),
    BRAND_COLOUR("navColour");

    private String val;

    private AdminPanelSortColumn(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }
}
