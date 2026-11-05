import { createRouter, createWebHistory } from 'vue-router'
import DashboardView from './views/DashboardView.vue'
import CustomersView from './views/CustomersView.vue'
import IssuesView from './views/IssuesView.vue'
import IssueDetailView from './views/IssueDetailView.vue'
import LoginView from './views/LoginView.vue'
import UsersView from './views/UsersView.vue'
import CategoriesView from './views/CategoriesView.vue'
import PortalIssuesView from './views/PortalIssuesView.vue'
import PortalIssueDetailView from './views/PortalIssueDetailView.vue'
import { useAuth } from './auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: LoginView, meta: { public: true } },
    { path: '/portal', component: PortalIssuesView, meta: { customer: true } },
    { path: '/portal/issues/:id', component: PortalIssueDetailView, meta: { customer: true } },
    { path: '/', component: DashboardView },
    { path: '/customers', component: CustomersView },
    { path: '/issues', component: IssuesView },
    { path: '/issues/:id', component: IssueDetailView },
    { path: '/users', component: UsersView, meta: { admin: true } },
    { path: '/categories', component: CategoriesView, meta: { admin: true } }
  ]
})

router.beforeEach(async to => {
  const auth = useAuth()
  if (!auth.state.checked) await auth.loadMe()
  if (!to.meta.public && !auth.state.user) return '/login'
  if (auth.state.user?.role === 'CUSTOMER' && !to.meta.customer && !to.meta.public) return '/portal'
  if (auth.state.user?.role !== 'CUSTOMER' && to.meta.customer) return '/'
  if (to.meta.admin && auth.state.user?.role !== 'ADMIN') return '/'
  if (to.path === '/login' && auth.state.user) return auth.state.user.role === 'CUSTOMER' ? '/portal' : '/'
})

export default router
