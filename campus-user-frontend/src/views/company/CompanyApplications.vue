<template>
  <div class="company-applications">
    <div class="container">
      <div class="header">
        <h2>申请管理</h2>
        <el-select v-model="filters.replyStatus" placeholder="筛选状态" clearable @change="loadApplications" style="width: 200px">
          <el-option label="等待回复" value="Wait_For_Reply"></el-option>
          <el-option label="同意入职" value="Agree_With_Induction"></el-option>
          <el-option label="已拒绝" value="Rejected"></el-option>
        </el-select>
      </div>

      <el-card>
        <el-table :data="applicationList" border v-loading="loading">
          <el-table-column prop="name" label="姓名" width="120"></el-table-column>
          <el-table-column prop="phone" label="联系电话" width="130"></el-table-column>
          <el-table-column prop="jobTitle" label="申请职位" width="200"></el-table-column>
          <el-table-column prop="introduce" label="自我介绍" show-overflow-tooltip></el-table-column>
          <el-table-column label="简历" width="120">
            <template slot-scope="scope">
              <el-button v-if="scope.row.userResume" size="mini" type="info" @click="viewResume(scope.row.userResume)">
                查看简历
              </el-button>
              <span v-else class="no-resume">未上传</span>
            </template>
          </el-table-column>
          <el-table-column label="投递时间" width="180">
            <template slot-scope="scope">
              {{ formatTime(scope.row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="replyStatus" label="状态" width="120">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.replyStatus)">
                {{ getStatusText(scope.row.replyStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template slot-scope="scope">
              <el-button size="mini" @click="viewDetail(scope.row)">查看</el-button>
              <el-button v-if="scope.row.replyStatus === 'Wait_For_Reply'" size="mini" type="success" @click="handleApplication(scope.row, 'Agree_With_Induction')">
                通过
              </el-button>
              <el-button v-if="scope.row.replyStatus === 'Wait_For_Reply'" size="mini" type="danger" @click="handleApplication(scope.row, 'Rejected')">
                拒绝
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination">
          <el-pagination
            @current-change="handlePageChange"
            :current-page="currentPage"
            :page-size="pageSize"
            layout="total, prev, pager, next"
            :total="total">
          </el-pagination>
        </div>
      </el-card>

      <!-- 详情对话框 -->
      <el-dialog title="申请详情" :visible.sync="detailVisible" width="600px">
        <div v-if="currentApplication">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="姓名">{{ currentApplication.name }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ currentApplication.phone }}</el-descriptions-item>
            <el-descriptions-item label="申请职位" :span="2">{{ currentApplication.jobTitle }}</el-descriptions-item>
            <el-descriptions-item label="投递时间" :span="2">
              {{ formatTime(currentApplication.createTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="自我介绍" :span="2">
              <div class="introduce-content">{{ currentApplication.introduce }}</div>
            </el-descriptions-item>
          </el-descriptions>
          
          <div class="reply-section">
            <h4>回复</h4>
            <el-input type="textarea" v-model="replyContent" :rows="3" placeholder="请输入回复内容..."></el-input>
            <div class="reply-actions">
              <el-button type="success" @click="replyApplication('Agree_With_Induction')" :loading="replying">
                通过并回复
              </el-button>
              <el-button type="danger" @click="replyApplication('Rejected')" :loading="replying">
                拒绝并回复
              </el-button>
            </div>
          </div>
        </div>
      </el-dialog>

      <!-- PDF简历查看对話框 -->
      <el-dialog title="简历查看" :visible.sync="resumeDialogVisible" width="80%" top="5vh">
        <div class="resume-container">
          <iframe v-if="currentResumeUrl" :src="currentResumeUrl" width="100%" height="600px" frameborder="0"></iframe>
          <div v-else-if="currentResume && !currentResumeUrl" class="download-prompt">
            <i class="el-icon-download"></i>
            <h3>不支持在线预览</h3>
            <p>该简历格式 ({{ currentResume.extension }}) 不支持在线预览</p>
            <p>请下载后使用相应软件打开</p>
            <el-button type="primary" size="large" @click="downloadResume">
              <i class="el-icon-download"></i>
              下载简历
            </el-button>
          </div>
          <div v-else class="no-resume-content">
            <i class="el-icon-document"></i>
            <p>无法预览该简历</p>
          </div>
        </div>
        <div slot="footer" class="dialog-footer">
          <el-button @click="resumeDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="downloadResume" v-if="currentResume">下载简历</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CompanyApplications',
  data() {
    return {
      applicationList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      filters: {
        replyStatus: ''
      },
      detailVisible: false,
      currentApplication: null,
      replyContent: '',
      replying: false,
      resumeDialogVisible: false,
      currentResumeUrl: '',
      currentResume: null
    }
  },
  async mounted() {
    await this.loadApplications()
  },
  methods: {
    async loadApplications() {
      this.loading = true
      try {
        const company = await this.$api.company.getCurrentCompany()
        if (!company || !company.id) {
          this.$message.warning('请先完善企业信息')
          this.$router.push('/company/register')
          return
        }
        
        const params = { companyId: company.id }
        if (this.filters.replyStatus) {
          params.replyStatus = this.filters.replyStatus
        }
        
        const data = await this.$api.employmentUser.getCompanyApplications(this.currentPage, this.pageSize, params)
        this.applicationList = (data.list || []).map(app => ({
          ...app,
          jobTitle: app.employmentModel ? app.employmentModel.title : '未知职位'
        }))
        this.total = data.total || 0
      } catch (error) {
        this.$message.error('获取申请列表失败')
      } finally {
        this.loading = false
      }
    },
    
    handlePageChange(page) {
      this.currentPage = page
      this.loadApplications()
    },
    
    viewDetail(application) {
      this.currentApplication = application
      this.replyContent = application.replyContent || ''
      this.detailVisible = true
    },
    
    async handleApplication(application, status) {
      try {
        const action = status === 'Agree_With_Induction' ? '通过' : '拒绝'
        await this.$confirm(`确定要${action}这份申请吗？`, '提示', { type: 'warning' })
        
        await this.$api.employmentUser.updateApplication({
          id: application.id,
          replyStatus: status,
          replyContent: status === 'Agree_With_Induction' ? '恭喜您通过了初步筛选，请保持电话畅通' : '很遗憾，您的申请未通过筛选'
        })
        
        this.$message.success(`${action}成功`)
        await this.loadApplications()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('操作失败')
        }
      }
    },
    
    async replyApplication(status) {
      if (!this.replyContent.trim()) {
        this.$message.warning('请输入回复内容')
        return
      }
      
      this.replying = true
      try {
        await this.$api.employmentUser.updateApplication({
          id: this.currentApplication.id,
          replyStatus: status,
          replyContent: this.replyContent
        })
        
        this.$message.success('回复成功')
        this.detailVisible = false
        await this.loadApplications()
      } catch (error) {
        this.$message.error('回复失败')
      } finally {
        this.replying = false
      }
    },
    
    getStatusType(replyStatus) {
      const types = {
        'Wait_For_Reply': 'warning',
        'Agree_With_Induction': 'success',
        'Rejected': 'danger'
      }
      return types[replyStatus] || 'info'
    },
    
    getStatusText(replyStatus) {
      const texts = {
        'Wait_For_Reply': '等待回复',
        'Agree_With_Induction': '同意入职',
        'Rejected': '已拒绝'
      }
      return texts[replyStatus] || '未知'
    },
    
    formatTime(timestamp) {
      if (!timestamp) return ''
      return new Date(timestamp).toLocaleString()
    },
    
    // 查看简历
    viewResume(resume) {
      if (!resume || !resume.resumeUrl) {
        this.$message.warning('该申请者未上传简历')
        return
      }
      
      this.currentResume = resume
      
      if (resume.extension === '.pdf') {
        // PDF文件直接在iframe中显示
        this.currentResumeUrl = resume.resumeUrl
        this.resumeDialogVisible = true
      } else if (resume.extension === '.doc' || resume.extension === '.docx') {
        // Word文档使用在线预览服务
        const previewUrl = `https://view.officeapps.live.com/op/embed.aspx?src=${encodeURIComponent(resume.resumeUrl)}`
        this.currentResumeUrl = previewUrl
        this.resumeDialogVisible = true
      } else {
        // 不支持的格式，显示下载选项
        this.$confirm(`该简历格式 (${resume.extension}) 不支持在线预览，是否下载查看？`, '提示', {
          confirmButtonText: '下载',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.downloadResume()
        }).catch(() => {
          // 用户取消
        })
      }
    },
    
    // 下载简历
    downloadResume() {
      if (!this.currentResume) return
      
      const link = document.createElement('a')
      link.href = this.currentResume.resumeUrl
      link.download = this.currentResume.resumeName || '简历.pdf'
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
    }
  }
}
</script>

<style scoped>
.company-applications {
  padding: 20px;
  background: #f5f5f5;
  min-height: calc(100vh - 120px);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

h2 {
  margin: 0;
  color: #333;
}

.pagination {
  text-align: center;
  margin-top: 20px;
}

.introduce-content {
  max-height: 100px;
  overflow-y: auto;
  line-height: 1.6;
}

.reply-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.reply-section h4 {
  margin-bottom: 10px;
  color: #333;
}

.reply-actions {
  margin-top: 10px;
  text-align: right;
}

.reply-actions .el-button {
  margin-left: 10px;
}

.no-resume {
  color: #999;
  font-size: 12px;
}

.resume-container {
  width: 100%;
  height: 600px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.no-resume-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;
}

.no-resume-content i {
  font-size: 48px;
  margin-bottom: 16px;
}

.no-resume-content p {
  font-size: 16px;
  margin: 0;
}

.download-prompt {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #606266;
  text-align: center;
}

.download-prompt i {
  font-size: 64px;
  color: #409eff;
  margin-bottom: 20px;
}

.download-prompt h3 {
  font-size: 20px;
  margin: 0 0 16px 0;
  color: #303133;
}

.download-prompt p {
  font-size: 14px;
  margin: 8px 0;
  color: #909399;
}

.download-prompt .el-button {
  margin-top: 20px;
}
</style>
