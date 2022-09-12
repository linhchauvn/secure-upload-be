package com.tenerity.nordic.converter;

import com.tenerity.nordic.enums.CaseStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter
public class CaseStatusConverter implements AttributeConverter<CaseStatus, String> {
    @Override
    public String convertToDatabaseColumn(CaseStatus role) {
        if (role == null) {
            return null;
        }
        return role.getVal();
    }

    @Override
    public CaseStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return Stream.of(CaseStatus.values())
                .filter(c -> c.getVal().equals(dbData))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
