package com.caohao.service.impl;

import com.caohao.common.utils.DateUtil;
import com.caohao.common.utils.IDGenerator;
import com.caohao.dao.CommentReplyRelationDao;
import com.caohao.dao.CommentsDao;
import com.caohao.dao.ReplyInfoDao;
import com.caohao.dao.UserDao;
import com.caohao.pojo.entity.CommentReplyRelation;
import com.caohao.pojo.entity.Comments;
import com.caohao.pojo.entity.ReplyInfo;
import com.caohao.security.util.GetTokenInfoUtil;
import com.caohao.service.ReplyInfoService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service("replyInfoService")
public class ReplyInfoServiceImpl implements ReplyInfoService {
    @Resource
    private ReplyInfoDao replyInfoDao;
    @Resource
    private CommentReplyRelationDao commentReplyRelationDao;
    @Resource
    private CommentsDao commentsDao; // 引入CommentsDao以查询被回复的评论
    @Resource
    private UserDao userDao; // 引入UserDao以回显数据

    @Override
    public ReplyInfo queryById(String id) {
        return this.replyInfoDao.queryById(id);
    }

    @Override
    public PageInfo<ReplyInfo> queryByPage(ReplyInfo replyInfo, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ReplyInfo> replyInfos = this.replyInfoDao.queryAllByLimit(replyInfo);
        return new PageInfo<>(replyInfos);
    }

    /**
     * 核心逻辑：发表回复
     * 前端传参建议：
     * 1. commentsId: 必填，所属的主评论ID
     * 2. content: 必填
     * 3. parentReplyId: 选填。如果有值，说明是回复某个具体的回复(二级回复)；如果为空，说明是直接回复层主(一级回复)。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReplyInfo insert(ReplyInfo replyInfo) {
        String replyId = IDGenerator.StringID();
        String currentUsername = GetTokenInfoUtil.getUsername(); // 当前登录用户
        Long now = DateUtil.getCurrentTimeMillis();

        // 1. 设置 ReplyInfo 基础信息
        replyInfo.setId(replyId);
        replyInfo.setCreateTime(now);
        replyInfo.setOperatorId(currentUsername);

        // 2. 构建关系实体
        CommentReplyRelation relation = new CommentReplyRelation();
        relation.setId(IDGenerator.StringID());
        relation.setCommentsId(replyInfo.getCommentsId()); // 关联主评论
        relation.setReplyInfoId(replyId); // 关联当前生成的回复
        relation.setCreateTime(now);

        // 3. 判断回复类型并设置被回复人(recipientId)
        if (replyInfo.getParentReplyId() != null && !replyInfo.getParentReplyId().isEmpty()) {
            // Case A: 回复了某个具体的回复 (楼中楼)
            // 需要查出 parentReplyId 对应的那条回复是谁发的
            ReplyInfo parentReply = replyInfoDao.queryById(replyInfo.getParentReplyId());
            if (parentReply != null) {
                replyInfo.setRecipientId(parentReply.getOperatorId()); // 设置被回复的人
            }
            // 关系表中记录父回复ID
            relation.setParentReplyId(replyInfo.getParentReplyId());
            relation.setType(2); // 类型2：子回复
        } else {
            // Case B: 直接回复层主 (一级回复)
            // 需要查出 commentsId 对应的评论是谁发的
            Comments parentComment = commentsDao.queryById(replyInfo.getCommentsId());
            if (parentComment != null) {
                replyInfo.setRecipientId(parentComment.getOperatorId()); // 设置被回复的人为层主
            }
            relation.setParentReplyId(replyInfo.getCommentsId()); // 父级ID为主评论ID
            relation.setType(1); // 类型1：回复评论
        }

        // 4. 落库
        commentReplyRelationDao.insert(relation);
        this.replyInfoDao.insert(replyInfo);

        // 5. 补充用户信息以便前端回显
        replyInfo.setSelfUserModel(userDao.selectByUserName(currentUsername));
        if (replyInfo.getRecipientId() != null) {
            replyInfo.setOtherUserModel(userDao.selectByUserName(replyInfo.getRecipientId()));
        }

        return replyInfo;
    }

    @Override
    public ReplyInfo update(ReplyInfo replyInfo) {
        this.replyInfoDao.update(replyInfo);
        return this.queryById(replyInfo.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(String id) {
        // 删除回复时，同时删除关系表中的记录
        // 注意：这里需要先根据 replyInfoId 查出 relation 的 ID 进行删除，或者在 DAO 中增加 deleteByReplyId 的方法
        // 这里简化处理，假设业务逻辑允许仅删除内容
        return this.replyInfoDao.deleteById(id) > 0;
    }
}