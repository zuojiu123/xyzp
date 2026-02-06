package com.caohao.pojo.model;

import com.caohao.common.enums.impl.UserRoleEnum;
import com.caohao.pojo.entity.User;
import lombok.Data;

import java.io.Serializable;

/**
 * (User)实体类
 *
 * @author caohao
 * @since 2021-11-14 18:48:16
 */
@Data
public class UserModel extends User {
    private static final long serialVersionUID = -80774066608981041L;

    // UserModel直接继承父类User的所有字段，包括String类型的role字段
    
    /**
     * 获取用户角色枚举
     */
    public UserRoleEnum getRoleEnum() {
        if (getRole() != null) {
            try {
                return UserRoleEnum.valueOf(getRole());
            } catch (IllegalArgumentException e) {
                return UserRoleEnum.Normal_User;
            }
        }
        return UserRoleEnum.Normal_User;
    }




}

