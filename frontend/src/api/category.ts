import request from './request'

export interface Category {
  id: number
  name: string
  parentId: number
  articleCount?: number
}

export function getCategoryTree() {
  return request.get('/category/tree')
}

export function getCategoryList() {
  return request.get('/category/list')
}
