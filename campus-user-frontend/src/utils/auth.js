// 认证相关工具函数

/**
 * 获取token
 */
export function getToken() {
  return localStorage.getItem('token')
}

/**
 * 设置token
 */
export function setToken(token) {
  localStorage.setItem('token', token)
}

/**
 * 移除token
 */
export function removeToken() {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  try {
    const userInfo = localStorage.getItem('userInfo')
    return userInfo ? JSON.parse(userInfo) : null
  } catch (error) {
    console.error('解析用户信息失败:', error)
    return null
  }
}

/**
 * 设置用户信息
 */
export function setUserInfo(userInfo) {
  localStorage.setItem('userInfo', JSON.stringify(userInfo))
}

/**
 * 检查是否已登录
 */
export function isLoggedIn() {
  return !!getToken()
}

/**
 * 检查用户角色
 */
export function hasRole(role) {
  const userInfo = getUserInfo()
  return userInfo && userInfo.role === role
}

/**
 * 检查是否为企业用户
 */
export function isEnterpriseUser() {
  return hasRole('Enterprise_User')
}

/**
 * 检查是否为普通用户
 */
export function isNormalUser() {
  return hasRole('Normal_User')
}

/**
 * 检查是否为管理员
 */
export function isAdmin() {
  return hasRole('Admin')
}