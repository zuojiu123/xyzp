import Vue from 'vue'
import VueRouter from 'vue-router'
import { getUserInfo, isLoggedIn } from '@/utils/auth'

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
  {
    path: '/notifications',
    name: 'Notifications',
    component: () => import('@/views/Notifications.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/resume',
    name: 'Resume',
    component: () => import('@/views/Resume.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/resume-templates',
    name: 'ResumeTemplates',
    component: () => import('@/views/ResumeTemplates.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/company/edit/:id',
    name: 'CompanyEdit',
    component: () => import('@/views/company/CompanyEdit.vue'),
    meta: {
      title: '编辑企业信息',
      requiresAuth: true,
      role: 'enterprise'
    }
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('@/views/About.vue')
  },
  {
    path: '/contact',
    name: 'Contact',
    component: () => import('@/views/Contact.vue')
  },
  {
    path: '/privacy',
    name: 'PrivacyPolicy',
    component: () => import('@/views/PrivacyPolicy.vue')
  },
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
  {
    path: '/company/notifications',
    name: 'CompanyNotifications',
    component: () => import('@/views/Notifications.vue'),
    meta: { requiresAuth: true, requiresRole: 'Enterprise_User' }
  },
  {
    path: '/company/:id',
    name: 'CompanyDetail',
    component: () => import('@/views/CompanyDetail.vue')
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    }
    return { x: 0, y: 0 }
  }
})

function redirectToAdminConsole() {
  const { protocol, hostname } = window.location
  window.location.href = `${protocol}//${hostname}:8081/login`
}

router.beforeEach((to, from, next) => {
  const currentUser = getUserInfo()
  if (currentUser && currentUser.role === 'Admin') {
    redirectToAdminConsole()
    return
  }

  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!isLoggedIn()) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }

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
