package com.caohao.service.impl;

import com.caohao.common.Result;
import com.caohao.common.enums.impl.CompanyStatusEnum;
import com.caohao.common.enums.impl.UserRoleEnum;
import com.caohao.common.utils.DateUtil;
import com.caohao.common.utils.IDGenerator;
import com.caohao.dao.CompanyDao;
import com.caohao.dao.UserDao;
import com.caohao.pojo.model.UserModel;
import com.caohao.pojo.param.CompanyParam;
import com.caohao.pojo.param.UserParam;
import com.caohao.security.util.BCryptUtil;
import com.caohao.security.util.GetTokenInfoUtil;
import com.caohao.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * (User)表服务实现类
 *
 * @author caohao
 * @since 2021-11-14 18:48:17
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CompanyDao companyDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public UserModel queryById(String id) {
        return this.userDao.queryById(id);
    }

    /**
     * 分页查询
     *
     * @param user     筛选条件
     * @param pageNum  当前页数
     * @param pageSize 每页显示数量
     * @return 查询结果
     */
    @Override
    public PageInfo<UserModel> queryByPage(UserParam user, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<UserModel> users = this.userDao.queryAllByLimit(user);
        return new PageInfo<>(users);
    }

    /**
     * 新增数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    @Override
    public UserParam insert(UserParam user) {
        user.setId(IDGenerator.generateUserId());
        user.setCreateTime(DateUtil.getCurrentTimeMillis());
        user.setUpdateTime(DateUtil.getCurrentTimeMillis());
        user.setPassword(BCryptUtil.encode(user.getPassword()));
        user.setIntegral(0);
        user.setCredit(500);
        if ("Enterprise_User".equals(user.getRole())) {
            CompanyParam company = new CompanyParam();
            company.setId(IDGenerator.generateCompanyId());
            company.setCreateTime(DateUtil.getCurrentTimeMillis());
            company.setUpdateTime(DateUtil.getCurrentTimeMillis());
            company.setUserId(user.getId()); // 使用用户ID
            company.setStatus(CompanyStatusEnum.Check_Pending.name());
            company.setName("待完善公司信息");
            company.setDescription("请在企业信息管理中完善公司信息");
            companyDao.insert(company);
            System.out.println("企业用户注册时创建公司 - 用户ID: " + user.getId() + ", 公司ID: " + company.getId());
        }
        this.userDao.insert(user);
        return user;

    }

    /**
     * 修改数据
     *
     * @param user 实例对象
     * @return 实例对象
     */
    @Override
    public UserModel update(UserParam user) {
        // 获取当前登录用户的ID
        String username = GetTokenInfoUtil.getUsername();
        UserModel currentUser = this.selectByUserName(username);
        if (currentUser == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setId(currentUser.getId());
        user.setUpdateTime(DateUtil.getCurrentTimeMillis());
        this.userDao.update(user);
        return this.queryById(user.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(String id) {
        return this.userDao.deleteById(id) > 0;
    }

    @Override
    public UserModel selectByUserName(String username) {
        UserParam userParam = new UserParam();
        userParam.setUserName(username);
        List<UserModel> userModels = userDao.queryAllByLimit(userParam);
        if (userModels != null && userModels.size() > 0) {
            UserModel userModel = userModels.get(0);
            return userModel;
        }
        return null;
    }

    @Override
    public Result registeredUser(UserParam user) {
        // 跳过邮箱验证码，默认使用1234
        if (user.getInputCode() == null || !"1234".equals(user.getInputCode())) {
            return Result.failed("验证码错误，请输入1234");
        }
        UserModel userModel = userDao.selectByUserName(user.getUserName());
        if (userModel != null) {
            return Result.failed("该邮箱已被注册");
        }
        user.setId(IDGenerator.generateUserId());
        user.setCreateTime(DateUtil.getCurrentTimeMillis());
        user.setUpdateTime(DateUtil.getCurrentTimeMillis());
        user.setPassword(BCryptUtil.encode(user.getPassword()));
        // 设置默认角色，如果没有指定则为普通用户
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("Normal_User");
        }
        user.setIntegral(500);
        user.setCredit(5);
        user.setDeleted(0);
        
        this.userDao.insert(user);
        
        // 如果是企业用户，创建默认公司
        if ("Enterprise_User".equals(user.getRole())) {
            CompanyParam company = new CompanyParam();
            company.setId(IDGenerator.generateCompanyId());
            company.setCreateTime(DateUtil.getCurrentTimeMillis());
            company.setUpdateTime(DateUtil.getCurrentTimeMillis());
            company.setUserId(user.getId());
            company.setStatus(CompanyStatusEnum.Check_Pending.name());
            company.setName("待完善公司信息");
            company.setDescription("请在企业信息管理中完善公司信息");
            companyDao.insert(company);
        }
        return Result.ok(user);
    }

    @Override
    public Result updatePassword(UserParam user) {
        String username = GetTokenInfoUtil.getUsername();
        
        // 检查用户是否已登录
        if ("noLogin".equals(username) || username == null || username.trim().isEmpty()) {
            return Result.failed("请先登录后修改密码");
        }
        
        // 支持测试验证码1234或Redis中的验证码
        String authCode = stringRedisTemplate.opsForValue().get("AuthCode" + username);
        if (!"1234".equals(user.getInputCode()) && (authCode == null || !authCode.equals(user.getInputCode()))) {
            return Result.failed("验证码错误，请输入1234或获取正确的验证码");
        }

        user.setPassword(BCryptUtil.encode(user.getPassword()));
        user.setUserName(username);
        userDao.patchInfo(user);
        return Result.ok(user);
    }

    @Override
    public UserModel queryByUsername(String username) {
        return userDao.selectByUserName(username);
    }

    @Override
    public void patchInfo(UserParam user) {
        user.setUserName(GetTokenInfoUtil.getUsername());
        userDao.patchInfo(user);
    }

    @Override
    public int getTotalCount() {
        return userDao.getTotalCount();
    }

    @Override
    public int getNormalUserCount() {
        return userDao.getCountByRole("Normal_User");
    }

    @Override
    public int getEnterpriseUserCount() {
        return userDao.getCountByRole("Enterprise_User");
    }

    @Override
    public int getAdminUserCount() {
        return userDao.getCountByRole("Admin");
    }

}
