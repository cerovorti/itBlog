import request from './request'

export interface CommentVO {
  id: number
  articleId: number
  userId: number
  username: string
  userAvatar: string
  parentId: number
  replyToUserId: number
  replyToUsername: string
  content: string
  createTime: string
  children: CommentVO[]
  replyCount: number
}

export function getTopComments(articleId: number, page = 1, size = 10) {
  return request.get(`/comment/page/${articleId}`, { params: { page, size } })
}

export function getReplies(parentId: number) {
  return request.get(`/comment/replies/${parentId}`)
}

export function addComment(data: {
  articleId: number; parentId?: number; replyToUserId?: number; content: string
}) {
  return request.post('/comment', data)
}

export function deleteComment(id: number) {
  return request.delete(`/comment/${id}`)
}