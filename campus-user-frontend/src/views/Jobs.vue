<template>
  <div class="jobs-page">
    <div class="container">
      <!-- 1. 顶部搜索与筛选区域 -->
      <div class="filter-container">
        <!-- 搜索栏 -->
        <div class="search-header">
          <h1 class="page-title">发现新机会</h1>
          <div class="search-wrapper">
            <el-input
                v-model="filters.keyword"
                placeholder="搜索职位名称、公司名称..."
                class="custom-search-input"
                clearable
                @keyup.enter.native="searchJobs">
              <el-button slot="append" icon="el-icon-search" @click="searchJobs" class="search-btn">搜索</el-button>
            </el-input>
          </div>
        </div>

        <!-- 筛选条件组 -->
        <div class="filter-groups-wrapper">
          <!-- 城市 -->
          <div class="filter-row">
            <span class="filter-label">工作地点</span>
            <div class="filter-items">
              <span
                  v-for="city in cities"
                  :key="city.value"
                  class="filter-pill"
                  :class="{ active: filters.city === city.value }"
                  @click="selectCity(city.value)">
                {{ city.label }}
              </span>
            </div>
          </div>

          <!-- 薪资 -->
          <div class="filter-row">
            <span class="filter-label">薪资待遇</span>
            <div class="filter-items">
              <span
                  v-for="salary in salaryRanges"
                  :key="salary.value"
                  class="filter-pill"
                  :class="{ active: filters.salary === salary.value }"
                  @click="selectSalary(salary.value)">
                {{ salary.label }}
              </span>
            </div>
          </div>

          <!-- 学历 -->
          <div class="filter-row">
            <span class="filter-label">学历要求</span>
            <div class="filter-items">
              <span
                  v-for="edu in educations"
                  :key="edu.value"
                  class="filter-pill"
                  :class="{ active: filters.education === edu.value }"
                  @click="selectEducation(edu.value)">
                {{ edu.label }}
              </span>
            </div>
          </div>

          <!-- 更多筛选与排序 (折叠/工具栏) -->
          <div class="filter-toolbar">
            <div class="left-tools">
              <!-- 公司规模下拉筛选 (节省空间) -->
              <el-select v-model="filters.companyScale" placeholder="公司规模" size="small" clearable @change="searchJobs" class="mini-select">
                <el-option v-for="scale in companyScales" :key="scale.value" :label="scale.label" :value="scale.value"></el-option>
              </el-select>
            </div>

            <div class="right-tools">
              <span class="sort-label">排序：</span>
              <el-select v-model="sortBy" @change="searchJobs" size="small" class="sort-select">
                <el-option label="最新发布" value="time_desc"></el-option>
                <el-option label="薪资最高" value="salary_desc"></el-option>
                <el-option label="薪资最低" value="salary_asc"></el-option>
              </el-select>
              <el-button type="text" class="reset-btn" @click="clearFilters">
                <i class="el-icon-refresh"></i> 重置
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 2. 职位列表区域 -->
      <div class="jobs-list-section" v-loading="loading">
        <template v-if="jobList.length > 0">
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12" :lg="12" v-for="(job, index) in jobList" :key="job.id">
              <div class="job-card-modern" @click="viewJobDetail(job.id)" :style="{animationDelay: index * 0.05 + 's'}">

                <div class="card-top">
                  <div class="job-main">
                    <div class="title-row">
                      <h3 class="job-title">{{ job.title }}</h3>
                      <span class="salary-text">{{ job.minSalary }}-{{ job.maxSalary }}K</span>
                    </div>
                    <div class="tag-row">
                      <span class="mini-tag">{{ job.position || '地点不限' }}</span>
                      <span class="mini-tag">{{ job.education || '学历不限' }}</span>
                      <span class="mini-tag">{{ job.experience || '经验不限' }}</span>
                    </div>
                  </div>
                  <!-- 右上角状态或发布时间 -->
                  <div class="job-extra">
                    <span class="time-tag">{{ formatTime(job.createTime) }}</span>
                  </div>
                </div>

                <div class="card-divider"></div>

                <div class="card-bottom">
                  <div class="company-wrapper">
                    <div class="company-logo-text">
                      {{ job.companyModel ? job.companyModel.name.substring(0,1) : '企' }}
                    </div>
                    <div class="company-info">
                      <div class="company-name">{{ job.companyModel ? job.companyModel.name : '未知公司' }}</div>
                      <div class="company-meta">
                        {{ job.companyModel ? job.companyModel.industry : '互联网' }} ·
                        {{ job.companyModel ? job.companyModel.number : '100-499人' }}
                      </div>
                    </div>
                  </div>
                  <el-button class="apply-btn" size="small" round @click.stop="applyJob(job.id)">
                    立即申请
                  </el-button>
                </div>

              </div>
            </el-col>
          </el-row>

          <!-- 分页 -->
          <div class="pagination-wrapper">
            <el-pagination
                background
                @current-change="handlePageChange"
                :current-page="currentPage"
                :page-size="pageSize"
                layout="prev, pager, next, jumper"
                :total="total">
            </el-pagination>
          </div>
        </template>

        <!-- 空状态 -->
        <template v-else>
          <el-empty description="暂无符合条件的职位" :image-size="200"></el-empty>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
// 逻辑部分保持不变
export default {
  name: 'Jobs',
  data() {
    return {
      filters: {
        keyword: '',
        city: '',
        salary: '',
        education: '',
        companyScale: ''
      },
      cities: [
        { label: '全部', value: '' },
        { label: '北京', value: '北京' },
        { label: '上海', value: '上海' },
        { label: '深圳', value: '深圳' },
        { label: '杭州', value: '杭州' },
        { label: '广州', value: '广州' },
        { label: '成都', value: '成都' },
        { label: '南京', value: '南京' },
        { label: '武汉', value: '武汉' }
      ],
      salaryRanges: [
        { label: '不限', value: '' },
        { label: '3K以下', value: '3K以下' },
        { label: '3K-5K', value: '3K-5K' },
        { label: '5K-10K', value: '5K-10K' },
        { label: '10K-15K', value: '10K-15K' },
        { label: '15K-25K', value: '15K-25K' },
        { label: '25K-40K', value: '25K-40K' },
        { label: '40K以上', value: '40K以上' }
      ],
      educations: [
        { label: '不限', value: '' },
        { label: '大专', value: '大专' },
        { label: '本科', value: '本科' },
        { label: '硕士', value: '硕士' },
        { label: '博士', value: '博士' }
      ],
      companyScales: [
        { label: '全部', value: '' },
        { label: '0-20人', value: '0-20人' },
        { label: '20-99人', value: '20-99人' },
        { label: '100-499人', value: '100-499人' },
        { label: '500-999人', value: '500-999人' },
        { label: '1000-9999人', value: '1000-9999人' },
        { label: '10000人以上', value: '10000人以上' }
      ],
      sortBy: 'time_desc',
      jobList: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0
    }
  },
  methods: {
    async loadJobs() {
      this.loading = true
      try {
        const params = {}
        if (this.filters.keyword) params.title = this.filters.keyword
        if (this.filters.city) params.cityFilter = this.filters.city
        if (this.filters.salary) params.salaryRange = this.filters.salary
        if (this.filters.education) params.education = this.filters.education
        if (this.filters.companyScale) params.companyScale = this.filters.companyScale
        params.sortBy = this.sortBy

        console.log('职位筛选参数:', params)
        const response = await this.$api.employment.getJobList(this.currentPage, this.pageSize, params)
        this.jobList = response.list || []
        this.total = response.total || 0
      } catch (error) {
        console.error('获取职位列表失败:', error)
        // 演示数据防白屏
        this.jobList = []
      } finally {
        this.loading = false
      }
    },
    searchJobs() {
      this.currentPage = 1
      this.loadJobs()
    },
    selectCity(city) {
      this.filters.city = this.filters.city === city ? '' : city
      this.searchJobs()
    },
    selectSalary(salary) {
      this.filters.salary = this.filters.salary === salary ? '' : salary
      this.searchJobs()
    },
    selectEducation(education) {
      this.filters.education = this.filters.education === education ? '' : education
      this.searchJobs()
    },
    clearFilters() {
      this.filters = {
        keyword: '',
        city: '',
        salary: '',
        education: '',
        companyScale: ''
      }
      this.sortBy = 'time_desc'
      this.searchJobs()
    },
    viewJobDetail(id) {
      this.$router.push(`/job/${id}`)
    },
    applyJob(jobId) {
      this.$router.push(`/job/${jobId}`)
    },
    handlePageChange(page) {
      this.currentPage = page
      this.loadJobs()
    },
    formatTime(timestamp) {
      if (!timestamp) return '刚刚'
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
    if (this.$route.query.keyword) {
      this.filters.keyword = this.$route.query.keyword
    }
    this.loadJobs()
  }
}
</script>

<style scoped>
.jobs-page {
  min-height: 100vh;
  background: #f8fafc;
  padding-bottom: 60px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* ================= 1. 筛选区样式 ================= */
.filter-container {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  margin-bottom: 24px;
}

.search-header {
  margin-bottom: 30px;
  text-align: center;
}

.page-title {
  font-size: 28px;
  color: #1e293b;
  margin-bottom: 20px;
  font-weight: 800;
}

.search-wrapper {
  max-width: 600px;
  margin: 0 auto;
}

/* 覆盖 Element UI 输入框样式 */
.custom-search-input >>> .el-input__inner {
  height: 50px;
  line-height: 50px;
  border-radius: 25px 0 0 25px;
  border-right: none;
  border-color: #e2e8f0;
  padding-left: 20px;
  font-size: 15px;
}

.custom-search-input >>> .el-input__inner:focus {
  border-color: #ff6b00;
}

.custom-search-input >>> .el-input-group__append {
  background-color: #ff6b00;
  border-color: #ff6b00;
  color: white;
  border-radius: 0 25px 25px 0;
  font-weight: 600;
  padding: 0 30px;
}

.custom-search-input >>> .el-input-group__append button.el-button {
  margin: 0;
  border: none;
}

/* 筛选行样式 */
.filter-groups-wrapper {
  border-top: 1px solid #f1f5f9;
  padding-top: 20px;
}

.filter-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 15px;
}

.filter-label {
  width: 80px;
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
  padding-top: 6px; /* 对齐 */
}

.filter-items {
  flex: 1;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

/* 胶囊筛选按钮 */
.filter-pill {
  font-size: 14px;
  color: #475569;
  padding: 6px 16px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s;
  background: transparent;
}

.filter-pill:hover {
  color: #ff6b00;
  background: #fff7ed;
}

.filter-pill.active {
  background: #ff6b00;
  color: white;
  font-weight: 500;
  box-shadow: 0 2px 6px rgba(255, 107, 0, 0.2);
}

/* 工具栏 */
.filter-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px dashed #e2e8f0;
}

.right-tools {
  display: flex;
  align-items: center;
  gap: 15px;
}

.sort-label {
  font-size: 14px;
  color: #64748b;
}

.mini-select {
  width: 140px;
}

.reset-btn {
  color: #94a3b8;
  font-size: 14px;
}

.reset-btn:hover {
  color: #ff6b00;
}

/* ================= 2. 职位卡片样式 ================= */
.job-card-modern {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 20px;
  cursor: pointer;
  border: 1px solid #f0f4f8;
  box-shadow: 0 2px 8px rgba(0,0,0,0.02);
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  animation: fadeUp 0.5s ease-out backwards;
}

.job-card-modern:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0,0,0,0.08);
  border-color: rgba(255, 107, 0, 0.2);
}

.card-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.job-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1.4;
}

.salary-text {
  font-size: 18px;
  font-weight: 800;
  color: #ff6b00;
}

.tag-row {
  display: flex;
  gap: 8px;
}

.mini-tag {
  background: #f8fafc;
  color: #64748b;
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 4px;
}

.time-tag {
  font-size: 12px;
  color: #94a3b8;
}

.card-divider {
  height: 1px;
  background: #f1f5f9;
  margin: 10px 0 15px;
}

.card-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.company-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.company-logo-text {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #fff7ed 0%, #ffedd5 100%);
  color: #ff6b00;
  font-weight: 700;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}

.company-info {
  display: flex;
  flex-direction: column;
}

.company-name {
  font-size: 14px;
  font-weight: 600;
  color: #334155;
  margin-bottom: 2px;
}

.company-meta {
  font-size: 12px;
  color: #94a3b8;
}

.apply-btn {
  background: #fff0e6;
  color: #ff6b00;
  border: none;
  font-weight: 600;
  transition: all 0.2s;
}

.job-card-modern:hover .apply-btn {
  background: #ff6b00;
  color: white;
}

/* ================= 3. 分页 ================= */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

/* 覆盖分页样式 */
.pagination-wrapper >>> .el-pagination.is-background .el-pager li:not(.disabled).active {
  background-color: #ff6b00;
}

.pagination-wrapper >>> .el-pagination.is-background .el-pager li:hover {
  color: #ff6b00;
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 响应式 */
@media (max-width: 768px) {
  .search-wrapper { max-width: 100%; }
  .filter-row { flex-direction: column; }
  .filter-items { margin-top: 8px; }
  .card-bottom { flex-direction: column; align-items: flex-start; gap: 15px; }
  .apply-btn { width: 100%; }
}
</style>