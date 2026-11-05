<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { api, type Customer, type Page } from '../api'
import { formatTime } from '../labels'
import type { UserRole } from '../auth'
interface UserRow { id:number;username:string;displayName:string;role:UserRole;customerId?:number;customerName?:string;enabled:boolean;createdAt:string }
const rows=ref<UserRow[]>([]),customers=ref<Customer[]>([]),visible=ref(false),loading=ref(false)
const form=reactive<{username:string;password:string;displayName:string;role:UserRole;customerId:number|string}>({username:'',password:'',displayName:'',role:'AGENT',customerId:''})
const roleLabels={ADMIN:'管理员',AGENT:'处理人员',VIEWER:'只读用户',CUSTOMER:'客户用户'}
async function load(){loading.value=true;try{rows.value=(await api.get<UserRow[]>('/users')).data}finally{loading.value=false}}
async function create(){try{await api.post('/users',{...form,customerId:form.role==='CUSTOMER'?form.customerId:null});ElMessage.success('用户创建成功');visible.value=false;Object.assign(form,{username:'',password:'',displayName:'',role:'AGENT',customerId:''});await load()}catch(e){ElMessage.error((e as Error).message)}}
async function toggle(row:UserRow,value:boolean){try{await api.patch(`/users/${row.id}/enabled`,{enabled:value});ElMessage.success('用户状态已更新');await load()}catch(e){ElMessage.error((e as Error).message);await load()}}
function onToggle(row:UserRow,value:string|number|boolean){return toggle(row,Boolean(value))}
onMounted(async()=>{customers.value=(await api.get<Page<Customer>>('/customers',{params:{size:100}})).data.content;await load()})
</script>

<template>
  <div class="page-heading"><div><h1>用户管理</h1><p>创建独立账号并分配最小权限</p></div><el-button type="primary" @click="visible=true">新增用户</el-button></div>
  <div class="panel"><el-table :data="rows" v-loading="loading"><el-table-column prop="username" label="用户名"/><el-table-column prop="displayName" label="姓名"/><el-table-column label="角色"><template #default="{row}"><el-tag>{{roleLabels[row.role as UserRole]}}</el-tag></template></el-table-column><el-table-column prop="customerName" label="绑定客户"><template #default="{row}">{{row.customerName||'—'}}</template></el-table-column><el-table-column label="创建时间"><template #default="{row}">{{formatTime(row.createdAt)}}</template></el-table-column><el-table-column label="启用" width="100"><template #default="{row}"><el-switch :model-value="row.enabled" @change="onToggle(row,$event)"/></template></el-table-column></el-table></div>
  <el-dialog v-model="visible" title="新增系统用户" width="520px"><el-form label-position="top"><el-form-item label="用户名" required><el-input v-model="form.username" placeholder="3-60位字母、数字、点、横线或下划线"/></el-form-item><el-form-item label="显示名称" required><el-input v-model="form.displayName"/></el-form-item><el-form-item label="初始密码" required><el-input v-model="form.password" type="password" show-password placeholder="至少12位"/></el-form-item><el-form-item label="角色"><el-select v-model="form.role" style="width:100%" @change="form.customerId=''"><el-option v-for="(label,key) in roleLabels" :key="key" :label="label" :value="key"/></el-select></el-form-item><el-form-item v-if="form.role==='CUSTOMER'" label="绑定客户" required><el-select v-model="form.customerId" filterable style="width:100%"><el-option v-for="c in customers.filter(item=>item.active)" :key="c.id" :label="c.name" :value="c.id"/></el-select></el-form-item></el-form><template #footer><el-button @click="visible=false">取消</el-button><el-button type="primary" :disabled="form.username.length<3||form.password.length<12||!form.displayName||(form.role==='CUSTOMER'&&!form.customerId)" @click="create">创建用户</el-button></template></el-dialog>
</template>
