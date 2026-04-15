<template>
  <div class="app-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-number">
              {{ resumeStats.total }}
            </div>
            <div class="stats-label">
              总申请数
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card pending">
          <div class="stats-content">
            <div class="stats-number">
              {{ resumeStats.pending }}
            </div>
            <div class="stats-label">
              待处理
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card approved">
          <div class="stats-content">
            <div class="stats-number">
              {{ resumeStats.approved }}
            </div>
            <div class="stats-label">
              已通过
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card rejected">
          <div class="stats-content">
            <div class="stats-number">
              {{ resumeStats.rejected }}
            </div>
            <div class="stats-label">
              已拒绝
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选条件 -->
    <div class="filter-container">
      <el-input v-model="listQuery.name" placeholder="申请人姓名" style="width: 150px;" class="filter-item" @keyup.enter.native="handleQuery" />
      <el-input v-model="listQuery.phone" placeholder="手机号" style="width: 150px;" class="filter-item" @keyup.enter.native="handleQuery" />
      <el-select v-model="listQuery.replyStatus" placeholder="申请状态" clearable style="width: 120px;" class="filter-item">
        <el-option label="待回复" value="Wait_For_Reply" />
        <el-option label="已通过" value="Agree_With_Induction" />
        <el-option label="已拒绝" value="Rejected" />
      </el-select>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleQuery">
        查询
      </el-button>
      <el-button class="filter-item" type="info" icon="el-icon-refresh" @click="resetQuery">
        重置
      </el-button>
    </div>

    <!-- 简历申请列表 -->
    <el-table v-loading="listLoading" :data="resumeList" border fit highlight-current-row>
      <el-table-column align="center" label="申请人" prop="name" width="100" />
      <el-table-column align="center" label="手机号" prop="phone" width="120" />
      <el-table-column align="center" label="应聘职位" prop="employmentModel.title" width="150" show-overflow-tooltip />
      <el-table-column align="center" label="附件简历" width="200">
        <template slot-scope="{ row }">
          <template v-if="row.userResume && row.userResume.id">
            <el-button type="text" size="mini" :loading="resumeActionLoading === row.id + '-p'" @click="previewAttachment(row)">
              预览
            </el-button>
            <el-button type="text" size="mini" :loading="resumeActionLoading === row.id + '-d'" @click="downloadAttachment(row)">
              下载
            </el-button>
          </template>
          <span v-else class="text-muted">未上传</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="申请时间" width="150">
        <template slot-scope="{row}">
          <span>{{ row.createTime | parseTime }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="流程阶段" width="120">
        <template slot-scope="{row}">
          <el-tag type="info">
            {{ getStageText(row.recruitStage) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="申请状态" width="110">
        <template slot-scope="{row}">
          <el-tag v-if="row.replyStatus === 'Wait_For_Reply'" type="warning">
            待处理
          </el-tag>
          <el-tag v-else-if="row.replyStatus === 'Agree_With_Induction'" type="success">
            已通过
          </el-tag>
          <el-tag v-else-if="row.replyStatus === 'Refused_Entry' || row.replyStatus === 'Rejected'" type="danger">
            已拒绝
          </el-tag>
          <el-tag v-else type="info">
            {{ row.replyStatus || '—' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作" width="220">
        <template slot-scope="{row}">
          <el-button type="info" size="mini" @click="viewDetail(row)">
            详情
          </el-button>
          <template v-if="row.replyStatus === 'Wait_For_Reply'">
            <el-button type="success" size="mini" @click="quickHandle(row, 'Agree_With_Induction')">
              通过
            </el-button>
            <el-button type="danger" size="mini" @click="quickHandle(row, 'Rejected')">
              拒绝
            </el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.pageNum" :limit.sync="listQuery.pageSize" @pagination="getResumeList" />

    <!-- 投递详情 -->
    <el-dialog title="投递详情" :visible.sync="detailVisible" width="560px" @close="currentRow = null">
      <template v-if="currentRow">
        <el-descriptions :column="1" border size="small">
          <el-descriptions-item label="申请人">
            {{ currentRow.name || '—' }}
          </el-descriptions-item>
          <el-descriptions-item label="手机">
            {{ currentRow.phone || '—' }}
          </el-descriptions-item>
          <el-descriptions-item label="职位">
            {{ (currentRow.employmentModel && currentRow.employmentModel.title) || '—' }}
          </el-descriptions-item>
          <el-descriptions-item label="流程阶段">
            <el-tag type="info">{{ getStageText(currentRow.recruitStage) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="申请状态">
            <el-tag :type="getReplyStatusType(currentRow.replyStatus)">{{ getReplyStatusText(currentRow.replyStatus) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="自我介绍">
            <div class="intro-block">
              {{ currentRow.introduce || '—' }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="附件">
            <template v-if="currentRow.userResume && currentRow.userResume.id">
              <el-button type="primary" size="mini" @click="previewAttachment(currentRow)">
                预览简历
              </el-button>
              <el-button size="mini" @click="downloadAttachment(currentRow)">
                下载
              </el-button>
            </template>
            <span v-else>未上传</span>
          </el-descriptions-item>
          <el-descriptions-item label="流程时间线">
            <el-button type="text" @click="loadTimeline(currentRow)">刷新时间线</el-button>
            <div v-loading="timelineLoading" class="timeline-box">
              <el-timeline v-if="timeline.length">
                <el-timeline-item
                  v-for="item in timeline"
                  :key="item.id"
                  :timestamp="item.createTime | parseTime"
                  placement="top"
                >
                  <div class="timeline-node-title">
                    <strong>{{ getFlowActionText(item.actionType) }}</strong>
                    <el-tag size="mini" type="info">{{ getStageText(item.stageTo) }}</el-tag>
                  </div>
                  <div class="timeline-node-content">{{ item.content || '状态已更新' }}</div>
                </el-timeline-item>
              </el-timeline>
              <div v-else-if="!timelineLoading" class="timeline-empty">暂无流程记录</div>
            </div>
          </el-descriptions-item>
        </el-descriptions>
        <div v-if="currentRow.replyStatus === 'Wait_For_Reply'" class="decision-panel">
          <div class="decision-panel__title">处理此申请</div>
          <el-select v-model="decisionStage" style="width: 220px" placeholder="请选择流程阶段">
            <el-option label="筛选中" value="SCREENING" />
            <el-option label="面试中" value="INTERVIEW" />
            <el-option label="待录用" value="OFFER" />
            <el-option label="已录用" value="HIRED" />
            <el-option label="已淘汰" value="REJECTED" />
          </el-select>
          <el-input
            v-model="decisionRemark"
            type="textarea"
            :rows="4"
            maxlength="300"
            show-word-limit
            placeholder="填写处理备注，候选人会收到通知"
          />
          <div class="decision-actions">
            <el-button type="success" :loading="decisionLoading" @click="handleDecision('Agree_With_Induction')">
              通过并通知
            </el-button>
            <el-button type="danger" :loading="decisionLoading" @click="handleDecision('Rejected')">
              拒绝并通知
            </el-button>
          </div>
        </div>
      </template>
    </el-dialog>

    <!-- 简历预览（PDF 等） -->
    <el-dialog title="简历预览" :visible.sync="resumeDialogVisible" width="80%" top="5vh" @closed="onResumeDialogClosed">
      <div v-loading="resumeBlobLoading" class="resume-preview-wrap">
        <iframe v-if="currentResumeObjectUrl" :src="currentResumeObjectUrl" width="100%" height="600px" frameborder="0" title="resume-preview" />
        <div v-else-if="!resumeBlobLoading" class="preview-empty">
          无法显示预览
        </div>
      </div>
      <div slot="footer">
        <el-button @click="resumeDialogVisible = false">
          关闭
        </el-button>
        <el-button v-if="previewRow && previewRow.userResume" type="primary" @click="downloadAttachment(previewRow)">
          下载
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import Pagination from '@/components/Pagination'
import { parseTime } from '@/utils/time'
import { getDashboardStatistics } from '@/api/statistics'
import { fetchResumeBlob } from '@/utils/resumeFile'
import { getEmploymentUserTimeline } from '@/api/employmentUser'

function normalizeExt(extension) {
  if (!extension) return ''
  const e = String(extension).toLowerCase().trim()
  return e.startsWith('.') ? e : '.' + e
}

export default {
  components: { Pagination },
  filters: {
    parseTime (val) {
      return parseTime(val, '{y}-{m}-{d} {h}:{i}') || '--'
    }
  },
  data () {
    return {
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        name: '',
        phone: '',
        replyStatus: null
      },
      listLoading: true,
      total: 0,
      resumeStats: {
        total: 0,
        pending: 0,
        approved: 0,
        rejected: 0
      },
      detailVisible: false,
      currentRow: null,
      resumeDialogVisible: false,
      currentResumeObjectUrl: '',
      resumeBlobLoading: false,
      previewRow: null,
      resumeActionLoading: '',
      timelineLoading: false,
      timeline: [],
      decisionRemark: '',
      decisionLoading: false,
      decisionStage: 'SCREENING'
    }
  },
  computed: {
    ...mapState({
      resumeList: state => state.employmentUser.employmentUserList
    })
  },
  mounted () {
    this.applyRouteQuery()
    this.loadResumeSummary()
    this.getResumeList()
  },
  methods: {
    applyRouteQuery () {
      const q = this.$route.query || {}
      if (q.replyStatus) {
        this.listQuery.replyStatus = q.replyStatus
      }
    },

    loadResumeSummary () {
      getDashboardStatistics()
        .then(data => {
          this.resumeStats.total = data.applicationCount || 0
          this.resumeStats.pending = data.pendingApplicationCount || 0
          this.resumeStats.approved = data.approvedApplicationCount || 0
          this.resumeStats.rejected = data.rejectedApplicationCount || 0
        })
        .catch(() => {})
    },
    getResumeExt (row) {
      const ur = row && row.userResume
      if (!ur) return ''
      return normalizeExt(ur.extension)
    },
    async previewAttachment (row) {
      const ur = row.userResume
      if (!ur || !ur.id) {
        this.$message.warning('该记录未关联附件简历')
        return
      }
      const ext = this.getResumeExt(row)
      this.previewRow = row
      if (ext === '.pdf') {
        this.revokeObjectUrlSafe()
        this.resumeBlobLoading = true
        this.resumeDialogVisible = true
        this.resumeActionLoading = row.id + '-p'
        try {
          const blob = await fetchResumeBlob(ur.id)
          this.currentResumeObjectUrl = URL.createObjectURL(blob)
        } catch (e) {
          this.$message.error((e && e.message) || '加载简历失败')
          this.resumeDialogVisible = false
        } finally {
          this.resumeBlobLoading = false
          this.resumeActionLoading = ''
        }
        return
      }
      if (ext === '.doc' || ext === '.docx') {
        this.$confirm('Word 简历需下载后用本地软件打开，是否现在下载？', '预览提示', {
          confirmButtonText: '下载',
          cancelButtonText: '取消',
          type: 'info'
        }).then(() => this.downloadAttachment(row)).catch(() => {})
        return
      }
      this.$message.info('该格式请使用下载查看')
      await this.downloadAttachment(row)
    },
    async downloadAttachment (row) {
      const ur = row.userResume
      if (!ur || !ur.id) {
        this.$message.warning('该记录未关联附件简历')
        return
      }
      this.resumeActionLoading = row.id + '-d'
      try {
        const blob = await fetchResumeBlob(ur.id)
        const objectUrl = URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = objectUrl
        const extNorm = this.getResumeExt(row)
        let downloadName = ur.resumeName || '简历'
        if (extNorm && !downloadName.toLowerCase().endsWith(extNorm.toLowerCase())) {
          downloadName += extNorm
        }
        a.download = downloadName.replace(/[/\\?%*:|"<>]/g, '_')
        document.body.appendChild(a)
        a.click()
        document.body.removeChild(a)
        URL.revokeObjectURL(objectUrl)
        this.$message.success('已开始下载')
      } catch (e) {
        this.$message.error((e && e.message) || '下载失败')
      } finally {
        this.resumeActionLoading = ''
      }
    },
    revokeObjectUrlSafe () {
      if (this.currentResumeObjectUrl) {
        URL.revokeObjectURL(this.currentResumeObjectUrl)
        this.currentResumeObjectUrl = ''
      }
    },
    onResumeDialogClosed () {
      this.revokeObjectUrlSafe()
      this.previewRow = null
    },
    getResumeList () {
      const params = {
        pageNum: this.listQuery.pageNum,
        pageSize: this.listQuery.pageSize,
        params: {
          showAll: true,
          name: this.listQuery.name,
          phone: this.listQuery.phone,
          replyStatus: this.listQuery.replyStatus
        }
      }

      this.$store.dispatch('getEmploymentUserList', params).then(res => {
        this.total = res.total
        this.listLoading = false
      }).catch(err => {
        const msg = (err && err.message) || '获取简历列表失败'
        this.$message.error(msg)
        this.listLoading = false
      })
    },
    handleQuery () {
      this.listQuery.pageNum = 1
      this.getResumeList()
    },
    resetQuery () {
      this.listQuery = {
        pageNum: 1,
        pageSize: 10,
        name: '',
        phone: '',
        replyStatus: null
      }
      this.getResumeList()
    },
    viewDetail (row) {
      this.currentRow = row
      this.decisionRemark = row.replyContent || ''
      this.decisionStage = row.recruitStage || 'SCREENING'
      this.detailVisible = true
      this.loadTimeline(row)
    },
    async quickHandle (row, status) {
      const actionText = status === 'Agree_With_Induction' ? '通过' : '拒绝'
      try {
        await this.$confirm(`确定要${actionText}该申请吗？`, '提示', { type: 'warning' })
        await this.$store.dispatch('updateEmploymentUser', {
          id: row.id,
          replyStatus: status,
          recruitStage: status === 'Agree_With_Induction' ? 'HIRED' : 'REJECTED',
          replyContent: status === 'Agree_With_Induction'
            ? '管理员审核后已通过该申请。'
            : '管理员审核后未通过该申请。'
        })
        this.$message.success(`已${actionText}`)
        this.loadResumeSummary()
        this.getResumeList()
      } catch (e) {
        if (e !== 'cancel') {
          this.$message.error((e && e.message) || `${actionText}失败`)
        }
      }
    },
    async loadTimeline (row) {
      if (!row || !row.id) return
      this.timelineLoading = true
      try {
        this.timeline = await getEmploymentUserTimeline(row.id)
      } catch (e) {
        this.$message.error((e && e.message) || '获取流程时间线失败')
      } finally {
        this.timelineLoading = false
      }
    },
    getStageText (stage) {
      const texts = {
        SUBMITTED: '已投递',
        SCREENING: '筛选中',
        INTERVIEW: '面试中',
        OFFER: '待录用',
        HIRED: '已录用',
        REJECTED: '已淘汰',
        WITHDRAWN: '已撤回'
      }
      return texts[stage] || stage || '未知阶段'
    },
    getFlowActionText (actionType) {
      const texts = {
        SUBMIT: '候选人投递',
        STATUS_CHANGE: '处理状态更新',
        COLLECT: '收藏职位'
      }
      return texts[actionType] || actionType || '流程变更'
    },
    getReplyStatusType (status) {
      const map = {
        Wait_For_Reply: 'warning',
        Agree_With_Induction: 'success',
        Rejected: 'danger',
        Refused_Entry: 'danger'
      }
      return map[status] || 'info'
    },
    getReplyStatusText (status) {
      const map = {
        Wait_For_Reply: '待处理',
        Agree_With_Induction: '已通过',
        Rejected: '已拒绝',
        Refused_Entry: '已拒绝'
      }
      return map[status] || status || '未知'
    },
    async handleDecision (status) {
      if (!this.currentRow) return
      this.decisionLoading = true
      try {
        await this.$store.dispatch('updateEmploymentUser', {
          id: this.currentRow.id,
          replyStatus: status,
          recruitStage: this.decisionStage,
          replyContent: this.decisionRemark || (status === 'Agree_With_Induction'
            ? '管理员审核后已通过该申请。'
            : '管理员审核后未通过该申请。')
        })
        this.$message.success('处理成功')
        this.detailVisible = false
        this.loadResumeSummary()
        this.getResumeList()
      } catch (e) {
        this.$message.error((e && e.message) || '处理失败')
      } finally {
        this.decisionLoading = false
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  text-align: center;

  &.pending {
    border-left: 4px solid #e6a23c;
  }

  &.approved {
    border-left: 4px solid #67c23a;
  }

  &.rejected {
    border-left: 4px solid #f56c6c;
  }
}

.stats-content {
  .stats-number {
    font-size: 24px;
    font-weight: bold;
    color: #303133;
  }

  .stats-label {
    font-size: 14px;
    color: #909399;
    margin-top: 5px;
  }
}

.text-muted {
  color: #909399;
  font-size: 12px;
}

.intro-block {
  white-space: pre-wrap;
  word-break: break-word;
  max-height: 160px;
  overflow-y: auto;
}

.resume-preview-wrap {
  min-height: 320px;
}

.preview-empty {
  text-align: center;
  color: #909399;
  padding: 48px;
}

.timeline-box {
  margin-top: 8px;
}

.timeline-node-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.timeline-node-content {
  margin-top: 6px;
  color: #606266;
  line-height: 1.6;
}

.timeline-empty {
  color: #909399;
}

.decision-panel {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.decision-panel__title {
  margin-bottom: 12px;
  color: #303133;
  font-size: 14px;
  font-weight: 600;
}

.decision-panel .el-select,
.decision-panel .el-textarea {
  display: block;
  margin-bottom: 12px;
}

.decision-actions {
  display: flex;
  gap: 10px;
}
</style>
