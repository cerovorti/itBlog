<template>
  <div class="sidebar-card glass">
    <h4 class="card-header">✍️ 推荐作者</h4>
    <div v-if="!loading">
      <div v-for="author in authors" :key="author.id" class="author-item">
        <UserAvatar :username="author.username" :src="author.avatar" :size="40" />
        <div class="author-info">
          <router-link :to="`/profile/${author.id}`" class="author-name">{{ author.username }}</router-link>
          <p class="author-bio">{{ author.bio || '这个人很懒，什么都没写~' }}</p>
          <p class="author-likes" v-if="author.totalLikes">❤️ {{ author.totalLikes }} 获赞</p>
        </div>
      </div>
      <div v-if="authors.length === 0" class="empty">暂无推荐作者</div>
    </div>
    <el-skeleton v-else :rows="3" animated />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getRecommendedAuthors, type UserVO } from '../../api/user'
import UserAvatar from '../common/UserAvatar.vue'

const authors = ref<UserVO[]>([])
const loading = ref(true)

onMounted(async () => {
  try { const res = await getRecommendedAuthors(); authors.value = res.data || [] }
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
.author-item { display: flex; align-items: center; gap: 12px; padding: 10px 0; border-bottom: 1px solid var(--card-border); }
.author-item:last-child { border-bottom: none; }
.author-info { min-width: 0; }
.author-name { font-size: 14px; font-weight: 600; color: var(--text); text-decoration: none; }
.author-name:hover { color: var(--primary); }
.author-bio { font-size: 12px; color: var(--text-muted); margin: 2px 0 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.author-likes { font-size: 11px; color: var(--primary); margin: 2px 0 0; font-weight: 600; }
.empty { font-size: 13px; color: var(--text-muted); }
</style>
