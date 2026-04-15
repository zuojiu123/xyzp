<template>
  <div class="main">
    <div class="left">
      <el-form ref="infoForm" :rules="rules" :model="info" label-width="80px" class="demo-ruleForm">
        <el-form-item label="头像">
          <el-upload class="avatar-uploader" action="http://82.157.42.25:5050/file/upload" :show-file-list="false" :on-success="handleAvatarSuccess">
            <img v-if="imageUrl" :src="imageUrl" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="info.userName" disabled />
        </el-form-item>
        <el-form-item label="昵称" prop="nickName">
          <el-input v-model="info.nickName" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="info.phone" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="updateInfo">
            修改
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="right">
      <div class="inputBox">
        <el-form ref="passwordForm" :rules="passwordRules" :model="passwordForm">
          <el-form-item prop="password">
            <el-input v-model="passwordForm.password" clearable type="text" placeholder="新密码" style="width:300px;margin-bottom:30px" />
          </el-form-item>
          <br>
          <el-form-item prop="authCode">
            <el-input v-model="passwordForm.authCode" clearable type="text" placeholder="验证码" style="width:300px;margin-bottom:30px" />
          </el-form-item>
        </el-form>
      </div>
      <el-button
        class="auth-code-btn"
        :class="{ 'is-counting': !isCheck }"
        :disabled="!isCheck || sendingAuthCode"
        @click="getAuthCode"
      >
        {{ !isCheck ? `重新获取（${time}s）` : (sendingAuthCode ? '发送中...' : '获取验证码') }}
      </el-button>
      <el-button type="primary" @click="updatePassword">
        修改密码
      </el-button>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
export default {
  data () {
    return {
      info: {},

      isCheck: true, // 显示获取验证码
      time: 60,
      countdownTimer: null,
      sendingAuthCode: false,
      imageUrl: '',
      passwordForm: {
        password: '',
        authCode: ''
      },
      rules: {
        nickName: [
          { required: true, message: '请输入昵称', trigger: 'blur' },
          { min: 2, max: 6, message: '长度在 2 到 6 个字符', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3456789]\d{9}$/, message: '手机号码格式不正确', trigger: 'blur' }
        ]
      },
      passwordRules: {
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '长度不小于6 个字符', trigger: 'blur' }
        ],
        authCode: [
          { required: true, message: '请输入验证码', trigger: 'blur' }
        ]
      }
    }
  },
  mounted () {
    this.getInfo()
  },
  computed: {
    ...mapState({
      userInfo: state => state.user.userInfo
    })
  },
  methods: {
    handleAvatarSuccess (el) {
      console.log(el)
      this.imageUrl = el.data.accessAddress
      this.$message.success('头像上传成功，点击修改保存')
    },
    getInfo () {
      this.$store.dispatch('getInfo').then(res => {
        console.log(this.userInfo)
        this.info = this.userInfo
        this.imageUrl = this.userInfo.avatar
      })
    },
    updateInfo () {
      this.$refs.infoForm.validate(valid => {
        const params = {
          nickName: this.info.nickName,
          phone: this.info.phone,
          identityNum: this.info.identityNum,
          address: this.info.address,
          avatar: this.imageUrl,
          id: this.userInfo.id
        }
        if (valid) {
          this.$store.dispatch('updateUser', params).then(res => {
            console.log(11111111111)
            this.$message.success('修改成功')
            this.getInfo()
          })
        } else {
          return false
        }
      })
    },

    /* 获取验证码 */
    getAuthCode () {
      if (!this.isCheck || this.sendingAuthCode) {
        return
      }
      const localUser = JSON.parse(localStorage.getItem('userInfo') || '{}')
      const receiver = this.userInfo.userName || this.info.userName || localUser.userName || localUser.email
      if (!receiver) {
        this.$message.error('当前用户邮箱信息缺失')
        return
      }
      this.sendingAuthCode = true
      const params = { receiver }
      this.$store.dispatch('getAuthCode', params).then((msg) => {
        this.$message.success(msg || '验证码发送成功')
        this.isCheck = false
        if (this.countdownTimer) {
          clearInterval(this.countdownTimer)
        }
        this.countdownTimer = setInterval(() => {
          if (this.time > 0) {
            this.time--
          }
          if (this.time === 0) {
            this.isCheck = true
            this.time = 60
            clearInterval(this.countdownTimer)
            this.countdownTimer = null
          }
        }, 1000)
      }).catch((err) => {
        this.$message.error((err && err.message) || '验证码发送失败')
      }).finally(() => {
        this.sendingAuthCode = false
      })
    },
    updatePassword () {
      this.$refs.passwordForm.validate(valid => {
        if (valid) {
          this.$store.dispatch('updatePassword', { password: this.passwordForm.password, inputCode: this.passwordForm.authCode }).then(res => {
            this.$message.success('密码修改成功')
          }).catch(err => {
            console.log(err)
            this.$message.error((err && err.message) || '密码修改失败')
          })
        } else {
          return false
        }
      })
    }
  },
  beforeDestroy () {
    if (this.countdownTimer) {
      clearInterval(this.countdownTimer)
      this.countdownTimer = null
    }
  }
}
</script>
<style lang="scss" scoped>
.word {
  font-weight: 600;
  font-size: 18px;
}
.main {
  // margin: 0 auto;
  // width: 1200px;
  min-height: calc(100vh - 240px);
  background-color: #fff;
  padding: 60px 30px 30px 100px;
  text-align: left;
  display: flex;
  .left {
    // padding-right: 50px;
    flex: 1;
    border-right: 1px solid #ccc;

    display: flex;
    margin-bottom: 40px;
    .avatar-uploader {
      width: 180px;
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
    }
    .avatar-uploader:hover {
      border-color: #409eff;
    }
    .avatar-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 178px;
      height: 178px;
      line-height: 178px;
      text-align: center;
    }
    .avatar {
      width: 178px;
      height: 178px;
      display: block;
    }
    .label {
      margin-right: 20px;
    }
    .avator {
      width: 160px;
      height: 160px;
    }
    .el-input {
      width: 300px;
    }

    .username {
      margin-top: 20px;
    }
  }
  .right {
    flex: 1;
    padding-left: 50px;
    .auth-code-btn {
      min-width: 180px;
      height: 44px;
      margin-bottom: 30px;
      border: 1px solid rgba(47, 128, 237, 0.16);
      border-radius: 14px;
      background: linear-gradient(135deg, #f9fbff 0%, #eef5ff 100%);
      color: #2f80ed;
      font-size: 15px;
      font-weight: 600;
      transition: all .24s ease;
    }

    .auth-code-btn:hover,
    .auth-code-btn:focus {
      border-color: rgba(47, 128, 237, 0.35);
      background: linear-gradient(135deg, #ffffff 0%, #edf4ff 100%);
      color: #1f63c9;
    }

    .auth-code-btn.is-counting,
    .auth-code-btn.is-disabled,
    .auth-code-btn.is-disabled:hover,
    .auth-code-btn.is-disabled:focus {
      border-color: #e3e9f3;
      background: #f7f9fc;
      color: #94a3b8;
    }
  }
}
</style>
