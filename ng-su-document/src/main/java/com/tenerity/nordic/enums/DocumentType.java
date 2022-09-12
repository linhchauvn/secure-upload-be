package com.tenerity.nordic.enums;

import java.util.Arrays;
import java.util.Optional;

public enum DocumentType {
    PROOF_OF_IDENTITY("poi"),
    POWER_OF_ATTORNEY("poa"),
    CASE_DOC("casedoc-1");

    private String val;

    private DocumentType(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    public static Optional<DocumentType> fromText(String text) {
        return Arrays.stream(values())
                .filter(bl -> bl.val.equalsIgnoreCase(text))
                .findFirst();
    }

}
