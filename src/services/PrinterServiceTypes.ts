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
  { key: 'randomSlogan', label: '随机文案' },
  { key: 'priceMy', label: '我:XX块钱' },
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

  // 40x30 专用模板：文案 + 品名 + 我:XX块钱 + 条码
  if (size === '40x30') {
    return [
      { id: genElementId(), type: 'text', x: m, y: m, w: right - m, h: 10, fieldKey: 'randomSlogan', fontSizeMm: 3, bold: false, align: 'left' },
      { id: genElementId(), type: 'text', x: m, y: m + 11, w: right - m, h: 5, fieldKey: 'name', fontSizeMm: 3.5, bold: true, align: 'left' },
      { id: genElementId(), type: 'text', x: m, y: m + 16.5, w: right - m, h: 5, fieldKey: 'priceMy', fontSizeMm: 4, bold: true, align: 'left' },
      { id: genElementId(), type: 'barcode', x: m, y: bottom - 11, w: right - m, h: 8, fieldKey: 'code', align: 'center' },
    ];
  }

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
    case 'randomSlogan': return getRandomSlogan();
    case 'priceMy': return data.retailPrice > 0 ? `我:${data.retailPrice}块钱` : '';
    default: return '';
  }
}

const SLOGANS = [
  '我带给你不仅好心情\n还有好运气',
  '你要昂首挺胸\n你要勇敢自信\n你要热烈漂亮',
  '选我啊！姐妹！\n一条顶天立地\n又好看的裤子',
  '我好喜欢我自己\n我的刺绣真好看\n我特喜欢',
  '曾经的我想靠脸吃饭\n可是我妈想靠价格',
  '我个人觉得\n我比显微露可爱一万倍\n对吧！主人！',
  '我不仅是一件衣服\n我是热爱生活的意义',
  '人生就应该多尝试\n衣服就应该多试穿',
  '别看我价格美丽\n我质量也很能打',
  '穿上我你就是\n这条街最靓的崽',
  '我不是什么名牌\n但我是你的唯一',
  '今天也要加油鸭\n穿我去搬砖吧',
  '你的衣柜里\n还差一个我',
  '好看不贵\n经济实惠',
  '这件衣服会说话\n它说快把我带回家',
  '我小小的身体里\n藏着大大的时尚',
  '老板说赔本卖\n只为交你这个朋友',
  '你负责赚钱养家\n我负责貌美如花',
  '穿上我\n前任后悔现任骄傲',
  '我不是你的唯一\n但你可以试试',
  '夏天的风\n吹不动我对你的心',
  '这件衣服有毒\n穿上就脱不下来了',
  '我的美\n需要你亲自来验证',
  '今天穿我\n明天还想穿我',
  '我不是在打折\n我是在打你的心',
  '你和我之间\n只差一个下单',
  '这件衣服有灵魂\n它选中了你',
  '穿上我\n你就是行走的种草机',
  '我贵吗？\n看看你的颜值配不配',
  '衣服千千万\n新的最好看',
  '这个颜色\n写满了你的名字',
  '买不买没关系\n试试又不花钱',
  '你试穿的样子\n真的很美',
  '好衣服不等人\n下手要快姿势要帅',
  '每一件衣服\n都在等它的主人',
  '我在这里\n等你带我回家',
  '今天不买\n明天就涨价了',
  '你的气质\n配得上更好的衣服',
  '这件衣服会\n让你忘记所有烦恼',
  '穿上我\n你就是全场焦点',
  '我不是在推销\n我是在推荐美好',
  '你值得拥有\n更好的自己',
  '这件衣服\n是你今年最对的选择',
  '每天都要\n穿得漂漂亮亮的',
  '你的衣橱\n需要一次革命',
  '穿上我\n自信自然来',
  '我不是最好的\n但我是最适合你的',
  '这件衣服有魔力\n穿上就不想脱',
  '你和时尚之间\n只差这件衣服',
  '今天穿什么\n穿我就对了',
  '我是一块宝藏\n等你来挖掘',
  '这件衣服\n是你对自己的奖励',
  '穿上我\n去见你想见的人',
  '衣服选得好\n没对象也能找',
  '你穿这件\n回头率百分之两百',
  '我不是在吹牛\n我是在说实话',
  '这件衣服\n是你今夏的命定',
  '穿上我\n你就是行走的画报',
];

let _sloganIdx = 0;
export function getRandomSlogan(): string {
  const s = SLOGANS[_sloganIdx % SLOGANS.length];
  _sloganIdx++;
  return s;
}
