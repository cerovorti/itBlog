<template>
  <div class="user-avatar" :style="avatarStyle" :title="username">
    <img v-if="src" :src="src" :alt="username" class="avatar-img" />
    <span v-else class="avatar-text">{{ initial }}</span>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{ username: string; src?: string | null; size?: number }>()

const initial = computed(() => props.username ? props.username.charAt(0).toUpperCase() : '?')

const colors = ['#FF6B6B','#4ECDC4','#A29BFE','#FD79A8','#74B9FF','#55EFC4','#FFE66D','#FF8E53','#6C5CE7','#00CEC9']
const bgColor = computed(() => {
  let hash = 0
  for (let i = 0; i < props.username.length; i++) hash = props.username.charCodeAt(i) + ((hash << 5) - hash)
  return colors[Math.abs(hash) % colors.length]
})

const sz = props.size || 40
const avatarStyle = computed(() => ({
  width: sz + 'px', height: sz + 'px', fontSize: (sz * 0.42) + 'px',
  background: props.src ? 'transparent' : `linear-gradient(135deg, ${bgColor.value}, ${bgColor.value}dd)`,
  borderRadius: '50%', display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
  color: '#fff', flexShrink: 0, overflow: 'hidden', userSelect: 'none',
  boxShadow: '0 2px 8px rgba(0,0,0,.1)', fontWeight: 700
}))
</script>

<style scoped>
.avatar-img { width: 100%; height: 100%; object-fit: cover; }
.avatar-text { line-height: 1; }
</style>
