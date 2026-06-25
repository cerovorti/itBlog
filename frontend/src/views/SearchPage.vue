<template>
  <div class="search-page">
    <div class="search-header">
      <h2>🔍 搜索结果：<span class="kw">"{{ keyword }}"</span></h2>
      <p v-if="!loading" class="count">共找到 {{ total }} 篇文章</p>
    </div>

    <el-skeleton v-if="loading" :rows="5" animated />
    <el-empty v-else-if="articles.length === 0" description="没有找到相关文章，换个关键词试试吧" />

    <template v-else>
      <div class="result-list">
        <div v-for="a in articles" :key="a.id" class="result-card" @click="$router.push(`/article/${a.id}`)">
          <h3>{{ a.title }}</h3>
          <p class="r-summary" v-if="a.summary">{{ a.summary }}</p>
          <div class="r-meta">
            <span>{{ a.authorName }}</span>
            <span>{{ a.categoryName }}</span>
            <span>{{ formatDate(a.createTime) }}</span>
            <span>👍 {{ a.likeCount }}</span>
          </div>
        </div>
      </div>
      <div class="pagination" v-if="total > 10">
        <el-pagination background layout="prev,pager,next" :total="total" :page-size="10" :current-page="page" @current-change="handlePageChange" />
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { searchArticles, type ArticleVO } from '../api/article'

const route = useRoute()
const keyword = ref((route.query.q as string) || '')
const page = ref(1); const total = ref(0)
const articles = ref<ArticleVO[]>([]); const loading = ref(true)

async function fetchResults() {
  loading.value = true
  try { const r = await searchArticles(keyword.value, page.value); articles.value = r.data.records || []; total.value = r.data.total || 0 }
  finally { loading.value = false }
}
function formatDate(s: string) { return s ? new Date(s).toLocaleDateString('zh-CN') : '' }
function handlePageChange(p: number) { page.value = p; fetchResults() }

watch(() => route.query.q, (newQ) => {
  keyword.value = (newQ as string) || ''
  page.value = 1
  fetchResults()
}, { immediate: true })
</script>

<style scoped>
.search-page { max-width: 800px; margin: 0 auto; }
.search-header { margin-bottom: 24px; }
.search-header h2 { font-size: 22px; font-weight: 800; }
.kw { color: var(--primary); }
.count { font-size: 13px; color: var(--text-muted); margin-top: 4px; }
.result-list { display: flex; flex-direction: column; gap: 8px; }
.result-card {
  padding: 20px; background: var(--card-bg); border: 1px solid var(--card-border);
  border-radius: var(--radius-md); cursor: pointer; transition: all var(--transition-fast);
}
.result-card:hover { transform: translateY(-2px); box-shadow: var(--shadow-md); }
.result-card h3 { font-size: 17px; font-weight: 700; color: var(--text); margin-bottom: 6px; }
.r-summary { font-size: 13px; color: var(--text-secondary); margin-bottom: 8px; }
.r-meta { display: flex; gap: 14px; font-size: 12px; color: var(--text-muted); }
.pagination { margin-top: 20px; display: flex; justify-content: center; }
</style>