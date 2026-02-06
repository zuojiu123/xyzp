package com.caohao.pojo.param;

import com.caohao.pojo.entity.Company;
import lombok.Data;

import java.io.Serializable;

/**
 * (Company)实体类
 *
 * @author caohao
 * @since 2021-11-14 16:28:22
 */
@Data
public class CompanyParam extends Company {
    private static final long serialVersionUID = 150393452170955368L;

    // CompanyParam直接继承父类Company的所有字段
    // 包括String类型的status、category、nature字段
    
    // 筛选字段
    private String cityFilter; // 城市筛选（使用description模糊匹配）
    private String industryFilter; // 行业筛选（使用category字段）
    private Integer minNumber; // 最小人数
    private Integer maxNumber; // 最大人数
    private String sortBy; // 排序字段
    private Boolean showAll; // 是否显示所有状态的公司

}

