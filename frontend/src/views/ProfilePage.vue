<template>
  <div class="profile-page" v-if="profile">
    <div class="cover-section">
      <div class="cover-bg"></div>
      <div class="cover-content">
        <UserAvatar :username="profile.username" :src="profile.avatar" :size="80" class="cover-avatar" />
        <h2>{{ profile.username }}</h2>
        <p class="cover-bio">{{ profile.bio || '这个人很懒，什么都没写~' }}</p>
        <div class="cover-skills" v-if="profile.skills">
          <span v-for="(s, i) in profile.skills.split(',').filter(Boolean)" :key="i" class="skill-tag">{{ s.trim() }}</span>
        </div>
        <p class="cover-email">{{ profile.email }}</p>
      </div>
    </div>

    <div class="tab-bar">
      <button v-for="t in tabs" :key="t.key" :class="{ active: activeTab === t.key }" @click="switchTab(t.key)">
        {{ t.icon }} {{ t.label }}
      </button>
    </div>

    <div class="tab-content">
      <template v-if="activeTab === 'published'">
        <el-skeleton v-if="loading" :rows="5" animated />
        <el-empty v-else-if="articles.length === 0" description="还没有发布文章" />
        <ArticleCard v-for="a in articles" :key="a.id" :article="a" mode="list" />
        <div class="pagination" v-if="total > 10"><el-pagination background layout="prev,pager,next" :total="total" :page-size="10" :current-page="page" @current-change="handlePageChange" /></div>
      </template>
      <template v-if="activeTab === 'favorites'">
        <el-skeleton v-if="loading" :rows="5" animated />
        <el-empty v-else-if="favArticles.length === 0" description="还没有收藏文章" />
        <ArticleCard v-for="a in favArticles" :key="'f'+a.id" :article="a" mode="list" />
      </template>
      <template v-if="activeTab === 'drafts' && isSelf">
        <el-skeleton v-if="loading" :rows="5" animated />
        <el-empty v-else-if="drafts.length === 0" description="暂无草稿" />
        <div v-for="d in drafts" :key="'d'+d.id" class="draft-row">
          <div class="draft-left">
            <router-link :to="`/editor/${d.id}`">{{ d.title || '无标题' }}</router-link>
            <span class="draft-datetime">{{ formatDateTime(d.updateTime) }}</span>
          </div>
          <span class="draft-right">
            <span class="draft-date">{{ formatRelativeTime(d.updateTime) }}</span>
            <button class="draft-del" @click="deleteDraft(d)" :disabled="deleting === d.id">🗑️</button>
          </span>
        </div>
      </template>
      <template v-if="activeTab === 'settings' && isSelf">
        <div class="settings-panel">
          <h3>个人设置</h3>
          <div class="setting-item"><label>个性签名</label><input v-model="editBio" placeholder="写一句话..." class="set-input" /></div>
          <div class="setting-item"><label>技术栈</label><input v-model="editSkills" placeholder="用英文逗号分隔，如: Spring Boot, Vue 3, TypeScript" class="set-input" /></div>
          <div class="setting-item"><label>头像</label><div class="avatar-edit"><UserAvatar :username="profile.username" :src="editAvatar" :size="48" /><el-upload :auto-upload="true" :show-file-list="false" :http-request="handleAvatarUpload" accept="image/*"><el-button size="small">上传头像</el-button></el-upload></div></div>
          <button class="save-btn" @click="saveProfile" :disabled="saving">{{ saving ? '保存中...' : '保存设置' }}</button>
          <h3 style="margin-top: 32px;">修改密码</h3>
          <div class="setting-item"><label>原密码</label><input v-model="oldPassword" type="password" placeholder="请输入原密码" class="set-input" /></div>
          <div class="setting-item"><label>新密码</label><input v-model="newPassword" type="password" placeholder="至少6位" class="set-input" /></div>
          <div class="setting-item"><label>确认新密码</label><input v-model="confirmPassword" type="password" placeholder="再次输入新密码" class="set-input" /></div>
          <button class="save-btn" @click="handleChangePassword" :disabled="changingPassword" style="margin-top: 8px;">{{ changingPassword ? '修改中...' : '修改密码' }}</button>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getUserProfile, getMyProfile, updateProfile, changePassword, type UserVO } from '../api/user'
import { getMyArticles, getUserArticles, getDrafts, deleteArticle, type ArticleVO } from '../api/article'
import { getFavoriteList, getUserFavoriteList } from '../api/favorite'
import { uploadAvatar } from '../api/upload'
import { useUserStore } from '../stores/user'
import UserAvatar from '../components/common/UserAvatar.vue'
import ArticleCard from '../components/ArticleCard.vue'

const route = useRoute(); const userStore = useUserStore()
const userId = computed(() => route.params.userId ? Number(route.params.userId) : null)
const isSelf = computed(() => !userId.value || userStore.user?.id === userId.value)
const DARK_KEY = 'demo_album_dark_manual'
const profile = ref<UserVO | null>(null); const activeTab = ref('published')
const loading = ref(true); const articles = ref<ArticleVO[]>([]); const total = ref(0); const page = ref(1)
const favArticles = ref<ArticleVO[]>([]); const drafts = ref<ArticleVO[]>([])
const editBio = ref(''); const editSkills = ref(''); const editAvatar = ref(''); const saving = ref(false); const deleting = ref<number | null>(null)
const oldPassword = ref(''); const newPassword = ref(''); const confirmPassword = ref(''); const changingPassword = ref(false)
const isDarkManual = ref(localStorage.getItem(DARK_KEY) === 'true')

const tabs = computed(() => {
  const list = [{ key: 'published', icon: '📝', label: '文章' }, { key: 'favorites', icon: '⭐', label: '收藏' }]
  if (isSelf.value) { list.push({ key: 'drafts', icon: '📋', label: '草稿' }); list.push({ key: 'settings', icon: '⚙️', label: '设置' }) }
  return list
})

async function loadProfile() {
  try { profile.value = isSelf.value ? (await getMyProfile()).data : (await getUserProfile(userId.value!)).data; editBio.value = profile.value?.bio || ''; editSkills.value = profile.value?.skills || ''; editAvatar.value = profile.value?.avatar || '' } catch {}
}
async function loadPublished() { loading.value = true; try { const r = isSelf.value ? (await getMyArticles(page.value)) : (await getUserArticles(userId.value!, page.value)); articles.value = r.data.records || []; total.value = r.data.total || 0 } finally { loading.value = false } }
async function loadFavorites() { loading.value = true; try { favArticles.value = (isSelf.value ? ((await getFavoriteList()).data.records || []) : ((await getUserFavoriteList(userId.value!)).data.records || [])) } finally { loading.value = false } }
async function loadDrafts() { loading.value = true; try { drafts.value = ((await getDrafts()).data.records || []) } finally { loading.value = false } }
async function switchTab(t: string) { activeTab.value = t; if (t === 'published') loadPublished(); else if (t === 'favorites') loadFavorites(); else if (t === 'drafts') loadDrafts() }
function handlePageChange(p: number) { page.value = p; loadPublished() }
async function handleAvatarUpload(o: any) { try { editAvatar.value = (await uploadAvatar(o.file)).data.url } catch { ElMessage.error('失败') } }
async function saveProfile() { saving.value = true; try { await updateProfile({ avatar: editAvatar.value, bio: editBio.value, skills: editSkills.value }); if (profile.value) { profile.value.avatar = editAvatar.value; profile.value.bio = editBio.value; profile.value.skills = editSkills.value }; ElMessage.success('保存成功') } catch {} finally { saving.value = false } }
async function handleChangePassword() {
  if (!oldPassword.value) { ElMessage.warning('请输入原密码'); return }
  if (!newPassword.value) { ElMessage.warning('请输入新密码'); return }
  if (newPassword.value.length < 6) { ElMessage.warning('新密码长度不能少于6位'); return }
  if (newPassword.value !== confirmPassword.value) { ElMessage.warning('两次输入的新密码不一致'); return }
  changingPassword.value = true
  try {
    await changePassword({ oldPassword: oldPassword.value, newPassword: newPassword.value })
    ElMessage.success('密码修改成功')
    oldPassword.value = ''; newPassword.value = ''; confirmPassword.value = ''
  } catch { ElMessage.error('密码修改失败') }
  finally { changingPassword.value = false }
}
async function deleteDraft(d: ArticleVO) {
  if (!confirm(`确定删除草稿「${d.title || '无标题'}」？`)) return
  deleting.value = d.id
  try { await deleteArticle(d.id); drafts.value = drafts.value.filter(x => x.id !== d.id); ElMessage.success('已删除') }
  catch { ElMessage.error('删除失败') }
  finally { deleting.value = null }
}
function toggleDark(v: boolean) { localStorage.setItem(DARK_KEY, String(v)); document.getElementById('app-container')?.classList.toggle('dark', v) }
function formatDate(s: string) { return s ? new Date(s).toLocaleDateString('zh-CN') : '' }
function formatDateTime(s: string): string {
  if (!s) return ''
  const d = new Date(s)
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日 ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}
function formatRelativeTime(s: string): string {
  if (!s) return ''
  const d = new Date(s)
  const now = Date.now()
  const diff = now - d.getTime()
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
  return `${d.getMonth() + 1}月${d.getDate()}日 ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}
onMounted(async () => { await loadProfile(); loadPublished() })

// 修复：同一组件复用下路由参数变化时重新加载
watch(() => route.params.userId, async (newUserId, oldUserId) => {
  // 同一组件复用下路由参数变化时重新加载（包括他人主页↔自己主页）
  if (newUserId === oldUserId) return
  activeTab.value = 'published'
  await loadProfile()
  loadPublished()
})
</script>

<style scoped>
.cover-section { position: relative; margin: -24px -20px 0; overflow: hidden; }
.cover-bg { height: 180px; background: var(--primary-gradient), var(--accent-purple), var(--accent-blue); background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 25%, var(--accent-pink) 50%, var(--accent-purple) 75%, var(--accent-blue) 100%); background-size: 200% 200%; animation: bgShift 10s ease infinite; }
@keyframes bgShift { 0%,100% { background-position: 0% 50%; } 50% { background-position: 100% 50%; } }
.cover-content { text-align: center; margin-top: -50px; padding-bottom: 24px; }
.cover-avatar { border: 4px solid var(--card-bg); box-shadow: var(--shadow-lg); }
.cover-content h2 { font-size: 24px; font-weight: 800; margin: 12px 0 4px; color: var(--text); }
.cover-bio { font-size: 14px; color: var(--text-secondary); margin: 0; }
.cover-email { font-size: 12px; color: var(--text-muted); margin: 4px 0 0; }
.cover-skills { display: flex; justify-content: center; gap: 8px; flex-wrap: wrap; margin-top: 8px; }
.skill-tag { padding: 3px 12px; border-radius: 12px; font-size: 12px; color: #fff; background: var(--primary-gradient); }

.tab-bar { display: flex; justify-content: center; gap: 4px; padding: 16px 0; background: var(--card-bg); border-bottom: 1px solid var(--card-border); margin: 0 -20px 20px; position: sticky; top: 60px; z-index: 40; }
.tab-bar button { padding: 10px 24px; border: none; border-radius: 20px; background: transparent; font-size: 14px; color: var(--text-secondary); cursor: pointer; transition: all var(--transition-fast); font-weight: 500; }
.tab-bar button.active { background: var(--primary-gradient); color: #fff; }
.tab-bar button:hover:not(.active) { background: var(--primary-bg); }

.tab-content { max-width: 800px; margin: 0 auto; }
.pagination { margin-top: 20px; display: flex; justify-content: center; }
.draft-row { display: flex; justify-content: space-between; align-items: center; padding: 14px 16px; border-bottom: 1px solid var(--card-border); background: var(--card-bg); border-radius: var(--radius-sm); margin-bottom: 6px; }
.draft-left { display: flex; flex-direction: column; gap: 2px; min-width: 0; }
.draft-datetime { font-size: 12px; color: var(--text-muted); }
.draft-row a { color: var(--text); text-decoration: none; font-weight: 500; }
.draft-row a:hover { color: var(--primary); }
.draft-right { display: flex; align-items: center; gap: 10px; flex-shrink: 0; }
.draft-date { font-size: 12px; color: var(--text-muted); }
.draft-del { background: none; border: none; font-size: 16px; cursor: pointer; padding: 2px 6px; border-radius: 4px; transition: all var(--transition-fast); }
.draft-del:hover { background: rgba(255,100,100,0.12); }
.draft-del:disabled { opacity: 0.3; cursor: not-allowed; }

.settings-panel { max-width: 500px; }
.settings-panel h3 { font-size: 18px; font-weight: 700; margin-bottom: 20px; color: var(--text); }
.setting-item { display: flex; flex-direction: column; gap: 6px; margin-bottom: 18px; }
.setting-item label { font-size: 13px; font-weight: 600; color: var(--text-secondary); }
.set-input { padding: 10px 14px; border: 1.5px solid var(--card-border); border-radius: var(--radius-sm); font-size: 14px; outline: none; background: var(--card-bg); color: var(--text); }
.set-input:focus { border-color: var(--primary-light); }
.avatar-edit { display: flex; align-items: center; gap: 12px; }
.save-btn { padding: 10px 28px; border: none; border-radius: var(--radius-md); background: var(--primary-gradient); color: #fff; font-size: 14px; font-weight: 600; cursor: pointer; transition: all var(--transition-fast); }
.save-btn:hover { transform: translateY(-1px); box-shadow: var(--shadow-md); }
.save-btn:disabled { opacity: 0.5; }
</style>