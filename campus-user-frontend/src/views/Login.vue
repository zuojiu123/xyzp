<template>
  <div class="login-container">
    <div class="login-form">
      <h2>用户登录</h2>
      <el-form :model="loginForm" :rules="rules" ref="loginForm">
        <el-form-item prop="userName">
          <el-input
            v-model="loginForm.userName"
            placeholder="请输入邮箱"
            prefix-icon="el-icon-user"
          ></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="el-icon-lock"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">
            登录
          </el-button>
        </el-form-item>
        <div class="login-footer">
          <span>还没有账号？</span>
          <el-button type="text" @click="$router.push('/register')">立即注册</el-button>
        </div>

      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        userName: '',
        password: ''
      },
      rules: {
        userName: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  mounted() {
    console.log('登录页面加载完成')
    console.log('API对象:', this.$api)
  },
  methods: {
    handleLogin() {
      console.log('开始登录流程')
      this.$refs.loginForm.validate(async (valid) => {
        if (valid) {
          this.loading = true
          try {
            console.log('登录数据:', this.loginForm)
            console.log('API对象:', this.$api)
            const response = await this.login(this.loginForm)
            console.log('登录响应:', response)
            
            if (response && response.token) {
              localStorage.setItem('token', response.token)
              localStorage.setItem('userInfo', JSON.stringify(response.user || {}))
              this.$message.success('登录成功')
              
              // 根据用户角色跳转不同页面
              const userRole = response.user && response.user.role
              if (userRole === 'Enterprise_User') {
                this.$router.push('/company/dashboard')
              } else {
                this.$router.push('/')
              }
            } else {
              console.error('登录响应格式错误:', response)
              this.$message.error('登录响应格式错误')
            }
          } catch (error) {
            console.error('登录错误:', error)
            this.$message.error(error.message || '登录失败')
          } finally {
            this.loading = false
          }
        }
      })
    },
    async login(data) {
      console.log('调用API登录', data)
      if (!this.$api || !this.$api.auth) {
        throw new Error('API对象未初始化')
      }
      try {
        const response = await this.$api.auth.login(data)
        console.log('API返回原始数据:', response)
        return response
      } catch (error) {
        console.error('API调用失败:', error)
        console.error('错误详情:', error.response)
        throw error
      }
    },

  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-form {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
}

.login-form h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  color: #666;
}


</style>