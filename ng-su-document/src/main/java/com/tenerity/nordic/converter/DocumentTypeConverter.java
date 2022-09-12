package com.tenerity.nordic.converter;

import com.tenerity.nordic.enums.DocumentType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter
public class DocumentTypeConverter implements AttributeConverter<DocumentType, String> {
    @Override
    public String convertToDatabaseColumn(DocumentType role) {
        if (role == null) {
            return null;
        }
        return role.getVal();
    }

    @Override
    public DocumentType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return Stream.of(DocumentType.values())
                .filter(c -> c.getVal().equals(dbData))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
