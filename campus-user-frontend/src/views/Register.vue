<template>
  <div class="auth-page auth-page--register">
    <div class="auth-page__glow auth-page__glow--one"></div>
    <div class="auth-page__glow auth-page__glow--two"></div>

    <div class="auth-layout">
      <section class="auth-showcase">
        <div class="brand-chip">
          <img :src="logoUrl" alt="青梧引校招平台" class="brand-chip__logo">
          <span>青梧引校招平台</span>
        </div>

        <div class="showcase-copy">
          <p class="showcase-copy__eyebrow">Create Your Account</p>
          <h1>几步完成注册，快速加入更顺手的校招平台</h1>
          <p class="showcase-copy__text">
            完成邮箱验证后即可创建账号，求职用户和企业用户都能在同一入口高效开始。
          </p>
        </div>

        <div class="showcase-timeline">
          <div
            v-for="item in steps"
            :key="item.title"
            class="timeline-card"
          >
            <div class="timeline-card__index">{{ item.step }}</div>
            <div>
              <h3>{{ item.title }}</h3>
              <p>{{ item.text }}</p>
            </div>
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

      <section class="auth-panel auth-panel--wide">
        <div class="auth-panel__badge">创建账号</div>
        <h2>注册你的账号</h2>
        <p class="auth-panel__desc">
          填写基础信息并完成邮箱验证，注册成功后即可返回登录页进入平台。
        </p>

        <el-form
          ref="registerForm"
          class="auth-form"
          :model="registerForm"
          :rules="rules"
          @submit.native.prevent
        >
          <el-form-item prop="email">
            <div class="field-label">邮箱地址</div>
            <el-input
              v-model="registerForm.email"
              clearable
              placeholder="请输入邮箱地址"
              prefix-icon="el-icon-message"
            ></el-input>
          </el-form-item>

          <el-form-item prop="authCode">
            <div class="field-label">邮箱验证码</div>
            <el-input
              v-model="registerForm.authCode"
              placeholder="请输入邮箱验证码"
              prefix-icon="el-icon-key"
            >
              <el-button
                slot="append"
                class="code-button"
                :disabled="countdown > 0 || sendingAuthCode"
                @click="sendAuthCode"
              >
                {{ countdown > 0 ? `${countdown}s 后重试` : (sendingAuthCode ? '发送中...' : '获取验证码') }}
              </el-button>
            </el-input>
          </el-form-item>

          <div class="auth-form__row">
            <el-form-item prop="nickName">
              <div class="field-label">昵称</div>
              <el-input
                v-model="registerForm.nickName"
                clearable
                placeholder="请输入昵称"
                prefix-icon="el-icon-user"
              ></el-input>
            </el-form-item>

            <el-form-item prop="phone">
              <div class="field-label">手机号</div>
              <el-input
                v-model="registerForm.phone"
                clearable
                placeholder="请输入手机号"
                prefix-icon="el-icon-phone"
              ></el-input>
            </el-form-item>
          </div>

          <div class="auth-form__row">
            <el-form-item prop="password">
              <div class="field-label">登录密码</div>
              <el-input
                v-model="registerForm.password"
                show-password
                type="password"
                placeholder="请输入登录密码"
                prefix-icon="el-icon-lock"
              ></el-input>
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <div class="field-label">确认密码</div>
              <el-input
                v-model="registerForm.confirmPassword"
                show-password
                type="password"
                placeholder="请再次输入密码"
                prefix-icon="el-icon-lock"
              ></el-input>
            </el-form-item>
          </div>

          <el-form-item prop="userType">
            <div class="field-label">账号类型</div>
            <el-radio-group v-model="registerForm.userType" class="account-type-group">
              <el-radio-button label="Normal_User">求职用户</el-radio-button>
              <el-radio-button label="Enterprise_User">企业用户</el-radio-button>
            </el-radio-group>
          </el-form-item>

          <div class="auth-panel__tips auth-panel__tips--stack">
            <span>验证码有效期请以邮箱内提示为准</span>
            <span>企业用户注册后可继续完善企业资料与职位信息</span>
          </div>

          <el-form-item class="auth-form__submit">
            <el-button
              type="primary"
              class="submit-button"
              :loading="loading"
              @click="handleRegister"
            >
              注册并完成创建
            </el-button>
          </el-form-item>

          <div class="auth-footer">
            <span>已经有账号？</span>
            <el-button type="text" @click="$router.push('/login')">
              立即登录
            </el-button>
          </div>
        </el-form>
      </section>
    </div>
  </div>
</template>

<script>
import api from '@/api'

export default {
  name: 'Register',
  data() {
    const validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.registerForm.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    return {
      logoUrl: require('@/image/logo.png'),
      steps: [
        {
          step: '01',
          title: '填写基础信息',
          text: '使用常用邮箱、手机号与昵称快速完成账号基础资料录入。'
        },
        {
          step: '02',
          title: '完成邮箱验证',
          text: '发送验证码后即可校验身份，注册流程清晰直观，不容易迷路。'
        },
        {
          step: '03',
          title: '选择账号角色',
          text: '求职用户与企业用户共用统一入口，但进入后会获得各自的工作台体验。'
        }
      ],
      featureList: [
        {
          icon: 'el-icon-check',
          title: '表单更清晰',
          text: '字段被分组整理，信息层次更明确，减少首次注册的理解成本。'
        },
        {
          icon: 'el-icon-time',
          title: '验证码更直观',
          text: '按钮状态与倒计时更明确，避免重复点击后不知道当前进度。'
        },
        {
          icon: 'el-icon-office-building',
          title: '双角色更统一',
          text: '无论是求职者还是企业，都能在同一页面完成账号创建。'
        }
      ],
      registerForm: {
        email: '',
        authCode: '',
        nickName: '',
        phone: '',
        password: '',
        confirmPassword: '',
        userType: 'Normal_User'
      },
      rules: {
        email: [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        authCode: [
          { required: true, message: '请输入验证码', trigger: 'blur' }
        ],
        nickName: [
          { required: true, message: '请输入昵称', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入登录密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于 6 位', trigger: 'blur' }
        ],
        confirmPassword: [
          { validator: validatePass, trigger: 'blur' }
        ],
        userType: [
          { required: true, message: '请选择账号类型', trigger: 'change' }
        ]
      },
      loading: false,
      countdown: 0,
      countdownTimer: null,
      sendingAuthCode: false
    }
  },
  methods: {
    async sendAuthCode() {
      if (!this.registerForm.email) {
        this.$message.error('请先输入邮箱地址')
        return
      }

      if (this.countdown > 0 || this.sendingAuthCode) {
        return
      }

      this.sendingAuthCode = true
      this.startCountdown()

      try {
        const message = await api.email.getAuthCode(this.registerForm.email)
        this.$message.success(message || '验证码发送成功')
      } catch (error) {
        this.$message.error(error.message || '验证码发送失败')
      } finally {
        this.sendingAuthCode = false
      }
    },
    startCountdown() {
      if (this.countdownTimer) {
        clearInterval(this.countdownTimer)
      }
      this.countdown = 60
      this.countdownTimer = setInterval(() => {
        this.countdown--
        if (this.countdown <= 0) {
          clearInterval(this.countdownTimer)
          this.countdownTimer = null
        }
      }, 1000)
    },
    handleRegister() {
      this.$refs.registerForm.validate(async (valid) => {
        if (valid) {
          this.loading = true
          try {
            const data = {
              userName: this.registerForm.email,
              email: this.registerForm.email,
              nickName: this.registerForm.nickName,
              phone: this.registerForm.phone,
              password: this.registerForm.password,
              inputCode: this.registerForm.authCode,
              role: this.registerForm.userType
            }

            await api.user.register(data)
            this.$message.success('注册成功')
            this.$router.push('/login')
          } catch (error) {
            this.$message.error(error.message || '注册失败')
          } finally {
            this.loading = false
          }
        }
      })
    }
  },
  beforeDestroy() {
    if (this.countdownTimer) {
      clearInterval(this.countdownTimer)
      this.countdownTimer = null
    }
  }
}
</script>

<style scoped>
.auth-page {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  padding: 32px 24px;
  background:
    radial-gradient(circle at left top, rgba(255, 214, 188, 0.95), transparent 30%),
    radial-gradient(circle at right 20%, rgba(232, 122, 86, 0.18), transparent 26%),
    linear-gradient(135deg, #fffaf7 0%, #fff3eb 52%, #fffdfa 100%);
}

.auth-page__glow {
  position: absolute;
  border-radius: 999px;
  pointer-events: none;
  filter: blur(10px);
}

.auth-page__glow--one {
  top: 88px;
  left: -80px;
  width: 260px;
  height: 260px;
  background: rgba(232, 122, 86, 0.14);
}

.auth-page__glow--two {
  right: -92px;
  bottom: 42px;
  width: 300px;
  height: 300px;
  background: rgba(255, 162, 116, 0.18);
}

.auth-layout {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(420px, 540px);
  align-items: center;
  gap: 34px;
  max-width: 1220px;
  min-height: calc(100vh - 64px);
  margin: 0 auto;
}

.auth-showcase {
  padding: 16px 8px;
}

.brand-chip {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: 10px 18px;
  border: 1px solid rgba(232, 122, 86, 0.18);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.78);
  box-shadow: 0 14px 36px rgba(232, 122, 86, 0.08);
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
  margin-top: 28px;
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
  color: #332720;
  font-size: 44px;
  line-height: 1.18;
  letter-spacing: -0.03em;
}

.showcase-copy__text {
  max-width: 560px;
  margin-top: 18px;
  color: #6f5a51;
  font-size: 16px;
  line-height: 1.85;
}

.showcase-timeline {
  display: grid;
  gap: 16px;
  max-width: 650px;
  margin-top: 30px;
}

.timeline-card {
  display: flex;
  gap: 18px;
  align-items: flex-start;
  padding: 20px 22px;
  border: 1px solid rgba(232, 122, 86, 0.12);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.78);
  box-shadow: 0 18px 40px rgba(232, 122, 86, 0.1);
}

.timeline-card__index {
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 54px;
  width: 54px;
  height: 54px;
  border-radius: 18px;
  background: linear-gradient(135deg, rgba(232, 122, 86, 0.18), rgba(255, 171, 134, 0.3));
  color: var(--primary-dark);
  font-size: 16px;
  font-weight: 700;
}

.timeline-card h3,
.showcase-point h3 {
  margin: 2px 0 6px;
  color: #3f2e27;
  font-size: 16px;
  font-weight: 700;
}

.timeline-card p,
.showcase-point p {
  margin: 0;
  color: #7d685f;
  font-size: 14px;
  line-height: 1.7;
}

.showcase-points {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  max-width: 700px;
  margin-top: 22px;
}

.showcase-point {
  padding: 20px 18px;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.7);
  box-shadow: 0 16px 32px rgba(232, 122, 86, 0.08);
}

.showcase-point__icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 46px;
  height: 46px;
  margin-bottom: 16px;
  border-radius: 16px;
  background: linear-gradient(135deg, rgba(232, 122, 86, 0.14), rgba(255, 173, 137, 0.26));
  color: var(--primary-dark);
  font-size: 20px;
}

.auth-panel {
  padding: 32px 34px 28px;
  border: 1px solid rgba(232, 122, 86, 0.12);
  border-radius: 30px;
  background: rgba(255, 255, 255, 0.88);
  box-shadow: 0 26px 64px rgba(232, 122, 86, 0.16);
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
  color: #7a655b;
  font-size: 14px;
  line-height: 1.7;
}

.field-label {
  margin-bottom: 10px;
  color: #5d4a42;
  font-size: 14px;
  font-weight: 600;
}

.auth-form__row {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.auth-panel__tips {
  display: flex;
  gap: 12px;
  margin: 2px 0 20px;
  color: #967b70;
  font-size: 12px;
  line-height: 1.7;
}

.auth-panel__tips--stack {
  flex-direction: column;
}

.auth-form__submit {
  margin-top: 8px;
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
  margin-top: 8px;
  color: #7b655b;
  font-size: 14px;
}

.auth-form >>> .el-form-item {
  margin-bottom: 20px;
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

.auth-form >>> .el-input-group__append {
  border: none;
  background: transparent;
  padding: 0 0 0 10px;
}

.auth-form >>> .el-input__prefix {
  left: 12px;
  color: #c8876a;
}

.auth-form >>> .el-input__icon {
  line-height: 52px;
}

.auth-form >>> .el-radio-group {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  width: 100%;
}

.auth-form >>> .el-radio-button__inner {
  width: 100%;
  height: 48px;
  border: 1px solid rgba(232, 122, 86, 0.16);
  border-radius: 16px !important;
  background: rgba(255, 249, 246, 0.95);
  color: #6f5a51;
  font-size: 14px;
  font-weight: 600;
  line-height: 46px;
  box-shadow: none !important;
}

.auth-form >>> .el-radio-button:first-child .el-radio-button__inner,
.auth-form >>> .el-radio-button:last-child .el-radio-button__inner {
  border-left: 1px solid rgba(232, 122, 86, 0.16);
}

.auth-form >>> .el-radio-button__orig-radio:checked + .el-radio-button__inner {
  border-color: rgba(232, 122, 86, 0.48);
  background: rgba(232, 122, 86, 0.12);
  color: var(--primary-dark);
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

.code-button {
  min-width: 116px;
  height: 44px;
  border: none;
  border-radius: 14px;
  background: linear-gradient(135deg, rgba(232, 122, 86, 0.14), rgba(255, 183, 150, 0.3));
  color: var(--primary-dark);
  font-size: 13px;
  font-weight: 700;
}

.code-button:hover,
.code-button:focus {
  color: var(--primary-dark);
  background: linear-gradient(135deg, rgba(232, 122, 86, 0.18), rgba(255, 183, 150, 0.36));
}

.code-button.is-disabled,
.code-button.is-disabled:hover,
.code-button.is-disabled:focus {
  background: rgba(245, 223, 214, 0.82);
  color: #b09589;
}

@media (max-width: 1120px) {
  .auth-layout {
    grid-template-columns: 1fr;
    gap: 28px;
  }

  .auth-showcase {
    padding: 0;
  }

  .showcase-copy h1 {
    font-size: 38px;
  }

  .auth-panel {
    width: 100%;
    max-width: 720px;
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
    font-size: 30px;
  }

  .showcase-timeline,
  .showcase-points,
  .auth-form__row {
    grid-template-columns: 1fr;
  }

  .auth-panel {
    padding: 28px 22px 24px;
    border-radius: 24px;
  }

  .auth-form >>> .el-radio-group {
    grid-template-columns: 1fr;
  }

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

  .timeline-card {
    padding: 18px;
  }

  .timeline-card__index {
    flex-basis: 48px;
    width: 48px;
    height: 48px;
    border-radius: 16px;
  }

  .auth-panel h2 {
    font-size: 28px;
  }
}
</style>
