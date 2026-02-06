<template>
  <div class="company-layout">
    <el-header class="header">
      <div class="header-content">
        <div class="logo" @click="$router.push('/company/dashboard')">
          <img src="../image/android-chrome-512x512.png" alt="Logo" class="logo-img">
          <span class="logo-text">企业管理中心</span>
        </div>
        
        <el-menu
          :default-active="$route.path"
          class="nav-menu"
          mode="horizontal"
          @select="handleMenuSelect"
          background-color="#545c64"
          text-color="#fff"
          active-text-color="#ffd04b">
          <el-menu-item index="/company/dashboard">企业仪表板</el-menu-item>
          <el-menu-item index="/company/jobs">职位管理</el-menu-item>
          <el-menu-item index="/company/applications">申请管理</el-menu-item>
          <el-menu-item index="/company/profile">企业资料</el-menu-item>
        </el-menu>

        <div class="user-info">
          <el-dropdown @command="handleCommand">
            <span class="user-name">
              <el-avatar :size="32" icon="el-icon-user-solid"></el-avatar>
              {{ userInfo.nickName || userInfo.userName }}
              <i class="el-icon-arrow-down"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <el-main class="main-content">
      <router-view />
    </el-main>

    <el-footer class="footer">
      <p>&copy; 2025 校园招聘系统. All rights reserved.</p>
    </el-footer>
  </div>
</template>

<script>
export default {
  name: 'CompanyLayout',
  data() {
    return {
      userInfo: {}
    }
  },
  mounted() {
    const userInfo = localStorage.getItem('userInfo')
    if (userInfo) {
      this.userInfo = JSON.parse(userInfo)
    }
  },
  methods: {
    handleMenuSelect(index) {
      this.$router.push(index).catch(() => {})
    },
    
    handleCommand(command) {
      if (command === 'logout') {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        this.$message.success('已退出登录')
        this.$router.push('/login')
      }
    }
  }
}
</script>

<style scoped>
.company-layout {
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
  color: white;
}

.logo-img {
  height: 40px;
  margin-right: 10px;
}

.logo-text {
  font-size: 20px;
  font-weight: bold;
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
</style>
