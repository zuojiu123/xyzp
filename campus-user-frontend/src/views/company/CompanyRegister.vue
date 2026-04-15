<template>
  <div class="company-register">
    <div class="container">
      <el-card class="register-card">
        <h2>企业信息完善</h2>
        <el-form ref="form" :model="form" :rules="rules" label-width="120px">
          <el-form-item label="公司名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入公司名称"></el-input>
          </el-form-item>
          <el-form-item label="营业执照号" prop="companyNumber">
            <el-input v-model="form.companyNumber" placeholder="请输入营业执照号"></el-input>
          </el-form-item>
          <el-form-item label="公司规模" prop="number">
            <el-select v-model="form.number" placeholder="请选择公司规模" style="width: 100%">
              <el-option label="1-50人" :value="50"></el-option>
              <el-option label="51-200人" :value="200"></el-option>
              <el-option label="201-500人" :value="500"></el-option>
              <el-option label="501-1000人" :value="1000"></el-option>
              <el-option label="1000人以上" :value="5000"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="公司描述" prop="description">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="4"
              placeholder="请简单介绍一下公司情况"
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="loading" @click="submitForm">
              提交企业信息
            </el-button>
            <el-button @click="$router.go(-1)">返回</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CompanyRegister',
  data() {
    return {
      existingCompanyId: '',
      form: {
        name: '',
        companyNumber: '',
        number: '',
        description: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入公司名称', trigger: 'blur' }
        ],
        companyNumber: [
          { required: true, message: '请输入营业执照号', trigger: 'blur' }
        ],
        number: [
          { required: true, message: '请选择公司规模', trigger: 'change' }
        ],
        description: [
          { required: true, message: '请输入公司描述', trigger: 'blur' },
          { min: 20, message: '公司描述不能少于20个字符', trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  async mounted() {
    await this.loadExistingCompany()
  },
  methods: {
    async loadExistingCompany() {
      try {
        const company = await this.$api.company.getCurrentCompany()
        if (!company || !company.id) {
          return
        }
        this.existingCompanyId = company.id
        this.form = {
          name: company.name || '',
          companyNumber: company.companyNumber || '',
          number: company.number || '',
          description: company.description || ''
        }
      } catch (error) {
        this.existingCompanyId = ''
      }
    },
    submitForm() {
      this.$refs.form.validate(async (valid) => {
        if (!valid) return

        this.loading = true
        try {
          if (this.existingCompanyId) {
            await this.$api.company.updateCompany({
              ...this.form,
              id: this.existingCompanyId
            })
          } else {
            await this.$api.company.createCompany(this.form)
          }
          this.$message.success('企业信息已提交，请等待审核')
          this.$router.push('/company/dashboard')
        } catch (error) {
          this.$message.error('提交失败：' + (error.message || '未知错误'))
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.company-register {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 40px 0;
}

.container {
  max-width: 600px;
  margin: 0 auto;
  padding: 0 20px;
}

.register-card {
  padding: 20px;
}

.register-card h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}
</style>
