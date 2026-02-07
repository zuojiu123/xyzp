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
import com.caohao.service.CommentsService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service("commentsService")
public class CommentsServiceImpl implements CommentsService {
    @Resource
    private CommentsDao commentsDao;
    @Resource
    private ReplyInfoDao replyInfoDao;
    @Resource
    private CommentReplyRelationDao commentReplyRelationDao;
    @Resource
    private UserDao userDao;

    @Override
    public Comments queryById(String id) {
        return this.commentsDao.queryById(id);
    }

    @Override
    public PageInfo<Comments> queryByPage(Comments comments, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Comments> list = this.getCommentsData(comments);
        return new PageInfo<>(list);
    }

    @Override
    public Comments insert(Comments comments) {
        comments.setId(IDGenerator.StringID());
        // 获取当前登录用户
        comments.setOperatorId(GetTokenInfoUtil.getUsername());
        comments.setCreateTime(DateUtil.getCurrentTimeMillis());
        this.commentsDao.insert(comments);
        // 插入后回显用户信息，方便前端直接展示
        comments.setUserModel(userDao.selectByUserName(comments.getOperatorId()));
        return comments;
    }

    @Override
    public Comments update(Comments comments) {
        this.commentsDao.update(comments);
        return this.queryById(comments.getId());
    }

    /**
     * 级联删除：删除评论 -> 删除评论下的回复关系 -> 删除回复内容
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(String id) {
        // 1. 查找该评论下的所有回复关系
        CommentReplyRelation relationQuery = new CommentReplyRelation();
        relationQuery.setCommentsId(id);
        List<CommentReplyRelation> relations = commentReplyRelationDao.queryAllByLimit(relationQuery);

        // 2. 如果有回复，先删除回复内容和关系
        if (relations != null && !relations.isEmpty()) {
            for (CommentReplyRelation relation : relations) {
                // 删除回复本体
                replyInfoDao.deleteById(relation.getReplyInfoId());
                // 删除关系
                commentReplyRelationDao.deleteById(relation.getId());
            }
        }

        // 3. 删除评论本身
        return this.commentsDao.deleteById(id) > 0;
    }

    @Override
    public Object getCommentsByArticle(String articleId) {
        Comments query = new Comments();
        query.setArticleId(articleId);
        return this.getCommentsData(query);
    }

    /**
     * 核心修复逻辑：正确组装评论和回复数据
     */
    private List<Comments> getCommentsData(Comments query) {
        // 1. 查询所有主评论
        List<Comments> commentsList = this.commentsDao.queryAllByLimit(query);

        if (commentsList == null || commentsList.isEmpty()) {
            return new ArrayList<>();
        }

        for (Comments comment : commentsList) {
            // 2. 填充主评论的用户信息
            comment.setUserModel(userDao.selectByUserName(comment.getOperatorId()));

            // 3. 查询该评论下的所有回复关系
            // 注意：不管是直接回复层主，还是回复楼中楼，只要 commentsId 是这个评论的ID，都应该被查出来
            CommentReplyRelation relationQuery = new CommentReplyRelation();
            relationQuery.setCommentsId(comment.getId());

            // 这里假设 dao.queryAllByLimit 能根据 commentsId 查出所有关联记录
            List<CommentReplyRelation> relations = commentReplyRelationDao.queryAllByLimit(relationQuery);

            if (relations != null && !relations.isEmpty()) {
                // 4. 提取所有回复的 ID
                List<String> replyIds = relations.stream()
                        .map(CommentReplyRelation::getReplyInfoId)
                        .collect(Collectors.toList());

                // 5. 批量查询回复详情
                // 请确保 ReplyInfoDao.xml 中 selectByIDS 实现了 where id in (...)
                List<ReplyInfo> replyInfos = replyInfoDao.selectByIDS(replyIds);

                if (replyInfos != null) {
                    // 6. 填充每条回复的用户信息
                    for (ReplyInfo replyInfo : replyInfos) {
                        // 填充回复者信息 (selfUserModel)
                        replyInfo.setSelfUserModel(userDao.selectByUserName(replyInfo.getOperatorId()));

                        // 填充被回复者信息 (otherUserModel)
                        // 如果 recipientId 不为空，说明是回复某个人
                        if (replyInfo.getRecipientId() != null && !replyInfo.getRecipientId().isEmpty()) {
                            replyInfo.setOtherUserModel(userDao.selectByUserName(replyInfo.getRecipientId()));
                        }
                    }

                    // 7. (可选) 按时间排序，保证新回复在底部
                    replyInfos.sort((a, b) -> {
                        long t1 = a.getCreateTime() != null ? a.getCreateTime() : 0;
                        long t2 = b.getCreateTime() != null ? b.getCreateTime() : 0;
                        return Long.compare(t1, t2);
                    });

                    // 8. 将组装好的回复列表塞给主评论对象
                    comment.setReplyInfos(replyInfos);
                }
            } else {
                comment.setReplyInfos(new ArrayList<>());
            }
        }
        return commentsList;
    }
}