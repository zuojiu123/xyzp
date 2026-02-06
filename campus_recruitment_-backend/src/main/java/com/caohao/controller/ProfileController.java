package com.caohao.controller;

import com.caohao.common.Result;
import com.caohao.pojo.model.EmploymentUserModel;
import com.caohao.pojo.model.UserModel;
import com.caohao.pojo.param.UserParam;
import com.caohao.security.util.GetTokenInfoUtil;
import com.caohao.service.EmploymentUserService;
import com.caohao.service.UserService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "个人中心")
@RestController
@RequestMapping("/profile")
@CrossOrigin
public class ProfileController {

    @Resource
    private UserService userService;
    
    @Resource
    private EmploymentUserService employmentUserService;

    @ApiOperation("获取个人信息")
    @GetMapping("/info")
    public Result getUserInfo() {
        try {
            String username = GetTokenInfoUtil.getUsername();
            UserModel user = userService.selectByUserName(username);
            if (user == null) {
                return Result.failed("用户不存在");
            }
            return Result.ok(user);
        } catch (Exception e) {
            return Result.failed("获取用户信息失败");
        }
    }

    @ApiOperation("更新个人信息")
    @PutMapping("/info")
    public Result updateUserInfo(@RequestBody UserParam userParam) {
        try {
            String username = GetTokenInfoUtil.getUsername();
            UserModel currentUser = userService.selectByUserName(username);
            if (currentUser == null) {
                return Result.failed("用户不存在");
            }
            
            userParam.setId(currentUser.getId());
            userParam.setUserName(username);
            UserModel updatedUser = userService.update(userParam);
            return Result.ok(updatedUser);
        } catch (Exception e) {
            return Result.failed("更新用户信息失败");
        }
    }

    @ApiOperation("获取我的求职申请")
    @GetMapping("/applications")
    public Result getMyApplications() {
        try {
            String username = GetTokenInfoUtil.getUsername();
            List<EmploymentUserModel> applications = employmentUserService.getByUser(username);
            return Result.ok(applications);
        } catch (Exception e) {
            return Result.failed("获取求职申请失败");
        }
    }

    @ApiOperation("获取我的求职申请（分页）")
    @GetMapping("/applications/{pageNum}/{pageSize}")
    public Result getMyApplicationsPage(@PathVariable Integer pageNum, @PathVariable Integer pageSize) {
        try {
            String username = GetTokenInfoUtil.getUsername();
            // 这里需要实现分页版本的getByUser方法
            List<EmploymentUserModel> applications = employmentUserService.getByUser(username);
            return Result.ok(applications);
        } catch (Exception e) {
            return Result.failed("获取求职申请失败");
        }
    }
}