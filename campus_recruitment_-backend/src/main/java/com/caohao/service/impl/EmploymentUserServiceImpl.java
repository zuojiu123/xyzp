package com.caohao.service.impl;

import com.caohao.common.utils.DateUtil;
import com.caohao.common.utils.IDGenerator;
import com.caohao.controller.websocket.server.WebsocketServer;
import com.caohao.dao.CompanyDao;
import com.caohao.dao.EmploymentDao;
import com.caohao.dao.EmploymentUserDao;
import com.caohao.dao.UserDao;
import com.caohao.dao.UserResumeDao;
import com.caohao.pojo.model.UserModel;
import com.caohao.pojo.model.CompanyModel;
import com.caohao.pojo.model.EmploymentModel;
import com.caohao.pojo.model.EmploymentUserModel;
import com.caohao.pojo.param.EmploymentParam;
import com.caohao.pojo.param.EmploymentUserParam;
import com.caohao.security.util.GetTokenInfoUtil;
import com.caohao.service.EmploymentUserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    @Resource
    private UserDao userDao;

    private static final Gson GSON = new Gson();

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
        // 设置类型为投递
        employmentUser.setType(1);
        if (employmentUser.getReplyStatus() == null || employmentUser.getReplyStatus().isEmpty()) {
            employmentUser.setReplyStatus("Wait_For_Reply");
        }
        if (employmentUser.getUserStatus() == null || employmentUser.getUserStatus().isEmpty()) {
            employmentUser.setUserStatus("Wait_For_Reply");
        }
        if (employmentUser.getDeleted() == null) {
            employmentUser.setDeleted(0);
        }

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
        EmploymentUserModel existing = this.employmentUserDao.queryById(employmentUser.getId());
        String oldReplyStatus = existing != null ? existing.getReplyStatus() : null;

        employmentUser.setReplyTime(DateUtil.getCurrentTimeMillis());
        if (employmentUser.getReplyStatus() != null) {
            String rs = employmentUser.getReplyStatus();
            if ("Agree_With_Induction".equals(rs)) {
                employmentUser.setUserStatus("Pass");
            } else if ("Refused_Entry".equals(rs) || "Rejected".equals(rs)) {
                employmentUser.setUserStatus("Reject");
            }
        }

        this.employmentUserDao.update(employmentUser);
        EmploymentUserModel result = this.queryById(employmentUser.getId());
        if (existing != null && employmentUser.getReplyStatus() != null
                && !Objects.equals(employmentUser.getReplyStatus(), oldReplyStatus)) {
            pushApplicationStatusToApplicant(existing.getUserId(), result);
        }
        return result;
    }

    private void pushApplicationStatusToApplicant(String applicantUserId, EmploymentUserModel result) {
        if (applicantUserId == null || result == null) {
            return;
        }
        try {
            UserModel applicant = userDao.queryById(applicantUserId);
            if (applicant == null || applicant.getUserName() == null) {
                return;
            }
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "application_status");
            payload.put("replyStatus", result.getReplyStatus());
            payload.put("userStatus", result.getUserStatus());
            payload.put("replyContent", result.getReplyContent());
            payload.put("employmentId", result.getEmploymentId());
            payload.put("id", result.getId());
            WebsocketServer.pushMessage(null, GSON.toJson(payload), applicant.getUserName());
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public int countByReplyStatus(String replyStatus) {
        if (replyStatus == null || replyStatus.isEmpty()) {
            return 0;
        }
        return employmentUserDao.countByReplyStatus(replyStatus);
    }
}
