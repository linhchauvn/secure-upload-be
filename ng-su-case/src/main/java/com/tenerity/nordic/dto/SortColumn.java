package com.tenerity.nordic.dto;

public class SortColumn {
    private String columnName;
    private Boolean descending;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Boolean getDescending() {
        if (descending == null) {
            return Boolean.FALSE;
        }
        return descending;
    }

    public void setDescending(Boolean descending) {
        this.descending = descending;
    }
}
