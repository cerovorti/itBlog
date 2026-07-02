<template>
  <div id="app-container">
    <AppHeader />
    <main class="main-content">
      <router-view />
    </main>
    <AppFooter />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import AppHeader from './components/AppHeader.vue'
import AppFooter from './components/AppFooter.vue'

import { useUserStore } from './stores/user'
import { getToken } from './utils/token'
import { getMyProfile } from './api/user'
import { initTheme, toggleTheme } from './utils/theme'

const userStore = useUserStore()

// 暴露给外部（AppHeader 等组件使用）
;(window as any).__toggleTheme = toggleTheme

onMounted(() => {
  initTheme()
  window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', () => {
    if (!localStorage.getItem('it_blog_theme')) {
      initTheme()
    }
  })
  if (getToken() && !userStore.user) {
    getMyProfile().then(res => { userStore.user = res.data }).catch(() => {})
  }
})
</script>

<style>
/* ================================================================
   CSS 变量 —— 浅色主题 (默认)
   ================================================================ */
:root {
  /* 主色调 */
  --primary: #FF6B6B;
  --primary-dark: #EE5A5A;
  --primary-light: #FF8E8E;
  --primary-gradient: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
  --primary-gradient-soft: linear-gradient(135deg, #FF8E8E 0%, #FFA07A 100%);
  --primary-bg: rgba(255, 107, 107, 0.08);
  --primary-bg-hover: rgba(255, 107, 107, 0.15);

  /* 辅助色 */
  --accent-teal: #4ECDC4;
  --accent-yellow: #FFE66D;
  --accent-purple: #A29BFE;
  --accent-pink: #FD79A8;
  --accent-blue: #74B9FF;
  --accent-green: #55EFC4;

  /* 背景层级 */
  --bg: #FFF5F5;
  --bg-warm: #FFF8F6;
  --bg-white: #FFFFFF;
  --card-bg: #FFFFFF;
  --card-bg-hover: #FFFAFA;
  --card-border: #FFE8E4;
  --divider: #F0E8E6;

  /* 文字层级 */
  --text: #2D3436;
  --text-secondary: #636E72;
  --text-muted: #B2BEC3;
  --text-inverse: #FFFFFF;

  /* 输入框 */
  --input-bg: #FFFFFF;
  --input-border: #FFE8E4;
  --input-focus-shadow: rgba(255, 107, 107, 0.15);

  /* 阴影 */
  --shadow-sm: 0 1px 3px rgba(255, 107, 107, 0.06);
  --shadow-md: 0 4px 12px rgba(255, 107, 107, 0.08);
  --shadow-lg: 0 8px 30px rgba(255, 107, 107, 0.12);
  --shadow-float: 0 8px 25px rgba(255, 107, 107, 0.15);

  /* 圆角 */
  --radius-sm: 8px;
  --radius-md: 12px;
  --radius-lg: 16px;
  --radius-xl: 20px;

  /* 毛玻璃 */
  --glass-bg: rgba(255, 255, 255, 0.72);
  --glass-border: rgba(255, 255, 255, 0.6);
  --glass-blur: blur(16px);

  /* 侧边栏 */
  --sidebar-bg: rgba(255, 255, 255, 0.72);
  --sidebar-border: rgba(255, 255, 255, 0.6);

  /* 工具栏/导航 */
  --toolbar-bg: rgba(255, 255, 255, 0.8);
  --toolbar-border: rgba(0, 0, 0, 0.06);

  /* 头像 */
  --avatar-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  /* 封面占位 */
  --cover-placeholder: linear-gradient(135deg, #FFE8E4 0%, #FFF0EC 50%, #FFE0F0 100%);

  /* 认证页背景 */
  --auth-bg: linear-gradient(135deg, #FFE8E4 0%, #FFF0EC 25%, #FFE0F0 50%, #E0F0FF 75%, #E0FFF4 100%);

  /* 标签色板 (不受暗色影响，保持彩色) */
  --tag-1: #FFE0E0;
  --tag-2: #E0F0FF;
  --tag-3: #E0FFE8;
  --tag-4: #FFF3E0;
  --tag-5: #F0E0FF;
  --tag-6: #FFE8F0;

  /* 滚动条 */
  --scrollbar-thumb: #D0C8C6;
  --scrollbar-thumb-hover: #B0A8A6;

  /* 选中 */
  --selection-bg: rgba(255, 107, 107, 0.25);

  /* 字体 */
  --font-heading: 'PingFang SC', 'Microsoft YaHei', 'Noto Sans SC', -apple-system, sans-serif;
  --font-body: 'PingFang SC', 'Microsoft YaHei', -apple-system, BlinkMacSystemFont, sans-serif;
  --font-mono: 'JetBrains Mono', 'Fira Code', 'SF Mono', Consolas, monospace;

  /* 过渡 */
  --transition-fast: 0.15s ease;
  --transition-normal: 0.25s ease;
  --transition-slow: 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

/* ================================================================
   CSS 变量 —— 暗色主题
   ================================================================ */
:root.dark {
  --primary: #F99332;
  --primary-dark: #E07B1A;
  --primary-light: #FFB366;
  --primary-gradient: linear-gradient(135deg, #F99332 0%, #E07B1A 100%);
  --primary-gradient-soft: linear-gradient(135deg, #FFB366 0%, #F99332 100%);
  --primary-bg: rgba(249, 147, 50, 0.10);
  --primary-bg-hover: rgba(249, 147, 50, 0.20);

  /* 辅助色 */
  --accent-teal: #5CE8DB;
  --accent-yellow: #F2D06B;
  --accent-purple: #BAA8FF;
  --accent-pink: #FFA0BE;
  --accent-blue: #8FCCFF;
  --accent-green: #64F0C6;

  /* 背景层级 —— 三层递进 */
  --bg: #1B2233;
  --bg-warm: #202A3D;
  --bg-white: #2B3550;
  --card-bg: #2B3550;
  --card-bg-hover: #343F5E;
  --card-border: #3A4670;
  --divider: #3A4670;

  /* 文字层级 */
  --text: #EBEDF3;
  --text-secondary: #A0ABC4;
  --text-muted: #657090;
  --text-inverse: #121726;

  /* 输入框 */
  --input-bg: #232C41;
  --input-border: #3A4670;
  --input-focus-shadow: rgba(249, 147, 50, 0.20);

  /* 阴影 */
  --shadow-sm: 0 1px 4px rgba(0, 0, 0, 0.25);
  --shadow-md: 0 4px 14px rgba(0, 0, 0, 0.35);
  --shadow-lg: 0 8px 32px rgba(0, 0, 0, 0.45);
  --shadow-float: 0 8px 28px rgba(0, 0, 0, 0.50);

  /* 毛玻璃 */
  --glass-bg: rgba(27, 34, 51, 0.92);
  --glass-border: rgba(255, 255, 255, 0.07);
  --glass-blur: blur(22px);

  /* 侧边栏 */
  --sidebar-bg: rgba(27, 34, 51, 0.92);
  --sidebar-border: rgba(255, 255, 255, 0.07);

  /* 工具栏 */
  --toolbar-bg: rgba(27, 34, 51, 0.95);
  --toolbar-border: rgba(255, 255, 255, 0.05);

  /* 头像 */
  --avatar-shadow: 0 2px 10px rgba(0, 0, 0, 0.40);

  /* 封面占位 */
  --cover-placeholder: linear-gradient(135deg, #2B3550 0%, #3B4070 40%, #2B4A5E 100%);

  /* 认证页背景 */
  --auth-bg: linear-gradient(135deg, #1B2233 0%, #232C41 20%, #2A3550 40%, #213048 60%, #1B2838 80%, #1B2233 100%);

  /* 标签色板（暗色适配） */
  --tag-1: #3D3048;
  --tag-2: #2E3D52;
  --tag-3: #2D4538;
  --tag-4: #3D3528;
  --tag-5: #36304A;
  --tag-6: #422E3C;

  /* 滚动条 */
  --scrollbar-thumb: #3A4670;
  --scrollbar-thumb-hover: #4A5682;

  /* 选中 */
  --selection-bg: rgba(249, 147, 50, 0.22);
}

/* ================================================================
   全局基础
   ================================================================ */
* { margin: 0; padding: 0; box-sizing: border-box; }

body {
  font-family: var(--font-body);
  background: var(--bg);
  color: var(--text);
  transition: background var(--transition-slow), color var(--transition-slow);
  -webkit-font-smoothing: antialiased;
}

#app-container {
  overflow-x: clip;
}

.main-content {
  min-height: calc(100vh - 140px);
  max-width: 1240px;
  margin: 0 auto;
  padding: 24px 20px;
}

/* 路由过渡 */
.page-fade-enter-active, .page-fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}
.page-fade-enter-from { opacity: 0; transform: translateY(8px); }
.page-fade-leave-to { opacity: 0; transform: translateY(-8px); }

/* 毛玻璃 */
.glass {
  background: var(--glass-bg);
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  border: 1px solid var(--glass-border);
}

/* 卡片通用 */
.card {
  background: var(--card-bg);
  border: 1px solid var(--card-border);
  border-radius: var(--radius-md);
}

/* ================================================================
   Element Plus 完整暗色覆盖
   ================================================================ */

/* 按钮 */
.el-button--primary {
  --el-button-bg-color: var(--primary);
  --el-button-border-color: var(--primary);
  --el-button-hover-bg-color: var(--primary-dark);
  --el-button-hover-border-color: var(--primary-dark);
  --el-button-active-bg-color: var(--primary-dark);
}
.el-button--primary:hover { transform: translateY(-1px); box-shadow: var(--shadow-md); }

/* 默认按钮 */
:root.dark .el-button:not(.el-button--primary) {
  --el-button-bg-color: var(--card-bg);
  --el-button-border-color: var(--card-border);
  --el-button-text-color: var(--text);
  --el-button-hover-bg-color: var(--card-bg-hover);
  --el-button-hover-border-color: var(--primary-light);
  --el-button-hover-text-color: var(--primary);
}

/* Radio button */
.el-radio-button__original-radio:checked + .el-radio-button__inner {
  background: var(--primary-gradient) !important;
  border-color: transparent !important;
  color: #fff !important;
}
:root.dark .el-radio-button__inner {
  background: var(--card-bg) !important;
  border-color: var(--card-border) !important;
  color: var(--text-secondary) !important;
}
:root.dark .el-radio-button__original-radio:checked + .el-radio-button__inner {
  color: #fff !important;
}

/* 分页 */
.el-pagination.is-background .el-pager li.is-active {
  background: var(--primary-gradient) !important;
}
:root.dark .el-pagination.is-background .el-pager li:not(.is-active),
:root.dark .el-pagination.is-background .btn-prev,
:root.dark .el-pagination.is-background .btn-next {
  background: var(--card-bg) !important;
  color: var(--text-secondary) !important;
  border: 1px solid var(--card-border) !important;
}
:root.dark .el-pagination.is-background .el-pager li:hover:not(.is-active),
:root.dark .el-pagination.is-background .btn-prev:hover,
:root.dark .el-pagination.is-background .btn-next:hover {
  color: var(--primary) !important;
}

/* 输入框 */
.el-input__wrapper {
  border-radius: var(--radius-sm) !important;
  transition: all var(--transition-fast) !important;
}
:root.dark .el-input__wrapper {
  background: var(--input-bg) !important;
  border-color: var(--input-border) !important;
  box-shadow: none !important;
}
:root.dark .el-input__inner {
  color: var(--text) !important;
}
:root.dark .el-input__inner::placeholder {
  color: var(--text-muted) !important;
}
.el-input__wrapper:hover { border-color: var(--primary-light) !important; }
.el-input__wrapper.is-focus {
  border-color: var(--primary) !important;
  box-shadow: 0 0 0 2px var(--input-focus-shadow) !important;
}

/* Select 下拉 + 触发器 */
:root.dark .el-select__wrapper {
  background: var(--input-bg) !important;
  border-color: var(--input-border) !important;
  box-shadow: none !important;
}
:root.dark .el-select__placeholder {
  color: var(--text-muted) !important;
}
:root.dark .el-select-dropdown {
  background: var(--card-bg) !important;
  border: 1px solid var(--card-border) !important;
}
:root.dark .el-select-dropdown__item {
  color: var(--text) !important;
}
:root.dark .el-select-dropdown__item.hover,
:root.dark .el-select-dropdown__item:hover {
  background: var(--primary-bg) !important;
}
:root.dark .el-select-dropdown__item.selected {
  color: var(--primary) !important;
}
:root.dark .el-popper__arrow::before {
  background: var(--card-bg) !important;
  border: 1px solid var(--card-border) !important;
}

/* Dialog / Drawer */
:root.dark .el-dialog {
  background: var(--card-bg) !important;
  border: 1px solid var(--card-border) !important;
}
:root.dark .el-dialog__title { color: var(--text) !important; }
:root.dark .el-dialog__headerbtn .el-dialog__close { color: var(--text-secondary) !important; }
:root.dark .el-drawer { background: var(--card-bg) !important; }

/* Table */
:root.dark .el-table {
  --el-table-bg-color: var(--card-bg);
  --el-table-tr-bg-color: var(--card-bg);
  --el-table-header-bg-color: var(--bg-warm);
  --el-table-border-color: var(--card-border);
  --el-table-text-color: var(--text);
  --el-table-header-text-color: var(--text);
  --el-table-row-hover-bg-color: var(--card-bg-hover);
}
:root.dark .el-table th.el-table__cell { background: var(--bg-warm) !important; }

/* Tabs */
:root.dark .el-tabs__header { border-bottom-color: var(--card-border) !important; }
:root.dark .el-tabs__item { color: var(--text-secondary) !important; }
:root.dark .el-tabs__item.is-active { color: var(--primary) !important; }
:root.dark .el-tabs__active-bar { background: var(--primary) !important; }
:root.dark .el-tabs__nav-wrap::after { background: var(--card-border) !important; }

/* Tag */
:root.dark .el-tag--info {
  --el-tag-bg-color: rgba(255, 255, 255, 0.06);
  --el-tag-border-color: rgba(255, 255, 255, 0.08);
  --el-tag-text-color: var(--text-secondary);
}

/* Message / Notification */
:root.dark .el-message {
  background: var(--card-bg) !important;
  border: 1px solid var(--card-border) !important;
}
:root.dark .el-message .el-message__content { color: var(--text) !important; }
:root.dark .el-notification {
  background: var(--card-bg) !important;
  border: 1px solid var(--card-border) !important;
}
:root.dark .el-notification__title { color: var(--text) !important; }
:root.dark .el-notification__content { color: var(--text-secondary) !important; }

/* Empty */
:root.dark .el-empty__description p { color: var(--text-muted) !important; }

/* Skeleton */
.el-skeleton {
  --el-skeleton-color: var(--card-border);
  --el-skeleton-to-color: var(--bg);
}
:root.dark .el-skeleton {
  --el-skeleton-color: var(--card-border);
  --el-skeleton-to-color: var(--bg-warm);
}

/* Upload */
:root.dark .el-upload-list__item { border-color: var(--card-border) !important; }
:root.dark .el-upload-list__item-name { color: var(--text) !important; }

/* Switch */
:root.dark .el-switch__core { background: var(--card-border) !important; border-color: var(--card-border) !important; }
:root.dark .el-switch.is-checked .el-switch__core { background: var(--primary) !important; border-color: var(--primary) !important; }

/* Checkbox */
:root.dark .el-checkbox__label { color: var(--text-secondary) !important; }
:root.dark .el-checkbox__inner { background: var(--input-bg) !important; border-color: var(--input-border) !important; }

/* 原生表单控件暗色 */
:root.dark select {
  background: var(--input-bg) !important;
  color: var(--text) !important;
  border-color: var(--input-border) !important;
}
:root.dark textarea {
  background: var(--input-bg) !important;
  color: var(--text) !important;
  border-color: var(--input-border) !important;
}
:root.dark textarea::placeholder,
:root.dark select::placeholder {
  color: var(--text-muted) !important;
}

/* ================================================================
   Element Plus 弹出层 z-index 修正 (避免滚动容器内被裁剪)
   ================================================================ */
.el-select__popper,
.el-cascader__suggestion-panel,
.el-picker__popper {
  z-index: 5000 !important;
}

/* ================================================================
   滚动条
   ================================================================ */
::-webkit-scrollbar { width: 6px; height: 6px; }
::-webkit-scrollbar-track { background: transparent; }
::-webkit-scrollbar-thumb { background: var(--scrollbar-thumb); border-radius: 3px; }
::-webkit-scrollbar-thumb:hover { background: var(--scrollbar-thumb-hover); }

/* 选中 */
::selection { background: var(--selection-bg); color: var(--text); }
</style>
