<template>
  <div class="company-detail">
    <div class="container">
      <!-- 公司基本信息 -->
      <div class="company-header">
        <div class="company-logo">
          <div class="logo-wrapper">
            <img v-if="company.companyLogo" :src="company.companyLogo" :alt="company.name" @error="handleLogoError">
            <div v-else class="default-logo">
              <i class="el-icon-office-building"></i>
            </div>
          </div>
        </div>
        <div class="company-info">
          <h1>{{ company.name }}</h1>
          <div class="company-tags">
            <el-tag v-if="company.industry">{{ company.industry }}</el-tag>
            <el-tag type="info">{{ company.number || 100 }}人</el-tag>
            <el-tag type="success" v-if="company.address">{{ company.address }}</el-tag>
          </div>
          <div class="company-stats">
            <div class="stat-item">
              <span class="stat-number">{{ company.jobCount || 0 }}</span>
              <span class="stat-label">在招职位</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{ company.viewCount || 0 }}</span>
              <span class="stat-label">浏览次数</span>
            </div>
          </div>
        </div>
      </div>

      <el-row :gutter="20">
        <!-- 左侧内容 -->
        <el-col :span="16">
          <!-- 公司介绍 -->
          <el-card class="section-card">
            <div slot="header">
              <i class="el-icon-document"></i>
              公司介绍
            </div>
            <div class="company-description">
              {{ company.description || '该公司暂未添加介绍' }}
            </div>
          </el-card>

          <!-- 在招职位 -->
          <el-card class="section-card">
            <div slot="header">
              <i class="el-icon-suitcase"></i>
              在招职位 ({{ jobList.length }})
            </div>
            <div class="jobs-list">
              <div v-for="job in jobList" :key="job.id" class="job-item" @click="viewJob(job.id)">
                <div class="job-main">
                  <h4 class="job-title">{{ job.title }}</h4>
                  <div class="job-info">
                    <span class="job-salary">{{ job.minSalary }}-{{ job.maxSalary }}K</span>
                    <span class="job-location">{{ job.position }}</span>
                    <span class="job-experience">{{ job.experience || '经验不限' }}</span>
                  </div>
                </div>
                <div class="job-time">
                  {{ formatTime(job.createTime) }}
                </div>
              </div>
              <div v-if="jobList.length === 0" class="no-jobs">
                暂无在招职位
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- 右侧信息 -->
        <el-col :span="8">
          <!-- 公司信息 -->
          <el-card class="section-card">
            <div slot="header">
              <i class="el-icon-info"></i>
              公司信息
            </div>
            <div class="company-details">
              <div class="detail-item">
                <span class="detail-label">公司规模：</span>
                <span class="detail-value">{{ company.number || 100 }}人</span>
              </div>
              <div class="detail-item" v-if="company.industry">
                <span class="detail-label">所属行业：</span>
                <span class="detail-value">{{ company.industry }}</span>
              </div>
              <div class="detail-item" v-if="company.address">
                <span class="detail-label">公司地址：</span>
                <span class="detail-value">{{ company.address }}</span>
              </div>
              <div class="detail-item" v-if="company.website">
                <span class="detail-label">公司网站：</span>
                <span class="detail-value">
                  <a :href="company.website" target="_blank">{{ company.website }}</a>
                </span>
              </div>
              <div class="detail-item">
                <span class="detail-label">成立时间：</span>
                <span class="detail-value">{{ company.foundedYear || '未知' }}</span>
              </div>
            </div>
          </el-card>

          <!-- 联系方式 -->
          <el-card class="section-card">
            <div slot="header">
              <i class="el-icon-phone"></i>
              联系方式
            </div>
            <div class="contact-info">
              <div class="contact-item" v-if="company.phone">
                <i class="el-icon-phone"></i>
                <span>{{ company.phone }}</span>
              </div>
              <div class="contact-item" v-if="company.email">
                <i class="el-icon-message"></i>
                <span>{{ company.email }}</span>
              </div>
              <div class="contact-item" v-if="company.address">
                <i class="el-icon-location"></i>
                <span>{{ company.address }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CompanyDetail',
  data() {
    return {
      company: {},
      jobList: [],
      loading: false
    }
  },
  methods: {
    async loadCompanyDetail() {
      this.loading = true
      try {
        const id = this.$route.params.id
        console.log('加载公司详情, ID:', id)
        
        // 如果API不存在，使用模拟数据
        if (!this.$api.company.getCompanyDetail) {
          this.company = {
            id: id,
            name: '示例公司',
            description: '这是一家示例公司，专注于技术创新和人才培养。',
            number: 500,
            industry: 'IT互联网',
            address: '北京市朝阳区',
            jobCount: 15,
            viewCount: 1234
          }
          await this.loadCompanyJobs()
          return
        }
        
        const response = await this.$api.company.getCompanyDetail(id)
        this.company = response || {}
        
        // 加载公司职位
        await this.loadCompanyJobs()
      } catch (error) {
        console.error('获取公司详情失败:', error)
        this.$message.error('获取公司详情失败')
        
        // 使用默认数据
        this.company = {
          id: this.$route.params.id,
          name: '公司名称',
          description: '公司介绍暂未加载',
          number: 100,
          industry: '未知',
          address: '未知',
          jobCount: 0,
          viewCount: 0
        }
      } finally {
        this.loading = false
      }
    },
    async loadCompanyJobs() {
      try {
        const response = await this.$api.employment.getJobList(1, 10, { 
          companyId: this.company.id 
        })
        this.jobList = response.list || []
      } catch (error) {
        console.error('获取公司职位失败:', error)
        // 使用模拟数据
        this.jobList = []
      }
    },
    viewJob(id) {
      this.$router.push(`/job/${id}`)
    },
    handleLogoError(event) {
      event.target.style.display = 'none'
      event.target.parentNode.innerHTML = '<div class="default-logo"><i class="el-icon-office-building"></i></div>'
    },
    formatTime(timestamp) {
      const date = new Date(timestamp)
      const now = new Date()
      const diff = now - date
      const days = Math.floor(diff / (1000 * 60 * 60 * 24))
      
      if (days === 0) return '今天'
      if (days === 1) return '昨天'
      return `${days}天前`
    }
  },
  mounted() {
    this.loadCompanyDetail()
  }
}
</script>

<style scoped>
.company-detail {
  min-height: 100vh;
  background: linear-gradient(135deg, #fff5eb 0%, #ffe8d6 100%);
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.company-header {
  background: white;
  padding: 40px;
  border-radius: 16px;
  margin-bottom: 25px;
  display: flex;
  align-items: flex-start;
  gap: 30px;
  box-shadow: 0 8px 24px rgba(255, 152, 0, 0.1);
  animation: fadeInDown 0.6s ease-out;
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.logo-wrapper {
  width: 120px;
  height: 120px;
  border-radius: 16px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #fff5eb, #ffe8d6);
  box-shadow: 0 8px 20px rgba(255, 152, 0, 0.15);
}

.logo-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.default-logo {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #ff9800, #ffb74d);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 48px;
}

.company-info {
  flex: 1;
}

.company-info h1 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 32px;
  font-weight: 600;
}

.company-tags {
  margin-bottom: 25px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.company-tags .el-tag {
  margin: 0;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
}

.company-stats {
  display: flex;
  gap: 50px;
}

.stat-item {
  text-align: center;
  padding: 15px 25px;
  background: linear-gradient(135deg, #fff5eb, #ffe8d6);
  border-radius: 12px;
  min-width: 100px;
}

.stat-number {
  display: block;
  font-size: 28px;
  font-weight: bold;
  color: #ff9800;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.section-card {
  margin-bottom: 20px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(255, 152, 0, 0.08);
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

.section-card:nth-child(2) {
  animation-delay: 0.1s;
}

.section-card:nth-child(3) {
  animation-delay: 0.2s;
}

.section-card >>> .el-card__header {
  background: linear-gradient(135deg, #fff5eb, #ffe8d6);
  border-bottom: 2px solid #ff9800;
  font-weight: 600;
  color: #333;
  font-size: 16px;
}

.section-card >>> .el-card__header i {
  color: #ff9800;
  margin-right: 8px;
  font-size: 18px;
}

.company-description {
  line-height: 1.8;
  color: #666;
  font-size: 15px;
  padding: 10px 0;
}

.jobs-list {
  max-height: 500px;
  overflow-y: auto;
}

.jobs-list::-webkit-scrollbar {
  width: 6px;
}

.jobs-list::-webkit-scrollbar-thumb {
  background: #ff9800;
  border-radius: 3px;
}

.jobs-list::-webkit-scrollbar-track {
  background: #fff5eb;
}

.job-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 15px;
  border-bottom: 1px solid #fff5eb;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 8px;
  margin-bottom: 5px;
}

.job-item:hover {
  background: linear-gradient(135deg, #fff5eb, #ffe8d6);
  transform: translateX(5px);
  box-shadow: 0 4px 12px rgba(255, 152, 0, 0.1);
}

.job-title {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 16px;
  font-weight: 600;
}

.job-info {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.job-info span {
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.job-salary {
  color: #ff9800;
  font-weight: 600;
  font-size: 15px;
}

.job-location, .job-experience {
  color: #666;
  background: #fff5eb;
  padding: 3px 10px;
  border-radius: 10px;
}

.job-time {
  color: #999;
  font-size: 13px;
  white-space: nowrap;
}

.no-jobs {
  text-align: center;
  color: #999;
  padding: 60px 0;
  font-size: 15px;
}

.company-details {
  line-height: 2.2;
}

.detail-item {
  display: flex;
  margin-bottom: 15px;
  padding: 10px;
  border-radius: 8px;
  transition: background 0.3s;
}

.detail-item:hover {
  background: #fff5eb;
}

.detail-label {
  min-width: 90px;
  color: #666;
  font-weight: 500;
}

.detail-value {
  color: #333;
  flex: 1;
}

.detail-value a {
  color: #ff9800;
  text-decoration: none;
  transition: color 0.3s;
}

.detail-value a:hover {
  color: #f57c00;
  text-decoration: underline;
}

.contact-info {
  line-height: 2.2;
}

.contact-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  color: #666;
  padding: 10px;
  border-radius: 8px;
  transition: all 0.3s;
}

.contact-item:hover {
  background: #fff5eb;
  transform: translateX(5px);
}

.contact-item i {
  margin-right: 12px;
  color: #ff9800;
  font-size: 18px;
  width: 24px;
  text-align: center;
}
</style>