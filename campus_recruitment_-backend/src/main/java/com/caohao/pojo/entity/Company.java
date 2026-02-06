package com.caohao.pojo.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * (Company)实体类
 *
 * @author caohao
 * @since 2021-11-14 16:28:22
 */
@Data
public class Company implements Serializable {
    private static final long serialVersionUID = 150393452170955368L;

    private String id;
    /**
     * 经营许可证编号
     */
    private String companyNumber;
    /**
     * 公司名称
     */
    private String name;
    /**
     * 公司描述
     */
    private String description;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 修改时间
     */
    private Long updateTime;

    /**
     * 审核人
     */
    private String processor;
    /**
     * 回复时间
     */
    private Long replyTime;
    /**
     * 审核状态
     */
    private String status;
    /**
     * 公司类别
     */
    private String category;
    /**
     * 公司性质
     */
    private String nature;
    /**
     * 回复内容
     */
    private String replyContent;
    /**
     * 公司人数
     */
    private Integer number;
    /**
     * 公司Logo
     */
    private String companyLogo;

}

