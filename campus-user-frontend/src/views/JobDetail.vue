<template>
  <div class="job-detail-page" v-loading="detailLoading">
    <!-- 1. 顶部职位信息栏 (Header) -->
    <div class="job-header-card">
      <div class="container header-container">
        <div class="header-left">
          <div class="title-row">
            <h1 class="job-title">{{ job.title || '职位名称' }}</h1>
            <span class="salary-highlight">{{ job.minSalary }}-{{ job.maxSalary }}K</span>
          </div>

          <div class="meta-row">
            <span class="meta-tag" v-if="job.position"><i class="el-icon-location-outline"></i> {{ job.position }}</span>
            <span class="meta-tag" v-if="job.education"><i class="el-icon-reading"></i> {{ job.education }}</span>
            <span class="meta-tag" v-if="job.experience"><i class="el-icon-suitcase"></i> {{ job.experience }}</span>
            <span class="publish-time">发布于 {{ formatTime(job.createTime) }}</span>
          </div>
        </div>

        <div class="header-right">
          <el-button
              type="primary"
              class="apply-btn-lg"
              @click="showApplyDialog"
              :disabled="job.userStatus === 'have'">
            {{ job.userStatus === 'have' ? '已投递' : '立即沟通' }}
          </el-button>
          <div class="action-links">
            <span class="action-link" @click="handleCollect" :loading="collectLoading">
              <i :class="isCollected ? 'el-icon-star-on' : 'el-icon-star-off'"></i> 
              {{ isCollected ? '已收藏' : '收藏' }}
            </span>
            <span class="action-link" @click="handleShare">
              <i class="el-icon-share"></i> 分享
            </span>
          </div>
        </div>
      </div>
    </div>

    <div class="container content-container">
      <el-row :gutter="24">
        <!-- 2. 左侧：职位详情 -->
        <el-col :span="16" :xs="24">
          <div class="content-card">
            <div class="section-title">职位描述</div>

            <!-- 职位标签/福利 -->
            <div class="welfare-tags" v-if="job.treatment">
              <span v-for="(tag, index) in parseTreatment(job.treatment)" :key="index" class="welfare-item">{{ tag }}</span>
            </div>

            <div class="job-desc-text">
              <div class="desc-content">{{ job.description || '暂无详细描述...' }}</div>
            </div>

            <div class="divider"></div>

            <div class="section-title">工作地点</div>
            <div class="work-location">
              <i class="el-icon-location-information"></i>
              <span>{{ job.position || '地点未填写' }}</span>
            </div>
          </div>
        </el-col>

        <!-- 3. 右侧：公司信息 -->
        <el-col :span="8" :xs="24">
          <div class="side-card company-card" v-if="job.companyModel">
            <div class="company-header" @click="viewCompany">
              <img v-if="job.companyModel.companyLogo" :src="job.companyModel.companyLogo" class="company-logo-img">
              <div v-else class="company-logo-text">
                {{ job.companyModel.name ? job.companyModel.name.substring(0, 1) : '企' }}
              </div>

              <div class="company-basic">
                <h3 class="company-name">{{ job.companyModel.name }}</h3>
                <div class="company-industry">{{ job.companyModel.industry || '互联网' }}</div>
              </div>
            </div>

            <div class="company-meta-list">
              <div class="meta-item">
                <i class="el-icon-data-line"></i>
                <span>{{ job.companyModel.number || '规模未知' }}</span>
              </div>
              <div class="meta-item">
                <i class="el-icon-s-check"></i>
                <span>{{ job.companyModel.category || '私营企业' }}</span>
              </div>
            </div>

            <div class="divider-mini"></div>

            <div class="company-desc-mini">
              {{ job.companyModel.description || '暂无介绍' }}
            </div>

            <el-button class="view-company-btn" @click="viewCompany">查看公司主页</el-button>
          </div>

          <!-- 相似职位推荐 (占位) -->
          <!-- <div class="side-card"> ... </div> -->
        </el-col>
      </el-row>
    </div>

    <!-- 申请弹窗 -->
    <el-dialog
        title="投递简历"
        :visible.sync="applyDialogVisible"
        width="520px"
        custom-class="apply-dialog"
        :close-on-click-modal="false">

      <div class="dialog-job-info">
        <span class="dialog-job-name">申请职位：{{ job.title }}</span>
        <span class="dialog-salary">{{ job.minSalary }}-{{ job.maxSalary }}K</span>
      </div>

      <el-form :model="applyForm" :rules="applyRules" ref="applyForm" label-position="top">
        <div class="form-row">
          <el-form-item label="姓名" prop="name" class="half-width">
            <el-input v-model="applyForm.name" placeholder="您的姓名"></el-input>
          </el-form-item>
          <el-form-item label="联系电话" prop="phone" class="half-width">
            <el-input v-model="applyForm.phone" placeholder="手机号码"></el-input>
          </el-form-item>
        </div>

        <el-form-item label="我的优势" prop="introduce">
          <el-input
              type="textarea"
              v-model="applyForm.introduce"
              :rows="4"
              placeholder="简要介绍您的优势，增加面试机会..."
              maxlength="200"
              show-word-limit>
          </el-input>
        </el-form-item>

        <el-form-item label="附件简历" prop="resume">
          <el-upload
              class="resume-uploader"
              drag
              :action="resumeUploadAction"
              :headers="uploadHeaders"
              :on-success="handleResumeSuccess"
              :on-error="handleResumeError"
              :before-upload="beforeResumeUpload"
              :file-list="resumeFileList"
              :limit="1"
              accept=".pdf,.doc,.docx"
              :on-exceed="handleExceed">
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将简历拖到此处，或<em>点击上传</em></div>
            <div class="el-upload__tip" slot="tip">支持 PDF/Word，大小不超过 5MB</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="applyDialogVisible = false" plain>取消</el-button>
        <el-button type="primary" @click="submitApplication" :loading="submitting">确认投递</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// 逻辑部分保持不变
export default {
  name: 'JobDetail',
  data() {
    return {
      job: {},
      detailLoading: false,
      applyDialogVisible: false,
      submitting: false,
      collectLoading: false,
      isCollected: false,
      applyForm: {
        name: '',
        phone: '',
        introduce: '',
        resume: ''
      },
      resumeFileList: [],
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
          { required: true, message: '请输入自我介绍', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    resumeUploadAction() {
      if (process.env.VUE_APP_UPLOAD_URL) return process.env.VUE_APP_UPLOAD_URL
      if (typeof window !== 'undefined') {
        return `${window.location.origin}/api/userResume/upload`
      }
      return '/api/userResume/upload'
    }
  },
  methods: {
    async loadJobDetail() {
      this.detailLoading = true
      try {
        const jobId = this.$route.params.id
        const jobData = await this.$api.employment.getJobById(jobId)
        this.job = jobData || {}
        await this.checkCollectStatus()
      } catch (error) {
        console.error('获取职位详情失败:', error)
        this.$message.error('职位信息加载失败')
      } finally {
        this.detailLoading = false
      }
    },

    checkCollectStatus() {
      // 从职位详情数据中获取收藏状态
      if (this.job && this.job.isCollected) {
        this.isCollected = true
      } else {
        this.isCollected = false
      }
    },

    async handleCollect() {
      const token = localStorage.getItem('token')
      if (!token) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }

      this.collectLoading = true
      try {
        if (this.isCollected) {
          await this.$api.employment.uncollectJob(this.job.id)
          this.$message.success('取消收藏成功')
          this.isCollected = false
          if (this.job.collectNumber) {
            this.job.collectNumber--
          }
        } else {
          await this.$api.employment.collectJob(this.job.id)
          this.$message.success('收藏成功')
          this.isCollected = true
          if (this.job.collectNumber) {
            this.job.collectNumber++
          } else {
            this.job.collectNumber = 1
          }
        }
      } catch (error) {
        if (error.message === '已经收藏过该职位') {
          // 如果已经收藏过，设置isCollected为true
          this.isCollected = true
          this.$message.info('您已收藏过该职位')
        } else {
          this.$message.error(error.message || '操作失败')
        }
      } finally {
        this.collectLoading = false
      }
    },

    handleShare() {
      const jobId = this.job.id
      const shareUrl = `${window.location.origin}/job/${jobId}`
      const done = () => this.$message.success('链接已复制到剪贴板，您可以分享给好友了')
      const fail = () => this.$message.warning(`请手动复制链接：${shareUrl}`)
      if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(shareUrl).then(done).catch(fail)
        return
      }
      try {
        const ta = document.createElement('textarea')
        ta.value = shareUrl
        ta.style.position = 'fixed'
        ta.style.left = '-9999px'
        document.body.appendChild(ta)
        ta.select()
        document.execCommand('copy')
        document.body.removeChild(ta)
        done()
      } catch (e) {
        fail()
      }
    },

    // 简单的福利处理，假设是逗号分隔的字符串
    parseTreatment(treatment) {
      if (!treatment) return []
      // 如果已经是数组直接返回，如果是字符串则分割
      return Array.isArray(treatment) ? treatment : treatment.split(/[,，、]/).filter(t => t)
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
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      this.applyForm.name = userInfo.realName || userInfo.nickName || ''
      this.applyForm.phone = userInfo.phone || ''
      this.applyForm.introduce = ''
      this.applyForm.resume = ''
      this.resumeFileList = []
      this.uploadHeaders = { 'Authorization': `Bearer ${token}` }
      this.applyDialogVisible = true
    },

    submitApplication() {
      this.$refs.applyForm.validate(async (valid) => {
        if (!valid) return false
        this.submitting = true
        try {
          const applicationData = {
            employmentId: this.job.id,
            name: this.applyForm.name,
            phone: this.applyForm.phone,
            introduce: this.applyForm.introduce,
            resume: this.applyForm.resume
          }
          await this.$api.employmentUser.applyJob(applicationData)
          this.$message.success('申请提交成功')
          this.applyDialogVisible = false
          await this.loadJobDetail()
        } catch (error) {
          this.$message.error(error.message || '申请提交失败')
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

    // 上传相关方法保持不变...
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
    handleExceed() {
      this.$message.warning('只能上传一个简历文件')
    },

    formatTime(timestamp) {
      if(!timestamp) return ''
      const date = new Date(timestamp)
      return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`
    }
  },

  async mounted() {
    await this.loadJobDetail()
  },
  watch: {
    '$route.params.id'(newId, oldId) {
      if (newId && newId !== oldId) {
        this.loadJobDetail()
      }
    }
  }
}
</script>

<style scoped>
/* ================= 基础布局 ================= */
.job-detail-page {
  min-height: 100vh;
  background-color: #f8fafc;
  padding-bottom: 60px;
}

.container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 20px;
}

/* ================= 1. 头部信息卡 (Header) ================= */
.job-header-card {
  background: #202329; /* 深色背景，提升专业感 */
  color: white;
  padding: 40px 0;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  margin-bottom: -40px; /* 让下面的内容卡片上浮 */
  position: relative;
  z-index: 1;
}

.header-container {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 16px;
}

.job-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0;
  line-height: 1.2;
}

.salary-highlight {
  font-size: 28px;
  color: #ff6b00; /* 品牌橙 */
  font-weight: 700;
}

.meta-row {
  display: flex;
  align-items: center;
  gap: 24px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
}

.meta-tag i {
  margin-right: 4px;
}

.publish-time {
  color: rgba(255, 255, 255, 0.5);
  margin-left: auto; /* 靠右 */
}

/* 右侧操作区 */
.header-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 16px;
}

.apply-btn-lg {
  background-color: #ff6b00;
  border-color: #ff6b00;
  font-size: 16px;
  padding: 12px 40px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(255, 107, 0, 0.3);
}

.apply-btn-lg:hover {
  background-color: #ff8533;
  border-color: #ff8533;
}

.action-links {
  display: flex;
  gap: 20px;
}

.action-link {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  cursor: pointer;
  transition: color 0.2s;
}

.action-link:hover {
  color: white;
}

/* ================= 2. 主内容布局 ================= */
.content-container {
  position: relative;
  z-index: 2;
}

/* 左侧内容卡片 */
.content-card {
  background: white;
  border-radius: 12px;
  padding: 32px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.03);
  margin-bottom: 24px;
  min-height: 400px;
}

.section-title {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 20px;
  padding-left: 12px;
  border-left: 4px solid #ff6b00;
  line-height: 1;
}

/* 福利标签 */
.welfare-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 24px;
}

.welfare-item {
  font-size: 13px;
  color: #64748b;
  background: #f1f5f9;
  padding: 4px 12px;
  border-radius: 4px;
}

/* 职位描述文本 */
.job-desc-text {
  font-size: 15px;
  line-height: 1.8;
  color: #334155;
}

.desc-content {
  white-space: pre-wrap; /* 保留换行 */
}

.divider {
  height: 1px;
  background: #f1f5f9;
  margin: 32px 0;
}

.work-location {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #475569;
}

/* ================= 3. 右侧边栏 (Sidebar) ================= */
.side-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.03);
  margin-bottom: 20px;
}

.company-card {
  text-align: center;
}

.company-header {
  cursor: pointer;
  margin-bottom: 20px;
}

.company-logo-img {
  width: 80px;
  height: 80px;
  border-radius: 12px;
  border: 1px solid #f1f5f9;
  margin-bottom: 12px;
}

.company-logo-text {
  width: 80px;
  height: 80px;
  margin: 0 auto 12px;
  background: linear-gradient(135deg, #fff7ed 0%, #ffedd5 100%);
  color: #ff6b00;
  font-size: 32px;
  font-weight: 700;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.company-name {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 4px 0;
}

.company-industry {
  font-size: 13px;
  color: #64748b;
}

.company-meta-list {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 20px;
}

.meta-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #475569;
}

.meta-item i {
  font-size: 18px;
  color: #94a3b8;
}

.divider-mini {
  height: 1px;
  background: #f1f5f9;
  margin: 16px 0;
}

.company-desc-mini {
  font-size: 13px;
  color: #64748b;
  line-height: 1.5;
  margin-bottom: 20px;
  text-align: left;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.view-company-btn {
  width: 100%;
  border-radius: 20px;
  border-color: #ff6b00;
  color: #ff6b00;
  padding: 10px;
}

.view-company-btn:hover {
  background-color: #fff7ed;
}

/* ================= 弹窗美化 ================= */
.apply-dialog >>> .el-dialog__header {
  border-bottom: 1px solid #f1f5f9;
  padding: 20px;
}

.apply-dialog >>> .el-dialog__title {
  font-weight: 700;
  font-size: 18px;
}

.dialog-job-info {
  background: #f8fafc;
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-job-name {
  font-weight: 600;
  color: #334155;
}

.dialog-salary {
  color: #ff6b00;
  font-weight: 700;
}

.form-row {
  display: flex;
  gap: 20px;
}

.half-width {
  flex: 1;
}

/* 上传控件美化 */
.resume-uploader >>> .el-upload {
  width: 100%;
}

.resume-uploader >>> .el-upload-dragger {
  width: 100%;
  height: 120px;
}

.resume-uploader >>> .el-icon-upload {
  margin: 20px 0 10px;
}

/* 响应式 */
@media (max-width: 768px) {
  .header-container { flex-direction: column; gap: 20px; }
  .header-right { width: 100%; align-items: flex-start; }
  .apply-btn-lg { width: 100%; }
  .form-row { flex-direction: column; gap: 0; }
}
</style>