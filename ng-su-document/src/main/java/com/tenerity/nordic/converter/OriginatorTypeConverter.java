package com.tenerity.nordic.converter;

import com.tenerity.nordic.enums.OriginatorType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter
public class OriginatorTypeConverter implements AttributeConverter<OriginatorType, String> {
    @Override
    public String convertToDatabaseColumn(OriginatorType role) {
        if (role == null) {
            return null;
        }
        return role.getVal();
    }

    @Override
    public OriginatorType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return Stream.of(OriginatorType.values())
                .filter(c -> c.getVal().equals(dbData))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
