<template>
  <div class="article-detail" v-if="detail">
    <div class="left-space"></div>
    <article class="article-body">
      <img v-if="detail.article.coverImg" :src="detail.article.coverImg" class="cover-img" alt="封面" />
      <div v-else class="cover-placeholder"><span>📖</span></div>

      <h1 class="article-title">{{ detail.article.title }}</h1>
      <div class="article-meta">
        <UserAvatar :username="detail.article.authorName" :src="detail.article.authorAvatar" :size="28" />
        <router-link :to="`/profile/${detail.article.authorId}`" class="author-name">{{ detail.article.authorName }}</router-link>
        <span class="meta-sep">·</span>
        <span>{{ formatDate(detail.article.createTime) }}</span>
        <span v-if="detail.article.updateTime !== detail.article.createTime" class="edited-badge">编辑于 {{ formatDate(detail.article.updateTime) }}</span>
        <span class="meta-sep">·</span>
        <span class="meta-cat" v-if="detail.article.categoryName">{{ detail.article.categoryName }}</span>
      </div>

      <div class="article-tags" v-if="detail.article.tags?.length">
        <span v-for="(t, i) in detail.article.tags" :key="t" class="a-tag" :style="{ background: tagColors[i % tagColors.length] }">{{ t }}</span>
      </div>

      <div class="article-actions-bar">
        <div class="stats-group"><span class="stat">👁️ {{ detail.article.viewCount }}</span><span class="stat">💬 {{ detail.article.commentCount }}</span></div>
        <div class="action-group">
          <button @click="handleLike" :class="{ active: detail.liked }" class="action-btn">{{ detail.liked ? '❤️' : '🤍' }} {{ detail.article.likeCount }}</button>
          <button @click="handleFavorite" :class="{ active: detail.favorited }" class="action-btn">{{ detail.favorited ? '⭐' : '☆' }}</button>
          <template v-if="isAuthor">
            <button @click="handleEdit" class="action-btn edit-btn">✏️ 编辑</button>
            <button v-if="detail.article.status === 1" @click="handleWithdraw" class="action-btn withdraw-btn" :disabled="withdrawing">{{ withdrawing ? '撤回中...' : '↩ 撤回草稿' }}</button>
            <button @click="handleDelete" class="action-btn delete-btn" :disabled="deleting">{{ deleting ? '删除中...' : '🗑️ 删除' }}</button>
          </template>
        </div>
      </div>

      <div class="article-content markdown-body" ref="contentRef" v-html="renderedHtml" />

      <div class="prev-next-nav">
        <router-link v-if="detail.prevArticle" :to="`/article/${detail.prevArticle.id}`" class="pn-link prev"><small>← 上一篇</small><span>{{ detail.prevArticle.title }}</span></router-link>
        <router-link v-if="detail.nextArticle" :to="`/article/${detail.nextArticle.id}`" class="pn-link next"><small>下一篇 →</small><span>{{ detail.nextArticle.title }}</span></router-link>
      </div>

      <CommentList :articleId="articleId" :currentUserId="userStore.user?.id" :isAdmin="userStore.isAdmin()" />
    </article>

    <aside class="article-toc" v-if="tocFlat.length">
      <h4 class="toc-header">📑 目录</h4>
      <nav class="toc-nav">
        <a v-for="(item, i) in tocFlat" :key="i" class="toc-item"
          :style="{ paddingLeft: (item.level - 1) * 14 + 10 + 'px' }"
          :class="{ active: activeTocIndex === item.headingIndex }"
          @click.prevent="scrollToHeading(item.headingIndex)"
        >{{ item.title }}</a>
      </nav>
    </aside>

    <button v-show="showBackTop" class="btn-back-top" @click="scrollToTop">↑</button>
  </div>
  <div v-else class="skeleton-detail"><el-skeleton :rows="20" animated /></div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getArticleDetail, updateArticle, deleteArticle, type ArticleDetailVO } from '../api/article'
import { toggleLike } from '../api/like'
import { toggleFavorite } from '../api/favorite'
import { useUserStore } from '../stores/user'
import UserAvatar from '../components/common/UserAvatar.vue'
import CommentList from '../components/CommentList.vue'

// Cherry Engine 用于 Markdown → HTML
import CherryEngine from 'cherry-markdown/dist/cherry-markdown.engine.core'
const cherryEngine = new (CherryEngine as any)() as { makeHtml: (md: string) => string }
const route = useRoute(); const router = useRouter(); const userStore = useUserStore()
const articleId = computed(() => Number(route.params.id))
const detail = ref<ArticleDetailVO | null>(null); const showBackTop = ref(false)
const contentRef = ref<HTMLElement | null>(null)
const withdrawing = ref(false)
const deleting = ref(false)
const tagColors = ['var(--tag-1)', 'var(--tag-2)', 'var(--tag-3)', 'var(--tag-4)', 'var(--tag-5)', 'var(--tag-6)']

const isAuthor = computed(() => {
  if (!userStore.isLoggedIn() || !detail.value) return false
  return userStore.user?.id === detail.value.article.authorId
})

const renderedHtml = ref('')

// 扁平化目录（带 headingIndex）
interface FlatToc { level: number; title: string; headingIndex: number }
const tocFlat = ref<FlatToc[]>([])

function flattenToc(toc: ArticleDetailVO['toc'], acc: FlatToc[] = [], headingIdx = { v: 0 }): FlatToc[] {
  if (!toc) return acc
  for (const item of toc) {
    acc.push({ level: item.level, title: item.title, headingIndex: headingIdx.v++ })
    if (item.children?.length) flattenToc(item.children, acc, headingIdx)
  }
  return acc
}

const activeTocIndex = ref(-1)

async function fetchDetail() {
  try { detail.value = (await getArticleDetail(articleId.value)).data } catch {}
  if (detail.value) {
    renderedHtml.value = cherryEngine.makeHtml(detail.value.content)
    await nextTick()
    // 遍历 content 区标题，建立 headingIndex
    tocFlat.value = []
    let hi = 0
    if (detail.value.toc) {
      for (const item of detail.value.toc) {
        tocFlat.value.push({ level: item.level, title: item.title, headingIndex: hi++ })
        if (item.children) {
          for (const child of item.children) {
            tocFlat.value.push({ level: child.level, title: child.title, headingIndex: hi++ })
            if (child.children) {
              for (const grandchild of child.children) {
                tocFlat.value.push({ level: grandchild.level, title: grandchild.title, headingIndex: hi++ })
              }
            }
          }
        }
      }
    }
    setupHeadingObserver()
  }
}

function scrollToHeading(idx: number) {
  const headings = contentRef.value?.querySelectorAll('h1,h2,h3,h4,h5,h6')
  const el = headings?.[idx] as HTMLElement | undefined
  if (el) {
    el.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

let headingObserver: IntersectionObserver | null = null
function setupHeadingObserver() {
  headingObserver?.disconnect()
  headingObserver = new IntersectionObserver((entries) => {
    for (const entry of entries) {
      if (entry.isIntersecting && entry.target instanceof HTMLElement) {
        const hs = contentRef.value?.querySelectorAll('h1,h2,h3,h4,h5,h6')
        if (hs) {
          const idx = Array.from(hs).indexOf(entry.target)
          if (idx >= 0) activeTocIndex.value = idx
        }
      }
    }
  }, { rootMargin: '-60px 0px -70% 0px' })
  nextTick(() => {
    contentRef.value?.querySelectorAll('h1,h2,h3,h4,h5,h6').forEach(h => headingObserver?.observe(h))
  })
}

async function handleLike() { if (!userStore.isLoggedIn()) { ElMessage.warning('请先登录'); return }; await toggleLike(articleId.value); fetchDetail() }
async function handleFavorite() { if (!userStore.isLoggedIn()) { ElMessage.warning('请先登录'); return }; await toggleFavorite(articleId.value); fetchDetail() }
function handleEdit() { router.push(`/editor/${articleId.value}`) }
async function handleDelete() {
  try {
    await ElMessageBox.confirm('确定删除文章？此操作不可撤销！', '警告', { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'error' })
  } catch { return }
  deleting.value = true
  try {
    await deleteArticle(articleId.value)
    ElMessage.success('文章已删除')
    router.push('/')
  } catch { ElMessage.error('删除失败') }
  finally { deleting.value = false }
}
async function handleWithdraw() {
  try {
    await ElMessageBox.confirm('确定将文章撤回为草稿？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
  } catch { return }
  withdrawing.value = true
  try {
    await updateArticle(articleId.value, {
      title: detail.value!.article.title,
      content: detail.value!.content,
      summary: detail.value!.article.summary,
      coverImg: detail.value!.article.coverImg,
      categoryId: detail.value!.article.categoryId,
      status: 0,
    })
    detail.value!.article.status = 0
    ElMessage.success('已撤回为草稿')
  } catch { ElMessage.error('撤回失败') }
  finally { withdrawing.value = false }
}
function scrollToTop() { window.scrollTo({ top: 0, behavior: 'smooth' }) }
function onScroll() { showBackTop.value = window.scrollY > 500 }
function formatDate(s: string) { return s ? new Date(s).toLocaleDateString('zh-CN') : '' }

onMounted(async () => { await fetchDetail(); window.addEventListener('scroll', onScroll) })
onUnmounted(() => { window.removeEventListener('scroll', onScroll); headingObserver?.disconnect() })
</script>

<style scoped>
.article-detail { display: flex; gap: 28px; position: relative; }
.left-space { width: 60px; flex-shrink: 0; }
.article-body { flex: 1; min-width: 0; }
.cover-img { width: 100%; max-height: 360px; object-fit: cover; border-radius: var(--radius-lg); margin-bottom: 24px; }
.cover-placeholder { width: 100%; height: 200px; border-radius: var(--radius-lg); margin-bottom: 24px; background: var(--cover-placeholder); display: flex; align-items: center; justify-content: center; font-size: 56px; }
.article-title { font-family: var(--font-heading); font-size: 30px; font-weight: 800; line-height: 1.4; margin-bottom: 16px; color: var(--text); }
.article-meta { display: flex; gap: 8px; align-items: center; font-size: 13px; color: var(--text-secondary); flex-wrap: wrap; margin-bottom: 14px; }
.author-name { color: var(--primary); text-decoration: none; font-weight: 600; }
.author-name:hover { text-decoration: underline; }
.meta-sep { color: var(--text-muted); }
.edited-badge { font-size: 12px; color: var(--text-muted); background: var(--card-border); padding: 2px 8px; border-radius: 10px; }
.meta-cat { color: var(--primary); background: var(--primary-bg); padding: 2px 10px; border-radius: 10px; font-size: 12px; }
.article-tags { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 16px; }
.a-tag { padding: 3px 12px; border-radius: 12px; font-size: 12px; color: var(--text-secondary); }
.article-actions-bar { display: flex; justify-content: space-between; align-items: center; padding: 12px 16px; background: var(--card-bg); border: 1px solid var(--card-border); border-radius: var(--radius-md); margin-bottom: 24px; }
.stats-group { display: flex; gap: 16px; }
.stat { font-size: 13px; color: var(--text-secondary); }
.action-group { display: flex; gap: 8px; }
.action-btn { padding: 8px 18px; border-radius: 20px; border: 1.5px solid var(--card-border); background: transparent; font-size: 14px; cursor: pointer; transition: all var(--transition-fast); color: var(--text-secondary); }
.action-btn.active { border-color: var(--primary); background: var(--primary-bg); color: var(--primary); }
.action-btn:hover { border-color: var(--primary-light); }
.action-btn.edit-btn { color: var(--primary); border-color: var(--primary-light); }
.action-btn.edit-btn:hover { background: var(--primary-bg); }
.action-btn.withdraw-btn { color: var(--accent-yellow); border-color: var(--card-border); }
.action-btn.withdraw-btn:hover { border-color: var(--accent-yellow); background: rgba(255,230,109,0.1); }
.action-btn.delete-btn { color: #E05555; border-color: var(--card-border); }
.action-btn.delete-btn:hover { border-color: #E05555; background: rgba(231,76,60,0.1); }
.action-btn:disabled { opacity: 0.5; cursor: not-allowed; }
.article-content { font-size: 16px; line-height: 1.9; }
.article-content :deep(pre) { border-radius: var(--radius-md) !important; }
.prev-next-nav { display: flex; gap: 16px; margin-top: 36px; padding-top: 20px; border-top: 1px solid var(--card-border); }
.pn-link { flex: 1; min-width: 0; text-decoration: none; padding: 14px 18px; background: var(--card-bg); border: 1px solid var(--card-border); border-radius: var(--radius-md); transition: all var(--transition-fast); }
.pn-link:hover { border-color: var(--primary-light); box-shadow: var(--shadow-sm); }
.pn-link small { display: block; font-size: 12px; color: var(--text-muted); margin-bottom: 4px; }
.pn-link span { font-size: 14px; color: var(--text); font-weight: 500; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; display: block; }
.pn-link.next { text-align: right; }

/* ===== 目录美化 ===== */
.article-toc {
  width: 240px; flex-shrink: 0;
  position: sticky; top: 80px; align-self: flex-start;
  max-height: calc(100vh - 120px); overflow-y: auto;
  background: var(--card-bg); border: 1px solid var(--card-border);
  border-radius: var(--radius-md); padding: 20px 6px 20px 6px;
}
.toc-header { font-size: 14px; font-weight: 700; margin: 0 0 12px 10px; color: var(--text); }
.toc-nav { display: flex; flex-direction: column; }
.toc-item {
  font-size: 13px; color: var(--text-secondary); text-decoration: none;
  line-height: 2; display: block; border-radius: 6px; padding: 2px 10px;
  transition: all var(--transition-fast); white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
  border-left: 2px solid transparent;
}
.toc-item:hover { color: var(--primary); background: var(--primary-bg); }
.toc-item.active { color: var(--primary); font-weight: 700; background: var(--primary-bg); border-left-color: var(--primary); }
/* 目录滚动条 */
.article-toc::-webkit-scrollbar { width: 4px; }
.article-toc::-webkit-scrollbar-thumb { background: var(--scrollbar-thumb); border-radius: 4px; }

.btn-back-top { position: fixed; bottom: 40px; right: 40px; width: 46px; height: 46px; border-radius: 50%; background: var(--primary-gradient); color: #fff; border: none; font-size: 22px; cursor: pointer; box-shadow: var(--shadow-float); z-index: 50; transition: all var(--transition-normal); }
.btn-back-top:hover { transform: translateY(-3px); }
.skeleton-detail { max-width: 800px; margin: 0 auto; }
@media (max-width: 960px) { .left-space { display: none; } .article-toc { display: none; } }
</style>
