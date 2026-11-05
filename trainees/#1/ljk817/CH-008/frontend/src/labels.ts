import type { IssueStatus, Severity } from './api'
export const statusLabels: Record<IssueStatus,string> = { OPEN:'待处理', PROCESSING:'处理中', RESOLVED:'已解决', CLOSED:'已关闭' }
export const severityLabels: Record<Severity,string> = { LOW:'低', MEDIUM:'中', HIGH:'高', CRITICAL:'严重' }
export const statusTag: Record<IssueStatus, 'danger' | 'warning' | 'primary' | 'success'> = { OPEN:'danger', PROCESSING:'warning', RESOLVED:'primary', CLOSED:'success' }
export const formatTime = (value?: string) => value ? new Date(value).toLocaleString('zh-CN',{hour12:false}) : '-'
