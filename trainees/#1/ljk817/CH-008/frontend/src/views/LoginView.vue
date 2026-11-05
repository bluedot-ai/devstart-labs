<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lock, User } from '@element-plus/icons-vue'
import { useAuth } from '../auth'

const router = useRouter(), auth = useAuth(), loading = ref(false)
const form = reactive({ username:'', password:'' })
async function submit(){
  loading.value=true
  try { await auth.login(form.username,form.password); await router.push('/') }
  catch(e){ ElMessage.error((e as Error).message) }
  finally { loading.value=false }
}
</script>

<template>
  <div class="login-page">
    <div class="login-visual"><div class="visual-content"><span class="brand-mark large">Q</span><h1>让每一个客户问题<br/>都有清晰的答案</h1><p>集中跟踪、协同处理、完整记录问题生命周期。</p><div class="visual-badges"><span>客户档案</span><span>问题跟踪</span><span>处理审计</span></div></div></div>
    <div class="login-panel"><div class="login-card"><h2>欢迎回来</h2><p>登录客户问题跟踪系统</p><el-form @submit.prevent="submit">
      <el-form-item><el-input v-model="form.username" size="large" placeholder="用户名" :prefix-icon="User" /></el-form-item>
      <el-form-item><el-input v-model="form.password" size="large" type="password" show-password placeholder="密码" :prefix-icon="Lock" @keyup.enter="submit" /></el-form-item>
      <el-button type="primary" size="large" style="width:100%" :loading="loading" :disabled="!form.username||!form.password" @click="submit">登录系统</el-button>
    </el-form><div class="login-foot"><span class="online-dot"></span>安全连接至客户服务中心</div></div></div>
  </div>
</template>
