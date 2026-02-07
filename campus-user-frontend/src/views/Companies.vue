<template>
  <div class="companies-page">
    <div class="container">

      <!-- 1. 顶部搜索与筛选区域 -->
      <div class="filter-container">
        <!-- 搜索栏 -->
        <div class="search-header">
          <h1 class="page-title">探索优秀企业</h1>
          <p class="page-subtitle">连接数千家顶尖雇主，发现你的理想职场</p>
          <div class="search-wrapper">
            <el-input
                v-model="filters.keyword"
                placeholder="搜索公司名称、行业..."
                class="custom-search-input"
                clearable
                @keyup.enter.native="searchCompanies">
              <el-button slot="append" icon="el-icon-search" @click="searchCompanies" class="search-btn">搜索</el-button>
            </el-input>
          </div>
        </div>

        <!-- 筛选条件组 -->
        <div class="filter-groups-wrapper">
          <!-- 城市 -->
          <div class="filter-row">
            <span class="filter-label">所在城市</span>
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

          <!-- 行业 -->
          <div class="filter-row">
            <span class="filter-label">所属行业</span>
            <div class="filter-items">
              <span
                  v-for="industry in industries"
                  :key="industry.value"
                  class="filter-pill"
                  :class="{ active: filters.industry === industry.value }"
                  @click="selectIndustry(industry.value)">
                {{ industry.label }}
              </span>
            </div>
          </div>

          <!-- 公司规模 -->
          <div class="filter-row">
            <span class="filter-label">人员规模</span>
            <div class="filter-items">
              <span
                  v-for="scale in companyScales"
                  :key="scale.value"
                  class="filter-pill"
                  :class="{ active: filters.scale === scale.value }"
                  @click="selectScale(scale.value)">
                {{ scale.label }}
              </span>
            </div>
          </div>

          <!-- 工具栏 -->
          <div class="filter-toolbar">
            <div class="right-tools">
              <span class="sort-label">排序：</span>
              <el-select v-model="sortBy" @change="searchCompanies" size="small" class="sort-select">
                <el-option label="综合排序" value="createTime"></el-option>
                <el-option label="规模最大" value="scale"></el-option>
                <el-option label="职位最多" value="jobs"></el-option>
              </el-select>
              <el-button type="text" class="reset-btn" @click="clearFilters">
                <i class="el-icon-refresh"></i> 重置筛选
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 2. 企业列表区域 -->
      <div class="companies-grid" v-loading="loading">
        <template v-if="companyList.length > 0">
          <el-row :gutter="24">
            <el-col :xs="24" :sm="12" :md="8" v-for="(company, index) in companyList" :key="company.id">
              <div class="company-card-modern" @click="viewCompany(company.id)" :style="{animationDelay: index * 0.05 + 's'}">

                <!-- 顶部背景条 -->
                <div class="card-bg-decoration"></div>

                <div class="card-body">
                  <!-- Logo与基本信息 -->
                  <div class="company-header-block">
                    <div class="logo-box">
                      <img v-if="company.companyLogo" :src="company.companyLogo" :alt="company.name" class="real-logo" @error="handleLogoError">
                      <div v-else class="text-logo">
                        {{ company.name.substring(0, 1) }}
                      </div>
                    </div>

                    <h3 class="company-name" :title="company.name">{{ company.name }}</h3>

                    <div class="company-tags">
                      <span class="tag-item" v-if="company.category">{{ company.category }}</span>
                      <span class="tag-item">{{ company.number || '100+' }}人</span>
                      <span class="tag-item" v-if="company.city">{{ company.city }}</span>
                    </div>
                  </div>

                  <!-- 简介 -->
                  <p class="company-desc">
                    {{ company.description || '这家公司很低调，暂无详细介绍...' }}
                  </p>

                  <div class="card-divider"></div>

                  <!-- 底部数据栏 -->
                  <div class="card-footer">
                    <div class="footer-stat">
                      <span class="stat-num">{{ company.jobCount || 0 }}</span>
                      <span class="stat-label">个热招职位</span>
                    </div>
                    <button class="view-btn">
                      进入主页 <i class="el-icon-right"></i>
                    </button>
                  </div>
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
          <el-empty description="暂无符合条件的企业" :image-size="200"></el-empty>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
// 逻辑代码保持不变
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
        if (this.filters.keyword) params.name = this.filters.keyword
        if (this.filters.city) params.cityFilter = this.filters.city
        if (this.filters.industry) params.industryFilter = this.filters.industry
        if (this.filters.scale) {
          const [minScale, maxScale] = this.filters.scale.split('-')
          if (minScale) params.minNumber = parseInt(minScale)
          if (maxScale && maxScale !== '+') {
            params.maxNumber = parseInt(maxScale)
          } else if (maxScale === '+') {
            params.minNumber = parseInt(minScale)
          }
        }
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
      event.target.style.display = 'none'
      event.target.parentNode.innerHTML = '<div class="text-logo">企</div>'
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
/* ================= 基础设置 ================= */
.companies-page {
  min-height: 100vh;
  background: #f8fafc; /* 统一使用冷灰色背景 */
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
  padding: 40px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  margin-bottom: 30px;
}

.search-header {
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
  margin-bottom: 30px;
}

.search-wrapper {
  max-width: 600px;
  margin: 0 auto;
}

/* 搜索框覆盖 */
.custom-search-input >>> .el-input__inner {
  height: 52px;
  line-height: 52px;
  border-radius: 26px 0 0 26px;
  border-right: none;
  border-color: #e2e8f0;
  padding-left: 24px;
  font-size: 16px;
  box-shadow: 0 4px 6px rgba(0,0,0,0.02);
}

.custom-search-input >>> .el-input__inner:focus {
  border-color: #ff6b00;
}

.custom-search-input >>> .el-input-group__append {
  background-color: #ff6b00;
  border-color: #ff6b00;
  color: white;
  border-radius: 0 26px 26px 0;
  font-weight: 600;
  padding: 0 30px;
  font-size: 16px;
}

.custom-search-input >>> .el-input-group__append button.el-button {
  margin: 0;
  border: none;
}

/* 筛选行 */
.filter-groups-wrapper {
  border-top: 1px solid #f1f5f9;
  padding-top: 30px;
}

.filter-row {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;
}

.filter-label {
  width: 80px;
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
  padding-top: 6px;
}

.filter-items {
  flex: 1;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

/* 胶囊筛选 */
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
  justify-content: flex-end;
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

.reset-btn {
  color: #94a3b8;
  font-size: 14px;
}
.reset-btn:hover { color: #ff6b00; }

/* ================= 2. 企业卡片样式 ================= */
.company-card-modern {
  background: white;
  border-radius: 12px;
  margin-bottom: 24px;
  cursor: pointer;
  border: 1px solid #f1f5f9;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  height: 280px; /* 固定高度，保证整齐 */
  display: flex;
  flex-direction: column;
  animation: fadeUp 0.6s ease-out backwards;
}

.company-card-modern:hover {
  transform: translateY(-6px);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
}

/* 装饰背景条 */
.card-bg-decoration {
  height: 60px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  position: absolute;
  top: 0; left: 0; right: 0;
  z-index: 0;
}

.card-body {
  padding: 24px;
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.company-header-block {
  text-align: center;
  margin-bottom: 16px;
}

/* Logo 容器 */
.logo-box {
  width: 64px;
  height: 64px;
  background: white;
  border-radius: 12px;
  margin: 0 auto 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 6px rgba(0,0,0,0.05);
  border: 1px solid #f8fafc;
}

.real-logo {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 12px;
}

.text-logo {
  font-size: 24px;
  font-weight: 800;
  color: #ff6b00;
}

.company-name {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 8px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.company-tags {
  display: flex;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
}

.tag-item {
  font-size: 12px;
  color: #64748b;
  background: #f1f5f9;
  padding: 2px 8px;
  border-radius: 4px;
}

.company-desc {
  font-size: 13px;
  color: #94a3b8;
  text-align: center;
  line-height: 1.6;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  height: 42px; /* 限制高度 */
}

.card-divider {
  margin-top: auto; /* 推到底部 */
  height: 1px;
  background: #f1f5f9;
  margin-bottom: 12px;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footer-stat {
  display: flex;
  flex-direction: column;
}

.stat-num {
  font-size: 18px;
  font-weight: 800;
  color: #ff6b00;
  line-height: 1.2;
}

.stat-label {
  font-size: 12px;
  color: #94a3b8;
}

.view-btn {
  background: white;
  border: 1px solid #e2e8f0;
  color: #64748b;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 4px;
}

.company-card-modern:hover .view-btn {
  background: #ff6b00;
  border-color: #ff6b00;
  color: white;
}

/* ================= 3. 分页 ================= */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 40px;
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

@media (max-width: 768px) {
  .search-wrapper { max-width: 100%; }
  .filter-row { flex-direction: column; }
  .filter-items { margin-top: 8px; }
  .company-card-modern { height: auto; padding-bottom: 20px; }
}
</style>