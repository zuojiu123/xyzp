<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <div slot="header" class="card-header">
        <span>个人中心</span>
      </div>

      <el-tabs v-model="activeTab" type="card">
        <el-tab-pane label="个人信息" name="info">
          <el-form ref="userForm" :model="userInfo" :rules="rules" label-width="100px">
            <el-form-item label="邮箱" prop="userName">
              <el-input v-model="userInfo.userName" disabled></el-input>
            </el-form-item>
            <el-form-item label="昵称" prop="nickName">
              <el-input v-model="userInfo.nickName" placeholder="请输入昵称"></el-input>
            </el-form-item>
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="userInfo.realName" placeholder="请输入真实姓名"></el-input>
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="userInfo.phone" placeholder="请输入手机号"></el-input>
            </el-form-item>
            <el-form-item label="身份证号" prop="identityNum">
              <el-input v-model="userInfo.identityNum" placeholder="请输入身份证号"></el-input>
            </el-form-item>
            <el-form-item label="居住地址" prop="address">
              <el-input v-model="userInfo.address" placeholder="请输入居住地址"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="updating" @click="updateUserInfo">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="我的申请" name="applications">
          <el-table :data="applications" style="width: 100%" v-loading="loadingApplications">
            <el-table-column prop="employmentModel.title" label="职位名称" width="180"></el-table-column>
            <el-table-column prop="employmentModel.companyModel.name" label="公司名称" width="180"></el-table-column>
            <el-table-column prop="recruitStage" label="流程阶段" width="130">
              <template slot-scope="scope">
                <el-tag type="info">{{ getStageText(scope.row.recruitStage) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="userStatus" label="申请状态" width="120">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.userStatus || scope.row.replyStatus)">
                  {{ getStatusText(scope.row.userStatus || scope.row.replyStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="申请时间" width="180">
              <template slot-scope="scope">
                {{ formatDate(scope.row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="replyContent" label="回复内容" min-width="220">
              <template slot-scope="scope">
                <span v-if="scope.row.replyContent">{{ scope.row.replyContent }}</span>
                <span v-else class="text-muted">暂无回复</span>
              </template>
            </el-table-column>
            <el-table-column label="流程" width="120">
              <template slot-scope="scope">
                <el-button type="text" @click="openTimeline(scope.row)">查看时间线</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="账户设置" name="settings">
          <el-form ref="passwordForm" :model="passwordForm" :rules="passwordRules" label-width="100px">
            <el-form-item label="新密码" prop="password">
              <el-input v-model="passwordForm.password" type="password" placeholder="请输入新密码"></el-input>
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请确认新密码"></el-input>
            </el-form-item>
            <el-form-item label="验证码" prop="inputCode">
              <el-input v-model="passwordForm.inputCode" placeholder="请输入邮箱验证码">
                <el-button slot="append" @click="sendCode" :disabled="countdown > 0">
                  {{ countdown > 0 ? `${countdown}s` : '获取验证码' }}
                </el-button>
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="updatingPassword" @click="updatePassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog :visible.sync="timelineVisible" title="申请流程时间线" width="680px">
      <div v-loading="timelineLoading" class="timeline-list">
        <el-timeline v-if="applicationTimeline.length">
          <el-timeline-item
            v-for="item in applicationTimeline"
            :key="item.id"
            :timestamp="formatDate(item.createTime)"
            placement="top"
          >
            <div class="timeline-item-title">
              <strong>{{ getFlowActionText(item.actionType) }}</strong>
              <el-tag size="mini" type="info">{{ getStageText(item.stageTo) }}</el-tag>
            </div>
            <p class="timeline-time">
              操作角色：{{ item.operatorRole || '系统' }}
            </p>
            <p>{{ item.content || '状态已更新' }}</p>
          </el-timeline-item>
        </el-timeline>
        <div v-else-if="!timelineLoading" class="timeline-empty">暂无流程记录</div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Profile',
  data() {
    const validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.passwordForm.password) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }

    return {
      activeTab: 'info',
      userInfo: {
        userName: '',
        nickName: '',
        realName: '',
        phone: '',
        identityNum: '',
        address: ''
      },
      applications: [],
      passwordForm: {
        password: '',
        confirmPassword: '',
        inputCode: ''
      },
      rules: {
        nickName: [
          { required: true, message: '请输入昵称', trigger: 'blur' }
        ],
        phone: [
          { pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ]
      },
      passwordRules: {
        password: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { validator: validatePass, trigger: 'blur' }
        ],
        inputCode: [
          { required: true, message: '请输入验证码', trigger: 'blur' }
        ]
      },
      updating: false,
      updatingPassword: false,
      loadingApplications: false,
      countdown: 0,
      timelineVisible: false,
      timelineLoading: false,
      currentTimelineApplication: null,
      applicationTimeline: []
    }
  },
  mounted() {
    this.applyRouteTab()
    this.loadUserInfo()
    this.loadApplications()
  },
  watch: {
    '$route.query.tab'() {
      this.applyRouteTab()
    }
  },
  methods: {
    applyRouteTab() {
      const tab = this.$route.query && this.$route.query.tab
      if (tab && ['info', 'applications', 'settings'].includes(tab)) {
        this.activeTab = tab
      }
    },
    async loadUserInfo() {
      try {
        this.userInfo = await this.$api.profile.getUserInfo()
      } catch (error) {
        this.$message.error('获取用户信息失败')
      }
    },
    async updateUserInfo() {
      this.$refs.userForm.validate(async (valid) => {
        if (!valid) return
        this.updating = true
        try {
          await this.$api.profile.updateUserInfo(this.userInfo)
          this.$message.success('更新成功')
        } catch (error) {
          this.$message.error('更新失败')
        } finally {
          this.updating = false
        }
      })
    },
    async loadApplications() {
      this.loadingApplications = true
      try {
        const response = await this.$api.profile.getApplications()
        this.applications = response.list || []
      } catch (error) {
        this.$message.error('获取申请记录失败: ' + (error.message || '未知错误'))
      } finally {
        this.loadingApplications = false
      }
    },
    async openTimeline(application) {
      this.currentTimelineApplication = application
      this.timelineVisible = true
      this.timelineLoading = true
      this.applicationTimeline = []
      try {
        this.applicationTimeline = await this.$api.employmentUser.getApplicationTimeline(application.id)
      } catch (error) {
        this.$message.error(error.message || '获取流程时间线失败')
      } finally {
        this.timelineLoading = false
      }
    },
    async updatePassword() {
      this.$refs.passwordForm.validate(async (valid) => {
        if (!valid) return
        this.updatingPassword = true
        try {
          await this.$api.user.updatePassword(this.passwordForm)
          this.$message.success('密码修改成功')
          this.passwordForm = { password: '', confirmPassword: '', inputCode: '' }
        } catch (error) {
          this.$message.error(error.message || '密码修改失败')
        } finally {
          this.updatingPassword = false
        }
      })
    },
    async sendCode() {
      const receiver = this.userInfo.userName || this.userInfo.email
      if (!receiver) {
        this.$message.error('当前用户邮箱信息缺失')
        return
      }
      try {
        const message = await this.$api.email.getAuthCode(receiver)
        this.$message.success(message || '验证码发送成功')
        this.startCountdown()
      } catch (error) {
        this.$message.error(error.message || '验证码发送失败')
      }
    },
    startCountdown() {
      this.countdown = 60
      const timer = setInterval(() => {
        this.countdown--
        if (this.countdown <= 0) {
          clearInterval(timer)
        }
      }, 1000)
    },
    getStatusType(status) {
      const statusMap = {
        Wait_For_Reply: 'warning',
        Pass: 'success',
        Reject: 'danger',
        Agree_With_Induction: 'success',
        Refused_Entry: 'danger',
        Rejected: 'danger'
      }
      return statusMap[status] || 'info'
    },
    getStatusText(status) {
      const statusMap = {
        Wait_For_Reply: '待回复',
        Pass: '通过',
        Reject: '拒绝',
        Agree_With_Induction: '通过',
        Refused_Entry: '未通过',
        Rejected: '未通过'
      }
      return statusMap[status] || '未知'
    },
    getStageText(stage) {
      const stageMap = {
        SUBMITTED: '已投递',
        SCREENING: '筛选中',
        INTERVIEW: '面试中',
        OFFER: '待录用',
        HIRED: '已录用',
        REJECTED: '已淘汰',
        WITHDRAWN: '已撤回'
      }
      return stageMap[stage] || stage || '未知阶段'
    },
    getFlowActionText(actionType) {
      const actionMap = {
        SUBMIT: '投递申请',
        STATUS_CHANGE: '状态更新',
        COLLECT: '收藏职位'
      }
      return actionMap[actionType] || actionType || '流程变更'
    },
    formatDate(timestamp) {
      if (!timestamp) return ''
      return new Date(timestamp).toLocaleString()
    }
  }
}
</script>

<style scoped>
.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.profile-card {
  margin-bottom: 20px;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.text-muted {
  color: #999;
}

.el-form {
  max-width: 600px;
}

.timeline-list {
  min-height: 120px;
}

.timeline-item-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.timeline-time {
  color: #909399;
  font-size: 12px;
}

.timeline-empty {
  padding: 20px 0;
  color: #909399;
  text-align: center;
}
</style>
