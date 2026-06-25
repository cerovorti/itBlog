<template>
  <div class="md-wrapper" ref="wrapperRef" :style="{ height: (height || 520) + 'px' }" @dragover.prevent @drop.prevent="handleMdDrop">
    <div :id="editorId" class="md-cherry-inner"></div>
    <aside class="md-toc" v-if="tocItems.length">
      <h4>📑 目录</h4>
      <a v-for="(it, i) in tocItems" :key="i" class="toc-a"
        :style="{ paddingLeft: (it.level - 1) * 10 + 6 + 'px' }"
        @click.prevent="goToc(it.headingIndex)">{{ it.title }}</a>
    </aside>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import Cherry from 'cherry-markdown'
import 'cherry-markdown/dist/cherry-markdown.css'
import '../styles/cherry-theme.css'
import { uploadImage } from '../api/upload'
import { isDark } from '../utils/theme'

interface TocItem { level: number; title: string; headingIndex: number }

const props = defineProps<{ modelValue: string; height?: number }>()
const emit = defineEmits<{ 'update:modelValue': [value: string] }>()

const wrapperRef = ref<HTMLElement | null>(null)
const editorId = 'cherry_' + Math.random().toString(36).slice(2, 8)
let cherry: any = null

const content = ref(props.modelValue)
watch(() => props.modelValue, (val) => {
  if (cherry && val !== cherry.getMarkdown()) {
    cherry.setMarkdown(val)
  }
})

// ====== 暗色模式同步：监听全局 isDark ======
function syncCherryTheme() {
  if (!cherry) return
  cherry.setTheme(isDark.value ? 'dark' : 'default')
}
watch(isDark, () => syncCherryTheme())

// ====== 目录解析 ======
const tocItems = computed<TocItem[]>(() => {
  const h: TocItem[] = []
  const ls = content.value.split('\n')
  let headingIdx = 0
  for (const l of ls) {
    const m = l.match(/^(#{1,6})\s+(.+)$/)
    if (m) {
      const t = m[2].replace(/[`*~\[\]!]/g, '').trim()
      if (m[1].length >= 2 && m[1].length <= 4) {
        h.push({ level: m[1].length, title: t, headingIndex: headingIdx })
      }
      headingIdx++
    }
  }
  return h
})

function goToc(headingIndex: number) {
  const container = document.getElementById(editorId)
  const previewer = container?.querySelector('.cherry-previewer') as HTMLElement | null
  if (!previewer) return
  const headings = previewer.querySelectorAll('h1,h2,h3,h4,h5,h6')
  const heading = headings[headingIndex] as HTMLElement | undefined
  if (!heading) return
  previewer.scrollTop = heading.offsetTop - 20
}

async function handleMdDrop(e: DragEvent) {
  const file = e.dataTransfer?.files?.[0]
  if (!file?.type.startsWith('image/')) return
  try {
    const res = await uploadImage(file)
    const url = res.data.url
    if (cherry) {
      cherry.insert(`![](${url})`)
    }
  } catch {
    ElMessage.error('图片上传失败')
  }
}

onMounted(() => {
  nextTick(() => {
    const h = (props.height || 520) - 44
    const pageTheme = isDark.value ? 'dark' : 'default'
    // Cherry 内部 saveThemeToLocal 存的是不带 theme__ 前缀的值（如 'dark'）
    // Cherry 在两个位置（wrapper + previewer）各自独立读取 localStorage，
    // 必须在 new Cherry 之前写入 Cherry 认可的格式消除竞态
    localStorage.setItem('cherry-theme', pageTheme)
    cherry = new Cherry({
      id: editorId,
      value: props.modelValue,
      editor: {
        defaultModel: 'edit&preview',
        height: `${h}px`,
      },
      toolbars: {
        toolbar: [
          'undo', 'redo', '|',
          'header', 'bold', 'italic', 'strikethrough', '|',
          'ol', 'ul', 'checklist', 'quote', '|',
          'code', 'table', 'hr', 'link', 'image', '|',
          'graph', 'formula',
        ],
        showToolbar: true,
      },
      themeSettings: {
        // Cherry 内部会 .replace(/theme__/g, '') 处理，但传不带前缀的值避免歧义
        mainTheme: pageTheme,
      },
      fileUpload: async (file: File, callback: (url: string) => void) => {
        try {
          const res = await uploadImage(file)
          callback(res.data.url)
        } catch {
          ElMessage.error('图片上传失败')
        }
      },
      callback: {
        afterChange: (md: string) => {
          content.value = md
          emit('update:modelValue', md)
        },
        afterInit: () => {
          // afterInit 保底：显式调用 setTheme 确保 wrapper + previewer 均已同步
          if (cherry) {
            cherry.setTheme(isDark.value ? 'dark' : 'default')
          }
        },
      },
      previewer: {
        enablePreviewerBubble: false,
      },
    } as any)
  })
})

onUnmounted(() => {
  cherry?.destroy()
  cherry = null
})

defineExpose({ getMarkdown: () => content.value })
</script>

<style scoped>
.md-wrapper {
  display: flex;
  min-height: 0;
  border: 1px solid var(--card-border);
  border-radius: var(--radius-sm);
  overflow: hidden;
  flex-shrink: 0;
}
.md-cherry-inner {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.md-toc {
  width: 190px; flex-shrink: 0;
  padding: 12px 10px;
  border-left: 1px solid var(--card-border);
  background: var(--card-bg);
  overflow-y: auto;
}
.md-toc h4 { font-size: 13px; font-weight: 700; color: var(--text); margin: 0 0 6px; }
.toc-a {
  display: block; font-size: 12px; color: var(--text-secondary);
  text-decoration: none; padding: 3px 8px; border-radius: 4px;
  line-height: 1.7; cursor: pointer;
  overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
  transition: all var(--transition-fast);
}
.toc-a:hover { color: var(--primary); background: var(--primary-bg); }
</style>
