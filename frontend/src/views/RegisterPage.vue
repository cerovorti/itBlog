<template>
  <div class="auth-bg reg-bg">
    <div class="auth-card glass">
      <div class="auth-icon">🚀</div>
      <h2>加入我们</h2>
      <p class="auth-sub">创建账号开始你的技术分享之旅</p>
      <el-form @submit.prevent="handleRegister">
        <div class="input-group"><span class="input-icon">👤</span><input v-model="username" placeholder="用户名 (2-20位)" class="auth-input" /></div>
        <div class="input-group"><span class="input-icon">📧</span><input v-model="email" placeholder="邮箱" class="auth-input" /></div>
        <div class="input-group"><span class="input-icon">🔒</span><input v-model="password" :type="showPwd ? 'text' : 'password'" placeholder="密码 (8位以上,含字母+数字)" class="auth-input" /><span class="pwd-toggle" @click="showPwd = !showPwd">{{ showPwd ? '🙈' : '👁' }}</span></div>
        <div class="input-group"><span class="input-icon">🔒</span><input v-model="confirmPassword" :type="showConfirmPwd ? 'text' : 'password'" placeholder="确认密码" class="auth-input" /><span class="pwd-toggle" @click="showConfirmPwd = !showConfirmPwd">{{ showConfirmPwd ? '🙈' : '👁' }}</span></div>
        <button type="submit" class="btn-auth" :disabled="loading">{{ loading ? '注册中...' : '注 册' }}</button>
      </el-form>
      <p class="auth-switch">已有账号？<router-link to="/login">去登录 →</router-link></p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '../api/auth'
const router = useRouter()
const username = ref(''); const email = ref('')
const password = ref(''); const confirmPassword = ref(''); const showPwd = ref(false); const showConfirmPwd = ref(false); const loading = ref(false)
async function handleRegister() {
  if (!username.value || !email.value || !password.value || !confirmPassword.value) { ElMessage.warning('请填写所有字段'); return }
  if (password.value.length < 8 || !/[a-zA-Z]/.test(password.value) || !/[0-9]/.test(password.value)) { ElMessage.warning('密码需8位以上且包含字母和数字'); return }
  if (password.value !== confirmPassword.value) { ElMessage.warning('两次密码不一致'); return }
  loading.value = true
  try { await register(username.value, password.value, email.value); ElMessage.success('注册成功！请登录'); router.push('/login') }
  catch {} finally { loading.value = false }
}
</script>

<style scoped>
.auth-bg {
  min-height: calc(100vh - 60px); display: flex; align-items: center; justify-content: center;
  background: var(--auth-bg);
  background-size: 400% 400%; animation: bgShift 15s ease infinite;
  margin: -24px -100vw; padding: 40px calc(100vw - 20px);
}
.reg-bg { background: var(--auth-bg); }
@keyframes bgShift { 0%,100% { background-position: 0% 50%; } 50% { background-position: 100% 50%; } }
.auth-card { width: 400px; padding: 40px 36px; border-radius: var(--radius-xl); text-align: center; }
.auth-icon { font-size: 48px; margin-bottom: 12px; }
.auth-card h2 { font-size: 24px; font-weight: 800; margin-bottom: 4px; }
.auth-sub { font-size: 14px; color: var(--text-secondary); margin-bottom: 28px; }
.input-group { display: flex; align-items: center; gap: 10px; padding: 10px 14px; border: 2px solid var(--card-border); border-radius: var(--radius-md); margin-bottom: 14px; background: var(--card-bg); transition: all var(--transition-fast); }
.input-group:focus-within { border-color: var(--primary-light); box-shadow: 0 0 0 3px var(--input-focus-shadow); }
.input-icon { font-size: 16px; flex-shrink: 0; }
.auth-input { flex: 1; border: none; outline: none; font-size: 15px; background: transparent; color: var(--text); }
.pwd-toggle { font-size: 16px; cursor: pointer; flex-shrink: 0; user-select: none; }
.btn-auth { width: 100%; padding: 13px; border: none; border-radius: var(--radius-md); background: var(--primary-gradient); color: #fff; font-size: 16px; font-weight: 700; cursor: pointer; transition: all var(--transition-normal); }
.btn-auth:hover { transform: translateY(-2px); box-shadow: var(--shadow-float); }
.btn-auth:disabled { opacity: 0.6; }
.auth-switch { margin-top: 20px; font-size: 13px; color: var(--text-secondary); }
.auth-switch a { color: var(--primary); text-decoration: none; font-weight: 600; }
</style>
