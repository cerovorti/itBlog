import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useArticleStore = defineStore('article', () => {
  const sort = ref<string>('popular')
  const page = ref<number>(1)
  const categoryId = ref<number | null>(null)
  const tagId = ref<number | null>(null)

  function setSort(s: string) {
    sort.value = s
  }

  function setPage(p: number) {
    page.value = p
  }

  function setCategory(cid: number | null) {
    categoryId.value = cid
  }

  function setTag(tid: number | null) {
    tagId.value = tid
  }

  function reset() {
    sort.value = 'popular'
    page.value = 1
    categoryId.value = null
    tagId.value = null
  }

  return { sort, page, categoryId, tagId, setSort, setPage, setCategory, setTag, reset }
})
