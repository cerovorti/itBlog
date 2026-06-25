<template>
  <div class="comment-section">
    <h3 class="comment-title">💬 评论 <span class="count">{{ total }}</span></h3>
    <div class="comment-input-box" v-if="currentUserId">
      <div class="input-row"><textarea v-model="newComment" placeholder="写下你的评论..." rows="3" class="cmt-textarea"></textarea></div>
      <div class="input-actions"><button @click="submitComment" :disabled="!newComment.trim() || submitting" class="cmt-submit">{{ submitting ? '发送中...' : '📤 发表评论' }}</button></div>
    </div>
    <div v-else class="login-hint"><router-link to="/login">登录</router-link> 后参与评论</div>
    <el-skeleton v-if="loading" :rows="4" animated style="margin-top:16px" />
    <el-empty v-else-if="comments.length === 0" description="暂无评论，来发表第一条吧" />
    <template v-else>
      <CommentItem v-for="c in comments" :key="c.id" :comment="c" :currentUserId="currentUserId" :isAdmin="isAdmin" @deleted="fetchComments" />
      <div class="load-more" v-if="hasMore"><button @click="loadMore" :disabled="loadingMore" class="cmt-more">{{ loadingMore ? '加载中...' : '加载更多评论' }}</button></div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'; import { ElMessage } from 'element-plus'
import { getTopComments, addComment, type CommentVO } from '../api/comment'; import CommentItem from './CommentItem.vue'
const props = defineProps<{ articleId: number; currentUserId?: number; isAdmin?: boolean }>()
const comments = ref<CommentVO[]>([]); const newComment = ref('')
const loading = ref(true); const loadingMore = ref(false); const submitting = ref(false)
const page = ref(1); const total = ref(0); const hasMore = ref(false)
async function fetchComments() { loading.value = true; page.value = 1; try { const r = await getTopComments(props.articleId, 1); comments.value = r.data.records || []; total.value = r.data.total || 0; hasMore.value = r.data.total > 10 } finally { loading.value = false } }
async function loadMore() { loadingMore.value = true; page.value++; try { const r = await getTopComments(props.articleId, page.value); comments.value.push(...(r.data.records || [])); hasMore.value = comments.value.length < r.data.total } finally { loadingMore.value = false } }
async function submitComment() { if (!newComment.value.trim()) return; submitting.value = true; try { await addComment({ articleId: props.articleId, content: newComment.value }); newComment.value = ''; ElMessage.success('评论成功'); fetchComments() } catch {} finally { submitting.value = false } }
onMounted(fetchComments); defineExpose({ fetchComments })
</script>

<style scoped>
.comment-section { margin-top: 32px; }
.comment-title { font-family: var(--font-heading); font-size: 18px; font-weight: 700; margin-bottom: 16px; color: var(--text); }
.count { color: var(--primary); }
.comment-input-box { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: var(--radius-md); padding: 14px; margin-bottom: 20px; }
.cmt-textarea { width: 100%; border: none; outline: none; resize: vertical; font-size: 14px; background: transparent; color: var(--text); font-family: var(--font-body); }
.input-actions { display: flex; justify-content: flex-end; margin-top: 8px; }
.cmt-submit { padding: 8px 22px; border-radius: 20px; border: none; background: var(--primary-gradient); color: #fff; font-size: 13px; font-weight: 600; cursor: pointer; transition: all var(--transition-fast); }
.cmt-submit:hover:not(:disabled) { transform: translateY(-1px); box-shadow: var(--shadow-sm); }
.cmt-submit:disabled { opacity: 0.5; cursor: not-allowed; }
.login-hint { text-align: center; padding: 20px; color: var(--text-secondary); font-size: 14px; }
.login-hint a { color: var(--primary); font-weight: 600; }
.load-more { text-align: center; padding: 12px; }
.cmt-more { padding: 8px 24px; border-radius: 20px; border: 1.5px solid var(--card-border); background: transparent; color: var(--text-secondary); font-size: 13px; cursor: pointer; transition: all var(--transition-fast); }
.cmt-more:hover { border-color: var(--primary); color: var(--primary); }
</style>
