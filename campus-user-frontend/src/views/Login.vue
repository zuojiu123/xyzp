<template>
  <div class="auth-page auth-page--login">
    <div class="auth-page__glow auth-page__glow--one"></div>
    <div class="auth-page__glow auth-page__glow--two"></div>

    <div class="auth-layout">
      <section class="auth-showcase">
        <div class="brand-chip">
          <img :src="logoUrl" alt="青梧引校招平台" class="brand-chip__logo">
          <span>青梧引校招平台</span>
        </div>

        <div class="showcase-copy">
          <p class="showcase-copy__eyebrow">Campus Recruitment Hub</p>
          <h1>让每一次校招登录，都成为接近理想 Offer 的起点</h1>
          <p class="showcase-copy__text">
            统一查看职位、企业和投递进度，用更轻盈的体验开启你的求职旅程。
          </p>
        </div>

        <div class="showcase-stats">
          <div
            v-for="item in highlights"
            :key="item.label"
            class="showcase-stat"
          >
            <strong>{{ item.value }}</strong>
            <span>{{ item.label }}</span>
          </div>
        </div>

        <div class="showcase-points">
          <div
            v-for="item in featureList"
            :key="item.title"
            class="showcase-point"
          >
            <div class="showcase-point__icon">
              <i :class="item.icon"></i>
            </div>
            <div>
              <h3>{{ item.title }}</h3>
              <p>{{ item.text }}</p>
            </div>
          </div>
        </div>
      </section>

      <section class="auth-panel">
        <div class="auth-panel__badge">欢迎回来</div>
        <h2>登录你的账号</h2>
        <p class="auth-panel__desc">
          使用注册邮箱登录后，继续浏览职位、企业详情和个人投递记录。
        </p>

        <el-form
          ref="loginForm"
          class="auth-form"
          :model="loginForm"
          :rules="rules"
          @submit.native.prevent
        >
          <el-form-item prop="userName">
            <div class="field-label">邮箱地址</div>
            <el-input
              v-model="loginForm.userName"
              clearable
              placeholder="请输入邮箱地址"
              prefix-icon="el-icon-message"
              @keyup.enter.native="handleLogin"
            ></el-input>
          </el-form-item>

          <el-form-item prop="password">
            <div class="field-label">登录密码</div>
            <el-input
              v-model="loginForm.password"
              show-password
              type="password"
              placeholder="请输入登录密码"
              prefix-icon="el-icon-lock"
              @keyup.enter.native="handleLogin"
            ></el-input>
          </el-form-item>

          <div class="auth-panel__tips">
            <span>支持使用注册邮箱直接登录</span>
            <span>企业账号进入企业工作台，管理员账号将跳转到管理端</span>
          </div>

          <el-form-item class="auth-form__submit">
            <el-button
              type="primary"
              class="submit-button"
              :loading="loading"
              @click="handleLogin"
            >
              登录并继续
            </el-button>
          </el-form-item>

          <div class="auth-footer">
            <span>还没有账号？</span>
            <el-button type="text" @click="$router.push('/register')">
              立即注册
            </el-button>
          </div>
        </el-form>
      </section>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      logoUrl: require('@/image/logo.png'),
      highlights: [
        { value: '2000+', label: '校招岗位推荐' },
        { value: '300+', label: '活跃企业入驻' },
        { value: '24H', label: '消息提醒在线' }
      ],
      featureList: [
        {
          icon: 'el-icon-suitcase',
          title: '职位浏览更高效',
          text: '从热门岗位到企业详情，登录后即可继续追踪你的求职节奏。'
        },
        {
          icon: 'el-icon-data-analysis',
          title: '投递动态更清晰',
          text: '统一查看投递记录、简历状态与企业反馈，减少来回切换。'
        },
        {
          icon: 'el-icon-star-off',
          title: '体验更统一',
          text: '与站内暖橙色主题保持一致，让认证流程也更有品牌感。'
        }
      ],
      loginForm: {
        userName: '',
        password: ''
      },
      rules: {
        userName: [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入登录密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于 6 位', trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  methods: {
    getAdminConsoleUrl() {
      const { protocol, hostname, port } = window.location
      const adminPort = port === '8080' ? '8081' : '8081'
      return `${protocol}//${hostname}:${adminPort}/login`
    },
    redirectToAdminConsole() {
      window.location.href = this.getAdminConsoleUrl()
    },
    handleLogin() {
      this.$refs.loginForm.validate(async (valid) => {
        if (valid) {
          this.loading = true
          try {
            const response = await this.login(this.loginForm)

            if (response && response.token) {
              localStorage.setItem('token', response.token)
              localStorage.setItem('userInfo', JSON.stringify(response.user || {}))
              this.$message.success('登录成功')

              const userRole = response.user && response.user.role
              if (userRole === 'Admin') {
                this.redirectToAdminConsole()
              } else if (userRole === 'Enterprise_User') {
                this.$router.push('/company/dashboard')
              } else {
                this.$router.push('/')
              }
            } else {
              this.$message.error('登录响应格式错误')
            }
          } catch (error) {
            this.$message.error(error.message || '登录失败')
          } finally {
            this.loading = false
          }
        }
      })
    },
    async login(data) {
      if (!this.$api || !this.$api.auth) {
        throw new Error('API 对象未初始化')
      }

      try {
        return await this.$api.auth.login(data)
      } catch (error) {
        throw error
      }
    }
  }
}
</script>

<style scoped>
.auth-page {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  padding: 40px 24px;
  background:
    radial-gradient(circle at top left, rgba(255, 214, 188, 0.95), transparent 34%),
    radial-gradient(circle at right center, rgba(232, 122, 86, 0.2), transparent 28%),
    linear-gradient(135deg, #fff9f6 0%, #fff2eb 48%, #fffaf7 100%);
}

.auth-page__glow {
  position: absolute;
  border-radius: 999px;
  filter: blur(8px);
  pointer-events: none;
}

.auth-page__glow--one {
  top: 96px;
  left: -72px;
  width: 220px;
  height: 220px;
  background: rgba(232, 122, 86, 0.18);
}

.auth-page__glow--two {
  right: -88px;
  bottom: 60px;
  width: 280px;
  height: 280px;
  background: rgba(255, 143, 92, 0.2);
}

.auth-layout {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: minmax(0, 1.05fr) minmax(360px, 440px);
  align-items: center;
  gap: 36px;
  max-width: 1160px;
  min-height: calc(100vh - 80px);
  margin: 0 auto;
}

.auth-showcase {
  padding: 18px 12px 18px 8px;
}

.brand-chip {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: 10px 18px;
  border: 1px solid rgba(232, 122, 86, 0.18);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.76);
  box-shadow: 0 16px 40px rgba(232, 122, 86, 0.1);
  color: var(--primary-dark);
  font-size: 14px;
  font-weight: 600;
  backdrop-filter: blur(10px);
}

.brand-chip__logo {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  object-fit: cover;
  box-shadow: 0 8px 18px rgba(232, 122, 86, 0.2);
}

.showcase-copy {
  max-width: 620px;
  margin-top: 30px;
}

.showcase-copy__eyebrow {
  margin-bottom: 18px;
  color: var(--primary-color);
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.18em;
  text-transform: uppercase;
}

.showcase-copy h1 {
  margin: 0;
  color: #3a271f;
  font-size: 48px;
  line-height: 1.18;
  letter-spacing: -0.03em;
}

.showcase-copy__text {
  max-width: 540px;
  margin-top: 20px;
  color: #6a554d;
  font-size: 16px;
  line-height: 1.85;
}

.showcase-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  max-width: 640px;
  margin-top: 34px;
}

.showcase-stat {
  padding: 22px 20px;
  border: 1px solid rgba(232, 122, 86, 0.12);
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.8);
  box-shadow: 0 18px 40px rgba(232, 122, 86, 0.12);
  backdrop-filter: blur(12px);
}

.showcase-stat strong {
  display: block;
  margin-bottom: 8px;
  color: #2f241f;
  font-size: 28px;
  font-weight: 700;
}

.showcase-stat span {
  color: #826b63;
  font-size: 14px;
}

.showcase-points {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
  max-width: 700px;
  margin-top: 28px;
}

.showcase-point {
  display: flex;
  gap: 14px;
  padding: 20px;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.7);
  box-shadow: 0 18px 36px rgba(232, 122, 86, 0.08);
}

.showcase-point__icon {
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 46px;
  width: 46px;
  height: 46px;
  border-radius: 16px;
  background: linear-gradient(135deg, rgba(232, 122, 86, 0.14), rgba(255, 165, 124, 0.26));
  color: var(--primary-dark);
  font-size: 20px;
}

.showcase-point h3 {
  margin: 2px 0 6px;
  color: #3f2e27;
  font-size: 16px;
  font-weight: 700;
}

.showcase-point p {
  margin: 0;
  color: #7f6b64;
  font-size: 14px;
  line-height: 1.7;
}

.auth-panel {
  padding: 34px 34px 28px;
  border: 1px solid rgba(232, 122, 86, 0.12);
  border-radius: 30px;
  background: rgba(255, 255, 255, 0.86);
  box-shadow: 0 24px 60px rgba(232, 122, 86, 0.16);
  backdrop-filter: blur(18px);
}

.auth-panel__badge {
  display: inline-flex;
  align-items: center;
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(232, 122, 86, 0.12);
  color: var(--primary-dark);
  font-size: 13px;
  font-weight: 700;
}

.auth-panel h2 {
  margin: 20px 0 10px;
  color: #2f241f;
  font-size: 32px;
}

.auth-panel__desc {
  margin: 0 0 28px;
  color: #7b655b;
  font-size: 14px;
  line-height: 1.7;
}

.field-label {
  margin-bottom: 10px;
  color: #5d4a42;
  font-size: 14px;
  font-weight: 600;
}

.auth-panel__tips {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin: 4px 0 18px;
  color: #957b70;
  font-size: 12px;
  line-height: 1.6;
}

.auth-form__submit {
  margin-top: 12px;
  margin-bottom: 10px;
}

.submit-button {
  width: 100%;
  height: 52px;
  border: none;
  border-radius: 16px;
  font-size: 16px;
  font-weight: 700;
}

.auth-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-top: 6px;
  color: #7b655b;
  font-size: 14px;
}

.auth-form >>> .el-form-item {
  margin-bottom: 22px;
}

.auth-form >>> .el-input__inner {
  height: 52px;
  border: 1px solid rgba(232, 122, 86, 0.16);
  border-radius: 16px;
  background: rgba(255, 249, 246, 0.95);
  color: #3f2e27;
  font-size: 15px;
  transition: all 0.25s ease;
}

.auth-form >>> .el-input__inner:focus {
  border-color: rgba(232, 122, 86, 0.55);
  background: #fff;
  box-shadow: 0 0 0 4px rgba(232, 122, 86, 0.12);
}

.auth-form >>> .el-input__prefix {
  left: 12px;
  color: #c8876a;
}

.auth-form >>> .el-input__icon {
  line-height: 52px;
}

.auth-form >>> .el-form-item__error {
  padding-top: 6px;
}

.auth-form >>> .el-button--text {
  padding: 0;
  color: var(--primary-color);
  font-size: 14px;
  font-weight: 600;
}

.auth-form >>> .el-button--text:hover,
.auth-form >>> .el-button--text:focus {
  color: var(--primary-dark);
}

@media (max-width: 1080px) {
  .auth-layout {
    grid-template-columns: 1fr;
    gap: 28px;
  }

  .auth-showcase {
    padding: 0;
  }

  .showcase-copy h1 {
    font-size: 40px;
  }

  .auth-panel {
    max-width: 560px;
    width: 100%;
    margin: 0 auto;
  }
}

@media (max-width: 768px) {
  .auth-page {
    padding: 24px 16px;
  }

  .auth-layout {
    min-height: auto;
  }

  .showcase-copy h1 {
    font-size: 32px;
  }

  .showcase-stats,
  .showcase-points {
    grid-template-columns: 1fr;
  }

  .auth-panel {
    padding: 28px 22px 24px;
    border-radius: 24px;
  }

  .auth-panel__tips,
  .auth-footer {
    flex-direction: column;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .showcase-copy__eyebrow {
    letter-spacing: 0.12em;
  }

  .showcase-copy h1 {
    font-size: 28px;
  }

  .showcase-copy__text {
    font-size: 14px;
  }

  .showcase-stat strong {
    font-size: 24px;
  }

  .auth-panel h2 {
    font-size: 28px;
  }
}
</style>
