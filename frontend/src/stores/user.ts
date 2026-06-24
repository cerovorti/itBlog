import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getToken, setToken, removeToken } from '../utils/token'
import type { UserVO } from '../api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref<string | null>(getToken())
  const user = ref<UserVO | null>(null)

  function login(t: string) {
    token.value = t
    setToken(t)
  }

  function logout() {
    token.value = null
    user.value = null
    removeToken()
  }

  function isLoggedIn() {
    return !!token.value
  }

  function isAdmin() {
    return user.value?.role === 1
  }

  return { token, user, login, logout, isLoggedIn, isAdmin }
})
