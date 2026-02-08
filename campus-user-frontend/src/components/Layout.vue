<template>
  <div class="layout">
    <!-- 顶部导航栏 -->
    <!-- 修改点：添加了 sticky-header 类，移除了内联背景色设置，改用 CSS 控制 -->
    <el-header class="header sticky-header">
      <div class="header-container">
        <!-- Logo 区域 -->
        <div class="logo-wrapper" @click="goHome">
          <a href="javascript:void(0)" class="logo-link">
            <img src="../image/android-chrome-512x512.png" alt="Logo" class="logo-img">
            <span class="logo-text">校园招聘<span class="highlight">系统</span></span>
          </a>
        </div>

        <!-- 导航菜单 -->
        <!-- 修改点：移除了原本的 background-color 等硬编码属性，让 CSS 接管样式 -->
        <el-menu
            :default-active="activeIndex"
            class="nav-menu"
            mode="horizontal"
            @select="handleMenuSelect"
            active-text-color="#ff6b00">

          <!-- 企业用户菜单 -->
          <template v-if="userInfo && userInfo.role === 'Enterprise_User'">
            <el-menu-item index="/company/dashboard">企业仪表板</el-menu-item>
            <el-menu-item index="/company/jobs">职位管理</el-menu-item>
            <el-menu-item index="/company/applications">申请管理</el-menu-item>
            <el-menu-item index="/company/profile">企业资料</el-menu-item>
          </template>

          <!-- 普通用户菜单 -->
          <template v-else>
            <el-menu-item index="/home">首页</el-menu-item>
            <el-menu-item index="/jobs">职位招聘</el-menu-item>
            <el-menu-item index="/companies">企业列表</el-menu-item>
            <el-menu-item index="/articles">求职资讯</el-menu-item>
          </template>
        </el-menu>

        <!-- 用户信息/登录区域 -->
        <div class="right-actions">
          <div v-if="userInfo" class="user-menu-area">
            <el-dropdown @command="handleCommand" trigger="click">
              <div class="user-dropdown-trigger">
                <el-avatar :size="36" :src="userInfo.avatar" icon="el-icon-user-solid" class="user-avatar"></el-avatar>
                <span class="user-name-text">{{ userInfo.nickName || userInfo.userName }}</span>
                <i class="el-icon-arrow-down el-icon--right"></i>
              </div>
              <el-dropdown-menu slot="dropdown" class="custom-dropdown">
                <el-dropdown-item command="profile" icon="el-icon-user">个人中心</el-dropdown-item>
                <el-dropdown-item command="resume" icon="el-icon-document">简历管理</el-dropdown-item>
                <el-dropdown-item command="logout" divided icon="el-icon-switch-button" style="color: #f56c6c;">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
          <div v-else class="auth-btn-group">
            <el-button class="btn-text-login" type="text" @click="$router.push('/login')">登录</el-button>
            <el-button class="btn-primary-register" type="primary" round size="small" @click="$router.push('/register')">注册账号</el-button>
          </div>
        </div>
      </div>
    </el-header>

    <!-- 主要内容区域 -->
    <el-main class="main-wrapper">
      <transition name="fade-transform" mode="out-in">
        <router-view class="view-content" />
      </transition>
    </el-main>

    <!-- 底部 -->
    <el-footer class="footer">
      <div class="footer-content">
        <div class="footer-links">
          <router-link to="/about" class="footer-link">关于我们</router-link>
          <span class="divider">|</span>
          <router-link to="/contact" class="footer-link">联系方式</router-link>
          <span class="divider">|</span>
          <router-link to="/privacy" class="footer-link">隐私政策</router-link>
        </div>
        <p class="copyright">© 2025 校园招聘系统. All rights reserved.</p>
      </div>
    </el-footer>
  </div>
</template>

<script>
// 逻辑部分保持原样，未做修改
export default {
  name: 'Layout',
  data() {
    return {
      userInfo: null,
      activeIndex: '/'
    }
  },
  mounted() {
    this.loadUserInfo()
    this.activeIndex = this.$route.path
  },
  watch: {
    '$route'(to) {
      this.activeIndex = to.path
    }
  },
  methods: {
    loadUserInfo() {
      const token = localStorage.getItem('token')
      const userInfo = localStorage.getItem('userInfo')

      if (token && userInfo) {
        try {
          this.userInfo = JSON.parse(userInfo)
        } catch (e) {
          console.error('解析用户信息失败:', e)
          this.clearUserInfo()
        }
      }
    },

    goHome() {
      const path = this.userInfo && this.userInfo.role === 'Enterprise_User'
          ? '/company/dashboard'
          : '/home'
      this.$router.push(path).catch(() => {})
    },

    handleMenuSelect(index) {
      if (index && index.startsWith('/')) {
        this.$router.push(index).catch(err => {
          if (err.name !== 'NavigationDuplicated') {
            console.error('路由跳转失败:', err)
          }
        })
      }
    },

    handleCommand(command) {
      switch (command) {
        case 'profile':
          this.$router.push('/profile')
          break
        case 'resume':
          this.$router.push('/resume')
          break
        case 'logout':
          this.logout()
          break
      }
    },

    logout() {
      this.$confirm('确认退出登录?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.clearUserInfo()
        this.$message.success('已退出登录')
        this.$router.push('/login')
      })
    },

    clearUserInfo() {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      this.userInfo = null
    }
  }
}
</script>

<style scoped>
/* 全局布局 */
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f8fafc; /* 与首页背景一致 */
}

/* ================= Header 样式 ================= */
.header {
  padding: 0;
  height: 64px !important; /* 稍微增加高度 */
  line-height: 64px;
  background-color: #ffffff;
  box-shadow: 0 1px 10px rgba(0, 0, 0, 0.05); /* 柔和阴影 */
  z-index: 1000;
  transition: all 0.3s;
}

/* 吸顶效果 */
.sticky-header {
  position: sticky;
  top: 0;
}

.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* Logo 区域 */
.logo-wrapper {
  display: flex;
  align-items: center;
  cursor: pointer;
  min-width: 200px;
}

.logo-link {
  display: flex;
  align-items: center;
  text-decoration: none;
}

.logo-img {
  height: 32px; /* 调整 Logo 大小 */
  margin-right: 12px;
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
  letter-spacing: -0.5px;
}

.highlight {
  color: #ff6b00; /* 品牌主色 */
}

/* 导航菜单 - 覆盖 Element UI 默认样式 */
.nav-menu {
  flex: 1;
  margin: 0 40px;
  border-bottom: none !important;
  display: flex;
  justify-content: center; /* 菜单居中 */
  background: transparent !important;
}

/* 使用 ::v-deep 穿透修改 Element 组件样式 */
.layout >>> .el-menu-item {
  font-size: 15px;
  font-weight: 500;
  color: #64748b !important; /* 默认灰色 */
  height: 64px;
  line-height: 64px;
  border-bottom: 3px solid transparent;
  transition: all 0.3s;
}

.layout >>> .el-menu-item:hover {
  color: #ff6b00 !important;
  background-color: rgba(255, 107, 0, 0.05) !important;
}

.layout >>> .el-menu-item.is-active {
  color: #ff6b00 !important;
  border-bottom-color: #ff6b00 !important;
  background: transparent !important;
  font-weight: 600;
}

/* 用户区域 */
.right-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  min-width: 200px;
}

/* 登录状态下的下拉菜单触发器 */
.user-dropdown-trigger {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 20px;
  transition: background 0.3s;
}

.user-dropdown-trigger:hover {
  background: #f1f5f9;
}

.user-avatar {
  border: 2px solid #fff;
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
}

.user-name-text {
  margin: 0 8px;
  font-size: 14px;
  color: #334155;
  font-weight: 500;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 未登录状态按钮组 */
.auth-btn-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.btn-text-login {
  color: #64748b;
  font-size: 15px;
}

.btn-text-login:hover {
  color: #ff6b00;
}

.btn-primary-register {
  background: linear-gradient(135deg, #ff8736 0%, #ff6b00 100%);
  border: none;
  padding: 8px 20px;
  font-weight: 600;
  box-shadow: 0 4px 10px rgba(255, 107, 0, 0.2);
  transition: transform 0.2s;
}

.btn-primary-register:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 15px rgba(255, 107, 0, 0.3);
  background: linear-gradient(135deg, #ff9f43 0%, #ff7f24 100%);
}

/* ================= Main 内容区域 ================= */
.main-wrapper {
  flex: 1;
  padding: 0;
  width: 100%;
  position: relative;
}

/* 页面切换动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}

.fade-transform-enter {
  opacity: 0;
  transform: translateY(10px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* ================= Footer 样式 ================= */
.footer {
  background-color: #1e293b; /* 深蓝灰，比纯黑更有质感 */
  color: #94a3b8;
  text-align: center;
  height: auto !important;
  padding: 40px 0;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
}

.footer-links {
  margin-bottom: 15px;
  font-size: 14px;
}

.footer-links .footer-link {
  cursor: pointer;
  transition: color 0.3s;
  color: #94a3b8;
  text-decoration: none;
}

.footer-links .footer-link:hover {
  color: white;
}

.divider {
  margin: 0 15px;
  color: #475569;
}

.copyright {
  font-size: 13px;
  color: #64748b;
  margin: 0;
}

/* ================= 响应式设计 ================= */
@media (max-width: 768px) {
  .header-container {
    padding: 0 10px;
  }

  .logo-text {
    display: none; /* 小屏幕隐藏Logo文字 */
  }

  .nav-menu {
    margin: 0 10px;
  }

  .right-actions {
    min-width: auto;
  }

  .user-name-text {
    display: none; /* 小屏幕隐藏用户名 */
  }
}
</style>