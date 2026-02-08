<template>
  <div class="company-edit-container">
    <div class="container">
      <h2>编辑企业信息</h2>
      <el-form :model="companyForm" :rules="rules" ref="companyForm" label-width="120px" class="company-form">
        <el-form-item label="企业名称" prop="companyName">
          <el-input v-model="companyForm.companyName" placeholder="请输入企业名称" maxlength="100"></el-input>
        </el-form-item>
        
        <el-form-item label="企业规模" prop="companyScale">
          <el-select v-model="companyForm.companyScale" placeholder="请选择企业规模">
            <el-option label="1-50人" value="1-50人"></el-option>
            <el-option label="51-200人" value="51-200人"></el-option>
            <el-option label="201-500人" value="201-500人"></el-option>
            <el-option label="501-1000人" value="501-1000人"></el-option>
            <el-option label="1000人以上" value="1000人以上"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="所属行业" prop="industry">
          <el-select v-model="companyForm.industry" placeholder="请选择所属行业">
            <el-option label="互联网" value="互联网"></el-option>
            <el-option label="金融" value="金融"></el-option>
            <el-option label="教育" value="教育"></el-option>
            <el-option label="医疗" value="医疗"></el-option>
            <el-option label="制造业" value="制造业"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="企业性质" prop="companyNature">
          <el-select v-model="companyForm.companyNature" placeholder="请选择企业性质">
            <el-option label="国有企业" value="国有企业"></el-option>
            <el-option label="民营企业" value="民营企业"></el-option>
            <el-option label="外资企业" value="外资企业"></el-option>
            <el-option label="合资企业" value="合资企业"></el-option>
            <el-option label="事业单位" value="事业单位"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="所在地区" prop="companyAddress">
          <el-input v-model="companyForm.companyAddress" placeholder="请输入所在地区" maxlength="200"></el-input>
        </el-form-item>
        
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input v-model="companyForm.detailAddress" placeholder="请输入详细地址" maxlength="200"></el-input>
        </el-form-item>
        
        <el-form-item label="企业简介" prop="companyProfile">
          <el-input
            v-model="companyForm.companyProfile"
            type="textarea"
            placeholder="请输入企业简介"
            :rows="4"
            maxlength="1000"
          ></el-input>
        </el-form-item>
        
        <el-form-item label="联系电话" prop="companyPhone">
          <el-input v-model="companyForm.companyPhone" placeholder="请输入联系电话" maxlength="20"></el-input>
        </el-form-item>
        
        <el-form-item label="电子邮箱" prop="companyEmail">
          <el-input v-model="companyForm.companyEmail" placeholder="请输入电子邮箱" maxlength="100"></el-input>
        </el-form-item>
        
        <el-form-item label="官网地址" prop="companyWebsite">
          <el-input v-model="companyForm.companyWebsite" placeholder="请输入官网地址" maxlength="200"></el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submitForm">提交修改</el-button>
          <el-button @click="resetForm">重置</el-button>
          <el-button @click="goBack">返回</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import api from '@/api'

export default {
  name: 'CompanyEdit',
  data() {
    return {
      companyForm: {
        id: '',
        companyName: '',
        companyScale: '',
        industry: '',
        companyNature: '',
        companyAddress: '',
        detailAddress: '',
        companyProfile: '',
        companyPhone: '',
        companyEmail: '',
        companyWebsite: ''
      },
      rules: {
        companyName: [
          { required: true, message: '请输入企业名称', trigger: 'blur' },
          { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
        ],
        companyScale: [
          { required: true, message: '请选择企业规模', trigger: 'change' }
        ],
        industry: [
          { required: true, message: '请选择所属行业', trigger: 'change' }
        ],
        companyNature: [
          { required: true, message: '请选择企业性质', trigger: 'change' }
        ],
        companyAddress: [
          { required: true, message: '请输入所在地区', trigger: 'blur' },
          { min: 2, max: 200, message: '长度在 2 到 200 个字符', trigger: 'blur' }
        ],
        detailAddress: [
          { required: true, message: '请输入详细地址', trigger: 'blur' },
          { min: 2, max: 200, message: '长度在 2 到 200 个字符', trigger: 'blur' }
        ],
        companyProfile: [
          { required: true, message: '请输入企业简介', trigger: 'blur' },
          { min: 10, max: 1000, message: '长度在 10 到 1000 个字符', trigger: 'blur' }
        ],
        companyPhone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        companyEmail: [
          { required: true, message: '请输入电子邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的电子邮箱格式', trigger: 'blur' }
        ],
        companyWebsite: [
          { pattern: /^(https?:\/\/)?([\da-z.-]+)\.([a-z.]{2,6})([/\w .-]*)*\/?$/, message: '请输入正确的官网地址格式', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.getCompanyInfo()
  },
  methods: {
    // 获取企业信息
    getCompanyInfo() {
      const id = this.$route.params.id
      if (id) {
        api.company.getCompanyById(id)
          .then(res => {
            this.companyForm = res
          })
          .catch(err => {
            this.$message.error('获取企业信息失败：' + err.message)
          })
      }
    },
    // 提交表单
    submitForm() {
      this.$refs.companyForm.validate((valid) => {
        if (valid) {
          api.company.updateCompany(this.companyForm)
            .then(res => {
              this.$message.success('企业信息更新成功')
              this.$router.push('/company/profile')
            })
            .catch(err => {
              this.$message.error('企业信息更新失败：' + err.message)
            })
        } else {
          return false
        }
      })
    },
    // 重置表单
    resetForm() {
      this.$refs.companyForm.resetFields()
    },
    // 返回
    goBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
.company-edit-container {
  min-height: 100vh;
  padding: 20px 0;
  background-color: #f5f7fa;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}

.company-form {
  margin-top: 20px;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-button {
  margin-right: 10px;
}
</style>