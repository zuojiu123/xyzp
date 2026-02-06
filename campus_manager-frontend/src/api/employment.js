import { request } from '@/utils/request'
import { getResult } from '@/utils/result'

/**
* 获取职位列表（管理端显示所有状态）
*/
export function getEmploymentList(params) {
  const requestData = {
    showAll: true // 管理端显示所有状态的职位
  }
  // 如果有筛选条件，传递筛选条件
  if (params.condition) {
    Object.assign(requestData, params.condition)
  }
  return request({
    url: `employment/${params.pageNum}/${params.pageSize}`,
    method: 'post',
    data: requestData
  }).then(res => getResult(res))
}
/**
* 获取用户列表
*/
export function getSingleEmploymentList(params) {
  return request({
    url: `employment/username/${params.pageNum}/${params.pageSize}`,
    method: 'get',
    params: params.params
  }).then(res => getResult(res))
}

/**
* 新增用户信息
*/
export function addEmployment(params) {
  return request({
    url: `employment`,
    method: 'post',
    data: params
  }).then(res => getResult(res))
}

/**
* 修改用户信息
*/
export function updateEmployment(params) {
  return request({
    url: `employment`,
    method: 'put',
    data: params
  }).then(res => getResult(res))
}

/**
* 修改职位
*/
export function deleteEmployment(id) {
  return request({
    url: `employment/${id}`,
    method: 'delete'
  }).then(res => getResult(res))
}

/**
* 按条件查询职位（管理端）
*/
export function queryEmployment(params) {
  const requestData = {
    ...params.condition,
    showAll: true // 管理端查询时也显示所有状态
  }
  return request({
    url: `employment/${params.pageNum}/${params.pageSize}`,
    method: 'post',
    data: requestData
  }).then(res => getResult(res))
}

