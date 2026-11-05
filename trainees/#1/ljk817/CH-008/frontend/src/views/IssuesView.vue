<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { api, type AssignableUser, type Category, type Customer, type Issue, type Page, type Severity } from '../api'
import { formatTime, severityLabels, statusLabels, statusTag } from '../labels'
import { useAuth } from '../auth'
const auth=useAuth(),canEdit=computed(()=>auth.state.user?.role!=='VIEWER')
const route=useRoute(), rows=ref<Issue[]>([]), customers=ref<Customer[]>([]), loading=ref(false), visible=ref(false)
const categories=ref<Category[]>([]),assignees=ref<AssignableUser[]>([])
const activeCustomers=computed(()=>customers.value.filter(item=>item.active))
const activeCategories=computed(()=>categories.value.filter(item=>item.active))
const filters=reactive({keyword:'',status:'',severity:'',customerId:route.query.customerId||''})
const form=reactive<{customerId:number|string,title:string,description:string,category:string,severity:Severity,assignee:string}>({customerId:'',title:'',description:'',category:'',severity:'MEDIUM',assignee:''})
function openIssue(row: Issue){ window.location.assign(`/issues/${row.id}`) }
async function load(){ loading.value=true; try { rows.value=(await api.get<Page<Issue>>('/issues',{params:{...filters,size:100}})).data.content } finally{loading.value=false} }
async function submit(){ try{ await api.post('/issues',form);ElMessage.success('问题创建成功');visible.value=false;Object.assign(form,{customerId:'',title:'',description:'',category:'',severity:'MEDIUM',assignee:''});await load()}catch(e){ElMessage.error((e as Error).message)} }
watch(filters,load)
onMounted(async()=>{const [customerResult,categoryResult,assigneeResult]=await Promise.all([api.get<Page<Customer>>('/customers',{params:{size:100}}),api.get<Category[]>('/categories'),api.get<AssignableUser[]>('/users/assignable')]);customers.value=customerResult.data.content;categories.value=categoryResult.data;assignees.value=assigneeResult.data;await load()})
</script>

<template>
  <div class="page-heading"><div><h1>问题管理</h1><p>筛选、跟进并关闭客户问题</p></div><el-button v-if="canEdit" type="primary" @click="visible=true">创建问题</el-button></div>
  <div class="panel">
    <div class="filters">
      <el-input v-model="filters.keyword" clearable placeholder="搜索编号或标题" style="width:240px" />
      <el-select v-model="filters.status" clearable placeholder="全部状态" style="width:140px"><el-option v-for="(label,key) in statusLabels" :key="key" :label="label" :value="key" /></el-select>
      <el-select v-model="filters.severity" clearable placeholder="严重程度" style="width:140px"><el-option v-for="(label,key) in severityLabels" :key="key" :label="label" :value="key" /></el-select>
      <el-select v-model="filters.customerId" clearable filterable placeholder="全部客户" style="width:190px"><el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id" /></el-select>
    </div>
    <el-table :data="rows" v-loading="loading" @row-click="openIssue">
      <el-table-column prop="issueNo" label="问题编号" width="190" /><el-table-column prop="title" label="标题" min-width="230" /><el-table-column prop="customerName" label="客户" width="150" />
      <el-table-column label="严重程度" width="110"><template #default="{row}"><span :class="['severity-dot',row.severity]"></span>{{severityLabels[row.severity as Severity]}}</template></el-table-column>
      <el-table-column label="状态" width="105"><template #default="{row}"><el-tag :type="statusTag[row.status as keyof typeof statusTag]">{{statusLabels[row.status as keyof typeof statusLabels]}}</el-tag></template></el-table-column>
      <el-table-column prop="assignee" label="负责人" width="115" /><el-table-column label="更新时间" width="175"><template #default="{row}">{{formatTime(row.updatedAt)}}</template></el-table-column>
    </el-table>
  </div>
  <el-dialog v-model="visible" title="创建客户问题" width="620px">
    <el-form :model="form" label-position="top">
      <el-form-item label="客户" required><el-select v-model="form.customerId" filterable style="width:100%"><el-option v-for="c in activeCustomers" :key="c.id" :label="c.name" :value="c.id" /></el-select></el-form-item>
      <el-form-item label="问题标题" required><el-input v-model="form.title" /></el-form-item>
      <el-form-item label="问题描述" required><el-input v-model="form.description" type="textarea" :rows="4" /></el-form-item>
      <div style="display:grid;grid-template-columns:1fr 1fr;gap:16px"><el-form-item label="分类" required><el-select v-model="form.category" style="width:100%"><el-option v-for="item in activeCategories" :key="item.id" :label="item.name" :value="item.name" /></el-select></el-form-item><el-form-item label="严重程度"><el-select v-model="form.severity" style="width:100%"><el-option v-for="(label,key) in severityLabels" :key="key" :label="label" :value="key" /></el-select></el-form-item></div>
      <el-form-item label="负责人"><el-select v-model="form.assignee" clearable filterable style="width:100%"><el-option v-for="user in assignees" :key="user.id" :label="`${user.displayName} (${user.username})`" :value="user.username" /></el-select></el-form-item>
    </el-form>
    <template #footer><el-button @click="visible=false">取消</el-button><el-button type="primary" :disabled="!form.customerId||!form.title||!form.description||!form.category" @click="submit">创建问题</el-button></template>
  </el-dialog>
</template>
