package com.caohao.controller;

import com.caohao.common.Result;
import com.caohao.dao.UserDao;
import com.caohao.pojo.entity.UserResume;
import com.caohao.pojo.model.UserModel;
import com.caohao.security.util.GetTokenInfoUtil;
import com.caohao.service.UserResumeService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/resume")
@CrossOrigin
public class ResumeController {
    @Resource
    private UserDao userDao;

    @Resource
    private UserResumeService userResumeService;

    private UserModel requireCurrentUser() {
        String username = GetTokenInfoUtil.getUsername();
        if ("noLogin".equals(username) || username == null || username.trim().isEmpty()) {
            throw new RuntimeException("请先登录后再执行该操作");
        }
        UserModel currentUser = userDao.selectByUserName(username);
        if (currentUser == null) {
            throw new RuntimeException("当前用户不存在，请重新登录");
        }
        return currentUser;
    }

    private boolean isAdmin(UserModel user) {
        return user != null && "Admin".equals(user.getRole());
    }

    @PostMapping("/upload")
    public Result resumeUpload(@RequestParam("file") MultipartFile file) throws Exception {
        requireCurrentUser();
        return Result.ok(userResumeService.uploadResume(file));
    }

    /**
     * 获取用户简历列表
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 简历列表
     */
    @GetMapping("/user/{pageNum}/{pageSize}")
    public Result getUserResumeList(@PathVariable("pageNum") Integer pageNum,
                                   @PathVariable("pageSize") Integer pageSize) {
        try {
            UserModel currentUser = requireCurrentUser();
            System.out.println("获取简历列表 - 用户ID: " + currentUser.getId());
            UserResume queryParam = new UserResume();
            queryParam.setUserId(currentUser.getId());
            PageInfo<UserResume> pageInfo = userResumeService.queryByPage(queryParam, pageNum, pageSize);
            return Result.ok(pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed("获取简历列表失败: " + e.getMessage());
        }
    }

    /**
     * 删除简历
     *
     * @param id 简历ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result deleteResume(@PathVariable("id") String id) {
        try {
            userResumeService.deleteById(id);
            return Result.ok("删除成功");
        } catch (Exception e) {
            return Result.failed("删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有简历列表（企业端使用）
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 简历列表
     */
    @GetMapping("/all/{pageNum}/{pageSize}")
    public Result getAllResumeList(@PathVariable("pageNum") Integer pageNum,
                                  @PathVariable("pageSize") Integer pageSize) {
        try {
            UserModel currentUser = requireCurrentUser();
            if (!isAdmin(currentUser)) {
                return Result.failed("仅管理员可查看全部简历");
            }
            PageInfo<UserResume> pageInfo = userResumeService.queryByPage(new UserResume(), pageNum, pageSize);
            return Result.ok(pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed("获取简历列表失败: " + e.getMessage());
        }
    }
}
