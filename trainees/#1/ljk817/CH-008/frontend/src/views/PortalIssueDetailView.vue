<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { api, type Issue, type IssueRecord } from '../api'
import { formatTime, severityLabels, statusLabels, statusTag } from '../labels'

const id=useRoute().params.id,issue=ref<Issue>(),records=ref<IssueRecord[]>([]),loading=ref(false)
const form=reactive({content:'',customerVisible:true})
async function load(){loading.value=true;try{const [a,b]=await Promise.all([api.get<Issue>(`/issues/${id}`),api.get<IssueRecord[]>(`/issues/${id}/records`)]);issue.value=a.data;records.value=b.data}finally{loading.value=false}}
async function reply(){try{await api.post(`/issues/${id}/records`,form);form.content='';ElMessage.success('补充信息已提交');await load()}catch(e){ElMessage.error((e as Error).message)}}
onMounted(load)
</script>

<template>
  <template v-if="issue">
    <div class="page-heading"><div><el-button link @click="$router.push('/portal')">← 返回我的问题</el-button><h1 style="margin-top:12px">{{issue.title}}</h1><p>{{issue.issueNo}} · 提交于 {{formatTime(issue.createdAt)}}</p></div><el-tag size="large" :type="statusTag[issue.status]">{{statusLabels[issue.status]}}</el-tag></div>
    <div class="detail-grid" v-loading="loading">
      <div class="panel">
        <h3>问题描述</h3><p style="line-height:1.8;white-space:pre-wrap">{{issue.description}}</p>
        <el-divider/><h3>沟通进展</h3>
        <div v-if="!records.length" class="muted">服务团队受理后，公开回复会显示在这里。</div>
        <div v-for="record in records" :key="record.id" class="timeline-item"><b>{{record.authorRole==='CUSTOMER'?'我':'服务团队 · '+record.operatorName}}</b><span class="muted" style="margin-left:10px;font-size:13px">{{formatTime(record.createdAt)}}</span><p>{{record.content}}</p></div>
        <el-divider/><h3>补充信息</h3><el-input v-model="form.content" type="textarea" :rows="4" placeholder="可以补充问题现象、复现步骤或最新情况，请勿填写密码等敏感信息"/><div style="display:flex;justify-content:flex-end;margin-top:12px"><el-button type="primary" :disabled="!form.content.trim()" @click="reply">提交补充</el-button></div>
      </div>
      <div><div class="panel"><h3>问题信息</h3><el-descriptions :column="1" border><el-descriptions-item label="分类">{{issue.category}}</el-descriptions-item><el-descriptions-item label="紧急程度"><span :class="['severity-dot',issue.severity]"></span>{{severityLabels[issue.severity]}}</el-descriptions-item><el-descriptions-item label="当前状态"><el-tag :type="statusTag[issue.status]">{{statusLabels[issue.status]}}</el-tag></el-descriptions-item><el-descriptions-item label="最近更新">{{formatTime(issue.updatedAt)}}</el-descriptions-item></el-descriptions></div></div>
    </div>
  </template>
</template>
