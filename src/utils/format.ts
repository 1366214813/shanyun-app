export function formatMoney(n: number): string {
  return n.toLocaleString('zh-CN', {
    minimumFractionDigits: n % 1 === 0 ? 0 : 2,
    maximumFractionDigits: 2,
  });
}

export function formatDate(d: Date): string {
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${y}-${m}-${day}`;
}

export function localDateKey(d: Date = new Date()): string {
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${y}-${m}-${day}`;
}

export function formatDisplayDate(d: Date): string {
  const weekdays = ['日', '一', '二', '三', '四', '五', '六'];
  return `${String(d.getFullYear()).slice(2)}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} 星期${weekdays[d.getDay()]}`;
}

export function genId(prefix: string): string {
  return `${prefix}_${Date.now().toString(36)}_${Math.random().toString(36).slice(2, 6)}`;
}

/**
 * 生成商品条码：13 位纯数字（EAN-13 风格），前 12 位随机、第 13 位为校验位。
 * 入库时自动分配给新商品，唯一且可直接扫码。
 */
export function genBarcode(): string {
  let digits = '';
  for (let i = 0; i < 12; i++) digits += Math.floor(Math.random() * 10);
  let sum = 0;
  for (let i = 0; i < 12; i++) {
    const d = Number(digits[i]);
    sum += i % 2 === 0 ? d : d * 3;
  }
  const check = (10 - (sum % 10)) % 10;
  return digits + check;
}

export function levelText(level: string): string {
  return { platinum: '铂金会员', gold: '黄金会员', vip: 'VIP', normal: '普通会员' }[level] || '普通会员';
}

export function levelColor(level: string): string {
  return { platinum: '#9C27B0', gold: '#FF9800', vip: '#2196F3', normal: '#9E9E9E' }[level] || '#9E9E9E';
}

export function categoryEmoji(cat: string): string {
  const map: Record<string, string> = {
    '连衣裙': '👗', '裙装': '👗', '半裙': '👗', '短裙': '👗', '长裙': '👗',
    '裤装': '👖', '西装裤': '👖', '牛仔裤': '👖', '休闲裤': '👖', '九分裤': '👖', '阔腿裤': '👖',
    '外套': '🧥', '西装': '👔', '套装': '👔',
    '上衣': '👕', 'T恤': '👕', '衬衫': '👕', '卫衣': '👕', '毛衣': '🧶',
    '鞋子': '👟', '鞋履': '👟',
    '配饰': '👜', '包': '👜', '帽子': '🧢', '围巾': '🧣',
    '内衣': '🩱', '睡衣': '🛌',
  };
  if (map[cat]) return map[cat];
  for (const [key, emoji] of Object.entries(map)) {
    if (cat.includes(key)) return emoji;
  }
  return '👔';
}
