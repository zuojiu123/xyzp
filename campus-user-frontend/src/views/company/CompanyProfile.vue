<template>
  <div class="company-profile">
    <div class="container">
      <h2>企业信息管理</h2>
      
      <el-card>
        <el-form :model="companyInfo" :rules="rules" ref="form" label-width="120px">
          <el-form-item label="公司名称" prop="name">
            <el-input v-model="companyInfo.name" placeholder="请输入公司名称"></el-input>
          </el-form-item>
          
          <el-form-item label="营业执照号" prop="companyNumber">
            <el-input v-model="companyInfo.companyNumber" placeholder="请输入营业执照号"></el-input>
          </el-form-item>
          
          <el-form-item label="公司规模" prop="number">
            <el-select v-model="companyInfo.number" placeholder="请选择公司规模">
              <el-option label="1-50人" :value="50"></el-option>
              <el-option label="51-200人" :value="200"></el-option>
              <el-option label="201-500人" :value="500"></el-option>
              <el-option label="501-1000人" :value="1000"></el-option>
              <el-option label="1000人以上" :value="5000"></el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item label="公司描述" prop="description">
            <el-input type="textarea" v-model="companyInfo.description" :rows="6" placeholder="请详细介绍公司情况、发展历程、企业文化等"></el-input>
          </el-form-item>
          
          <el-form-item label="审核状态">
            <el-tag :type="getStatusType(companyInfo.status)">
              {{ getStatusText(companyInfo.status) }}
            </el-tag>
            <div v-if="companyInfo.replyContent" class="reply-content">
              <strong>审核意见：</strong>{{ companyInfo.replyContent }}
            </div>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="updateCompany" :loading="loading">保存修改</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CompanyProfile',
  data() {
    return {
      companyInfo: {
        name: '',
        companyNumber: '',
        number: '',
        description: '',
        status: '',
        replyContent: ''
      },
      rules: {
        name: [{ required: true, message: '请输入公司名称', trigger: 'blur' }],
        companyNumber: [{ required: true, message: '请输入营业执照号', trigger: 'blur' }],
        number: [{ required: true, message: '请选择公司规模', trigger: 'change' }],
        description: [
          { required: true, message: '请输入公司描述', trigger: 'blur' },
          { min: 50, message: '公司描述不能少于50个字符', trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  async mounted() {
    await this.loadCompanyInfo()
  },
  methods: {
    async loadCompanyInfo() {
      try {
        const data = await this.$api.company.getCurrentCompany()
        this.companyInfo = data || {}
      } catch (error) {
        this.$message.error('获取企业信息失败')
      }
    },
    
    updateCompany() {
      this.$refs.form.validate(async (valid) => {
        if (!valid) return
        
        this.loading = true
        try {
          await this.$api.company.updateCompany(this.companyInfo)
          this.$message.success('更新成功')
          await this.loadCompanyInfo()
        } catch (error) {
          this.$message.error('更新失败：' + (error.message || '未知错误'))
        } finally {
          this.loading = false
        }
      })
    },
    
    getStatusType(status) {
      const types = { 
        'Check_Pending': 'warning', 
        'Approve': 'success', 
        'Audit_Failed': 'danger' 
      }
      return types[status] || 'info'
    },
    
    getStatusText(status) {
      const texts = { 
        'Check_Pending': '待审核', 
        'Approve': '已通过', 
        'Audit_Failed': '已拒绝' 
      }
      return texts[status] || '未知'
    }
  }
}
</script>

<style scoped>
.company-profile {
  padding: 20px;
  background: #f5f5f5;
  min-height: calc(100vh - 120px);
}

.container {
  max-width: 800px;
  margin: 0 auto;
}

h2 {
  margin-bottom: 20px;
  color: #333;
}

.reply-content {
  margin-top: 10px;
  padding: 10px;
  background: #f5f5f5;
  border-radius: 4px;
  color: #666;
}
</style>
