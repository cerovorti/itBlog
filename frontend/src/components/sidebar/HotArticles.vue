<template>
  <div class="sidebar-card glass">
    <h4 class="card-header">🔥 热门文章</h4>
    <div v-if="!loading">
      <div v-for="(a, idx) in articles" :key="a.id" class="hot-item">
        <span class="hot-rank" :class="'r' + (idx + 1)">{{ ['🥇','🥈','🥉'][idx] || idx + 1 }}</span>
        <router-link :to="`/article/${a.id}`" class="hot-title">{{ a.title }}</router-link>
      </div>
      <div v-if="articles.length === 0" class="empty">暂无文章</div>
    </div>
    <el-skeleton v-else :rows="5" animated />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getArticleList, type ArticleVO } from '../../api/article'

const articles = ref<ArticleVO[]>([])
const loading = ref(true)

onMounted(async () => {
  try { const res = await getArticleList({ page: 1, size: 10, sort: 'popular' }); articles.value = res.data.records || [] }
  finally { loading.value = false }
})
</script>

<style scoped>
.sidebar-card {
  background: var(--glass-bg);
  backdrop-filter: var(--glass-blur);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-md);
  padding: 18px;
  margin-bottom: 16px;
}
.card-header { font-family: var(--font-heading); font-size: 15px; font-weight: 700; margin: 0 0 14px; color: var(--text); }
.hot-item { display: flex; align-items: flex-start; gap: 10px; padding: 8px 0; border-bottom: 1px solid var(--card-border); }
.hot-item:last-child { border-bottom: none; }
.hot-rank { font-size: 16px; flex-shrink: 0; width: 24px; text-align: center; }
.hot-title { font-size: 13px; color: var(--text); text-decoration: none; line-height: 1.5; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.hot-title:hover { color: var(--primary); }
.empty { font-size: 13px; color: var(--text-muted); }
</style>
