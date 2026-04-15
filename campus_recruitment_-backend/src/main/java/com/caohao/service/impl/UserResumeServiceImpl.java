package com.caohao.service.impl;

import com.caohao.common.enums.impl.UserRoleEnum;
import com.caohao.common.utils.DateUtil;
import com.caohao.common.utils.IDGenerator;
import com.caohao.pojo.entity.UserResume;
import com.caohao.dao.UserResumeDao;
import com.caohao.dao.UserDao;
import com.caohao.dao.CompanyDao;
import com.caohao.dao.EmploymentUserDao;
import com.caohao.pojo.model.CompanyModel;
import com.caohao.pojo.model.UserModel;
import com.caohao.security.util.GetTokenInfoUtil;
import com.caohao.service.UserResumeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * (UserResume)表服务实现类
 *
 * @author caohao
 * @since 2022-04-03 19:23:29
 */
@Service("userResumeService")
public class UserResumeServiceImpl implements UserResumeService {
    @Resource
    private UserResumeDao userResumeDao;
    
    @Resource
    private UserDao userDao;

    @Resource
    private CompanyDao companyDao;

    @Resource
    private EmploymentUserDao employmentUserDao;

    @Value("${file.upload.path:./uploads/resumes/}")
    private String uploadPath;

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

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public UserResume queryById(String id) {
        UserResume resume = this.userResumeDao.queryById(id);
        if (resume == null) {
            return null;
        }
        UserModel currentUser = requireCurrentUser();
        if (!isAdmin(currentUser) && !currentUser.getId().equals(resume.getUserId())) {
            throw new RuntimeException("无权查看该简历信息");
        }
        return resume;
    }

    /**
     * 分页查询
     *
     * @param userResume 筛选条件
     * @param pageNum      当前页数
     * @param pageSize     每页显示数量
     * @return 查询结果
     */
    @Override
    public PageInfo<UserResume> queryByPage(UserResume userResume,  Integer pageNum, Integer pageSize) {
        UserModel currentUser = requireCurrentUser();
        if (!isAdmin(currentUser)) {
            userResume.setUserId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<UserResume> userResumes =this.userResumeDao.queryAllByLimit(userResume);
        return new PageInfo<>(userResumes);
    }

    /**
     * 新增数据
     *
     * @param userResume 实例对象
     * @return 实例对象
     */
    @Override
    public UserResume insert(UserResume userResume) {
        UserModel currentUser = requireCurrentUser();
        if (!isAdmin(currentUser)) {
            userResume.setUserId(currentUser.getId());
        }
        this.userResumeDao.insert(userResume);
        return userResume;
    }

    /**
     * 修改数据
     *
     * @param userResume 实例对象
     * @return 实例对象
     */
    @Override
    public UserResume update(UserResume userResume) {
        UserResume existing = this.userResumeDao.queryById(userResume.getId());
        if (existing == null) {
            throw new RuntimeException("简历不存在");
        }
        UserModel currentUser = requireCurrentUser();
        if (!isAdmin(currentUser) && !currentUser.getId().equals(existing.getUserId())) {
            throw new RuntimeException("无权修改该简历");
        }
        userResume.setUserId(existing.getUserId());
        this.userResumeDao.update(userResume);
        return this.queryById(userResume.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(String id) {
        UserResume existing = this.userResumeDao.queryById(id);
        if (existing == null) {
            throw new RuntimeException("简历不存在");
        }
        UserModel currentUser = requireCurrentUser();
        if (!isAdmin(currentUser) && !currentUser.getId().equals(existing.getUserId())) {
            throw new RuntimeException("无权删除该简历");
        }
        return this.userResumeDao.deleteById(id) > 0;
    }

    @Override
    public List<UserResume> getResumeByUserName(String username) {
        // 先通过用户名获取用户ID
        UserModel user = userDao.selectByUserName(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        UserModel currentUser = requireCurrentUser();
        if (!isAdmin(currentUser) && !currentUser.getId().equals(user.getId())) {
            throw new RuntimeException("无权查看该用户简历列表");
        }
        return userResumeDao.getResumeByUserName(user.getId());
    }

    @Override
    public UserResume uploadResume(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new Exception("文件不能为空");
        }

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new Exception("文件名不能为空");
        }

        // 获取文件扩展名
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex + 1);
        }

        // 生成唯一文件名
        String fileName = UUID.randomUUID().toString() + "." + extension;

        // 确保上传目录存在
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 保存文件
        Path filePath = Paths.get(uploadPath, fileName);
        Files.copy(file.getInputStream(), filePath);

        // 获取当前用户ID
        UserModel user = requireCurrentUser();
        
        // 创建简历记录
        UserResume userResume = new UserResume();
        userResume.setId(IDGenerator.StringID());
        userResume.setUserId(user.getId());
        userResume.setResumeName(originalFilename);
        userResume.setResumeUrl(fileName);
        userResume.setType(file.getContentType());
        userResume.setExtension(extension);
        userResume.setResumeType(1);
        userResume.setCreateTime(DateUtil.getCurrentTimeMillis());

        this.userResumeDao.insert(userResume);
        return userResume;
    }

    @Override
    public void writeResumeToResponse(String resumeRecordId, boolean inline, HttpServletResponse response) throws IOException {
        UserResume resume = userResumeDao.queryById(resumeRecordId);
        if (resume == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        if (!canAccessResume(auth, resume)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String stored = resume.getResumeUrl();
        if (stored == null || stored.isEmpty()
                || stored.contains("..") || stored.contains("/") || stored.contains("\\")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Path baseDir = Paths.get(uploadPath).toAbsolutePath().normalize();
        Path filePath = baseDir.resolve(stored).normalize();
        if (!filePath.startsWith(baseDir) || !Files.isRegularFile(filePath)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String fileName = resume.getResumeName() != null ? resume.getResumeName() : stored;
        String asciiName = fileName.replaceAll("[^\\x20-\\x7E]", "_");
        String dispositionType = inline ? "inline" : "attachment";
        String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replace("+", "%20");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                dispositionType + "; filename=\"" + asciiName + "\"; filename*=UTF-8''" + encoded);

        String ct = Files.probeContentType(filePath);
        if (ct == null || ct.isEmpty()) {
            String lower = fileName.toLowerCase();
            if (lower.endsWith(".pdf")) {
                ct = "application/pdf";
            } else if (lower.endsWith(".doc")) {
                ct = "application/msword";
            } else if (lower.endsWith(".docx")) {
                ct = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            } else {
                ct = "application/octet-stream";
            }
        }
        response.setContentType(ct);
        response.setContentLengthLong(Files.size(filePath));
        Files.copy(filePath, response.getOutputStream());
    }

    private boolean isAdminAuthority(String authority) {
        if (authority == null) {
            return false;
        }
        return "Admin".equals(authority)
                || "ROLE_Admin".equals(authority)
                || "ROLE_ADMIN".equalsIgnoreCase(authority);
    }

    private boolean canAccessResume(Authentication auth, UserResume resume) {
        for (GrantedAuthority a : auth.getAuthorities()) {
            if (isAdminAuthority(a.getAuthority())) {
                return true;
            }
        }
        String username = auth.getName();
        UserModel user = userDao.selectByUserName(username);
        if (user == null) {
            return false;
        }
        if (user.getId().equals(resume.getUserId())) {
            return true;
        }
        for (GrantedAuthority a : auth.getAuthorities()) {
            if ("Enterprise_User".equals(a.getAuthority())) {
                CompanyModel company = companyDao.queryByUserId(user.getId());
                if (company != null
                        && employmentUserDao.countByResumeRecordIdAndCompanyId(resume.getId(), company.getId()) > 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
