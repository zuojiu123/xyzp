<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input v-model="listQuery.companyName" placeholder="请输入公司名称" style="width: 200px;" class="filter-item" @keyup.enter.native="handleQuery" />
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleQuery">
        查询
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
      <el-table-column align="center" label="操作" width="200">
        <template slot-scope="row">
          <el-button v-if="row.row.status === 'Check_Pending'" type="primary" size="mini" @click="updateData(row)">
            审核
          </el-button>
          <el-tag v-if="row.row.status === 'Approve'" type="success">
            审核通过
          </el-tag>
          <el-tag v-if="row.row.status === 'Audit_Failed'" type="danger">
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

    <pagination v-show="total > 0" :total="total" :page.sync="listQuery.pageNum" :limit.sync="listQuery.pageSize" @pagination="getCompanyList" />

    <!-- 弹框 -->
    <el-dialog :visible.sync="dialogFormVisible" :title="type === 'add'? '新增数据' : '修改数据'">
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
        <el-button type="primary" @click="dialogStatus==='create'?addData():updateData()">
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
        companyName: ''
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
      total: 0
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
    this.getCompanyList()
    // this.getRoleList()
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
        }
      } else { // 说明收起了
        that.expands = []
      }
    },
    // 获取用户角色枚举
    getRoleList () {
      this.$store.dispatch('getCompanyRoleList', 'CompanyRoleEnum')
    },

    // 获取用户列表
    getCompanyList () {
      const params = {
        pageNum: this.listQuery.pageNum,
        pageSize: this.listQuery.pageSize
      }
      this.$store.dispatch('getCompanyList', params).then(res => {
        this.total = res.total
        this.listLoading = false
      }).catch(err => {
        console.log(err)
      })
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
    // 修改用户弹窗
    handleUpdate (row) {
      this.type = ''
      this.dialogFormVisible = true
      this.temp = Object.assign({}, row.row)
      this.temp.status = row.row.status.enumCode
      this.temp.nature = row.row.nature.enumCode
      this.temp.category = row.row.category.enumCode
      this.dialogStatus = 'update'
    },

    // 删除用户
    handleDelete (row) {
      this.$store.dispatch('deleteCompany', row.row.id).then(res => {
        this.getCompanyList()
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
        this.getCompanyList()
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
    // 修改用户
    updateData (row) {
      this.$store.dispatch('updateCompany', { id: row.row.id, status: 'Approve' }).then(res => {
        this.getCompanyList()
        this.$notify({
          title: 'Success',
          message: '修改成功',
          type: 'success',
          duration: 2000
        })
        this.dialogFormVisible = false
      }).catch(err => {
        this.$message.error(err)
      })
    },

    // 按条件查询用户
    handleQuery () {
      const params = {
        pageNum: this.listQuery.pageNum,
        pageSize: this.listQuery.pageSize,
        condition: { name: this.listQuery.companyName }
      }
      this.$store.dispatch('queryCompany', params).then(res => {
        this.total = res.total
      }).catch(err => {
        console.log(err)
      })
    },

    // 获取状态文本
    getStatusText(status) {
      const statusMap = {
        'Check_Pending': '待审核',
        'Approve': '审核通过',
        'Audit_Failed': '审核失败'
      }
      return statusMap[status] || status
    },

    // 获取状态标签类型
    getStatusType(status) {
      const typeMap = {
        'Check_Pending': 'warning',
        'Approve': 'success',
        'Audit_Failed': 'danger'
      }
      return typeMap[status] || ''
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
  }
}

.filter-container {
  margin-bottom: 20px;

  .filter-item {
    margin-right: 10px;
  }
}
</style>
