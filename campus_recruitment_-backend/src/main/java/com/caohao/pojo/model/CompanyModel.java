package com.caohao.pojo.model;

import com.caohao.pojo.entity.Company;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * (Company)实体类
 *
 * @author caohao
 * @since 2021-11-14 16:28:22
 */
@Data
public class CompanyModel extends Company {
    private static final long serialVersionUID = 150393452170955368L;

    /**
     * 已发布职位数量
     */
    private Integer publishedSize;

    /**
     * 关联的职位列表
     */
    private List<EmploymentModel> employmentModels;
    
    /**
     * 职位数量（供前端使用）
     */
    private Integer jobCount;
    
    /**
     * 用户名（联系人）
     */
    private String userName;
    
    /**
     * 用户邮箱
     */
    private String userEmail;
    
    /**
     * 用户手机
     */
    private String userPhone;

}

