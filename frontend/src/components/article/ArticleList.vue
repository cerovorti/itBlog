<template>
  <div class="article-list" :class="{ 'is-waterfall': mode === 'waterfall' }">
    <!-- 骨架屏 -->
    <template v-if="loading">
      <div v-for="i in (mode === 'waterfall' ? 6 : 5)" :key="i" class="skeleton-card" :class="{ 'sk-waterfall': mode === 'waterfall' }">
        <el-skeleton animated>
          <template #template>
            <div :class="mode === 'waterfall' ? 'sk-wf' : 'sk-row'">
              <el-skeleton-item v-if="mode !== 'waterfall'" variant="image" style="width:180px;height:120px" />
              <el-skeleton-item v-else variant="image" style="width:100%;height:160px" />
              <div class="sk-info" :style="mode === 'waterfall' ? 'padding:14px 16px' : ''">
                <el-skeleton-item variant="text" style="width:60%;height:22px" />
                <el-skeleton-item variant="text" style="width:90%" />
                <el-skeleton-item variant="text" style="width:40%" />
              </div>
            </div>
          </template>
        </el-skeleton>
      </div>
    </template>

    <!-- 空状态 -->
    <el-empty v-else-if="articles.length === 0" :description="emptyText">
      <template #image>
        <span style="font-size:64px;">📭</span>
      </template>
    </el-empty>

    <!-- 卡片列表 -->
    <template v-else>
      <div :class="mode === 'waterfall' ? 'waterfall-grid' : 'list-stack'">
        <ArticleCard v-for="a in articles" :key="a.id" :article="a" :mode="mode" />
      </div>
    </template>

    <!-- 分页 -->
    <div class="pagination" v-if="total > pageSize && !loading">
      <el-pagination
        background layout="prev, pager, next"
        :total="total" :page-size="pageSize"
        :current-page="currentPage"
        @current-change="$emit('pageChange', $event)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import ArticleCard from '../ArticleCard.vue'
import type { ArticleVO } from '../../api/article'

defineProps<{
  articles: ArticleVO[]
  loading: boolean
  total: number
  currentPage: number
  pageSize?: number
  emptyText?: string
  mode: 'list' | 'waterfall'
}>()

defineEmits<{ (e: 'pageChange', page: number): void }>()
</script>

<style scoped>
.list-stack { /* default column */ }
.waterfall-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.pagination { margin-top: 24px; display: flex; justify-content: center; }

.skeleton-card {
  background: var(--card-bg);
  border-radius: var(--radius-md);
  border: 1px solid var(--card-border);
  padding: 20px;
  margin-bottom: 12px;
}
.sk-waterfall { padding: 0; }
.sk-row { display: flex; gap: 16px; }
.sk-info { flex: 1; display: flex; flex-direction: column; gap: 10px; }
</style>
