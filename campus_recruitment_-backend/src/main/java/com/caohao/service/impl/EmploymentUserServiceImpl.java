package com.caohao.service.impl;

import com.caohao.common.enums.impl.EmploymentReplyStatus;
import com.caohao.common.enums.impl.EmploymentUserStatus;
import com.caohao.common.utils.DateUtil;
import com.caohao.common.utils.IDGenerator;
import com.caohao.dao.CompanyDao;
import com.caohao.dao.EmploymentDao;
import com.caohao.dao.UserResumeDao;
import com.caohao.pojo.entity.Company;
import com.caohao.pojo.entity.EmploymentUser;
import com.caohao.dao.EmploymentUserDao;
import com.caohao.pojo.model.CompanyModel;
import com.caohao.pojo.model.EmploymentModel;
import com.caohao.pojo.model.EmploymentUserModel;
import com.caohao.pojo.param.EmploymentParam;
import com.caohao.pojo.param.EmploymentUserParam;
import com.caohao.security.util.GetTokenInfoUtil;
import com.caohao.service.EmploymentUserService;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * (EmploymentUser)表服务实现类
 *
 * @author caohao
 * @since 2021-11-14 17:48:02
 */
@Service("employmentUserService")
public class EmploymentUserServiceImpl implements EmploymentUserService {
    @Resource
    private EmploymentUserDao employmentUserDao;

    @Resource
    private EmploymentDao employmentDao;

    @Resource
    private CompanyDao companyDao;

    @Resource
    private UserResumeDao userResumeDao;
    
    @Resource
    private com.caohao.service.UserService userService;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public EmploymentUserModel queryById(String id) {
        return this.employmentUserDao.queryById(id);
    }

    /**
     * 分页查询
     *
     * @param employmentUser 筛选条件
     * @param pageNum      当前页数
     * @param pageSize     每页显示数量
     * @return 查询结果
     */
    @Override
    public PageInfo<EmploymentUserModel> queryByPage(EmploymentUserParam employmentUser, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<EmploymentUserModel> employmentUsers =this.employmentUserDao.queryAllByLimit(employmentUser);
        return new PageInfo<>(employmentUsers);
    }

    /**
     * 新增数据
     *
     * @param employmentUser 实例对象
     * @return 实例对象
     */
    @Override
    public EmploymentUserParam insert(EmploymentUserParam employmentUser) {
        employmentUser.setId(IDGenerator.StringID());
        employmentUser.setCreateTime(DateUtil.getCurrentTimeMillis());
        
        // 获取当前用户ID
        String username = GetTokenInfoUtil.getUsername();
        System.out.println("申请职位时获取到的用户名: " + username);
        
        // 检查用户是否已登录
        if ("noLogin".equals(username) || username == null || username.trim().isEmpty()) {
            System.out.println("用户未登录，username: " + username);
            throw new RuntimeException("请先登录后再申请职位");
        }
        
        com.caohao.pojo.model.UserModel currentUser = userService.selectByUserName(username);
        if (currentUser == null) {
            throw new RuntimeException("用户不存在，请重新登录");
        }
        
        System.out.println("找到用户ID: " + currentUser.getId());
        System.out.println("用户名: " + username);
        // 使用用户ID
        employmentUser.setUserId(currentUser.getId());
        

        this.employmentUserDao.insert(employmentUser);
        return employmentUser;
    }

    /**
     * 修改数据
     *
     * @param employmentUser 实例对象
     * @return 实例对象
     */
    @Override
    public EmploymentUserModel update(EmploymentUserParam employmentUser) {
        employmentUser.setReplyTime(DateUtil.getCurrentTimeMillis());
        this.employmentUserDao.update(employmentUser);
        return this.queryById(employmentUser.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(String id) {
        return this.employmentUserDao.deleteById(id) > 0;
    }

    @Override
    public List<EmploymentUserModel> getByUser(String username) {
        // 先通过用户名获取用户ID
        com.caohao.pojo.model.UserModel user = userService.selectByUserName(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        List<EmploymentUserModel> byUser = employmentUserDao.getByUser(user.getId());
        for (EmploymentUserModel employmentUserModel : byUser) {
            EmploymentModel employmentModel = employmentDao.queryById(employmentUserModel.getEmploymentId());
            CompanyModel companyModel = companyDao.queryById(employmentModel.getCompanyId());
            employmentModel.setCompanyModel(companyModel);
            employmentUserModel.setEmploymentModel(employmentModel);
        }
        return byUser;
    }

    @Override
    public PageInfo<EmploymentUserModel> queryByPageCompany(EmploymentUserParam employmentUser, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        EmploymentParam employmentParam = new EmploymentParam();
        employmentParam.setCompanyId(employmentUser.getCompanyId());
        List<EmploymentModel> employmentModels = employmentDao.queryAllByLimit(employmentParam);
        List<String> collect = employmentModels.stream().map(EmploymentModel::getId).collect(Collectors.toList());
        employmentUser.setIds(collect);
        List<EmploymentUserModel> employmentUsers =this.employmentUserDao.queryAllByLimit(employmentUser);
        for (EmploymentUserModel user : employmentUsers) {
            user.setUserResume(userResumeDao.queryById(user.getResume()));
            user.setEmploymentModel(employmentDao.queryById(user.getEmploymentId()));
        }
        return new PageInfo<>(employmentUsers);
    }

    @Override
    public PageInfo<EmploymentUserModel> queryByPageAdmin(EmploymentUserParam employmentUser, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<EmploymentUserModel> employmentUsers = this.employmentUserDao.queryAllByLimit(employmentUser);
        for (EmploymentUserModel user : employmentUsers) {
            user.setUserResume(userResumeDao.queryById(user.getResume()));
            user.setEmploymentModel(employmentDao.queryById(user.getEmploymentId()));
        }
        return new PageInfo<>(employmentUsers);
    }

    @Override
    public int getTotalCount() {
        return employmentUserDao.getTotalCount();
    }
}
