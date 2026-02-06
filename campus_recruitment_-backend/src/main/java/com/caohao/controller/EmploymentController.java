package com.caohao.controller;

import com.caohao.common.Result;
import com.caohao.common.SuperController;
import com.caohao.pojo.entity.Employment;
import com.caohao.pojo.model.EmploymentModel;
import com.caohao.pojo.param.EmploymentParam;
import com.caohao.security.util.GetTokenInfoUtil;
import com.caohao.service.EmploymentService;
import com.github.pagehelper.PageInfo;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * (Employment)表控制层
 *
 * @author caohao
 * @since 2021-11-14 17:17:16
 */
@Api(tags = "Employment")
@RestController
@RequestMapping("/employment")
@CrossOrigin
public class EmploymentController extends SuperController {
    /**
     * 服务对象
     */
    @Resource
    private EmploymentService employmentService;


    @GetMapping("/username/{pageNum}/{pageSize}")
    public Result getByUsername( EmploymentParam employment,
                                @PathVariable("pageNum") Integer pageNum,
                                @PathVariable("pageSize") Integer pageSize){
        if (employment==null){
            employment=new EmploymentParam();
        }
        // 使用EmploymentService中的getByUsername方法
        String username = GetTokenInfoUtil.getUsername();
        System.out.println("获取用户职位，用户名: " + username);
        PageInfo<EmploymentModel> result = this.employmentService.getByUsername(username, pageNum, pageSize);
        System.out.println("查询结果数量: " + (result.getList() != null ? result.getList().size() : 0));
        return success(result);
    }

    /**
     * 分页查询
     *
     * @param employment 筛选条件
     * @param pageNum      当前页
     * @param pageSize     每页显示数量
     * @return 查询结果
     */
    @ApiOperation("分页查询")
    @PostMapping("/{pageNum}/{pageSize}")
    public Result queryByPage(@RequestBody EmploymentParam employment,
                              @PathVariable("pageNum") Integer pageNum,
                              @PathVariable("pageSize") Integer pageSize) {
        System.out.println("职位筛选参数: " + employment.toString());
        return success(this.employmentService.queryByPage(employment, pageNum, pageSize));
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
        return success(this.employmentService.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param employment 实体
     * @return 新增结果
     */
    @ApiOperation("新增数据")
    @PostMapping
    public Result add(@RequestBody EmploymentParam employment) {

        return success(this.employmentService.insert(employment));
    }

    /**
     * 编辑数据
     *
     * @param employment 实体
     * @return 编辑结果
     */
    @ApiOperation("编辑数据")
    @PutMapping
    public Result edit(@RequestBody EmploymentParam employment) {
        return success(this.employmentService.update(employment));
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
        return success(this.employmentService.deleteById(id));
    }

    /**
     * 审核职位
     *
     * @param employment 审核信息
     * @return 审核结果
     */
    @ApiOperation("审核职位")
    @PutMapping("/audit")
    public Result auditEmployment(@RequestBody EmploymentParam employment) {
        return success(this.employmentService.auditEmployment(employment));
    }
    
    /**
     * 测试接口 - 检查用户和公司信息
     */
    @GetMapping("/test/userinfo")
    public Result testUserInfo() {
        String username = GetTokenInfoUtil.getUsername();
        System.out.println("当前用户: " + username);
        return success("Current user: " + username);
    }

}

