package com.caohao.service.impl;

import com.caohao.common.enums.impl.UserRoleEnum;
import com.caohao.common.utils.DateUtil;
import com.caohao.common.utils.IDGenerator;
import com.caohao.controller.websocket.server.WebsocketServer;
import com.caohao.dao.CompanyDao;
import com.caohao.dao.EmploymentDao;
import com.caohao.dao.EmploymentUserDao;
import com.caohao.dao.UserDao;
import com.caohao.dao.UserResumeDao;
import com.caohao.pojo.entity.ApplicationFlowLog;
import com.caohao.pojo.entity.UserResume;
import com.caohao.pojo.model.UserModel;
import com.caohao.pojo.model.CompanyModel;
import com.caohao.pojo.model.EmploymentModel;
import com.caohao.pojo.model.EmploymentUserModel;
import com.caohao.pojo.param.EmploymentParam;
import com.caohao.pojo.param.EmploymentUserParam;
import com.caohao.security.util.GetTokenInfoUtil;
import com.caohao.service.EmploymentUserService;
import com.caohao.service.ApplicationFlowLogService;
import com.caohao.service.UserNotificationService;
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

    @Resource
    private UserNotificationService userNotificationService;

    @Resource
    private ApplicationFlowLogService applicationFlowLogService;

    private static final Gson GSON = new Gson();

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
        return user != null && UserRoleEnum.Admin.name().equals(user.getRole());
    }

    private String normalizeRecruitStage(String recruitStage, String replyStatus) {
        if (replyStatus != null && !replyStatus.trim().isEmpty()) {
            switch (replyStatus) {
                case "Agree_With_Induction":
                    return "HIRED";
                case "Rejected":
                case "Refused_Entry":
                    return "REJECTED";
                default:
                    break;
            }
        }
        if (recruitStage != null && !recruitStage.trim().isEmpty()) {
            return recruitStage;
        }
        return "SUBMITTED";
    }

    private String normalizeUserStatus(String userStatus, String replyStatus) {
        if (userStatus != null && !userStatus.trim().isEmpty()) {
            return userStatus;
        }
        if ("Agree_With_Induction".equals(replyStatus)) {
            return "Pass";
        }
        if ("Rejected".equals(replyStatus) || "Refused_Entry".equals(replyStatus)) {
            return "Reject";
        }
        return "Normal";
    }

    private void recordFlowLog(EmploymentUserModel before,
                               EmploymentUserModel after,
                               String actionType,
                               String content,
                               UserModel operator,
                               EmploymentModel employmentModel) {
        if (after == null) {
            return;
        }
        ApplicationFlowLog log = new ApplicationFlowLog();
        log.setEmploymentUserId(after.getId());
        log.setEmploymentId(after.getEmploymentId());
        log.setCompanyId(employmentModel != null ? employmentModel.getCompanyId() : null);
        log.setApplicantUserId(after.getUserId());
        log.setOperatorUserId(operator != null ? operator.getId() : null);
        log.setOperatorRole(operator != null ? operator.getRole() : null);
        log.setActionType(actionType);
        log.setStageFrom(before != null ? before.getRecruitStage() : null);
        log.setStageTo(after.getRecruitStage());
        log.setReplyStatusFrom(before != null ? before.getReplyStatus() : null);
        log.setReplyStatusTo(after.getReplyStatus());
        log.setUserStatusFrom(before != null ? before.getUserStatus() : null);
        log.setUserStatusTo(after.getUserStatus());
        log.setContent(content);
        applicationFlowLogService.save(log);
    }

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
        UserModel currentUser = requireCurrentUser();
        String username = currentUser.getUserName();
        System.out.println("找到用户ID: " + currentUser.getId());
        System.out.println("用户名: " + username);

        EmploymentModel employmentModel = employmentDao.queryById(employmentUser.getEmploymentId());
        if (employmentModel == null) {
            throw new RuntimeException("投递职位不存在");
        }
        if (employmentModel.getStatus() == null || employmentModel.getStatus() != 1) {
            throw new RuntimeException("该职位暂不可投递");
        }

        EmploymentUserParam duplicateCheck = new EmploymentUserParam();
        duplicateCheck.setUserId(currentUser.getId());
        duplicateCheck.setEmploymentId(employmentUser.getEmploymentId());
        duplicateCheck.setType(1);
        List<EmploymentUserModel> existingApplications = employmentUserDao.queryAllByLimit(duplicateCheck);
        if (existingApplications != null && !existingApplications.isEmpty()) {
            throw new RuntimeException("请勿重复投递同一职位");
        }

        if (employmentUser.getResume() == null || employmentUser.getResume().trim().isEmpty()) {
            throw new RuntimeException("请先选择可用简历后再投递");
        }
        UserResume selectedResume = userResumeDao.queryById(employmentUser.getResume());
        if (selectedResume == null) {
            throw new RuntimeException("所选简历不存在");
        }
        if (!currentUser.getId().equals(selectedResume.getUserId())) {
            throw new RuntimeException("仅可投递本人上传的简历");
        }

        // 使用用户ID
        employmentUser.setUserId(currentUser.getId());
        // 设置类型为投递
        employmentUser.setType(1);
        if (employmentUser.getReplyStatus() == null || employmentUser.getReplyStatus().isEmpty()) {
            employmentUser.setReplyStatus("Wait_For_Reply");
        }
        employmentUser.setUserStatus(normalizeUserStatus(employmentUser.getUserStatus(), employmentUser.getReplyStatus()));
        employmentUser.setRecruitStage(normalizeRecruitStage(employmentUser.getRecruitStage(), employmentUser.getReplyStatus()));
        if (employmentUser.getDeleted() == null) {
            employmentUser.setDeleted(0);
        }

        this.employmentUserDao.insert(employmentUser);
        EmploymentUserModel created = this.queryById(employmentUser.getId());
        recordFlowLog(null, created, "SUBMIT", "候选人提交了职位申请", currentUser, employmentModel);
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
        if (existing == null) {
            throw new RuntimeException("申请记录不存在");
        }

        UserModel currentUser = requireCurrentUser();
        boolean admin = isAdmin(currentUser);
        EmploymentModel employmentModel = employmentDao.queryById(existing.getEmploymentId());
        if (employmentModel == null) {
            throw new RuntimeException("关联职位不存在");
        }
        CompanyModel companyModel = companyDao.queryById(employmentModel.getCompanyId());
        boolean companyOwner = companyModel != null && currentUser.getId().equals(companyModel.getUserId());
        if (!admin && !companyOwner) {
            throw new RuntimeException("无权处理该申请记录");
        }

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
        employmentUser.setRecruitStage(normalizeRecruitStage(employmentUser.getRecruitStage(), employmentUser.getReplyStatus()));
        employmentUser.setUserStatus(normalizeUserStatus(employmentUser.getUserStatus(), employmentUser.getReplyStatus()));

        this.employmentUserDao.update(employmentUser);
        EmploymentUserModel result = this.queryById(employmentUser.getId());
        String flowContent = (result.getReplyContent() != null && !result.getReplyContent().trim().isEmpty())
                ? result.getReplyContent()
                : "申请状态已更新";
        recordFlowLog(existing, result, "STATUS_CHANGE", flowContent, currentUser, employmentModel);
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
            payload.put("recruitStage", result.getRecruitStage());
            payload.put("replyContent", result.getReplyContent());
            payload.put("employmentId", result.getEmploymentId());
            payload.put("id", result.getId());
            String title = "求职申请状态更新";
            String content = (result.getReplyContent() != null && !result.getReplyContent().trim().isEmpty())
                    ? result.getReplyContent()
                    : "您投递的职位申请状态已更新为：" + result.getRecruitStage();
            userNotificationService.saveNotification(
                    applicant.getId(),
                    applicant.getUserName(),
                    "APPLICATION_STATUS",
                    title,
                    content,
                    "EMPLOYMENT_USER",
                    result.getId()
            );
            payload.put("notificationType", "APPLICATION_STATUS");
            payload.put("title", title);
            payload.put("content", content);
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
        EmploymentUserModel existing = this.employmentUserDao.queryById(id);
        if (existing == null) {
            throw new RuntimeException("申请记录不存在");
        }

        UserModel currentUser = requireCurrentUser();
        boolean admin = isAdmin(currentUser);
        boolean applicant = currentUser.getId().equals(existing.getUserId());
        EmploymentModel employmentModel = employmentDao.queryById(existing.getEmploymentId());
        CompanyModel companyModel = employmentModel == null ? null : companyDao.queryById(employmentModel.getCompanyId());
        boolean companyOwner = companyModel != null && currentUser.getId().equals(companyModel.getUserId());
        if (!admin && !applicant && !companyOwner) {
            throw new RuntimeException("无权删除该申请记录");
        }
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
        UserModel currentUser = requireCurrentUser();
        if (!UserRoleEnum.Enterprise_User.name().equals(currentUser.getRole()) && !isAdmin(currentUser)) {
            throw new RuntimeException("仅企业用户或管理员可查看企业申请记录");
        }

        if (!isAdmin(currentUser)) {
            CompanyModel myCompany = companyDao.queryByUserId(currentUser.getId());
            if (myCompany == null) {
                throw new RuntimeException("当前企业账号未绑定企业信息");
            }
            employmentUser.setCompanyId(myCompany.getId());
        }

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
        UserModel currentUser = requireCurrentUser();
        if (!isAdmin(currentUser)) {
            throw new RuntimeException("仅管理员可查看全局申请记录");
        }
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
