import { request } from '@/utils/request'
import { getResult } from '@/utils/result'

export function login(params) {
  return request({
    url: '/auth/login',
    method: 'post',
    data: {
      userName: params.username,
      password: params.password
    }
  }).then(res => getResult(res))
}

export function getInfo() {
  return request({
    url: '/user/username',
    method: 'get'
  }).then(res => getResult(res))
}

/* 查询企业信息 */
export function getCompanyInfo(params) {
  return request({
    url: '/company/user/name',
    method: 'get'
  }).then(res => getResult(res))
}

export function logout() {
  return request({
    url: '/user/logout',
    method: 'post'
  })
}
