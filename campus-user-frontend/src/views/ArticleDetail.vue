<template>
  <div class="article-detail">
    <div class="container">
      <!-- 文章内容 -->
      <el-card class="article-card">
        <div class="article-header">
          <h1 class="article-title">{{ article.title }}</h1>
          <div class="article-meta">
            <span class="meta-item">
              <i class="el-icon-user"></i>
              {{ article.userName || '匿名用户' }}
            </span>
            <span class="meta-item">
              <i class="el-icon-time"></i>
              {{ formatTime(article.createTime) }}
            </span>
            <el-tag size="small" type="warning" v-if="article.type">
              {{ article.type }}
            </el-tag>
          </div>
        </div>

        <div class="article-content" v-html="article.content"></div>

        <div class="article-actions">
          <el-button 
            :type="article.isLiked ? 'warning' : 'default'" 
            icon="el-icon-thumb" 
            @click="toggleLike"
            :loading="liking">
            点赞 {{ article.thumbUp || 0 }}
          </el-button>
          <el-button 
            :type="article.isCollected ? 'warning' : 'default'"
            icon="el-icon-star-off" 
            @click="toggleCollect"
            :loading="collecting">
            收藏 {{ article.collectNumber || 0 }}
          </el-button>
        </div>
      </el-card>

      <!-- 评论区 -->
      <el-card class="comments-card">
        <div slot="header" class="card-header">
          <i class="el-icon-chat-dot-round"></i>
          <span>评论 ({{ comments.length }})</span>
        </div>

        <!-- 发表评论 -->
        <div class="comment-input">
          <el-input
            type="textarea"
            v-model="newComment"
            :rows="4"
            placeholder="写下你的评论..."
            maxlength="500"
            show-word-limit>
          </el-input>
          <el-button 
            type="warning" 
            @click="submitComment" 
            :loading="commenting"
            style="margin-top: 10px;">
            发表评论
          </el-button>
        </div>

        <!-- 评论列表 -->
        <div class="comments-list">
          <div 
            v-for="comment in comments" 
            :key="comment.id" 
            class="comment-item">
            <div class="comment-avatar">
              <i class="el-icon-user-solid"></i>
            </div>
            <div class="comment-content">
              <div class="comment-header">
                <span class="comment-author">{{ comment.userName || '匿名用户' }}</span>
                <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
              </div>
              <div class="comment-text">{{ comment.content }}</div>
              <div class="comment-actions">
                <el-button 
                  type="text" 
                  size="small" 
                  @click="showReplyInput(comment.id)">
                  <i class="el-icon-chat-line-round"></i> 回复
                </el-button>
              </div>

              <!-- 回复输入框 -->
              <div v-if="replyingTo === comment.id" class="reply-input">
                <el-input
                  type="textarea"
                  v-model="replyContent"
                  :rows="3"
                  placeholder="写下你的回复..."
                  maxlength="300"
                  show-word-limit>
                </el-input>
                <div class="reply-actions">
                  <el-button size="small" @click="cancelReply">取消</el-button>
                  <el-button 
                    type="warning" 
                    size="small" 
                    @click="submitReply(comment.id)"
                    :loading="replying">
                    回复
                  </el-button>
                </div>
              </div>

              <!-- 回复列表 -->
              <div v-if="comment.replies && comment.replies.length > 0" class="replies-list">
                <div 
                  v-for="reply in comment.replies" 
                  :key="reply.id" 
                  class="reply-item">
                  <div class="reply-avatar">
                    <i class="el-icon-user"></i>
                  </div>
                  <div class="reply-content">
                    <div class="reply-header">
                      <span class="reply-author">{{ reply.userName || '匿名用户' }}</span>
                      <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
                    </div>
                    <div class="reply-text">{{ reply.content }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div v-if="comments.length === 0" class="empty-comments">
            <i class="el-icon-chat-dot-round"></i>
            <p>暂无评论，快来发表第一条评论吧~</p>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
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
      collecting: false
    }
  },
  methods: {
    async loadArticle() {
      this.loading = true
      try {
        const id = this.$route.params.id
        const response = await this.$api.article.getArticleById(id)
        this.article = response || {}
        console.log('文章详情:', this.article)
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
        this.comments = response || []
        console.log('评论列表:', this.comments)
      } catch (error) {
        console.error('获取评论失败:', error)
      }
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
        console.error('发表评论失败:', error)
        this.$message.error('发表评论失败')
      } finally {
        this.commenting = false
      }
    },

    showReplyInput(commentId) {
      this.replyingTo = commentId
      this.replyContent = ''
    },

    cancelReply() {
      this.replyingTo = null
      this.replyContent = ''
    },

    async submitReply(commentId) {
      if (!this.replyContent.trim()) {
        this.$message.warning('请输入回复内容')
        return
      }

      this.replying = true
      try {
        await this.$api.replyInfo.createReply({
          commentsId: commentId,
          content: this.replyContent
        })
        this.$message.success('回复成功')
        this.cancelReply()
        await this.loadComments()
      } catch (error) {
        console.error('回复失败:', error)
        this.$message.error('回复失败')
      } finally {
        this.replying = false
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
        // 这里需要实现点赞API
        this.$message.success(this.article.isLiked ? '取消点赞' : '点赞成功')
        this.article.isLiked = !this.article.isLiked
        this.article.thumbUp = this.article.isLiked 
          ? (this.article.thumbUp || 0) + 1 
          : (this.article.thumbUp || 1) - 1
      } catch (error) {
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
        // 这里需要实现收藏API
        this.$message.success(this.article.isCollected ? '取消收藏' : '收藏成功')
        this.article.isCollected = !this.article.isCollected
        this.article.collectNumber = this.article.isCollected 
          ? (this.article.collectNumber || 0) + 1 
          : (this.article.collectNumber || 1) - 1
      } catch (error) {
        this.$message.error('操作失败')
      } finally {
        this.collecting = false
      }
    },

    formatTime(timestamp) {
      if (!timestamp) return ''
      const date = new Date(timestamp)
      const now = new Date()
      const diff = now - date
      const days = Math.floor(diff / (1000 * 60 * 60 * 24))
      
      if (days === 0) return '今天'
      if (days === 1) return '昨天'
      if (days < 7) return `${days}天前`
      return date.toLocaleDateString()
    }
  },

  async mounted() {
    await this.loadArticle()
    await this.loadComments()
  }
}
</script>

<style scoped>
.article-detail {
  min-height: 100vh;
  background: linear-gradient(135deg, #fff5eb 0%, #ffe8d6 100%);
  padding: 30px 0;
}

.container {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 20px;
}

.article-card {
  margin-bottom: 25px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(255, 152, 0, 0.1);
  animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.article-header {
  padding: 30px;
  background: linear-gradient(135deg, #fff5eb, #ffe8d6);
  border-bottom: 2px solid #ff9800;
}

.article-title {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 28px;
  font-weight: 600;
  line-height: 1.4;
}

.article-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  align-items: center;
  font-size: 14px;
  color: #666;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.meta-item i {
  color: #ff9800;
}

.article-content {
  padding: 30px;
  line-height: 1.8;
  font-size: 16px;
  color: #333;
  min-height: 200px;
}

.article-content >>> p {
  margin: 15px 0;
}

.article-content >>> img {
  max-width: 100%;
  border-radius: 8px;
  margin: 20px 0;
}

.article-actions {
  padding: 20px 30px;
  border-top: 1px solid #fff5eb;
  display: flex;
  gap: 15px;
}

.article-actions .el-button {
  border-radius: 20px;
  padding: 10px 24px;
}

.comments-card {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(255, 152, 0, 0.08);
  animation: fadeInUp 0.6s ease-out 0.2s both;
}

.card-header {
  font-weight: 600;
  font-size: 18px;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-header i {
  color: #ff9800;
  font-size: 20px;
}

.comments-card >>> .el-card__header {
  background: linear-gradient(135deg, #fff5eb, #ffe8d6);
  border-bottom: 2px solid #ff9800;
}

.comment-input {
  margin-bottom: 30px;
}

.comments-list {
  margin-top: 20px;
}

.comment-item {
  display: flex;
  gap: 15px;
  padding: 20px 0;
  border-bottom: 1px solid #fff5eb;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ff9800, #ffb74d);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
}

.comment-author {
  font-weight: 600;
  color: #333;
  font-size: 15px;
}

.comment-time {
  font-size: 13px;
  color: #999;
}

.comment-text {
  color: #666;
  line-height: 1.6;
  margin-bottom: 10px;
  font-size: 15px;
}

.comment-actions {
  display: flex;
  gap: 10px;
}

.comment-actions .el-button {
  color: #ff9800;
  padding: 0;
}

.comment-actions .el-button:hover {
  color: #f57c00;
}

.reply-input {
  margin-top: 15px;
  padding: 15px;
  background: #fff5eb;
  border-radius: 12px;
}

.reply-actions {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.replies-list {
  margin-top: 15px;
  padding-left: 20px;
  border-left: 2px solid #ffe8d6;
}

.reply-item {
  display: flex;
  gap: 12px;
  padding: 15px 0;
}

.reply-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ffb74d, #ffc107);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  flex-shrink: 0;
}

.reply-content {
  flex: 1;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.reply-author {
  font-weight: 500;
  color: #333;
  font-size: 14px;
}

.reply-time {
  font-size: 12px;
  color: #999;
}

.reply-text {
  color: #666;
  line-height: 1.5;
  font-size: 14px;
}

.empty-comments {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-comments i {
  font-size: 48px;
  color: #ddd;
  margin-bottom: 15px;
}

.empty-comments p {
  font-size: 15px;
  margin: 0;
}
</style>
