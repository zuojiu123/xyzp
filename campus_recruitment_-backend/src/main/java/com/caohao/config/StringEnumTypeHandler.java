package com.caohao.config;

import com.caohao.common.enums.EnumCode;
import com.caohao.common.enums.EnumEntity;
import com.caohao.common.enums.impl.UserRoleEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 字符串枚举类型处理器
 * 用于处理数据库中存储字符串的枚举类型
 */
public class StringEnumTypeHandler extends BaseTypeHandler<EnumCode> {
    private Class<EnumCode> type;

    public StringEnumTypeHandler(Class<EnumCode> type) {
        if (type == null)
            throw new IllegalArgumentException("Type argument cannot be null");
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, EnumCode parameter, JdbcType jdbcType) throws SQLException {
        // 存储枚举名称到数据库
        ps.setString(i, ((Enum<?>) parameter).name());
    }

    @Override
    public EnumEntity getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String enumName = rs.getString(columnName);
        if (rs.wasNull()) {
            return null;
        } else {
            return locateEnumByName(enumName);
        }
    }

    @Override
    public EnumEntity getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String enumName = rs.getString(columnIndex);
        if (rs.wasNull()) {
            return null;
        } else {
            return locateEnumByName(enumName);
        }
    }

    @Override
    public EnumEntity getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String enumName = cs.getString(columnIndex);
        if (cs.wasNull()) {
            return null;
        } else {
            return locateEnumByName(enumName);
        }
    }

    /**
     * 根据枚举名称定位枚举
     */
    private EnumEntity locateEnumByName(String enumName) {
        if (enumName == null) {
            return null;
        }
        
        EnumCode[] enums = type.getEnumConstants();
        if (enums == null) {
            throw new IllegalArgumentException(type.getSimpleName() + " does not represent an enum type.");
        }
        
        for (EnumCode enumCode : enums) {
            if (((Enum<?>) enumCode).name().equals(enumName)) {
                EnumEntity enumEntity = new EnumEntity();
                enumEntity.setEnumCode(enumCode);
                enumEntity.setCode(enumCode.getCode());
                enumEntity.setMsg(enumCode.getMsg());
                return enumEntity;
            }
        }
        
        throw new IllegalArgumentException("未知的枚举类型：" + enumName + "，请核对" + type.getSimpleName());
    }
}