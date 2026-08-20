export type LabelSize = '40x30' | '50x30' | '60x40' | '80x50' | '100x60' | '100x80';

export type LabelElementType = 'text' | 'barcode' | 'qrcode' | 'line' | 'rect';

export type LabelElement = {
  id: string;
  type: LabelElementType;
  x: number;  // mm, left
  y: number;  // mm, top
  w: number;  // mm
  h: number;  // mm (text: line height)
  fieldKey?: string; // 绑定商品字段
  text?: string;     // 静态文本（无 fieldKey 时用）
  fontSizeMm?: number; // 字高 mm
  bold?: boolean;
  align?: 'left' | 'center' | 'right';
  thicknessMm?: number; // line/rect 线宽
};

export type LabelConfig = {
  size: LabelSize;
  elements: LabelElement[];
};

export const LABEL_PRESETS: Record<LabelSize, { w: number; h: number; name: string }> = {
  '40x30': { w: 40, h: 30, name: '40×30mm 小标签' },
  '50x30': { w: 50, h: 30, name: '50×30mm 标准' },
  '60x40': { w: 60, h: 40, name: '60×40mm 中号' },
  '80x50': { w: 80, h: 50, name: '80×50mm 大号' },
  '100x60': { w: 100, h: 60, name: '100×60mm 特大' },
  '100x80': { w: 100, h: 80, name: '100×80mm 超大' },
};

export const FIELD_KEYS: { key: string; label: string }[] = [
  { key: 'name', label: '商品名称' },
  { key: 'code', label: '款号' },
  { key: 'category', label: '分类' },
  { key: 'color', label: '颜色' },
  { key: 'size', label: '尺码' },
  { key: 'retailPrice', label: '零售价' },
  { key: 'purchasePrice', label: '进货价' },
];

let elementSeq = 0;
export function genElementId(): string {
  elementSeq += 1;
  return `el_${Date.now().toString(36)}_${elementSeq}_${Math.random().toString(36).slice(2, 5)}`;
}

function buildDefaultElements(size: LabelSize): LabelElement[] {
  const { w, h } = LABEL_PRESETS[size];
  const m = 4;
  const right = w - m;
  const bottom = h - m;
  const priceW = Math.min(30, Math.round(w * 0.5));

  const els: LabelElement[] = [];
  els.push({ id: genElementId(), type: 'text', x: m, y: m, w: right - m, h: 6, fieldKey: 'name', fontSizeMm: 5, bold: true, align: 'left' });
  els.push({ id: genElementId(), type: 'text', x: m, y: m + 8, w: right - m, h: 4.5, fieldKey: 'code', fontSizeMm: 3.5, bold: false, align: 'left' });
  els.push({ id: genElementId(), type: 'text', x: m, y: m + 13.5, w: right - m, h: 4.5, fieldKey: 'retailPrice', fontSizeMm: 6, bold: true, align: 'left' });
  els.push({ id: genElementId(), type: 'barcode', x: m, y: Math.max(bottom - 12, m + 19), w: right - m, h: 9, fieldKey: 'code', align: 'center' });
  return els;
}

/** 兼容旧版 config（fields/showBarcode 等）迁移为 element 版 */
export function migrateLabelConfig(raw: any): LabelConfig {
  const size: LabelSize = raw?.size || '60x40';
  if (raw && Array.isArray(raw.elements) && raw.elements.length > 0) {
    return { size, elements: raw.elements };
  }
  if (raw && Array.isArray(raw.fields)) {
    const { w, h } = LABEL_PRESETS[size];
    const m = 4;
    const els: LabelElement[] = [];
    let y = m;
    for (const f of raw.fields) {
      if (!f || !f.show) continue;
      const fontSizeMm = [3, 4, 5, 7, 9][(f.fontSize || 2) - 1] || 4;
      els.push({
        id: genElementId(), type: 'text', x: m, y, w: w - m * 2, h: Math.round(fontSizeMm + 1.5),
        fieldKey: f.key, fontSizeMm, bold: !!f.bold, align: 'left',
      });
      y += Math.round(fontSizeMm + 3);
    }
    if (raw.showBarcode) {
      els.push({ id: genElementId(), type: 'barcode', x: m, y: h - 13, w: w - m * 2, h: 9, fieldKey: 'code', align: 'center' });
    }
    if (raw.showQrcode) {
      els.push({ id: genElementId(), type: 'qrcode', x: w - 22, y: h - 22, w: 18, h: 18, fieldKey: 'code' });
    }
    if (raw.showBorder) {
      els.push({ id: genElementId(), type: 'rect', x: 1, y: 1, w: w - 2, h: h - 2, thicknessMm: 0.3 });
    }
    return { size, elements: els };
  }
  return { size, elements: buildDefaultElements(size) };
}

export function buildDefaultConfig(size: LabelSize = '60x40'): LabelConfig {
  return { size, elements: buildDefaultElements(size) };
}

export const DEFAULT_LABEL_CONFIG: LabelConfig = buildDefaultConfig('60x40');

export type LabelData = {
  name: string;
  code: string;
  category: string;
  color: string;
  size: string;
  retailPrice: number;
  purchasePrice: number;
};

export function fieldValue(data: LabelData, key: string): string {
  switch (key) {
    case 'name': return data.name || '商品';
    case 'code': return data.code || '';
    case 'category': return data.category || '';
    case 'color': return data.color || '';
    case 'size': return data.size || '';
    case 'retailPrice': return data.retailPrice > 0 ? `¥${data.retailPrice}` : '';
    case 'purchasePrice': return data.purchasePrice > 0 ? `进:¥${data.purchasePrice}` : '';
    default: return '';
  }
}
