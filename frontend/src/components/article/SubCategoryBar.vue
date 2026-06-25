<template>
  <div class="subcat-bar" v-if="items.length">
    <button :class="{ active: !activeId }" @click="$emit('select', undefined)">全部</button>
    <button v-for="c in items" :key="c.id" :class="{ active: activeId === c.id }" @click="$emit('select', c.id)">{{ c.name }}</button>
  </div>
</template>

<script setup lang="ts">
defineProps<{ items: { id: number; name: string }[]; activeId?: number }>()
defineEmits<{ select: [id: number | undefined] }>()
</script>

<style scoped>
.subcat-bar { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 16px; }
.subcat-bar button { padding: 6px 16px; border-radius: 20px; border: 1.5px solid var(--card-border); background: var(--card-bg); color: var(--text-secondary); font-size: 13px; cursor: pointer; transition: all var(--transition-fast); font-weight: 500; }
.subcat-bar button.active { background: var(--primary-gradient); color: #fff; border-color: transparent; }
.subcat-bar button:hover:not(.active) { border-color: var(--primary-light); color: var(--primary); }
</style>