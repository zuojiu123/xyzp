<template>
  <div class="dashboard">
    <div class="welcome-section">
      <div class="welcome-content">
        <h1 class="welcome-title">欢迎回来，管理员</h1>
        <p class="welcome-subtitle">今天是 {{ currentDate }}，系统运行正常</p>
      </div>
      <div class="weather-info">
        <i class="el-icon-data-line" />
        <span>数据已同步</span>
      </div>
    </div>

    <el-row :gutter="24" class="stats-row">
      <el-col :span="6">
        <div class="stat-card user-card">
          <div class="stat-icon">
            <i class="el-icon-user" />
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ userCount }}</div>
            <div class="stat-label">用户总数</div>
            <div class="stat-trend">实时统计</div>
          </div>
          <div class="stat-chart">
            <div class="mini-chart user-chart" />
          </div>
        </div>
      </el-col>

      <el-col :span="6">
        <div class="stat-card company-card">
          <div class="stat-icon">
            <i class="el-icon-office-building" />
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ companyCount }}</div>
            <div class="stat-label">企业总数</div>
            <div class="stat-trend">实时统计</div>
          </div>
          <div class="stat-chart">
            <div class="mini-chart company-chart" />
          </div>
        </div>
      </el-col>

      <el-col :span="6">
        <div class="stat-card job-card">
          <div class="stat-icon">
            <i class="el-icon-suitcase" />
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ jobCount }}</div>
            <div class="stat-label">职位总数</div>
            <div class="stat-trend">实时统计</div>
          </div>
          <div class="stat-chart">
            <div class="mini-chart job-chart" />
          </div>
        </div>
      </el-col>

      <el-col :span="6">
        <div class="stat-card resume-card">
          <div class="stat-icon">
            <i class="el-icon-document" />
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ resumeCount }}</div>
            <div class="stat-label">简历投递</div>
            <div class="stat-trend">实时统计</div>
          </div>
          <div class="stat-chart">
            <div class="mini-chart resume-chart" />
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="dashboard-row">
      <el-col :span="12">
        <el-card class="panel-card">
          <div slot="header">
            <span>数据趋势</span>
          </div>
          <div id="trendChart" style="height: 300px;" />
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="panel-card">
          <div slot="header">
            <span>数据分布</span>
          </div>
          <div id="pieChart" style="height: 300px;" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="dashboard-row">
      <el-col :span="12">
        <el-card class="panel-card">
          <div slot="header">
            <span>快速操作</span>
          </div>
          <div class="quick-actions">
            <el-button type="primary" icon="el-icon-user" @click="$router.push('/user/index')">
              用户管理
            </el-button>
            <el-button type="success" icon="el-icon-office-building" @click="$router.push('/company/index')">
              公司管理
            </el-button>
            <el-button type="warning" icon="el-icon-suitcase" @click="$router.push('/employment/index')">
              职位管理
            </el-button>
            <el-button type="info" icon="el-icon-document" @click="$router.push('/resume/index')">
              简历投递管理
            </el-button>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="panel-card">
          <div slot="header">
            <span>系统信息</span>
          </div>
          <div class="system-info">
            <p><strong>系统名称：</strong>校园招聘管理系统</p>
            <p><strong>版本：</strong>v1.0.0</p>
            <p><strong>技术栈：</strong>Vue 2.6 + Element UI + ECharts</p>
            <p><strong>后端：</strong>Spring Boot + MySQL</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row class="dashboard-row">
      <el-col :span="24">
        <el-card class="panel-card">
          <div slot="header">
            <span>待处理事项</span>
          </div>
          <el-table :data="pendingTasks" style="width: 100%">
            <el-table-column prop="type" label="类型" width="120">
              <template slot-scope="scope">
                <el-tag :type="scope.row.tagType">
                  {{ scope.row.type }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="count" label="数量" width="100">
              <template slot-scope="scope">
                <span :class="{ 'text-muted': scope.row.count === 0 }">{{ scope.row.count }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template slot-scope="scope">
                <el-button size="mini" type="primary" :disabled="scope.row.count === 0" @click="handleTask(scope.row)">
                  去处理
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getDashboardStatistics } from '@/api/statistics'

export default {
  name: 'Dashboard',
  data() {
    return {
      userCount: 0,
      companyCount: 0,
      jobCount: 0,
      resumeCount: 0,
      currentDate: new Date().toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        weekday: 'long'
      }),
      pendingTasks: [],
      trendChart: null,
      pieChart: null
    }
  },
  mounted() {
    this.loadStatistics()
    this.initCharts()
  },
  beforeDestroy() {
    if (this.trendChart) {
      this.trendChart.dispose()
    }
    if (this.pieChart) {
      this.pieChart.dispose()
    }
  },
  methods: {
    async loadStatistics() {
      try {
        const data = await getDashboardStatistics()
        this.userCount = data.userCount || 0
        this.companyCount = data.companyCount || 0
        this.jobCount = data.jobCount || 0
        this.resumeCount = data.applicationCount || 0
        this.updatePendingTasks(data)
        this.updateCharts()
      } catch (e) {
        const msg = (e && e.message) || '加载统计数据失败'
        this.$message.error(msg)
        this.pendingTasks = []
      }
    },

    updatePendingTasks(data) {
      const pendingCompany = (data && data.pendingCompanyCount) || 0
      const pendingJob = (data && data.pendingEmploymentCount) || 0
      const pendingApply = (data && data.pendingApplicationCount) || 0
      this.pendingTasks = [
        {
          type: '企业审核',
          title: '待审核企业',
          count: pendingCompany,
          tagType: 'warning',
          route: '/company/index',
          query: { status: 'Check_Pending' }
        },
        {
          type: '职位审核',
          title: '待审核职位',
          count: pendingJob,
          tagType: 'info',
          route: '/employment/index',
          query: { status: '0' }
        },
        {
          type: '投递处理',
          title: '待处理投递',
          count: pendingApply,
          tagType: 'success',
          route: '/resume/index',
          query: { replyStatus: 'Wait_For_Reply' }
        }
      ]
    },

    handleTask(task) {
      if (task.query && Object.keys(task.query).length) {
        this.$router.push({ path: task.route, query: task.query })
      } else {
        this.$router.push(task.route)
      }
    },

    initCharts() {
      this.initTrendChart()
      this.initPieChart()
    },

    initTrendChart() {
      this.trendChart = echarts.init(document.getElementById('trendChart'))
      const userStyle = { color: '#ff7a18' }
      const companyStyle = { color: '#2f80ed' }
      const jobStyle = { color: '#36cfc9' }
      const resumeStyle = { color: '#67c23a' }

      const option = {
        title: {
          text: '近 7 天数据趋势（示意）',
          subtext: '后端暂未提供按日统计，仅作版面展示',
          textStyle: {
            fontSize: 14,
            color: '#1f2a44'
          }
        },
        tooltip: { trigger: 'axis' },
        legend: { data: ['用户注册', '企业注册', '职位发布', '简历投递'] },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
        },
        yAxis: { type: 'value' },
        series: [
          {
            name: '用户注册',
            type: 'line',
            stack: 'Total',
            data: [12, 13, 10, 13, 9, 23, 21],
            smooth: true,
            itemStyle: userStyle
          },
          {
            name: '企业注册',
            type: 'line',
            stack: 'Total',
            data: [2, 2, 3, 5, 3, 3, 6],
            smooth: true,
            itemStyle: companyStyle
          },
          {
            name: '职位发布',
            type: 'line',
            stack: 'Total',
            data: [15, 23, 24, 21, 19, 14, 16],
            smooth: true,
            itemStyle: jobStyle
          },
          {
            name: '简历投递',
            type: 'line',
            stack: 'Total',
            data: [32, 33, 30, 33, 29, 43, 41],
            smooth: true,
            itemStyle: resumeStyle
          }
        ]
      }
      this.trendChart.setOption(option)
    },

    initPieChart() {
      this.pieChart = echarts.init(document.getElementById('pieChart'))
      const userStyle = { color: '#ff7a18' }
      const companyStyle = { color: '#2f80ed' }
      const jobStyle = { color: '#36cfc9' }
      const resumeStyle = { color: '#67c23a' }

      const option = {
        title: {
          text: '数据分布（实时）',
          textStyle: {
            fontSize: 14,
            color: '#1f2a44'
          }
        },
        tooltip: { trigger: 'item' },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '数据分布',
            type: 'pie',
            radius: '50%',
            data: [
              { value: this.userCount, name: '用户', itemStyle: userStyle },
              { value: this.companyCount, name: '企业', itemStyle: companyStyle },
              { value: this.jobCount, name: '职位', itemStyle: jobStyle },
              { value: this.resumeCount, name: '简历', itemStyle: resumeStyle }
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }
      this.pieChart.setOption(option)
    },

    updateCharts() {
      if (this.pieChart) {
        const userStyle = { color: '#ff7a18' }
        const companyStyle = { color: '#2f80ed' }
        const jobStyle = { color: '#36cfc9' }
        const resumeStyle = { color: '#67c23a' }

        const option = {
          series: [
            {
              data: [
                { value: this.userCount, name: '用户', itemStyle: userStyle },
                { value: this.companyCount, name: '企业', itemStyle: companyStyle },
                { value: this.jobCount, name: '职位', itemStyle: jobStyle },
                { value: this.resumeCount, name: '简历', itemStyle: resumeStyle }
              ]
            }
          ]
        }
        this.pieChart.setOption(option)
      }
    }
  }
}
</script>

<style scoped lang="scss">
.dashboard {
  min-height: 100vh;
  padding: 24px;
  background: transparent;
}

.dashboard-row {
  margin-top: 20px;
}

.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 34px 36px;
  border-radius: 24px;
  color: white;
  background:
    radial-gradient(circle at right top, rgba(255, 255, 255, 0.22), transparent 24%),
    linear-gradient(135deg, #14213d 0%, #1c3560 58%, #2f80ed 100%);
  box-shadow: 0 24px 50px rgba(20, 33, 61, 0.22);
}

.welcome-title {
  margin: 0 0 8px;
  font-size: 34px;
  font-weight: 700;
}

.welcome-subtitle {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
}

.weather-info {
  display: flex;
  align-items: center;
  padding: 14px 18px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.08);
  font-size: 18px;

  i {
    margin-right: 8px;
    font-size: 24px;
  }
}

.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  position: relative;
  overflow: hidden;
  padding: 24px;
  border: 1px solid #e7edf6;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 16px 36px rgba(20, 33, 61, 0.08);
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 24px 48px rgba(20, 33, 61, 0.12);
  }
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  margin-bottom: 16px;
  border-radius: 16px;

  i {
    font-size: 28px;
    color: white;
  }
}

.stat-content {
  .stat-number {
    margin-bottom: 8px;
    color: #1f2a44;
    font-size: 36px;
    font-weight: 700;
    line-height: 1;
  }

  .stat-label {
    margin-bottom: 8px;
    color: #7b879c;
    font-size: 14px;
  }

  .stat-trend {
    color: #ff7a18;
    font-size: 12px;
    font-weight: 500;
  }
}

.stat-chart {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  width: 60px;
  height: 40px;
}

.mini-chart {
  width: 100%;
  height: 100%;
  border-radius: 8px;
  opacity: 0.1;
}

.user-card .stat-icon,
.user-chart {
  background: linear-gradient(135deg, #ff7a18, #ffb347);
}

.company-card .stat-icon,
.company-chart {
  background: linear-gradient(135deg, #2f80ed, #56ccf2);
}

.job-card .stat-icon,
.job-chart {
  background: linear-gradient(135deg, #36cfc9, #5cdbd3);
}

.resume-card .stat-icon,
.resume-chart {
  background: linear-gradient(135deg, #67c23a, #95de64);
}

.panel-card {
  overflow: hidden;
  border: 1px solid #e7edf6;
  border-radius: 20px;
}

.panel-card ::v-deep .el-card__header {
  background: #f9fbff;
  border-bottom: 1px solid #e7edf6;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;

  .el-button {
    margin: 0;
    min-width: 140px;
  }
}

.system-info p {
  margin: 10px 0;
  color: #5f6f86;
}

.text-muted {
  color: #c0c4cc;
}
</style>
