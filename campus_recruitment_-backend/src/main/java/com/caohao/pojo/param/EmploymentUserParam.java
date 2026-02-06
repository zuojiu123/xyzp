package com.caohao.pojo.param;

import com.caohao.common.enums.impl.EmploymentReplyStatus;
import com.caohao.common.enums.impl.EmploymentUserStatus;
import com.caohao.pojo.entity.EmploymentUser;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * (EmploymentUser)实体类
 *
 * @author caohao
 * @since 2021-11-14 17:48:02
 */
@Data
public class EmploymentUserParam extends EmploymentUser {
    private static final long serialVersionUID = -65741003971629401L;


    /**
     * 回复状态
     */
    private String replyStatus;
    /**
     * 用户状态
     */
    private String userStatus;

    private String companyId;

    private List<String> ids;



}

