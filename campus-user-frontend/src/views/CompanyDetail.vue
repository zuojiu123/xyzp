<template>
  <div class="company-detail-page">
    <!-- 1. 企业封面背景图 (Header Banner) -->
    <div class="company-banner">
      <div class="banner-overlay"></div>
    </div>

    <div class="container">

      <!-- 2. 企业头部信息卡片 -->
      <div class="company-profile-card">
        <div class="profile-main">
          <!-- Logo 悬浮 -->
          <div class="logo-wrapper">
            <img v-if="company.companyLogo" :src="company.companyLogo" :alt="company.name" @error="handleLogoError" class="real-logo">
            <div v-else class="text-logo">
              {{ company.name ? company.name.substring(0, 1) : '企' }}
            </div>
          </div>

          <div class="info-content">
            <h1 class="company-name">{{ company.name }} <i class="el-icon-circle-check verify-icon" title="已认证"></i></h1>
            <div class="company-meta-row">
              <span class="meta-tag" v-if="company.industry"><i class="el-icon-suitcase"></i> {{ company.industry }}</span>
              <span class="meta-tag"><i class="el-icon-user"></i> {{ company.number || '100+' }}人</span>
              <span class="meta-tag" v-if="company.address"><i class="el-icon-location-outline"></i> {{ company.address }}</span>
            </div>
          </div>

          <div class="company-actions">
            <div class="stat-box">
              <div class="stat-num">{{ company.jobCount || 0 }}</div>
              <div class="stat-label">在招职位</div>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-box">
              <div class="stat-num">{{ company.viewCount || 0 }}</div>
              <div class="stat-label">人气热度</div>
            </div>
          </div>
        </div>
      </div>

      <div class="content-layout">
        <!-- 左侧：主要内容 -->
        <div class="main-column">
          <!-- 公司介绍 -->
          <div class="content-card">
            <div class="card-title">公司介绍</div>
            <div class="company-desc-text">
              {{ company.description || '这家公司很神秘，暂无详细介绍...' }}
            </div>
          </div>

          <!-- 在招职位列表 -->
          <div class="content-card">
            <div class="card-title-row">
              <div class="card-title">在招职位 <span class="highlight-count">({{ jobList.length }})</span></div>
            </div>

            <div class="jobs-list-wrapper">
              <template v-if="jobList.length > 0">
                <div v-for="job in jobList" :key="job.id" class="job-item-clean" @click="viewJob(job.id)">
                  <div class="job-left">
                    <h4 class="job-name">{{ job.title }}</h4>
                    <div class="job-tags">
                      <span>{{ job.position }}</span>
                      <span class="dot">·</span>
                      <span>{{ job.experience || '经验不限' }}</span>
                      <span class="dot">·</span>
                      <span>{{ job.education || '学历不限' }}</span>
                    </div>
                  </div>
                  <div class="job-right">
                    <span class="salary-text">{{ job.minSalary }}-{{ job.maxSalary }}K</span>
                    <span class="date-text">{{ formatTime(job.createTime) }}</span>
                  </div>
                </div>
              </template>

              <div v-else class="empty-state">
                <el-empty description="暂无在招职位" :image-size="100"></el-empty>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧：侧边栏信息 -->
        <div class="side-column">
          <!-- 基本信息 -->
          <div class="side-card">
            <div class="side-title">基本信息</div>
            <div class="info-list">
              <div class="info-item">
                <span class="label">公司规模</span>
                <span class="value">{{ company.number || '未知' }}人</span>
              </div>
              <div class="info-item">
                <span class="label">所属行业</span>
                <span class="value">{{ company.industry || '未分类' }}</span>
              </div>
              <div class="info-item">
                <span class="label">成立时间</span>
                <span class="value">{{ company.foundedYear || '未知' }}</span>
              </div>
              <div class="info-item" v-if="company.website">
                <span class="label">官方网站</span>
                <span class="value link">
                  <a :href="company.website" target="_blank">点击访问 <i class="el-icon-link"></i></a>
                </span>
              </div>
            </div>
          </div>

          <!-- 联系方式 -->
          <div class="side-card">
            <div class="side-title">联系方式</div>
            <div class="contact-list">
              <div class="contact-row" v-if="company.email">
                <div class="icon-box"><i class="el-icon-message"></i></div>
                <div class="text">{{ company.email }}</div>
              </div>
              <div class="contact-row" v-if="company.phone">
                <div class="icon-box"><i class="el-icon-phone-outline"></i></div>
                <div class="text">{{ company.phone }}</div>
              </div>
              <div class="contact-row" v-if="company.address">
                <div class="icon-box"><i class="el-icon-map-location"></i></div>
                <div class="text">{{ company.address }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
// 逻辑代码保持不变
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
        // 模拟数据或API调用
        if (!this.$api.company.getCompanyDetail) {
          this.company = {
            id: id,
            name: '示例科技公司',
            description: '我们是一家致力于技术创新的领先企业，提供优厚的薪资待遇和广阔的发展空间。',
            number: '500-999',
            industry: '互联网/软件',
            address: '上海市浦东新区张江高科',
            jobCount: 12,
            viewCount: 3456
          }
          await this.loadCompanyJobs()
          return
        }

        const response = await this.$api.company.getCompanyDetail(id)
        this.company = response || {}
        await this.loadCompanyJobs()
      } catch (error) {
        console.error('获取公司详情失败:', error)
        this.company = { name: '未知企业' }
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
        this.jobList = []
      }
    },
    viewJob(id) {
      this.$router.push(`/job/${id}`)
    },
    handleLogoError(event) {
      event.target.style.display = 'none'
      event.target.parentNode.innerHTML = '<div class="text-logo">企</div>'
    },
    formatTime(timestamp) {
      if (!timestamp) return ''
      const date = new Date(timestamp)
      return `${date.getMonth() + 1}-${date.getDate()}`
    }
  },
  mounted() {
    this.loadCompanyDetail()
  }
}
</script>

<style scoped>
/* ================= 基础布局 ================= */
.company-detail-page {
  min-height: 100vh;
  background-color: #f8fafc; /* 冷灰背景 */
  padding-bottom: 60px;
}

.container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 20px;
  position: relative;
  z-index: 2;
  margin-top: -60px; /* 卡片上浮，盖住 Banner */
}

/* 1. 顶部 Banner */
.company-banner {
  height: 200px;
  background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
  position: relative;
  overflow: hidden;
}

/* 增加纹理效果 */
.company-banner::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background-image: radial-gradient(rgba(255,255,255,0.1) 1px, transparent 1px);
  background-size: 20px 20px;
  opacity: 0.3;
}

/* 2. 头部信息卡片 */
.company-profile-card {
  background: white;
  border-radius: 12px;
  padding: 0 30px 30px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
  margin-bottom: 24px;
  position: relative;
}

.profile-main {
  display: flex;
  align-items: flex-end;
  position: relative;
  top: -30px; /* Logo 上浮 */
}

.logo-wrapper {
  width: 100px;
  height: 100px;
  background: white;
  border-radius: 12px;
  padding: 4px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  margin-right: 24px;
  flex-shrink: 0;
}

.real-logo {
  width: 100%;
  height: 100%;
  border-radius: 8px;
  object-fit: cover;
}

.text-logo {
  width: 100%;
  height: 100%;
  background: #f1f5f9;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  font-weight: bold;
  color: #94a3b8;
}

.info-content {
  flex: 1;
  padding-bottom: 5px; /* 对齐微调 */
}

.company-name {
  font-size: 28px;
  font-weight: 800;
  color: #1e293b;
  margin: 0 0 12px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.verify-icon {
  font-size: 20px;
  color: #3b82f6;
}

.company-meta-row {
  display: flex;
  gap: 16px;
}

.meta-tag {
  color: #64748b;
  font-size: 14px;
  background: #f8fafc;
  padding: 4px 12px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.company-actions {
  display: flex;
  align-items: center;
  gap: 20px;
  padding-bottom: 5px;
}

.stat-box {
  text-align: center;
}

.stat-num {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1.2;
}

.stat-label {
  font-size: 12px;
  color: #94a3b8;
}

.stat-divider {
  width: 1px;
  height: 30px;
  background: #e2e8f0;
}

/* ================= 3. 双栏布局 ================= */
.content-layout {
  display: flex;
  gap: 24px;
}

.main-column {
  flex: 1;
}

.side-column {
  width: 320px;
  flex-shrink: 0;
}

/* 通用卡片样式 */
.content-card, .side-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.03);
  margin-bottom: 24px;
}

.card-title, .side-title {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 20px;
  border-left: 4px solid #ff6b00;
  padding-left: 12px;
  line-height: 1;
}

/* 左侧内容 */
.company-desc-text {
  font-size: 15px;
  line-height: 1.8;
  color: #475569;
  white-space: pre-line;
}

.card-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.highlight-count {
  color: #ff6b00;
  font-weight: 400;
}

.jobs-list-wrapper {
  display: flex;
  flex-direction: column;
}

.job-item-clean {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid #f1f5f9;
  cursor: pointer;
  transition: all 0.2s;
}

.job-item-clean:last-child { border-bottom: none; }

.job-item-clean:hover {
  background: #fcfcfc; /* 微妙的背景变化 */
  padding-left: 10px; /* 增加一点缩进效果 */
  padding-right: 10px;
  border-radius: 8px;
}

.job-name {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 8px 0;
}

.job-tags {
  font-size: 13px;
  color: #94a3b8;
}

.dot { margin: 0 6px; color: #cbd5e1; }

.job-right {
  text-align: right;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
}

.salary-text {
  color: #ff6b00;
  font-weight: 700;
  font-size: 16px;
}

.date-text {
  font-size: 12px;
  color: #cbd5e1;
}

/* 右侧侧边栏 */
.info-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}

.info-item .label { color: #94a3b8; }
.info-item .value { color: #334155; font-weight: 500; }
.info-item .value.link a { color: #3b82f6; text-decoration: none; }
.info-item .value.link a:hover { text-decoration: underline; }

.contact-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.contact-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.icon-box {
  width: 32px;
  height: 32px;
  background: #fff7ed;
  color: #ff6b00;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
}

.contact-row .text {
  font-size: 14px;
  color: #475569;
  padding-top: 6px;
  line-height: 1.5;
  word-break: break-all;
}

.empty-state { padding: 40px 0; }

/* 响应式 */
@media (max-width: 768px) {
  .profile-main { flex-direction: column; align-items: center; text-align: center; top: -50px; }
  .logo-wrapper { margin-right: 0; margin-bottom: 16px; }
  .info-content { padding-bottom: 20px; }
  .company-meta-row { justify-content: center; }
  .company-actions { width: 100%; justify-content: center; border-top: 1px solid #f1f5f9; padding-top: 20px; }

  .content-layout { flex-direction: column; }
  .side-column { width: 100%; }
}
</style>