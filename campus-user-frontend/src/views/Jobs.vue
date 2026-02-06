<template>
  <div class="jobs">
    <div class="container">
      <h1>职位列表</h1>
      
      <!-- 搜索栏 -->
      <div class="search-section">
        <div class="search-bar">
          <el-input 
            v-model="filters.keyword" 
            placeholder="搜索职位名称、公司名称" 
            size="large"
            clearable
            @keyup.enter.native="searchJobs">
            <el-button slot="append" icon="el-icon-search" @click="searchJobs"></el-button>
          </el-input>
        </div>
      </div>

      <!-- 筛选条件 -->
      <div class="filter-section">
        <!-- 城市筛选 -->
        <div class="filter-group">
          <span class="filter-label">城市：</span>
          <div class="filter-options">
            <el-tag 
              v-for="city in cities" 
              :key="city.value"
              :type="filters.city === city.value ? '' : 'info'"
              :effect="filters.city === city.value ? 'dark' : 'plain'"
              @click="selectCity(city.value)"
              class="filter-tag">
              {{ city.label }}
            </el-tag>
          </div>
        </div>

        <!-- 薪资筛选 -->
        <div class="filter-group">
          <span class="filter-label">薪资：</span>
          <div class="filter-options">
            <el-tag 
              v-for="salary in salaryRanges" 
              :key="salary.value"
              :type="filters.salary === salary.value ? '' : 'info'"
              :effect="filters.salary === salary.value ? 'dark' : 'plain'"
              @click="selectSalary(salary.value)"
              class="filter-tag">
              {{ salary.label }}
            </el-tag>
          </div>
        </div>

        <!-- 学历要求 -->
        <div class="filter-group">
          <span class="filter-label">学历：</span>
          <div class="filter-options">
            <el-tag 
              v-for="edu in educations" 
              :key="edu.value"
              :type="filters.education === edu.value ? '' : 'info'"
              :effect="filters.education === edu.value ? 'dark' : 'plain'"
              @click="selectEducation(edu.value)"
              class="filter-tag">
              {{ edu.label }}
            </el-tag>
          </div>
        </div>

        <!-- 公司规模 -->
        <div class="filter-group">
          <span class="filter-label">规模：</span>
          <div class="filter-options">
            <el-tag 
              v-for="scale in companyScales" 
              :key="scale.value"
              :type="filters.companyScale === scale.value ? '' : 'info'"
              :effect="filters.companyScale === scale.value ? 'dark' : 'plain'"
              @click="selectCompanyScale(scale.value)"
              class="filter-tag">
              {{ scale.label }}
            </el-tag>
          </div>
        </div>

        <!-- 排序方式 -->
        <div class="sort-section">
          <span class="filter-label">排序：</span>
          <el-select v-model="sortBy" @change="searchJobs" size="small">
            <el-option label="最新发布" value="time_desc"></el-option>
            <el-option label="薪资最高" value="salary_desc"></el-option>
            <el-option label="薪资最低" value="salary_asc"></el-option>
          </el-select>
        </div>

        <!-- 清空筛选 -->
        <div class="clear-filters">
          <el-button size="small" @click="clearFilters">清空筛选</el-button>
        </div>
      </div>

      <!-- 职位列表 -->
      <div class="jobs-grid">
        <el-row :gutter="20">
          <el-col :span="12" v-for="(job, index) in jobList" :key="job.id">
            <el-card class="job-card" @click.native="viewJobDetail(job.id)" :style="{animationDelay: index * 0.1 + 's'}">
              <div class="job-header">
                <div class="job-icon">
                  <i class="el-icon-suitcase"></i>
                </div>
                <div class="job-basic">
                  <h3 class="job-title">{{ job.title }}</h3>
                  <div class="job-salary">
                    <i class="el-icon-coin"></i>
                    {{ job.minSalary }}-{{ job.maxSalary }}K
                  </div>
                </div>
                <div class="job-status">
                  <el-tag size="small" :type="job.status === 1 ? 'success' : 'warning'">
                    {{ job.status === 1 ? '招聘中' : '待审核' }}
                  </el-tag>
                </div>
              </div>
              
              <div class="job-details">
                <div class="job-meta">
                  <span class="meta-item">
                    <i class="el-icon-location"></i>
                    {{ job.position || '地点不限' }}
                  </span>
                  <span class="meta-item">
                    <i class="el-icon-reading"></i>
                    {{ job.education || '学历不限' }}
                  </span>
                  <span class="meta-item">
                    <i class="el-icon-time"></i>
                    {{ formatTime(job.createTime) }}
                  </span>
                </div>
                
                <div class="job-description">
                  {{ job.description || '职位描述暂未添加' }}
                </div>
                
                <div class="job-footer">
                  <div class="company-info">
                    <div class="company-logo">
                      <i class="el-icon-office-building"></i>
                    </div>
                    <div class="company-details">
                      <div class="company-name">{{ job.companyModel ? job.companyModel.name : '未知公司' }}</div>
                      <div class="company-scale" v-if="job.companyModel">
                        {{ job.companyModel.number || 100 }}人
                      </div>
                    </div>
                  </div>
                  <div class="job-actions">
                    <el-button size="small" type="primary" @click.stop="applyJob(job.id)">
                      <i class="el-icon-check"></i>
                      立即申请
                    </el-button>
                  </div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 分页 -->
      <div class="pagination">
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
        { label: '全部', value: '' },
        { label: '3K以下', value: '3K以下' },
        { label: '3K-5K', value: '3K-5K' },
        { label: '5K-10K', value: '5K-10K' },
        { label: '10K-15K', value: '10K-15K' },
        { label: '15K-25K', value: '15K-25K' },
        { label: '25K-40K', value: '25K-40K' },
        { label: '40K以上', value: '40K以上' }
      ],

      educations: [
        { label: '全部', value: '' },
        { label: '不限', value: '不限' },
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
      total: 2
    }
  },
  methods: {
    async loadJobs() {
      this.loading = true
      try {
        const params = {}
        
        // 添加所有筛选条件
        if (this.filters.keyword) {
          params.title = this.filters.keyword
        }
        if (this.filters.city) {
          params.cityFilter = this.filters.city
        }
        if (this.filters.salary) {
          params.salaryRange = this.filters.salary
        }
        if (this.filters.education) {
          params.education = this.filters.education
        }
        if (this.filters.companyScale) {
          params.companyScale = this.filters.companyScale
        }
        
        // 后端默认筛选已审核通过的职位
        
        // 添加排序参数
        params.sortBy = this.sortBy
        
        console.log('职位筛选参数:', params)
        
        const response = await this.$api.employment.getJobList(this.currentPage, this.pageSize, params)
        this.jobList = response.list || []
        this.total = response.total || 0
      } catch (error) {
        console.error('获取职位列表失败:', error)
        this.$message.error('获取职位列表失败')
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
    selectCompanyScale(scale) {
      this.filters.companyScale = this.filters.companyScale === scale ? '' : scale
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
      // 申请职位逻辑
      this.$router.push(`/job/${jobId}`)
    },
    handlePageChange(page) {
      this.currentPage = page
      this.loadJobs()
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
    // 获取URL参数
    if (this.$route.query.keyword) {
      this.filters.keyword = this.$route.query.keyword
    }
    this.loadJobs()
  }
}
</script>

<style scoped>
.jobs {
  min-height: 100vh;
  background: var(--background-light);
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.search-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 15px;
}

.search-bar {
  max-width: 600px;
  margin: 0 auto;
}

.filter-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.filter-group {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  flex-wrap: wrap;
}

.filter-label {
  min-width: 60px;
  color: #666;
  font-size: 14px;
  margin-right: 15px;
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  flex: 1;
}

.filter-tag {
  cursor: pointer;
  margin: 0;
  transition: all 0.3s;
}

.filter-tag:hover {
  transform: translateY(-1px);
}

.sort-section {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.clear-filters {
  text-align: right;
}

.jobs-grid {
  margin-bottom: 30px;
}

.job-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid #f0f0f0;
  animation: fadeInUp 0.6s ease-out both;
}

.job-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 20px 40px rgba(255, 152, 0, 0.15);
  border-color: #ff9800;
}

.job-header {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f5f5f5;
}

.job-icon {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #ff9800, #ffb74d);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  margin-right: 15px;
  box-shadow: 0 4px 12px rgba(255, 152, 0, 0.3);
}

.job-basic {
  flex: 1;
}

.job-title {
  margin: 0 0 8px 0;
  color: #333;
  font-size: 20px;
  font-weight: 600;
  line-height: 1.3;
}

.job-salary {
  display: flex;
  align-items: center;
  color: #ff9800;
  font-weight: 600;
  font-size: 16px;
}

.job-salary i {
  margin-right: 6px;
  font-size: 14px;
}

.job-status {
  margin-left: 15px;
}

.job-details {
  margin-top: 15px;
}

.job-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 15px;
}

.meta-item {
  display: flex;
  align-items: center;
  color: #666;
  font-size: 14px;
}

.meta-item i {
  margin-right: 5px;
  color: #ff9800;
  font-size: 12px;
}

.job-description {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 20px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.job-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #f5f5f5;
}

.company-info {
  display: flex;
  align-items: center;
}

.company-logo {
  width: 36px;
  height: 36px;
  background: #f5f5f5;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  margin-right: 10px;
}

.company-name {
  font-weight: 600;
  color: #333;
  font-size: 14px;
  margin-bottom: 2px;
}

.company-scale {
  color: #999;
  font-size: 12px;
}

.job-actions .el-button {
  border-radius: 20px;
  padding: 8px 16px;
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

.pagination {
  text-align: center;
  padding: 20px 0;
}
</style>