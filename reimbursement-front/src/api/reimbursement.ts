import request from './request'
import type { Reimbursement } from '../types'

// 定义查询参数的强类型接口
export interface ReimbursementQueryParams {
  reimNo?: string
  title?: string
  reason?: string
  companyId?: string
  deptId?: string
  reimburserId?: string
  businessTypeId?: string
  pageNum: number
  pageSize: number
}

// 定义后端分页返回值的强类型接口
export interface PageResult<T> {
  records: T[]
  total: number
}

// 获取分页列表
export function getReimbursementList(
  params: ReimbursementQueryParams,
): Promise<PageResult<Reimbursement>> {
  return request.get('/reimbursement/list', { params })
}
// 加载单条明细
export function getReimbursementDetail(id: string): Promise<Reimbursement> {
  return request.get(`/reimbursement/detail/${id}`)
}

// 保存草稿
export function saveDraft(data: Reimbursement): Promise<string> {
  return request.post('/reimbursement/save', data)
}

// 正式提交
export function submitSheet(data: Reimbursement): Promise<void> {
  return request.post('/reimbursement/submit', data)
}

// 删除单据
export function deleteReimbursement(id: string): Promise<void> {
  return request.delete(`/reimbursement/delete/${id}`)
}

// 作废单据
export function cancelReimbursement(id: string): Promise<void> {
  return request.put(`/reimbursement/cancel/${id}`)
}
