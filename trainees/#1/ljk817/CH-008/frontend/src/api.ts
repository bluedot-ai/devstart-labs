import axios from 'axios'
import type { InternalAxiosRequestConfig } from 'axios'

export type Severity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'
export type IssueStatus = 'OPEN' | 'PROCESSING' | 'RESOLVED' | 'CLOSED'

export interface Customer {
  id: number; name: string; contactName?: string; phone?: string; email?: string;
  description?: string; active: boolean; createdAt: string; updatedAt: string
}
export interface Category { id:number;name:string;description?:string;active:boolean;createdAt:string }
export interface AssignableUser { id:number;username:string;displayName:string;role:'ADMIN'|'AGENT';enabled:boolean }
export interface Issue {
  id: number; issueNo: string; customerId: number; customerName: string; title: string;
  description: string; category: string; severity: Severity; status: IssueStatus;
  assignee?: string; createdAt: string; updatedAt: string; closedAt?: string
}
export interface IssueRecord {
  id: number; content: string; operatorName: string; authorRole?: 'ADMIN'|'AGENT'|'VIEWER'|'CUSTOMER';
  customerVisible: boolean; oldStatus?: IssueStatus;
  newStatus?: IssueStatus; createdAt: string
}
export interface Page<T> { content: T[]; totalElements: number; totalPages: number; number: number; size: number }

export const api = axios.create({
  baseURL: '/api', timeout: 10000, withCredentials: true
})
type RetryableConfig = InternalAxiosRequestConfig & { _csrfRetried?: boolean }

api.interceptors.response.use(response => response, async error => {
  const config = error.config as RetryableConfig | undefined
  if (error.response?.status === 403 && config && !config._csrfRetried && !config.url?.includes('/auth/csrf')) {
    config._csrfRetried = true
    try {
      const csrf = await api.get<{token:string}>('/auth/csrf')
      config.headers.set('X-XSRF-TOKEN', csrf.data.token)
      api.defaults.headers.common['X-XSRF-TOKEN'] = csrf.data.token
      return await api.request(config)
    } catch { /* 使用下方的统一错误提示 */ }
  }
  const message = error.response?.data?.message || '请求失败，请稍后重试'
  return Promise.reject(new Error(message))
})
