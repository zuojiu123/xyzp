package com.caohao.pojo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * (Employment)实体类
 *
 * @author caohao
 * @since 2021-11-14 17:17:17
 */
@Data
public class Employment implements Serializable {
    private static final long serialVersionUID = 104752132349503805L;

    private String id;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 标题
     */
    private String title;
    /**
     * 描述
     */
    private String description;

    /**
     * 位置
     */
    private String position;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 修改时间
     */
    private Long updateTime;
    /**
     * 学历要求
     */
    private String education;
    /**
     * 状态：0-待审核，1-已发布，2-已下线
     */
    private Integer status;
    /**
     * 最低工资
     */
    private String minSalary;
    /**
     * 最高工资
     */
    private String maxSalary;
    /**
     * 薪资范围
     */
    private String salary;
    /**
     * 公司id
     */
    private String companyId;
    /**
     * 待遇
     */
    private String treatment;




}

