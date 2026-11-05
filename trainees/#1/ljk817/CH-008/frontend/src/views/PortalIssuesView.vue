<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { api, type Category, type Issue, type Page, type Severity } from '../api'
import { formatTime, severityLabels, statusLabels, statusTag } from '../labels'
import { useAuth } from '../auth'

const router=useRouter(),auth=useAuth(),rows=ref<Issue[]>([]),categories=ref<Category[]>([]),loading=ref(false),visible=ref(false)
const activeCategories=computed(()=>categories.value.filter(item=>item.active))
const form=reactive<{title:string;description:string;category:string;severity:Severity}>({title:'',description:'',category:'',severity:'MEDIUM'})
const counts=computed(()=>({
  total:rows.value.length,
  open:rows.value.filter(item=>item.status==='OPEN').length,
  processing:rows.value.filter(item=>item.status==='PROCESSING').length,
  resolved:rows.value.filter(item=>item.status==='RESOLVED'||item.status==='CLOSED').length
}))

async function load(){loading.value=true;try{rows.value=(await api.get<Page<Issue>>('/issues',{params:{size:100,sort:'updatedAt,desc'}})).data.content}finally{loading.value=false}}
function openIssue(row:Issue){router.push(`/portal/issues/${row.id}`)}
async function submit(){
  try{
    await api.post('/issues',{...form,customerId:auth.state.user?.customerId,assignee:''})
    ElMessage.success('问题已提交，服务团队会尽快处理')
    visible.value=false
    Object.assign(form,{title:'',description:'',category:'',severity:'MEDIUM'})
    await load()
  }catch(e){ElMessage.error((e as Error).message)}
}
onMounted(async()=>{categories.value=(await api.get<Category[]>('/categories')).data;await load()})
</script>

<template>
  <div class="page-heading"><div><h1>我的问题</h1><p>{{auth.state.user?.customerName}} · 提交问题并随时查看处理进展</p></div><el-button type="primary" @click="visible=true">提交新问题</el-button></div>
  <div class="stat-grid four">
    <div class="stat-card accent"><small>问题总数</small><strong>{{counts.total}}</strong></div>
    <div class="stat-card"><small>待受理</small><strong>{{counts.open}}</strong></div>
    <div class="stat-card"><small>处理中</small><strong>{{counts.processing}}</strong></div>
    <div class="stat-card"><small>已解决/关闭</small><strong>{{counts.resolved}}</strong></div>
  </div>
  <div class="panel">
    <h3>问题记录</h3>
    <el-table :data="rows" v-loading="loading" @row-click="openIssue">
      <el-table-column prop="issueNo" label="问题编号" width="190"/>
      <el-table-column prop="title" label="问题标题" min-width="260"/>
      <el-table-column prop="category" label="分类" width="140"/>
      <el-table-column label="严重程度" width="110"><template #default="{row}"><span :class="['severity-dot',row.severity]"></span>{{severityLabels[row.severity as Severity]}}</template></el-table-column>
      <el-table-column label="处理状态" width="115"><template #default="{row}"><el-tag :type="statusTag[row.status as keyof typeof statusTag]">{{statusLabels[row.status as keyof typeof statusLabels]}}</el-tag></template></el-table-column>
      <el-table-column label="最近更新" width="175"><template #default="{row}">{{formatTime(row.updatedAt)}}</template></el-table-column>
    </el-table>
  </div>
  <el-dialog v-model="visible" title="提交客户问题" width="620px">
    <el-form :model="form" label-position="top">
      <el-form-item label="问题标题" required><el-input v-model="form.title" placeholder="请简要描述遇到的问题"/></el-form-item>
      <el-form-item label="详细说明" required><el-input v-model="form.description" type="textarea" :rows="5" placeholder="请描述问题现象、发生时间和影响，避免填写密码等敏感信息"/></el-form-item>
      <div style="display:grid;grid-template-columns:1fr 1fr;gap:16px">
        <el-form-item label="问题分类" required><el-select v-model="form.category" style="width:100%"><el-option v-for="item in activeCategories" :key="item.id" :label="item.name" :value="item.name"/></el-select></el-form-item>
        <el-form-item label="紧急程度"><el-select v-model="form.severity" style="width:100%"><el-option v-for="(label,key) in severityLabels" :key="key" :label="label" :value="key"/></el-select></el-form-item>
      </div>
    </el-form>
    <template #footer><el-button @click="visible=false">取消</el-button><el-button type="primary" :disabled="!form.title||!form.description||!form.category" @click="submit">确认提交</el-button></template>
  </el-dialog>
</template>
