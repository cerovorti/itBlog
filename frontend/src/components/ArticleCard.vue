<template>
  <div class="article-card" :class="{ 'card-waterfall': mode === 'waterfall' }" @click="$router.push(`/article/${article.id}`)">
    <div class="card-cover" v-if="article.coverImg"><img :src="article.coverImg" :alt="article.title" /></div>
    <div class="card-cover placeholder" v-else><span>{{ placeholderIcon }}</span></div>
    <div class="card-body">
      <h3 class="card-title">{{ article.title }}</h3>
      <p class="card-summary" v-if="article.summary">{{ article.summary }}</p>
      <div class="card-meta">
        <span class="meta-author"><span class="author-dot" :style="{ background: authorColor }"></span>{{ article.authorName }}</span>
        <span v-if="article.categoryName" class="meta-cat">{{ article.categoryName }}</span>
        <span class="meta-date">{{ formatDate(article.createTime) }}</span>
      </div>
      <div class="card-tags" v-if="article.tags?.length">
        <span v-for="(tag, i) in article.tags.slice(0, 3)" :key="tag" class="card-tag" :style="{ background: tagColors[i % tagColors.length] }">{{ tag }}</span>
      </div>
      <div class="card-stats"><span>👁️ {{ article.viewCount }}</span><span>👍 {{ article.likeCount }}</span><span>💬 {{ article.commentCount }}</span></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { ArticleVO } from '../api/article'
const props = defineProps<{ article: ArticleVO; mode?: 'list' | 'waterfall' }>()
const placeholderIcons = ['📄', '📝', '📰', '📋', '📖']
const placeholderIcon = computed(() => placeholderIcons[props.article.id % placeholderIcons.length])
const tagColors = ['var(--tag-1)', 'var(--tag-2)', 'var(--tag-3)', 'var(--tag-4)', 'var(--tag-5)', 'var(--tag-6)']
const authorColors = ['#FF6B6B', '#4ECDC4', '#A29BFE', '#FD79A8', '#74B9FF', '#55EFC4']
const authorColor = computed(() => authorColors[props.article.authorId % authorColors.length])
function formatDate(s: string) { if (!s) return ''; const d = new Date(s); const now = new Date(); const diff = now.getTime() - d.getTime(); const days = Math.floor(diff / (1000 * 60 * 60 * 24)); if (days === 0) return '今天'; if (days === 1) return '昨天'; if (days < 7) return days + '天前'; if (days < 30) return Math.floor(days / 7) + '周前'; return d.toLocaleDateString('zh-CN') }
</script>

<style scoped>
.article-card { display: flex; gap: 16px; padding: 20px; background: var(--card-bg); border: 1px solid var(--card-border); border-radius: var(--radius-md); cursor: pointer; transition: all var(--transition-normal); margin-bottom: 12px; position: relative; overflow: hidden; }
.article-card::before { content: ''; position: absolute; left: 0; top: 0; bottom: 0; width: 3px; background: var(--primary-gradient); opacity: 0; border-radius: 0 3px 3px 0; transition: opacity var(--transition-normal); }
.article-card:hover { transform: translateY(-2px); box-shadow: var(--shadow-float); border-color: transparent; }
.article-card:hover::before { opacity: 1; }
.card-waterfall { flex-direction: column; padding: 0; }
.card-waterfall .card-cover { width: 100%; height: 160px; border-radius: var(--radius-md) var(--radius-md) 0 0; }
.card-waterfall .card-body { padding: 14px 16px 18px; }
.card-waterfall .card-title { font-size: 16px; }
.card-cover { width: 180px; height: 120px; flex-shrink: 0; border-radius: var(--radius-sm); overflow: hidden; }
.card-cover img { width: 100%; height: 100%; object-fit: cover; transition: transform var(--transition-slow); }
.article-card:hover .card-cover img { transform: scale(1.05); }
.card-cover.placeholder { background: var(--cover-placeholder); display: flex; align-items: center; justify-content: center; font-size: 36px; }
.card-body { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 8px; }
.card-title { font-family: var(--font-heading); font-size: 18px; font-weight: 700; color: var(--text); line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.card-summary { font-size: 13px; color: var(--text-secondary); display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden; }
.card-meta { display: flex; gap: 12px; align-items: center; font-size: 12px; color: var(--text-muted); flex-wrap: wrap; }
.author-dot { display: inline-block; width: 6px; height: 6px; border-radius: 50%; margin-right: 2px; }
.meta-cat { padding: 1px 8px; background: var(--primary-bg); color: var(--primary); border-radius: 10px; font-size: 11px; }
.card-tags { display: flex; gap: 6px; flex-wrap: wrap; }
.card-tag { padding: 2px 10px; border-radius: 10px; font-size: 11px; color: var(--text-secondary); }
.card-stats { display: flex; gap: 16px; font-size: 12px; color: var(--text-muted); }
</style>
