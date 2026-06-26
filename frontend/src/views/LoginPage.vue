<template>
  <div class="auth-bg">
    <div class="auth-card glass">
      <div class="auth-icon">👋</div>
      <h2>欢迎回来</h2>
      <p class="auth-sub">登录你的账号继续创作</p>
      <el-form @submit.prevent="handleLogin">
        <div class="input-group">
          <span class="input-icon">👤</span>
          <input v-model="username" placeholder="用户名" class="auth-input" />
        </div>
        <div class="input-group">
          <span class="input-icon">🔒</span>
          <input v-model="password" :type="showPwd ? 'text' : 'password'" placeholder="密码" class="auth-input" />
          <span class="pwd-toggle" @click="showPwd = !showPwd">{{ showPwd ? '🙈' : '👁' }}</span>
        </div>
        <label class="remember">
          <input type="checkbox" v-model="rememberMe" /> 记住我（7天）
        </label>
        <button type="submit" class="btn-auth" :disabled="submitting">
          {{ submitting ? '登录中...' : '登 录' }}
        </button>
      </el-form>
      <p class="auth-switch">还没有账号？<router-link to="/register">立即注册 →</router-link></p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api/auth'
import { getMyProfile } from '../api/user'
import { useUserStore } from '../stores/user'

const router = useRouter(); const userStore = useUserStore()
const username = ref(''); const password = ref(''); const showPwd = ref(false)
const rememberMe = ref(false); const submitting = ref(false)

async function handleLogin() {
  if (!username.value || !password.value) { ElMessage.warning('请输入用户名和密码'); return }
  submitting.value = true
  try {
    const r = await login(username.value, password.value, rememberMe.value)
    userStore.login(r.data.token)
    try { userStore.user = (await getMyProfile()).data } catch {}
    ElMessage.success('登录成功！🎉'); router.push('/')
  } catch {} finally { submitting.value = false }
}
</script>

<style scoped>
.auth-bg {
  min-height: calc(100vh - 60px); display: flex; align-items: center; justify-content: center;
  background: var(--auth-bg);
  background-size: 400% 400%; animation: bgShift 15s ease infinite;
  margin: -24px -100vw; padding: 40px calc(100vw - 20px);
}
@keyframes bgShift { 0%,100% { background-position: 0% 50%; } 50% { background-position: 100% 50%; } }
.auth-card { width: 400px; padding: 40px 36px; border-radius: var(--radius-xl); text-align: center; }
.auth-icon { font-size: 48px; margin-bottom: 12px; }
.auth-card h2 { font-family: var(--font-heading); font-size: 24px; font-weight: 800; margin-bottom: 4px; color: var(--text); }
.auth-sub { font-size: 14px; color: var(--text-secondary); margin-bottom: 28px; }
.input-group { display: flex; align-items: center; gap: 10px; padding: 10px 14px; border: 2px solid var(--card-border); border-radius: var(--radius-md); margin-bottom: 14px; background: var(--card-bg); transition: all var(--transition-fast); }
.input-group:focus-within { border-color: var(--primary-light); box-shadow: 0 0 0 3px var(--input-focus-shadow); }
.input-icon { font-size: 16px; flex-shrink: 0; }
.auth-input { flex: 1; border: none; outline: none; font-size: 15px; background: transparent; color: var(--text); }
.pwd-toggle { font-size: 16px; cursor: pointer; flex-shrink: 0; user-select: none; }
.remember { display: flex; align-items: center; gap: 6px; font-size: 13px; color: var(--text-secondary); cursor: pointer; margin-bottom: 20px; }
.btn-auth { width: 100%; padding: 13px; border: none; border-radius: var(--radius-md); background: var(--primary-gradient); color: #fff; font-size: 16px; font-weight: 700; cursor: pointer; transition: all var(--transition-normal); }
.btn-auth:hover { transform: translateY(-2px); box-shadow: var(--shadow-float); }
.btn-auth:disabled { opacity: 0.6; }
.auth-switch { margin-top: 20px; font-size: 13px; color: var(--text-secondary); }
.auth-switch a { color: var(--primary); text-decoration: none; font-weight: 600; }
</style>
