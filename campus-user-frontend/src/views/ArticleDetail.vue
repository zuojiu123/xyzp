<template>
  <div class="article-detail-page">
    <div class="container">

      <!-- 1. 文章主体卡片 -->
      <div class="main-content-wrapper" v-loading="loading">
        <div class="article-card-clean">
          <!-- 头部信息 -->
          <div class="article-header">
            <div class="meta-row">
              <el-tag size="small" type="primary" effect="plain" class="category-tag" v-if="article.type">
                {{ article.type }}
              </el-tag>
              <span class="publish-time">{{ formatTime(article.createTime) }}</span>
            </div>
            <h1 class="article-title">{{ article.title }}</h1>

            <div class="author-bar">
              <el-avatar :size="40" icon="el-icon-user-solid" class="author-avatar"></el-avatar>
              <div class="author-info">
                <span class="author-name">{{ article.userName || '匿名用户' }}</span>
                <span class="author-desc">发布于 校招平台</span>
              </div>
            </div>
          </div>

          <div class="divider"></div>

          <!-- 正文内容 -->
          <div class="article-body typography" v-html="article.content"></div>

          <!-- 底部操作区 -->
          <div class="article-actions-bar">
            <div class="action-btn-group">
              <button
                  class="action-btn"
                  :class="{ active: article.isLiked }"
                  @click="toggleLike"
                  :disabled="liking">
                <i class="el-icon-thumb"></i>
                <span>{{ article.thumbUp || 0 }} 点赞</span>
              </button>
              <button
                  class="action-btn"
                  :class="{ active: article.isCollected }"
                  @click="toggleCollect"
                  :disabled="collecting">
                <i class="el-icon-star-off"></i>
                <span>{{ article.collectNumber || 0 }} 收藏</span>
              </button>
            </div>
          </div>
        </div>

        <!-- 2. 评论区 -->
        <div class="comments-section">
          <div class="section-title">
            <h3>全部评论 <span class="count">({{ comments.length }})</span></h3>
          </div>

          <!-- 评论输入框 -->
          <div class="comment-input-card">
            <div class="input-header">
              <el-avatar :size="32" icon="el-icon-user-solid" class="current-user-avatar"></el-avatar>
              <span class="input-tip">发表你的观点...</span>
            </div>
            <el-input
                type="textarea"
                v-model="newComment"
                :rows="3"
                placeholder="友善的评论是交流的起点"
                maxlength="500"
                resize="none"
                class="custom-textarea"
                show-word-limit>
            </el-input>
            <div class="input-footer">
              <el-button
                  type="primary"
                  size="medium"
                  class="submit-btn"
                  @click="submitComment"
                  :loading="commenting">
                发布评论
              </el-button>
            </div>
          </div>

          <!-- 评论列表 -->
          <div class="comments-list-wrapper">
            <template v-if="comments.length > 0">
              <div
                  v-for="comment in comments"
                  :key="comment.id"
                  class="comment-row">

                <div class="comment-left">
                  <el-avatar :size="40" icon="el-icon-user-solid" class="comment-avatar"></el-avatar>
                </div>

                <div class="comment-right">
                  <div class="comment-meta">
                    <span class="username">{{
                        comment.userModel && comment.userModel.nickName
                            ? comment.userModel.nickName
                            : (comment.operatorId || '匿名用户')
                      }}</span>
                    <span class="time">{{ formatTime(comment.createTime) }}</span>
                  </div>

                  <div class="comment-content-text">{{ comment.content }}</div>

                  <div class="comment-tools">
                    <span class="tool-btn"
                          @click="showReplyInput(comment.id, null, comment.userModel ? comment.userModel.nickName : comment.userName)">
  <i class="el-icon-chat-round"></i> 回复
</span>
                  </div>

                  <!-- 回复输入框 -->
                  <transition name="el-zoom-in-top">
                    <div v-if="replyingToCommentId  === comment.id" class="reply-input-box">
                      <!-- 在 reply-input-box 区域 -->
                      <el-input
                          type="textarea"
                          v-model="replyContent"
                          :rows="2"
                          :placeholder="replyingToUser ? `回复 ${replyingToUser}...` : '请输入回复内容...'"
                          class="reply-textarea"
                          resize="none">
                      </el-input>
                      <div class="reply-actions">
                        <el-button size="mini" type="text" @click="cancelReply">取消</el-button>
                        <el-button
                            type="primary"
                            size="mini"
                            @click="submitReply(comment.id)"
                            :loading="replying">
                          发送
                        </el-button>
                      </div>
                    </div>
                  </transition>

                  <!-- 二级回复列表 -->
                  <div v-if="comment.replyInfos && comment.replyInfos.length > 0" class="sub-comments-list">
                    <div
                        v-for="reply in (comment.isExpanded ? comment.replyInfos : comment.replyInfos.slice(0, 2))"
                        :key="reply.id"
                        class="sub-comment-item">
                      <!-- ... 原有的子回复内容 ... -->
                      <div class="sub-header">
                        <span class="sub-user">{{
                            reply.selfUserModel ? reply.selfUserModel.nickName : (reply.userName || '匿名')
                          }}</span>
                        <template v-if="reply.otherUserModel">
                          <span style="margin: 0 4px; color: #94a3b8;">回复</span>
                          <span class="sub-user">@{{ reply.otherUserModel.nickName }}</span>
                        </template>
                        <span class="sub-time">{{ formatTime(reply.createTime) }}</span>
                      </div>
                      <div class="sub-content">{{ reply.content }}</div>
                      <div class="sub-actions" style="margin-top: 4px; text-align: right;">
                        <el-button
                            type="text"
                            size="mini"
                            @click="showReplyInput(comment.id, reply.id, reply.selfUserModel ? reply.selfUserModel.nickName : reply.userName)">
                          回复
                        </el-button>
                      </div>
                    </div>

                    <!-- 展开/收起 控制按钮 -->
                    <!-- 只有当子回复总数大于 2 条时才显示这个按钮 -->
                    <div v-if="comment.replyInfos.length > 2" class="toggle-replies-btn">
                      <el-button type="text" size="small" @click="toggleReplies(comment)">
                        {{ comment.isExpanded ? '收起回复' : `查看全部 ${comment.replyInfos.length} 条回复` }}
                        <i :class="comment.isExpanded ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
                      </el-button>
                    </div>

                  </div>
                </div>
              </div>
            </template>

            <!-- 空状态 -->
            <div v-else class="empty-comments-state">
              <img src="https://img.icons8.com/ios/100/cbd5e1/comments.png" alt="no comments">
              <p>还没有人评论，来抢沙发吧~</p>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
// 逻辑部分保持完全一致
export default {
  name: 'ArticleDetail',
  data() {
    return {
      article: {},
      comments: [],
      newComment: '',
      replyContent: '',
      replyingTo: null,
      loading: false,
      commenting: false,
      replying: false,
      liking: false,
      collecting: false,
      replyingToCommentId: null,
      replyingToSpecificId: null,
      replyingToUser: '',
    }
  },
  methods: {
    async loadArticle() {
      this.loading = true
      try {
        const id = this.$route.params.id
        const response = await this.$api.article.getArticleById(id)
        this.article = response || {}
        
        // 从localStorage获取用户的点赞和收藏状态
        const token = localStorage.getItem('token')
        if (token) {
          const likedArticles = JSON.parse(localStorage.getItem('likedArticles') || '[]')
          const collectedArticles = JSON.parse(localStorage.getItem('collectedArticles') || '[]')
          this.article.isLiked = likedArticles.includes(id)
          this.article.isCollected = collectedArticles.includes(id)
        } else {
          this.article.isLiked = false
          this.article.isCollected = false
        }
      } catch (error) {
        console.error('获取文章详情失败:', error)
        this.$message.error('获取文章详情失败')
      } finally {
        this.loading = false
      }
    },

    async loadComments() {
      try {
        const id = this.$route.params.id
        const response = await this.$api.comments.getCommentsByArticle(id)
        this.comments = (response || []).map(comment => ({
          ...comment,
          isExpanded: false // 默认折叠，只显示部分
        }))
      } catch (error) {
        console.error('获取评论失败:', error)
      }
    },
    toggleReplies(comment) {
      // 切换展开/收起状态
      comment.isExpanded = !comment.isExpanded
    },

    async submitComment() {
      if (!this.newComment.trim()) {
        this.$message.warning('请输入评论内容')
        return
      }
      const token = localStorage.getItem('token')
      if (!token) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      this.commenting = true
      try {
        await this.$api.comments.createComment({
          articleId: this.article.id,
          content: this.newComment
        })
        this.$message.success('评论发表成功')
        this.newComment = ''
        await this.loadComments()
      } catch (error) {
        this.$message.error('发表评论失败')
      } finally {
        this.commenting = false
      }
    },

    showReplyInput(commentId) {
      this.replyingToCommentId = commentId;
      this.replyingToSpecificId = specificId;
      // 设置 placeholder 显示的名字
      this.replyingToUser = targetName ? `@${targetName}` : '';
      this.replyContent = '';
    },

    cancelReply() {
      this.replyingToCommentId = null;
      this.replyingToSpecificId = null;
      this.replyContent = '';
    },

    async submitReply(commentId) {
      if (!this.replyContent.trim()) {
        this.$message.warning('请输入回复内容');
        return;
      }

      this.replying = true;
      try {
        // 构造参数
        const payload = {
          commentsId: commentId, // 必须：所属的主评论ID
          content: this.replyContent,
          // 关键：如果点击的是楼中楼的回复按钮，这里传那个楼中楼的ID
          parentReplyId: this.replyingToSpecificId
        };

        await this.$api.replyInfo.createReply(payload);

        this.$message.success('回复成功');
        this.cancelReply();
        await this.loadComments(); // 重新加载列表
      } catch (error) {
        console.error(error);
        this.$message.error('回复失败');
      } finally {
        this.replying = false;
      }
    },

    async toggleLike() {
      const token = localStorage.getItem('token')
      if (!token) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      this.liking = true
      try {
        if (this.article.isLiked) {
          this.article.thumbUp = await this.$api.article.unlikeArticle(this.article.id)
          this.$message.success('取消点赞成功')
          // 从localStorage中移除
          const likedArticles = JSON.parse(localStorage.getItem('likedArticles') || '[]')
          const index = likedArticles.indexOf(this.article.id)
          if (index > -1) {
            likedArticles.splice(index, 1)
            localStorage.setItem('likedArticles', JSON.stringify(likedArticles))
          }
        } else {
          this.article.thumbUp = await this.$api.article.likeArticle(this.article.id)
          this.$message.success('点赞成功')
          // 保存到localStorage
          const likedArticles = JSON.parse(localStorage.getItem('likedArticles') || '[]')
          if (!likedArticles.includes(this.article.id)) {
            likedArticles.push(this.article.id)
            localStorage.setItem('likedArticles', JSON.stringify(likedArticles))
          }
        }
        this.article.isLiked = !this.article.isLiked
      } catch (error) {
        console.error('点赞操作失败:', error)
        this.$message.error('操作失败')
      } finally {
        this.liking = false
      }
    },

    async toggleCollect() {
      const token = localStorage.getItem('token')
      if (!token) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      this.collecting = true
      try {
        if (this.article.isCollected) {
          this.article.collectNumber = await this.$api.article.uncollectArticle(this.article.id)
          this.$message.success('取消收藏成功')
          // 从localStorage中移除
          const collectedArticles = JSON.parse(localStorage.getItem('collectedArticles') || '[]')
          const index = collectedArticles.indexOf(this.article.id)
          if (index > -1) {
            collectedArticles.splice(index, 1)
            localStorage.setItem('collectedArticles', JSON.stringify(collectedArticles))
          }
        } else {
          this.article.collectNumber = await this.$api.article.collectArticle(this.article.id)
          this.$message.success('收藏成功')
          // 保存到localStorage
          const collectedArticles = JSON.parse(localStorage.getItem('collectedArticles') || '[]')
          if (!collectedArticles.includes(this.article.id)) {
            collectedArticles.push(this.article.id)
            localStorage.setItem('collectedArticles', JSON.stringify(collectedArticles))
          }
        }
        this.article.isCollected = !this.article.isCollected
      } catch (error) {
        console.error('收藏操作失败:', error)
        this.$message.error('操作失败')
      } finally {
        this.collecting = false
      }
    },

    formatTime(timestamp) {
      if (!timestamp) return ''

      // 兼容处理：有些后端返回的时间戳是秒，需要乘1000
      // 如果是毫秒级时间戳（13位），直接使用
      const date = new Date(timestamp.toString().length === 10 ? timestamp * 1000 : timestamp)

      const year = date.getFullYear()
      const month = (date.getMonth() + 1).toString().padStart(2, '0')
      const day = date.getDate().toString().padStart(2, '0')
      const hours = date.getHours().toString().padStart(2, '0')
      const minutes = date.getMinutes().toString().padStart(2, '0')
      const seconds = date.getSeconds().toString().padStart(2, '0')

      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    }
  },

  async mounted() {
    await this.loadArticle()
    await this.loadComments()
  }
}
</script>

<style scoped>
/* ================= 基础设置 ================= */
.article-detail-page {
  min-height: 100vh;
  background-color: #f8fafc; /* 冷灰色背景 */
  padding-bottom: 80px;
}

.container {
  max-width: 800px; /* 稍微收窄宽度，提升阅读体验 */
  margin: 0 auto;
  padding: 40px 20px;
}

.main-content-wrapper {
  animation: fadeUp 0.6s ease-out;
}

/* ================= 1. 文章卡片 ================= */
.article-card-clean {
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.03);
  margin-bottom: 30px;
}

/* 头部 */
.article-header {
  margin-bottom: 24px;
}

.meta-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.category-tag {
  background-color: #fff7ed !important;
  border-color: #ffedd5 !important;
  color: #ff6b00 !important;
  font-weight: 500;
}

.publish-time {
  font-size: 13px;
  color: #94a3b8;
}

.article-title {
  font-size: 32px;
  font-weight: 800;
  color: #1e293b;
  line-height: 1.3;
  margin: 0 0 24px 0;
  letter-spacing: -0.5px;
}

.author-bar {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-avatar {
  background: #f1f5f9;
  color: #94a3b8;
  border: 2px solid white;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.toggle-replies-btn {
  margin-top: 8px;
  padding-left: 10px;
}

.toggle-replies-btn .el-button {
  color: #94a3b8; /* 使用更淡的颜色，不抢视觉 */
  font-size: 13px;
}

.toggle-replies-btn .el-button:hover {
  color: #ff6b00; /* 主色调 */
}

.author-info {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-size: 15px;
  font-weight: 600;
  color: #334155;
}

.author-desc {
  font-size: 12px;
  color: #94a3b8;
}

.divider {
  height: 1px;
  background: #f1f5f9;
  margin: 24px 0;
}

/* 正文 Typography (排版) */
.article-body {
  font-size: 16px;
  line-height: 1.8;
  color: #334155;
  margin-bottom: 40px;
}

/* 深度选择器，控制 v-html 内部样式 */
.article-body >>> p {
  margin-bottom: 20px;
}

.article-body >>> h2 {
  font-size: 24px;
  color: #1e293b;
  margin: 32px 0 16px;
  font-weight: 700;
}

.article-body >>> h3 {
  font-size: 20px;
  color: #1e293b;
  margin: 24px 0 12px;
  font-weight: 600;
}

.article-body >>> img {
  max-width: 100%;
  border-radius: 8px;
  margin: 20px 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.article-body >>> blockquote {
  border-left: 4px solid #ff6b00;
  background: #fff7ed;
  padding: 16px 20px;
  margin: 20px 0;
  border-radius: 0 8px 8px 0;
  color: #ea580c;
}

/* 底部操作条 */
.article-actions-bar {
  display: flex;
  justify-content: center;
  padding-top: 20px;
}

.action-btn-group {
  display: flex;
  gap: 20px;
}

.action-btn {
  background: white;
  border: 1px solid #e2e8f0;
  color: #64748b;
  padding: 10px 24px;
  border-radius: 30px;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-btn i {
  font-size: 18px;
}

.action-btn:hover {
  background: #f8fafc;
  color: #334155;
  border-color: #cbd5e1;
}

.action-btn.active {
  background: #fff7ed;
  border-color: #ff6b00;
  color: #ff6b00;
}

/* ================= 2. 评论区 ================= */
.comments-section {
  margin-top: 40px;
}

.section-title h3 {
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 20px;
}

.section-title .count {
  color: #94a3b8;
  font-weight: 400;
  font-size: 16px;
}

/* 输入框卡片 */
.comment-input-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.03);
  margin-bottom: 30px;
}

.input-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.current-user-avatar {
  background: #ff6b00;
}

.input-tip {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

.custom-textarea >>> .el-textarea__inner {
  border: 1px solid #e2e8f0;
  background: #f8fafc;
  border-radius: 8px;
  padding: 12px;
  font-family: inherit;
}

.custom-textarea >>> .el-textarea__inner:focus {
  background: white;
  border-color: #ff6b00;
}

.input-footer {
  margin-top: 15px;
  text-align: right;
}

.submit-btn {
  background-color: #ff6b00;
  border-color: #ff6b00;
  font-weight: 600;
  padding: 10px 24px;
}

.submit-btn:hover {
  background-color: #ff8533;
  border-color: #ff8533;
}

/* 评论列表 */
.comments-list-wrapper {
  background: white;
  border-radius: 12px;
  padding: 10px 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.03);
}

.comment-row {
  display: flex;
  gap: 16px;
  padding: 24px 0;
  border-bottom: 1px solid #f1f5f9;
}

.comment-row:last-child {
  border-bottom: none;
}

.comment-avatar {
  background: #f1f5f9;
  color: #94a3b8;
}

.comment-right {
  flex: 1;
}

.comment-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.username {
  font-size: 15px;
  font-weight: 600;
  color: #334155;
}

.time {
  font-size: 12px;
  color: #94a3b8;
}

.comment-content-text {
  font-size: 15px;
  color: #475569;
  line-height: 1.6;
  margin-bottom: 12px;
}

.tool-btn {
  font-size: 13px;
  color: #64748b;
  cursor: pointer;
  transition: color 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.tool-btn:hover {
  color: #ff6b00;
}

/* 回复输入框 */
.reply-input-box {
  margin-top: 12px;
  background: #f8fafc;
  padding: 12px;
  border-radius: 8px;
}

.reply-actions {
  margin-top: 8px;
  text-align: right;
}

/* 二级评论 */
.sub-comments-list {
  margin-top: 16px;
  background: #f8fafc;
  border-radius: 8px;
  padding: 16px;
}

.sub-comment-item {
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px dashed #e2e8f0;
}

.sub-comment-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.sub-header {
  margin-bottom: 4px;
}

.sub-user {
  font-size: 13px;
  font-weight: 600;
  color: #334155;
  margin-right: 8px;
}

.sub-time {
  font-size: 12px;
  color: #94a3b8;
}

.sub-content {
  font-size: 14px;
  color: #475569;
  line-height: 1.5;
}

.empty-comments-state {
  text-align: center;
  padding: 60px 0;
}

.empty-comments-state img {
  width: 64px;
  opacity: 0.5;
  margin-bottom: 16px;
}

.empty-comments-state p {
  color: #94a3b8;
  font-size: 14px;
}

@keyframes fadeUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>