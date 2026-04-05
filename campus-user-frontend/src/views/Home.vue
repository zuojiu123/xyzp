<template>
  <div class="home-container">
    <!-- 1. 顶部 Hero Banner 区域 -->
    <div class="hero-section">
      <el-carousel height="500px" class="main-carousel" :autoplay="true" :interval="5000" arrow="hover">
        <el-carousel-item v-for="item in banners" :key="item.id">
          <!-- 动态绑定背景图 -->
          <div class="hero-slide" :style="{backgroundImage: `url(${item.image})`}">
            <div class="hero-overlay"></div>
            <div class="hero-content container">
              <div class="hero-text-box">
                <div class="brand-badge">
                  <i class="el-icon-trophy"></i> 职业发展的最佳起点
                </div>
                <h1 class="hero-title">{{ item.title }}</h1>
                <p class="hero-desc">{{ item.description }}</p>

                <div class="hero-actions">
                  <el-button type="primary" class="btn-glow" @click="$router.push('/jobs')">
                    <i class="el-icon-search"></i> 立即求职
                  </el-button>
                  <el-button class="btn-glass" @click="$router.push('/companies')">
                    <i class="el-icon-office-building"></i> 浏览企业
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>

      <!-- 悬浮统计栏 -->
      <div class="stats-floating-bar container">
        <el-row type="flex" justify="space-between" class="stats-row" :gutter="20">
          <el-col :span="6" :xs="12" v-for="(stat, index) in statsWithIcons" :key="stat.title" class="stat-col">
            <div class="stat-card">
              <div :class="['stat-icon-wrapper', `bg-color-${index}`]">
                <i :class="stat.icon"></i>
              </div>
              <div class="stat-info">
                <div class="stat-num">{{ stat.number }}</div>
                <div class="stat-label">{{ stat.title }}</div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 2. 主内容区域 -->
    <div class="main-wrapper">

      <!-- === 热门职位 (高端样式版) === -->
      <div class="section-block">
        <div class="container">
          <div class="section-header">
            <div class="header-left">
              <h2 class="section-title">热门职位 <span class="highlight-dot">.</span></h2>
              <p class="section-subtitle">最受求职者关注的精选机会</p>
            </div>
            <el-button type="text" class="more-btn" @click="$router.push('/jobs')">查看更多 <i class="el-icon-right"></i></el-button>
          </div>

          <el-row :gutter="24">
            <el-col :xs="24" :sm="12" :md="8" v-for="(job, index) in hotJobs.slice(0, 6)" :key="job.id">
              <div class="job-card-premium" @click="viewJob(job.id)" :style="{animationDelay: index * 0.05 + 's'}">
                <div class="premium-badge">
                  <i class="el-icon-s-fire"></i> HOT
                </div>

                <div class="card-main">
                  <div class="card-header-row">
                    <div class="company-logo-box">
                      {{ job.companyModel ? job.companyModel.name.substring(0,1) : '聘' }}
                    </div>
                    <div class="job-salary-text">{{ job.minSalary }}-{{ job.maxSalary }}K</div>
                  </div>

                  <h3 class="job-title-text">{{ job.title }}</h3>

                  <div class="company-info-row">
                    <span class="co-name">{{ job.companyModel ? job.companyModel.name : '未知公司' }}</span>
                    <el-tag size="mini" type="info" effect="plain" class="scale-tag">
                      {{ (job.companyModel && (job.companyModel.nature || job.companyModel.category)) || '企业招聘' }}
                    </el-tag>
                  </div>

                  <div class="tags-container">
                    <span class="tag-pill" v-if="job.position"><i class="el-icon-location-outline"></i> {{ job.position }}</span>
                    <span class="tag-pill" v-if="job.education"><i class="el-icon-reading"></i> {{ job.education }}</span>
                    <span class="tag-pill" v-if="job.experience"><i class="el-icon-suitcase"></i> {{ job.experience }}</span>
                    <span class="tag-pill" v-if="!job.position && !job.education && !job.experience"><i class="el-icon-info"></i> 详情见职位页</span>
                  </div>
                </div>

                <div class="card-bottom-action">
                  <div class="applicant-info">
                    <div class="avatars-stack">
                      <span class="stack-item" style="background: #e0f2fe"></span>
                      <span class="stack-item" style="background: #ffedd5"></span>
                      <span class="stack-item" style="background: #dcfce7"></span>
                    </div>
                    <span class="apply-text">{{ job.applicationCount || 0 }} 人刚刚申请</span>
                  </div>
                  <button type="button" class="action-btn" @click.stop="viewJob(job.id)">查看详情</button>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>

      <!-- === 最新职位 (清爽极简版) === -->
      <div class="section-block bg-light">
        <div class="container">
          <div class="section-header center-align">
            <h2 class="section-title">最新发布</h2>
            <p class="section-subtitle">抢先一步，发现最新鲜的职场机遇</p>
          </div>

          <el-row :gutter="24">
            <el-col :xs="24" :sm="12" :md="8" v-for="(job, index) in latestJobs.slice(0, 6)" :key="job.id">
              <div class="latest-job-card" @click="viewJob(job.id)">
                <!-- 顶部状态栏：NEW 标签 -->
                <div class="card-status">
                  <span class="status-badge">
                    <span class="pulse-dot"></span> New
                  </span>
                  <span class="publish-time-text">{{ formatTime(job.createTime) }}</span>
                </div>

                <!-- 核心信息区 -->
                <div class="card-content">
                  <div class="job-main-info">
                    <h3 class="job-title">{{ job.title }}</h3>
                    <span class="salary-text">{{ job.minSalary }}-{{ job.maxSalary }}K</span>
                  </div>

                  <!-- 公司信息 -->
                  <div class="company-row-clean">
                    <div class="mini-logo">
                      {{ job.companyModel ? job.companyModel.name.substring(0,1) : '企' }}
                    </div>
                    <span class="company-name">{{ job.companyModel ? job.companyModel.name : '未知公司' }}</span>
                  </div>

                  <!-- 底部标签与箭头 -->
                  <div class="bottom-tags">
                    <div class="tag-item" v-if="job.position">{{ job.position }}</div>
                    <div class="tag-item" v-if="job.education">{{ job.education }}</div>
                    <div class="tag-item" v-if="!job.position && !job.education">校招</div>
                    <div class="action-arrow">
                      <i class="el-icon-right"></i>
                    </div>
                  </div>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>

      <!-- === 明星企业 (全新品牌卡片版) === -->
      <div class="section-block">
        <div class="container">
          <div class="section-header">
            <h2 class="section-title">明星企业 <span class="highlight-dot">.</span></h2>
            <el-button type="text" class="more-btn" @click="$router.push('/companies')">全部企业 <i class="el-icon-right"></i></el-button>
          </div>
          <el-row :gutter="24">
            <el-col :xs="24" :sm="12" :md="6" v-for="(company, index) in hotCompanies.slice(0, 8)" :key="company.id">
              <div class="company-card-premium" @click="viewCompany(company.id)">
                <div class="card-overlay"></div>
                <!-- 1. 动态渐变背景 Header -->
                <div class="co-card-header" :class="`gradient-bg-${index % 4}`"></div>

                <div class="co-card-body">
                  <!-- 2. 悬浮 Logo -->
                  <div class="co-logo-wrapper">
                    <div class="co-logo-text">{{ company.name.substring(0,1) }}</div>
                  </div>

                  <!-- 3. 公司信息 -->
                  <h4 class="co-title">{{ company.name }}</h4>

                  <div class="co-tags-row">
                    <span class="co-tag-pill">{{ company.category || '优质企业' }}</span>
                    <span class="co-tag-pill">{{ formatCompanySize(company.number) }}</span>
                  </div>

                  <p class="co-desc-text">{{ company.description || '暂无详细介绍，点击查看更多信息...' }}</p>

                  <!-- 4. 底部按钮 -->
                  <button type="button" class="co-visit-btn" @click.stop="viewCompany(company.id)">
                    查看热招职位
                    <i class="el-icon-arrow-right"></i>
                  </button>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>

      <!-- === 求职资讯 === -->
      <div class="section-block bg-light" v-if="homeArticles.length">
        <div class="container">
          <div class="section-header">
            <div class="header-left">
              <h2 class="section-title">求职资讯 <span class="highlight-dot">.</span></h2>
              <p class="section-subtitle">面试技巧与求职干货，助力拿 Offer</p>
            </div>
            <el-button type="text" class="more-btn" @click="$router.push('/articles')">更多攻略 <i class="el-icon-right"></i></el-button>
          </div>
          <el-row :gutter="24">
            <el-col :xs="24" :sm="12" :md="8" v-for="article in homeArticles" :key="article.id">
              <div class="article-snippet-card" @click="goArticle(article.id)">
                <div class="snippet-meta">
                  <span class="snippet-type" v-if="article.type">{{ articleTypeLabel(article.type) }}</span>
                  <span class="snippet-time">{{ formatArticleTime(article.createTime) }}</span>
                </div>
                <h3 class="snippet-title">{{ article.title }}</h3>
                <p class="snippet-excerpt">{{ excerpt(article.content) }}</p>
                <div class="snippet-foot">
                  <span><i class="el-icon-view"></i> {{ article.viewCount || 0 }}</span>
                  <span class="snippet-more">阅读全文 <i class="el-icon-right"></i></span>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
import { articleTypeLabel } from '@/utils/articleDisplay'

export default {
  name: 'Home',
  data() {
    return {
      // 模拟 Banner
      banners: [
        {
          id: 1,
          title: '探索你的无限可能',
          description: '连接顶尖企业与优秀人才，让职业梦想触手可及',
          image: 'https://images.unsplash.com/photo-1486312338219-ce68d2c6f44d?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80'
        },
        {
          id: 2,
          title: '2026 校园招聘季',
          description: '面向应届毕业生的专属通道，开启职场第一步',
          image: 'https://images.unsplash.com/photo-1522071820081-009f0129c71c?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80'
        }
      ],
      stats: [
        { title: '活跃人才', number: '15,000+' },
        { title: '优质企业', number: '800+' },
        { title: '在线职位', number: '3,200+' },
        { title: '成功匹配', number: '98%' }
      ],
      hotJobs: [],
      latestJobs: [],
      hotCompanies: [],
      homeArticles: []
    }
  },
  computed: {
    statsWithIcons() {
      const icons = ['el-icon-user-solid', 'el-icon-s-cooperation', 'el-icon-s-claim', 'el-icon-s-check'];
      return this.stats.map((item, index) => ({
        ...item,
        icon: icons[index] || 'el-icon-data-line'
      }));
    }
  },
  methods: {
    // 模拟 API 调用与容错处理
    async loadStats() {
      if (!this.$api || !this.$api.statistics) return;
      try {
        const statsData = await this.$api.statistics.getDashboardStats();
        if(statsData) {
          this.stats = [
            { title: '活跃人才', number: statsData.userCount || '24k+' },
            { title: '优质企业', number: statsData.companyCount || '1.2k+' },
            { title: '在线职位', number: statsData.jobCount || '8.5k+' },
            { title: '成功入职', number: statsData.applicationCount || '12k+' }
          ];
        }
      } catch (error) { console.log("使用默认统计数据"); }
    },

    async loadHotJobs() {
      const mockJobs = Array(6).fill(0).map((_, i) => ({
        id: i + 100,
        title: '高级前端开发工程师 ' + (i+1),
        minSalary: 15,
        maxSalary: 25,
        position: '上海·徐汇',
        applicationCount: 45 + i,
        companyModel: { name: '未来科技创新有限公司' }
      }));
      if (this.$api && this.$api.employment) {
        try {
          const response = await this.$api.employment.getJobList(1, 20, { showAll: true, sortBy: 'applicationCount' });
          this.hotJobs = (response.list && response.list.length > 0) ? response.list.slice(0, 6) : mockJobs;
        } catch(e) { this.hotJobs = mockJobs; }
      } else { this.hotJobs = mockJobs; }
    },

    async loadLatestJobs() {
      const mockJobs = Array(6).fill(0).map((_, i) => ({
        id: i + 200,
        title: 'UI/UX 设计师 ' + (i+1),
        minSalary: 12,
        maxSalary: 20,
        position: '杭州·西湖',
        createTime: new Date().toISOString(),
        companyModel: { name: '创意设计工坊' }
      }));
      if (this.$api && this.$api.employment) {
        try {
          const response = await this.$api.employment.getJobList(1, 20, { showAll: true, sortBy: 'createTime' });
          this.latestJobs = (response.list && response.list.length > 0) ? response.list.slice(0, 6) : mockJobs;
        } catch(e) { this.latestJobs = mockJobs; }
      } else { this.latestJobs = mockJobs; }
    },

    articleTypeLabel,

    formatCompanySize(n) {
      if (n == null || n === '') return '规模待定'
      if (typeof n === 'number') return `${n} 人规模`
      return String(n).includes('人') ? n : `${n} 人`
    },

    excerpt(content) {
      if (!content) return '点击查看全文…'
      const text = String(content).replace(/<[^>]+>/g, '').trim()
      return text.length > 72 ? text.slice(0, 72) + '…' : text
    },

    formatArticleTime(t) {
      if (!t) return ''
      const ts = String(t).length === 10 ? t * 1000 : t
      const d = new Date(ts)
      if (Number.isNaN(d.getTime())) return ''
      return `${d.getMonth() + 1}/${d.getDate()}`
    },

    goArticle(id) {
      this.$router.push(`/article/${id}`)
    },

    async loadHomeArticles() {
      if (!this.$api || !this.$api.article) return
      try {
        const res = await this.$api.article.getArticleList(1, 6, {})
        this.homeArticles = (res.list || []).slice(0, 3)
      } catch (e) {
        this.homeArticles = []
      }
    },

    async loadHotCompanies() {
      const mockCompanies = Array(8).fill(0).map((_, i) => ({
        id: i + 300,
        name: i % 2 === 0 ? '字节跳动' : '大疆创新',
        number: '2000+',
        description: '这是一家全球领先的科技公司，致力于将数字世界带入每个人、每个家庭、每个组织。'
      }));
      if (this.$api && this.$api.company) {
        try {
          const response = await this.$api.company.getCompanyList(1, 8, { showAll: true });
          this.hotCompanies = (response.list && response.list.length > 0) ? response.list.slice(0, 8) : mockCompanies;
        } catch(e) { this.hotCompanies = mockCompanies; }
      } else { this.hotCompanies = mockCompanies; }
    },

    formatTime(time) {
      if (!time) return '刚刚'
      const now = new Date()
      const publishTime = new Date(time)
      const diff = now - publishTime
      const days = Math.floor(diff / (1000 * 60 * 60 * 24))
      if (days > 0) return `${days}天前`
      const hours = Math.floor(diff / (1000 * 60 * 60))
      if (hours > 0) return `${hours}小时前`
      return '刚刚'
    },
    viewJob(id) { this.$router.push(`/job/${id}`) },
    viewCompany(id) { this.$router.push(`/company/${id}`) }
  },
  mounted() {
    Promise.all([
      this.loadStats(),
      this.loadHotJobs(),
      this.loadLatestJobs(),
      this.loadHotCompanies(),
      this.loadHomeArticles()
    ]).catch(() => {})
  }
}
</script>

<style scoped>
/* ================= 全局基础设置 ================= */
.home-container {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f8fafc;
  min-height: 100vh;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* ================= 1. Hero Banner ================= */
.hero-section {
  position: relative;
  background: white;
  margin-bottom: 80px;
}

.hero-slide {
  height: 100%;
  background-size: cover;
  background-position: center;
  position: relative;
  display: flex;
  align-items: center;
}

.hero-overlay {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: linear-gradient(90deg, rgba(15, 23, 42, 0.9) 0%, rgba(15, 23, 42, 0.6) 50%, rgba(15, 23, 42, 0.2) 100%);
  z-index: 1;
}

.hero-content {
  position: relative;
  z-index: 3;
  width: 100%;
}

.hero-text-box {
  max-width: 600px;
  color: white;
  animation: fadeUp 0.8s ease-out;
}

.brand-badge {
  display: inline-block;
  background: rgba(255, 107, 0, 0.2);
  border: 1px solid rgba(255, 107, 0, 0.5);
  color: #ff9f43;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 20px;
  backdrop-filter: blur(4px);
}

.hero-title {
  font-size: 48px;
  font-weight: 800;
  line-height: 1.2;
  margin-bottom: 20px;
  text-shadow: 0 2px 10px rgba(0,0,0,0.3);
}

.hero-desc {
  font-size: 18px;
  color: rgba(255,255,255,0.9);
  margin-bottom: 40px;
  line-height: 1.6;
}

.hero-actions { display: flex; gap: 20px; }

.btn-glow {
  background: #ff6b00 !important;
  border-color: #ff6b00 !important;
  box-shadow: 0 0 20px rgba(255, 107, 0, 0.5);
  font-weight: 600;
  padding: 12px 30px;
  height: auto;
  font-size: 16px;
  transition: transform 0.3s;
}
.btn-glow:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 25px rgba(255, 107, 0, 0.7);
}

.btn-glass {
  background: rgba(255,255,255,0.15) !important;
  border: 1px solid rgba(255,255,255,0.3) !important;
  color: white !important;
  padding: 12px 30px;
  height: auto;
  font-size: 16px;
  backdrop-filter: blur(5px);
}
.btn-glass:hover { background: rgba(255,255,255,0.25) !important; }

/* ================= 悬浮统计栏 ================= */
.stats-floating-bar {
  position: relative;
  z-index: 10;
  margin-top: -60px;
  padding-bottom: 20px;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  transition: all 0.3s;
  height: 80px;
}
.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
}

.stat-icon-wrapper {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: 15px;
  flex-shrink: 0;
}

.bg-color-0 { background: #e0f2fe; color: #0284c7; }
.bg-color-1 { background: #dcfce7; color: #16a34a; }
.bg-color-2 { background: #fff7ed; color: #ea580c; }
.bg-color-3 { background: #f3e8ff; color: #9333ea; }

.stat-num {
  font-size: 20px;
  font-weight: 700;
  color: #0f172a;
  line-height: 1;
  margin-bottom: 5px;
}
.stat-label { font-size: 13px; color: #64748b; }

/* ================= 通用区块布局 ================= */
.section-block { padding: 60px 0; }
.section-block.bg-light { background: white; }

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 40px;
}
.section-header.center-align {
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.section-title {
  font-size: 32px;
  font-weight: 800;
  color: #0f172a;
  position: relative;
  margin: 0 0 10px 0;
}
.highlight-dot { color: #ff6b00; }
.section-subtitle { color: #64748b; font-size: 16px; margin: 0; }
.more-btn { font-size: 16px; color: #64748b; }
.more-btn:hover { color: #ff6b00; }

/* ================= 2. 热门职位 (Premium) ================= */
.job-card-premium {
  background: #ffffff;
  border-radius: 16px;
  position: relative;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  border: 1px solid #f0f4f8;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  height: 280px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  overflow: hidden;
  cursor: pointer;
  margin-bottom: 24px;
}

.job-card-premium:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 24px rgba(255, 107, 0, 0.15);
  border-color: rgba(255, 107, 0, 0.3);
}

.premium-badge {
  position: absolute;
  top: 0; right: 0;
  background: linear-gradient(135deg, #ff8736 0%, #ff521c 100%);
  color: white;
  font-size: 12px;
  font-weight: 800;
  padding: 4px 12px;
  border-bottom-left-radius: 12px;
  border-top-right-radius: 16px;
  box-shadow: -2px 2px 6px rgba(255, 82, 28, 0.25);
  z-index: 2;
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-main { padding: 24px 24px 10px; }

.card-header-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.company-logo-box {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #fff7ed 0%, #ffedd5 100%);
  color: #ff6b00;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.5), 0 4px 10px rgba(255, 168, 108, 0.1);
}

.job-salary-text {
  font-size: 20px;
  font-weight: 900;
  background: linear-gradient(90deg, #ff6b00, #ff3d00);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  color: #ff6b00;
  letter-spacing: -0.5px;
  margin-top: 4px;
}

.job-title-text {
  font-size: 18px;
  color: #1f2937;
  font-weight: 700;
  margin: 0 0 8px 0;
  line-height: 1.4;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.company-info-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.co-name { color: #4b5563; font-size: 14px; font-weight: 500; }
.scale-tag { transform: scale(0.9); transform-origin: left center; }
.tags-container { display: flex; flex-wrap: wrap; gap: 8px; }

.tag-pill {
  background: #f8fafc;
  color: #64748b;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 12px;
  border: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-bottom-action {
  padding: 16px 24px;
  background: linear-gradient(to bottom, rgba(255,255,255,0) 0%, #fdfdfd 100%);
  border-top: 1px solid #f8fafc;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.avatars-stack { display: flex; margin-right: 8px; }
.stack-item {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 2px solid white;
  margin-left: -8px;
  background-color: #ddd;
}
.stack-item:first-child { margin-left: 0; }

.applicant-info { display: flex; align-items: center; }
.apply-text { font-size: 12px; color: #94a3b8; }

.action-btn {
  background: #fff0e6;
  color: #ff6b00;
  border: none;
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.job-card-premium:hover .action-btn {
  background: #ff6b00;
  color: white;
  box-shadow: 0 4px 12px rgba(255, 107, 0, 0.3);
}

/* ================= 3. 最新职位 (Clean) ================= */
.latest-job-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #eef2f6;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.latest-job-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 25px rgba(16, 185, 129, 0.1);
  border-color: #d1fae5;
}

.latest-job-card::before {
  content: '';
  position: absolute;
  top: 0; left: 0; width: 100%; height: 3px;
  background: #10b981;
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.3s ease;
}

.latest-job-card:hover::before { transform: scaleX(1); }

.card-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.status-badge {
  background: #ecfdf5;
  color: #059669;
  font-size: 12px;
  font-weight: 700;
  padding: 4px 10px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.pulse-dot {
  width: 6px;
  height: 6px;
  background-color: #10b981;
  border-radius: 50%;
  box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.7);
  animation: pulse-green 2s infinite;
}

.publish-time-text { font-size: 12px; color: #94a3b8; font-family: monospace; }

.job-main-info {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  gap: 10px;
}

.job-title {
  font-size: 16px;
  font-weight: 700;
  color: #334155;
  margin: 0;
  line-height: 1.4;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.salary-text { font-size: 16px; font-weight: 800; color: #10b981; white-space: nowrap; }

.company-row-clean {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.mini-logo {
  width: 24px;
  height: 24px;
  background: #f1f5f9;
  border-radius: 6px;
  color: #64748b;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 8px;
  font-weight: bold;
}

.company-name { font-size: 13px; color: #64748b; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

.bottom-tags { display: flex; align-items: center; margin-top: auto; }
.tag-item { font-size: 12px; color: #64748b; background: #f8fafc; padding: 2px 8px; border-radius: 4px; margin-right: 8px; }

.action-arrow {
  margin-left: auto;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #cbd5e1;
  transition: all 0.3s;
}

.latest-job-card:hover .action-arrow { background: #10b981; color: white; transform: translateX(4px); }

/* ================= 4. 明星企业 (New Premium Brand) ================= */
.company-card-premium {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
  box-shadow: 0 4px 10px rgba(0,0,0,0.04);
  transition: all 0.3s ease;
  height: 260px;
  margin-bottom: 24px;
  display: flex;
  flex-direction: column;
}

.company-card-premium:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0,0,0,0.1);
  cursor: pointer;
}

.card-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
  pointer-events: none;
}

.co-visit-btn {
  position: relative;
  z-index: 2;
}

.co-card-header {
  height: 80px;
  width: 100%;
  position: relative;
  transition: transform 0.5s ease;
}

.company-card-premium:hover .co-card-header {
  transform: scale(1.05); /* 细微放大背景 */
}

/* 预设几种渐变背景，让卡片不单调 */
.gradient-bg-0 { background: linear-gradient(135deg, #a5b4fc, #6366f1); }
.gradient-bg-1 { background: linear-gradient(135deg, #fcd34d, #f59e0b); }
.gradient-bg-2 { background: linear-gradient(135deg, #6ee7b7, #10b981); }
.gradient-bg-3 { background: linear-gradient(135deg, #f9a8d4, #ec4899); }

.co-card-body {
  padding: 0 20px 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
}

.co-logo-wrapper {
  width: 60px;
  height: 60px;
  background: white;
  border-radius: 12px;
  margin-top: -30px; /* 负边距让它悬浮在Header上 */
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 10px rgba(0,0,0,0.08);
  position: relative;
  z-index: 2;
  margin-bottom: 12px;
}

.co-logo-text {
  font-size: 24px;
  font-weight: bold;
  color: #1e293b;
}

.co-title {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 8px 0;
  text-align: center;
}

.co-tags-row {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.co-tag-pill {
  font-size: 12px;
  background: #f1f5f9;
  color: #64748b;
  padding: 2px 8px;
  border-radius: 4px;
}

.co-desc-text {
  font-size: 13px;
  color: #64748b;
  text-align: center;
  line-height: 1.5;
  margin: 0 0 15px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  height: 40px;
}

.co-visit-btn {
  margin-top: auto; /* 推到底部 */
  width: 100%;
  padding: 10px;
  border: 1px solid #e2e8f0;
  background: transparent;
  color: #64748b;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 13px;
}

.company-card-premium:hover .co-visit-btn {
  background: #f8fafc;
  color: #334155;
  border-color: #cbd5e1;
}

/* ================= 动画定义 ================= */
@keyframes fadeUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes pulse-green {
  0% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.7); }
  70% { transform: scale(1); box-shadow: 0 0 0 6px rgba(16, 185, 129, 0); }
  100% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(16, 185, 129, 0); }
}

.article-snippet-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #eef2f6;
  cursor: pointer;
  transition: all 0.25s ease;
  margin-bottom: 20px;
  height: 200px;
  display: flex;
  flex-direction: column;
}
.article-snippet-card:hover {
  border-color: rgba(255, 107, 0, 0.35);
  box-shadow: 0 12px 24px rgba(255, 107, 0, 0.08);
  transform: translateY(-3px);
}
.snippet-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 12px;
  color: #94a3b8;
}
.snippet-type {
  background: #fff7ed;
  color: #ea580c;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 600;
}
.snippet-title {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.snippet-excerpt {
  margin: 0;
  flex: 1;
  font-size: 13px;
  color: #64748b;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.snippet-foot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  font-size: 12px;
  color: #94a3b8;
}
.snippet-more {
  color: #ff6b00;
  font-weight: 600;
}

@media (max-width: 768px) {
  .hero-title { font-size: 32px; }
  .stats-floating-bar { margin-top: 0; }
  .stat-col { margin-bottom: 15px; }
  .section-header { flex-direction: column; align-items: flex-start; }
  .more-btn { margin-top: 10px; }
  .job-card-premium, .company-card-premium { height: auto; padding-bottom: 20px;}
  .co-card-header { height: 60px; }
  .article-snippet-card { height: auto; min-height: 180px; }
}
</style>