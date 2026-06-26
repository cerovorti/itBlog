<template>
  <div class="comment-node">
    <UserAvatar :username="comment.username" :src="comment.userAvatar" :size="34" />
    <div class="node-body">
      <div class="node-header"><span class="node-user">{{ comment.username }}</span><span v-if="comment.replyToUsername" class="node-reply">@{{ comment.replyToUsername }}</span><span class="node-time">{{ formatDate(comment.createTime) }}</span></div>
      <div class="node-content">{{ comment.content }}</div>
      <div class="node-actions"><button v-if="currentUserId" @click="showReply = !showReply" class="na-btn">💬 回复</button><button v-if="canDelete" @click="handleDelete" class="na-btn del">删除</button></div>
      <div v-if="showReply" class="reply-box"><textarea v-model="replyContent" rows="2" placeholder="写下你的回复..." class="reply-textarea"></textarea><div class="reply-actions"><button @click="showReply = false" class="na-btn">取消</button><button @click="submitReply" :disabled="!replyContent.trim() || replying" class="na-btn primary">{{ replying ? '发送中' : '回复' }}</button></div></div>
      <div v-if="hasChildren" class="children-section">
        <div v-if="!showChildren" class="expand-bar" @click="loadReplies">—— 查看回复 ({{ comment.replyCount }}) ——</div>
        <template v-else>
          <div class="expand-bar" @click="showChildren = false">—— 收起回复 ——</div>
          <div class="children" v-if="replies.length">
            <div v-for="r in replies" :key="r.id" class="reply-flat">
              <UserAvatar :username="r.username" :src="r.userAvatar" :size="28" />
              <div class="reply-flat-body">
                <div class="node-header"><span class="node-user">{{ r.username }}</span><span v-if="r.replyToUsername" class="node-reply">@{{ r.replyToUsername }}</span><span class="node-time">{{ formatDate(r.createTime) }}</span></div>
                <div class="node-content">{{ r.content }}</div>
                <div class="node-actions"><button v-if="currentUserId" @click="replyTarget = r; showSubReply = !showSubReply" class="na-btn">💬 回复</button><button v-if="currentUserId === r.userId || isAdmin" @click="handleDeleteReply(r)" class="na-btn del">删除</button></div>
                <div v-if="showSubReply && replyTarget?.id === r.id" class="reply-box">
                  <textarea v-model="subReplyContent" rows="2" :placeholder="`回复 @${r.username}...`" class="reply-textarea"></textarea>
                  <div class="reply-actions"><button @click="showSubReply = false" class="na-btn">取消</button><button @click="submitSubReply(r)" :disabled="!subReplyContent.trim() || replying" class="na-btn primary">{{ replying ? '发送中' : '回复' }}</button></div>
                </div>
              </div>
            </div>
          </div>
        </template>
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
const showSubReply = ref(false); const subReplyContent = ref(''); const replyTarget = ref<CommentVO | null>(null)
const hasChildren = computed(() => (props.comment.replyCount ?? 0) > 0)
const canDelete = computed(() => props.currentUserId === props.comment.userId || props.isAdmin === true)
async function loadReplies() { replies.value = []; try { replies.value = (await getReplies(props.comment.id)).data || []; showChildren.value = true } catch (e) { ElMessage.error('加载回复失败') } }
async function submitReply() { if (!replyContent.value.trim()) return; replying.value = true; try { await addComment({ articleId: props.comment.articleId, parentId: props.comment.id, replyToUserId: props.comment.userId, content: replyContent.value }); replyContent.value = ''; showReply.value = false; ElMessage.success('回复成功'); props.comment.replyCount = (props.comment.replyCount || 0) + 1; loadReplies() } catch { ElMessage.error('回复失败') } finally { replying.value = false } }
async function handleDelete() {
  try {
    await ElMessageBox.confirm('确定删除该评论？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
  } catch { return }
  try { await deleteComment(props.comment.id); ElMessage.success('已删除'); emit('deleted') } catch {}
}
async function submitSubReply(r: CommentVO) { if (!subReplyContent.value.trim()) return; replying.value = true; try { await addComment({ articleId: props.comment.articleId, parentId: props.comment.id, replyToUserId: r.userId, content: subReplyContent.value }); subReplyContent.value = ''; showSubReply.value = false; replyTarget.value = null; ElMessage.success('回复成功'); props.comment.replyCount = (props.comment.replyCount || 0) + 1; loadReplies() } catch { ElMessage.error('回复失败') } finally { replying.value = false } }
async function handleDeleteReply(r: CommentVO) {
  try {
    await ElMessageBox.confirm('确定删除该回复？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
  } catch { return }
  try { await deleteComment(r.id); ElMessage.success('已删除'); loadReplies() } catch {}
}
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
.reply-flat { display: flex; gap: 10px; padding: 10px 0; border-bottom: 1px dashed var(--card-border); }
.reply-flat:last-child { border-bottom: none; }
.reply-flat-body { flex: 1; min-width: 0; }
</style>
