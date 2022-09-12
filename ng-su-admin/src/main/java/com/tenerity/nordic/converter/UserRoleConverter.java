package com.tenerity.nordic.converter;

import com.tenerity.nordic.enums.UserRole;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter
public class UserRoleConverter implements AttributeConverter<UserRole, String> {
    @Override
    public String convertToDatabaseColumn(UserRole role) {
        if (role == null) {
            return null;
        }
        return role.getVal();
    }

    @Override
    public UserRole convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return Stream.of(UserRole.values())
                .filter(c -> c.getVal().equals(dbData))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
