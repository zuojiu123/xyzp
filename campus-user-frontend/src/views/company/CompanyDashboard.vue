<template>
  <div class="company-dashboard" v-loading="pageLoading">
    <div class="container">
      <header class="page-head">
        <div class="head-text">
          <h1 class="title">{{ companyInfo.name || '企业工作台' }}</h1>
          <p class="sub">管理职位、处理投递、维护企业形象</p>
        </div>
        <div class="head-actions">
          <el-button type="primary" plain icon="el-icon-plus" @click="$router.push('/company/jobs')">发布职位</el-button>
          <el-button icon="el-icon-document" @click="$router.push('/company/applications')">申请管理</el-button>
          <el-button icon="el-icon-office-building" @click="$router.push('/company/profile')">企业资料</el-button>
        </div>
      </header>

      <el-row :gutter="16" class="stats-row">
        <el-col :xs="12" :sm="12" :md="6" v-for="(card, i) in statCards" :key="card.key">
          <div class="stat-card" :class="'accent-' + i">
            <div class="stat-inner">
              <span class="stat-num">{{ card.value }}</span>
              <span class="stat-label">{{ card.label }}</span>
            </div>
            <i :class="[card.icon, 'stat-ico']"></i>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="16" class="chart-row">
        <el-col :xs="24" :lg="14">
          <el-card shadow="never" class="panel-card">
            <div slot="header" class="panel-header">
              <span>近 7 日投递趋势</span>
              <span class="panel-hint">按投递时间统计</span>
            </div>
            <div ref="applicationChart" class="chart-box"></div>
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="10">
          <el-card shadow="never" class="panel-card">
            <div slot="header" class="panel-header">
              <span>投递状态分布</span>
            </div>
            <div ref="statusChart" class="chart-box chart-pie"></div>
          </el-card>
        </el-col>
      </el-row>

      <el-card shadow="never" class="panel-card shortcuts-card">
        <div slot="header" class="panel-header">
          <span>常用入口</span>
        </div>
        <div class="shortcuts">
          <div class="sc-item" @click="$router.push('/company/jobs')">
            <i class="el-icon-suitcase"></i>
            <div>
              <strong>职位管理</strong>
              <p>发布、编辑、上下线职位</p>
            </div>
            <i class="el-icon-arrow-right sc-arrow"></i>
          </div>
          <div class="sc-item" @click="$router.push('/company/applications')">
            <i class="el-icon-user"></i>
            <div>
              <strong>申请管理</strong>
              <p>筛选简历、通过或拒绝候选人</p>
            </div>
            <i class="el-icon-arrow-right sc-arrow"></i>
          </div>
          <div class="sc-item" @click="$router.push('/company/profile')">
            <i class="el-icon-edit-outline"></i>
            <div>
              <strong>企业资料</strong>
              <p>完善介绍与行业信息，提升曝光</p>
            </div>
            <i class="el-icon-arrow-right sc-arrow"></i>
          </div>
          <div v-if="companyInfo.id" class="sc-item" @click="openPublicPage">
            <i class="el-icon-view"></i>
            <div>
              <strong>预览企业主页</strong>
              <p>求职者端看到的公开页面</p>
            </div>
            <i class="el-icon-arrow-right sc-arrow"></i>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'

function toMs(t) {
  if (t == null) return null
  if (typeof t === 'number') return t > 1e12 ? t : t * 1000
  const n = Number(t)
  if (!Number.isNaN(n)) return n > 1e12 ? n : n * 1000
  const d = new Date(t)
  return d.getTime()
}

export default {
  name: 'CompanyDashboard',
  data() {
    return {
      pageLoading: false,
      companyInfo: {},
      stats: {
        totalJobs: 0,
        totalApplications: 0,
        pendingApplications: 0,
        hiredCount: 0,
        rejectedCount: 0
      },
      applications: [],
      applicationChart: null,
      statusChart: null
    }
  },
  computed: {
    statCards() {
      return [
        { key: 'jobs', label: '我的职位', value: this.stats.totalJobs, icon: 'el-icon-suitcase' },
        { key: 'apps', label: '收到申请', value: this.stats.totalApplications, icon: 'el-icon-message' },
        { key: 'pend', label: '待处理', value: this.stats.pendingApplications, icon: 'el-icon-time' },
        { key: 'hire', label: '已录用', value: this.stats.hiredCount, icon: 'el-icon-circle-check' }
      ]
    }
  },
  async mounted() {
    await this.loadData()
    this.$nextTick(() => this.initOrUpdateCharts())
    window.addEventListener('resize', this.onResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.onResize)
    this.disposeCharts()
  },
  methods: {
    onResize() {
      this.applicationChart && this.applicationChart.resize()
      this.statusChart && this.statusChart.resize()
    },
    disposeCharts() {
      if (this.applicationChart) {
        this.applicationChart.dispose()
        this.applicationChart = null
      }
      if (this.statusChart) {
        this.statusChart.dispose()
        this.statusChart = null
      }
    },
    openPublicPage() {
      const route = this.$router.resolve({ name: 'CompanyDetail', params: { id: this.companyInfo.id } })
      window.open(route.href, '_blank')
    },
    async loadData() {
      this.pageLoading = true
      try {
        this.companyInfo = await this.$api.company.getCurrentCompany()
        if (!this.companyInfo || !this.companyInfo.id) {
          this.$message.warning('请先完善企业信息')
          this.$router.push('/company/register')
          return
        }

        const [jobsData, applicationsData] = await Promise.all([
          this.$api.employment.getUserJobs(1, 500),
          this.$api.employmentUser.getCompanyApplications(1, 2000, { companyId: this.companyInfo.id })
        ])

        const jobs = jobsData.list || []
        const applications = applicationsData.list || []
        this.applications = applications

        const isPending = a => a.replyStatus === 'Wait_For_Reply'
        const isHired = a => a.replyStatus === 'Agree_With_Induction'
        const isRejected = a => a.replyStatus === 'Rejected' || a.replyStatus === 'Refused_Entry'

        this.stats = {
          totalJobs: jobs.length,
          totalApplications: applications.length,
          pendingApplications: applications.filter(isPending).length,
          hiredCount: applications.filter(isHired).length,
          rejectedCount: applications.filter(isRejected).length
        }
      } catch (e) {
        console.error(e)
        this.$message.error('加载工作台数据失败')
      } finally {
        this.pageLoading = false
      }
    },
    buildTrendSeries() {
      const labels = []
      const dayKeys = []
      const now = new Date()
      now.setHours(0, 0, 0, 0)
      for (let i = 6; i >= 0; i--) {
        const d = new Date(now)
        d.setDate(d.getDate() - i)
        labels.push(`${d.getMonth() + 1}/${d.getDate()}`)
        dayKeys.push(d.toDateString())
      }
      const counts = Array(7).fill(0)
      this.applications.forEach(a => {
        const ms = toMs(a.createTime)
        if (ms == null || Number.isNaN(ms)) return
        const d = new Date(ms)
        d.setHours(0, 0, 0, 0)
        const idx = dayKeys.indexOf(d.toDateString())
        if (idx >= 0) counts[idx]++
      })
      return { labels, counts }
    },
    initOrUpdateCharts() {
      if (!this.$refs.applicationChart || !this.$refs.statusChart) return

      const brand = '#ff6b00'
      const { labels, counts } = this.buildTrendSeries()

      if (!this.applicationChart) {
        this.applicationChart = echarts.init(this.$refs.applicationChart)
      }
      this.applicationChart.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: labels, boundaryGap: false },
        yAxis: { type: 'value', minInterval: 1 },
        series: [
          {
            name: '投递数',
            type: 'line',
            smooth: true,
            data: counts,
            itemStyle: { color: brand },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(255, 107, 0, 0.25)' },
                { offset: 1, color: 'rgba(255, 107, 0, 0.02)' }
              ])
            }
          }
        ]
      })

      const other = Math.max(
        0,
        this.stats.totalApplications - this.stats.pendingApplications - this.stats.hiredCount - this.stats.rejectedCount
      )
      const pieData = [
        { value: this.stats.pendingApplications, name: '待处理', itemStyle: { color: '#e6a23c' } },
        { value: this.stats.hiredCount, name: '已录用', itemStyle: { color: '#67c23a' } },
        { value: this.stats.rejectedCount, name: '已拒绝', itemStyle: { color: '#f56c6c' } },
        { value: other, name: '其他', itemStyle: { color: '#909399' } }
      ].filter(d => d.value > 0)

      if (!this.statusChart) {
        this.statusChart = echarts.init(this.$refs.statusChart)
      }
      this.statusChart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
        legend: { bottom: 0, left: 'center' },
        series: [
          {
            name: '状态',
            type: 'pie',
            radius: ['42%', '68%'],
            center: ['50%', '46%'],
            itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
            label: { show: true, formatter: '{b}\n{c}' },
            data: pieData.length ? pieData : [{ value: 1, name: '暂无数据', itemStyle: { color: '#e4e7ed' } }]
          }
        ]
      })
    }
  }
}
</script>

<style scoped>
.company-dashboard {
  min-height: calc(100vh - 120px);
  padding: 24px 20px 40px;
  background: #f8fafc;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.page-head {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 24px;
}

.title {
  margin: 0 0 6px;
  font-size: 26px;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.5px;
}

.sub {
  margin: 0;
  color: #64748b;
  font-size: 14px;
}

.head-actions .el-button {
  margin-left: 0;
  margin-right: 8px;
  margin-bottom: 8px;
}

.stats-row {
  margin-bottom: 16px;
}

.stat-card {
  position: relative;
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.06);
  border: 1px solid #f1f5f9;
  overflow: hidden;
  min-height: 108px;
}

.stat-card::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  border-radius: 12px 0 0 12px;
}

.accent-0::before { background: #ff6b00; }
.accent-1::before { background: #3b82f6; }
.accent-2::before { background: #e6a23c; }
.accent-3::before { background: #67c23a; }

.stat-inner {
  position: relative;
  z-index: 1;
}

.stat-num {
  display: block;
  font-size: 28px;
  font-weight: 800;
  color: #0f172a;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #64748b;
  margin-top: 4px;
  display: inline-block;
}

.stat-ico {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 40px;
  color: #ff6b00;
  opacity: 0.12;
}

.accent-1 .stat-ico { color: #3b82f6; }
.accent-2 .stat-ico { color: #e6a23c; }
.accent-3 .stat-ico { color: #67c23a; }

.chart-row {
  margin-bottom: 16px;
}

.panel-card {
  border-radius: 12px;
  border: 1px solid #f1f5f9;
  margin-bottom: 16px;
}

.panel-card >>> .el-card__header {
  padding: 14px 18px;
  border-bottom: 1px solid #f1f5f9;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 700;
  color: #1e293b;
}

.panel-hint {
  font-size: 12px;
  font-weight: 400;
  color: #94a3b8;
}

.chart-box {
  height: 300px;
}

.chart-pie {
  height: 320px;
}

.shortcuts-card .shortcuts {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 12px;
}

.sc-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px;
  border-radius: 10px;
  border: 1px solid #f1f5f9;
  cursor: pointer;
  transition: all 0.2s;
  background: #fafbfc;
}

.sc-item:hover {
  border-color: rgba(255, 107, 0, 0.35);
  background: #fff;
  box-shadow: 0 8px 20px rgba(255, 107, 0, 0.08);
}

.sc-item > .el-icon-suitcase,
.sc-item > .el-icon-user,
.sc-item > .el-icon-edit-outline,
.sc-item > .el-icon-view {
  font-size: 28px;
  color: #ff6b00;
  flex-shrink: 0;
}

.sc-item strong {
  display: block;
  color: #1e293b;
  font-size: 15px;
}

.sc-item p {
  margin: 4px 0 0;
  font-size: 12px;
  color: #94a3b8;
  line-height: 1.4;
}

.sc-arrow {
  margin-left: auto;
  color: #cbd5e1;
  font-size: 14px;
}

@media (max-width: 768px) {
  .head-actions {
    width: 100%;
  }
}
</style>
