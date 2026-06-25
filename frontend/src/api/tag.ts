import request from './request'

export interface Tag {
  id: number
  name: string
}

export interface TagCloudItem {
  id: number
  name: string
  articleCount: number
}

export function getTagList(categoryId?: number) {
  return request.get('/tag/list', { params: categoryId ? { categoryId } : undefined })
}

export function getTagCloud() {
  return request.get('/tag/cloud')
}

export function createTag(name: string) {
  return request.post('/tag', { name })
}
