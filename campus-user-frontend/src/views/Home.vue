<template>
  <div class="home">
    <!-- 轮播图 -->
    <el-carousel height="500px" class="banner" :autoplay="true" :interval="4000" indicator-position="outside">
      <el-carousel-item v-for="item in banners" :key="item.id">
        <div class="banner-item" :style="{backgroundImage: `url(${item.image})`}">
          <div class="banner-overlay"></div>
          <div class="banner-particles"></div>
          <div class="banner-content">
            <div class="banner-icon">
              <div class="logo-container">
                <i class="el-icon-star-on logo-icon"></i>
              </div>
            </div>
            <h2 class="banner-title">{{ item.title }}</h2>
            <p class="banner-desc">{{ item.description }}</p>
            <div class="banner-buttons">
              <el-button type="primary" size="large" @click="$router.push('/jobs')" class="cta-button primary">
                <i class="el-icon-search"></i>
                立即求职
              </el-button>
              <el-button size="large" @click="$router.push('/companies')" class="cta-button secondary">
                <i class="el-icon-office-building"></i>
                浏览企业
              </el-button>
            </div>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 统计数据 -->
    <div class="stats-section">
      <div class="container">
        <el-row :gutter="20">
          <el-col :xs="12" :sm="6" :md="6" :lg="6" :xl="6" v-for="stat in stats" :key="stat.title">
            <div class="stat-item">
              <div class="stat-number">{{ stat.number }}</div>
              <div class="stat-title">{{ stat.title }}</div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 热门职位 -->
    <div class="content-section">
      <div class="container">
        <div class="section-header">
          <h2><i class="el-icon-star-on"></i> 热门职位</h2>
          <p class="section-desc">根据投递简历数量排序</p>
        </div>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8" v-for="(job, index) in hotJobs.slice(0, 6)" :key="job.id">
            <el-card class="job-card hot-job-card" @click.native="viewJob(job.id)" :style="{animationDelay: index * 0.1 + 's'}">
              <div class="job-content">
                <div class="job-header">
                  <i class="el-icon-suitcase" style="color: #ff9800; font-size: 24px; margin-right: 10px;"></i>
                  <div class="job-title">{{ job.title }}</div>
                  <div class="hot-badge">热门</div>
                </div>
                <div class="job-company">
                  <i class="el-icon-office-building" style="margin-right: 5px;"></i>
                  {{ job.companyModel ? job.companyModel.name : '未知公司' }}
                </div>
                <div class="job-salary">
                  <i class="el-icon-coin" style="margin-right: 5px;"></i>
                  {{ job.minSalary }}-{{ job.maxSalary }}K
                </div>
                <div class="job-location">
                  <i class="el-icon-location" style="margin-right: 5px;"></i>
                  {{ job.position }}
                </div>
                <div class="job-stats">
                  <span class="apply-count">
                    <i class="el-icon-user"></i>
                    {{ job.applicationCount || 0 }}人申请
                  </span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 最新职位 -->
    <div class="content-section latest-jobs-section">
      <div class="container">
        <div class="section-header">
          <h2><i class="el-icon-time"></i> 最新职位</h2>
          <p class="section-desc">最新发布的招聘职位</p>
        </div>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8" v-for="(job, index) in latestJobs.slice(0, 6)" :key="job.id">
            <el-card class="job-card latest-job-card" @click.native="viewJob(job.id)" :style="{animationDelay: index * 0.1 + 's'}">
              <div class="job-content">
                <div class="job-header">
                  <i class="el-icon-suitcase" style="color: #67c23a; font-size: 24px; margin-right: 10px;"></i>
                  <div class="job-title">{{ job.title }}</div>
                  <div class="new-badge">新</div>
                </div>
                <div class="job-company">
                  <i class="el-icon-office-building" style="margin-right: 5px;"></i>
                  {{ job.companyModel ? job.companyModel.name : '未知公司' }}
                </div>
                <div class="job-salary">
                  <i class="el-icon-coin" style="margin-right: 5px;"></i>
                  {{ job.minSalary }}-{{ job.maxSalary }}K
                </div>
                <div class="job-location">
                  <i class="el-icon-location" style="margin-right: 5px;"></i>
                  {{ job.position }}
                </div>
                <div class="job-stats">
                  <span class="publish-time">
                    <i class="el-icon-time"></i>
                    {{ formatTime(job.createTime) }}
                  </span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 热门公司 -->
    <div class="content-section">
      <div class="container">
        <h2>热门公司</h2>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="8" v-for="(company, index) in hotCompanies.slice(0, 6)" :key="company.id">
            <el-card class="company-card">
              <div class="company-header">
                <div class="company-logo">
                  <div class="company-icon">
                    <i class="el-icon-office-building"></i>
                  </div>
                </div>
                <div class="company-info">
                  <div class="company-name">
                    <i class="el-icon-star-on" style="color: var(--accent-color); margin-right: 5px;"></i>
                    {{ company.name }}
                  </div>
                  <div class="company-scale">
                    <i class="el-icon-user" style="margin-right: 5px;"></i>
                    {{ company.number || 100 }}人
                  </div>
                </div>
              </div>
              <div class="company-desc">{{ company.description }}</div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Home',
  data() {
    return {
      banners: [
        {
          id: 1,
          title: '找到理想工作',
          description: '连接优秀人才与优质企业',
          image: '../image/banner1.png'
        },
        {
          id: 2,
          title: '校园招聘专场',
          description: '为应届毕业生提供更多机会',
          image: '../image/banner2.png'
        }
      ],
      stats: [],
      hotJobs: [],
      latestJobs: [],
      hotCompanies: []
    }
  },
  methods: {
    async loadStats() {
      try {
        const statsData = await this.$api.statistics.getDashboardStats()
        console.log('统计数据:', statsData)
        this.stats = [
          { title: '注册用户', number: statsData.userCount || '0' },
          { title: '合作企业', number: statsData.companyCount || '0' },
          { title: '职位发布', number: statsData.jobCount || '0' },
          { title: '成功匹配', number: statsData.applicationCount || '0' }
        ]
      } catch (error) {
        // 使用默认数据
        this.stats = [
          { title: '注册用户', number: '1000+' },
          { title: '合作企业', number: '100+' },
          { title: '职位发布', number: '500+' },
          { title: '成功匹配', number: '2000+' }
        ]
      }
    },
    async loadHotJobs() {
      try {
        const response = await this.$api.employment.getJobList(1, 20, { 
          showAll: true, 
          sortBy: 'applicationCount' 
        })
        this.hotJobs = (response.list || []).slice(0, 6)
        console.log('热门职位数据:', this.hotJobs)
      } catch (error) {
        console.error('获取热门职位失败:', error)
      }
    },
    async loadLatestJobs() {
      try {
        const response = await this.$api.employment.getJobList(1, 20, { 
          showAll: true, 
          sortBy: 'createTime' 
        })
        this.latestJobs = (response.list || []).slice(0, 6)
        console.log('最新职位数据:', this.latestJobs)
      } catch (error) {
        console.error('获取最新职位失败:', error)
      }
    },
    formatTime(time) {
      if (!time) return '刚刚发布'
      const now = new Date()
      const publishTime = new Date(time)
      const diff = now - publishTime
      const days = Math.floor(diff / (1000 * 60 * 60 * 24))
      const hours = Math.floor(diff / (1000 * 60 * 60))
      const minutes = Math.floor(diff / (1000 * 60))
      
      if (days > 0) return `${days}天前`
      if (hours > 0) return `${hours}小时前`
      if (minutes > 0) return `${minutes}分钟前`
      return '刚刚发布'
    },
    async loadHotCompanies() {
      try {
        const response = await this.$api.company.getCompanyList(1, 6, { showAll: true })
        this.hotCompanies = (response.list || []).slice(0, 6)
        console.log('热门公司数据:', this.hotCompanies)
      } catch (error) {
        console.error('获取热门公司失败:', error)
      }
    },
    searchJobs() {
      this.$router.push({
        path: '/jobs',
        query: { keyword: this.searchKeyword }
      })
    },
    viewJob(id) {
      this.$router.push(`/job/${id}`)
    },

  },
  mounted() {
    this.loadStats()
    this.loadHotJobs()
    this.loadLatestJobs()
    this.loadHotCompanies()
  }
}
</script>

<style scoped>
.banner {
  margin-bottom: 80px;
  border-radius: 0;
  overflow: hidden;
}

.banner-item {
  height: 500px;
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.banner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 152, 0, 0.8) 0%, rgba(255, 183, 77, 0.6) 50%, rgba(255, 204, 128, 0.4) 100%);
  z-index: 1;
}

.banner-particles {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    radial-gradient(circle at 20% 80%, rgba(255, 224, 178, 0.15) 2px, transparent 2px),
    radial-gradient(circle at 80% 20%, rgba(255, 204, 128, 0.1) 2px, transparent 2px),
    radial-gradient(circle at 40% 40%, rgba(255, 183, 77, 0.08) 1px, transparent 1px);
  background-size: 100px 100px, 150px 150px, 80px 80px;
  animation: float 20s ease-in-out infinite;
  z-index: 1;
}

.banner-content {
  text-align: center;
  color: white;
  z-index: 2;
  position: relative;
  max-width: 800px;
  padding: 0 20px;
}

.banner-title {
  font-size: 56px;
  margin-bottom: 24px;
  font-weight: 700;
  text-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
  animation: slideInDown 1s ease-out;
  background: linear-gradient(45deg, #fff, #fff3e0);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.banner-desc {
  font-size: 22px;
  margin-bottom: 40px;
  opacity: 0.95;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  animation: slideInUp 1s ease-out 0.3s both;
  line-height: 1.6;
}

.banner-icon {
  margin-bottom: 30px;
  animation: slideInDown 1s ease-out 0.1s both;
}

.logo-container {
  width: 80px;
  height: 80px;
  margin: 0 auto;
  background: rgba(255, 224, 178, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 183, 77, 0.3);
  animation: pulse 2s ease-in-out infinite;
}

.logo-icon {
  font-size: 36px;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.banner-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
  flex-wrap: wrap;
  animation: slideInUp 1s ease-out 0.6s both;
}

.cta-button {
  font-size: 16px;
  padding: 16px 32px;
  border-radius: 50px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: 600;
  letter-spacing: 0.5px;
  position: relative;
  overflow: hidden;
}

.cta-button.primary {
  background: linear-gradient(45deg, #ff9800, #ffb74d);
  border: none;
  box-shadow: 0 8px 25px rgba(255, 152, 0, 0.3);
}

.cta-button.secondary {
  background: rgba(255, 255, 255, 0.15);
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
}

.cta-button:hover {
  transform: translateY(-4px) scale(1.05);
}

.cta-button.primary:hover {
  box-shadow: 0 12px 35px rgba(255, 152, 0, 0.4);
  background: linear-gradient(45deg, #ffb74d, #ff9800);
}

.cta-button.secondary:hover {
  background: rgba(255, 255, 255, 0.25);
  border-color: rgba(255, 255, 255, 0.5);
}

.cta-button i {
  margin-right: 8px;
  font-size: 18px;
}

.stats-section {
  background: white;
  padding: 60px 0;
  margin-bottom: 60px;
}

.stat-item {
  text-align: center;
  padding: 20px;
}

.stat-number {
  font-size: 36px;
  font-weight: bold;
  color: #ff9800;
  margin-bottom: 10px;
  animation: pulse 2s infinite;
}

.stat-title {
  font-size: 16px;
  color: #666;
}

.content-section {
  padding: 60px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.container h2 {
  margin-bottom: 30px;
  color: #333;
}

.job-card {
  cursor: pointer;
  margin-bottom: 20px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  height: 220px;
  display: flex;
  flex-direction: column;
}

.job-card .el-card__body {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 20px;
}

.hot-job-card {
  border-left: 4px solid #ff9800;
}

.latest-job-card {
  border-left: 4px solid #67c23a;
}

.job-card:hover {
  transform: translateY(-8px) scale(1.02);
}

.hot-job-card:hover {
  box-shadow: 0 12px 30px rgba(255, 152, 0, 0.2);
}

.latest-job-card:hover {
  box-shadow: 0 12px 30px rgba(103, 194, 58, 0.2);
}

.section-header {
  text-align: center;
  margin-bottom: 40px;
}

.section-header h2 {
  font-size: 28px;
  color: #333;
  margin-bottom: 10px;
}

.section-header h2 i {
  margin-right: 10px;
  color: #ff9800;
}

.section-desc {
  color: #666;
  font-size: 16px;
  margin: 0;
}

.latest-jobs-section {
  background: #f8f9fa;
}

.job-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  position: relative;
}

.hot-badge, .new-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: bold;
  color: white;
}

.hot-badge {
  background: linear-gradient(45deg, #ff6b35, #ff9800);
}

.new-badge {
  background: linear-gradient(45deg, #4caf50, #67c23a);
}

.job-stats {
  margin-top: auto;
  padding-top: 10px;
  border-top: 1px solid #eee;
  font-size: 14px;
  color: #666;
}

.job-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.apply-count, .publish-time {
  display: flex;
  align-items: center;
}

.apply-count i, .publish-time i {
  margin-right: 5px;
  font-size: 14px;
}

.job-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 8px;
}

.job-company {
  color: #666;
  margin-bottom: 8px;
}

.job-salary {
  color: #ff9800;
  font-weight: bold;
  margin-bottom: 8px;
}

.job-location {
  color: #999;
  margin-bottom: 5px;
}

.company-card {
  margin-bottom: 20px;
  border-radius: 12px;
  overflow: hidden;
  height: 140px;
  display: flex;
  flex-direction: column;
}

.company-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.company-info {
  margin-left: 15px;
  flex: 1;
}

.company-icon {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  background: #ff9800;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.company-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
  color: #333;
}

.company-scale {
  font-size: 14px;
  color: #666;
}

.company-desc {
  color: #666;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  flex: 1;
}

/* 动画定义 */
@keyframes slideInDown {
  from {
    opacity: 0;
    transform: translateY(-50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    box-shadow: 0 0 0 0 rgba(255, 152, 0, 0.4);
  }
  50% {
    transform: scale(1.05);
    box-shadow: 0 0 0 10px rgba(255, 152, 0, 0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .banner-title {
    font-size: 36px;
  }
  
  .banner-desc {
    font-size: 18px;
  }
  
  .banner-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .cta-button {
    width: 200px;
  }
  
  .container {
    padding: 0 10px;
  }
  
  .section-header h2 {
    font-size: 24px;
  }
  
  .job-title {
    font-size: 16px;
  }
  
  .company-card {
    height: auto;
    min-height: 120px;
  }
}

@media (max-width: 576px) {
  .job-card {
    margin-bottom: 15px;
  }
  
  .stat-number {
    font-size: 28px;
  }
  
  .stat-title {
    font-size: 14px;
  }
}
</style>