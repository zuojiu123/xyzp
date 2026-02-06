import router from './router'

import { getToken } from '@/utils/auth'

const whiteList = ['/login'] // 不重定向白名单
router.beforeEach((to, from, next) => {
  const token = getToken()

  if (token) {
    // 验证用户角色
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    if (userInfo.role !== 'Admin') {
      // 非管理员用户，清除token并跳转到登录页
      localStorage.removeItem('recruit-Token')
      localStorage.removeItem('userInfo')
      next('/login')
      return
    }

    if (to.path === '/login') {
      next({ path: '/' })
    } else {
      next()
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
    }
  }
})
