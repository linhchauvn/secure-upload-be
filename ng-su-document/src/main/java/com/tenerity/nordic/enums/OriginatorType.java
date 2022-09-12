package com.tenerity.nordic.enums;

import java.util.Arrays;
import java.util.Optional;

public enum OriginatorType {
    ORIGINATOR_TYPE_CUSTOMER("originator-type/customer"),
    ORIGINATOR_TYPE_AGENT("originator-type/agent"),
    ORIGINATOR_TYPE_THIRD_PARTY("originator-type/third-party");

    private String val;

    private OriginatorType(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    public static Optional<OriginatorType> fromText(String text) {
        return Arrays.stream(values())
                .filter(bl -> bl.val.equalsIgnoreCase(text))
                .findFirst();
    }

}
