import axios from 'axios'
import { Message } from 'element-ui'
import { getToken, removeToken } from '@/utils/auth'

const baseURL = process.env.VUE_APP_BASE_API || 'http://localhost:3030'

export function request(config) {
  const instance = axios.create({
    baseURL,
    timeout: 15000
  })

  instance.interceptors.request.use(
    cfg => {
      const token = getToken()
      if (token) {
        const v = String(token).trim()
        cfg.headers.Authorization = /^bearer\s+/i.test(v) ? v : `Bearer ${v}`
      }
      return cfg
    },
    error => Promise.reject(error)
  )

  instance.interceptors.response.use(
    res => res,
    err => {
      const status = err.response && err.response.status
      const msg =
        (err.response && err.response.data && err.response.data.msg) ||
        err.message ||
        '网络请求失败'

      if (status === 401) {
        removeToken()
        if (!window.location.pathname.includes('/login')) {
          Message.error('登录已过期，请重新登录')
          window.location.href = `/login?redirect=${encodeURIComponent(window.location.pathname)}`
        }
      } else if (status === 403) {
        Message.error('没有权限执行该操作')
      } else if (!err.response) {
        Message.error('无法连接服务器，请检查后端是否启动或接口地址')
      } else {
        Message.error(msg)
      }

      return Promise.reject(err)
    }
  )

  return instance(config)
}
