import request from './request'

export interface ArticleVO {
  id: number
  title: string
  summary: string
  coverImg: string
  authorId: number
  authorName: string
  authorAvatar: string
  categoryId: number
  categoryName: string
  tags: string[]
  viewCount: number
  likeCount: number
  commentCount: number
  status: number
  reviewStatus?: number
  createTime: string
  updateTime: string
}

export interface ArticleDetailVO {
  article: ArticleVO
  content: string
  draftContent?: string
  toc: TocItem[]
  liked: boolean
  favorited: boolean
  prevArticle: ArticleVO | null
  nextArticle: ArticleVO | null
}

export interface TocItem {
  level: number
  title: string
  anchor: string
  children: TocItem[]
}

export interface PageData<T> {
  records: T[]
  total: number
  page: number
  size: number
}

export function getArticleList(params: Record<string, any>) {
  return request.get('/article/list', { params })
}

export function searchArticles(keyword: string, page = 1, size = 10) {
  return request.get('/article/search', { params: { keyword, page, size } })
}

export function getArticleDetail(id: number) {
  return request.get(`/article/${id}`)
}

export function getMyArticles(page = 1, size = 10) {
  return request.get('/article/my', { params: { page, size } })
}

export function getUserArticles(userId: number, page = 1, size = 10) {
  return request.get(`/article/user/${userId}`, { params: { page, size } })
}

export function getDrafts(page = 1, size = 10) {
  return request.get('/article/drafts', { params: { page, size } })
}

export function publishArticle(data: {
  title: string; content: string; summary?: string; coverImg?: string
  categoryId?: number; tagIds?: number[]; status: number
}) {
  return request.post('/article/publish', data)
}

export function updateArticle(id: number, data: any) {
  return request.put(`/article/${id}`, data)
}

export function deleteArticle(id: number) {
  return request.delete(`/article/${id}`)
}

// 管理员接口
export function getPendingArticles(page = 1, size = 10) {
  return request.get('/article/pending', { params: { page, size } })
}

export function approveArticle(id: number) {
  return request.put(`/article/${id}/approve`)
}

export function rejectArticle(id: number) {
  return request.put(`/article/${id}/reject`)
}
