<template>
  <el-scrollbar wrap-class="scrollbar-wrapper">
    <el-menu :show-timeout="200" :default-active="$route.path" :collapse="isCollapse" :background-color="variables.menuBg" :text-color="variables.menuText" :active-text-color="variables.menuActiveText" mode="vertical">
      <sidebar-item v-for="route in routes" :key="route.path" :item="route" :base-path="route.path" />
    </el-menu>
  </el-scrollbar>
</template>

<script>
import { mapGetters } from 'vuex'
import variables from '@/styles/variables.scss'
import SidebarItem from './SidebarItem'
import { getStoredUserAuthority } from '@/utils/userRole'

export default {
  components: { SidebarItem },
  computed: {
    ...mapGetters([
      'sidebar'
    ]),
    routes () {
      const authority = getStoredUserAuthority()
      const all = this.$router.options.routes || []
      if (authority === 'Admin') {
        return all.filter(item => item.meta && (item.meta.role === 'admin' || item.meta.role === 'user'))
      }
      if (authority === 'Enterprise_User') {
        return all.filter(item => item.meta && (item.meta.role === 'company' || item.meta.role === 'user'))
      }
      return []
    },
    variables () {
      return variables
    },
    isCollapse () {
      return !this.sidebar.opened
    }
  }
}
</script>
