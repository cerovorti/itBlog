<template>
  <div class="admin-layout">
    <nav class="admin-side glass">
      <div class="admin-logo">⚙️ 管理后台</div>
      <router-link to="/admin" exact-active-class="active" class="admin-nav-item">📊 仪表盘</router-link>
      <router-link to="/admin/articles" active-class="active" class="admin-nav-item">📝 文章审核</router-link>
      <router-link to="/admin/categories" active-class="active" class="admin-nav-item">📁 分类管理</router-link>
      <router-link to="/admin/tags" active-class="active" class="admin-nav-item">🏷️ 标签管理</router-link>
      <router-link to="/admin/users" active-class="active" class="admin-nav-item">👥 用户管理</router-link>
      <div class="admin-nav-div"></div>
      <router-link to="/" class="admin-nav-item back">← 返回前台</router-link>
    </nav>

    <div class="admin-main">
      <div class="breadcrumb">
        <router-link to="/admin">管理后台</router-link>
        <span class="bc-sep">›</span>
        <span class="bc-current">文章审核</span>
        <template v-if="selectedUser">
          <span class="bc-sep">›</span>
          <span class="bc-current">{{ selectedUser }}</span>
        </template>
      </div>

      <!-- 模式切换 -->
      <div class="mode-tabs">
        <button :class="['mode-tab', { active: viewMode === 'pending' }]" @click="switchToPending">⏳ 待审核</button>
        <button :class="['mode-tab', { active: viewMode === 'all' }]" @click="switchToAll">📋 全部文章</button>
      </div>

      <!-- 待审核列表 -->
      <template v-if="viewMode === 'pending'">
        <h2 class="page-title">⏳ 待审核文章 ({{ pendingTotal }})</h2>
        <div v-if="pendingLoading" style="text-align:center;padding:40px;color:var(--text-muted);">加载中...</div>
        <div v-else-if="pendingList.length === 0" style="text-align:center;padding:40px;color:var(--text-muted);">暂无待审核文章 🎉</div>
        <el-table v-else :data="pendingList" style="width:100%">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="title" label="标题" min-width="200">
            <template #default="{ row }">
              <router-link :to="`/article/${row.id}`" style="color:var(--text);text-decoration:none;">{{ row.title }}</router-link>
            </template>
          </el-table-column>
          <el-table-column prop="authorName" label="作者" width="120" />
          <el-table-column label="分类" width="120">
            <template #default="{ row }">{{ row.categoryName }}</template>
          </el-table-column>
          <el-table-column label="类型" width="110">
            <template #default="{ row }">
              <el-tag v-if="row.reviewStatus === 1" type="info" size="small">修改版本</el-tag>
              <el-tag v-else type="warning" size="small">新文章</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="提交时间" width="150">
            <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button size="small" type="success" @click="handleApprove(row.id)">✅ 通过</el-button>
              <el-button size="small" type="danger" @click="handleReject(row.id)">❌ 驳回</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-wrap" v-if="pendingTotal > pendingPageSize">
          <el-pagination
            v-model:current-page="pendingPage"
            :page-size="pendingPageSize"
            :total="pendingTotal"
            layout="prev, pager, next"
            @current-change="loadPending" />
        </div>
      </template>

      <!-- 全部文章（按用户分组） -->
      <template v-if="viewMode === 'all' && !selectedUser">
        <h2 class="page-title">📝 文章管理 · 按用户分组</h2>
        <div v-if="loading" style="text-align:center;padding:40px;color:var(--text-muted);">加载中...</div>
        <div v-else-if="userGroups.length === 0" style="text-align:center;padding:40px;color:var(--text-muted);">暂无文章数据</div>
        <div v-else>
          <div v-for="group in userGroups" :key="group.userId" class="user-group-card" @click="selectUserGroup(group)">
            <div class="group-header">
              <div class="group-user">
                <UserAvatar :username="group.username" :size="36" />
                <div>
                  <span class="group-name">{{ group.username }}</span>
                </div>
              </div>
              <div class="group-stats">
                <span class="gstat">📄 {{ group.total }} 篇</span>
                <span class="gstat">✅ {{ group.published }} 已发布</span>
                <span class="gstat">⏳ {{ group.pending }} 待审核</span>
                <span class="gstat">📋 {{ group.drafts }} 草稿</span>
                <span class="gstat-arrow">›</span>
              </div>
            </div>
          </div>
        </div>
      </template>

      <!-- 某用户的文章列表 -->
      <template v-if="viewMode === 'all' && selectedUser">
        <h2 class="page-title">
          <button class="back-btn" @click="selectedUser = null">← 返回用户列表</button>
          {{ selectedUser }} 的文章
        </h2>

        <div v-if="loading" style="text-align:center;padding:40px;color:var(--text-muted);">加载中...</div>
        <el-table v-else :data="userArticles" style="width:100%">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="title" label="标题" min-width="200">
            <template #default="{ row }">
              <router-link :to="`/article/${row.id}`" style="color:var(--text);text-decoration:none;">{{ row.title }}</router-link>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag v-if="row.status === 1 && row.reviewStatus !== 1" type="success" size="small">已发布</el-tag>
              <el-tag v-else-if="row.status === 1 && row.reviewStatus === 1" type="warning" size="small">更新审核中</el-tag>
              <el-tag v-else-if="row.status === 2" type="warning" size="small">待审核</el-tag>
              <el-tag v-else type="info" size="small">草稿</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="viewCount" label="阅读" width="70" />
          <el-table-column prop="likeCount" label="点赞" width="70" />
          <el-table-column prop="commentCount" label="评论" width="70" />
          <el-table-column label="时间" width="120">
            <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="220">
            <template #default="{ row }">
              <el-button v-if="row.status === 2 || row.reviewStatus === 1" size="small" type="success" @click.stop="handleApprove(row.id)">✅ 通过</el-button>
              <el-button v-if="row.status === 2 || row.reviewStatus === 1" size="small" type="danger" @click.stop="handleReject(row.id)">❌ 驳回</el-button>
              <el-button v-if="row.status !== 2 && row.reviewStatus !== 1" size="small" :type="row.status === 1 ? 'warning' : 'success'" @click.stop="toggleStatus(row)">{{ row.status === 1 ? '下架' : '发布' }}</el-button>
              <el-button size="small" type="danger" @click.stop="handleDelete(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminArticles, updateArticleStatus } from '../../api/admin'
import { deleteArticle, getPendingArticles, approveArticle, rejectArticle, type ArticleVO } from '../../api/article'
import UserAvatar from '../../components/common/UserAvatar.vue'

interface UserGroup { userId: number; username: string; total: number; published: number; pending: number; drafts: number }

const viewMode = ref<'pending' | 'all'>('pending')
const loading = ref(true)
const userGroups = ref<UserGroup[]>([])
const selectedUser = ref<string | null>(null)
const userArticles = ref<ArticleVO[]>([])

// 待审核
const pendingLoading = ref(false)
const pendingList = ref<ArticleVO[]>([])
const pendingPage = ref(1)
const pendingPageSize = ref(10)
const pendingTotal = ref(0)

async function switchToPending() {
  viewMode.value = 'pending'
  selectedUser.value = null
  await loadPending()
}

async function switchToAll() {
  viewMode.value = 'all'
  await fetchAll()
}

async function loadPending() {
  pendingLoading.value = true
  try {
    const res = await getPendingArticles(pendingPage.value, pendingPageSize.value)
    pendingList.value = res.data?.records || []
    pendingTotal.value = res.data?.total || 0
  } catch (e: any) {
    ElMessage.error('加载待审核列表失败')
  } finally { pendingLoading.value = false }
}

async function handleApprove(id: number) {
  try {
    await ElMessageBox.confirm('确定审核通过该文章？', '审核确认', { type: 'info' })
    await approveArticle(id)
    ElMessage.success('文章已审核通过')
    if (viewMode.value === 'pending') await loadPending()
    else if (selectedUser.value) {
      const group = userGroups.value.find(g => g.username === selectedUser.value)
      if (group) await loadUserArticles(group.userId)
    }
  } catch { /* 取消 */ }
}

async function handleReject(id: number) {
  try {
    await ElMessageBox.confirm('确定驳回该文章？驳回后文章将退回草稿。', '驳回确认', { type: 'warning' })
    await rejectArticle(id)
    ElMessage.success('文章已驳回')
    if (viewMode.value === 'pending') await loadPending()
    else if (selectedUser.value) {
      const group = userGroups.value.find(g => g.username === selectedUser.value)
      if (group) await loadUserArticles(group.userId)
    }
  } catch { /* 取消 */ }
}

async function fetchAll() {
  loading.value = true
  try {
    const res = await getAdminArticles({ page: 1, size: 1000 })
    const articles: ArticleVO[] = res.data?.records || []

    if (articles.length === 0) {
      userGroups.value = []
      return
    }

    const map = new Map<number, { articles: ArticleVO[]; username: string }>()
    for (const a of articles) {
      const name = a.authorName || '用户#' + a.authorId
      if (!map.has(a.authorId)) map.set(a.authorId, { articles: [], username: name })
      map.get(a.authorId)!.articles.push(a)
    }
    userGroups.value = Array.from(map.entries()).map(([uid, g]) => ({
      userId: uid, username: g.username,
      total: g.articles.length,
      published: g.articles.filter(a => a.status === 1).length,
      pending: g.articles.filter(a => a.status === 2).length,
      drafts: g.articles.filter(a => a.status === 0).length,
    }))
  } catch (e: any) {
    console.error('Failed to load admin articles:', e)
    ElMessage.error('加载文章失败')
  } finally { loading.value = false }
}

function selectUserGroup(group: UserGroup) {
  selectedUser.value = group.username
  loadUserArticles(group.userId)
}

async function loadUserArticles(userId: number) {
  loading.value = true
  try {
    const res = await getAdminArticles({ page: 1, size: 500 })
    userArticles.value = (res.data?.records || []).filter((a: ArticleVO) => a.authorId === userId)
  } finally { loading.value = false }
}

async function toggleStatus(row: ArticleVO) {
  await updateArticleStatus(row.id, row.status === 1 ? 0 : 1)
  ElMessage.success('操作成功')
  loadUserArticles(row.authorId)
}
async function handleDelete(id: number) {
  try { await ElMessageBox.confirm('确定删除该文章？', '删除确认', { type: 'warning' }); await deleteArticle(id); ElMessage.success('已删除'); fetchAll() } catch {}
}
function formatDate(s: string) { return s ? new Date(s).toLocaleDateString('zh-CN') : '' }

onMounted(() => {
  loadPending()
  fetchAll()
})
</script>

<style scoped>
.admin-layout { display: flex; gap: 24px; max-width: 1200px; margin: 0 auto; }
.admin-side { width: 220px; flex-shrink: 0; padding: 20px; display: flex; flex-direction: column; gap: 4px; border-radius: var(--radius-lg); position: sticky; top: 80px; align-self: flex-start; height: fit-content; }
.admin-logo { font-weight: 800; font-size: 16px; padding: 0 12px 16px; color: var(--text); }
.admin-nav-item { padding: 10px 14px; border-radius: var(--radius-sm); font-size: 14px; color: var(--text-secondary); text-decoration: none; transition: all var(--transition-fast); }
.admin-nav-item:hover { background: var(--primary-bg); color: var(--primary); }
.admin-nav-item.active { background: var(--primary-gradient); color: #fff; font-weight: 600; }
.admin-nav-item.back { margin-top: 8px; font-size: 13px; opacity: 0.7; }
.admin-nav-div { height: 1px; background: var(--card-border); margin: 8px 0; }
.admin-main { flex: 1; min-width: 0; }
.breadcrumb { font-size: 13px; color: var(--text-muted); margin-bottom: 12px; display: flex; align-items: center; gap: 6px; }
.breadcrumb a { color: var(--text-secondary); text-decoration: none; }
.breadcrumb a:hover { color: var(--primary); }
.bc-sep { color: var(--text-muted); }
.bc-current { color: var(--text); font-weight: 500; }
.page-title { font-size: 22px; font-weight: 800; margin-bottom: 20px; color: var(--text); display: flex; align-items: center; gap: 12px; }
.back-btn { padding: 6px 14px; border-radius: 16px; border: 1.5px solid var(--card-border); background: var(--card-bg); color: var(--text-secondary); font-size: 13px; cursor: pointer; transition: all var(--transition-fast); }
.back-btn:hover { border-color: var(--primary); color: var(--primary); }
.user-group-card { background: var(--card-bg); border: 1px solid var(--card-border); border-radius: var(--radius-md); padding: 16px 20px; margin-bottom: 8px; cursor: pointer; transition: all var(--transition-fast); }
.user-group-card:hover { border-color: var(--primary-light); box-shadow: var(--shadow-sm); transform: translateY(-1px); }
.group-header { display: flex; justify-content: space-between; align-items: center; }
.group-user { display: flex; align-items: center; gap: 12px; }
.group-name { font-size: 15px; font-weight: 700; color: var(--text); }
.group-stats { display: flex; gap: 14px; align-items: center; }
.gstat { font-size: 13px; color: var(--text-secondary); }
.gstat-arrow { font-size: 20px; color: var(--text-muted); }

.mode-tabs { display: flex; gap: 0; margin-bottom: 20px; border: 1.5px solid var(--card-border); border-radius: var(--radius-sm); overflow: hidden; width: fit-content; }
.mode-tab { padding: 8px 24px; border: none; background: var(--card-bg); color: var(--text-secondary); font-size: 14px; cursor: pointer; transition: all var(--transition-fast); font-weight: 500; }
.mode-tab:hover { background: var(--primary-bg); color: var(--primary); }
.mode-tab.active { background: var(--primary-gradient); color: #fff; font-weight: 600; }
.mode-tab + .mode-tab { border-left: 1.5px solid var(--card-border); }

.pagination-wrap { display: flex; justify-content: center; margin-top: 20px; }

@media (max-width: 768px) { .admin-layout { flex-direction: column; } .admin-side { width: 100%; position: static; flex-direction: row; flex-wrap: wrap; gap: 6px; } }
</style>