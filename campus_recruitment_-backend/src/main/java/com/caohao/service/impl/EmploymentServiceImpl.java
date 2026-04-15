package com.caohao.service.impl;

import com.caohao.common.enums.impl.CompanyStatusEnum;
import com.caohao.common.enums.impl.UserRoleEnum;
import com.caohao.common.utils.DateUtil;
import com.caohao.common.utils.IDGenerator;
import com.caohao.controller.websocket.server.WebsocketServer;
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
import com.caohao.service.AuditLogService;
import com.caohao.service.EmploymentService;
import com.caohao.service.UserNotificationService;
import com.google.gson.Gson;
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
    private static final Gson GSON = new Gson();

    @Resource
    private EmploymentDao employmentDao;
    @Resource
    private CompanyDao companyDao;
    @Resource
    private EmploymentUserDao employmentUserDao;
    @Resource
    private UserDao userDao;

    @Resource
    private AuditLogService auditLogService;

    @Resource
    private UserNotificationService userNotificationService;

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

    private UserModel getCurrentUserOrNull() {
        String username = GetTokenInfoUtil.getUsername();
        if ("noLogin".equals(username) || username == null || username.trim().isEmpty()) {
            return null;
        }
        return userDao.selectByUserName(username);
    }

    private boolean isAdmin(UserModel user) {
        return user != null && UserRoleEnum.Admin.name().equals(user.getRole());
    }

    private String employmentStatusText(Integer status) {
        if (status == null) {
            return null;
        }
        switch (status) {
            case 0:
                return "待审核";
            case 1:
                return "审核通过";
            case 2:
                return "已驳回";
            default:
                return String.valueOf(status);
        }
    }

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public EmploymentModel queryById(String id) {
        EmploymentModel employmentModel = this.employmentDao.queryById(id);
        if (employmentModel == null) {
            return null;
        }
        employmentModel.setCompanyModel(companyDao.queryById(employmentModel.getCompanyId()));
        UserModel currentUser = getCurrentUserOrNull();
        boolean admin = isAdmin(currentUser);
        boolean owner = currentUser != null && currentUser.getId().equals(employmentModel.getUserId());
        boolean publicVisible = employmentModel.getStatus() != null
                && employmentModel.getStatus() == 1
                && employmentModel.getCompanyModel() != null
                && CompanyStatusEnum.Approve.name().equals(employmentModel.getCompanyModel().getStatus());
        if (!publicVisible && !admin && !owner) {
            throw new RuntimeException("该职位尚未对外开放");
        }
        String currentUsername = GetTokenInfoUtil.getUsername();
        if (!"noLogin".equals(currentUsername)) {
            UserModel user = currentUser != null ? currentUser : userDao.selectByUserName(currentUsername);
            if (user != null) {
                // 检查投递状态
                EmploymentUserParam employmentUserParam = new EmploymentUserParam();
                employmentUserParam.setUserId(user.getId());
                employmentUserParam.setEmploymentId(id);
                employmentUserParam.setType(1); // 只查询投递记录，不包括收藏
                List<EmploymentUserModel> employmentUserModels = employmentUserDao.queryAllByLimit(employmentUserParam);
                if (employmentUserModels.size()>0){
                    employmentModel.setUserStatus("have");
                }else {
                    employmentModel.setUserStatus("no-have");
                }
                
                // 检查收藏状态
                EmploymentUserParam collectParam = new EmploymentUserParam();
                collectParam.setUserId(user.getId());
                collectParam.setEmploymentId(id);
                collectParam.setType(2); // 只查询收藏记录
                List<EmploymentUserModel> collectModels = employmentUserDao.queryAllByLimit(collectParam);
                if (collectModels.size()>0){
                    employmentModel.setIsCollected(true);
                }else {
                    employmentModel.setIsCollected(false);
                }
            } else {
                employmentModel.setUserStatus("no-have");
                employmentModel.setIsCollected(false);
            }
        } else {
            employmentModel.setUserStatus("no-have");
            employmentModel.setIsCollected(false);
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
        UserModel user = requireCurrentUser();
        if (!UserRoleEnum.Enterprise_User.name().equals(user.getRole())) {
            throw new RuntimeException("仅企业用户可发布职位");
        }
        employment.setUserId(user.getId());
        employment.setStatus(0); // 0=待审核
        // 查找当前企业用户对应的公司
        CompanyModel companyModel = companyDao.queryByUserId(user.getId());
        System.out.println("查询到的公司信息: " + companyModel);
        
        if (companyModel != null) {
            if (!CompanyStatusEnum.Approve.name().equals(companyModel.getStatus())) {
                throw new RuntimeException("企业信息审核通过后才能发布职位");
            }
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
        EmploymentModel existing = this.employmentDao.queryById(employment.getId());
        if (existing == null) {
            throw new RuntimeException("职位信息不存在");
        }

        UserModel currentUser = requireCurrentUser();
        boolean admin = isAdmin(currentUser);
        if (!admin && !currentUser.getId().equals(existing.getUserId())) {
            throw new RuntimeException("无权修改该职位信息");
        }

        employment.setUserId(existing.getUserId());
        employment.setCompanyId(existing.getCompanyId());
        employment.setCollectNumber(existing.getCollectNumber());
        if (!admin) {
            employment.setStatus(existing.getStatus());
        }
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
        EmploymentModel existing = this.employmentDao.queryById(id);
        if (existing == null) {
            throw new RuntimeException("职位信息不存在");
        }

        UserModel currentUser = requireCurrentUser();
        boolean admin = isAdmin(currentUser);
        if (!admin && !currentUser.getId().equals(existing.getUserId())) {
            throw new RuntimeException("无权删除该职位信息");
        }

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
        UserModel currentUser = requireCurrentUser();
        if (!isAdmin(currentUser)) {
            throw new RuntimeException("仅管理员可执行职位审核");
        }
        EmploymentModel existing = this.employmentDao.queryById(employment.getId());
        if (existing == null) {
            throw new RuntimeException("职位信息不存在");
        }
        employment.setUpdateTime(DateUtil.getCurrentTimeMillis());
        this.employmentDao.update(employment);
        EmploymentModel result = this.queryById(employment.getId());
        String auditContent = employment.getAuditRemark();
        if (auditContent == null || auditContent.trim().isEmpty()) {
            auditContent = employmentStatusText(result.getStatus()) + "操作";
        }
        auditLogService.saveAuditLog(
                "EMPLOYMENT",
                result.getId(),
                result.getTitle(),
                employmentStatusText(existing.getStatus()),
                employmentStatusText(result.getStatus()),
                auditContent,
                currentUser.getId(),
                currentUser.getUserName()
        );
        userNotificationService.saveNotification(
                result.getUserId(),
                targetUser != null ? targetUser.getUserName() : null,
                "EMPLOYMENT_AUDIT",
                "职位审核结果通知",
                "您发布的职位《" + result.getTitle() + "》状态已更新为：" + employmentStatusText(result.getStatus()) + (auditContent != null ? "。" + auditContent : ""),
                "EMPLOYMENT",
                result.getId()
        );
        UserModel targetUser = userDao.queryById(result.getUserId());
        if (targetUser != null && targetUser.getUserName() != null) {
            java.util.Map<String, Object> payload = new java.util.HashMap<>();
            payload.put("type", "notification");
            payload.put("notificationType", "EMPLOYMENT_AUDIT");
            payload.put("title", "职位审核结果通知");
            payload.put("content", "您发布的职位《" + result.getTitle() + "》状态已更新为：" + employmentStatusText(result.getStatus()) + (auditContent != null ? "。" + auditContent : ""));
            payload.put("targetType", "EMPLOYMENT");
            payload.put("targetId", result.getId());
            WebsocketServer.pushMessage(null, GSON.toJson(payload), targetUser.getUserName());
        }
        return result;
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

    @Override
    public int collectEmployment(String employmentId) {
        // 获取当前用户ID
        String currentUsername = GetTokenInfoUtil.getUsername();
        if ("noLogin".equals(currentUsername)) {
            throw new RuntimeException("请先登录");
        }
        UserModel user = userDao.selectByUserName(currentUsername);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查是否已经收藏
        EmploymentUserParam employmentUserParam = new EmploymentUserParam();
        employmentUserParam.setUserId(user.getId());
        employmentUserParam.setEmploymentId(employmentId);
        employmentUserParam.setType(2); // 只查询收藏记录
        List<EmploymentUserModel> employmentUserModels = employmentUserDao.queryAllByLimit(employmentUserParam);
        if (employmentUserModels.size() > 0) {
            throw new RuntimeException("已经收藏过该职位");
        }

        // 添加收藏记录
        employmentUserParam.setId(IDGenerator.StringID());
        employmentUserParam.setCreateTime(DateUtil.getCurrentTimeMillis());
        employmentUserParam.setDeleted(0);
        employmentUserParam.setType(2); // 设置为收藏类型
        // 从用户表中获取用户信息，填充name和phone字段（这些字段在表结构中是NOT NULL的）
        employmentUserParam.setName(user.getRealName() != null ? user.getRealName() : user.getNickName() != null ? user.getNickName() : user.getUserName());
        employmentUserParam.setPhone(user.getPhone() != null ? user.getPhone() : "");
        employmentUserDao.insert(employmentUserParam);

        // 更新职位收藏数
        Employment employment = new Employment();
        employment.setId(employmentId);
        return employmentDao.incrementCollectNumber(employment);
    }

    @Override
    public int uncollectEmployment(String employmentId) {
        // 获取当前用户ID
        String currentUsername = GetTokenInfoUtil.getUsername();
        if ("noLogin".equals(currentUsername)) {
            throw new RuntimeException("请先登录");
        }
        UserModel user = userDao.selectByUserName(currentUsername);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 删除收藏记录
        EmploymentUserParam employmentUserParam = new EmploymentUserParam();
        employmentUserParam.setUserId(user.getId());
        employmentUserParam.setEmploymentId(employmentId);
        employmentUserParam.setType(2); // 只删除收藏记录，不包括投递
        int deleteResult = employmentUserDao.deleteByUserIdAndEmploymentId(employmentUserParam);
        if (deleteResult == 0) {
            throw new RuntimeException("未收藏该职位");
        }

        // 更新职位收藏数
        Employment employment = new Employment();
        employment.setId(employmentId);
        return employmentDao.decrementCollectNumber(employment);
    }

}
