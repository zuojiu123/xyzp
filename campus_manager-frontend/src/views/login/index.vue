<template>
  <div class="login-container">
    <div class="login-background">
      <div class="bg-animation" />
    </div>
    <div class="login-card">
      <div class="login-header">
        <div class="logo">
          <i class="el-icon-s-platform" />
        </div>
        <h2 class="title">
          管理员登录
        </h2>
        <p class="subtitle">
          校园招聘后台管理系统
        </p>
      </div>
      <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
        <el-form-item prop="username">
          <div class="input-wrapper">
            <i class="el-icon-user input-icon" />
            <el-input
              v-model="loginForm.username"
              placeholder="请输入管理员邮箱"
              class="custom-input"
              @keyup.enter.native="handleLogin"
            />
          </div>
        </el-form-item>
        <el-form-item prop="password">
          <div class="input-wrapper">
            <i class="el-icon-lock input-icon" />
            <el-input
              v-model="loginForm.password"
              :type="pwdType"
              placeholder="请输入密码"
              class="custom-input"
              @keyup.enter.native="handleLogin"
            />
            <i
              :class="pwdType === 'password' ? 'el-icon-view' : 'el-icon-hide'"
              class="pwd-icon"
              @click="showPwd"
            />
          </div>
        </el-form-item>
        <el-form-item>
          <el-button
            :loading="loading"
            type="primary"
            class="login-btn"
            @click="handleLogin"
          >
            <span v-if="!loading">立即登录</span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <p>© 2024 校园招聘管理系统 - 仅限管理员使用</p>
      </div>
    </div>
  </div>
</template>

<script>
import { removeToken } from '@/utils/auth'
import { isStoredAdmin } from '@/utils/userRole'

export default {
  name: 'Login',
  data () {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      loginRules: {
        username: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }

        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '长度不小于6 个字符', trigger: 'blur' }
        ]
      },
      loading: false,
      pwdType: 'password',
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function (route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  methods: {
    showPwd () {
      if (this.pwdType === 'password') {
        this.pwdType = ''
      } else {
        this.pwdType = 'password'
      }
    },
    handleLogin () {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.$store.dispatch('login', this.loginForm).then(res => {
            if (isStoredAdmin()) {
              this.$message.success('登录成功！')
              this.$router.push({ path: this.redirect || '/' })
            } else {
              removeToken()
              this.$message.error('只有管理员才能登录后台管理系统')
            }
            this.loading = false
          }).catch(err => {
            this.loading = false
            this.$message.error(err.message || err)
          })
        } else {
          return false
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.login-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

  .bg-animation {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><radialGradient id="a" cx=".5" cy=".5" r=".5"><stop offset="0%" stop-color="%23ffffff" stop-opacity=".1"/><stop offset="100%" stop-color="%23ffffff" stop-opacity="0"/></radialGradient></defs><circle cx="20" cy="20" r="10" fill="url(%23a)"><animate attributeName="cx" values="20;80;20" dur="8s" repeatCount="indefinite"/></circle><circle cx="80" cy="80" r="15" fill="url(%23a)"><animate attributeName="cy" values="80;20;80" dur="10s" repeatCount="indefinite"/></circle></svg>') no-repeat;
    background-size: cover;
    opacity: 0.3;
  }
}

.login-card {
  position: relative;
  width: 420px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
  padding: 40px;
  z-index: 1;
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 40px;

  .logo {
    width: 80px;
    height: 80px;
    background: linear-gradient(135deg, #667eea, #764ba2);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 20px;
    box-shadow: 0 10px 30px rgba(102, 126, 234, 0.3);

    i {
      font-size: 36px;
      color: white;
    }
  }

  .title {
    font-size: 28px;
    font-weight: 600;
    color: #2c3e50;
    margin: 0 0 8px 0;
  }

  .subtitle {
    font-size: 14px;
    color: #7f8c8d;
    margin: 0;
  }
}

.login-form {
  .el-form-item {
    margin-bottom: 25px;

    &:last-child {
      margin-bottom: 0;
    }
  }
}

.input-wrapper {
  position: relative;

  .input-icon {
    position: absolute;
    left: 15px;
    top: 50%;
    transform: translateY(-50%);
    color: #bdc3c7;
    font-size: 18px;
    z-index: 2;
  }

  .pwd-icon {
    position: absolute;
    right: 15px;
    top: 50%;
    transform: translateY(-50%);
    color: #bdc3c7;
    font-size: 18px;
    cursor: pointer;
    z-index: 2;
    transition: color 0.3s;

    &:hover {
      color: #667eea;
    }
  }
}

.custom-input {
  ::v-deep .el-input__inner {
    height: 50px;
    border: 2px solid #ecf0f1;
    border-radius: 12px;
    padding-left: 50px;
    padding-right: 50px;
    font-size: 16px;
    background: #fafbfc;
    transition: all 0.3s;

    &:focus {
      border-color: #667eea;
      background: white;
      box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    }

    &::placeholder {
      color: #bdc3c7;
    }
  }
}

.login-btn {
  width: 100%;
  height: 50px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 10px 25px rgba(102, 126, 234, 0.3);
  }

  &:active {
    transform: translateY(0);
  }
}

.login-footer {
  text-align: center;
  margin-top: 30px;

  p {
    font-size: 12px;
    color: #95a5a6;
    margin: 0;
  }
}

// 响应式设计
@media (max-width: 480px) {
  .login-card {
    width: 90%;
    padding: 30px 20px;
  }

  .login-header .title {
    font-size: 24px;
  }
}
</style>
