<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { api,type Category } from '../api'
const rows=ref<Category[]>([]),visible=ref(false),editingId=ref<number|null>(null)
const form=reactive({name:'',description:''})
async function load(){rows.value=(await api.get<Category[]>('/categories')).data}
function openCreate(){editingId.value=null;Object.assign(form,{name:'',description:''});visible.value=true}
function openEdit(row:Category){editingId.value=row.id;Object.assign(form,{name:row.name,description:row.description||''});visible.value=true}
async function save(){try{if(editingId.value)await api.put(`/categories/${editingId.value}`,form);else await api.post('/categories',form);ElMessage.success('分类已保存');visible.value=false;await load()}catch(e){ElMessage.error((e as Error).message)}}
async function toggle(row:Category){try{await api.patch(`/categories/${row.id}/active`,{active:!row.active});ElMessage.success('分类状态已更新');await load()}catch(e){ElMessage.error((e as Error).message)}}
onMounted(load)
</script>

<template>
  <div class="page-heading"><div><h1>问题分类</h1><p>统一维护创建问题时可选择的分类</p></div><el-button type="primary" @click="openCreate">新增分类</el-button></div>
  <div class="panel"><el-table :data="rows"><el-table-column prop="name" label="分类名称" width="220"/><el-table-column prop="description" label="说明" min-width="360"/><el-table-column label="状态" width="100"><template #default="{row}"><el-tag :type="row.active?'success':'info'">{{row.active?'启用':'停用'}}</el-tag></template></el-table-column><el-table-column label="操作" width="180"><template #default="{row}"><el-button link type="primary" @click="openEdit(row)">编辑</el-button><el-button link :type="row.active?'danger':'success'" @click="toggle(row)">{{row.active?'停用':'启用'}}</el-button></template></el-table-column></el-table></div>
  <el-dialog v-model="visible" :title="editingId?'编辑分类':'新增分类'" width="520px"><el-form label-position="top"><el-form-item label="分类名称" required><el-input v-model="form.name"/></el-form-item><el-form-item label="分类说明"><el-input v-model="form.description" type="textarea" :rows="3"/></el-form-item></el-form><template #footer><el-button @click="visible=false">取消</el-button><el-button type="primary" :disabled="!form.name" @click="save">保存</el-button></template></el-dialog>
</template>
