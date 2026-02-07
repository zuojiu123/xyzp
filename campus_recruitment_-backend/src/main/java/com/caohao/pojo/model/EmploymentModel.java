package com.caohao.pojo.model;

import com.caohao.pojo.entity.Employment;
import lombok.Data;

import java.io.Serializable;

/**
 * (Employment)实体类
 *
 * @author caohao
 * @since 2021-11-14 17:17:17
 */
@Data
public class EmploymentModel extends Employment {
    private static final long serialVersionUID = 104752132349503805L;

    /**
     * 关联的公司信息
     */
    private CompanyModel companyModel;

    /**
     * 用户状态
     */
    private String userStatus;

    /**
     * 申请数量
     */
    private Integer applicationCount;

    /**
     * 是否收藏
     */
    private Boolean isCollected;

}

