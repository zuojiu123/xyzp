package com.caohao.service.impl;

import com.caohao.common.enums.impl.CompanyStatusEnum;
import com.caohao.common.enums.impl.UserRoleEnum;
import com.caohao.common.utils.DateUtil;
import com.caohao.common.utils.IDGenerator;
import com.caohao.controller.websocket.server.WebsocketServer;
import com.caohao.security.util.GetTokenInfoUtil;
import com.caohao.dao.EmploymentDao;
import com.caohao.dao.EmploymentUserDao;
import com.caohao.dao.UserDao;
import com.caohao.pojo.entity.Company;
import com.caohao.dao.CompanyDao;
import com.caohao.pojo.entity.Employment;
import com.caohao.pojo.model.CompanyModel;
import com.caohao.pojo.model.EmploymentModel;
import com.caohao.pojo.model.UserModel;
import com.caohao.pojo.param.CompanyParam;
import com.caohao.pojo.param.EmploymentParam;
import com.caohao.service.AuditLogService;
import com.caohao.service.CompanyService;
import com.caohao.service.UserNotificationService;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * (Company)表服务实现类
 *
 * @author caohao
 * @since 2021-11-14 16:28:23
 */
@Service("companyService")
public class CompanyServiceImpl implements CompanyService {
    private static final Gson GSON = new Gson();

    @Resource
    private CompanyDao companyDao;

    @Resource
    private EmploymentDao employmentDao;

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

    private String companyStatusText(String status) {
        if (status == null) {
            return null;
        }
        switch (status) {
            case "Check_Pending":
                return "待审核";
            case "Approve":
                return "审核通过";
            case "Audit_Failed":
                return "审核未通过";
            default:
                return status;
        }
    }

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public CompanyModel queryById(String id) {
        CompanyModel companyModel = this.companyDao.queryById(id);
        if (companyModel == null) {
            return null;
        }

        UserModel currentUser = getCurrentUserOrNull();
        boolean admin = isAdmin(currentUser);
        boolean owner = currentUser != null && currentUser.getId().equals(companyModel.getUserId());
        if (!CompanyStatusEnum.Approve.name().equals(companyModel.getStatus()) && !admin && !owner) {
            throw new RuntimeException("该企业信息尚未通过审核");
        }

        // 再更新阅读次数
        this.companyDao.updateViewCount(id);
        List<EmploymentModel> employmentModels = employmentDao.selectByCompanyId(id);
        companyModel.setEmploymentModels(employmentModels);
        // 设置职位数量
        companyModel.setJobCount(employmentModels.size());
        return companyModel;
    }

    /**
     * 分页查询
     *
     * @param company 筛选条件
     * @param pageNum      当前页数
     * @param pageSize     每页显示数量
     * @return 查询结果
     */
    @Override
    public PageInfo<CompanyModel> queryByPage(CompanyParam company, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<CompanyModel> companys =this.companyDao.queryAllByLimit(company);
        // 为每个公司添加职位数量统计
        for (CompanyModel companyModel : companys) {
            List<EmploymentModel> employmentModels = employmentDao.selectByCompanyId(companyModel.getId());
            companyModel.setPublishedSize(employmentModels.size());
            // 设置职位数量字段供前端使用
            companyModel.setJobCount(employmentModels.size());
        }
        return new PageInfo<>(companys);
    }

    /**
     * 新增数据
     *
     * @param company 实例对象
     * @return 实例对象
     */
    @Override
    public CompanyParam insert(CompanyParam company) {
        UserModel user = requireCurrentUser();
        CompanyModel existing = companyDao.queryByUserId(user.getId());

        if (existing != null) {
            company.setId(existing.getId());
            company.setUserId(existing.getUserId());
            company.setCreateTime(existing.getCreateTime());
            company.setUpdateTime(DateUtil.getCurrentTimeMillis());
            company.setStatus(existing.getStatus());
            company.setProcessor(existing.getProcessor());
            company.setReplyTime(existing.getReplyTime());
            company.setReplyContent(existing.getReplyContent());
            this.companyDao.update(company);
            return company;
        }

        company.setId(IDGenerator.StringID());
        company.setCreateTime(DateUtil.getCurrentTimeMillis());
        company.setUpdateTime(DateUtil.getCurrentTimeMillis());
        company.setUserId(user.getId());

        if (company.getStatus() == null || company.getStatus().isEmpty()) {
            company.setStatus(CompanyStatusEnum.Check_Pending.name());
        }

        this.companyDao.insert(company);
        return company;
    }

    /**
     * 修改数据
     *
     * @param company 实例对象
     * @return 实例对象
     */
    @Override
    public CompanyModel update(CompanyParam company) {
        CompanyModel existing = this.companyDao.queryById(company.getId());
        if (existing == null) {
            throw new RuntimeException("公司信息不存在");
        }

        UserModel currentUser = requireCurrentUser();
        boolean admin = isAdmin(currentUser);
        if (!admin && !currentUser.getId().equals(existing.getUserId())) {
            throw new RuntimeException("无权修改该企业信息");
        }

        company.setUserId(existing.getUserId());
        if (!admin) {
            company.setStatus(existing.getStatus());
            company.setProcessor(existing.getProcessor());
            company.setReplyTime(existing.getReplyTime());
            company.setReplyContent(existing.getReplyContent());
        }
        company.setUpdateTime(DateUtil.getCurrentTimeMillis());
        this.companyDao.update(company);
        return this.queryById(company.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(String id) {
        CompanyModel existing = this.companyDao.queryById(id);
        if (existing == null) {
            throw new RuntimeException("公司信息不存在");
        }

        UserModel currentUser = requireCurrentUser();
        boolean admin = isAdmin(currentUser);
        if (!admin && !currentUser.getId().equals(existing.getUserId())) {
            throw new RuntimeException("无权删除该企业信息");
        }

        EmploymentParam employmentParam = new EmploymentParam();
        employmentParam.setCompanyId(id);
        List<EmploymentModel> employmentModels = employmentDao.queryAllByLimit(employmentParam);
        for (EmploymentModel employmentModel : employmentModels) {
            employmentUserDao.deleteByEmployId(employmentModel.getId());
        }

        employmentDao.deleteByCompanyId(id);
        return this.companyDao.deleteById(id) > 0;
    }

    @Override
    public CompanyModel queryByUsername(String username) {
        // 先通过用户名获取用户ID
        UserModel user = userDao.selectByUserName(username);
        if (user == null) {
            System.out.println("queryByUsername - 用户不存在: " + username);
            return null;
        }
        System.out.println("queryByUsername - 用户ID: " + user.getId());
        
        // 通过用户ID查询公司
        CompanyModel company = companyDao.queryByUserId(user.getId());
        System.out.println("queryByUsername - 查询到的公司: " + (company != null ? company.getId() : "null"));
        
        if (company == null) {
            // 如果没有公司，尝试通过旧的方式查询（兼容性）
            company = companyDao.queryByUsername(username);
            System.out.println("queryByUsername - 兼容性查询结果: " + (company != null ? company.getId() : "null"));
        }
        return company;
    }

    @Override
    public CompanyModel auditCompany(CompanyParam company) {
        UserModel currentUser = requireCurrentUser();
        if (!isAdmin(currentUser)) {
            throw new RuntimeException("仅管理员可执行企业审核");
        }
        CompanyModel existing = this.companyDao.queryById(company.getId());
        if (existing == null) {
            throw new RuntimeException("公司信息不存在");
        }
        String beforeStatus = existing.getStatus();
        company.setUpdateTime(DateUtil.getCurrentTimeMillis());
        company.setReplyTime(DateUtil.getCurrentTimeMillis());
        company.setProcessor(currentUser.getUserName());
        this.companyDao.update(company);
        CompanyModel result = this.queryById(company.getId());
        String auditContent = company.getReplyContent();
        if (auditContent == null || auditContent.trim().isEmpty()) {
            auditContent = CompanyStatusEnum.Approve.name().equals(result.getStatus()) ? "企业审核通过" : "企业审核状态已更新";
        }
        auditLogService.saveAuditLog(
                "COMPANY",
                result.getId(),
                result.getName(),
                companyStatusText(beforeStatus),
                companyStatusText(result.getStatus()),
                auditContent,
                currentUser.getId(),
                currentUser.getUserName()
        );
        UserModel targetUser = null;
        userNotificationService.saveNotification(
                result.getUserId(),
                targetUser != null ? targetUser.getUserName() : null,
                "COMPANY_AUDIT",
                "企业审核结果通知",
                "您的企业信息审核状态已更新为：" + companyStatusText(result.getStatus()) + (auditContent != null ? "。" + auditContent : ""),
                "COMPANY",
                result.getId()
        );
        targetUser = userDao.queryById(result.getUserId());
        if (targetUser != null && targetUser.getUserName() != null) {
            java.util.Map<String, Object> payload = new java.util.HashMap<>();
            payload.put("type", "notification");
            payload.put("notificationType", "COMPANY_AUDIT");
            payload.put("title", "企业审核结果通知");
            payload.put("content", "您的企业信息审核状态已更新为：" + companyStatusText(result.getStatus()) + (auditContent != null ? "。" + auditContent : ""));
            payload.put("targetType", "COMPANY");
            payload.put("targetId", result.getId());
            WebsocketServer.pushMessage(null, GSON.toJson(payload), targetUser.getUserName());
        }
        return result;
    }

    @Override
    public int getTotalCount() {
        return companyDao.getTotalCount();
    }

    @Override
    public int getPendingCount() {
        return companyDao.getCountByStatus("Check_Pending");
    }

    @Override
    public int getApprovedCount() {
        return companyDao.getCountByStatus("Approve");
    }

    @Override
    public int getRejectedCount() {
        return companyDao.getCountByStatus("Audit_Failed");
    }
}
