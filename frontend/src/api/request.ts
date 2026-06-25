import axios, { type AxiosRequestConfig } from 'axios'
import { getToken, removeToken } from '../utils/token'
import { ElMessage } from 'element-plus'
import type { ApiResponse } from './types'

/** Axios 实例 */
const instance = axios.create({
  baseURL: '/api',
  timeout: 30000,
})

// ==================== 请求拦截器 ====================

instance.interceptors.request.use(config => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// ==================== 响应拦截器 ====================

instance.interceptors.response.use(
  (response) => {
    const body = response.data as ApiResponse
    const { code, message } = body

    if (code === 200) {
      return body as any // 保持原有行为：返回完整 { code, message, data }
    }

    // ---- 非 200 业务状态码统一处理 ----
    if (code === 401) {
      removeToken()
      const path = window.location.pathname
      if (path !== '/login' && path !== '/register') {
        window.location.href = '/login'
      }
    }

    ElMessage.error(message || '请求失败')
    return Promise.reject(new Error(message))
  },
  error => {
    // 网络异常 / 超时 / HTTP 状态码非 2xx
    let msg = '网络错误'
    if (error.response) {
      const status = error.response.status
      if (status === 401) {
        removeToken()
        window.location.href = '/login'
        msg = '登录已过期，请重新登录'
      } else if (status === 403) {
        msg = '权限不足'
      } else if (status === 404) {
        msg = '请求的资源不存在'
      } else if (status >= 500) {
        msg = error.response.data?.message || '服务器异常'
      }
    }
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

// ==================== 封装请求方法（带泛型） ====================

/**
 * GET 请求
 * @returns 完整响应 { code, message, data: T }
 */
export function get<T = any>(url: string, params?: Record<string, any>): Promise<ApiResponse<T>> {
  return instance.get(url, { params }) as any
}

/**
 * POST 请求
 */
export function post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
  return instance.post(url, data, config) as any
}

/**
 * PUT 请求
 */
export function put<T = any>(url: string, data?: any): Promise<ApiResponse<T>> {
  return instance.put(url, data) as any
}

/**
 * DELETE 请求
 */
export function del<T = any>(url: string): Promise<ApiResponse<T>> {
  return instance.delete(url) as any
}

/** 原始 axios 实例，供特殊场景使用（如上传文件需自定义 headers） */
export { instance as rawAxios }

export default instance