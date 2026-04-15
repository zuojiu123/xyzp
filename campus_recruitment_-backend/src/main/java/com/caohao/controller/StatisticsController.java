package com.caohao.controller;

import com.caohao.common.Result;
import com.caohao.common.enums.impl.UserRoleEnum;
import com.caohao.pojo.model.UserModel;
import com.caohao.security.util.GetTokenInfoUtil;
import com.caohao.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 统计数据控制层
 *
 * @author caohao
 */
@Api(tags = "Statistics")
@RestController
@RequestMapping("/statistics")
@CrossOrigin
public class StatisticsController {

    @Resource
    private UserService userService;
    
    @Resource
    private CompanyService companyService;
    
    @Resource
    private EmploymentService employmentService;
    
    @Resource
    private EmploymentUserService employmentUserService;
    
    @Resource
    private ArticleService articleService;

    private void requireAdmin() {
        String username = GetTokenInfoUtil.getUsername();
        if ("noLogin".equals(username) || username == null || username.trim().isEmpty()) {
            throw new RuntimeException("请先登录后再执行该操作");
        }
        UserModel currentUser = userService.queryByUsername(username);
        if (currentUser == null) {
            throw new RuntimeException("当前用户不存在，请重新登录");
        }
        if (!UserRoleEnum.Admin.name().equals(currentUser.getRole())) {
            throw new RuntimeException("仅管理员可查看统计数据");
        }
    }

    /**
     * 获取系统统计数据
     *
     * @return 统计数据
     */
    @ApiOperation("获取首页公开统计摘要")
    @GetMapping("/public")
    public Result getPublicStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        try {
            statistics.put("userCount", userService.getTotalCount());
            statistics.put("companyCount", companyService.getApprovedCount());
            statistics.put("jobCount", employmentService.getActiveCount());
            statistics.put("applicationCount",
                    employmentUserService.countByReplyStatus("Agree_With_Induction"));
            return Result.ok(statistics);
        } catch (Exception e) {
            return Result.failed("获取首页统计摘要失败");
        }
    }

    /**
     * 获取系统统计数据
     *
     * @return 统计数据
     */
    @ApiOperation("获取系统统计数据")
    @GetMapping("/dashboard")
    public Result getDashboardStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        try {
            requireAdmin();
            // 使用实际的Service方法
            statistics.put("userCount", userService.getTotalCount());
            statistics.put("companyCount", companyService.getTotalCount());
            statistics.put("jobCount", employmentService.getTotalCount()); // 修改为jobCount
            statistics.put("applicationCount", employmentUserService.getTotalCount());
            statistics.put("articleCount", articleService.getTotalCount());
            statistics.put("pendingCompanyCount", companyService.getPendingCount());
            statistics.put("pendingEmploymentCount", employmentService.getPendingCount());
            statistics.put("pendingApplicationCount",
                    employmentUserService.countByReplyStatus("Wait_For_Reply"));
            statistics.put("approvedApplicationCount",
                    employmentUserService.countByReplyStatus("Agree_With_Induction"));
            statistics.put("rejectedApplicationCount",
                    employmentUserService.countByReplyStatus("Refused_Entry"));
            
            return Result.ok(statistics);
        } catch (Exception e) {
            return Result.failed("获取统计数据失败");
        }
    }

    /**
     * 获取用户统计数据
     *
     * @return 用户统计数据
     */
    @ApiOperation("获取用户统计数据")
    @GetMapping("/user")
    public Result getUserStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        try {
            requireAdmin();
            // 使用实际的Service方法
            statistics.put("totalUsers", userService.getTotalCount());
            statistics.put("normalUsers", userService.getNormalUserCount());
            statistics.put("enterpriseUsers", userService.getEnterpriseUserCount());
            statistics.put("adminUsers", userService.getAdminUserCount());
            
            return Result.ok(statistics);
        } catch (Exception e) {
            return Result.failed("获取用户统计数据失败");
        }
    }

    /**
     * 获取企业统计数据
     *
     * @return 企业统计数据
     */
    @ApiOperation("获取企业统计数据")
    @GetMapping("/company")
    public Result getCompanyStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        try {
            requireAdmin();
            // 使用实际的Service方法
            statistics.put("totalCompanies", companyService.getTotalCount());
            statistics.put("approvedCompanies", companyService.getApprovedCount());
            statistics.put("pendingCompanies", companyService.getPendingCount());
            statistics.put("rejectedCompanies", companyService.getRejectedCount());
            
            return Result.ok(statistics);
        } catch (Exception e) {
            return Result.failed("获取企业统计数据失败");
        }
    }

    /**
     * 获取职位统计数据
     *
     * @return 职位统计数据
     */
    @ApiOperation("获取职位统计数据")
    @GetMapping("/employment")
    public Result getEmploymentStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        try {
            requireAdmin();
            // 使用实际的Service方法
            statistics.put("totalEmployments", employmentService.getTotalCount());
            statistics.put("activeEmployments", employmentService.getActiveCount());
            statistics.put("pendingEmployments", employmentService.getPendingCount());
            statistics.put("inactiveEmployments", employmentService.getInactiveCount());
            
            return Result.ok(statistics);
        } catch (Exception e) {
            return Result.failed("获取职位统计数据失败");
        }
    }
}
