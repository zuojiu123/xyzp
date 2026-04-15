<template>
  <div class="app-container feedback-page">
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card all">
          <div class="stats-number">{{ feedbackStats.total }}</div>
          <div class="stats-label">总反馈数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card pending">
          <div class="stats-number">{{ feedbackStats.pending }}</div>
          <div class="stats-label">待处理</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card processing">
          <div class="stats-number">{{ feedbackStats.processing }}</div>
          <div class="stats-label">处理中</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stats-card resolved">
          <div class="stats-number">{{ feedbackStats.resolved }}</div>
          <div class="stats-label">已解决/关闭</div>
        </el-card>
      </el-col>
    </el-row>

    <div class="filter-container">
      <el-button
        class="filter-item"
        :type="listQuery.status === 'Pending' ? 'primary' : 'default'"
        plain
        @click="togglePendingOnly"
      >
        只看待处理
      </el-button>
      <el-input
        v-model="listQuery.title"
        placeholder="请输入反馈标题"
        style="width: 220px;"
        class="filter-item"
        @keyup.enter.native="handleQuery"
      />
      <el-select v-model="listQuery.type" clearable placeholder="反馈类型" style="width: 150px;" class="filter-item">
        <el-option v-for="item in feedBackTypeEnum" :key="item.enumCode" :label="item.msg" :value="item.enumCode" />
      </el-select>
      <el-select v-model="listQuery.status" clearable placeholder="处理状态" style="width: 150px;" class="filter-item">
        <el-option label="待处理" value="Pending" />
        <el-option label="处理中" value="Processing" />
        <el-option label="已解决" value="Resolved" />
        <el-option label="已关闭" value="Closed" />
      </el-select>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleQuery">
        查询
      </el-button>
      <el-button class="filter-item" icon="el-icon-refresh" @click="resetQuery">
        重置
      </el-button>
    </div>

    <el-table v-loading="listLoading" :data="feedbackList" element-loading-text="加载中..." border fit highlight-current-row>
      <el-table-column align="center" label="标题" prop="title" min-width="180" show-overflow-tooltip />
      <el-table-column align="center" label="内容" prop="content" min-width="220" show-overflow-tooltip />
      <el-table-column align="center" label="用户ID" prop="userId" width="110" />
      <el-table-column align="center" label="类型" width="120">
        <template slot-scope="{ row }">
          <el-tag type="info">{{ getEnumText(row.type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="创建时间" width="160">
        <template slot-scope="{ row }">
          <span>{{ row.createTime | parseTime }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="处理状态" width="120">
        <template slot-scope="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="处理人" width="120">
        <template slot-scope="{ row }">
          {{ row.replyUserName || row.replyUserId || '—' }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="回复时间" width="160">
        <template slot-scope="{ row }">
          <span>{{ row.replyTime | parseTime }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作" width="220">
        <template slot-scope="{ row }">
          <el-button type="info" size="mini" @click="viewDetail(row)">
            详情
          </el-button>
          <el-button
            v-if="row.status === 'Pending' || row.status === 'Processing'"
            type="primary"
            size="mini"
            @click="handleUpdate(row)"
          >
            处理
          </el-button>
          <el-popconfirm title="确定要删除此条反馈吗？" @confirm="handleDelete(row)">
            <el-button slot="reference" size="mini" type="danger">
              删除
            </el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="listQuery.pageNum"
      :limit.sync="listQuery.pageSize"
      @pagination="getFeedbackList"
    />

    <el-dialog :visible.sync="detailVisible" title="反馈详情" width="680px">
      <template v-if="currentRow">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="标题">{{ currentRow.title || '—' }}</el-descriptions-item>
          <el-descriptions-item label="内容">
            <div class="content-block">{{ currentRow.content || '—' }}</div>
          </el-descriptions-item>
          <el-descriptions-item label="类型">{{ getEnumText(currentRow.type) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentRow.status)">{{ getStatusLabel(currentRow.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="反馈用户">{{ currentRow.userId || '—' }}</el-descriptions-item>
          <el-descriptions-item label="处理回复">
            <div class="content-block">{{ currentRow.replyContent || '暂无回复' }}</div>
          </el-descriptions-item>
        </el-descriptions>
      </template>
    </el-dialog>

    <el-dialog :visible.sync="dialogFormVisible" title="反馈处理" width="560px">
      <el-form ref="dataForm" :model="temp" label-position="left" label-width="90px">
        <el-form-item label="标题">
          <el-input v-model="temp.title" disabled />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="temp.content" type="textarea" :rows="4" disabled />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="temp.type" disabled style="width: 100%">
            <el-option v-for="item in feedBackTypeEnum" :key="item.enumCode" :label="item.msg" :value="item.enumCode" />
          </el-select>
        </el-form-item>
        <el-form-item label="回复内容">
          <el-input v-model="temp.replyContent" type="textarea" :rows="4" placeholder="请输入处理回复" />
          <div class="reply-template-row">
            <el-button size="mini" @click="applyReplyTemplate('已记录，问题正在排查处理中，我们会尽快同步进展。', 'Processing')">
              模板：处理中
            </el-button>
            <el-button size="mini" @click="applyReplyTemplate('问题已处理完成，如仍有异常请继续反馈。', 'Resolved')">
              模板：已解决
            </el-button>
            <el-button size="mini" @click="applyReplyTemplate('该问题已归档关闭，感谢你的反馈。', 'Closed')">
              模板：关闭
            </el-button>
          </div>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="temp.status" style="width: 100%">
            <el-option label="待处理" value="Pending" />
            <el-option label="处理中" value="Processing" />
            <el-option label="已解决" value="Resolved" />
            <el-option label="已关闭" value="Closed" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="updateData">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { parseTime } from '@/utils/time'
import Pagination from '@/components/Pagination'

export default {
  components: { Pagination },
  filters: {
    parseTime(val) {
      return parseTime(val, '{y}-{m}-{d} {h}:{i}') || '--'
    }
  },
  data() {
    return {
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        title: '',
        type: null,
        status: ''
      },
      listLoading: true,
      dialogFormVisible: false,
      detailVisible: false,
      currentRow: null,
      temp: {
        id: '',
        title: '',
        content: '',
        type: '',
        replyContent: '',
        status: 'Pending'
      },
      total: 0,
      feedbackStats: {
        total: 0,
        pending: 0,
        processing: 0,
        resolved: 0
      }
    }
  },
  computed: {
    ...mapState({
      feedbackList: state => state.feedback.feedbackList,
      feedBackTypeEnum: state => state.enumList.feedBackTypeEnum
    })
  },
  mounted() {
    this.getFeedbackList()
    this.getFeedBackTypeEnum()
  },
  methods: {
    getFeedBackTypeEnum() {
      this.$store.dispatch('getFeedBackTypeEnum', 'FeedBackTypeEnum')
    },
    updateStats(list) {
      const arr = Array.isArray(list) ? list : []
      this.feedbackStats = {
        total: arr.length,
        pending: arr.filter(item => item.status === 'Pending').length,
        processing: arr.filter(item => item.status === 'Processing').length,
        resolved: arr.filter(item => item.status === 'Resolved' || item.status === 'Closed').length
      }
    },
    buildQueryParams() {
      return {
        pageNum: this.listQuery.pageNum,
        pageSize: this.listQuery.pageSize,
        condition: {
          title: this.listQuery.title,
          type: this.listQuery.type,
          status: this.listQuery.status || null
        }
      }
    },
    getFeedbackList() {
      this.listLoading = true
      this.$store.dispatch('queryFeedback', this.buildQueryParams()).then(res => {
        this.total = res.total || 0
        this.updateStats(res.list || [])
      }).catch(err => {
        this.$message.error((err && err.message) || '获取反馈列表失败')
      }).finally(() => {
        this.listLoading = false
      })
    },
    viewDetail(row) {
      this.currentRow = row
      this.detailVisible = true
    },
    handleUpdate(row) {
      this.temp = {
        id: row.id,
        title: row.title || '',
        content: row.content || '',
        type: row.type && row.type.enumCode ? row.type.enumCode : row.type,
        replyContent: row.replyContent || '',
        status: row.status || (row.replyContent ? 'Resolved' : 'Pending')
      }
      this.dialogFormVisible = true
    },
    handleDelete(row) {
      this.$store.dispatch('deleteFeedback', row.id).then(() => {
        this.getFeedbackList()
        this.$notify({
          title: 'Success',
          message: '删除成功',
          type: 'success',
          duration: 2000
        })
      }).catch(err => {
        this.$message.error(err.message || err)
      })
    },
    updateData() {
      const payload = {
        ...this.temp,
        status: this.temp.replyContent && this.temp.replyContent.trim() && this.temp.status === 'Pending'
          ? 'Resolved'
          : this.temp.status
      }
      this.$store.dispatch('updateFeedback', payload).then(() => {
        this.getFeedbackList()
        this.$notify({
          title: 'Success',
          message: '处理成功',
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
      }).catch(err => {
        this.$message.error(err.message || err)
      })
    },
    handleQuery() {
      this.listQuery.pageNum = 1
      this.getFeedbackList()
    },
    resetQuery() {
      this.listQuery = {
        pageNum: 1,
        pageSize: 10,
        title: '',
        type: null,
        status: ''
      }
      this.getFeedbackList()
    },
    togglePendingOnly() {
      this.listQuery.pageNum = 1
      this.listQuery.status = this.listQuery.status === 'Pending' ? '' : 'Pending'
      this.getFeedbackList()
    },
    applyReplyTemplate(content, status) {
      this.temp.replyContent = content
      this.temp.status = status
    },
    getEnumText(value) {
      if (!value) return '—'
      if (typeof value === 'string') return value
      if (value.msg) return value.msg
      if (value.enumCode) return value.enumCode
      return '—'
    },
    getStatusType(status) {
      const map = {
        Pending: 'warning',
        Processing: 'primary',
        Resolved: 'success',
        Closed: 'info'
      }
      return map[status] || 'info'
    },
    getStatusLabel(status) {
      const map = {
        Pending: '待处理',
        Processing: '处理中',
        Resolved: '已解决',
        Closed: '已关闭'
      }
      return map[status] || status || '—'
    }
  }
}
</script>

<style scoped>
.feedback-page .stats-row {
  margin-bottom: 20px;
}

.stats-card {
  text-align: center;
}

.stats-number {
  font-size: 26px;
  font-weight: 700;
  color: #303133;
}

.stats-label {
  margin-top: 6px;
  color: #909399;
  font-size: 13px;
}

.stats-card.pending {
  border-left: 4px solid #e6a23c;
}

.stats-card.processing {
  border-left: 4px solid #409eff;
}

.stats-card.resolved {
  border-left: 4px solid #67c23a;
}

.stats-card.all {
  border-left: 4px solid #ff7a18;
}

.content-block {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.6;
}

.reply-template-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}
</style>
