export interface SubsidyStandard {
  mealStandard: number
  transportStandard: number
  commStandard: number
}
 // getSubsidyStandard方法 实现了城市分级的补助标准 6-16
export function getSubsidyStandard(cityType: '1' | '2' | '3'): SubsidyStandard {
  switch (cityType) {
    case '1': // 一线
      return { mealStandard: 100, transportStandard: 40, commStandard: 40 }
    case '2': // 二线
      return { mealStandard: 80, transportStandard: 40, commStandard: 40 }
    case '3': // 三线
    default:
      return { mealStandard: 50, transportStandard: 40, commStandard: 40 }
  }
}

// getDatesBetween方法，通过while循环逐日生成行程期间的所有日期，并包含中文星期信息，为补助日历提供了精确的基础数据。
export function getDatesBetween(
  startDateStr: string,
  endDateStr: string,
): { date: string; weekday: string }[] {
  const dates: { date: string; weekday: string }[] = []
  const start = new Date(startDateStr)
  const end = new Date(endDateStr)

  if (isNaN(start.getTime()) || isNaN(end.getTime())) return []

  const current = new Date(start)
  const weekdayNames = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']

  while (current <= end) {
    const year = current.getFullYear()
    const month = String(current.getMonth() + 1).padStart(2, '0')
    const day = String(current.getDate()).padStart(2, '0')
    const dateStr = `${year}-${month}-${day}`

    dates.push({
      date: dateStr,
      weekday: weekdayNames[current.getDay()] || '',
    })

    current.setDate(current.getDate() + 1)
  }

  return dates
}
