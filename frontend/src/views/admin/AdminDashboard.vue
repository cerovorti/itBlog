<template>
  <div class="admin">
    <nav class="admin-side glass">
      <div class="admin-logo">⚙️ 管理后台</div>
      <router-link to="/admin" exact-active-class="active" class="admin-nav-item">📊 仪表盘</router-link>
      <router-link to="/admin/articles" active-class="active" class="admin-nav-item">📝 文章审核</router-link>
      <router-link to="/admin/categories" active-class="active" class="admin-nav-item">📁 分类管理</router-link>
      <router-link to="/admin/tags" active-class="active" class="admin-nav-item">🏷️ 标签管理</router-link>
      <router-link to="/admin/users" active-class="active" class="admin-nav-item">👥 用户管理</router-link>
      <div class="admin-nav-div"></div>
      <router-link to="/" class="admin-nav-item back">← 返回前台</router-link>
    </nav>
    <div class="admin-main">
      <h2 class="page-title">仪表盘</h2>
      <div class="stat-grid">
        <div class="stat-card" v-for="s in statCards" :key="s.label"><div class="stat-icon">{{ s.icon }}</div><div class="stat-num">{{ s.value }}</div><div class="stat-label">{{ s.label }}</div></div>
      </div>
      <h3 style="margin:28px 0 16px;color:var(--text);">最近文章</h3>
      <div class="recent-table">
        <div class="rt-row rt-head"><span>标题</span><span>作者</span><span>状态</span><span>时间</span></div>
        <div v-for="a in stats.recentArticles" :key="a.id" class="rt-row"><router-link :to="`/article/${a.id}`">{{ a.title }}</router-link><span>{{ a.authorName }}</span><span :class="'st-' + a.status">{{ a.status === 1 ? '已发布' : '草稿' }}</span><span>{{ formatDate(a.createTime) }}</span></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'; import { getAdminDashboard } from '../../api/admin'
const stats = ref({ totalArticles: 0, publishedArticles: 0, draftArticles: 0, totalUsers: 0, recentArticles: [] as any[] })
const statCards = computed(() => [{ icon: '📄', value: stats.value.totalArticles, label: '文章总数' },{ icon: '✅', value: stats.value.publishedArticles, label: '已发布' },{ icon: '📋', value: stats.value.draftArticles, label: '草稿' },{ icon: '👥', value: stats.value.totalUsers, label: '用户数' }])
onMounted(async () => { try { stats.value = (await getAdminDashboard()).data } catch {} })
function formatDate(s: string) { return s ? new Date(s).toLocaleDateString('zh-CN') : '' }
</script>

<style scoped>
.admin { display: flex; gap: 24px; max-width: 1200px; margin: 0 auto; }
.admin-side { width: 220px; flex-shrink: 0; padding: 20px; display: flex; flex-direction: column; gap: 4px; border-radius: var(--radius-lg); position: sticky; top: 80px; align-self: flex-start; height: fit-content; }
.admin-logo { font-weight: 800; font-size: 16px; padding: 0 12px 16px; color: var(--text); }
.admin-nav-item { padding: 10px 14px; border-radius: var(--radius-sm); font-size: 14px; color: var(--text-secondary); text-decoration: none; transition: all var(--transition-fast); }
.admin-nav-item:hover { background: var(--primary-bg); color: var(--primary); }
.admin-nav-item.active { background: var(--primary-gradient); color: #fff; font-weight: 600; }
.admin-nav-item.back { margin-top: 8px; font-size: 13px; opacity: 0.7; }
.admin-nav-div { height: 1px; background: var(--card-border); margin: 8px 0; }
.admin-main { flex: 1; min-width: 0; }
.page-title { font-size: 24px; font-weight: 800; margin-bottom: 24px; color: var(--text); }
.stat-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.stat-card { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: var(--radius-md); padding: 24px; text-align: center; transition: all var(--transition-normal); }
.stat-card:hover { transform: translateY(-2px); box-shadow: var(--shadow-md); }
.stat-icon { font-size: 28px; margin-bottom: 8px; }
.stat-num { font-size: 32px; font-weight: 800; color: var(--primary); }
.stat-label { font-size: 13px; color: var(--text-muted); margin-top: 4px; }
.recent-table { background: var(--card-bg); border-radius: var(--radius-md); border: 1px solid var(--card-border); overflow: hidden; }
.rt-row { display: grid; grid-template-columns: 2fr 1fr 0.8fr 1fr; gap: 12px; padding: 12px 16px; font-size: 13px; align-items: center; }
.rt-row:not(.rt-head) { border-top: 1px solid var(--card-border); }
.rt-head { font-weight: 700; color: var(--text); background: var(--bg-warm); }
.rt-row a { color: var(--text); text-decoration: none; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.rt-row a:hover { color: var(--primary); }
.st-1 { color: var(--accent-teal); font-weight: 500; }
.st-0 { color: var(--text-muted); }
@media (max-width: 768px) { .admin { flex-direction: column; } .admin-side { width: 100%; position: static; flex-direction: row; flex-wrap: wrap; gap: 6px; } .stat-grid { grid-template-columns: repeat(2, 1fr); } }
</style>
