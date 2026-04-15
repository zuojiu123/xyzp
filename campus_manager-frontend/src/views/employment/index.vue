<template>
  <div class="app-container">
    <div class="filter-container">
      <!-- 筛选条件 -->
      <el-input v-model="listQuery.title" placeholder="请输入职位名称" style="width: 150px;" class="filter-item" @keyup.enter.native="handleQuery" />
      <el-input v-model="listQuery.position" placeholder="工作地点" style="width: 120px;" class="filter-item" @keyup.enter.native="handleQuery" />
      <el-select v-model="listQuery.education" placeholder="学历要求" clearable style="width: 120px;" class="filter-item">
        <el-option v-for="item in educationOptions" :key="item.enumCode" :label="item.msg" :value="item.enumCode" />
      </el-select>
      <el-select v-model="listQuery.status" placeholder="审核状态" clearable style="width: 120px;" class="filter-item">
        <el-option label="待审核" :value="0" />
        <el-option label="审核通过" :value="1" />
        <el-option label="驳回" :value="2" />
      </el-select>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleQuery">
        查询
      </el-button>
      <el-button class="filter-item" type="info" icon="el-icon-refresh" @click="resetQuery">
        重置
      </el-button>
    </div>
    <!-- 快速筛选按钮 -->
    <div class="quick-filter" style="margin-top: 10px;">
      <span style="margin-right: 10px; color: #606266;">快速筛选：</span>
      <el-button-group>
        <el-button size="small" :type="listQuery.status === null ? 'primary' : ''" @click="filterByStatus(null)">
          全部 ({{ total }})
        </el-button>
        <el-button size="small" :type="listQuery.status === 0 ? 'warning' : ''" @click="filterByStatus(0)">
          待审核
        </el-button>
        <el-button size="small" :type="listQuery.status === 1 ? 'success' : ''" @click="filterByStatus(1)">
          已发布
        </el-button>
        <el-button size="small" :type="listQuery.status === 2 ? 'danger' : ''" @click="filterByStatus(2)">
          已驳回
        </el-button>
      </el-button-group>
    </div>
    <el-table v-loading="listLoading" style="margin-top:20px" :data="displayEmploymentList" element-loading-text="Loading" border fit highlight-current-row :expand-row-keys="expands" :row-key="getRowKeys" @expand-change="expandChange">
      <el-table-column type="expand">
        <template slot-scope="{row}">
          <el-descriptions title="职位详情" :column="2" border>
            <el-descriptions-item label="工作地点">
              {{ row.position || '--' }}
            </el-descriptions-item>
            <el-descriptions-item label="薪资范围">
              <el-tag type="warning">
                {{ row.minSalary }}-{{ row.maxSalary }}k
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="学历要求">
              {{ getEducationText(row.education) }}
            </el-descriptions-item>
            <el-descriptions-item label="福利待遇">
              {{ row.treatment || '暂无' }}
            </el-descriptions-item>
            <el-descriptions-item label="职位描述" :span="2">
              {{ row.description || '暂无描述' }}
            </el-descriptions-item>
          </el-descriptions>
        </template>
      </el-table-column>
      <el-table-column align="center" label="ID" prop="id" width="80" />
      <el-table-column align="center" label="职位标题" prop="title" width="150" show-overflow-tooltip />
      <el-table-column align="center" label="工作地点" prop="position" width="120" show-overflow-tooltip />
      <el-table-column align="center" label="学历" width="120">
        <template slot-scope="{row}">
          {{ getEducationText(row.education) }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="薪资" width="100">
        <template slot-scope="{row}">
          <span v-if="row.minSalary && row.maxSalary">
            {{ row.minSalary }}-{{ row.maxSalary }}k
          </span>
          <span v-else>{{ row.salary || '--' }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="福利待遇" prop="treatment" width="120" show-overflow-tooltip />
      <el-table-column align="center" label="发布者" prop="userId" width="100" show-overflow-tooltip>
        <template slot-scope="{row}">
          <span>{{ row.userId || '—' }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="创建时间" width="120">
        <template slot-scope="{row}">
          <span>{{ row.createTime | parseTime }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="审核状态" prop="status">
        <template slot-scope="{row}">
          <el-tag v-if="row.status === 0" type="warning" effect="plain">
            <i class="el-icon-time" /> 待审核
          </el-tag>
          <el-tag v-if="row.status === 1" type="success" effect="plain">
            <i class="el-icon-check" /> 已发布
          </el-tag>
          <el-tag v-if="row.status === 2" type="danger" effect="plain">
            <i class="el-icon-close" /> 已驳回
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="所属公司" prop="companyId">
        <template slot-scope="{row}">
          <span>
            {{ row.companyModel && row.companyModel.name }}
          </span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作" width="200" fixed="right">
        <template slot-scope="{row}">
          <el-dropdown>
            <el-button type="primary" size="mini">
              操作<i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item v-if="row.status === 0" @click.native="approveJob(row)">
                <i class="el-icon-check" /> 通过审核
              </el-dropdown-item>
              <el-dropdown-item v-if="row.status === 0" @click.native="rejectJob(row)">
                <i class="el-icon-close" /> 驳回审核
              </el-dropdown-item>
              <el-dropdown-item v-if="row.status === 2" divided @click.native="reactivateJob(row)">
                <i class="el-icon-refresh-left" /> 驳回后重新提交
              </el-dropdown-item>
              <el-dropdown-item @click.native="viewJob(row)">
                <i class="el-icon-view" /> 查看详情
              </el-dropdown-item>
              <el-dropdown-item @click.native="editJob(row)">
                <i class="el-icon-edit" /> 编辑信息
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.pageNum" :limit.sync="listQuery.pageSize" @pagination="getEmploymentList" />

    <!-- 弹框 -->
    <el-dialog :visible.sync="dialogFormVisible" :title="getDialogTitle()" width="700px">
      <el-form ref="dataForm" :rules="rules" :model="temp" label-position="left" label-width="100px" style="width: 600px; margin-left:20px;">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="职位标题" prop="title">
              <el-input v-model="temp.title" placeholder="请输入职位标题" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工作地点" prop="position">
              <el-input v-model="temp.position" placeholder="请输入工作地点" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学历要求" prop="education">
              <el-select v-model="temp.education" class="filter-item" placeholder="请选择学历" style="width: 100%;">
                <el-option v-for="item in educationOptions" :key="item.enumCode" :label="item.msg" :value="item.enumCode" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="薪资范围">
              <el-row :gutter="10">
                <el-col :span="11">
                  <el-input v-model="temp.minSalary" placeholder="最低薪资" />
                </el-col>
                <el-col :span="2" style="text-align: center; line-height: 32px;">
            -
          </el-col>
                <el-col :span="11">
                  <el-input v-model="temp.maxSalary" placeholder="最高薪资" />
                </el-col>
              </el-row>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="type !== 'add'" :gutter="20">
          <el-col :span="8">
            <el-form-item label="职位ID">
              <span>{{ temp.id || '--' }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="用户ID">
              <span>{{ temp.userId || '--' }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="公司ID">
              <span>{{ temp.companyId || '--' }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="type !== 'add'" :gutter="20">
          <el-col :span="8">
            <el-form-item label="审核状态">
              <el-select v-if="type === 'edit'" v-model="temp.status" style="width: 100%;">
                <el-option label="待审核" :value="0">
                  <el-tag type="warning" size="mini">
                    待审核
                  </el-tag>
                </el-option>
                <el-option label="审核通过" :value="1">
                  <el-tag type="success" size="mini">
                    审核通过
                  </el-tag>
                </el-option>
                <el-option label="已驳回" :value="2">
                  <el-tag type="danger" size="mini">
                    已驳回
                  </el-tag>
                </el-option>
              </el-select>
              <div v-else>
                <el-tag v-if="temp.status === 0" type="warning">
                  待审核
                </el-tag>
                <el-tag v-if="temp.status === 1" type="success">
                  审核通过
                </el-tag>
                <el-tag v-if="temp.status === 2" type="danger">
                  已驳回
                </el-tag>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="创建时间">
              <span>{{ temp.createTime | parseTime }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="修改时间">
              <span>{{ temp.updateTime | parseTime }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item v-if="type !== 'add'" label="所属公司">
          <span>{{ temp.companyModel && temp.companyModel.name || '--' }}</span>
        </el-form-item>
        <el-form-item label="福利待遇">
          <el-input v-model="temp.treatment" type="textarea" :rows="3" placeholder="请输入福利待遇" />
        </el-form-item>
        <el-form-item label="职位描述" prop="description">
          <el-input v-model="temp.description" type="textarea" :rows="4" placeholder="请输入职位描述" />
        </el-form-item>
        <el-form-item v-if="type !== 'add'" label="审核日志">
          <el-table
            v-loading="auditLogLoading"
            :data="auditLogs"
            size="mini"
            border
            style="width: 100%;"
          >
            <el-table-column label="审核时间" width="160">
              <template slot-scope="{ row }">
                <span>{{ row.createTime | parseTime }}</span>
              </template>
            </el-table-column>
            <el-table-column label="审核人" prop="operatorName" width="120" />
            <el-table-column label="原状态" prop="beforeStatus" width="110" />
            <el-table-column label="新状态" prop="afterStatus" width="110" />
            <el-table-column label="审核说明" prop="content" min-width="180" show-overflow-tooltip />
          </el-table>
          <div v-if="!auditLogLoading && !auditLogs.length" class="empty-log">
            暂无审核日志
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">
          {{ type === 'view' ? '关闭' : '取消' }}
        </el-button>
        <el-button v-if="type !== 'view'" type="primary" @click="dialogStatus==='create'?addData():updateData()">
          确定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import Pagination from '@/components/Pagination'
import { parseTime } from '@/utils/time'
import { getAuditLogList } from '@/api/auditLog'

export default {
  components: { Pagination },
  filters: {
    parseTime (val) {
      return parseTime(val, '{y}-{m}-{d} {h}:{i}') || '--'
    }
  },
  data () {
    return {
      defaultEducationOptions: [
        { enumCode: 'College_Degree_Or_Above', msg: '专科及以上' },
        { enumCode: 'Bachelor_Or_Above_Degree', msg: '本科及以上' },
        { enumCode: 'Master_Degree_Or_Above', msg: '硕士及以上' }
      ],
      expands: [],
      type: 'add',
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        title: '',
        position: '',
        education: '',
        status: null
      },
      listLoading: true,
      ExamName: '',
      dialogFormVisible: false,
      dialogStatus: '',
      temp: {
        title: '',
        description: '',
        education: '',
        position: '',
        minSalary: '',
        maxSalary: '',
        treatment: '',
        status: 0,
        companyModel: null
      },
      visible: false,
      total: 0,
      displayEmploymentList: [],
      auditLogs: [],
      auditLogLoading: false,
      rules: {
        title: [
          { required: true, message: '请输入职位标题', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '请输入职位描述', trigger: 'blur' }
        ],
        education: [
          { required: true, message: '请选择学历要求', trigger: 'change' }
        ],
        position: [
          { required: true, message: '请输入工作地点', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    ...mapState({
      employmentList: state => state.employment.employmentList,
      employmentEducationEnum: state => state.enumList.employmentEducationEnum,
      employmentReplyStatus: state => state.enumList.employmentReplyStatus
      // employmentUserStatus: state => state.enumList.employmentUserStatus
    }),
    educationOptions () {
      return this.employmentEducationEnum && this.employmentEducationEnum.length
        ? this.employmentEducationEnum
        : this.defaultEducationOptions
    }
  },
  mounted () {
    this.applyRouteQuery()
    this.getEmploymentList()
    this.$store.dispatch('getEmploymentEducationEnum', 'EmploymentEducationEnum')
    this.$store.dispatch('getEmploymentReplyStatus', 'EmploymentReplyStatus')
  },
  methods: {
    getRowKeys (row) {
      return row.id
    },
    applyRouteQuery () {
      const q = this.$route.query || {}
      if (q.status !== undefined && q.status !== null && q.status !== '') {
        const n = Number(q.status)
        if (!Number.isNaN(n)) {
          this.listQuery.status = n
        }
      }
    },

    expandChange (row, expandedRows) {
      const that = this
      if (expandedRows.length) { // 说明展开了
        that.expands = []
        if (row) {
          that.expands.push(row.id)// 只展开当前行id
        }
      } else { // 说明收起了
        that.expands = []
      }
    },
    // 获取职位列表（管理端显示所有状态）
    getEmploymentList () {
      this.listLoading = true
      // 构建筛选条件
      const condition = {}
      if (this.listQuery.title) condition.title = this.listQuery.title
      if (this.listQuery.position) condition.position = this.listQuery.position
      if (this.listQuery.education) condition.education = this.listQuery.education
      if (this.listQuery.status !== null && this.listQuery.status !== '') {
        condition.status = this.listQuery.status
      }

      const params = {
        pageNum: this.listQuery.pageNum,
        pageSize: this.listQuery.pageSize,
        condition: Object.keys(condition).length > 0 ? condition : null
      }

      this.$store.dispatch('getEmploymentList', params).then(res => {
        this.displayEmploymentList = this.applyLocalFilters(res.list || [])
        this.total = this.hasActiveFilters() ? this.displayEmploymentList.length : (res.total || 0)
        this.listLoading = false
      }).catch(err => {
        this.$message.error((err && err.message) || '获取职位列表失败')
        this.listLoading = false
      })
    },

    // 清空form表单
    resetrForm () {
      this.temp = {
        title: '',
        description: '',
        education: '',
        position: '',
        minSalary: '',
        maxSalary: '',
        treatment: '',
        status: 0,
        companyModel: null
      }
    },

    // 添加用户弹窗
    addExam () {
      this.type = 'add'
      this.resetrForm()
      this.dialogFormVisible = true
      this.dialogStatus = 'create'
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    // 审核通过
    approveJob (row) {
      if (row.status !== 0) {
        this.$message.warning('仅「待审核」职位可通过审核')
        return
      }
      const statusText = this.getStatusText(row.status)
      this.$confirm(`当前状态：${statusText}\n确认通过该职位审核并发布？`, '审核确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }).then(() => {
        const temp = Object.assign({}, row)
        temp.status = 1
        temp.auditRemark = `审核状态已从“${statusText}”更改为“审核通过”`
        this.updateEmploymentStatus(temp, `审核状态已从“${statusText}”更改为“审核通过”`)
      })
    },
    // 驳回职位
    rejectJob (row) {
      if (row.status !== 0) {
        this.$message.warning('仅「待审核」职位可驳回')
        return
      }
      const statusText = this.getStatusText(row.status)
      this.$prompt(`当前状态：${statusText}\n请输入驳回原因`, '驳回职位', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputType: 'textarea',
        inputValidator: (value) => {
          if (!value || value.trim() === '') {
            return '请输入驳回原因'
          }
          return true
        }
      }).then(({ value }) => {
        const temp = Object.assign({}, row)
        temp.status = 2
        temp.auditRemark = value
        this.updateEmploymentStatus(temp, `审核状态已从“${statusText}”更改为“已驳回”`)
      })
    },
    // 重新审核
    reactivateJob (row) {
      this.$confirm('该职位当前处于“已驳回”状态。确认重新提交后，状态会变回“待审核”，需要管理员再次审核。', '重新提交审核', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const temp = Object.assign({}, row)
        temp.status = 0
        temp.auditRemark = '企业已根据驳回意见修改职位，并重新提交审核'
        this.updateEmploymentStatus(temp, `职位“${row.title}”已重新提交审核，当前状态已变更为“待审核”`)
      })
    },
    // 编辑职位
    editJob (row) {
      this.type = 'edit'
      this.temp = Object.assign({}, row)
      this.dialogFormVisible = true
      this.dialogStatus = 'update'
      this.loadAuditLogs(row.id)
    },
    // 查看职位详情
    viewJob (row) {
      this.type = 'view'
      this.temp = Object.assign({}, row)
      this.dialogFormVisible = true
      this.dialogStatus = 'view'
      this.loadAuditLogs(row.id)
    },
    loadAuditLogs (employmentId) {
      this.auditLogLoading = true
      getAuditLogList({
        pageNum: 1,
        pageSize: 20,
        condition: {
          targetType: 'EMPLOYMENT',
          targetId: employmentId
        }
      }).then(res => {
        this.auditLogs = res.list || []
      }).catch(() => {
        this.auditLogs = []
      }).finally(() => {
        this.auditLogLoading = false
      })
    },
    // 更新职位状态
    updateEmploymentStatus (temp, message) {
      this.$store.dispatch('updateEmployment', temp).then(res => {
        this.$notify({
          title: 'Success',
          message: message,
          type: 'success',
          duration: 2000
        })
        // 刷新列表显示所有状态
        this.getEmploymentList()
        if (this.dialogFormVisible && this.temp && this.temp.id === temp.id) {
          this.temp = Object.assign({}, this.temp, temp)
          this.loadAuditLogs(temp.id)
        }
        if (temp.status === 0 && this.listQuery.status === 2) {
          this.$message.info('该职位已回到待审核状态，若当前仍筛选“已驳回”，它会从当前列表中消失')
        }
      }).catch(err => {
        this.$message.error((err && err.message) ? `操作失败：${err.message}` : '操作失败')
      })
    },

    // 删除用户
    handleDelete (row) {
      this.$store.dispatch('deleteEmployment', row.row.id).then(res => {
        this.getEmploymentList()
        this.$notify({
          title: 'Success',
          message: '删除成功',
          type: 'success',
          duration: 2000
        })
      }).catch(err => {
        this.$message.error(err)
      })
    },

    // 添加用户
    addData () {
      this.$store.dispatch('addEmployment', this.temp).then(res => {
        this.dialogFormVisible = false
        this.resetrForm()
        this.getEmploymentList()
        this.$notify({
          title: 'Success',
          message: '添加成功',
          type: 'success',
          duration: 2000
        })
      }).catch(err => {
        this.$message.error(err)
      })
    },
    // 修改职位
    updateData () {
      this.$refs.dataForm.validate(valid => {
        if (valid) {
          this.$store.dispatch('updateEmployment', this.temp).then(res => {
            this.getEmploymentList()
            this.$notify({
              title: 'Success',
              message: '修改成功',
              type: 'success',
              duration: 2000
            })
            this.dialogFormVisible = false
            this.resetrForm()
          }).catch(err => {
            this.$message.error('修改失败：' + err)
          })
        }
      })
    },

    // 按条件查询（管理端显示所有状态）
    handleQuery () {
      this.listQuery.pageNum = 1
      const condition = {}
      if (this.listQuery.title) condition.title = this.listQuery.title
      if (this.listQuery.position) condition.position = this.listQuery.position
      if (this.listQuery.education) condition.education = this.listQuery.education
      if (this.listQuery.status !== null && this.listQuery.status !== '') {
        condition.status = this.listQuery.status
      }
      const params = {
        pageNum: this.listQuery.pageNum,
        pageSize: this.listQuery.pageSize,
        condition: condition
      }
      this.listLoading = true
      this.$store.dispatch('queryEmployment', params).then(res => {
        this.displayEmploymentList = this.applyLocalFilters(res.list || [])
        this.total = this.displayEmploymentList.length
      }).catch(err => {
        this.$message.error((err && err.message) || '查询失败')
      }).finally(() => {
        this.listLoading = false
      })
    },
    // 重置查询
    resetQuery () {
      this.listQuery = {
        pageNum: 1,
        pageSize: 10,
        title: '',
        position: '',
        education: '',
        status: null
      }
      this.getEmploymentList()
    },
    // 快速筛选方法
    filterByStatus (status) {
      this.listQuery.status = status
      this.listQuery.pageNum = 1
      this.getEmploymentList()
    },
    applyLocalFilters (list) {
      return (list || []).filter(item => {
        const titleMatch = !this.listQuery.title || (item.title || '').includes(this.listQuery.title)
        const positionMatch = !this.listQuery.position || (item.position || '').includes(this.listQuery.position)
        const educationMatch = !this.listQuery.education || this.matchEducation(item.education, this.listQuery.education)
        const statusMatch = this.listQuery.status === null || this.listQuery.status === '' ||
          Number(item.status) === Number(this.listQuery.status)
        return titleMatch && positionMatch && educationMatch && statusMatch
      })
    },
    hasActiveFilters () {
      return Boolean(
        this.listQuery.title ||
        this.listQuery.position ||
        this.listQuery.education ||
        this.listQuery.status === 0 ||
        this.listQuery.status === 1 ||
        this.listQuery.status === 2
      )
    },
    // 获取弹框标题
    getDialogTitle () {
      const titleMap = {
        add: '新增职位',
        edit: '编辑职位',
        view: '查看职位详情'
      }
      return titleMap[this.type] || '职位信息'
    },
    // 获取状态文本
    getStatusText (status) {
      const statusMap = {
        0: '待审核',
        1: '审核通过',
        2: '已驳回'
      }
      return statusMap[status] || '未知状态'
    },
    getEducationText (education) {
      const found = this.educationOptions.find(item => item.enumCode === education)
      return found ? found.msg : (education || '--')
    },
    matchEducation (actualEducation, selectedEducation) {
      if (!selectedEducation) return true
      const actual = actualEducation || ''
      const rankMap = {
        专科: 1,
        '专科及以上': 1,
        本科: 2,
        '本科及以上': 2,
        硕士: 3,
        博士: 4,
        '研究生及以上': 3
      }
      const selectedRankMap = {
        College_Degree_Or_Above: 1,
        Bachelor_Or_Above_Degree: 2,
        Master_Degree_Or_Above: 3
      }
      const actualRank = rankMap[actual]
      const selectedRank = selectedRankMap[selectedEducation]
      if (actualRank && selectedRank) {
        return actualRank >= selectedRank
      }
      return actual === selectedEducation || this.getEducationText(actual) === this.getEducationText(selectedEducation)
    }
  }
}
</script>
<style lang="scss" scoped>
.empty-log {
  padding-top: 8px;
  color: #909399;
  font-size: 13px;
}
.el-descriptions {
  margin: 20px;
}

.quick-filter {
  padding: 10px 0;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 10px;

  .el-button-group {
    .el-button {
      margin-right: 0;
      border-radius: 0;

      &:first-child {
        border-top-left-radius: 4px;
        border-bottom-left-radius: 4px;
      }

      &:last-child {
        border-top-right-radius: 4px;
        border-bottom-right-radius: 4px;
      }
    }
  }
}
</style>
