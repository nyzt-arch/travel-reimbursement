export function formatAmount(value: number | undefined | null): string {
  if (value === undefined || value === null) return '0.00';
  return new Intl.NumberFormat('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(value);
}

export function formatDate(value: string | undefined | null): string {
  if (!value) return '';
  // Extract YYYY-MM-DD
  return value.substring(0, 10);
}

export function formatPercent(value: number | undefined | null): string {
  if (value === undefined || value === null) return '0.00%';
  const pct = value * 100;
  return `${pct.toFixed(2)}%`;
}
