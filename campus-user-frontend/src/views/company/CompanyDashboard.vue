<template>
  <div class="company-dashboard">
    <div class="container">
      <h2>欢迎回来，{{ companyInfo.name || '企业用户' }}</h2>
      
      <!-- 统计卡片 -->
      <el-row :gutter="20" class="stats-row">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ stats.totalJobs }}</div>
              <div class="stat-label">发布职位</div>
            </div>
            <i class="el-icon-suitcase stat-icon"></i>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ stats.totalApplications }}</div>
              <div class="stat-label">收到申请</div>
            </div>
            <i class="el-icon-user stat-icon"></i>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ stats.pendingApplications }}</div>
              <div class="stat-label">待处理</div>
            </div>
            <i class="el-icon-time stat-icon"></i>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ stats.hiredCount }}</div>
              <div class="stat-label">已录用</div>
            </div>
            <i class="el-icon-check stat-icon"></i>
          </el-card>
        </el-col>
      </el-row>

      <!-- 图表区域 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card>
            <div slot="header">申请趋势</div>
            <div ref="applicationChart" style="height: 300px;"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <div slot="header">申请状态分布</div>
            <div ref="statusChart" style="height: 300px;"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'CompanyDashboard',
  data() {
    return {
      companyInfo: {},
      stats: {
        totalJobs: 0,
        totalApplications: 0,
        pendingApplications: 0,
        hiredCount: 0
      },
      applicationChart: null,
      statusChart: null
    }
  },
  async mounted() {
    await this.loadData()
    this.initCharts()
  },
  beforeDestroy() {
    if (this.applicationChart) {
      this.applicationChart.dispose()
    }
    if (this.statusChart) {
      this.statusChart.dispose()
    }
  },
  methods: {
    async loadData() {
      try {
        this.companyInfo = await this.$api.company.getCurrentCompany()
        
        if (!this.companyInfo || !this.companyInfo.id) {
          this.$message.warning('请先完善企业信息')
          this.$router.push('/company/register')
          return
        }
        
        // 获取职位数据
        const jobsData = await this.$api.employment.getUserJobs(1, 100)
        const jobs = jobsData.list || []
        
        // 获取申请数据
        const applicationsData = await this.$api.employmentUser.getCompanyApplications(1, 100, {
          companyId: this.companyInfo.id
        })
        const applications = applicationsData.list || []
        
        // 统计数据
        this.stats = {
          totalJobs: jobs.length,
          totalApplications: applications.length,
          pendingApplications: applications.filter(a => a.replyStatus === 'Wait_For_Reply').length,
          hiredCount: applications.filter(a => a.replyStatus === 'Agree_With_Induction').length
        }
      } catch (error) {
        console.error('加载数据失败:', error)
      }
    },
    
    initCharts() {
      this.initApplicationChart()
      this.initStatusChart()
    },
    
    initApplicationChart() {
      this.applicationChart = echarts.init(this.$refs.applicationChart)
      
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '申请数',
            type: 'line',
            smooth: true,
            data: [12, 18, 15, 22, 28, 16, 10],
            itemStyle: {
              color: '#409EFF'
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
                { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
              ])
            }
          }
        ]
      }
      
      this.applicationChart.setOption(option)
    },
    
    initStatusChart() {
      this.statusChart = echarts.init(this.$refs.statusChart)
      
      const option = {
        tooltip: {
          trigger: 'item'
        },
        legend: {
          bottom: '5%',
          left: 'center'
        },
        series: [
          {
            name: '申请状态',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 20,
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: [
              { value: this.stats.pendingApplications, name: '待处理', itemStyle: { color: '#E6A23C' } },
              { value: this.stats.hiredCount, name: '已录用', itemStyle: { color: '#67C23A' } },
              { 
                value: this.stats.totalApplications - this.stats.pendingApplications - this.stats.hiredCount, 
                name: '已拒绝', 
                itemStyle: { color: '#F56C6C' } 
              }
            ]
          }
        ]
      }
      
      this.statusChart.setOption(option)
    }
  }
}
</script>

<style scoped>
.company-dashboard {
  padding: 20px;
  background: #f5f5f5;
  min-height: calc(100vh - 120px);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

h2 {
  margin-bottom: 20px;
  color: #333;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  position: relative;
  overflow: hidden;
}

.stat-card .el-card__body {
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 5px;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

.stat-icon {
  font-size: 48px;
  color: #409EFF;
  opacity: 0.3;
}
</style>
