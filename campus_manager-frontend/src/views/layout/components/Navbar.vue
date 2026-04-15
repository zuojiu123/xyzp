<template>
  <div class="navbar">
    <div class="navbar-left">
      <hamburger :toggle-click="toggleSideBar" :is-active="sidebar.opened" class="hamburger-container" />
      <div class="brand-copy">
        <span class="brand-copy__label">Admin Console</span>
        <breadcrumb />
      </div>
    </div>
    <el-dropdown class="avatar-container" trigger="click">
      <div class="avatar-wrapper">
        <div class="avatar-meta">
          <span class="avatar-role">Admin</span>
          <span class="avatar-name">系统管理员</span>
        </div>
        <img src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" class="user-avatar">
        <i class="el-icon-caret-bottom" />
      </div>
      <el-dropdown-menu slot="dropdown" class="user-dropdown">
        <router-link class="inlineBlock" to="/dashboard">
          <el-dropdown-item>工作台</el-dropdown-item>
        </router-link>
        <el-dropdown-item divided>
          <span style="display:block;" @click="logout">退出登录</span>
        </el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import Hamburger from '@/components/Hamburger'

export default {
  components: {
    Breadcrumb,
    Hamburger
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'avatar'
    ])
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('ToggleSideBar')
    },
    logout() {
      this.$store.dispatch('logOut').then(() => {
        location.reload()
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 72px;
  padding: 0 28px 0 12px;
  background: rgba(255, 255, 255, 0.84);
  backdrop-filter: blur(18px);
  border-bottom: 1px solid rgba(231, 237, 246, 0.9);
  box-shadow: 0 10px 30px rgba(20, 33, 61, 0.06);
}

.navbar-left {
  display: flex;
  align-items: center;
  min-width: 0;
  gap: 10px;
}

.hamburger-container {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  border-radius: 12px;
  transition: background .22s ease;
}

.hamburger-container:hover {
  background: #f4f7fb;
}

.brand-copy {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.brand-copy__label {
  margin-bottom: 2px;
  color: #ff7a18;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: .12em;
  text-transform: uppercase;
}

.avatar-container {
  height: 72px;
  display: inline-flex;
  align-items: center;
}

.avatar-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 10px 14px;
  border-radius: 16px;
  line-height: initial;
  transition: all .22s ease;
}

.avatar-wrapper:hover {
  background: #f8fafe;
}

.avatar-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.avatar-role {
  color: #ff7a18;
  font-size: 12px;
  font-weight: 700;
}

.avatar-name {
  color: #1f2a44;
  font-size: 14px;
  font-weight: 600;
}

.user-avatar {
  width: 42px;
  height: 42px;
  border-radius: 14px;
  box-shadow: 0 10px 20px rgba(47, 128, 237, 0.18);
}

.el-icon-caret-bottom {
  font-size: 12px;
  color: #7b879c;
}
</style>
