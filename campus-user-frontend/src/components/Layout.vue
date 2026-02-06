<template>
  <div class="layout">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-content">
        <div class="logo" @click="goHome">
          <a href="javascript:void(0)">
            <img src="../image/android-chrome-512x512.png" alt="校园招聘" class="logo-img">
            <span class="logo-text">校园招聘系统</span>
          </a>
        </div>
        
        <el-menu
          :default-active="activeIndex"
          class="nav-menu"
          mode="horizontal"
          @select="handleMenuSelect"
          background-color="#545c64"
          text-color="#fff"
          active-text-color="#ffd04b">
          
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

        <div class="user-info">
          <div v-if="userInfo" class="user-menu">
            <el-dropdown @command="handleCommand">
              <span class="user-name">
                <el-avatar :size="32" :src="userInfo.avatar" icon="el-icon-user-solid"></el-avatar>
                {{ userInfo.nickName || userInfo.userName }}
                <i class="el-icon-arrow-down"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
          <div v-else class="auth-buttons">
            <el-button type="text" @click="$router.push('/login')">登录</el-button>
            <el-button type="primary" size="small" @click="$router.push('/register')">注册</el-button>
          </div>
        </div>
      </div>
    </el-header>

    <!-- 主要内容区域 -->
    <el-main class="main-content">
      <router-view />
    </el-main>

    <!-- 底部 -->
    <el-footer class="footer">
      <div class="footer-content">
        <p>&copy; 2025 校园招聘系统. All rights reserved.</p>
      </div>
    </el-footer>
  </div>
</template>

<script>
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
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #545c64;
  padding: 0;
  height: 60px !important;
  line-height: 60px;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.logo a {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: white;
}

.logo-img {
  height: 40px;
  margin-right: 10px;
}

.logo-text {
  font-size: 20px;
  font-weight: bold;
  color: white;
}

.nav-menu {
  flex: 1;
  margin: 0 40px;
  border-bottom: none;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-name {
  display: flex;
  align-items: center;
  color: white;
  cursor: pointer;
  padding: 0 10px;
}

.user-name .el-avatar {
  margin-right: 8px;
}

.auth-buttons .el-button--text {
  color: white;
}

.main-content {
  flex: 1;
  padding: 0;
  background-color: #f5f5f5;
}

.footer {
  background-color: #2c3e50;
  color: white;
  text-align: center;
  height: 60px !important;
  line-height: 60px;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    padding: 0 10px;
  }
  
  .nav-menu {
    margin: 0 20px;
  }
  
  .logo-text {
    display: none;
  }
}
</style>