import { request } from '@/utils/request'
import { getResult } from '@/utils/result'

export function getAuditLogList(params) {
  return request({
    url: `auditLog/${params.pageNum}/${params.pageSize}`,
    method: 'post',
    data: params.condition || {}
  }).then(res => getResult(res))
}
