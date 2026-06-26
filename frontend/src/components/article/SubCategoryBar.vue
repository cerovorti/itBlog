<template>
  <div class="subcat-bar" v-if="items.length">
    <button v-for="c in items" :key="c.id" :class="{ active: activeId === c.id }" @click="$emit('select', c.id)">{{ c.name }}<span v-if="c.articleCount != null" class="count">{{ c.articleCount }}</span></button>
  </div>
</template>

<script setup lang="ts">
defineProps<{ items: { id: number; name: string; articleCount?: number }[]; activeId?: number }>()
defineEmits<{ select: [id: number | undefined] }>()
</script>

<style scoped>
.subcat-bar { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 16px; }
.subcat-bar button { padding: 6px 16px; border-radius: 20px; border: 1.5px solid var(--card-border); background: var(--card-bg); color: var(--text-secondary); font-size: 13px; cursor: pointer; transition: all var(--transition-fast); font-weight: 500; }
.subcat-bar button.active { background: var(--primary-gradient); color: #fff; border-color: transparent; }
.subcat-bar button.active .count { background: rgba(255,255,255,0.25); color: #fff; }
.subcat-bar button:hover:not(.active) { border-color: var(--primary-light); color: var(--primary); }
.count { font-size: 10.5px; background: var(--bg); color: var(--text-muted); padding: 1px 6px; border-radius: 8px; font-weight: 600; margin-left: 4px; line-height: 1.6; }
</style>