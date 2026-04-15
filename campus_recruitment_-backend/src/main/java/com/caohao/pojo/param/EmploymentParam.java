package com.caohao.pojo.param;

import com.caohao.pojo.entity.Employment;
import lombok.Data;

/**
 * (Employment)实体类
 *
 * @author caohao
 * @since 2021-11-14 17:17:17
 */
@Data
public class EmploymentParam extends Employment {
    private static final long serialVersionUID = 104752132349503805L;

    // EmploymentParam直接继承父类Employment的所有字段
    // 包括String类型的education、status字段
    
    // 筛选字段
    private String cityFilter; // 城市筛选
    private String salaryRange; // 薪资范围筛选
    private String companyScale; // 公司规模筛选
    private String sortBy; // 排序字段
    private String minSalary; // 最低薪资筛选
    private String maxSalary; // 最高薪资筛选
    private Boolean showAll; // 是否显示所有状态的职位
    private String auditRemark; // 审核备注

}

