import { reactive } from 'vue'
import { api } from './api'

export type UserRole = 'ADMIN' | 'AGENT' | 'VIEWER' | 'CUSTOMER'
export interface CurrentUser { id:number; username:string; displayName:string; role:UserRole; customerId?:number; customerName?:string }

const state = reactive<{user:CurrentUser|null; checked:boolean}>({ user:null, checked:false })

export function useAuth() {
  async function refreshCsrf() {
    const response = await api.get<{token:string}>('/auth/csrf')
    api.defaults.headers.common['X-XSRF-TOKEN'] = response.data.token
  }
  async function loadMe() {
    try {
      state.user = (await api.get<CurrentUser>('/auth/me')).data
      await refreshCsrf()
    }
    catch { state.user = null }
    finally { state.checked = true }
    return state.user
  }
  async function login(username:string, password:string) {
    await refreshCsrf()
    state.user = (await api.post<CurrentUser>('/auth/login', { username, password })).data
    state.checked = true
  }
  async function logout() {
    await api.post('/auth/logout')
    state.user = null
    window.location.assign('/login')
  }
  return { state, loadMe, login, logout }
}
