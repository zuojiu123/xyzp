<template>
  <div class="articles-page">
    <div class="container">

      <!-- 1. 页面头部 -->
      <div class="page-header">
        <h1 class="page-title">求职攻略</h1>
        <p class="page-subtitle">不仅是职位，更是成长的方向</p>
      </div>

      <!-- 2. 分类筛选 Tab (自定义样式) -->
      <div class="tabs-container">
        <div
            v-for="tab in tabs"
            :key="tab.name"
            class="custom-tab"
            :class="{ active: activeTab === tab.name }"
            @click="handleTabClick(tab)">
          {{ tab.label }}
        </div>
      </div>

      <!-- 3. 文章列表 -->
      <div class="articles-list" v-loading="loading">
        <template v-if="articleList.length > 0">
          <div
              v-for="(article, index) in articleList"
              :key="article.id"
              class="article-card"
              @click="viewArticle(article.id)"
              :style="{animationDelay: index * 0.05 + 's'}">

            <!-- 文章封面/图标区 -->
            <div class="article-cover" :class="getCoverClass(article.type)">
              <div class="cover-icon-wrapper">
                <i :class="getArticleIcon(article.type)"></i>
              </div>
            </div>

            <!-- 文章内容区 -->
            <div class="article-content">
              <div class="content-top">
                <div class="title-row">
                  <span class="category-tag" v-if="article.type">{{ article.type }}</span>
                  <h3 class="article-title">{{ article.title }}</h3>
                </div>
                <p class="article-summary">{{ getArticleSummary(article.content) }}</p>
              </div>

              <div class="content-bottom">
                <div class="author-info">
                  <el-avatar :size="24" icon="el-icon-user-solid" class="author-avatar"></el-avatar>
                  <span class="author-name">{{ article.userName || '匿名用户' }}</span>
                  <span class="publish-time">· {{ formatTime(article.createTime) }}</span>
                </div>

                <div class="stats-info">
                  <span class="stat-item"><i class="el-icon-view"></i> {{ article.viewCount || 0 }}</span>
                  <span class="stat-item"><i class="el-icon-star-off"></i> {{ article.collectNumber || 0 }}</span>
                  <span class="stat-item"><i class="el-icon-thumb"></i> {{ article.thumbUp || 0 }}</span>
                </div>
              </div>
            </div>
          </div>
        </template>

        <!-- 空状态 -->
        <div v-else-if="!loading" class="empty-state">
          <el-empty description="暂无相关文章" :image-size="200"></el-empty>
        </div>
      </div>

      <!-- 4. 分页 -->
      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
            background
            @current-change="handlePageChange"
            :current-page="currentPage"
            :page-size="pageSize"
            layout="prev, pager, next, jumper"
            :total="total">
        </el-pagination>
      </div>

    </div>
  </div>
</template>

<script>
export default {
  name: 'Articles',
  data() {
    return {
      activeTab: 'all',
      tabs: [
        { label: '全部推荐', name: 'all' },
        { label: '经验分享', name: 'experience' },
        { label: '求职心得', name: 'JobHuntingTips' },
        { label: '面试技巧', name: 'InterviewSkills' }
      ],
      articleList: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0
    }
  },
  methods: {
    async loadArticles() {
      this.loading = true
      try {
        const params = {}
        if (this.activeTab !== 'all') {
          params.type = this.activeTab
        }
        const response = await this.$api.article.getArticleList(this.currentPage, this.pageSize, params)
        this.articleList = response.list || []
        this.total = response.total || 0
      } catch (error) {
        console.error('获取文章列表错误:', error)
      } finally {
        this.loading = false
      }
    },
    handleTabClick(tab) {
      this.activeTab = tab.name
      this.currentPage = 1
      this.loadArticles()
    },
    viewArticle(id) {
      this.$router.push(`/article/${id}`)
    },
    handlePageChange(page) {
      this.currentPage = page
      this.loadArticles()
      window.scrollTo({ top: 0, behavior: 'smooth' })
    },
    getArticleSummary(content) {
      if (!content) return '暂无内容简介...'
      const text = content.replace(/<[^>]+>/g, '')
      return text.length > 120 ? text.substring(0, 120) + '...' : text
    },
    getArticleIcon(type) {
      const iconMap = {
        '经验分享': 'el-icon-reading',
        '求职心得': 'el-icon-notebook-2',
        '面试技巧': 'el-icon-chat-line-round'
      }
      return iconMap[type] || 'el-icon-document'
    },
    // 根据类型返回不同的背景色类名
    getCoverClass(type) {
      if (type === '经验分享') return 'bg-blue'
      if (type === '求职心得') return 'bg-purple'
      if (type === '面试技巧') return 'bg-orange'
      return 'bg-default'
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
      return `${date.getMonth() + 1}月${date.getDate()}日`
    }
  },
  mounted() {
    this.loadArticles()
  }
}
</script>

<style scoped>
/* ================= 基础设置 ================= */
.articles-page {
  min-height: 100vh;
  background-color: #f8fafc; /* 统一冷灰色背景 */
  padding-bottom: 60px;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 40px 20px;
}

/* ================= 1. 头部样式 ================= */
.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-title {
  font-size: 32px;
  color: #1e293b;
  margin-bottom: 10px;
  font-weight: 800;
  letter-spacing: -0.5px;
}

.page-subtitle {
  color: #64748b;
  font-size: 16px;
  margin: 0;
}

/* ================= 2. 自定义 Tab ================= */
.tabs-container {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-bottom: 40px;
}

.custom-tab {
  padding: 8px 24px;
  border-radius: 20px;
  background: white;
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid #e2e8f0;
}

.custom-tab:hover {
  color: #ff6b00;
  border-color: #ff6b00;
}

.custom-tab.active {
  background: #ff6b00;
  color: white;
  border-color: #ff6b00;
  box-shadow: 0 4px 6px rgba(255, 107, 0, 0.2);
}

/* ================= 3. 文章列表卡片 ================= */
.articles-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.article-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  gap: 24px;
  cursor: pointer;
  border: 1px solid #f1f5f9;
  box-shadow: 0 2px 4px rgba(0,0,0,0.02);
  transition: all 0.3s ease;
  animation: fadeUp 0.5s ease-out backwards;
}

.article-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0,0,0,0.06);
  border-color: rgba(255, 107, 0, 0.1);
}

/* 左侧封面/图标区 */
.article-cover {
  width: 140px;
  height: 140px;
  border-radius: 12px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

/* 不同类型的背景渐变 */
.bg-default { background: linear-gradient(135deg, #f1f5f9, #e2e8f0); color: #94a3b8; }
.bg-blue { background: linear-gradient(135deg, #e0f2fe, #bfdbfe); color: #3b82f6; }
.bg-purple { background: linear-gradient(135deg, #f3e8ff, #d8b4fe); color: #a855f7; }
.bg-orange { background: linear-gradient(135deg, #ffedd5, #fed7aa); color: #f97316; }

.cover-icon-wrapper {
  font-size: 48px;
  transition: transform 0.3s;
}

.article-card:hover .cover-icon-wrapper {
  transform: scale(1.1) rotate(-5deg);
}

/* 右侧内容区 */
.article-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.category-tag {
  background: #f8fafc;
  color: #64748b;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 600;
  border: 1px solid #e2e8f0;
}

.article-title {
  margin: 0;
  font-size: 18px;
  color: #1e293b;
  font-weight: 700;
  line-height: 1.4;
  transition: color 0.2s;
}

.article-card:hover .article-title {
  color: #ff6b00;
}

.article-summary {
  font-size: 14px;
  color: #64748b;
  line-height: 1.6;
  margin: 0 0 16px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.content-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px dashed #f1f5f9;
  padding-top: 12px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-avatar {
  background: #f1f5f9;
  color: #94a3b8;
}

.author-name {
  font-size: 13px;
  color: #334155;
  font-weight: 500;
}

.publish-time {
  font-size: 12px;
  color: #94a3b8;
}

.stats-info {
  display: flex;
  gap: 16px;
  color: #94a3b8;
  font-size: 13px;
}

.stat-item i {
  margin-right: 4px;
}

/* ================= 4. 分页 ================= */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

.pagination-wrapper >>> .el-pagination.is-background .el-pager li:not(.disabled).active {
  background-color: #ff6b00;
}

.pagination-wrapper >>> .el-pagination.is-background .el-pager li:hover {
  color: #ff6b00;
}

/* 动画 */
@keyframes fadeUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 响应式 */
@media (max-width: 768px) {
  .article-card { flex-direction: column; }
  .article-cover { width: 100%; height: 160px; }
  .content-bottom { flex-direction: column; align-items: flex-start; gap: 10px; }
  .stats-info { width: 100%; justify-content: space-between; }
}
</style>