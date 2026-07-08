// 金额格式化：把数字变成「带千分位 + 固定两位小数」的展示字符串
// 比如：1234.5 → "1,234.50"，空值 → "0.00"
export function formatAmount(value: number | undefined | null): string {
  if (value === undefined || value === null) {
    return '0.00'
  }
  // toFixed(2) 强制保留两位小数，再用正则从右往左每 3 位插入一个逗号
  return value.toFixed(2).replace(/\B(?=(\d{3})+\b)/g, ',')
}

// 日期格式化：从完整时间字符串里，只截取年月日部分
// 比如："2024-05-20 14:30:00" → "2024-05-20"
export function formatDate(value: string | undefined | null): string {
  if (!value) return ''
  // substring(0, 10)，截取前 10 个字符，正好对应 yyyy-MM-dd 格式
  return value.substring(0, 10)
}

// 日期时间格式化：把 ISO 8601 格式中的 T 替换为空格
// 比如："2026-07-06T00:00:00" → "2026-07-06 00:00:00"
export function formatDateTime(value: string | undefined | null): string {
  if (!value) return ''
  return value.replace('T', ' ')
}

// 30 百分比格式化：把小数比率转成带百分号的格式，固定两位小数
// 比如：0.1234 → "12.34%"，空值 → "0.00%"
export function formatPercent(value: number | undefined | null): string {
  if (value === undefined || value === null) return '0.00%'
  return (value * 100).toFixed(2) + '%'
}
