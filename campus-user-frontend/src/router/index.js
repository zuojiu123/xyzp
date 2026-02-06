import Vue from 'vue'
import VueRouter from 'vue-router'
import { isLoggedIn, isEnterpriseUser, getUserInfo } from '@/utils/auth'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/jobs',
    name: 'Jobs',
    component: () => import('@/views/Jobs.vue')
  },
  {
    path: '/job/:id',
    name: 'JobDetail',
    component: () => import('@/views/JobDetail.vue')
  },
  {
    path: '/companies',
    name: 'Companies',
    component: () => import('@/views/Companies.vue')
  },
  {
    path: '/articles',
    name: 'Articles',
    component: () => import('@/views/Articles.vue')
  },
  {
    path: '/article/:id',
    name: 'ArticleDetail',
    component: () => import('@/views/ArticleDetail.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { requiresAuth: true }
  },
  // 企业相关路由（必须在 /company/:id 之前）
  {
    path: '/company/register',
    name: 'CompanyRegister',
    component: () => import('@/views/company/CompanyRegister.vue')
  },
  {
    path: '/company/dashboard',
    name: 'CompanyDashboard',
    component: () => import('@/views/company/CompanyDashboard.vue'),
    meta: { requiresAuth: true, requiresRole: 'Enterprise_User' }
  },
  {
    path: '/company/jobs',
    name: 'CompanyJobs',
    component: () => import('@/views/company/CompanyJobs.vue'),
    meta: { requiresAuth: true, requiresRole: 'Enterprise_User' }
  },
  {
    path: '/company/applications',
    name: 'CompanyApplications',
    component: () => import('@/views/company/CompanyApplications.vue'),
    meta: { requiresAuth: true, requiresRole: 'Enterprise_User' }
  },
  {
    path: '/company/profile',
    name: 'CompanyProfile',
    component: () => import('@/views/company/CompanyProfile.vue'),
    meta: { requiresAuth: true, requiresRole: 'Enterprise_User' }
  },
  // 动态路由放在最后
  {
    path: '/company/:id',
    name: 'CompanyDetail',
    component: () => import('@/views/CompanyDetail.vue')
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 检查是否需要认证
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!isLoggedIn()) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }
    
    // 检查角色权限
    if (to.meta.requiresRole) {
      const userInfo = getUserInfo()
      if (!userInfo || userInfo.role !== to.meta.requiresRole) {
        next('/')
        return
      }
    }
  }
  
  next()
})

export default router