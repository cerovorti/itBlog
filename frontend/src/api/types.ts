/**
 * 后端统一响应结构
 * { code: number, message: string, data: T }
 */
export interface ApiResponse<T = unknown> {
  code: number
  message: string
  data: T
}

/**
 * 分页数据
 */
export interface PageData<T> {
  records: T[]
  total: number
  page: number
  size: number
}