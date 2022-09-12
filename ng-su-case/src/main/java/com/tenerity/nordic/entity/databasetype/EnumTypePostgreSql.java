package com.tenerity.nordic.entity.databasetype;

import com.tenerity.nordic.enums.CaseStatus;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.EnumType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class EnumTypePostgreSql extends EnumType {
    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if(value == null) {
            st.setNull( index, Types.CHAR );
        }
        else if (value instanceof  CaseStatus) {
            st.setString(index, ((CaseStatus) value).getVal());
        }
        else {
            st.setObject( index, value.toString(), Types.CHAR );
        }
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner) throws SQLException {
        String val = rs.getString(names[0]);
        if(rs.wasNull()) {
            return null;
        }

        return CaseStatus.fromText(val).get();
    }
}
