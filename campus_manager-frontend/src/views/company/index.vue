<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input v-model="listQuery.companyName" placeholder="请输入公司名称" style="width: 200px;" class="filter-item" @keyup.enter.native="handleQuery" />
      <el-select v-model="listQuery.status" placeholder="审核状态" clearable style="width: 140px;" class="filter-item" @change="handleQuery">
        <el-option label="待审核" value="Check_Pending" />
        <el-option label="审核通过" value="Approve" />
        <el-option label="审核未通过" value="Audit_Failed" />
      </el-select>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleQuery">
        查询
      </el-button>
      <el-button class="filter-item" type="info" icon="el-icon-refresh" @click="resetFilter">
        重置
      </el-button>
      <!-- <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="addCompany">
        Add
      </el-button> -->
    </div>
    <el-table v-loading="listLoading" style="margin-top:20px" :data="companyList" element-loading-text="Loading" border fit highlight-current-row :expand-row-keys="expands" :row-key="getRowKeys" @expand-change="expandChange">
      <el-table-column type="expand">
        <template slot-scope="{row}">
          <div class="company-detail">
            <el-row :gutter="20">
              <el-col :span="12">
                <div class="detail-section">
                  <h4>公司信息</h4>
                  <div class="info-item">
                    <span class="label">公司名称：</span>
                    <span class="value">{{ row.name }}</span>
                  </div>
                  <div class="info-item">
                    <span class="label">统一信用编码：</span>
                    <span class="value">{{ row.companyNumber }}</span>
                  </div>
                  <div class="info-item">
                    <span class="label">公司人数：</span>
                    <span class="value">{{ row.number }}人</span>
                  </div>
                  <div class="info-item">
                    <span class="label">公司类别：</span>
                    <span class="value">{{ row.category?.msg || '--' }}</span>
                  </div>
                  <div class="info-item">
                    <span class="label">公司性质：</span>
                    <span class="value">{{ row.nature?.msg || '--' }}</span>
                  </div>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="detail-section">
                  <h4>联系信息</h4>
                  <div class="info-item">
                    <span class="label">联系人：</span>
                    <span class="value">{{ row.userName || '--' }}</span>
                  </div>
                  <div class="info-item">
                    <span class="label">联系邮箱：</span>
                    <span class="value">{{ row.userEmail || '--' }}</span>
                  </div>
                  <div class="info-item">
                    <span class="label">联系电话：</span>
                    <span class="value">{{ row.userPhone || '--' }}</span>
                  </div>
                  <div class="info-item">
                    <span class="label">审核人：</span>
                    <span class="value">{{ row.processor || '--' }}</span>
                  </div>
                  <div v-if="row.replyContent" class="info-item">
                    <span class="label">审核意见：</span>
                    <span class="value">{{ row.replyContent }}</span>
                  </div>
                </div>
              </el-col>
            </el-row>
            <div class="detail-section" style="margin-top: 20px;">
              <h4>公司简介</h4>
              <div class="company-description">
                {{ row.description || '暂无公司简介' }}
              </div>
            </div>
            <div class="detail-section" style="margin-top: 20px;">
              <h4>审核日志</h4>
              <el-table
                v-loading="auditLogLoading[row.id]"
                :data="companyAuditLogs[row.id] || []"
                size="mini"
                border
                style="width: 100%;"
              >
                <el-table-column label="审核时间" width="160">
                  <template slot-scope="{ row: log }">
                    <span>{{ log.createTime | parseTime }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="审核人" prop="operatorName" width="120" />
                <el-table-column label="原状态" prop="beforeStatus" width="120" />
                <el-table-column label="新状态" prop="afterStatus" width="120" />
                <el-table-column label="审核说明" prop="content" min-width="180" show-overflow-tooltip />
              </el-table>
              <div v-if="!auditLogLoading[row.id] && !(companyAuditLogs[row.id] && companyAuditLogs[row.id].length)" class="empty-log">
                暂无审核日志
              </div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column align="center" type="index" />
      <el-table-column align="center" label="公司名称" prop="name" min-width="150" />
      <el-table-column align="center" label="统一信用编码" prop="companyNumber" width="180" />
      <el-table-column align="center" label="联系人" prop="userName" width="100">
        <template slot-scope="{row}">
          <span>{{ row.userName || '--' }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="公司人数" prop="number" width="100">
        <template slot-scope="{row}">
          <span>{{ row.number || '--' }}人</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="公司类别" width="120">
        <template slot-scope="{row}">
          <span>{{ row.category?.msg || '--' }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="公司性质" width="120">
        <template slot-scope="{row}">
          <span>{{ row.nature?.msg || '--' }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="状态" width="100">
        <template slot-scope="{row}">
          <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="创建时间" width="150">
        <template slot-scope="{row}">
          <span>{{ row.createTime | parseTime }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="审核人" prop="processor" width="100">
        <template slot-scope="{row}">
          <span>{{ row.processor || '--' }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作" width="260">
        <template slot-scope="row">
          <el-button v-if="statusCode(row.row.status) === 'Check_Pending'" type="primary" size="mini" @click="handleUpdate(row)">
            审核
          </el-button>
          <el-button v-if="statusCode(row.row.status) === 'Check_Pending'" type="success" size="mini" plain @click="quickApprove(row)">
            一键通过
          </el-button>
          <el-tag v-if="statusCode(row.row.status) === 'Approve'" type="success">
            审核通过
          </el-tag>
          <el-tag v-if="statusCode(row.row.status) === 'Audit_Failed'" type="danger">
            审核未通过
          </el-tag>
          <el-popconfirm title="确定要删除此条数据吗？" @confirm="handleDelete(row)">
            <el-button slot="reference" size="mini" type="danger">
              删除
            </el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="listQuery.pageNum" :limit.sync="listQuery.pageSize" @pagination="loadCompanyList" />

    <!-- 弹框 -->
    <el-dialog :visible.sync="dialogFormVisible" :title="dialogStatus === 'create' ? '新增企业' : '企业审核'">
      <el-form ref="dataForm" :model="temp" label-position="left" label-width="70px" style="width: 400px; margin-left:50px;">
        <el-form-item label="信用编码" prop="companyNumber">
          <el-input v-model="temp.companyNumber" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="公司名称" prop="name">
          <el-input v-model="temp.name" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="公司描述" prop="description">
          <el-input v-model="temp.description" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="temp.status" class="filter-item" placeholder="请选择">
            <el-option v-for="item in companyStatusEnum" :key="item.enumCode" :label="item.msg" :value="item.enumCode" />
          </el-select>
        </el-form-item>
        <el-form-item label="回复内容" prop="replyContent">
          <el-input v-model="temp.replyContent" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="公司人数" prop="number">
          <el-input v-model="temp.number" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="公司类别" prop="category">
          <el-select v-model="temp.category" class="filter-item" placeholder="请选择">
            <el-option v-for="item in companyCategoryEnum" :key="item.enumCode" :label="item.msg" :value="item.enumCode" />
          </el-select>
        </el-form-item>
        <el-form-item label="公司性质" prop="nature">
          <el-select v-model="temp.nature" class="filter-item" placeholder="请选择">
            <el-option v-for="item in companyNatureEnum" :key="item.enumCode" :label="item.msg" :value="item.enumCode" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">
          取消
        </el-button>
        <el-button type="primary" @click="submitCompanyDialog">
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
      expands: [],
      type: 'add',
      listQuery: {
        pageNum: 1,
        pageSize: 10,
        companyName: '',
        status: null
      },
      listLoading: true,
      companyName: '',
      dialogFormVisible: false,
      dialogStatus: '',
      temp: {
        companyNumber: '',
        name: '',
        description: '',
        status: '',
        replyContent: '',
        number: '',
        category: '',
        nature: ''
      },
      visible: false,
      total: 0,
      auditCompanyId: null,
      companyAuditLogs: {},
      auditLogLoading: {}
    }
  },
  computed: {
    ...mapState({
      companyList: state => state.company.companyList,
      roleList: state => state.enumList.roleList,
      companyStatusEnum: state => state.enumList.companyStatusEnum,
      companyNatureEnum: state => state.enumList.companyNatureEnum,
      companyCategoryEnum: state => state.enumList.companyCategoryEnum
    })
  },
  mounted () {
    this.applyRouteQuery()
    this.loadCompanyList()
    this.$store.dispatch('getCompanyStatusEnum', 'CompanyStatusEnum')
    this.$store.dispatch('getCompanyNatureEnum', 'CompanyNatureEnum')
    this.$store.dispatch('getCompanyCategoryEnum', 'CompanyCategoryEnum')
  },
  methods: {
    getRowKeys (row) {
      return row.id
    },
    expandChange (row, expandedRows) {
      const that = this
      console.log(expandedRows)
      if (expandedRows.length) { // 说明展开了
        that.expands = []
        if (row) {
          that.expands.push(row.id)// 只展开当前行id
          that.loadCompanyAuditLogs(row.id)
        }
      } else { // 说明收起了
        that.expands = []
      }
    },
    loadCompanyAuditLogs (companyId) {
      if (!companyId) return
      this.$set(this.auditLogLoading, companyId, true)
      getAuditLogList({
        pageNum: 1,
        pageSize: 20,
        condition: {
          targetType: 'COMPANY',
          targetId: companyId
        }
      }).then(res => {
        this.$set(this.companyAuditLogs, companyId, res.list || [])
      }).catch(() => {
        this.$set(this.companyAuditLogs, companyId, [])
      }).finally(() => {
        this.$set(this.auditLogLoading, companyId, false)
      })
    },
    // 获取用户角色枚举
    getRoleList () {
      this.$store.dispatch('getCompanyRoleList', 'CompanyRoleEnum')
    },

    applyRouteQuery () {
      const q = this.$route.query || {}
      if (q.status) {
        this.listQuery.status = q.status
      }
    },

    loadCompanyList () {
      this.listLoading = true
      const condition = {}
      if (this.listQuery.companyName) condition.name = this.listQuery.companyName
      if (this.listQuery.status != null && this.listQuery.status !== '') {
        condition.status = this.listQuery.status
      }
      const params = {
        pageNum: this.listQuery.pageNum,
        pageSize: this.listQuery.pageSize,
        condition
      }
      this.$store.dispatch('queryCompany', params).then(res => {
        this.total = res.total
      }).catch(err => {
        this.$message.error((err && err.message) || '加载企业列表失败')
      }).finally(() => {
        this.listLoading = false
      })
    },

    resetFilter () {
      this.listQuery = {
        pageNum: 1,
        pageSize: 10,
        companyName: '',
        status: null
      }
      this.loadCompanyList()
    },

    // 清空form表单
    resetrForm () {
      this.temp = {
        companyNumber: '',
        name: '',
        description: '',
        status: '',
        replyContent: '',
        number: '',
        category: '',
        nature: ''
      }
    },

    // 添加用户弹窗
    addCompany () {
      this.type = 'add'
      this.resetrForm()
      this.dialogFormVisible = true
      this.dialogStatus = 'create'
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    // 审核/编辑弹窗
    handleUpdate (row) {
      this.type = ''
      this.dialogFormVisible = true
      this.auditCompanyId = row.row.id
      this.temp = Object.assign({}, row.row)
      const pickEnum = (v) => (v && typeof v === 'object' && v.enumCode) ? v.enumCode : v
      this.temp.status = pickEnum(row.row.status)
      this.temp.nature = pickEnum(row.row.nature)
      this.temp.category = pickEnum(row.row.category)
      this.dialogStatus = 'update'
    },

    submitCompanyDialog () {
      if (this.dialogStatus === 'create') {
        this.addData()
        return
      }
      if (!this.auditCompanyId) {
        this.$message.error('缺少企业 ID')
        return
      }
      const payload = Object.assign({}, this.temp, { id: this.auditCompanyId })
      this.$store.dispatch('updateCompany', payload).then(() => {
        this.loadCompanyList()
        this.$notify({ title: '成功', message: '保存成功', type: 'success', duration: 2000 })
        this.dialogFormVisible = false
      }).catch(err => {
        this.$message.error((err && err.message) || '保存失败')
      })
    },

    // 删除用户
    handleDelete (row) {
      this.$store.dispatch('deleteCompany', row.row.id).then(res => {
        this.loadCompanyList()
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
      this.$store.dispatch('addCompany', this.temp).then(res => {
        this.resetrForm()
        this.loadCompanyList()
        this.$notify({
          title: 'Success',
          message: '添加成功',
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
      }).catch(err => {
        this.$message.error(err)
      })
    },
    quickApprove (row) {
      this.$store.dispatch('updateCompany', { id: row.row.id, status: 'Approve' }).then(() => {
        this.loadCompanyList()
        this.$notify({ title: '成功', message: '已通过审核', type: 'success', duration: 2000 })
      }).catch(err => {
        this.$message.error((err && err.message) || '操作失败')
      })
    },

    handleQuery () {
      this.listQuery.pageNum = 1
      this.loadCompanyList()
    },

    statusCode (status) {
      if (status == null) return ''
      if (typeof status === 'object' && status.enumCode) return status.enumCode
      return status
    },

    // 获取状态文本
    getStatusText(status) {
      const code = this.statusCode(status)
      const statusMap = {
        'Check_Pending': '待审核',
        'Approve': '审核通过',
        'Audit_Failed': '审核失败'
      }
      return statusMap[code] || (typeof status === 'object' && status.msg) || code || '--'
    },

    // 获取状态标签类型
    getStatusType(status) {
      const code = this.statusCode(status)
      const typeMap = {
        'Check_Pending': 'warning',
        'Approve': 'success',
        'Audit_Failed': 'danger'
      }
      return typeMap[code] || ''
    }
  }

}
</script>
<style lang="scss" scoped>
.company-detail {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;

  .detail-section {
    h4 {
      color: #303133;
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 15px;
      border-bottom: 2px solid #409eff;
      padding-bottom: 5px;
    }

    .info-item {
      display: flex;
      margin-bottom: 10px;

      .label {
        width: 120px;
        color: #606266;
        font-weight: 500;
      }

      .value {
        flex: 1;
        color: #303133;
      }
    }

    .company-description {
      color: #606266;
      line-height: 1.6;
      padding: 15px;
      background: white;
      border-radius: 6px;
      border-left: 4px solid #409eff;
    }

    .empty-log {
      padding-top: 8px;
      color: #909399;
      font-size: 13px;
    }
  }
}

.filter-container {
  margin-bottom: 20px;

  .filter-item {
    margin-right: 10px;
  }
}
</style>
