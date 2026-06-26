<template>
  <header class="app-header glass">
    <div class="header-inner">
      <router-link to="/" class="logo">
        <span class="logo-icon">🖥️</span>
        <span class="logo-text">IT<span class="logo-accent">Blog</span></span>
      </router-link>
      <nav class="nav-links">
        <router-link to="/" class="nav-item" exact-active-class="active"><span class="nav-icon">🏠</span> 首页</router-link>
        <router-link to="/editor" v-if="isLoggedIn" class="nav-item" active-class="active"><span class="nav-icon">✍️</span> 写文章</router-link>
      </nav>
      <div class="search-box" @mouseleave="showHistory = false">
        <input
          v-model="searchKeyword"
          class="search-input"
          placeholder="🔍  搜索文章..."
          @keydown.enter="handleSearch"
          @focus="onSearchFocus"
          @blur="onSearchBlur"
        />
        <Transition name="menu-fade">
          <div v-show="showHistory && isLoggedIn && historyList.length > 0" class="search-history glass">
            <div class="history-header">
              <span>历史搜索</span>
              <button @mousedown.prevent="onClearHistory">清空</button>
            </div>
            <div v-for="h in historyList" :key="h.id" class="history-item">
              <span @mousedown.prevent="onHistoryClick(h.keyword)">{{ h.keyword }}</span>
              <button class="history-del" @mousedown.prevent="onDeleteHistoryItem(h.id)">✕</button>
            </div>
          </div>
        </Transition>
      </div>
      <div class="user-area">
        <button class="theme-toggle" @click="toggleDark" :title="isDark ? '切换日间模式' : '切换夜间模式'">
          {{ isDark ? '☀️' : '🌙' }}
        </button>
        <template v-if="isLoggedIn">
          <div class="avatar-dropdown" @mouseenter="showMenu = true" @mouseleave="showMenu = false">
            <div class="avatar-wrapper" @click="$router.push('/profile')">
              <UserAvatar :username="displayName" :src="userAvatar" :size="34" />
            </div>
            <Transition name="menu-fade">
              <div v-show="showMenu" class="dropdown-menu glass">
                <div class="menu-user-info">
                  <UserAvatar :username="displayName" :src="userAvatar" :size="40" />
                  <div>
                    <div class="menu-username">{{ displayName }}</div>
                    <div class="menu-role">{{ isAdminUser ? '⚡ 管理员' : '普通用户' }}</div>
                  </div>
                </div>
                <div class="menu-divider"></div>
                <a class="menu-item" @click="goToMyProfile">👤 我的主页</a>
                <router-link v-if="isAdminUser" to="/admin" class="menu-item" @click="showMenu = false">⚙️ 管理后台</router-link>
                <div class="menu-divider"></div>
                <button @click="handleLogout" class="menu-item menu-logout">🚪 退出登录</button>
              </div>
            </Transition>
          </div>
        </template>
        <template v-else>
          <router-link to="/login" class="btn-login">登录</router-link>
          <router-link to="/register" class="btn-register">注册</router-link>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import UserAvatar from './common/UserAvatar.vue'
import { isDark, toggleTheme } from '../utils/theme'
import { recordSearchHistory, getSearchHistory, clearSearchHistory, deleteSearchHistoryItem } from '../api/search'

const router = useRouter()
const userStore = useUserStore()
const searchKeyword = ref('')
const showMenu = ref(false)
const showHistory = ref(false)
const historyList = ref<{ id: number; keyword: string }[]>([])
const historyLoading = ref(false)

const isLoggedIn = computed(() => userStore.isLoggedIn())
const isAdminUser = computed(() => userStore.isAdmin())
const displayName = computed(() => userStore.user?.username || '用户')
const userAvatar = computed(() => userStore.user?.avatar || null)

function toggleDark() {
  toggleTheme()
}

async function handleSearch() {
  const kw = searchKeyword.value.trim()
  if (kw) {
    router.push(`/search?q=${encodeURIComponent(kw)}`)
    if (isLoggedIn.value) {
      recordSearchHistory(kw).catch(() => {})
    }
    showHistory.value = false
  }
}

async function fetchSearchHistory() {
  if (!isLoggedIn.value) return
  historyLoading.value = true
  try {
    const res = await getSearchHistory()
    historyList.value = res.data || []
  } catch { historyList.value = [] }
  finally { historyLoading.value = false }
}

function onSearchFocus(e: FocusEvent) {
  if (isLoggedIn.value) {
    fetchSearchHistory()
    showHistory.value = true
  }
  // 有内容时全选
  if (searchKeyword.value) {
    (e.target as HTMLInputElement).select()
  }
}

function onSearchBlur() {
  setTimeout(() => { showHistory.value = false }, 200)
}

function onHistoryClick(kw: string) {
  searchKeyword.value = kw
  showHistory.value = false
  handleSearch()
}

async function onClearHistory() {
  await clearSearchHistory()
  historyList.value = []
  showHistory.value = false
}

async function onDeleteHistoryItem(id: number) {
  await deleteSearchHistoryItem(id)
  fetchSearchHistory()
}
function goToMyProfile() {
  showMenu.value = false
  router.push('/profile')
}
function handleLogout() {
  showMenu.value = false
  searchKeyword.value = ''
  userStore.logout()
  router.push('/')
}
</script>

<style scoped>
.app-header { position: sticky; top: 0; z-index: 100; border-bottom: 1px solid var(--glass-border); }
.header-inner { max-width: 1240px; margin: 0 auto; display: flex; align-items: center; padding: 0 20px; height: 60px; gap: 20px; }

.logo { display: flex; align-items: center; gap: 8px; text-decoration: none; flex-shrink: 0; }
.logo-icon { font-size: 24px; }
.logo-text { font-family: var(--font-heading); font-size: 20px; font-weight: 800; color: var(--text); }
.logo-accent { background: var(--primary-gradient); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; }

.nav-links { display: flex; gap: 4px; flex-shrink: 0; }
.nav-item { padding: 8px 16px; border-radius: 20px; font-size: 14px; color: var(--text-secondary); text-decoration: none; transition: all var(--transition-fast); font-weight: 500; }
.nav-item:hover { color: var(--primary); background: var(--primary-bg); }
.nav-item.active { color: #fff; background: var(--primary-gradient); }
.nav-icon { font-size: 14px; }

.search-box { flex: 1; max-width: 280px; position: relative; }
.search-input { width: 100%; padding: 9px 16px; border: 2px solid var(--card-border); border-radius: 24px; font-size: 14px; outline: none; background: var(--input-bg); color: var(--text); transition: all var(--transition-fast); }
.search-input::placeholder { color: var(--text-muted); }
.search-input:focus { border-color: var(--primary-light); box-shadow: 0 0 0 3px var(--input-focus-shadow); }

.user-area { margin-left: auto; display: flex; align-items: center; gap: 12px; flex-shrink: 0; }

.avatar-dropdown { position: relative; }
.avatar-wrapper { cursor: pointer; transition: transform var(--transition-fast); display: inline-block; }
.avatar-wrapper:hover { transform: scale(1.08); }

.dropdown-menu {
  position: absolute; top: 46px; right: -8px; width: 200px;
  background: var(--card-bg); border: 1px solid var(--card-border);
  border-radius: var(--radius-md); padding: 8px; box-shadow: var(--shadow-lg); z-index: 200;
}
.menu-user-info { display: flex; align-items: center; gap: 10px; padding: 8px; }
.menu-username { font-size: 14px; font-weight: 700; color: var(--text); }
.menu-role { font-size: 12px; color: var(--text-muted); margin-top: 2px; }
.menu-divider { height: 1px; background: var(--card-border); margin: 6px 0; }
.menu-item { display: block; width: 100%; padding: 10px 12px; border: none; border-radius: var(--radius-sm); background: none; font-size: 14px; color: var(--text-secondary); text-decoration: none; cursor: pointer; transition: all var(--transition-fast); text-align: left; font-family: var(--font-body); }
.menu-item:hover { background: var(--primary-bg); color: var(--primary); }
.menu-logout { color: var(--text-muted); }
.menu-logout:hover { color: #E74C3C; background: rgba(231, 76, 60, 0.08); }

.menu-fade-enter-active { transition: opacity 0.15s ease, transform 0.15s ease; }
.menu-fade-leave-active { transition: opacity 0.1s ease, transform 0.1s ease; }
.menu-fade-enter-from { opacity: 0; transform: translateY(-4px); }
.menu-fade-leave-to { opacity: 0; transform: translateY(-4px); }

.btn-login { padding: 8px 20px; border-radius: 20px; color: var(--primary); text-decoration: none; font-size: 14px; font-weight: 600; border: 1.5px solid var(--primary); transition: all var(--transition-fast); }
.btn-login:hover { background: var(--primary-bg); }
.btn-register { padding: 8px 20px; border-radius: 20px; color: #fff; background: var(--primary-gradient); text-decoration: none; font-size: 14px; font-weight: 600; transition: all var(--transition-fast); }
.btn-register:hover { transform: translateY(-1px); box-shadow: var(--shadow-md); }

.theme-toggle {
  width: 36px; height: 36px; border-radius: 50%; border: 1.5px solid var(--card-border);
  background: var(--card-bg); font-size: 16px; cursor: pointer; display: flex;
  align-items: center; justify-content: center; transition: all var(--transition-fast);
}
.theme-toggle:hover { border-color: var(--primary-light); transform: scale(1.1); }

.search-history {
  position: absolute; top: 48px; left: 0; right: 0;
  background: var(--card-bg); border: 1px solid var(--card-border);
  border-radius: var(--radius-md); padding: 8px 0; box-shadow: var(--shadow-lg); z-index: 150;
}
.history-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 6px 12px; font-size: 12px; color: var(--text-muted);
}
.history-header button {
  background: none; border: none; color: var(--text-muted);
  cursor: pointer; font-size: 12px; font-family: var(--font-body);
}
.history-header button:hover { color: #E05555; }
.history-item {
  display: flex; justify-content: space-between; align-items: center;
  padding: 8px 12px; font-size: 13px; color: var(--text);
  cursor: pointer; transition: background var(--transition-fast);
}
.history-item:hover { background: var(--primary-bg); }
.history-item span { flex: 1; }
.history-del {
  background: none; border: none; color: var(--text-muted);
  cursor: pointer; font-size: 11px; padding: 2px 6px;
  border-radius: 50%; transition: all var(--transition-fast);
}
.history-del:hover { color: #E05555; background: rgba(231, 76, 60, 0.1); }

@media (max-width: 768px) { .nav-item { padding: 6px 10px; font-size: 13px; } .search-box { max-width: 160px; } }
</style>
