<template>
  <div class="companies">
    <div class="container">
      <h1>公司列表</h1>
      
      <!-- 搜索栏 -->
      <div class="search-section">
        <div class="search-bar">
          <el-input 
            v-model="filters.keyword" 
            placeholder="搜索公司名称、行业" 
            size="large"
            clearable
            @keyup.enter.native="searchCompanies">
            <el-button slot="append" icon="el-icon-search" @click="searchCompanies"></el-button>
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

        <!-- 行业筛选 -->
        <div class="filter-group">
          <span class="filter-label">行业：</span>
          <div class="filter-options">
            <el-tag 
              v-for="industry in industries" 
              :key="industry.value"
              :type="filters.industry === industry.value ? '' : 'info'"
              :effect="filters.industry === industry.value ? 'dark' : 'plain'"
              @click="selectIndustry(industry.value)"
              class="filter-tag">
              {{ industry.label }}
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
              :type="filters.scale === scale.value ? '' : 'info'"
              :effect="filters.scale === scale.value ? 'dark' : 'plain'"
              @click="selectScale(scale.value)"
              class="filter-tag">
              {{ scale.label }}
            </el-tag>
          </div>
        </div>

        <!-- 排序方式 -->
        <div class="sort-section">
          <span class="filter-label">排序：</span>
          <el-select v-model="sortBy" @change="searchCompanies" size="small">
            <el-option label="最新注册" value="createTime"></el-option>
            <el-option label="规模最大" value="scale"></el-option>
            <el-option label="职位最多" value="jobs"></el-option>
          </el-select>
        </div>

        <!-- 清空筛选 -->
        <div class="clear-filters">
          <el-button size="small" @click="clearFilters">清空筛选</el-button>
        </div>
      </div>

      <div class="companies-grid">
        <el-row :gutter="20">
          <el-col :span="8" v-for="company in companyList" :key="company.id">
            <el-card class="company-card" @click.native="viewCompany(company.id)">
              <div class="company-header">
                <div class="company-logo">
                  <div class="logo-wrapper">
                    <img v-if="company.companyLogo" :src="company.companyLogo" :alt="company.name" @error="handleLogoError">
                    <div v-else class="default-logo">
                      <i class="el-icon-office-building"></i>
                    </div>
                  </div>
                </div>
                <div class="company-basic">
                  <h3>{{ company.name }}</h3>
                  <div class="company-info">
                    <span class="company-scale">
                      <i class="el-icon-user"></i>
                      {{ company.number || 100 }}人
                    </span>
                    <span class="company-industry" v-if="company.category">
                      <i class="el-icon-suitcase"></i>
                      {{ company.category }}
                    </span>
                  </div>
                </div>
              </div>
              <div class="company-desc">
                {{ company.description || '该公司暂未添加介绍' }}
              </div>
              <div class="company-footer">
                <div class="company-jobs">
                  <i class="el-icon-position"></i>
                  在招职位: {{ company.jobCount || 0 }}个
                </div>
                <div class="company-status">
                  <el-tag size="mini" :type="company.status === 'Approve' ? 'success' : 'warning'">
                    {{ company.status === 'Approve' ? '已认证' : '待审核' }}
                  </el-tag>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

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
  name: 'Companies',
  data() {
    return {
      filters: {
        keyword: '',
        city: '',
        industry: '',
        scale: ''
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
      industries: [
        { label: '全部', value: '' },
        { label: 'IT互联网', value: 'IT互联网' },
        { label: '金融', value: '金融' },
        { label: '教育', value: '教育' },
        { label: '医疗健康', value: '医疗健康' },
        { label: '电商', value: '电商' },
        { label: '游戏', value: '游戏' },
        { label: '房地产', value: '房地产' },
        { label: '制造业', value: '制造业' }
      ],
      companyScales: [
        { label: '全部', value: '' },
        { label: '0-20人', value: '0-20' },
        { label: '20-99人', value: '20-99' },
        { label: '100-499人', value: '100-499' },
        { label: '500-999人', value: '500-999' },
        { label: '1000-9999人', value: '1000-9999' },
        { label: '10000人以上', value: '10000+' }
      ],
      sortBy: 'createTime',
      companyList: [],
      loading: false,
      currentPage: 1,
      pageSize: 9,
      total: 0
    }
  },
  methods: {
    async loadCompanies() {
      this.loading = true
      try {
        const params = {}
        
        // 添加所有筛选条件
        if (this.filters.keyword) {
          params.name = this.filters.keyword
        }
        if (this.filters.city) {
          params.cityFilter = this.filters.city
        }
        if (this.filters.industry) {
          params.industryFilter = this.filters.industry
        }
        if (this.filters.scale) {
          const [minScale, maxScale] = this.filters.scale.split('-')
          if (minScale) params.minNumber = parseInt(minScale)
          if (maxScale && maxScale !== '+') {
            params.maxNumber = parseInt(maxScale)
          } else if (maxScale === '+') {
            params.minNumber = parseInt(minScale)
            // 不设置maxNumber，表示无上限
          }
        }
        
        // 添加排序参数
        params.sortBy = this.sortBy
        
        console.log('企业筛选参数:', params)
        
        const response = await this.$api.company.getCompanyList(this.currentPage, this.pageSize, params)
        this.companyList = response.list || []
        this.total = response.total || 0
      } catch (error) {
        console.error('获取公司列表失败:', error)
        this.$message.error('获取公司列表失败')
      } finally {
        this.loading = false
      }
    },
    searchCompanies() {
      this.currentPage = 1
      this.loadCompanies()
    },
    selectCity(city) {
      this.filters.city = this.filters.city === city ? '' : city
      this.searchCompanies()
    },
    selectIndustry(industry) {
      this.filters.industry = this.filters.industry === industry ? '' : industry
      this.searchCompanies()
    },
    selectScale(scale) {
      this.filters.scale = this.filters.scale === scale ? '' : scale
      this.searchCompanies()
    },
    clearFilters() {
      this.filters = {
        keyword: '',
        city: '',
        industry: '',
        scale: ''
      }
      this.sortBy = 'createTime'
      this.searchCompanies()
    },
    viewCompany(id) {
      this.$router.push(`/company/${id}`)
    },
    handleLogoError(event) {
      // 当公司logo加载失败时，隐藏图片元素
      event.target.style.display = 'none'
      event.target.parentNode.innerHTML = '<div class="default-logo"><i class="el-icon-office-building"></i></div>'
    },
    handlePageChange(page) {
      this.currentPage = page
      this.loadCompanies()
    }
  },
  mounted() {
    this.loadCompanies()
  }
}
</script>

<style scoped>
.companies {
  min-height: 100vh;
  background: linear-gradient(135deg, #fff5eb 0%, #ffe8d6 100%);
  padding: 20px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

h1 {
  text-align: center;
  color: #ff9800;
  font-size: 32px;
  margin-bottom: 30px;
  font-weight: 600;
}

.search-section {
  background: white;
  padding: 25px;
  border-radius: 16px;
  margin-bottom: 20px;
  box-shadow: 0 4px 12px rgba(255, 152, 0, 0.08);
}

.search-bar {
  max-width: 600px;
  margin: 0 auto;
}

.filter-section {
  background: white;
  padding: 25px;
  border-radius: 16px;
  margin-bottom: 25px;
  box-shadow: 0 4px 12px rgba(255, 152, 0, 0.08);
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
  font-weight: 500;
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  flex: 1;
}

.filter-tag {
  cursor: pointer;
  margin: 0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 20px;
  padding: 6px 16px;
}

.filter-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(255, 152, 0, 0.2);
}

.sort-section {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.clear-filters {
  text-align: right;
}

.companies-grid {
  margin-bottom: 30px;
}

.company-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid #ffe8d6;
  animation: fadeInUp 0.6s ease-out both;
}

.company-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 20px 40px rgba(255, 152, 0, 0.15);
  border-color: #ff9800;
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

.company-header {
  display: flex;
  align-items: flex-start;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #fff5eb;
}

.company-logo {
  margin-right: 15px;
}

.logo-wrapper {
  width: 70px;
  height: 70px;
  border-radius: 12px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #fff5eb, #ffe8d6);
  box-shadow: 0 4px 12px rgba(255, 152, 0, 0.15);
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
  font-size: 28px;
}

.company-basic {
  flex: 1;
}

.company-basic h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.company-info {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.company-scale, .company-industry {
  color: #666;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 5px;
  background: #fff5eb;
  padding: 4px 12px;
  border-radius: 12px;
}

.company-scale i, .company-industry i {
  color: #ff9800;
}

.company-desc {
  color: #666;
  line-height: 1.6;
  margin-bottom: 15px;
  font-size: 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.company-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #fff5eb;
}

.company-status {
  display: flex;
  align-items: center;
}

.company-jobs {
  color: #ff9800;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
}

.company-jobs i {
  font-size: 16px;
}

.pagination {
  text-align: center;
  padding: 20px 0;
}
</style>