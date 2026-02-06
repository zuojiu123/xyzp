<template>
  <div class="company-jobs">
    <div class="container">
      <div class="header">
        <h2>职位管理</h2>
        <el-button type="primary" icon="el-icon-plus" @click="showCreateDialog">发布职位</el-button>
      </div>

      <el-card>
        <el-table :data="jobList" border v-loading="loading">
          <el-table-column prop="title" label="职位名称" width="200"></el-table-column>
          <el-table-column label="薪资范围" width="150">
            <template slot-scope="scope">
              {{ scope.row.minSalary }}-{{ scope.row.maxSalary }}K
            </template>
          </el-table-column>
          <el-table-column prop="position" label="工作地点" width="120"></el-table-column>
          <el-table-column prop="education" label="学历要求" width="100"></el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="发布时间" width="180">
            <template slot-scope="scope">
              {{ formatTime(scope.row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template slot-scope="scope">
              <el-button size="mini" @click="editJob(scope.row)">编辑</el-button>
              <el-button size="mini" type="danger" @click="deleteJob(scope.row)">删除</el-button>
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

      <!-- 创建/编辑职位对话框 -->
      <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px" @close="resetForm">
        <el-form :model="jobForm" :rules="jobRules" ref="jobForm" label-width="100px">
          <el-form-item label="职位名称" prop="title">
            <el-input v-model="jobForm.title" placeholder="请输入职位名称"></el-input>
          </el-form-item>
          <el-form-item label="工作地点" prop="position">
            <el-select v-model="jobForm.position" placeholder="请选择工作地点" filterable>
              <el-option label="北京" value="北京"></el-option>
              <el-option label="上海" value="上海"></el-option>
              <el-option label="广州" value="广州"></el-option>
              <el-option label="深圳" value="深圳"></el-option>
              <el-option label="杭州" value="杭州"></el-option>
              <el-option label="南京" value="南京"></el-option>
              <el-option label="苏州" value="苏州"></el-option>
              <el-option label="成都" value="成都"></el-option>
              <el-option label="武汉" value="武汉"></el-option>
              <el-option label="西安" value="西安"></el-option>
              <el-option label="重庆" value="重庆"></el-option>
              <el-option label="天津" value="天津"></el-option>
              <el-option label="青岛" value="青岛"></el-option>
              <el-option label="大连" value="大连"></el-option>
              <el-option label="厦门" value="厦门"></el-option>
              <el-option label="长沙" value="长沙"></el-option>
              <el-option label="郑州" value="郑州"></el-option>
              <el-option label="济南" value="济南"></el-option>
              <el-option label="合肥" value="合肥"></el-option>
              <el-option label="福州" value="福州"></el-option>
              <el-option label="其他" value="其他"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="学历要求" prop="education">
            <el-select v-model="jobForm.education" placeholder="请选择学历要求">
              <el-option label="不限" value="不限"></el-option>
              <el-option label="大专" value="大专"></el-option>
              <el-option label="本科" value="本科"></el-option>
              <el-option label="硕士" value="硕士"></el-option>
              <el-option label="博士" value="博士"></el-option>
            </el-select>
          </el-form-item>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="最低薪资" prop="minSalary">
                <el-input v-model.number="jobForm.minSalary" placeholder="单位：K">
                  <template slot="append">K</template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="最高薪资" prop="maxSalary">
                <el-input v-model.number="jobForm.maxSalary" placeholder="单位：K">
                  <template slot="append">K</template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="职位描述" prop="description">
            <el-input type="textarea" v-model="jobForm.description" :rows="4" placeholder="请详细描述职位要求和工作内容"></el-input>
          </el-form-item>
          <el-form-item label="福利待遇" prop="treatment">
            <el-input type="textarea" v-model="jobForm.treatment" :rows="3" placeholder="请描述福利待遇"></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitJob" :loading="submitting">
            {{ isEdit ? '更新' : '发布' }}
          </el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CompanyJobs',
  data() {
    return {
      jobList: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      dialogVisible: false,
      isEdit: false,
      submitting: false,
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
        position: [{ required: true, message: '请选择工作地点', trigger: 'change' }],
        education: [{ required: true, message: '请选择学历要求', trigger: 'change' }],
        minSalary: [
          { required: true, message: '请输入最低薪资', trigger: 'blur' },
          { type: 'number', message: '请输入数字', trigger: 'blur' }
        ],
        maxSalary: [
          { required: true, message: '请输入最高薪资', trigger: 'blur' },
          { type: 'number', message: '请输入数字', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '请输入职位描述', trigger: 'blur' },
          { min: 20, message: '职位描述不能少于20个字符', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    dialogTitle() {
      return this.isEdit ? '编辑职位' : '发布职位'
    }
  },
  async mounted() {
    await this.loadJobs()
  },
  methods: {
    async loadJobs() {
      this.loading = true
      try {
        console.log('开始加载职位列表...')
        const data = await this.$api.employment.getUserJobs(this.currentPage, this.pageSize)
        console.log('获取到的职位数据:', data)
        this.jobList = data.list || []
        this.total = data.total || 0
        console.log('职位列表数量:', this.jobList.length)
      } catch (error) {
        console.error('获取职位列表失败:', error)
        this.$message.error('获取职位列表失败: ' + (error.message || '未知错误'))
      } finally {
        this.loading = false
      }
    },
    
    handlePageChange(page) {
      this.currentPage = page
      this.loadJobs()
    },
    
    showCreateDialog() {
      this.isEdit = false
      this.dialogVisible = true
    },
    
    editJob(job) {
      this.isEdit = true
      this.jobForm = { ...job }
      this.dialogVisible = true
    },
    
    async deleteJob(job) {
      try {
        await this.$confirm('确定要删除这个职位吗？', '提示', { type: 'warning' })
        await this.$api.employment.deleteJob(job.id)
        this.$message.success('删除成功')
        await this.loadJobs()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
        }
      }
    },
    
    submitJob() {
      this.$refs.jobForm.validate(async (valid) => {
        if (!valid) return
        
        this.submitting = true
        try {
          if (this.isEdit) {
            await this.$api.employment.updateJob(this.jobForm)
            this.$message.success('更新成功')
          } else {
            await this.$api.employment.createJob(this.jobForm)
            this.$message.success('发布成功，等待管理员审核')
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
      if (this.$refs.jobForm) {
        this.$refs.jobForm.resetFields()
      }
    },
    
    getStatusType(status) {
      const types = { 0: 'info', 1: 'success', 2: 'danger' }
      return types[status] || 'info'
    },
    
    getStatusText(status) {
      const texts = { 0: '待审核', 1: '已发布', 2: '已下线' }
      return texts[status] || '未知'
    },
    
    formatTime(timestamp) {
      if (!timestamp) return ''
      return new Date(timestamp).toLocaleString()
    }
  }
}
</script>

<style scoped>
.company-jobs {
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
</style>
