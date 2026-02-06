<template>
  <div class="articles">
    <div class="container">
      <div class="page-header">
        <h1>求职文章</h1>
        <p class="subtitle">分享求职经验，互相学习成长</p>
      </div>
      
      <div class="filter-section">
        <el-tabs v-model="activeTab" @tab-click="handleTabClick">
          <el-tab-pane label="全部" name="all"></el-tab-pane>
          <el-tab-pane label="经验分享" name="经验分享"></el-tab-pane>
          <el-tab-pane label="求职心得" name="求职心得"></el-tab-pane>
          <el-tab-pane label="面试技巧" name="面试技巧"></el-tab-pane>
        </el-tabs>
      </div>

      <div class="articles-list" v-loading="loading">
        <el-card 
          v-for="(article, index) in articleList" 
          :key="article.id" 
          class="article-item" 
          @click.native="viewArticle(article.id)"
          :style="{animationDelay: index * 0.1 + 's'}">
          <div class="article-content">
            <div class="article-main">
              <div class="article-header">
                <h3 class="article-title">{{ article.title }}</h3>
                <el-tag size="mini" type="warning" v-if="article.type">
                  {{ article.type }}
                </el-tag>
              </div>
              <div class="article-summary">{{ getArticleSummary(article.content) }}</div>
              <div class="article-meta">
                <span class="meta-item">
                  <i class="el-icon-user"></i>
                  {{ article.userName || '匿名用户' }}
                </span>
                <span class="meta-item">
                  <i class="el-icon-time"></i>
                  {{ formatTime(article.createTime) }}
                </span>
                <span class="meta-item stats">
                  <i class="el-icon-star-off"></i> {{ article.collectNumber || 0 }}
                </span>
                <span class="meta-item stats">
                  <i class="el-icon-thumb"></i> {{ article.thumbUp || 0 }}
                </span>
              </div>
            </div>
            <div class="article-cover">
              <i :class="getArticleIcon(article.type)"></i>
            </div>
          </div>
        </el-card>
        
        <div v-if="!loading && articleList.length === 0" class="empty-state">
          <i class="el-icon-document"></i>
          <p>暂无文章</p>
        </div>
      </div>

      <div class="pagination" v-if="total > 0">
        <el-pagination
          @current-change="handlePageChange"
          :current-page="currentPage"
          :page-size="pageSize"
          layout="total, prev, pager, next"
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
        console.log('请求文章列表参数:', params)
        const response = await this.$api.article.getArticleList(this.currentPage, this.pageSize, params)
        this.articleList = response.list || []
        this.total = response.total || 0
        console.log('文章列表:', this.articleList)
      } catch (error) {
        console.error('获取文章列表错误:', error)
        this.$message.error('获取文章列表失败')
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
      if (!content) return '暂无内容'
      const text = content.replace(/<[^>]+>/g, '')
      return text.length > 150 ? text.substring(0, 150) + '...' : text
    },
    getArticleIcon(type) {
      const iconMap = {
        '经验分享': 'el-icon-trophy',
        '求职心得': 'el-icon-edit',
        '面试技巧': 'el-icon-chat-dot-round'
      }
      return iconMap[type] || 'el-icon-document'
    },
    formatTime(timestamp) {
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
  mounted() {
    this.loadArticles()
  }
}
</script>

<style scoped>
.articles {
  min-height: 100vh;
  background: linear-gradient(135deg, #fff5eb 0%, #ffe8d6 100%);
  padding: 30px 0;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h1 {
  color: #ff9800;
  font-size: 32px;
  margin: 0 0 10px 0;
  font-weight: 600;
}

.subtitle {
  color: #666;
  font-size: 16px;
  margin: 0;
}

.filter-section {
  background: white;
  padding: 20px;
  border-radius: 16px;
  margin-bottom: 25px;
  box-shadow: 0 4px 12px rgba(255, 152, 0, 0.08);
}

.filter-section >>> .el-tabs__item {
  font-size: 15px;
  font-weight: 500;
}

.filter-section >>> .el-tabs__item.is-active {
  color: #ff9800;
}

.filter-section >>> .el-tabs__active-bar {
  background-color: #ff9800;
}

.articles-list {
  margin-bottom: 30px;
  min-height: 400px;
}

.article-item {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid #ffe8d6;
  animation: fadeInUp 0.6s ease-out both;
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

.article-item:hover {
  transform: translateY(-8px) scale(1.01);
  box-shadow: 0 20px 40px rgba(255, 152, 0, 0.15);
  border-color: #ff9800;
}

.article-content {
  display: flex;
  justify-content: space-between;
  gap: 20px;
}

.article-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.article-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.article-title {
  margin: 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
  line-height: 1.4;
  flex: 1;
}

.article-summary {
  color: #666;
  line-height: 1.6;
  font-size: 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  font-size: 13px;
  color: #999;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.meta-item i {
  color: #ff9800;
  font-size: 14px;
}

.stats {
  font-weight: 500;
}

.article-cover {
  width: 120px;
  height: 120px;
  flex-shrink: 0;
  border-radius: 12px;
  background: linear-gradient(135deg, #ff9800, #ffb74d);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(255, 152, 0, 0.15);
  transition: all 0.3s;
}

.article-cover i {
  font-size: 48px;
  color: white;
}

.article-item:hover .article-cover {
  transform: scale(1.05) rotate(5deg);
  box-shadow: 0 8px 20px rgba(255, 152, 0, 0.25);
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #999;
}

.empty-state i {
  font-size: 64px;
  color: #ddd;
  margin-bottom: 20px;
}

.empty-state p {
  font-size: 16px;
  margin: 0;
}

.pagination {
  text-align: center;
  padding: 20px 0;
}

@media (max-width: 768px) {
  .article-content {
    flex-direction: column;
  }
  
  .article-cover {
    width: 100%;
    height: 200px;
  }
}
</style>