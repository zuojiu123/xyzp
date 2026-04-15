package com.caohao.controller;

import com.caohao.common.Result;
import com.caohao.common.enums.impl.ArticleTypeEnum;
import com.caohao.common.enums.impl.CompanyCategoryEnum;
import com.caohao.common.enums.impl.CompanyNatureEnum;
import com.caohao.common.enums.impl.CompanyStatusEnum;
import com.caohao.common.enums.impl.EmploymentEducationEnum;
import com.caohao.common.enums.impl.EmploymentReplyStatus;
import com.caohao.common.enums.impl.EmploymentUserStatus;
import com.caohao.common.enums.impl.FeedBackStatusEnum;
import com.caohao.common.enums.impl.FeedBackTypeEnum;
import com.caohao.common.enums.impl.UserRoleEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.stream.Collectors;

@Api(tags = "Enum")
@RestController
@RequestMapping("/enum")
@CrossOrigin
public class EnumController {

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
                case "EmploymentEducationEnum":
                    return Result.ok(Arrays.stream(EmploymentEducationEnum.values())
                            .map(e -> new EnumItem(e.name(), e.getMsg()))
                            .collect(Collectors.toList()));
                case "EmploymentUserStatus":
                    return Result.ok(Arrays.stream(EmploymentUserStatus.values())
                            .map(e -> new EnumItem(e.name(), e.getMsg()))
                            .collect(Collectors.toList()));
                case "FeedBackTypeEnum":
                    return Result.ok(Arrays.stream(FeedBackTypeEnum.values())
                            .map(e -> new EnumItem(e.name(), e.getMsg()))
                            .collect(Collectors.toList()));
                case "FeedBackStatusEnum":
                    return Result.ok(Arrays.stream(FeedBackStatusEnum.values())
                            .map(e -> new EnumItem(e.name(), e.getMsg()))
                            .collect(Collectors.toList()));
                default:
                    return Result.failed("不支持的枚举类型");
            }
        } catch (Exception e) {
            return Result.failed("获取枚举列表失败");
        }
    }

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
