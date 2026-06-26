/**
 * 一级分类与二级标签的映射关系（硬编码）
 *
 * 数据库变更说明：
 * - busi_category 表只保留 6 个一级分类，parent_id 全部为 0
 * - busi_tag 表存储 20 个新的二级标签
 * - 二级标签可以属于多个一级分类，此映射由业务层控制
 */

/** 一级分类 ID → 关联的二级标签名称数组 */
export const CATEGORY_TAG_MAP: Record<number, string[]> = {
  1: ['React', 'Vue.js', 'Node.js', 'GraphQL', 'Nginx'],
  2: ['Node.js', 'Python', 'Java', 'Go', 'Spring Boot', 'MySQL', 'Redis',
      'Docker', 'Linux', 'Nginx', 'Kafka', 'GraphQL', 'gRPC', 'OAuth2/JWT'],
  3: ['Go', 'Redis', 'Docker', 'Kubernetes', 'Linux', 'Nginx', 'Kafka', 'Terraform'],
  4: ['Python', 'Docker', 'Kubernetes', 'Kafka', 'PyTorch', 'TensorFlow'],
  5: ['Python', 'Docker', 'Linux', 'Terraform', 'OAuth2/JWT'],
  6: ['Java', 'MySQL', 'Redis', 'Kubernetes', 'Kafka', 'GraphQL', 'gRPC'],
}

/** 一级分类 ID → 分类名称 */
export const CATEGORY_NAMES: Record<number, string> = {
  1: '前端开发',
  2: '后端开发',
  3: '云计算与运维',
  4: '人工智能与大数据',
  5: '信息安全',
  6: '软件架构',
}

/** 根据一级分类 ID 获取关联的二级标签名称列表 */
export function getTagsByCategoryId(categoryId: number): string[] {
  return CATEGORY_TAG_MAP[categoryId] || []
}