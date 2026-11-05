<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { api, type Customer, type Page } from '../api'
import { formatTime } from '../labels'
import { useAuth } from '../auth'
const auth=useAuth(),canEdit=computed(()=>auth.state.user?.role!=='VIEWER')
const rows = ref<Customer[]>([]), loading = ref(false), visible = ref(false), editingId=ref<number|null>(null)
const form = reactive({ name:'', contactName:'', phone:'', email:'', description:'' })
async function load(){ loading.value=true; try { rows.value=(await api.get<Page<Customer>>('/customers',{params:{size:100}})).data.content } finally{ loading.value=false } }
function openCreate(){editingId.value=null;Object.assign(form,{name:'',contactName:'',phone:'',email:'',description:''});visible.value=true}
function openEdit(row:Customer){editingId.value=row.id;Object.assign(form,{name:row.name,contactName:row.contactName||'',phone:row.phone||'',email:row.email||'',description:row.description||''});visible.value=true}
async function submit(){ try { if(editingId.value) await api.put(`/customers/${editingId.value}`,form); else await api.post('/customers',form); ElMessage.success(editingId.value?'客户更新成功':'客户创建成功'); visible.value=false; await load() } catch(e){ ElMessage.error((e as Error).message) } }
async function toggleActive(row:Customer){try{await api.patch(`/customers/${row.id}/active`,{active:!row.active});ElMessage.success('客户状态已更新');await load()}catch(e){ElMessage.error((e as Error).message)}}
onMounted(load)
</script>

<template>
  <div class="page-heading"><div><h1>客户管理</h1><p>维护客户档案并查看关联问题</p></div><el-button v-if="canEdit" type="primary" @click="openCreate">新增客户</el-button></div>
  <div class="panel">
    <el-table :data="rows" v-loading="loading">
      <el-table-column prop="name" label="客户名称" min-width="190" />
      <el-table-column prop="contactName" label="联系人" width="130" />
      <el-table-column prop="phone" label="联系电话" width="150" />
      <el-table-column prop="email" label="邮箱" min-width="190" />
      <el-table-column label="状态" width="90"><template #default="{row}"><el-tag :type="row.active?'success':'info'">{{row.active?'启用':'停用'}}</el-tag></template></el-table-column>
      <el-table-column label="创建时间" width="175"><template #default="{row}">{{ formatTime(row.createdAt) }}</template></el-table-column>
      <el-table-column label="操作" width="230"><template #default="{row}"><el-button link type="primary" @click="$router.push(`/issues?customerId=${row.id}`)">查看问题</el-button><el-button v-if="canEdit" link @click="openEdit(row)">编辑</el-button><el-button v-if="auth.state.user?.role==='ADMIN'" link :type="row.active?'danger':'success'" @click="toggleActive(row)">{{row.active?'停用':'启用'}}</el-button></template></el-table-column>
    </el-table>
  </div>
  <el-dialog v-model="visible" :title="editingId?'编辑客户':'新增客户'" width="560px">
    <el-form :model="form" label-position="top">
      <el-form-item label="客户名称" required><el-input v-model="form.name" /></el-form-item>
      <div style="display:grid;grid-template-columns:1fr 1fr;gap:16px"><el-form-item label="联系人"><el-input v-model="form.contactName" /></el-form-item><el-form-item label="联系电话"><el-input v-model="form.phone" /></el-form-item></div>
      <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
      <el-form-item label="备注"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
    </el-form>
    <template #footer><el-button @click="visible=false">取消</el-button><el-button type="primary" :disabled="!form.name" @click="submit">保存客户</el-button></template>
  </el-dialog>
</template>
