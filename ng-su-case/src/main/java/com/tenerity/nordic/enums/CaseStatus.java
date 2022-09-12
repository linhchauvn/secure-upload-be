package com.tenerity.nordic.enums;

import java.util.Arrays;
import java.util.Optional;

public enum CaseStatus {
    OPEN("status/open"),
    CLOSE("status/closed");

    private String val;

    private CaseStatus(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    public static Optional<CaseStatus> fromText(String text) {
        return Arrays.stream(values())
                .filter(bl -> bl.val.equalsIgnoreCase(text))
                .findFirst();
    }
}
