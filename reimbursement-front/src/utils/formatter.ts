// 金额格式化：把数字变成「带千分位 + 固定两位小数」的展示字符串
// 比如：1234.5 → "1,234.50"，空值 → "0.00"
export function formatAmount(value: number | undefined | null): string {
  // 1. 空值兜底：没传值或者值为 null，直接返回 0.00
  if (value === undefined || value === null) {
    return '0.00'
  }

  // 2. 先把数字强制保留两位小数，转成字符串
  let numText = value.toFixed(2)

  // 3. 给整数部分加千分位逗号（从右往左每3位插一个逗号）
  numText = numText.replace(/\B(?=(\d{3})+\b)/g, ',')

  return numText
}

// 日期格式化：从完整时间字符串里，只截取年月日部分
// 比如："2024-05-20 14:30:00" → "2024-05-20"
export function formatDate(value: string | undefined | null): string {
  // 1. 空值兜底：值为空/没传，直接返回空字符串
  if (!value) {
    return ''
  }

  // 2. 直接截取前10个字符，正好对应 YYYY-MM-DD 格式
  const dateOnly = value.substring(0, 10)

  return dateOnly
}

// 百分比格式化：把小数比率转成带百分号的格式，固定两位小数
// 比如：0.1234 → "12.34%"，空值 → "0.00%"
export function formatPercent(value: number | undefined | null): string {
  // 1. 空值兜底：没传值或者值为 null，直接返回 0.00%
  if (value === undefined || value === null) {
    return '0.00%'
  }

  // 2. 小数转百分比：乘以 100
  const percentValue = value * 100

  // 3. 保留两位小数
  const fixedValue = percentValue.toFixed(2)

  // 4. 拼上百分号
  const result = fixedValue + '%'

  return result
}
