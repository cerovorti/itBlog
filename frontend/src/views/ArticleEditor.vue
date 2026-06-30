<template>
  <div class="editor-shell">
    <!-- 工具栏 -->
    <div class="editor-toolbar glass">
      <div class="tb-left">
        <router-link to="/" class="tb-back">← 返回</router-link>
        <span class="tb-divider">|</span>
        <span class="tb-status">{{ editId ? '编辑文章' : '写新文章' }}</span>
        <span v-if="lastSaved" class="tb-saved">💾 {{ lastSaved }}</span>
      </div>
      <div class="tb-right">
        <button class="tb-btn draft" @click="saveDraft" :disabled="saving">{{ saving ? '保存中...' : '💾 存草稿' }}</button>
        <button class="tb-btn publish" @click="publish" :disabled="publishing">{{ publishing ? '提交中...' : '🚀 发布' }}</button>
        <button v-if="editId && detail?.article?.status === 1" class="tb-btn withdraw" @click="withdrawToDraft">↩ 撤回草稿</button>
        <span v-if="editId && (detail?.article?.status === 2 || detail?.article?.reviewStatus === 1)" class="tb-reviewing">⏳ 审核中</span>
      </div>
    </div>

    <!-- 左侧：文章设置卡片（fixed悬浮，不挤占编辑器） -->
    <aside class="settings-card">
      <div class="set-item">
        <label>📁 分类</label>
        <el-select v-model="categoryId" placeholder="选择分类" clearable size="default" class="wide-select" :teleported="true" :popper-options="{ strategy: 'fixed' }">
          <el-option v-for="c in flatCategories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </div>

      <div class="set-item">
        <label>🖼️ 封面</label>
        <div
          class="cover-card"
          :class="{ 'has-bg': coverImg }"
          :style="coverImg ? { backgroundImage: isSvgCover(coverImg) ? coverImg : `url(${coverImg})`, backgroundSize: 'cover', backgroundPosition: 'center' } : {}"
          @click="triggerCoverUpload"
          @dragover.prevent="coverDragOver = true"
          @dragleave.prevent="coverDragOver = false"
          @drop.prevent="handleCoverDrop"
        >
          <img v-if="coverImg && !isSvgCover(coverImg)" :src="coverImg" class="cover-card-img" alt="封面预览" />
          <div v-if="coverImg" class="cover-card-overlay"><span>点击更换</span></div>
          <div v-else class="cover-card-empty"><span>+<br>封面</span></div>
          <input ref="coverFileInput" type="file" accept="image/*" style="display:none" @change="onCoverFileChange" />
        </div>
        <div class="cover-url-row" v-if="!coverImg">
          <input v-model="coverUrlInput" class="cover-url-input" placeholder="或输入URL" @keydown.enter="applyCoverUrl" />
          <button v-if="coverUrlInput" class="cover-url-btn" @click="applyCoverUrl">使用</button>
        </div>
        <div v-if="coverImg" class="cover-actions">
          <button class="btn-remove-cover" @click.stop="removeCover">✕ 移除</button>
        </div>
        <label class="sub-label">🎨 默认封面</label>
        <div class="default-covers">
          <div
            v-for="(gc, i) in defaultCovers" :key="i"
            class="dcover-chip"
            :style="{ backgroundImage: `url(${gc.src})` }"
            @click="coverImg = gc.src"
            :class="{ selected: coverImg === gc.src }"
          >
            <span class="dcover-check" v-if="coverImg === gc.src">✓</span>
          </div>
        </div>
      </div>

      <div class="set-item">
        <label>📝 摘要</label>
        <textarea v-model="summary" class="summary-input" rows="3" placeholder="一段简短描述" />
      </div>

      <div class="set-item">
        <label>🏷️ 标签</label>
        <div class="tag-input-row">
          <span v-for="tag in selectedTags" :key="tag.id" class="stag" @click="removeTag(tag.id)">{{ tag.name }} ×</span>
          <input v-model="tagInput" placeholder="回车添加" @keydown.enter.prevent="addTag" class="tag-inp" />
        </div>
        <div class="tag-hints" v-if="availableTags.length">
          <span v-for="t in availableTags" :key="t.id" class="hint" @click="selectTag(t)">{{ t.name }}</span>
        </div>
      </div>
    </aside>

    <!-- 编辑器主体（全宽，不被卡片挤占） -->
    <div class="editor-main">
      <input v-model="title" class="title-input" placeholder="文章标题..." />
      <MarkdownEditor ref="mdEditorRef" v-model="content" :height="Math.max(editorHeight, 520)" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'; import { ElMessage } from 'element-plus'
import { publishArticle, updateArticle, getArticleDetail, type ArticleDetailVO } from '../api/article'
import { getCategoryList, type Category } from '../api/category'
import { getTagList, createTag, type Tag } from '../api/tag'
import { uploadImage, uploadCover } from '../api/upload'; import { useUserStore } from '../stores/user'
import MarkdownEditor from '../components/MarkdownEditor.vue'

const route = useRoute(); const router = useRouter(); const userStore = useUserStore()
const editId = computed(() => route.params.id ? Number(route.params.id) : null)
const detail = ref<ArticleDetailVO | null>(null)
const title = ref(''); const summary = ref(''); const content = ref('')
const coverImg = ref(''); const coverUrlInput = ref(''); const coverUploading = ref(false)
const coverDragOver = ref(false); const coverFileInput = ref<HTMLInputElement | null>(null)
const categoryId = ref<number | null>(null); const allTags = ref<Tag[]>([]); const selectedTags = ref<Tag[]>([])
const tagInput = ref(''); const flatCategories = ref<Category[]>([])
const saving = ref(false); const publishing = ref(false); const lastSaved = ref('')
const mdEditorRef = ref<any>(null)
const editorHeight = ref(window.innerHeight - 180)
const selectedTagIds = computed(() => selectedTags.value.map(t => t.id))
const availableTags = computed(() => allTags.value.filter(t => !selectedTags.value.find(s => s.id === t.id)))

function makeCoverSvg(color1: string, color2: string): string {
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="800" height="400"><defs><linearGradient id="g" x1="0%" y1="0%" x2="100%" y2="100%"><stop offset="0%" style="stop-color:${color1}"/><stop offset="100%" style="stop-color:${color2}"/></linearGradient></defs><rect width="800" height="400" fill="url(#g)"/><text x="400" y="220" text-anchor="middle" fill="rgba(255,255,255,0.35)" font-size="120" font-weight="bold">IT</text></svg>`
  return 'data:image/svg+xml,' + encodeURIComponent(svg)
}

const defaultCovers = [
  { src: 'https://picsum.photos/seed/techpc/800/400' },
  { src: 'https://picsum.photos/seed/devworkspace/800/400' },
  { src: 'https://picsum.photos/seed/cloudserver/800/400' },
  { src: 'https://picsum.photos/seed/datacenter/800/400' },
  { src: 'https://picsum.photos/seed/programming/800/400' },
  { src: 'https://picsum.photos/seed/keyboard/800/400' },
]

function isSvgCover(v: string): boolean {
  return v.startsWith('data:image/svg+xml,')
}

function onResize() { editorHeight.value = window.innerHeight - 180 }

watch(categoryId, async (c) => { if (c) { const r = await getTagList(c); if (r.data?.length) { allTags.value = r.data; return } } allTags.value = (await getTagList()).data || [] })
let autoTimer: ReturnType<typeof setInterval> | null = null

onMounted(async () => {
  window.addEventListener('resize', onResize)
  const [c, t] = await Promise.all([getCategoryList(), getTagList()])
  // 数据库变更后 busi_category 只保留一级分类（parentId=0），后端将 0 转为 null 返回
  flatCategories.value = ((c.data || []) as Category[]).filter((cat: Category) => !cat.parentId)
  allTags.value = t.data || []
  if (editId.value) {
    const r = await getArticleDetail(editId.value)
    const d = r.data; detail.value = d
    title.value = d.article.title === '无标题' ? '' : d.article.title
    summary.value = d.draftContent ? (d.article.summary || '') : (d.article.summary || '')
    content.value = d.draftContent || d.content
    coverImg.value = d.article.coverImg || ''; categoryId.value = d.article.categoryId
    if (d.article.tags) for (const tn of d.article.tags) {
      const f = allTags.value.find(t => t.name === tn)
      selectedTags.value.push(f || { id: -1, name: tn, createTime: '' } as Tag)
    }
  } else {
    coverImg.value = defaultCovers[0].src
    autoTimer = setInterval(async () => { if (content.value.trim()) await submit(0, true) }, 30000)
  }
})
onUnmounted(() => { window.removeEventListener('resize', onResize); if (autoTimer) clearInterval(autoTimer) })

// 修复：同一组件复用下路由参数变化时重新加载
watch(() => route.params.id, async (newId) => {
  if (autoTimer) { clearInterval(autoTimer); autoTimer = null }
  const editIdNew = newId ? Number(newId) : null
  if (editIdNew) {
    // 切换到编辑另一篇文章
    const r = await getArticleDetail(editIdNew)
    const d = r.data; detail.value = d
    title.value = d.article.title === '无标题' ? '' : d.article.title
    summary.value = d.article.summary || ''
    content.value = d.draftContent || d.content
    coverImg.value = d.article.coverImg || ''
    categoryId.value = d.article.categoryId
    selectedTags.value = []
    if (d.article.tags) {
      for (const tn of d.article.tags) {
        const f = allTags.value.find(t => t.name === tn)
        selectedTags.value.push(f || { id: -1, name: tn, createTime: '' } as Tag)
      }
    }
  } else if (!newId) {
    // 从编辑切换到新建
    detail.value = null
    title.value = ''; summary.value = ''; content.value = ''
    coverImg.value = defaultCovers[0].src; categoryId.value = null
    selectedTags.value = []
    autoTimer = setInterval(async () => { if (content.value.trim()) await submit(0, true) }, 30000)
  }
})

function selectTag(t: Tag) { selectedTags.value.push(t) }
function removeTag(id: number) { selectedTags.value = selectedTags.value.filter(t => t.id !== id) }
async function addTag() { const n = tagInput.value.trim(); if (!n) return; if (selectedTags.value.find(t => t.name === n)) { ElMessage.warning('已存在'); tagInput.value = ''; return }; const ex = allTags.value.find(t => t.name === n); if (ex) { selectedTags.value.push(ex); tagInput.value = ''; return }; try { const r = await createTag(n); allTags.value.push(r.data); selectedTags.value.push(r.data); tagInput.value = '' } catch {} }

function triggerCoverUpload() { coverFileInput.value?.click() }
async function onCoverFileChange(e: Event) { const input = e.target as HTMLInputElement; if (input.files?.[0]) await doCoverUpload(input.files[0]) }
async function handleCoverDrop(e: DragEvent) { coverDragOver.value = false; const file = e.dataTransfer?.files?.[0]; if (file?.type.startsWith('image/')) await doCoverUpload(file) }
async function doCoverUpload(file: File) { coverUploading.value = true; try { coverImg.value = (await uploadCover(file)).data.url; ElMessage.success('上传成功') } catch { ElMessage.error('上传失败') } finally { coverUploading.value = false } }
function applyCoverUrl() { const url = coverUrlInput.value.trim(); if (!url) return; coverImg.value = url; coverUrlInput.value = ''; ElMessage.success('封面URL已设置') }
function removeCover() { coverImg.value = ''; coverUrlInput.value = '' }

function validate(): boolean {
  if (!title.value.trim()) { ElMessage.warning('请输入标题'); return false }
  if (!content.value.trim()) { ElMessage.warning('请输入文章内容'); return false }
  if (!summary.value.trim()) { ElMessage.warning('请输入文章摘要'); return false }
  if (!coverImg.value.trim()) { ElMessage.warning('请上传或选择封面'); return false }
  if (!categoryId.value) { ElMessage.warning('请选择分类'); return false }
  if (selectedTags.value.length === 0) { ElMessage.warning('请至少添加一个标签'); return false }
  return true
}

function validateDraft(): boolean {
  if (!content.value.trim()) { ElMessage.warning('请输入文章内容'); return false }
  return true
}
async function saveDraft() { if (!validateDraft()) return; await submit(0) }
async function publish() { if (!validate()) return; await submit(1) }
async function submit(status: number, silent = false) {
  if (status === 0) saving.value = true; else publishing.value = true
  const titleText = title.value.trim() || '无标题'
  const d = { title: titleText, content: content.value, summary: summary.value, coverImg: coverImg.value, categoryId: categoryId.value || undefined, tagIds: selectedTagIds.value.length ? selectedTagIds.value : undefined, status }
      try { if (editId.value) { await updateArticle(editId.value, d) } else { const r = await publishArticle(d); if (!editId.value) router.replace(`/editor/${r.data.id}`) } if (!silent) ElMessage.success(status === 1 ? '已提交审核，请等待管理员审核' : '草稿已保存'); lastSaved.value = new Date().toLocaleTimeString('zh-CN'); if (status === 1) router.push('/') } catch { if (!silent) ElMessage.error('操作失败') } finally { saving.value = false; publishing.value = false } }
async function withdrawToDraft() {
  await updateArticle(editId.value!, {
    title: title.value,
    content: content.value,
    summary: summary.value,
    coverImg: coverImg.value,
    categoryId: categoryId.value,
    status: 0,
  })
  ElMessage.success('已撤回')
  if (detail.value) detail.value.article.status = 0
}
</script>

<style scoped>
.editor-shell { margin: -24px -20px; }

/* ===== 工具栏 ===== */
.editor-toolbar {
  display: flex; justify-content: space-between; align-items: center;
  padding: 0 24px; height: 56px;
  position: sticky; top: 60px; z-index: 50;
  background: var(--glass-bg); backdrop-filter: var(--glass-blur);
  border-bottom: 1px solid var(--glass-border);
}
.tb-left { display: flex; align-items: center; gap: 12px; font-size: 14px; }
.tb-back { color: var(--text-secondary); text-decoration: none; }
.tb-back:hover { color: var(--primary); }
.tb-divider { color: var(--card-border); }
.tb-status { color: var(--text); font-weight: 600; }
.tb-saved { font-size: 12px; color: var(--text-muted); }
.tb-right { display: flex; gap: 8px; }
.tb-btn { padding: 8px 20px; border-radius: 20px; border: none; font-size: 13px; font-weight: 600; cursor: pointer; transition: all var(--transition-fast); }
.tb-btn.draft { background: var(--card-bg); color: var(--text-secondary); border: 1.5px solid var(--card-border); }
.tb-btn.draft:hover { border-color: var(--primary-light); color: var(--primary); }
.tb-btn.publish { background: var(--primary-gradient); color: #fff; }
.tb-btn.publish:hover { transform: translateY(-1px); box-shadow: var(--shadow-md); }
.tb-btn.withdraw { background: var(--card-bg); color: var(--accent-yellow); border: 1.5px solid var(--card-border); }
:root.dark .tb-btn.withdraw { color: #F0C060; }
.tb-btn:disabled { opacity: 0.6; cursor: not-allowed; }
.tb-reviewing { padding: 8px 16px; border-radius: 20px; font-size: 13px; font-weight: 600; color: var(--accent-yellow); background: rgba(240, 185, 11, 0.1); border: 1.5px solid rgba(240, 185, 11, 0.3); }
:root.dark .tb-reviewing { color: #F0C060; }

/* ===== 左侧设置卡片（fixed悬浮Dock：默认半隐，hover展开） ===== */
.settings-card {
  position: fixed; left: 0; top: 132px; z-index: 55;
  width: 260px;
  max-height: calc(100vh - 170px); overflow-y: auto;
  background: var(--card-bg); border: 1px solid var(--card-border);
  border-radius: 0 var(--radius-md) var(--radius-md) 0;
  padding: 20px 16px;
  display: flex; flex-direction: column; gap: 14px;
  box-shadow: var(--shadow-lg);
  transform: translateX(-218px);
   opacity: 0.75;
   transition: transform 0.35s cubic-bezier(0.4, 0, 0.2, 1), opacity 0.3s ease, box-shadow 0.3s ease;
   /* 右边缘 42px 宽的细条作为Dock触发热区 */
 }
 .settings-card:hover {
   transform: translateX(0);
   opacity: 1;
   box-shadow: 0 8px 40px rgba(0,0,0,0.18);
 }
.settings-card::-webkit-scrollbar { width: 4px; }
.settings-card::-webkit-scrollbar-thumb { background: var(--scrollbar-thumb); border-radius: 4px; }

.set-item { display: flex; flex-direction: column; gap: 5px; }
.set-item label { font-size: 12px; font-weight: 600; color: var(--text-secondary); }
.set-item .sub-label { font-size: 11.5px; font-weight: 600; color: var(--text-secondary); margin-top: 4px; }
.wide-select { width: 100% !important; }

/* 编辑器主体：全宽，不被卡片挤占 */
.editor-main {
  padding: 16px 24px 60px;
  display: flex; flex-direction: column; gap: 12px;
}
.editor-main :deep(.md-wrapper) { flex: 1; min-height: 0; }

.title-input {
  width: 100%; padding: 0 0 6px;
  font-size: 26px; font-weight: 800;
  border: none; outline: none; background: transparent;
  color: var(--text); font-family: var(--font-heading);
  flex-shrink: 0;
}
.title-input::placeholder { color: var(--text-muted); }

/* ===== 封面 ===== */
.cover-card {
  position: relative; height: 110px; border: 2px dashed var(--card-border);
  border-radius: var(--radius-sm); cursor: pointer; overflow: hidden;
  transition: all var(--transition-fast); background: var(--bg);
  display: flex; align-items: center; justify-content: center;
}
.cover-card:hover { border-color: var(--primary-light); }
.cover-card.has-bg { border-style: solid; height: 130px; }
.cover-card-img { width: 100%; height: 100%; object-fit: cover; display: block; }
.cover-card-overlay {
  position: absolute; inset: 0; background: rgba(0,0,0,0.45);
  display: flex; align-items: center; justify-content: center;
  opacity: 0; transition: opacity var(--transition-fast);
  color: #fff; font-size: 13px;
}
.cover-card:hover .cover-card-overlay { opacity: 1; }
.cover-card-empty { text-align: center; color: var(--text-muted); font-size: 14px; line-height: 1.6; }
.cover-url-row { display: flex; gap: 6px; margin-top: 4px; }
.cover-url-input { flex: 1; padding: 6px 10px; border: 1.5px solid var(--card-border); border-radius: var(--radius-sm); font-size: 12px; outline: none; background: var(--card-bg); color: var(--text); }
.cover-url-btn { padding: 6px 12px; border: none; border-radius: var(--radius-sm); background: var(--primary); color: #fff; font-size: 12px; cursor: pointer; }
.cover-actions { margin-top: 4px; }
.btn-remove-cover { padding: 4px 10px; border: none; border-radius: var(--radius-sm); background: transparent; color: var(--text-muted); font-size: 12px; cursor: pointer; }
.btn-remove-cover:hover { background: rgba(255,100,100,0.1); color: #E05555; }

.default-covers { display: grid; grid-template-columns: repeat(4, 1fr); gap: 6px; }
.dcover-chip {
  aspect-ratio: 1.6; border-radius: 4px; cursor: pointer;
  border: 2px solid transparent; transition: all var(--transition-fast);
  background-size: cover; background-position: center;
  display: flex; align-items: center; justify-content: center;
}
.dcover-chip:hover { transform: scale(1.08); }
.dcover-chip.selected { border-color: var(--primary); box-shadow: 0 0 0 2px var(--primary-bg); }
.dcover-check { color: #fff; font-size: 14px; font-weight: 700; text-shadow: 0 1px 3px rgba(0,0,0,0.5); }

.summary-input {
  width: 100%; padding: 8px 10px;
  border: 1.5px solid var(--card-border); border-radius: var(--radius-sm);
  font-size: 12.5px; outline: none; background: var(--card-bg);
  color: var(--text); resize: vertical; font-family: inherit;
}

.tag-input-row { display: flex; flex-wrap: wrap; gap: 5px; align-items: center; border: 1.5px solid var(--card-border); border-radius: var(--radius-sm); padding: 5px 6px; background: var(--card-bg); }
.stag { display: inline-block; padding: 2px 7px; border-radius: 10px; background: var(--primary-bg); color: var(--primary); font-size: 11.5px; cursor: pointer; transition: all var(--transition-fast); }
.stag:hover { background: var(--primary); color: #fff; }
.tag-inp { border: none; outline: none; font-size: 12.5px; flex: 1; min-width: 60px; background: transparent; color: var(--text); }
.tag-hints { display: flex; flex-wrap: wrap; gap: 4px; margin-top: 4px; }
.hint { padding: 2px 8px; border-radius: 10px; background: var(--bg); color: var(--text-secondary); font-size: 11.5px; cursor: pointer; border: 1px solid var(--card-border); transition: all var(--transition-fast); }
.hint:hover { border-color: var(--primary); color: var(--primary); }

@media (max-width: 960px) { .settings-card { display: none; } }
</style>