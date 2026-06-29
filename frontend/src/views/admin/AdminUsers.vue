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
        <span class="bc-current">用户管理</span>
      </div>

      <h2 class="page-title">👥 用户管理</h2>

      <el-table :data="users" style="width:100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="用户" min-width="160">
          <template #default="{ row }">
            <div class="user-cell">
              <UserAvatar :username="row.username" :src="row.avatar" :size="32" />
              <span>{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column label="角色" width="90">
          <template #default="{ row }">
            <el-tag :type="row.role === 1 ? 'danger' : ''" size="small">
              {{ row.role === 1 ? '管理员' : '用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'danger' : 'success'" size="small">
              {{ row.status === 0 ? '已封禁' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="推荐" width="70">
          <template #default="{ row }">
            <el-switch :model-value="row.isRecommended === 1" @change="toggleRecommend(row)" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <div v-if="row.role !== 1" style="display:flex;gap:6px;">
              <el-button size="small"
                :type="row.status === 0 ? 'success' : 'danger'"
                @click="toggleBan(row)">
                {{ row.status === 0 ? '解封' : '封禁' }}
              </el-button>
              <el-button size="small" @click="showResetPassword(row)">重置密码</el-button>
            </div>
            <span v-else style="color:var(--text-muted);font-size:12px;">不可操作</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 封禁时长选择弹窗 -->
      <el-dialog v-model="banDialogVisible" title="封禁用户" width="400px">
        <p style="margin-bottom:12px;color:var(--text);">
          选择对 <strong>{{ banTarget?.username }}</strong> 的封禁时长：
        </p>
        <div class="ban-options">
          <div class="ban-group">
            <span class="ban-group-label">小时</span>
            <el-radio-group v-model="banDuration" size="small">
              <el-radio-button v-for="o in hourOptions" :key="o.value" :value="o.value">{{ o.label }}</el-radio-button>
            </el-radio-group>
          </div>
          <div class="ban-group">
            <span class="ban-group-label">天</span>
            <el-radio-group v-model="banDuration" size="small">
              <el-radio-button v-for="o in dayOptions" :key="o.value" :value="o.value">{{ o.label }}</el-radio-button>
            </el-radio-group>
          </div>
          <div class="ban-group">
            <el-radio v-model="banDuration" :value="-1" size="small">永久封禁</el-radio>
          </div>
        </div>
        <template #footer>
          <el-button @click="banDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmBan">确认封禁</el-button>
        </template>
      </el-dialog>

      <!-- 重置密码弹窗 -->
      <el-dialog v-model="resetPwdVisible" title="重置用户密码" width="400px">
        <p style="margin-bottom:12px;color:var(--text);">
          为 <strong>{{ resetPwdTarget?.username }}</strong> 设置新密码：
        </p>
        <el-input v-model="resetNewPassword" type="password" placeholder="请输入新密码（至少6位）" show-password />
        <template #footer>
          <el-button @click="resetPwdVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmResetPassword" :disabled="!resetNewPassword || resetNewPassword.length < 6">确认重置</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminUsers, setUserRecommend, banUser, adminResetPassword } from '../../api/admin'
import type { UserVO } from '../../api/user'
import UserAvatar from '../../components/common/UserAvatar.vue'

const users = ref<UserVO[]>([])
const loading = ref(true)
const banDialogVisible = ref(false)
const banTarget = ref<UserVO | null>(null)
const banDuration = ref(168)

const resetPwdVisible = ref(false)
const resetPwdTarget = ref<UserVO | null>(null)
const resetNewPassword = ref('')

const hourOptions = [
  { label: '1小时', value: 1 }, { label: '3小时', value: 3 },
  { label: '5小时', value: 5 }, { label: '12小时', value: 12 },
]
const dayOptions = [
  { label: '1天', value: 24 }, { label: '3天', value: 72 },
  { label: '5天', value: 120 }, { label: '7天', value: 168 },
  { label: '30天', value: 720 },
]

async function fetchUsers() {
  loading.value = true
  try { users.value = (await getAdminUsers()).data || [] }
  finally { loading.value = false }
}

async function toggleRecommend(row: UserVO) {
  const v = row.isRecommended !== 1
  try { await setUserRecommend(row.id, v); row.isRecommended = v ? 1 : 0; ElMessage.success(v ? '已推荐' : '已取消') }
  catch { ElMessage.error('操作失败') }
}

function toggleBan(row: UserVO) {
  if (row.status === 0) {
    // 解封
    ElMessageBox.confirm(`确定要解封用户「${row.username}」吗？`, '确认操作', { type: 'warning' })
      .then(async () => {
        await banUser(row.id, false)
        row.status = 1
        row.banUntil = null
        ElMessage.success('解封成功')
      }).catch(() => {})
  } else {
    // 打开封禁弹窗
    banTarget.value = row
    banDuration.value = 168
    banDialogVisible.value = true
  }
}

async function confirmBan() {
  if (!banTarget.value) return
  const row = banTarget.value
  banDialogVisible.value = false
  try {
    if (banDuration.value === -1) {
      await banUser(row.id, true, undefined, true)
      row.status = 0
      row.banUntil = null
      ElMessage.success('已永久封禁')
    } else {
      await banUser(row.id, true, banDuration.value, false)
      row.status = 0
      ElMessage.success('封禁成功')
    }
  } catch { ElMessage.error('操作失败') }
}

function showResetPassword(row: UserVO) {
  resetPwdTarget.value = row
  resetNewPassword.value = ''
  resetPwdVisible.value = true
}

async function confirmResetPassword() {
  if (!resetPwdTarget.value || !resetNewPassword.value || resetNewPassword.value.length < 6) return
  try {
    await adminResetPassword(resetPwdTarget.value.id, resetNewPassword.value)
    ElMessage.success(`已重置「${resetPwdTarget.value.username}」的密码`)
    resetPwdVisible.value = false
  } catch { ElMessage.error('操作失败') }
}

onMounted(fetchUsers)
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

.page-title { font-size: 22px; font-weight: 800; margin-bottom: 20px; color: var(--text); }
.user-cell { display: flex; align-items: center; gap: 10px; }

.ban-options { display: flex; flex-direction: column; gap: 16px; }
.ban-group { display: flex; flex-direction: column; gap: 8px; }
.ban-group-label { font-size: 13px; font-weight: 600; color: var(--text-secondary); }

@media (max-width: 768px) { .admin-layout { flex-direction: column; } .admin-side { width: 100%; position: static; flex-direction: row; flex-wrap: wrap; gap: 6px; } }
</style>