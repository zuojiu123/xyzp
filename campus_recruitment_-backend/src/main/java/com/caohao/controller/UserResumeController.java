package com.caohao.controller;

import com.caohao.common.Result;
import com.caohao.common.SuperController;
import com.caohao.pojo.entity.UserResume;
import com.caohao.security.util.GetTokenInfoUtil;
import com.caohao.service.UserResumeService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * (UserResume)表控制层
 *
 * @author caohoa
 * @since 2022-04-03 19:23:25
 */
@Api(tags = "UserResume")
@RestController
@RequestMapping("/userResume")
@CrossOrigin
public class UserResumeController extends SuperController {
    /**
     * 服务对象
     */
    @Resource
    private UserResumeService userResumeService;

    @GetMapping("/user")
    public Result getResumeByUserName(){
       List<UserResume> userResumes=userResumeService.getResumeByUserName(GetTokenInfoUtil.getUsername());
       return Result.ok(userResumes);
    }

    /**
     * 上传简历文件
     *
     * @param file 简历文件
     * @return 上传结果
     */
    @ApiOperation("上传简历")
    @PostMapping("/upload")
    public Result uploadResume(@RequestParam("file") MultipartFile file) {
        try {
            return success(this.userResumeService.uploadResume(file));
        } catch (Exception e) {
            return Result.failed("简历上传失败: " + e.getMessage());
        }
    }

    /**
     * 下载/预览简历文件（需登录：管理员、附件所有者、收到该投递的企业用户）
     */
    /**
     * 使用 /attachment/{id} 避免与下方 {@code GET /userResume/{id}} 查询接口在部分环境下的路径匹配歧义。
     */
    @ApiOperation("下载或预览简历文件")
    @GetMapping("/attachment/{id}")
    public void downloadResumeFile(@PathVariable("id") String id,
                                   @RequestParam(value = "inline", defaultValue = "false") boolean inline,
                                   HttpServletResponse response) throws IOException {
        this.userResumeService.writeResumeToResponse(id, inline, response);
    }

    /**
     * 分页查询
     *
     * @param userResume 筛选条件
     * @param pageNum      当前页
     * @param pageSize     每页显示数量
     * @return 查询结果
     */
    @ApiOperation("分页查询")
    @PostMapping("/{pageNum}/{pageSize}")
    public Result queryByPage(@RequestBody UserResume userResume,
                              @PathVariable("pageNum") Integer pageNum,
                              @PathVariable("pageSize") Integer pageSize) {
        return success(this.userResumeService.queryByPage(userResume, pageNum, pageSize));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @ApiOperation("通过主键查询单条数据")
    @GetMapping("/{id}")
    public Result queryById(@PathVariable("id") String id) {
        return success(this.userResumeService.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param userResume 实体
     * @return 新增结果
     */
    @ApiOperation("新增数据")
    @PostMapping("/add")
    public Result add(@RequestBody UserResume userResume) {
        return success(this.userResumeService.insert(userResume));
    }

    /**
     * 编辑数据
     *
     * @param userResume 实体
     * @return 编辑结果
     */
    @ApiOperation("编辑数据")
    @PutMapping
    public Result edit(@RequestBody UserResume userResume) {
        return success(this.userResumeService.update(userResume));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @ApiOperation("删除数据")
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable("id") String id) {
        return success(this.userResumeService.deleteById(id));
    }

}

