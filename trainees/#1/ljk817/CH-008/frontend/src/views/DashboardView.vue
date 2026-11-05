<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { api, type Issue, type Page } from '../api'
import { formatTime, severityLabels, statusLabels, statusTag } from '../labels'
const stats = ref({ total: 0, open: 0, processing: 0, critical: 0, closed: 0 })
const recent = ref<Issue[]>([])
const loading = ref(true)
function openIssue(row: Issue) {
  window.location.assign(`/issues/${row.id}`)
}
onMounted(async () => {
  try {
    const [overview, issues] = await Promise.all([
      api.get('/statistics/overview'), api.get<Page<Issue>>('/issues', { params: { size: 6 } })
    ])
    stats.value = overview.data; recent.value = issues.data.content
  } finally { loading.value = false }
})
</script>

<template>
  <div class="page-heading"><div><h1>工作台</h1><p>掌握客户问题的最新进展和处理状态</p></div><el-button type="primary" @click="$router.push('/issues')">查看全部问题</el-button></div>
  <div class="stat-grid">
    <div class="stat-card accent"><small>问题总数</small><strong>{{ stats.total }}</strong></div>
    <div class="stat-card"><small>待处理</small><strong>{{ stats.open }}</strong></div>
    <div class="stat-card"><small>处理中</small><strong>{{ stats.processing }}</strong></div>
    <div class="stat-card"><small>严重问题</small><strong>{{ stats.critical }}</strong></div>
    <div class="stat-card"><small>已关闭</small><strong>{{ stats.closed }}</strong></div>
  </div>
  <div class="panel">
    <div class="page-heading"><div><h1 style="font-size:19px">最近问题</h1><p>按照创建时间显示最新记录</p></div></div>
    <el-table :data="recent" v-loading="loading" @row-click="openIssue">
      <el-table-column prop="issueNo" label="问题编号" width="190" />
      <el-table-column prop="title" label="问题标题" min-width="230" />
      <el-table-column prop="customerName" label="客户" width="150" />
      <el-table-column label="严重程度" width="110"><template #default="{row}"><span :class="['severity-dot',row.severity]"></span>{{ severityLabels[row.severity as keyof typeof severityLabels] }}</template></el-table-column>
      <el-table-column label="状态" width="105"><template #default="{row}"><el-tag :type="statusTag[row.status as keyof typeof statusTag]">{{ statusLabels[row.status as keyof typeof statusLabels] }}</el-tag></template></el-table-column>
      <el-table-column label="创建时间" width="175"><template #default="{row}">{{ formatTime(row.createdAt) }}</template></el-table-column>
    </el-table>
  </div>
</template>
