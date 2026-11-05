<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { api, type AssignableUser, type Category, type Customer, type Issue, type IssueRecord, type IssueStatus, type Page, type Severity } from '../api'
import { formatTime, severityLabels, statusLabels, statusTag } from '../labels'
import { useAuth } from '../auth'
const auth=useAuth(),canEdit=computed(()=>auth.state.user?.role!=='VIEWER')
const id=useRoute().params.id, issue=ref<Issue>(), records=ref<IssueRecord[]>([]), status=ref<IssueStatus>('OPEN')
const recordForm=reactive({content:'',customerVisible:false})
const editVisible=ref(false),customers=ref<Customer[]>([]),categories=ref<Category[]>([]),assignees=ref<AssignableUser[]>([])
const editForm=reactive<{customerId:number|string;title:string;description:string;category:string;severity:Severity;assignee:string}>({customerId:'',title:'',description:'',category:'',severity:'MEDIUM',assignee:''})
const selectableCategories=computed(()=>categories.value.filter(item=>item.active||item.name===editForm.category))
const allowedStatusOptions=computed(()=>{
  const map:Record<IssueStatus,IssueStatus[]>={OPEN:['PROCESSING'],PROCESSING:['OPEN','RESOLVED'],RESOLVED:['PROCESSING','CLOSED'],CLOSED:['PROCESSING']}
  return issue.value ? map[issue.value.status] : []
})
async function load(){const [a,b]=await Promise.all([api.get<Issue>(`/issues/${id}`),api.get<IssueRecord[]>(`/issues/${id}/records`)]);issue.value=a.data;status.value=a.data.status;records.value=b.data}
async function openEdit(){if(!customers.value.length){const [c,cat,u]=await Promise.all([api.get<Page<Customer>>('/customers',{params:{size:100}}),api.get<Category[]>('/categories'),api.get<AssignableUser[]>('/users/assignable')]);customers.value=c.data.content;categories.value=cat.data;assignees.value=u.data}Object.assign(editForm,{customerId:issue.value!.customerId,title:issue.value!.title,description:issue.value!.description,category:issue.value!.category,severity:issue.value!.severity,assignee:issue.value!.assignee||''});editVisible.value=true}
async function saveEdit(){try{await api.put(`/issues/${id}`,editForm);ElMessage.success('问题信息已更新');editVisible.value=false;await load()}catch(e){ElMessage.error((e as Error).message)}}
async function changeStatus(){try{await api.patch(`/issues/${id}/status`,{status:status.value,note:''});ElMessage.success('状态已更新');await load()}catch(e){ElMessage.error((e as Error).message)}}
async function addRecord(){try{await api.post(`/issues/${id}/records`,recordForm);Object.assign(recordForm,{content:'',customerVisible:false});ElMessage.success('处理记录已添加');await load()}catch(e){ElMessage.error((e as Error).message)}}
onMounted(load)
</script>

<template>
  <template v-if="issue">
    <div class="page-heading"><div><el-button link @click="$router.back()">← 返回问题列表</el-button><h1 style="margin-top:12px">{{issue.title}}</h1><p>{{issue.issueNo}} · 创建于 {{formatTime(issue.createdAt)}}</p></div><div style="display:flex;gap:12px;align-items:center"><el-button v-if="canEdit" @click="openEdit">编辑问题</el-button><el-tag size="large" :type="statusTag[issue.status]">{{statusLabels[issue.status]}}</el-tag></div></div>
    <div class="detail-grid">
      <div class="panel"><h3>问题描述</h3><p style="line-height:1.8;white-space:pre-wrap">{{issue.description}}</p><el-divider/><h3>处理记录</h3>
        <div v-if="!records.length" class="muted">暂无处理记录</div><div v-for="r in records" :key="r.id" class="timeline-item"><b>{{r.operatorName}}</b><span class="muted" style="margin-left:10px;font-size:13px">{{formatTime(r.createdAt)}}</span><el-tag v-if="r.customerVisible" size="small" type="success" style="margin-left:10px">客户可见</el-tag><el-tag v-else size="small" type="info" style="margin-left:10px">内部备注</el-tag><p>{{r.content}}</p><el-tag v-if="r.newStatus" size="small">{{statusLabels[r.newStatus]}}</el-tag></div>
        <template v-if="canEdit"><el-divider/><el-input v-model="recordForm.content" type="textarea" :rows="3" placeholder="记录排查过程、处理方案或客户反馈"/><div style="display:flex;justify-content:space-between;align-items:center;margin-top:12px"><el-checkbox v-model="recordForm.customerVisible">客户可见回复</el-checkbox><el-button type="primary" :disabled="!recordForm.content" @click="addRecord">添加记录</el-button></div></template>
      </div>
      <div><div class="panel"><h3>问题信息</h3><el-descriptions :column="1" border><el-descriptions-item label="客户">{{issue.customerName}}</el-descriptions-item><el-descriptions-item label="分类">{{issue.category}}</el-descriptions-item><el-descriptions-item label="严重程度"><span :class="['severity-dot',issue.severity]"></span>{{severityLabels[issue.severity]}}</el-descriptions-item><el-descriptions-item label="负责人">{{issue.assignee||'未分配'}}</el-descriptions-item></el-descriptions></div>
        <div v-if="canEdit" class="panel" style="margin-top:18px"><h3>更新状态</h3><el-select v-model="status" style="width:100%;margin-bottom:12px"><el-option :label="statusLabels[issue.status]" :value="issue.status"/><el-option v-for="value in allowedStatusOptions" :key="value" :label="statusLabels[value]" :value="value" /></el-select><el-button type="primary" style="width:100%" :disabled="status===issue.status" @click="changeStatus">保存状态</el-button></div></div>
    </div>
    <el-dialog v-model="editVisible" title="编辑问题" width="620px"><el-form label-position="top"><el-form-item label="客户" required><el-select v-model="editForm.customerId" filterable style="width:100%"><el-option v-for="c in customers" :key="c.id" :label="c.name" :value="c.id"/></el-select></el-form-item><el-form-item label="问题标题" required><el-input v-model="editForm.title"/></el-form-item><el-form-item label="问题描述" required><el-input v-model="editForm.description" type="textarea" :rows="4"/></el-form-item><div style="display:grid;grid-template-columns:1fr 1fr;gap:16px"><el-form-item label="分类"><el-select v-model="editForm.category" style="width:100%"><el-option v-for="item in selectableCategories" :key="item.id" :label="item.name" :value="item.name"/></el-select></el-form-item><el-form-item label="严重程度"><el-select v-model="editForm.severity" style="width:100%"><el-option v-for="(label,key) in severityLabels" :key="key" :label="label" :value="key"/></el-select></el-form-item></div><el-form-item label="负责人"><el-select v-model="editForm.assignee" clearable style="width:100%"><el-option v-for="user in assignees" :key="user.id" :label="`${user.displayName} (${user.username})`" :value="user.username"/></el-select></el-form-item></el-form><template #footer><el-button @click="editVisible=false">取消</el-button><el-button type="primary" @click="saveEdit">保存</el-button></template></el-dialog>
  </template>
</template>
