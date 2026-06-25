<template>
  <div class="sidebar-card glass">
    <h4 class="card-header">📁 分类</h4>
    <div class="cat-cloud" v-if="!loading">
      <router-link to="/" class="cat-tag all-tag" :class="{ active: !curCatId }">
        <span class="cat-emoji">📋</span>全部
      </router-link>
      <router-link
        v-for="cat in dedupCategories" :key="cat.id"
        :to="`/?catId=${cat.id}`"
        class="cat-tag"
        :class="{ active: curCatId === cat.id }"
        :style="{ borderColor: catColors[cat.id % catColors.length] }"
      >
        <span class="cat-emoji">{{ catIcons[cat.id % catIcons.length] }}</span>
        <span class="cat-name">{{ cat.name }}</span>
        <span class="cat-num" v-if="(cat.articleCount ?? 0) >= 0">{{ cat.articleCount ?? 0 }}</span>
      </router-link>
      <div v-if="dedupCategories.length === 0" class="empty">暂无分类</div>
    </div>
    <el-skeleton v-else :rows="2" animated />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getCategoryList, type Category } from '../../api/category'

const route = useRoute()
const categories = ref<Category[]>([]); const loading = ref(true)
const catIcons = ['📚', '💻', '🎨', '📊', '⚙️', '🌐', '🔬', '📱', '🗄️', '🛠️']
const catColors = [
  '#8B9DC3', '#7CB9A8', '#C4A882', '#B39BC0', '#8CC4B6',
  '#A8C0D8', '#C8B6A0', '#9BB8C8', '#B8A8C4', '#A0C8B4'
]

async function fetchCategories() {
  loading.value = true
  try { categories.value = (await getCategoryList()).data || [] } finally { loading.value = false }
}

// 页面重新可见时刷新（发布/撤回文章后切回首页）
function onVisibilityChange() {
  if (document.visibilityState === 'visible') fetchCategories()
}

onMounted(() => {
  fetchCategories()
  document.addEventListener('visibilitychange', onVisibilityChange)
})
onUnmounted(() => {
  document.removeEventListener('visibilitychange', onVisibilityChange)
})

// 路由 query 变化（如切换分类）也触发刷新
watch(() => route.query, () => fetchCategories())

// 按 (name, parentId) 去重，保留最小 ID 的记录
const dedupCategories = computed(() => {
  const map = new Map<string, Category>()
  for (const cat of categories.value) {
    const key = `${cat.name}|${cat.parentId ?? 0}`
    const exist = map.get(key)
    if (!exist || cat.id < exist.id) {
      map.set(key, cat)
    }
  }
  return Array.from(map.values())
})

const curCatId = computed(() => {
  const v = route.query.catId
  return v ? Number(v) : null
})
</script>

<style scoped>
.sidebar-card { background: var(--glass-bg); backdrop-filter: var(--glass-blur); border: 1px solid var(--glass-border); border-radius: var(--radius-md); padding: 16px 14px; margin-bottom: 16px; }
.card-header { font-family: var(--font-heading); font-size: 14px; font-weight: 700; margin: 0 0 12px; color: var(--text); letter-spacing: 0.3px; }
.cat-cloud { display: flex; flex-wrap: wrap; gap: 7px; }
.cat-tag {
  display: inline-flex; align-items: center; gap: 4px;
  padding: 5px 11px; border-radius: 14px;
  font-size: 12.5px; font-weight: 500; text-decoration: none;
  color: var(--text-secondary); background: var(--card-bg);
  border: 1.5px solid var(--card-border);
  transition: all 0.2s ease;
  white-space: nowrap; line-height: 1.4;
}
.cat-tag:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  color: var(--text);
}
.cat-tag.active {
  background: var(--card-bg-hover);
  color: var(--text);
  font-weight: 700;
  border-width: 1.5px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}
.cat-tag.active .cat-num {
  background: var(--text-secondary);
  color: #fff;
}
.all-tag { font-weight: 600; }
.cat-emoji { font-size: 13px; flex-shrink: 0; }
.cat-name { flex-shrink: 1; min-width: 0; }
.cat-num {
  font-size: 10.5px; background: var(--bg); color: var(--text-muted);
  padding: 1px 6px; border-radius: 8px; font-weight: 600;
  min-width: 16px; text-align: center; line-height: 1.6;
  flex-shrink: 0;
}
.empty { font-size: 13px; color: var(--text-muted); }
</style>
