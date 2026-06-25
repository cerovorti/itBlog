<template>
  <div class="sidebar-card glass">
    <h4 class="card-header">🏷️ 标签云</h4>
    <div class="tag-list" v-if="!loading">
      <router-link to="/" class="tag-item all-tag" :class="{ active: !curTagId }">全部</router-link>
      <router-link
        v-for="tag in tags" :key="tag.id"
        :to="`/?tagId=${tag.id}`"
        class="tag-item"
        :class="{ active: curTagId === tag.id }"
        :style="{ fontSize: getSize(tag.articleCount) + 'px', background: tagColors[tag.id % tagColors.length] }"
      >{{ tag.name }} <em>{{ tag.articleCount }}</em></router-link>
      <div v-if="tags.length === 0" class="empty">暂无标签</div>
    </div>
    <el-skeleton v-else :rows="3" animated />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getTagCloud, type TagCloudItem } from '../../api/tag'

const route = useRoute()
const tags = ref<TagCloudItem[]>([]); const loading = ref(true)
const tagColors = ['var(--tag-1)', 'var(--tag-2)', 'var(--tag-3)', 'var(--tag-4)', 'var(--tag-5)', 'var(--tag-6)']
const curTagId = computed(() => {
  const v = route.query.tagId
  return v ? Number(v) : null
})
onMounted(async () => { try { tags.value = (await getTagCloud()).data || [] } finally { loading.value = false } })
function getSize(count: number): number { if (count >= 10) return 16; if (count >= 5) return 14; if (count >= 2) return 13; return 12 }
</script>

<style scoped>
.sidebar-card { background: var(--glass-bg); backdrop-filter: var(--glass-blur); border: 1px solid var(--glass-border); border-radius: var(--radius-md); padding: 18px; margin-bottom: 16px; }
.card-header { font-family: var(--font-heading); font-size: 15px; font-weight: 700; margin: 0 0 14px; color: var(--text); }
.tag-list { display: flex; flex-wrap: wrap; gap: 8px; }
.tag-item { padding: 4px 12px; border-radius: 14px; color: var(--text-secondary); text-decoration: none; font-weight: 500; transition: all var(--transition-fast); }
.tag-item:hover { transform: scale(1.08); }
.tag-item.active { outline: 2px solid var(--primary); outline-offset: 1px; }
.tag-item em { font-style: normal; font-size: 0.85em; color: var(--text-muted); margin-left: 2px; }
.all-tag { background: var(--primary-bg); color: var(--primary); font-weight: 700; font-size: 13px; }
.empty { font-size: 13px; color: var(--text-muted); }
</style>
