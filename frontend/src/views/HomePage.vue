<template>
  <div class="home-page">
    <div class="content-area">
      <div class="top-bar">
        <div class="sort-tabs">
          <button :class="{ active: sort === 'popular' }" @click="switchSort('popular')">🔥 最热</button>
          <button :class="{ active: sort === 'latest' }" @click="switchSort('latest')">🕐 最新</button>
        </div>
        <div class="top-right">
          <select v-model="pageSize" @change="handlePageSizeChange" class="size-select">
            <option :value="5">每页 5 条</option>
            <option :value="10">每页 10 条</option>
            <option :value="20">每页 20 条</option>
            <option :value="30">每页 30 条</option>
          </select>
          <div class="view-toggle">
            <button :class="{ active: viewMode === 'list' }" @click="viewMode = 'list'" title="列表视图">☰</button>
            <button :class="{ active: viewMode === 'waterfall' }" @click="viewMode = 'waterfall'" title="瀑布流视图">⊞</button>
          </div>
        </div>
      </div>
      <SubCategoryBar :items="subTags" :activeId="subTagId" @select="onSubTagSelect" />
      <ArticleList :articles="articles" :loading="loading" :total="total" :current-page="page" :page-size="pageSize" :mode="viewMode" empty-text="还没有文章，快去写一篇吧！" @page-change="handlePageChange" />
    </div>
    <aside class="sidebar"><CategoryCloud /><HotArticles /><RecommendedAuthors /></aside>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'; import { useRoute } from 'vue-router'
import { getArticleList, type ArticleVO } from '../api/article'
import { getTagCloud, getTagList, type Tag, type TagCloudItem } from '../api/tag'
import { getTagsByCategoryId } from '../utils/categoryTagMapping'
import ArticleList from '../components/article/ArticleList.vue'
import SubCategoryBar from '../components/article/SubCategoryBar.vue'
import CategoryCloud from '../components/sidebar/CategoryCloud.vue'
import HotArticles from '../components/sidebar/HotArticles.vue'
import RecommendedAuthors from '../components/sidebar/RecommendedAuthors.vue'

const route = useRoute(); const articles = ref<ArticleVO[]>([])
const sort = ref('popular'); const page = ref(1); const total = ref(0); const loading = ref(true)
const viewMode = ref<'list' | 'waterfall'>('list')
const pageSize = ref(10)
// 二级标签筛选器数据（对应选中一级分类下的标签列表，含文章数量）
interface SubTagItem { id: number; name: string; articleCount?: number }
const subTags = ref<SubTagItem[]>([])
const subTagId = ref<number | undefined>()

async function loadSubTags(categoryId: number) {
  try {
    const tagNames = getTagsByCategoryId(categoryId)
    const [tagListRes, tagCloudRes] = await Promise.all([getTagList(), getTagCloud()])
    const allTags: Tag[] = tagListRes.data || []
    const cloudItems: TagCloudItem[] = tagCloudRes.data || []
    const countMap = new Map<number, number>()
    for (const item of cloudItems) {
      countMap.set(item.id, item.articleCount)
    }
    subTags.value = allTags
      .filter(t => tagNames.includes(t.name))
      .map(t => ({ id: t.id, name: t.name, articleCount: countMap.get(t.id) ?? 0 }))
    // 默认选中第一个有内容的二级标签（articleCount > 0）
    const firstWithContent = subTags.value.find(t => (t.articleCount ?? 0) > 0)
    subTagId.value = firstWithContent ? firstWithContent.id : undefined
  } catch { subTags.value = []; subTagId.value = undefined }
}
async function fetchArticles() { loading.value = true; try { const catId = route.query.catId ? Number(route.query.catId) : undefined; if (catId && subTagId.value === undefined) { await loadSubTags(catId) } else if (!catId) { subTags.value = []; subTagId.value = undefined } const res = await getArticleList({ page: page.value, size: pageSize.value, sort: sort.value, tagId: subTagId.value !== undefined ? subTagId.value : undefined, categoryId: subTagId.value !== undefined ? undefined : catId }); articles.value = res.data.records || []; total.value = res.data.total || 0 } finally { loading.value = false } }
function onSubTagSelect(id?: number) { subTagId.value = id; page.value = 1; fetchArticles() }
function switchSort(s: string) { sort.value = s; page.value = 1; fetchArticles() }
function handlePageChange(p: number) { page.value = p; fetchArticles(); window.scrollTo({ top: 0, behavior: 'smooth' }) }
function handlePageSizeChange() { page.value = 1; fetchArticles() }
onMounted(fetchArticles)
watch(() => route.query.catId, (newVal) => { page.value = 1; subTagId.value = undefined; fetchArticles() })
</script>

<style scoped>
.home-page { display: flex; gap: 24px; }
.content-area { flex: 1; min-width: 0; }
.top-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; flex-wrap: wrap; gap: 10px; }
.top-right { display: flex; align-items: center; gap: 10px; }
.sort-tabs { display: flex; gap: 4px; background: var(--card-bg); border-radius: 24px; padding: 4px; border: 1px solid var(--card-border); }
.sort-tabs button { padding: 8px 20px; border-radius: 20px; border: none; background: transparent; color: var(--text-secondary); font-size: 14px; cursor: pointer; transition: all var(--transition-fast); font-weight: 500; }
.sort-tabs button.active { background: var(--primary-gradient); color: #fff; }
.size-select { padding: 8px 12px; border-radius: var(--radius-sm); border: 1.5px solid var(--card-border); background: var(--card-bg); color: var(--text); font-size: 13px; outline: none; cursor: pointer; }
.view-toggle { display: flex; gap: 4px; }
.view-toggle button { width: 38px; height: 38px; border-radius: var(--radius-sm); border: 1.5px solid var(--card-border); background: var(--card-bg); font-size: 18px; cursor: pointer; transition: all var(--transition-fast); color: var(--text-secondary); display: flex; align-items: center; justify-content: center; }
.view-toggle button.active { border-color: var(--primary); color: var(--primary); background: var(--primary-bg); }
.view-toggle button:hover { border-color: var(--primary-light); }
.sidebar { width: 290px; flex-shrink: 0; }
@media (max-width: 960px) { .home-page { flex-direction: column; } .sidebar { width: 100%; } }
</style>