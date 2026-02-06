<template>
  <div id="app">
    <CompanyLayout v-if="isCompanyPage" />
    <Layout v-else-if="!isAuthPage" />
    <router-view v-else />
  </div>
</template>

<script>
import Layout from '@/components/Layout.vue'
import CompanyLayout from '@/components/CompanyLayout.vue'

export default {
  name: 'App',
  components: {
    Layout,
    CompanyLayout
  },
  computed: {
    isAuthPage() {
      const authPages = ['/login', '/register', '/company/register']
      return authPages.includes(this.$route.path)
    },
    isCompanyPage() {
      return this.$route.path.startsWith('/company/') && this.$route.path !== '/company/register'
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}

body {
  margin: 0;
  background-color: #f5f5f5;
}
</style>