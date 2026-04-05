<template>
  <div class="company-applications">
    <div class="container">
      <header class="page-head">
        <div>
          <h1 class="title">申请管理</h1>
          <p class="sub">查看投递、处理简历、反馈候选人</p>
        </div>
      </header>

      <el-row :gutter="12" class="summary-row" v-if="summaryLoaded">
        <el-col :xs="12" :sm="6" v-for="s in summaryItems" :key="s.key">
          <div class="sum-card" :class="s.cls">
            <span class="sum-val">{{ s.value }}</span>
            <span class="sum-lab">{{ s.label }}</span>
          </div>
        </el-col>
      </el-row>

      <el-card shadow="never" class="toolbar-card">
        <el-row :gutter="12" type="flex" align="middle">
          <el-col :xs="24" :sm="10" :md="7">
            <el-select v-model="filters.replyStatus" placeholder="投递状态" clearable style="width: 100%" @change="onFilterStatus">
              <el-option label="全部状态" value="" />
              <el-option label="等待回复" value="Wait_For_Reply" />
              <el-option label="同意入职" value="Agree_With_Induction" />
              <el-option label="已拒绝" value="Rejected" />
              <el-option label="已拒绝(旧)" value="Refused_Entry" />
            </el-select>
          </el-col>
          <el-col :xs="24" :sm="14" :md="17" class="toolbar-meta">
            <span class="meta-text">共 <strong>{{ total }}</strong> 条记录</span>
            <el-button type="text" icon="el-icon-refresh" @click="refreshAll">刷新</el-button>
          </el-col>
        </el-row>
      </el-card>

      <el-card shadow="never" class="table-card">
        <el-table :data="applicationList" v-loading="loading" stripe empty-text="暂无投递记录">
          <el-table-column prop="name" label="姓名" width="100" />
          <el-table-column prop="phone" label="联系电话" width="124" />
          <el-table-column prop="jobTitle" label="申请职位" min-width="140" show-overflow-tooltip />
          <el-table-column label="自我介绍" min-width="160" show-overflow-tooltip>
            <template slot-scope="scope">{{ scope.row.introduce || '—' }}</template>
          </el-table-column>
          <el-table-column label="简历" width="108">
            <template slot-scope="scope">
              <el-button v-if="scope.row.userResume" type="text" size="small" @click="viewResume(scope.row.userResume)">查看</el-button>
              <span v-else class="muted">未上传</span>
            </template>
          </el-table-column>
          <el-table-column label="投递时间" width="158">
            <template slot-scope="scope">{{ formatTime(scope.row.createTime) }}</template>
          </el-table-column>
          <el-table-column label="状态" width="112">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.replyStatus)" size="small">{{ getStatusText(scope.row.replyStatus) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="viewDetail(scope.row)">详情</el-button>
              <template v-if="scope.row.replyStatus === 'Wait_For_Reply'">
                <el-button type="text" size="small" class="ok-text" @click="handleApplication(scope.row, 'Agree_With_Induction')">通过</el-button>
                <el-button type="text" size="small" class="no-text" @click="handleApplication(scope.row, 'Rejected')">拒绝</el-button>
              </template>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination" v-if="total > 0">
          <el-pagination
            background
            layout="total, prev, pager, next, jumper"
            :current-page.sync="currentPage"
            :page-size="pageSize"
            :total="total"
            @current-change="handlePageChange"
          />
        </div>
      </el-card>

      <el-dialog title="申请详情" :visible.sync="detailVisible" width="620px" custom-class="detail-dialog" @closed="onDetailClosed">
        <div v-if="currentApplication">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="姓名">{{ currentApplication.name }}</el-descriptions-item>
            <el-descriptions-item label="电话">{{ currentApplication.phone }}</el-descriptions-item>
            <el-descriptions-item label="职位" :span="2">{{ currentApplication.jobTitle }}</el-descriptions-item>
            <el-descriptions-item label="状态" :span="2">
              <el-tag :type="getStatusType(currentApplication.replyStatus)" size="small">{{ getStatusText(currentApplication.replyStatus) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="投递时间" :span="2">{{ formatTime(currentApplication.createTime) }}</el-descriptions-item>
            <el-descriptions-item label="自我介绍" :span="2">
              <div class="intro-block">{{ currentApplication.introduce || '—' }}</div>
            </el-descriptions-item>
            <el-descriptions-item v-if="currentApplication.replyContent" label="历史回复" :span="2">
              <div class="reply-block">{{ currentApplication.replyContent }}</div>
            </el-descriptions-item>
          </el-descriptions>

          <div v-if="currentApplication.replyStatus === 'Wait_For_Reply'" class="reply-editor">
            <div class="reply-title">处理此申请</div>
            <el-input type="textarea" v-model="replyContent" :rows="4" maxlength="500" show-word-limit placeholder="向候选人说明结果或下一步安排…" />
            <div class="reply-btns">
              <el-button type="primary" @click="replyApplication('Agree_With_Induction')" :loading="replying">通过并通知</el-button>
              <el-button @click="replyApplication('Rejected')" :loading="replying">拒绝并通知</el-button>
            </div>
          </div>
        </div>
      </el-dialog>

      <el-dialog title="简历查看" :visible.sync="resumeDialogVisible" width="80%" top="5vh" @closed="onResumeDialogClosed">
        <div class="resume-container">
          <iframe v-if="currentResumeUrl" :src="currentResumeUrl" width="100%" height="600px" frameborder="0"></iframe>
          <div v-else-if="currentResume && !currentResumeUrl" class="download-prompt">
            <i class="el-icon-download"></i>
            <h3>不支持在线预览</h3>
            <p>该简历格式 ({{ currentResume.extension }}) 需下载后查看</p>
            <el-button type="primary" @click="downloadResume"><i class="el-icon-download"></i> 下载简历</el-button>
          </div>
          <div v-else class="no-resume-content">
            <i class="el-icon-document"></i>
            <p>无法预览该简历</p>
          </div>
        </div>
        <div slot="footer">
          <el-button @click="resumeDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="downloadResume" v-if="currentResume">下载</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { fetchResumeBlob } from '@/utils/resumeFile'

function normalizeResumeExt(extension) {
  if (!extension) return ''
  const e = String(extension).toLowerCase().trim()
  return e.startsWith('.') ? e : '.' + e
}

export default {
  name: 'CompanyApplications',
  data() {
    return {
      applicationList: [],
      allForSummary: [],
      summaryLoaded: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      filters: { replyStatus: '' },
      detailVisible: false,
      currentApplication: null,
      replyContent: '',
      replying: false,
      resumeDialogVisible: false,
      currentResumeUrl: '',
      currentResume: null,
      _resumeBlobUrl: '',
      companyId: ''
    }
  },
  computed: {
    summaryItems() {
      const list = this.allForSummary
      const c = key => list.filter(a => a.replyStatus === key).length
      const rejected = list.filter(a => a.replyStatus === 'Rejected' || a.replyStatus === 'Refused_Entry').length
      return [
        { key: 'all', label: '总投递', value: list.length, cls: 's-all' },
        { key: 'wait', label: '待处理', value: c('Wait_For_Reply'), cls: 's-wait' },
        { key: 'ok', label: '已录用', value: c('Agree_With_Induction'), cls: 's-ok' },
        { key: 'no', label: '已拒绝', value: rejected, cls: 's-no' }
      ]
    }
  },
  mounted() {
    this.bootstrap()
  },
  methods: {
    refreshAll() {
      Promise.all([this.loadSummary(), this.loadApplications()])
    },
    async bootstrap() {
      const company = await this.$api.company.getCurrentCompany().catch(() => null)
      if (!company || !company.id) {
        this.$message.warning('请先完善企业信息')
        this.$router.push('/company/register')
        return
      }
      this.companyId = company.id
      await Promise.all([this.loadSummary(), this.loadApplications()])
    },
    async loadSummary() {
      if (!this.companyId) return
      try {
        const data = await this.$api.employmentUser.getCompanyApplications(1, 2000, { companyId: this.companyId })
        this.allForSummary = (data.list || []).map(app => ({
          ...app,
          jobTitle: app.employmentModel ? app.employmentModel.title : '未知职位'
        }))
        this.summaryLoaded = true
      } catch (e) {
        this.allForSummary = []
      }
    },
    onFilterStatus() {
      this.currentPage = 1
      this.loadApplications()
    },
    async loadApplications() {
      if (!this.companyId) return
      this.loading = true
      try {
        const params = { companyId: this.companyId }
        if (this.filters.replyStatus) params.replyStatus = this.filters.replyStatus
        const data = await this.$api.employmentUser.getCompanyApplications(this.currentPage, this.pageSize, params)
        this.applicationList = (data.list || []).map(app => ({
          ...app,
          jobTitle: app.employmentModel ? app.employmentModel.title : '未知职位'
        }))
        this.total = data.total || 0
      } catch (error) {
        this.$message.error('获取申请列表失败')
        this.applicationList = []
        this.total = 0
      } finally {
        this.loading = false
      }
    },
    handlePageChange() {
      this.loadApplications()
      window.scrollTo({ top: 0, behavior: 'smooth' })
    },
    viewDetail(application) {
      this.currentApplication = application
      this.replyContent = ''
      this.detailVisible = true
    },
    onDetailClosed() {
      this.currentApplication = null
      this.replyContent = ''
    },
    async handleApplication(application, status) {
      try {
        const action = status === 'Agree_With_Induction' ? '通过' : '拒绝'
        await this.$confirm(`确定要${action}该候选人吗？将发送默认通知语。`, '提示', { type: 'warning' })
        await this.$api.employmentUser.updateApplication({
          id: application.id,
          replyStatus: status,
          replyContent:
            status === 'Agree_With_Induction'
              ? '恭喜您通过初步筛选，请保持电话畅通，我们会进一步联系您。'
              : '感谢您的投递，本轮未能继续安排面试，祝您早日找到合适机会。'
        })
        this.$message.success('已更新')
        await Promise.all([this.loadSummary(), this.loadApplications()])
      } catch (error) {
        if (error !== 'cancel') this.$message.error('操作失败')
      }
    },
    async replyApplication(status) {
      if (!this.replyContent.trim()) {
        this.$message.warning('请填写通知内容')
        return
      }
      this.replying = true
      try {
        await this.$api.employmentUser.updateApplication({
          id: this.currentApplication.id,
          replyStatus: status,
          replyContent: this.replyContent.trim()
        })
        this.$message.success('已通知候选人')
        this.detailVisible = false
        await Promise.all([this.loadSummary(), this.loadApplications()])
      } catch (error) {
        this.$message.error('提交失败')
      } finally {
        this.replying = false
      }
    },
    getStatusType(replyStatus) {
      const types = {
        Wait_For_Reply: 'warning',
        Agree_With_Induction: 'success',
        Rejected: 'danger',
        Refused_Entry: 'info'
      }
      return types[replyStatus] || 'info'
    },
    getStatusText(replyStatus) {
      const texts = {
        Wait_For_Reply: '等待回复',
        Agree_With_Induction: '同意入职',
        Rejected: '已拒绝',
        Refused_Entry: '已拒绝'
      }
      return texts[replyStatus] || replyStatus || '未知'
    },
    formatTime(timestamp) {
      if (timestamp == null) return '—'
      const ms = typeof timestamp === 'number' && timestamp < 1e12 ? timestamp * 1000 : Number(timestamp)
      const d = new Date(ms)
      if (Number.isNaN(d.getTime())) return '—'
      const y = d.getFullYear()
      const m = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      const h = String(d.getHours()).padStart(2, '0')
      const mi = String(d.getMinutes()).padStart(2, '0')
      return `${y}-${m}-${day} ${h}:${mi}`
    },
    revokePreviewUrl() {
      if (this._resumeBlobUrl) {
        URL.revokeObjectURL(this._resumeBlobUrl)
        this._resumeBlobUrl = ''
      }
    },
    onResumeDialogClosed() {
      this.revokePreviewUrl()
      this.currentResumeUrl = ''
    },
    async viewResume(resume) {
      if (!resume || !resume.id) {
        this.$message.warning('未上传简历')
        return
      }
      this.revokePreviewUrl()
      this.currentResume = resume
      const ext = normalizeResumeExt(resume.extension)

      if (ext === '.pdf') {
        this.currentResumeUrl = ''
        this.resumeDialogVisible = true
        try {
          const blob = await fetchResumeBlob(resume.id)
          this._resumeBlobUrl = URL.createObjectURL(blob)
          this.currentResumeUrl = this._resumeBlobUrl
        } catch (e) {
          this.$message.error((e && e.message) || '加载简历失败')
          this.resumeDialogVisible = false
        }
        return
      }

      if (ext === '.doc' || ext === '.docx') {
        const absolute = resume.resumeUrl && /^https?:\/\//i.test(resume.resumeUrl)
        if (absolute) {
          this.currentResumeUrl = `https://view.officeapps.live.com/op/embed.aspx?src=${encodeURIComponent(resume.resumeUrl)}`
          this.resumeDialogVisible = true
          return
        }
        this.$confirm('Word 简历需下载后打开，是否现在下载？', '提示', { type: 'info' })
          .then(() => this.downloadResume())
          .catch(() => {})
        return
      }

      this.$confirm(`格式 (${ext || '未知'}) 不支持在线预览，是否下载？`, '提示', { type: 'warning' })
        .then(() => this.downloadResume())
        .catch(() => {})
    },
    async downloadResume() {
      if (!this.currentResume || !this.currentResume.id) return
      try {
        const blob = await fetchResumeBlob(this.currentResume.id)
        const objectUrl = URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = objectUrl
        const ext = normalizeResumeExt(this.currentResume.extension)
        let name = this.currentResume.resumeName || '简历'
        if (ext && !name.toLowerCase().endsWith(ext.toLowerCase())) name += ext
        a.download = name.replace(/[/\\?%*:|"<>]/g, '_')
        document.body.appendChild(a)
        a.click()
        document.body.removeChild(a)
        URL.revokeObjectURL(objectUrl)
      } catch (e) {
        this.$message.error((e && e.message) || '下载失败')
      }
    }
  }
}
</script>

<style scoped>
.company-applications {
  min-height: calc(100vh - 120px);
  padding: 24px 20px 40px;
  background: #f8fafc;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.page-head {
  margin-bottom: 16px;
}

.title {
  margin: 0 0 6px;
  font-size: 26px;
  font-weight: 800;
  color: #0f172a;
}

.sub {
  margin: 0;
  font-size: 14px;
  color: #64748b;
}

.summary-row {
  margin-bottom: 16px;
}

.sum-card {
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  border: 1px solid #f1f5f9;
  background: #fff;
}

.sum-val {
  display: block;
  font-size: 24px;
  font-weight: 800;
  color: #0f172a;
}

.sum-lab {
  font-size: 13px;
  color: #64748b;
}

.s-wait .sum-val { color: #e6a23c; }
.s-ok .sum-val { color: #67c23a; }
.s-no .sum-val { color: #f56c6c; }
.s-all .sum-val { color: #ff6b00; }

.toolbar-card,
.table-card {
  border-radius: 12px;
  border: 1px solid #f1f5f9;
  margin-bottom: 16px;
}

.toolbar-meta {
  text-align: right;
  line-height: 36px;
}

.meta-text {
  font-size: 13px;
  color: #64748b;
  margin-right: 8px;
}

.meta-text strong {
  color: #ff6b00;
}

.muted {
  color: #c0c4cc;
  font-size: 12px;
}

.ok-text {
  color: #67c23a;
}

.no-text {
  color: #f56c6c;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.pagination >>> .el-pagination.is-background .el-pager li:not(.disabled).active {
  background-color: #ff6b00;
}

.intro-block,
.reply-block {
  max-height: 120px;
  overflow-y: auto;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.reply-editor {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #f1f5f9;
}

.reply-title {
  font-weight: 600;
  margin-bottom: 10px;
  color: #334155;
}

.reply-btns {
  margin-top: 12px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.resume-container {
  width: 100%;
  height: 600px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
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
  font-size: 56px;
  color: #ff6b00;
  margin-bottom: 16px;
}

.download-prompt h3 {
  margin: 0 0 8px;
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
  margin-bottom: 12px;
}

@media (max-width: 768px) {
  .toolbar-meta {
    text-align: left;
  }
}
</style>
