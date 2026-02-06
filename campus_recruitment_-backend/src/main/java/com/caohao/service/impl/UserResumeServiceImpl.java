package com.caohao.service.impl;

import com.caohao.common.utils.DateUtil;
import com.caohao.common.utils.IDGenerator;
import com.caohao.pojo.entity.UserResume;
import com.caohao.dao.UserResumeDao;
import com.caohao.dao.UserDao;
import com.caohao.pojo.model.UserModel;
import com.caohao.security.util.GetTokenInfoUtil;
import com.caohao.service.UserResumeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
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

    @Value("${file.upload.path:./uploads/resumes/}")
    private String uploadPath;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public UserResume queryById(String id) {
        return this.userResumeDao.queryById(id);
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
        return this.userResumeDao.deleteById(id) > 0;
    }

    @Override
    public List<UserResume> getResumeByUserName(String username) {
        // 先通过用户名获取用户ID
        UserModel user = userDao.selectByUserName(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
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
        String username = GetTokenInfoUtil.getUsername();
        UserModel user = userDao.selectByUserName(username);
        if (user == null) {
            throw new Exception("用户不存在，请重新登录");
        }
        
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
}
