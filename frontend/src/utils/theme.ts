import { ref } from 'vue'

const THEME_KEY = 'demo_album_theme'

function getSystemDark(): boolean {
  return window.matchMedia('(prefers-color-scheme: dark)').matches
}

export const isDark = ref(false)

export function initTheme() {
  const saved = localStorage.getItem(THEME_KEY)
  isDark.value = saved ? saved === 'dark' : getSystemDark()
  document.documentElement.classList.toggle('dark', isDark.value)
}

export function toggleTheme() {
  isDark.value = !isDark.value
  localStorage.setItem(THEME_KEY, isDark.value ? 'dark' : 'light')
  document.documentElement.classList.toggle('dark', isDark.value)
}
