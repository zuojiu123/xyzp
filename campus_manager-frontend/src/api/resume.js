import { request } from '@/utils/request'
import { getResult } from '@/utils/result'

/**
 * 获取所有简历申请列表（管理端）
 */
export function getAllResumeList(params) {
  return request({
    url: `employmentUser/admin/${params.pageNum}/${params.pageSize}`,
    method: 'post',
    data: {
      showAll: true, // 管理端显示所有状态
      ...params.condition
    }
  }).then(res => getResult(res))
}

/**
 * 按条件查询简历申请（管理端）
 */
export function queryResumeList(params) {
  return request({
    url: `employmentUser/admin/${params.pageNum}/${params.pageSize}`,
    method: 'post',
    data: {
      showAll: true,
      ...params.condition
    }
  }).then(res => getResult(res))
}

/**
 * 获取简历统计信息
 */
export function getResumeStats() {
  return request({
    url: 'employmentUser/stats',
    method: 'get'
  }).then(res => getResult(res))
}

/**
 * 审核简历申请
 */
export function auditResume(params) {
  return request({
    url: 'employmentUser/audit',
    method: 'put',
    data: params
  }).then(res => getResult(res))
}

/**
 * 下载简历文件
 */
export function downloadResume(resumeId) {
  return request({
    url: `userResume/download/${resumeId}`,
    method: 'get',
    responseType: 'blob'
  })
}
