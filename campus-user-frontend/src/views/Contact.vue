<template>
  <div class="contact-container">
    <div class="section-banner">
      <div class="banner-content">
        <h1>联系方式</h1>
        <p>与我们取得联系</p>
      </div>
    </div>

    <div class="main-content">
      <section class="contact-section">
        <div class="container">
          <div class="contact-wrapper">
            <!-- 联系信息 -->
            <div class="contact-info">
              <div class="section-header">
                <h2>联系我们</h2>
                <div class="divider"></div>
                <p>如果您有任何问题或建议，欢迎随时与我们联系</p>
              </div>

              <div class="info-items">
                <div class="info-item">
                  <div class="info-icon">
                    <i class="el-icon-message"></i>
                  </div>
                  <div class="info-content">
                    <h3>邮箱</h3>
                    <p>contact@campus-recruitment.com</p>
                  </div>
                </div>

                <div class="info-item">
                  <div class="info-icon">
                    <i class="el-icon-phone"></i>
                  </div>
                  <div class="info-content">
                    <h3>电话</h3>
                    <p>400-123-4567</p>
                  </div>
                </div>

                <div class="info-item">
                  <div class="info-icon">
                    <i class="el-icon-location"></i>
                  </div>
                  <div class="info-content">
                    <h3>地址</h3>
                    <p>北京市海淀区中关村南大街5号</p>
                  </div>
                </div>

                <div class="info-item">
                  <div class="info-icon">
                    <i class="el-icon-time"></i>
                  </div>
                  <div class="info-content">
                    <h3>工作时间</h3>
                    <p>周一至周五: 9:00 - 18:00</p>
                  </div>
                </div>
              </div>

              <!-- 社交媒体 -->
              <div class="social-media">
                <h3>关注我们</h3>
                <div class="social-icons">
                  <a href="#" class="social-icon weixin">
                    <i class="el-icon-chat-dot-round"></i>
                  </a>
                  <a href="#" class="social-icon weibo">
                    <i class="el-icon-s-promotion"></i>
                  </a>
                  <a href="#" class="social-icon qq">
                    <i class="el-icon-chat-line-square"></i>
                  </a>
                </div>
              </div>
            </div>

            <!-- 联系表单 -->
            <div class="contact-form">
              <div class="form-header">
                <h2>发送消息</h2>
                <p>填写以下表单，我们将尽快回复您</p>
              </div>
              <el-form :model="form" :rules="rules" ref="form" label-width="80px">
                <el-form-item label="姓名" prop="name">
                  <el-input v-model="form.name" placeholder="请输入您的姓名"></el-input>
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="form.email" placeholder="请输入您的邮箱"></el-input>
                </el-form-item>
                <el-form-item label="电话" prop="phone">
                  <el-input v-model="form.phone" placeholder="请输入您的电话"></el-input>
                </el-form-item>
                <el-form-item label="主题" prop="subject">
                  <el-input v-model="form.subject" placeholder="请输入消息主题"></el-input>
                </el-form-item>
                <el-form-item label="消息内容" prop="message">
                  <el-input v-model="form.message" type="textarea" rows="5" placeholder="请输入您的消息内容"></el-input>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="submitForm" class="submit-btn">发送消息</el-button>
                </el-form-item>
              </el-form>
            </div>
          </div>
        </div>
      </section>

      <!-- 常见问题 -->
      <section class="faq-section bg-light">
        <div class="container">
          <div class="section-header">
            <h2>常见问题</h2>
            <div class="divider"></div>
          </div>
          <el-collapse v-model="activeNames">
            <el-collapse-item title="如何注册成为求职者？" name="1">
              <div class="faq-content">
                <p>在网站顶部点击"注册"按钮，选择"个人用户"注册类型，填写相关信息即可完成注册。注册成功后，您可以完善个人简历，开始投递职位。</p>
              </div>
            </el-collapse-item>
            <el-collapse-item title="如何注册成为企业用户？" name="2">
              <div class="faq-content">
                <p>在网站顶部点击"注册"按钮，选择"企业用户"注册类型，填写企业相关信息并上传营业执照等证明材料，提交审核后等待系统管理员审核通过即可。</p>
              </div>
            </el-collapse-item>
            <el-collapse-item title="如何发布职位？" name="3">
              <div class="faq-content">
                <p>企业用户登录后，进入"职位管理"页面，点击"发布新职位"按钮，填写职位相关信息，提交后即可发布职位。</p>
              </div>
            </el-collapse-item>
            <el-collapse-item title="如何修改个人信息？" name="4">
              <div class="faq-content">
                <p>登录后，进入"个人中心"页面，点击"编辑资料"按钮，即可修改个人基本信息、教育背景、工作经验等。</p>
              </div>
            </el-collapse-item>
            <el-collapse-item title="忘记密码怎么办？" name="5">
              <div class="faq-content">
                <p>在登录页面点击"忘记密码"链接，按照提示输入注册邮箱，系统会发送重置密码的链接到您的邮箱，点击链接即可重置密码。</p>
              </div>
            </el-collapse-item>
          </el-collapse>
        </div>
      </section>
    </div>
  </div>
</template>

<script>
import api from '@/api'

export default {
  name: 'Contact',
  data() {
    return {
      form: {
        name: '',
        email: '',
        phone: '',
        subject: '',
        message: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入电话', trigger: 'blur' }
        ],
        subject: [
          { required: true, message: '请输入主题', trigger: 'blur' }
        ],
        message: [
          { required: true, message: '请输入消息内容', trigger: 'blur' }
        ]
      },
      activeNames: ['1']
    }
  },
  mounted() {
    // 设置页面标题
    document.title = '联系方式 - 校园招聘系统'
  },
  methods: {
    submitForm() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          try {
            // 构造反馈数据
            const feedbackData = {
              title: this.form.subject,
              content: this.form.message,
              userId: 'contact_form', // 非登录用户使用默认值
              type: 2, // 2-建议类型
              status: 0 // 0-未回复状态
            }
            
            // 调用API发送消息
            await api.feedback.createFeedback(feedbackData)
            
            this.$message.success('消息发送成功，我们将尽快与您联系！')
            // 重置表单
            this.$refs.form.resetFields()
          } catch (error) {
            this.$message.error('消息发送失败，请稍后重试')
            console.error('发送消息失败:', error)
          }
        } else {
          return false
        }
      })
    }
  }
}
</script>

<style scoped>
.contact-container {
  min-height: 100vh;
  background-color: #f8fafc;
}

/* 横幅区域 */
.section-banner {
  background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
  color: white;
  padding: 80px 0;
  text-align: center;
}

.banner-content h1 {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 16px;
}

.banner-content p {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
}

/* 主内容区域 */
.main-content {
  padding: 60px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 通用区块样式 */
.contact-section,
.faq-section {
  padding: 60px 0;
}

.bg-light {
  background-color: white;
}

/* 区块标题 */
.section-header {
  text-align: center;
  margin-bottom: 40px;
}

.section-header h2 {
  font-size: 32px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 16px;
}

.section-header p {
  font-size: 16px;
  color: #64748b;
  margin-bottom: 20px;
}

.divider {
  width: 80px;
  height: 4px;
  background: linear-gradient(90deg, #ff8736 0%, #ff6b00 100%);
  margin: 0 auto;
  border-radius: 2px;
}

/* 联系信息和表单容器 */
.contact-wrapper {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  margin-top: 40px;
}

/* 联系信息 */
.contact-info {
  background-color: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.info-items {
  margin-top: 30px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 30px;
}

.info-icon {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #ff8736 0%, #ff6b00 100%);
  color: white;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: 20px;
  flex-shrink: 0;
}

.info-content h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 8px;
}

.info-content p {
  font-size: 14px;
  color: #64748b;
  margin: 0;
  line-height: 1.6;
}

/* 社交媒体 */
.social-media {
  margin-top: 40px;
}

.social-media h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 20px;
}

.social-icons {
  display: flex;
  gap: 15px;
}

.social-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;
}

.social-icon:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.social-icon.weixin {
  background-color: #07C160;
  color: white;
}

.social-icon.weibo {
  background-color: #E6162D;
  color: white;
}

.social-icon.qq {
  background-color: #12B7F5;
  color: white;
}

/* 联系表单 */
.contact-form {
  background-color: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.form-header {
  text-align: center;
  margin-bottom: 30px;
}

.form-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 8px;
}

.form-header p {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

.submit-btn {
  width: 100%;
  background: linear-gradient(135deg, #ff8736 0%, #ff6b00 100%);
  border: none;
  padding: 12px;
  font-size: 16px;
  font-weight: 600;
  transition: transform 0.3s, box-shadow 0.3s;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 0, 0.3);
  background: linear-gradient(135deg, #ff9f43 0%, #ff7f24 100%);
}

/* 常见问题 */
.faq-section {
  margin-top: 60px;
}

.el-collapse {
  max-width: 800px;
  margin: 0 auto;
}

.el-collapse-item {
  margin-bottom: 10px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.el-collapse-item__header {
  font-size: 16px;
  font-weight: 500;
  color: #1e293b;
  padding: 16px 20px;
  background-color: white;
}

.faq-content {
  padding: 20px;
  background-color: #f8fafc;
  border-top: 1px solid #e2e8f0;
}

.faq-content p {
  font-size: 14px;
  line-height: 1.6;
  color: #64748b;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .banner-content h1 {
    font-size: 32px;
  }

  .section-header h2 {
    font-size: 24px;
  }

  .contact-wrapper {
    grid-template-columns: 1fr;
    gap: 30px;
  }

  .contact-info,
  .contact-form {
    padding: 30px 20px;
  }

  .info-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .info-icon {
    margin-bottom: 15px;
  }
}
</style>