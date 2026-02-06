package com.caohao.controller;

import com.caohao.common.Result;
import com.caohao.common.enums.impl.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 枚举接口控制层
 *
 * @author caohao
 */
@Api(tags = "Enum")
@RestController
@RequestMapping("/enum")
@CrossOrigin
public class EnumController {

    /**
     * 获取枚举列表
     *
     * @param enumType 枚举类型
     * @return 枚举列表
     */
    @ApiOperation("获取枚举列表")
    @GetMapping("/{enumType}")
    public Result getEnumList(@PathVariable("enumType") String enumType) {
        try {
            switch (enumType) {
                case "UserRoleEnum":
                    return Result.ok(Arrays.stream(UserRoleEnum.values())
                            .map(e -> new EnumItem(e.name(), e.getMsg()))
                            .collect(Collectors.toList()));
                case "CompanyStatusEnum":
                    return Result.ok(Arrays.stream(CompanyStatusEnum.values())
                            .map(e -> new EnumItem(e.name(), e.getMsg()))
                            .collect(Collectors.toList()));
                case "CompanyNatureEnum":
                    return Result.ok(Arrays.stream(CompanyNatureEnum.values())
                            .map(e -> new EnumItem(e.name(), e.getMsg()))
                            .collect(Collectors.toList()));
                case "CompanyCategoryEnum":
                    return Result.ok(Arrays.stream(CompanyCategoryEnum.values())
                            .map(e -> new EnumItem(e.name(), e.getMsg()))
                            .collect(Collectors.toList()));
                case "ArticleTypeEnum":
                    return Result.ok(Arrays.stream(ArticleTypeEnum.values())
                            .map(e -> new EnumItem(e.name(), e.getMsg()))
                            .collect(Collectors.toList()));
                case "EmploymentReplyStatus":
                    return Result.ok(Arrays.stream(EmploymentReplyStatus.values())
                            .map(e -> new EnumItem(e.name(), e.getMsg()))
                            .collect(Collectors.toList()));
                case "EmploymentUserStatus":
                    return Result.ok(Arrays.stream(EmploymentUserStatus.values())
                            .map(e -> new EnumItem(e.name(), e.getMsg()))
                            .collect(Collectors.toList()));
                default:
                    return Result.failed("不支持的枚举类型");
            }
        } catch (Exception e) {
            return Result.failed("获取枚举列表失败");
        }
    }

    /**
     * 枚举项
     */
    public static class EnumItem {
        private String enumCode;
        private String msg;

        public EnumItem(String enumCode, String msg) {
            this.enumCode = enumCode;
            this.msg = msg;
        }

        public String getEnumCode() {
            return enumCode;
        }

        public void setEnumCode(String enumCode) {
            this.enumCode = enumCode;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
}