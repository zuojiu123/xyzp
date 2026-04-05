<template>
  <div class="company-jobs">
    <div class="container">
      <header class="page-head">
        <div>
          <h1 class="title">职位管理</h1>
          <p class="sub">发布与维护职位，审核通过后将展示在求职者端</p>
        </div>
        <el-button type="primary" icon="el-icon-plus" class="btn-publish" @click="showCreateDialog">发布职位</el-button>
      </header>

      <el-card shadow="never" class="toolbar-card">
        <el-row :gutter="12" type="flex" align="middle">
          <el-col :xs="24" :sm="10" :md="8">
            <el-input
              v-model="filterKeyword"
              placeholder="搜索职位名称、工作地点…"
              clearable
              prefix-icon="el-icon-search"
              @clear="currentPage = 1"
              @input="onFilterChange"
            />
          </el-col>
          <el-col :xs="24" :sm="8" :md="6">
            <el-select v-model="filterStatus" placeholder="职位状态" clearable style="width: 100%" @change="currentPage = 1">
              <el-option label="全部状态" :value="null" />
              <el-option label="待审核" :value="0" />
              <el-option label="已发布" :value="1" />
              <el-option label="已下线" :value="2" />
            </el-select>
          </el-col>
          <el-col :xs="24" :sm="6" :md="10" class="toolbar-meta">
            <span class="meta-text">共 <strong>{{ filteredJobs.length }}</strong> 个职位</span>
            <el-button type="text" icon="el-icon-refresh" @click="loadJobs">刷新</el-button>
          </el-col>
        </el-row>
      </el-card>

      <el-card shadow="never" class="table-card">
        <el-table :data="pagedJobList" v-loading="loading" stripe class="jobs-table" empty-text="暂无职位，点击右上角发布">
          <el-table-column prop="title" label="职位名称" min-width="160" show-overflow-tooltip />
          <el-table-column label="薪资" width="120">
            <template slot-scope="scope">
              {{ scope.row.minSalary }}-{{ scope.row.maxSalary }}K
            </template>
          </el-table-column>
          <el-table-column prop="position" label="工作地点" width="110" show-overflow-tooltip />
          <el-table-column prop="education" label="学历" width="88" />
          <el-table-column label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.status)" size="small">{{ getStatusText(scope.row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="发布时间" width="158">
            <template slot-scope="scope">{{ formatTime(scope.row.createTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="220" fixed="right">
            <template slot-scope="scope">
              <el-button type="text" size="small" @click="previewJob(scope.row)">预览</el-button>
              <el-button type="text" size="small" @click="editJob(scope.row)">编辑</el-button>
              <el-button type="text" size="small" class="btn-danger-text" @click="deleteJob(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination" v-if="filteredTotal > 0">
          <el-pagination
            background
            layout="total, prev, pager, next, jumper"
            :current-page.sync="currentPage"
            :page-size="pageSize"
            :total="filteredTotal"
            @current-change="handlePageChange"
          />
        </div>
      </el-card>

      <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="640px" custom-class="job-dialog" @close="resetForm">
        <el-form :model="jobForm" :rules="jobRules" ref="jobForm" label-width="100px">
          <el-form-item label="职位名称" prop="title">
            <el-input v-model="jobForm.title" placeholder="请输入职位名称" maxlength="80" show-word-limit />
          </el-form-item>
          <el-form-item label="工作地点" prop="position">
            <el-select v-model="jobForm.position" placeholder="请选择工作地点" filterable allow-create default-first-option style="width: 100%">
              <el-option v-for="c in cityOptions" :key="c" :label="c" :value="c" />
            </el-select>
          </el-form-item>
          <el-form-item label="学历要求" prop="education">
            <el-select v-model="jobForm.education" placeholder="请选择学历要求" style="width: 100%">
              <el-option label="不限" value="不限" />
              <el-option label="大专" value="大专" />
              <el-option label="本科" value="本科" />
              <el-option label="硕士" value="硕士" />
              <el-option label="博士" value="博士" />
            </el-select>
          </el-form-item>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="最低薪资" prop="minSalary">
                <el-input v-model="jobForm.minSalary" placeholder="单位 K">
                  <template slot="append">K</template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="最高薪资" prop="maxSalary">
                <el-input v-model="jobForm.maxSalary" placeholder="单位 K">
                  <template slot="append">K</template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="职位描述" prop="description">
            <el-input type="textarea" v-model="jobForm.description" :rows="5" placeholder="职责、要求、团队介绍等" maxlength="2000" show-word-limit />
          </el-form-item>
          <el-form-item label="福利待遇" prop="treatment">
            <el-input type="textarea" v-model="jobForm.treatment" :rows="3" placeholder="五险一金、补贴、假期等（选填）" maxlength="500" show-word-limit />
          </el-form-item>
        </el-form>
        <div slot="footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitJob" :loading="submitting">{{ isEdit ? '保存修改' : '提交发布' }}</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
const CITY_OPTIONS = ['北京', '上海', '广州', '深圳', '杭州', '南京', '苏州', '成都', '武汉', '西安', '重庆', '天津', '青岛', '大连', '厦门', '长沙', '郑州', '济南', '合肥', '福州', '其他']

export default {
  name: 'CompanyJobs',
  data() {
    const numSalary = (rule, value, callback) => {
      const n = Number(value)
      if (value === '' || value == null) {
        callback(new Error('请填写薪资'))
        return
      }
      if (Number.isNaN(n) || n < 0) {
        callback(new Error('请输入有效数字'))
        return
      }
      callback()
    }
    return {
      rawJobs: [],
      filterKeyword: '',
      filterStatus: null,
      currentPage: 1,
      pageSize: 10,
      loading: false,
      dialogVisible: false,
      isEdit: false,
      submitting: false,
      cityOptions: CITY_OPTIONS,
      jobForm: {
        id: '',
        title: '',
        position: '',
        education: '',
        minSalary: '',
        maxSalary: '',
        description: '',
        treatment: ''
      },
      jobRules: {
        title: [{ required: true, message: '请输入职位名称', trigger: 'blur' }],
        position: [{ required: true, message: '请选择或填写工作地点', trigger: 'change' }],
        education: [{ required: true, message: '请选择学历要求', trigger: 'change' }],
        minSalary: [{ required: true, validator: numSalary, trigger: 'blur' }],
        maxSalary: [{ required: true, validator: numSalary, trigger: 'blur' }],
        description: [
          { required: true, message: '请输入职位描述', trigger: 'blur' },
          { min: 20, message: '职位描述不少于 20 字', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    dialogTitle() {
      return this.isEdit ? '编辑职位' : '发布职位'
    },
    filteredJobs() {
      let list = this.rawJobs.slice()
      const kw = (this.filterKeyword || '').trim().toLowerCase()
      if (kw) {
        list = list.filter(
          j =>
            (j.title && String(j.title).toLowerCase().includes(kw)) ||
            (j.position && String(j.position).toLowerCase().includes(kw)) ||
            (j.description && String(j.description).toLowerCase().includes(kw))
        )
      }
      if (this.filterStatus !== null && this.filterStatus !== '') {
        list = list.filter(j => Number(j.status) === Number(this.filterStatus))
      }
      return list
    },
    filteredTotal() {
      return this.filteredJobs.length
    },
    pagedJobList() {
      const start = (this.currentPage - 1) * this.pageSize
      return this.filteredJobs.slice(start, start + this.pageSize)
    }
  },
  mounted() {
    this.loadJobs()
  },
  methods: {
    onFilterChange() {
      this.currentPage = 1
    },
    async loadJobs() {
      this.loading = true
      try {
        const data = await this.$api.employment.getUserJobs(1, 500)
        this.rawJobs = data.list || []
        if (this.currentPage > 1 && (this.currentPage - 1) * this.pageSize >= this.filteredTotal) {
          this.currentPage = 1
        }
      } catch (error) {
        this.$message.error('获取职位列表失败：' + (error.message || '未知错误'))
        this.rawJobs = []
      } finally {
        this.loading = false
      }
    },
    handlePageChange(page) {
      this.currentPage = page
      window.scrollTo({ top: 0, behavior: 'smooth' })
    },
    showCreateDialog() {
      this.isEdit = false
      this.resetForm()
      this.dialogVisible = true
    },
    editJob(job) {
      this.isEdit = true
      this.jobForm = {
        id: job.id,
        title: job.title || '',
        position: job.position || '',
        education: job.education || '',
        minSalary: job.minSalary != null && job.minSalary !== '' ? String(job.minSalary) : '',
        maxSalary: job.maxSalary != null && job.maxSalary !== '' ? String(job.maxSalary) : '',
        description: job.description || '',
        treatment: job.treatment || ''
      }
      this.dialogVisible = true
      this.$nextTick(() => this.$refs.jobForm && this.$refs.jobForm.clearValidate())
    },
    previewJob(job) {
      const route = this.$router.resolve({ name: 'JobDetail', params: { id: job.id } })
      window.open(route.href, '_blank')
    },
    async deleteJob(job) {
      try {
        await this.$confirm('删除后不可恢复，确定删除该职位？', '提示', { type: 'warning' })
        await this.$api.employment.deleteJob(job.id)
        this.$message.success('已删除')
        await this.loadJobs()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    },
    submitJob() {
      this.$refs.jobForm.validate(async valid => {
        if (!valid) return
        const minN = Number(this.jobForm.minSalary)
        const maxN = Number(this.jobForm.maxSalary)
        if (minN > maxN) {
          this.$message.warning('最低薪资不能高于最高薪资')
          return
        }
        const payload = {
          ...this.jobForm,
          minSalary: String(this.jobForm.minSalary),
          maxSalary: String(this.jobForm.maxSalary)
        }
        this.submitting = true
        try {
          if (this.isEdit) {
            await this.$api.employment.updateJob(payload)
            this.$message.success('保存成功')
          } else {
            await this.$api.employment.createJob(payload)
            this.$message.success('已提交，请等待管理员审核')
          }
          this.dialogVisible = false
          await this.loadJobs()
        } catch (error) {
          this.$message.error('操作失败：' + (error.message || '未知错误'))
        } finally {
          this.submitting = false
        }
      })
    },
    resetForm() {
      this.jobForm = {
        id: '',
        title: '',
        position: '',
        education: '',
        minSalary: '',
        maxSalary: '',
        description: '',
        treatment: ''
      }
      this.$nextTick(() => {
        if (this.$refs.jobForm) this.$refs.jobForm.resetFields()
      })
    },
    getStatusType(status) {
      const types = { 0: 'warning', 1: 'success', 2: 'info' }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = { 0: '待审核', 1: '已发布', 2: '已下线' }
      return texts[status] != null ? texts[status] : '未知'
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
    }
  }
}
</script>

<style scoped>
.company-jobs {
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
  margin-bottom: 20px;
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

.btn-publish {
  background: #ff6b00;
  border-color: #ff6b00;
}

.btn-publish:hover {
  background: #ff8533;
  border-color: #ff8533;
}

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

.jobs-table {
  width: 100%;
}

.btn-danger-text {
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

@media (max-width: 768px) {
  .toolbar-meta {
    text-align: left;
  }
}
</style>
