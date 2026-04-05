<template>
  <div class="company-profile" v-loading="pageLoading">
    <div class="container">
      <header class="page-head">
        <div>
          <h1 class="title">企业资料</h1>
          <p class="sub">信息将展示在求职者端企业主页，请保持真实、完整</p>
        </div>
        <div class="head-actions" v-if="companyInfo.id">
          <el-button type="primary" plain icon="el-icon-view" @click="openPublicPage">预览主页</el-button>
        </div>
      </header>

      <el-alert
        v-if="companyInfo.status === 'Check_Pending'"
        title="资料正在审核中，审核通过前部分功能可能受限。"
        type="warning"
        show-icon
        :closable="false"
        class="alert-block"
      />
      <el-alert
        v-if="companyInfo.status === 'Audit_Failed' && companyInfo.replyContent"
        :title="'审核未通过：' + companyInfo.replyContent"
        type="error"
        show-icon
        :closable="false"
        class="alert-block"
      />

      <el-card shadow="never" class="form-card">
        <div slot="header" class="card-head">企业信息</div>
        <el-form :model="companyInfo" :rules="rules" ref="form" label-width="120px" class="profile-form">
          <h4 class="section-title">工商与基本资料</h4>
          <el-form-item label="公司名称" prop="name">
            <el-input v-model="companyInfo.name" placeholder="与营业执照一致" maxlength="100" show-word-limit />
          </el-form-item>
          <el-form-item label="营业执照号" prop="companyNumber">
            <el-input v-model="companyInfo.companyNumber" placeholder="统一社会信用代码" maxlength="50" />
          </el-form-item>
          <el-form-item label="公司规模" prop="number">
            <el-select v-model="companyInfo.number" placeholder="请选择规模" style="width: 100%">
              <el-option label="1-50 人" :value="50" />
              <el-option label="51-200 人" :value="200" />
              <el-option label="201-500 人" :value="500" />
              <el-option label="501-1000 人" :value="1000" />
              <el-option label="1000 人以上" :value="5000" />
            </el-select>
          </el-form-item>
          <el-form-item label="所属行业" prop="category">
            <el-select v-model="companyInfo.category" placeholder="用于求职者筛选" filterable allow-create default-first-option style="width: 100%">
              <el-option v-for="i in industryOptions" :key="i" :label="i" :value="i" />
            </el-select>
          </el-form-item>
          <el-form-item label="公司性质" prop="nature">
            <el-select v-model="companyInfo.nature" placeholder="请选择（选填）" clearable style="width: 100%">
              <el-option v-for="n in natureOptions" :key="n" :label="n" :value="n" />
            </el-select>
          </el-form-item>

          <h4 class="section-title">品牌介绍</h4>
          <el-form-item label="公司描述" prop="description">
            <el-input
              type="textarea"
              v-model="companyInfo.description"
              :rows="8"
              placeholder="业务方向、团队文化、发展愿景等，建议不少于 50 字"
              maxlength="2000"
              show-word-limit
            />
          </el-form-item>
          <el-form-item label="审核状态">
            <el-tag :type="getStatusType(companyInfo.status)" size="medium">{{ getStatusText(companyInfo.status) }}</el-tag>
            <p v-if="companyInfo.replyContent && companyInfo.status !== 'Audit_Failed'" class="reply-hint">
              审核备注：{{ companyInfo.replyContent }}
            </p>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="saving" @click="updateCompany">保存修改</el-button>
            <el-button @click="loadCompanyInfo">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script>
const INDUSTRIES = ['IT互联网', '金融', '教育', '医疗健康', '电商', '游戏', '房地产', '制造业', '企业服务', '文化传媒', '其他']
const NATURES = ['私营企业', '国有企业', '外资企业', '合资企业', '事业单位', '民营公司']

export default {
  name: 'CompanyProfile',
  data() {
    return {
      pageLoading: false,
      saving: false,
      industryOptions: INDUSTRIES,
      natureOptions: NATURES,
      companyInfo: {
        id: '',
        name: '',
        companyNumber: '',
        number: '',
        category: '',
        nature: '',
        description: '',
        status: '',
        replyContent: ''
      },
      rules: {
        name: [{ required: true, message: '请输入公司名称', trigger: 'blur' }],
        companyNumber: [{ required: true, message: '请输入营业执照号', trigger: 'blur' }],
        number: [{ required: true, message: '请选择公司规模', trigger: 'change' }],
        category: [{ required: true, message: '请选择或填写所属行业', trigger: 'change' }],
        description: [
          { required: true, message: '请输入公司描述', trigger: 'blur' },
          { min: 50, message: '公司描述不少于 50 字', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.loadCompanyInfo()
  },
  methods: {
    openPublicPage() {
      if (!this.companyInfo.id) return
      const route = this.$router.resolve({ name: 'CompanyDetail', params: { id: this.companyInfo.id } })
      window.open(route.href, '_blank')
    },
    async loadCompanyInfo() {
      this.pageLoading = true
      try {
        const data = await this.$api.company.getCurrentCompany()
        if (!data || !data.id) {
          this.$message.warning('请先完成企业注册')
          this.$router.push('/company/register')
          return
        }
        this.companyInfo = {
          id: data.id,
          name: data.name || '',
          companyNumber: data.companyNumber || '',
          number: data.number != null && data.number !== '' ? data.number : '',
          category: data.category || '',
          nature: data.nature || '',
          description: data.description || '',
          status: data.status || '',
          replyContent: data.replyContent || ''
        }
      } catch (error) {
        this.$message.error('获取企业信息失败')
      } finally {
        this.pageLoading = false
      }
    },
    updateCompany() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.saving = true
        try {
          await this.$api.company.updateCompany(this.companyInfo)
          this.$message.success('保存成功')
          await this.loadCompanyInfo()
        } catch (error) {
          this.$message.error('更新失败：' + (error.message || '未知错误'))
        } finally {
          this.saving = false
        }
      })
    },
    getStatusType(status) {
      const types = {
        Check_Pending: 'warning',
        Approve: 'success',
        Audit_Failed: 'danger'
      }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = {
        Check_Pending: '待审核',
        Approve: '已通过',
        Audit_Failed: '审核未通过'
      }
      return texts[status] || '未知'
    }
  }
}
</script>

<style scoped>
.company-profile {
  min-height: calc(100vh - 120px);
  padding: 24px 20px 40px;
  background: #f8fafc;
}

.container {
  max-width: 800px;
  margin: 0 auto;
}

.page-head {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
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

.alert-block {
  margin-bottom: 16px;
  border-radius: 10px;
}

.form-card {
  border-radius: 12px;
  border: 1px solid #f1f5f9;
  margin-bottom: 16px;
}

.form-card >>> .el-card__header {
  padding: 14px 20px;
  background: #fafbfc;
  border-bottom: 1px solid #f1f5f9;
}

.card-head {
  font-weight: 700;
  color: #1e293b;
}

.profile-form {
  padding-top: 8px;
}

.section-title {
  margin: 24px 0 16px;
  font-size: 15px;
  font-weight: 700;
  color: #334155;
  padding-left: 10px;
  border-left: 3px solid #ff6b00;
}

.section-title:first-of-type {
  margin-top: 0;
}

.reply-hint {
  margin: 8px 0 0;
  font-size: 13px;
  color: #64748b;
  line-height: 1.5;
}

.head-actions .el-button--primary.is-plain {
  border-color: #ff6b00;
  color: #ff6b00;
}

.head-actions .el-button--primary.is-plain:hover {
  background: #fff7ed;
}
</style>
