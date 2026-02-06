package com.caohao.service.impl;

import com.caohao.common.utils.DateUtil;
import com.caohao.common.utils.IDGenerator;
import com.caohao.dao.CompanyDao;
import com.caohao.dao.EmploymentUserDao;
import com.caohao.dao.UserDao;
import com.caohao.pojo.entity.Employment;
import com.caohao.dao.EmploymentDao;
import com.caohao.pojo.model.CompanyModel;
import com.caohao.pojo.model.EmploymentModel;
import com.caohao.pojo.model.EmploymentUserModel;
import com.caohao.pojo.model.UserModel;
import com.caohao.pojo.param.EmploymentParam;
import com.caohao.pojo.param.EmploymentUserParam;
import com.caohao.pojo.param.CompanyParam;
import com.caohao.security.util.GetTokenInfoUtil;
import com.caohao.service.EmploymentService;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import javax.annotation.Resource;
import java.util.List;

/**
 * (Employment)表服务实现类
 *
 * @author caohao
 * @since 2021-11-14 17:17:17
 */
@Service("employmentService")
public class EmploymentServiceImpl implements EmploymentService {
    @Resource
    private EmploymentDao employmentDao;
    @Resource
    private CompanyDao companyDao;
    @Resource
    private EmploymentUserDao employmentUserDao;
    @Resource
    private UserDao userDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public EmploymentModel queryById(String id) {
        EmploymentModel employmentModel = this.employmentDao.queryById(id);
        employmentModel.setCompanyModel(companyDao.queryById(employmentModel.getCompanyId()));
        String currentUsername = GetTokenInfoUtil.getUsername();
        if (!"noLogin".equals(currentUsername)) {
            UserModel user = userDao.selectByUserName(currentUsername);
            if (user != null) {
                EmploymentUserParam employmentUserParam = new EmploymentUserParam();
                employmentUserParam.setUserId(user.getId());
                employmentUserParam.setEmploymentId(id);
                List<EmploymentUserModel> employmentUserModels = employmentUserDao.queryAllByLimit(employmentUserParam);
                if (employmentUserModels.size()>0){
                    employmentModel.setUserStatus("have");
                }else {
                    employmentModel.setUserStatus("no-have");
                }
            } else {
                employmentModel.setUserStatus("no-have");
            }
        } else {
            employmentModel.setUserStatus("no-have");
        }
        return employmentModel;
    }

    /**
     * 分页查询
     *
     * @param employment 筛选条件
     * @param pageNum      当前页数
     * @param pageSize     每页显示数量
     * @return 查询结果
     */
    @Override
    public PageInfo<EmploymentModel> queryByPage(EmploymentParam employment, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<EmploymentModel> employments =this.employmentDao.queryAllByLimit(employment);
        for (EmploymentModel employmentModel : employments) {
            CompanyModel companyModel = companyDao.queryById(employmentModel.getCompanyId());
            employmentModel.setCompanyModel(companyModel);
        }
        return new PageInfo<>(employments);
    }

    /**
     * 新增数据
     *
     * @param employment 实例对象
     * @return 实例对象
     */
    @Override
    public EmploymentParam insert(EmploymentParam employment) {
        employment.setId(IDGenerator.generateEmploymentId());
        employment.setCreateTime(DateUtil.getCurrentTimeMillis());
        employment.setUpdateTime(DateUtil.getCurrentTimeMillis());
        
        // 获取当前用户的ID和公司ID
        String username = GetTokenInfoUtil.getUsername();
        UserModel user = userDao.selectByUserName(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        employment.setUserId(user.getId());
        employment.setStatus(0); // 0=待审核
        // 查找当前企业用户对应的公司
        CompanyModel companyModel = companyDao.queryByUserId(user.getId());
        System.out.println("查询到的公司信息: " + companyModel);
        
        if (companyModel != null) {
            employment.setCompanyId(companyModel.getId());
            System.out.println("使用当前企业账号对应的公司ID: " + companyModel.getId());
        } else {
            throw new RuntimeException("企业用户必须先完善公司信息才能发布职位");
        }
        this.employmentDao.insert(employment);
        return employment;
    }

    /**
     * 修改数据
     *
     * @param employment 实例对象
     * @return 实例对象
     */
    @Override
    public EmploymentModel update(EmploymentParam employment) {
        employment.setUpdateTime(DateUtil.getCurrentTimeMillis());
        this.employmentDao.update(employment);
        return this.queryById(employment.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(String id) {
        employmentUserDao.deleteByEmployId(id);
        return this.employmentDao.deleteById(id) > 0;
    }

    @Override
    public PageInfo<EmploymentModel> getByUsername(String username, Integer pageNum, Integer pageSize) {
        System.out.println("EmploymentService.getByUsername - 用户名: " + username);
        PageHelper.startPage(pageNum, pageSize);
        EmploymentParam employmentParam = new EmploymentParam();
        
        // 获取当前用户ID
        UserModel user = userDao.selectByUserName(username);
        if (user == null) {
            System.out.println("用户不存在: " + username);
            throw new RuntimeException("用户不存在");
        }
        System.out.println("用户ID: " + user.getId());
        employmentParam.setUserId(user.getId());
        
        // 显示所有状态的职位（企业用户查看自己的职位）
        employmentParam.setShowAll(true);
        
        System.out.println("查询参数 - userId: " + employmentParam.getUserId() + ", showAll: " + employmentParam.getShowAll());
        List<EmploymentModel> employments = this.employmentDao.queryAllByLimit(employmentParam);
        System.out.println("查询到的职位数量: " + employments.size());
        
        for (EmploymentModel employmentModel : employments) {
            CompanyModel companyModel = companyDao.queryById(employmentModel.getCompanyId());
            employmentModel.setCompanyModel(companyModel);
        }
        return new PageInfo<>(employments);
    }

    @Override
    public EmploymentModel auditEmployment(EmploymentParam employment) {
        employment.setUpdateTime(DateUtil.getCurrentTimeMillis());
        this.employmentDao.update(employment);
        return this.queryById(employment.getId());
    }

    @Override
    public int getTotalCount() {
        return employmentDao.getTotalCount();
    }

    @Override
    public int getPendingCount() {
        return employmentDao.getCountByStatus(0); // 0=待审核
    }

    @Override
    public int getActiveCount() {
        return employmentDao.getCountByStatus(1); // 1=已发布
    }

    @Override
    public int getInactiveCount() {
        return employmentDao.getCountByStatus(2); // 2=已下线
    }

}
