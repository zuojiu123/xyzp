<template>
  <div class="job-detail">
    <div class="container">
      <!-- 职位信息卡片 -->
      <el-card class="job-card">
        <div class="job-header">
          <div class="job-title-section">
            <h1>{{ job.title || '职位名称' }}</h1>
            <div class="job-tags">
              <el-tag type="warning" size="small" v-if="job.position">
                <i class="el-icon-location"></i> {{ job.position }}
              </el-tag>
              <el-tag type="info" size="small" v-if="job.education">
                <i class="el-icon-reading"></i> {{ job.education }}
              </el-tag>
              <el-tag size="small" v-if="job.createTime">
                <i class="el-icon-time"></i> {{ formatTime(job.createTime) }}
              </el-tag>
            </div>
          </div>
          <div class="job-salary">
            <span class="salary-amount">{{ job.minSalary }}-{{ job.maxSalary }}K</span>
            <span class="salary-label">月薪</span>
          </div>
        </div>

        <div class="job-description">
          <h3><i class="el-icon-document"></i> 职位描述</h3>
          <div class="description-content">
            {{ job.description || '暂无职位描述' }}
          </div>
        </div>

        <div class="job-treatment" v-if="job.treatment">
          <h3><i class="el-icon-present"></i> 福利待遇</h3>
          <div class="treatment-content">
            {{ job.treatment }}
          </div>
        </div>

        <div class="apply-section">
          <el-button 
            type="warning" 
            size="large" 
            @click="showApplyDialog" 
            :disabled="job.userStatus === 'have'"
            icon="el-icon-check">
            {{ job.userStatus === 'have' ? '已申请' : '立即申请' }}
          </el-button>
          <el-button size="large" @click="$router.go(-1)" icon="el-icon-back">
            返回列表
          </el-button>
        </div>
      </el-card>

      <!-- 公司信息卡片 -->
      <el-card class="company-card" v-if="job.companyModel">
        <div slot="header" class="card-header">
          <i class="el-icon-office-building"></i>
          <span>公司信息</span>
        </div>
        <div class="company-info" @click="viewCompany">
          <div class="company-logo">
            <div class="logo-wrapper">
              <i class="el-icon-office-building"></i>
            </div>
          </div>
          <div class="company-details">
            <h4>{{ job.companyModel.name }}</h4>
            <p class="company-desc">{{ job.companyModel.description || '暂无公司介绍' }}</p>
            <div class="company-meta">
              <span v-if="job.companyModel.number">
                <i class="el-icon-user"></i> {{ job.companyModel.number }}人
              </span>
              <span v-if="job.companyModel.category">
                <i class="el-icon-suitcase"></i> {{ job.companyModel.category }}
              </span>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 申请对话框 -->
    <el-dialog 
      title="申请职位" 
      :visible.sync="applyDialogVisible" 
      width="600px"
      :close-on-click-modal="false">
      <el-form :model="applyForm" :rules="applyRules" ref="applyForm" label-width="100px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="applyForm.name" placeholder="请输入您的真实姓名"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="applyForm.phone" placeholder="请输入您的联系电话"></el-input>
        </el-form-item>
        <el-form-item label="简历附件" prop="resume">
          <el-upload
            class="resume-upload"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :on-success="handleResumeSuccess"
            :on-error="handleResumeError"
            :before-upload="beforeResumeUpload"
            :file-list="resumeFileList"
            :limit="1"
            accept=".pdf,.doc,.docx"
            :on-exceed="handleExceed">
            <el-button size="small" type="primary" icon="el-icon-upload">
              上传简历
            </el-button>
            <div slot="tip" class="el-upload__tip">
              支持PDF、Word格式，文件大小不超过5MB
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="自我介绍" prop="introduce">
          <el-input 
            type="textarea" 
            v-model="applyForm.introduce" 
            :rows="6"
            placeholder="请简单介绍一下自己的工作经历、技能特长等（不少于20字）"
            maxlength="500"
            show-word-limit>
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="applyDialogVisible = false">取 消</el-button>
        <el-button type="warning" @click="submitApplication" :loading="submitting">
          提交申请
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'JobDetail',
  data() {
    return {
      job: {},
      loading: false,
      applyDialogVisible: false,
      submitting: false,
      applyForm: {
        name: '',
        phone: '',
        introduce: '',
        resume: ''
      },
      resumeFileList: [],
      uploadUrl: 'http://localhost:3030/userResume/upload',
      uploadHeaders: {},
      applyRules: {
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        introduce: [
          { required: true, message: '请输入自我介绍', trigger: 'blur' },
          { min: 20, message: '自我介绍不能少于20个字符', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    async loadJobDetail() {
      this.loading = true
      try {
        const jobId = this.$route.params.id
        const jobData = await this.$api.employment.getJobById(jobId)
        this.job = jobData || {}
        console.log('职位详情:', this.job)
      } catch (error) {
        console.error('获取职位详情失败:', error)
        this.$message.error('获取职位详情失败')
      } finally {
        this.loading = false
      }
    },
    
    showApplyDialog() {
      const token = localStorage.getItem('token')
      if (!token) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }

      if (this.job.userStatus === 'have') {
        this.$message.info('您已申请过该职位')
        return
      }
      
      // 预填充用户信息
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      this.applyForm.name = userInfo.realName || userInfo.nickName || ''
      this.applyForm.phone = userInfo.phone || ''
      this.applyForm.introduce = ''
      this.applyForm.resume = ''
      this.resumeFileList = []
      
      // 设置上传请求头
      this.uploadHeaders = {
        'Authorization': `Bearer ${token}`
      }
      
      this.applyDialogVisible = true
    },
    
    submitApplication() {
      this.$refs.applyForm.validate(async (valid) => {
        if (!valid) {
          return false
        }
        
        this.submitting = true
        try {
          const applicationData = {
            employmentId: this.job.id,
            name: this.applyForm.name,
            phone: this.applyForm.phone,
            introduce: this.applyForm.introduce,
            resume: this.applyForm.resume
          }
          
          console.log('提交申请数据:', applicationData)
          await this.$api.employmentUser.applyJob(applicationData)
          
          this.$message.success('申请提交成功，请等待企业回复')
          this.applyDialogVisible = false
          
          // 重新加载职位详情以更新申请状态
          await this.loadJobDetail()
        } catch (error) {
          console.error('申请提交失败:', error)
          this.$message.error(error.message || '申请提交失败，请稍后重试')
        } finally {
          this.submitting = false
        }
      })
    },

    viewCompany() {
      if (this.job.companyModel && this.job.companyModel.id) {
        this.$router.push(`/company/${this.job.companyModel.id}`)
      }
    },

    beforeResumeUpload(file) {
      const isValidType = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'].includes(file.type)
      const isLt5M = file.size / 1024 / 1024 < 5

      if (!isValidType) {
        this.$message.error('简历只能是PDF或Word格式')
        return false
      }
      if (!isLt5M) {
        this.$message.error('简历文件大小不能超过5MB')
        return false
      }
      return true
    },

    handleResumeSuccess(response, file, fileList) {
      if (response.flag) {
        this.applyForm.resume = response.data.id
        this.$message.success('简历上传成功')
      } else {
        this.$message.error(response.msg || '简历上传失败')
        this.resumeFileList = []
      }
    },

    handleResumeError(err, file, fileList) {
      this.$message.error('简历上传失败')
      this.resumeFileList = []
    },

    handleExceed(files, fileList) {
      this.$message.warning('只能上传一个简历文件')
    },

    formatTime(timestamp) {
      const date = new Date(timestamp)
      const now = new Date()
      const diff = now - date
      const days = Math.floor(diff / (1000 * 60 * 60 * 24))
      
      if (days === 0) return '今天发布'
      if (days === 1) return '昨天发布'
      if (days < 7) return `${days}天前发布`
      return date.toLocaleDateString()
    }
  },
  
  async mounted() {
    await this.loadJobDetail()
  }
}
</script>

<style scoped>
.job-detail {
  min-height: 100vh;
  background: linear-gradient(135deg, #fff5eb 0%, #ffe8d6 100%);
  padding: 30px 0;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

.job-card {
  margin-bottom: 25px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(255, 152, 0, 0.1);
  animation: fadeInUp 0.6s ease-out;
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

.job-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 30px;
  background: linear-gradient(135deg, #fff5eb, #ffe8d6);
  border-bottom: 2px solid #ff9800;
}

.job-title-section h1 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 28px;
  font-weight: 600;
}

.job-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.job-tags .el-tag {
  border-radius: 12px;
  padding: 6px 12px;
}

.job-tags .el-tag i {
  margin-right: 4px;
}

.job-salary {
  text-align: right;
  padding: 15px 25px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(255, 152, 0, 0.15);
}

.salary-amount {
  display: block;
  font-size: 32px;
  color: #ff9800;
  font-weight: bold;
  line-height: 1.2;
}

.salary-label {
  display: block;
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}

.job-description, .job-treatment {
  padding: 30px;
  border-bottom: 1px solid #fff5eb;
}

.job-description h3, .job-treatment h3 {
  color: #333;
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.job-description h3 i, .job-treatment h3 i {
  color: #ff9800;
  font-size: 20px;
}

.description-content, .treatment-content {
  line-height: 1.8;
  color: #666;
  font-size: 15px;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.apply-section {
  text-align: center;
  padding: 30px;
  display: flex;
  justify-content: center;
  gap: 15px;
}

.apply-section .el-button {
  min-width: 150px;
  border-radius: 24px;
  font-size: 16px;
  padding: 12px 30px;
}

.company-card {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(255, 152, 0, 0.08);
  animation: fadeInUp 0.6s ease-out 0.2s both;
}

.card-header {
  font-weight: 600;
  font-size: 16px;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-header i {
  color: #ff9800;
  font-size: 18px;
}

.company-card >>> .el-card__header {
  background: linear-gradient(135deg, #fff5eb, #ffe8d6);
  border-bottom: 2px solid #ff9800;
}

.company-info {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  cursor: pointer;
  padding: 10px;
  border-radius: 12px;
  transition: all 0.3s;
}

.company-info:hover {
  background: linear-gradient(135deg, #fff5eb, #ffe8d6);
  transform: translateX(5px);
}

.logo-wrapper {
  width: 80px;
  height: 80px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #ff9800, #ffb74d);
  box-shadow: 0 4px 12px rgba(255, 152, 0, 0.15);
  color: white;
  font-size: 36px;
}

.company-details {
  flex: 1;
}

.company-details h4 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.company-desc {
  color: #666;
  line-height: 1.6;
  margin-bottom: 12px;
  font-size: 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.company-meta {
  display: flex;
  gap: 20px;
  color: #666;
  font-size: 14px;
}

.company-meta span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.company-meta i {
  color: #ff9800;
}

/* 对话框样式 */
.el-dialog >>> .el-dialog__header {
  background: linear-gradient(135deg, #fff5eb, #ffe8d6);
  border-bottom: 2px solid #ff9800;
}

.el-dialog >>> .el-dialog__title {
  color: #333;
  font-weight: 600;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.dialog-footer .el-button {
  min-width: 100px;
  border-radius: 20px;
}

.resume-upload >>> .el-upload {
  width: 100%;
}

.resume-upload >>> .el-upload-list {
  margin-top: 10px;
}

.resume-upload >>> .el-upload__tip {
  color: #999;
  font-size: 12px;
  margin-top: 5px;
}
</style>