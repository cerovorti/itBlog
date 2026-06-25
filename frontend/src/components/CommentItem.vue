<template>
  <div class="comment-node">
    <UserAvatar :username="comment.username" :src="comment.userAvatar" :size="34" />
    <div class="node-body">
      <div class="node-header"><span class="node-user">{{ comment.username }}</span><span v-if="comment.replyToUsername" class="node-reply">@{{ comment.replyToUsername }}</span><span class="node-time">{{ formatDate(comment.createTime) }}</span></div>
      <div class="node-content">{{ comment.content }}</div>
      <div class="node-actions"><button v-if="currentUserId" @click="showReply = !showReply" class="na-btn">💬 回复</button><button v-if="canDelete" @click="handleDelete" class="na-btn del">删除</button></div>
      <div v-if="showReply" class="reply-box"><textarea v-model="replyContent" rows="2" placeholder="写下你的回复..." class="reply-textarea"></textarea><div class="reply-actions"><button @click="showReply = false" class="na-btn">取消</button><button @click="submitReply" :disabled="!replyContent.trim() || replying" class="na-btn primary">{{ replying ? '发送中' : '回复' }}</button></div></div>
      <div v-if="hasChildren" class="children-section">
        <div v-if="!showChildren" class="expand-bar" @click="loadReplies">—— 查看回复 ——</div>
        <template v-else><div class="expand-bar" @click="showChildren = false">—— 收起回复 ——</div><div class="children" v-if="replies.length"><CommentItem v-for="r in replies" :key="r.id" :comment="r" :currentUserId="currentUserId" @deleted="onChildDeleted" /></div></template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'; import { ElMessage, ElMessageBox } from 'element-plus'
import { addComment, deleteComment, getReplies, type CommentVO } from '../api/comment'; import UserAvatar from './common/UserAvatar.vue'
const props = defineProps<{ comment: CommentVO; currentUserId?: number; isAdmin?: boolean }>(); const emit = defineEmits<{ (e: 'deleted'): void }>()
const showReply = ref(false); const replyContent = ref(''); const replying = ref(false)
const showChildren = ref(false); const replies = ref<CommentVO[]>([])
const hasChildren = computed(() => props.comment.children === null || props.comment.children === undefined || (Array.isArray(props.comment.children) && props.comment.children.length >= 0))
const canDelete = computed(() => props.currentUserId === props.comment.userId || props.isAdmin === true)
async function loadReplies() { try { replies.value = (await getReplies(props.comment.id)).data || []; showChildren.value = true } catch {} }
async function submitReply() { if (!replyContent.value.trim()) return; replying.value = true; try { await addComment({ articleId: props.comment.articleId, parentId: props.comment.id, replyToUserId: props.comment.userId, content: replyContent.value }); replyContent.value = ''; showReply.value = false; ElMessage.success('回复成功'); loadReplies() } catch {} finally { replying.value = false } }
async function handleDelete() {
  try {
    await ElMessageBox.confirm('确定删除该评论？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
  } catch { return }
  try { await deleteComment(props.comment.id); ElMessage.success('已删除'); emit('deleted') } catch {}
}
function onChildDeleted() { loadReplies() }
function formatDate(s: string) { if (!s) return ''; return new Date(s).toLocaleString('zh-CN') }
</script>

<style scoped>
.comment-node { display: flex; gap: 12px; padding: 14px 0; border-bottom: 1px solid var(--card-border); }
.node-body { flex: 1; min-width: 0; }
.node-header { display: flex; gap: 8px; align-items: center; font-size: 13px; flex-wrap: wrap; }
.node-user { font-weight: 700; color: var(--text); }
.node-reply { color: var(--primary); font-size: 12px; }
.node-time { color: var(--text-muted); font-size: 12px; }
.node-content { margin-top: 6px; font-size: 14px; line-height: 1.7; color: var(--text); word-break: break-word; }
.node-actions { margin-top: 6px; display: flex; gap: 8px; }
.na-btn { background: none; border: none; font-size: 12px; color: var(--text-muted); cursor: pointer; padding: 2px 6px; border-radius: 4px; transition: all var(--transition-fast); }
.na-btn:hover { color: var(--primary); background: var(--primary-bg); }
.na-btn.del:hover { color: #E74C3C; }
.na-btn.primary { color: var(--primary); font-weight: 600; }
.reply-box { margin-top: 10px; }
.reply-textarea { width: 100%; padding: 10px; border: 1.5px solid var(--card-border); border-radius: var(--radius-sm); font-size: 13px; outline: none; resize: vertical; background: var(--input-bg); color: var(--text); }
.reply-textarea:focus { border-color: var(--primary-light); }
.reply-actions { display: flex; gap: 8px; justify-content: flex-end; margin-top: 6px; }
.children-section { margin-top: 6px; }
.expand-bar { font-size: 12px; color: var(--primary); cursor: pointer; padding: 8px 0; user-select: none; font-weight: 500; }
.expand-bar:hover { opacity: 0.8; }
.children { margin-left: 20px; padding-left: 16px; border-left: 2px solid var(--primary-light); }
</style>
