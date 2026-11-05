<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { House, User, Tickets, Bell, Setting, SwitchButton } from '@element-plus/icons-vue'
import { useAuth } from './auth'
const route=useRoute(), auth=useAuth()
const isLogin=computed(()=>route.path==='/login')
const roleLabel={ADMIN:'管理员',AGENT:'处理人员',VIEWER:'只读用户',CUSTOMER:'客户用户'}
const isCustomer=computed(()=>auth.state.user?.role==='CUSTOMER')
</script>

<template>
  <RouterView v-if="isLogin" />
  <div v-else class="app-shell">
    <aside class="sidebar">
      <div class="brand">
        <span class="brand-mark">Q</span>
        <div><strong>QuestFlow</strong><small>客户问题跟踪</small></div>
      </div>
      <nav>
        <RouterLink v-if="isCustomer" to="/portal"><el-icon><Tickets /></el-icon>我的问题</RouterLink>
        <template v-else>
        <RouterLink to="/"><el-icon><House /></el-icon>工作台</RouterLink>
        <RouterLink to="/customers"><el-icon><User /></el-icon>客户管理</RouterLink>
        <RouterLink to="/issues"><el-icon><Tickets /></el-icon>问题管理</RouterLink>
        </template>
        <RouterLink v-if="auth.state.user?.role==='ADMIN'" to="/users"><el-icon><Setting /></el-icon>用户管理</RouterLink>
        <RouterLink v-if="auth.state.user?.role==='ADMIN'" to="/categories"><el-icon><Setting /></el-icon>问题分类</RouterLink>
      </nav>
      <div class="sidebar-foot"><span class="online-dot"></span>系统运行正常</div>
    </aside>
    <main class="main-area">
      <header class="topbar">
        <div><b>{{isCustomer?'客户问题中心':'客户服务中心'}}</b><span>{{isCustomer?'提交问题并查看最新处理进展':'集中跟踪每一个客户问题'}}</span></div>
        <div class="top-actions"><el-icon><Bell /></el-icon><span class="avatar">{{auth.state.user?.displayName}} · {{roleLabel[auth.state.user?.role||'VIEWER']}}</span><el-button class="logout-button" :icon="SwitchButton" @click="auth.logout">退出登录</el-button></div>
      </header>
      <section class="page-container"><RouterView /></section>
    </main>
  </div>
</template>
