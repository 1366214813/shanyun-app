import React, { useState, useEffect, useRef } from 'react';
import {
  View, Text, TouchableOpacity, StyleSheet, FlatList,
  Image, ActivityIndicator, Alert, TextInput, Switch,
  Dimensions,
} from 'react-native';
import * as ImagePicker from 'expo-image-picker';
import { useOcr } from '../hooks/useOcr';
import { useAppStore, THEMES } from '../store/useAppStore';
import { genId, genBarcode } from '../utils/format';
import { logError, logInfo } from '../utils/logger';
import type { RecognitionResult } from 'ppu-paddle-ocr/mobile';

type ParsedItem = {
  id: string;
  name: string;
  code: string;
  category: string;
  color: string;
  size: string;
  retailPrice: string;
  purchasePrice: string;
  qty: string;
  selected: boolean;
};

function parseReceiptItems(text: string, ocrItems?: RecognitionResult[]): ParsedItem[] {
  let items: ParsedItem[] = [];

  if (ocrItems && ocrItems.length >= 2) {
    const coordItems = parseByCoordinates(ocrItems);
    if (coordItems.length > 0) items = coordItems;
  }

  if (!items.length) {
    items = parseByLines(text);
  }

  if (!items.length) {
    items = parseContinuous(text);
  }

  const uniq = new Map<string, ParsedItem>();
  items.forEach(it => {
    const key = it.code || `${it.name}|${it.purchasePrice}`;
    if (!uniq.has(key)) uniq.set(key, it);
  });

  return Array.from(uniq.values());
}

const EXCLUDE_RE = /(合计|总计|实付|付款|退货|出货|未付|欠款|地址|电话|微信|温馨提示|累计|运费|确认|收报|打印|投诉|导购|小票编号|会员|本次|消费|抵扣|剩余|千万|退换|出示|所退|未弄|吊牌|货号|描述|品名|序号|分类|小计|单价|数量|总价|收款|找零|积分|剩念|斜音号|抖音号|收银小票|万千注意|最后期限|退换货|不影响|未弄脏|吊牌未|小抖章|购买时|未经穿着|未见水|开单时间|销售|回2|颜色|开单|备注|电话|手机|长按|删除此|清零|兑换|积分将|在此日期|完成积分|卡券|怦然心动|kids|连锁|收银小票)/;
const COLORS_RE = /(混色|混他|纯色|黑色|白色|红色|蓝色|绿色|黄色|粉色|灰色|棕色|米色|驼色|杏色|卡其|藏青|酒红|墨绿|焦糖|奶白|浅灰|深灰|米白|裸粉|豆沙|玫瑰|[\u4e00-\u9fa5]{1,3}色)/;
const CLOTHING_RE = /(防晒裤|西装裤|工装裤|休闲裤|牛仔裤|九分裤|七分裤|运动裤|打底裤|阔腿裤|直筒裤|铅笔裤|小脚裤|哈伦裤|背带裤|设计款|吊带蛋糕裙|连衣裙|半身裙|百褶裙|A字裙|鱼尾裙|蓬蓬裙|公主裙|背带裙|吊带裙|吊带|上衣|衬衫|衬衣|卫衣|毛衣|针织衫|开衫|马甲|背心|夹克|外套|大衣|风衣|棉服|羽绒服|皮衣|西服|西装|裤子|长裤|短裤|西裤|裙子|短裙|迷你裙|半裙|蛋糕裙|长袖|套装|T恤|T|两件套|三件套|衣|裤|裙)/;

function parseByCoordinates(ocrItems: RecognitionResult[]): ParsedItem[] {
  if (ocrItems.length === 0) return [];
  const sorted = [...ocrItems].sort((a, b) => a.box.y - b.box.y);
  const avgHeight = sorted.reduce((s, r) => s + r.box.height, 0) / sorted.length;
  const threshold = Math.max(avgHeight * 0.5, 10);
  const lines: RecognitionResult[][] = [];
  let curLine = [sorted[0]];
  for (let i = 1; i < sorted.length; i++) {
    const item = sorted[i];
    const prev = curLine[curLine.length - 1];
    if (Math.abs(item.box.y - prev.box.y) <= threshold) {
      curLine.push(item);
    } else {
      lines.push(curLine);
      curLine = [item];
    }
  }
  lines.push(curLine);

  let headerIdx = -1;
  const colRanges: { name?: [number, number]; code?: [number, number]; qty?: [number, number]; price?: [number, number]; total?: [number, number]; color?: [number, number] } = {};
  for (let i = 0; i < lines.length; i++) {
    const texts = lines[i].map(r => r.text).join('');
    if (/单价|进价|价格|金额/.test(texts) && /数量|件数|个数/.test(texts)) {
      headerIdx = i;
      for (const r of lines[i]) {
        const t = r.text;
        if (/品名|名称|货品|分类/.test(t)) colRanges.name = [r.box.x, r.box.x + r.box.width];
        else if (/货号|款号|编号/.test(t)) colRanges.code = [r.box.x, r.box.x + r.box.width];
        else if (/颜色|色/.test(t) && !/颜色尺/.test(t)) colRanges.color = [r.box.x, r.box.x + r.box.width];
        else if (/单价|进价|价格/.test(t)) colRanges.price = [r.box.x, r.box.x + r.box.width];
        else if (/数量|件数|个数/.test(t)) colRanges.qty = [r.box.x, r.box.x + r.box.width];
        else if (/总价|金额|小计|合计/.test(t)) colRanges.total = [r.box.x, r.box.x + r.box.width];
      }
      break;
    }
  }

  const items: ParsedItem[] = [];
  if (headerIdx >= 0 && (colRanges.name || colRanges.price || colRanges.qty)) {
    for (let i = headerIdx + 1; i < lines.length; i++) {
      const item = parseLineByColumns(lines[i], colRanges);
      if (item) items.push(item);
    }
  } else {
    for (const line of lines) {
      const t = line.map(r => r.text).join('');
      const item = parseLine(t);
      if (item) items.push(item);
    }
  }
  return items;
}

function assignToColumn(x: number, range?: [number, number]): boolean {
  if (!range) return false;
  return x >= range[0] - 15 && x <= range[1] + 15;
}

function parseLineByColumns(lineItems: RecognitionResult[], colRanges: { name?: [number, number]; code?: [number, number]; qty?: [number, number]; price?: [number, number]; total?: [number, number]; color?: [number, number] }): ParsedItem | null {
  const groups: { name: string[]; code: string[]; qty: string[]; price: string[]; total: string[]; color: string[]; other: string[] } = { name: [], code: [], qty: [], price: [], total: [], color: [], other: [] };
  for (const r of lineItems) {
    const cx = r.box.x;
    const t = r.text.trim();
    if (!t) continue;
    if (assignToColumn(cx, colRanges.code)) groups.code.push(t);
    else if (assignToColumn(cx, colRanges.name)) groups.name.push(t);
    else if (assignToColumn(cx, colRanges.color)) groups.color.push(t);
    else if (assignToColumn(cx, colRanges.qty)) groups.qty.push(t);
    else if (assignToColumn(cx, colRanges.price)) groups.price.push(t);
    else if (assignToColumn(cx, colRanges.total)) groups.total.push(t);
    else groups.other.push(t);
  }
  const allText = lineItems.sort((a, b) => a.box.x - b.box.x).map(r => r.text).join(' ');
  let code = '';
  const codeText = groups.code.join(' ');
  const cm = codeText.match(/([A-Za-z]?\d{4,8})/) || allText.match(/([A-Za-z]?\d{4,8})/);
  if (cm) code = normalizeCode(cm[1]);
  let name = '';
  const nameText = groups.name.join(' ');
  const clm = nameText.match(CLOTHING_RE) || allText.match(CLOTHING_RE);
  name = clm ? clm[1] : '';
  let color = '';
  const colorText = groups.color.join(' ');
  const colorM = colorText.match(COLORS_RE) || allText.match(COLORS_RE);
  color = colorM ? colorM[0].replace(/色$/, '') : '';
  let qty = '1';
  const qtyText = groups.qty.join(' ');
  const qtyNum = qtyText.match(/(-?\d+)/);
  if (qtyNum) qty = qtyNum[1];
  let price = '';
  const priceText = groups.price.join(' ');
  const priceNum = priceText.match(/(\d+\.?\d*)/);
  if (priceNum) price = priceNum[1];
  else {
    const totalText = groups.total.join(' ');
    const totalNum = totalText.match(/(\d+\.?\d*)/);
    if (totalNum && qty !== '0' && qty !== '1') {
      const total = parseFloat(totalNum[1]);
      const q = Math.abs(parseInt(qty));
      price = q > 0 ? String(Math.round(total / q)) : totalNum[1];
    } else if (totalNum) { price = totalNum[1]; }
  }
  if (!name && !code) return null;
  return finalizeItem({ code, name, color, qty, purchasePrice: price });
}

function parseLine(raw: string): ParsedItem | null {
  let t = raw;
  t = t.replace(/([\u4e00-\u9fa5])([a-zA-Z0-9¥￥])/g, '$1 $2');
  t = t.replace(/([a-zA-Z0-9¥￥])([\u4e00-\u9fa5])/g, '$1 $2');
  t = t.replace(/\)([a-zA-Z])/g, ') $1');
  t = t.replace(/\s+/g, ' ').trim();
  if (t.length < 2) return null;
  if (EXCLUDE_RE.test(t)) return null;
  if (!/[\u4e00-\u9fa5]/.test(t)) return null;
  const codeMatch = t.match(/\b(\d{5,8})\b/) || t.match(/([A-Za-z]\d{3,6})/) || t.match(/(\d{3}[A-Za-z])/);
  const code = codeMatch ? normalizeCode(codeMatch[1]) : '';
  const clothingMatch = t.match(CLOTHING_RE);
  let name = clothingMatch ? clothingMatch[1] : '';
  if (!name) {
    const m = t.match(/^[\u4e00-\u9fa5]{2,10}/);
    name = m ? m[0] : '';
  }
  if (!name && !code) return null;
  if (name && EXCLUDE_RE.test(name)) return null;
  const colorMatch = t.match(COLORS_RE);
  const color = colorMatch ? colorMatch[0].replace(/色$/, '') : '';
  const numStrs = t.match(/-?\d+\.?\d*/g) || [];
  const nums = numStrs.filter(n => n !== codeRaw(codeMatch)).map(Number);
  const qtyMatch = t.match(/(\d+)\s*件/);
  let qty = '1';
  let price = '';
  if (qtyMatch) {
    qty = qtyMatch[1];
    const afterQty = t.slice(t.indexOf(qtyMatch[0]) + qtyMatch[0].length);
    const pm = afterQty.match(/[¥￥]\s*(\d+\.?\d*)/);
    price = pm ? pm[1] : '';
  } else if (nums.length >= 2) {
    qty = String(nums[0]);
    price = String(Math.abs(nums[1]));
  } else if (nums.length === 1) {
    if (String(nums[0]).includes('.')) { price = String(Math.abs(nums[0])); qty = '1'; }
    else { qty = String(nums[0]); }
  }
  return finalizeItem({ code, name, color, qty, purchasePrice: price });
}

function codeRaw(m: RegExpMatchArray | null): string { return m ? m[0] : ''; }

function parseByLines(text: string): ParsedItem[] {
  const lines = text.split('\n').map(l => l.trim()).filter(l => l.length >= 1);
  const items: ParsedItem[] = [];
  let current: Partial<ParsedItem> = {};
  const pushCurrent = () => {
    if (current.name || current.code || current.retailPrice) { items.push(finalizeItem(current)); current = {}; }
  };
  for (const line of lines) {
    const codeMatch = line.match(/款号[：:\s]*([^\s,，]+)/i) || line.match(/[A-Za-z]{1,4}\d{3,10}/i) || line.match(/^\d{5,12}$/);
    if (codeMatch) { if (current.code && (current.name || current.retailPrice)) pushCurrent(); current.code = (codeMatch[1] || codeMatch[0]).trim(); continue; }
    const priceMatch = line.match(/[¥￥]\s*(\d+(?:\.\d{1,2})?)/) || line.match(/(?:单价|价格|零售|吊牌)[：:\s]*(\d+(?:\.\d{1,2})?)/i);
    if (priceMatch) { const price = parseFloat(priceMatch[1]); if (price > 0 && price < 100000) { if (!current.retailPrice) current.retailPrice = priceMatch[1]; else if (!current.purchasePrice) current.purchasePrice = priceMatch[1]; continue; } }
    const qtyMatch = line.match(/[×xX*]\s*(\d+)/i) || line.match(/(\d+)\s*[件条个双套]/);
    if (qtyMatch) { current.qty = qtyMatch[1]; continue; }
    if (/^(XX?S|S|M|L|XL|XXL|XXXL|\d{1,2})$/i.test(line)) { current.size = line.toUpperCase(); continue; }
    const colorMatch = line.match(/(黑|白|红|蓝|绿|黄|粉|灰|棕|米|驼|藏青|酒红|墨绿|杏|卡其|焦糖|雾霾蓝|奶白|浅灰|深灰|米白|裸粉|豆沙|玫瑰|黑色|白色|红色|蓝色|绿色|黄色|粉色|灰色|棕色)/);
    if (colorMatch && line.length <= 10) { current.color = line; continue; }
    if (/(连衣裙|半身裙|裤|上衣|外套|T恤|衬衫|卫衣|西装|风衣|大衣|毛衣|针织|羽绒|棉服|马甲|背心|牛仔|裙|包|鞋|靴|帽|围巾)/.test(line)) { if (!current.category) current.category = line; continue; }
    if (/^[\d\s\-()+]{8,}$/.test(line)) continue;
    if (/^\d{4}[-/]/.test(line)) continue;
    if (/^\d{1,2}:\d{2}/.test(line)) continue;
    if (/^1[3-9]\d{9}$/.test(line)) continue;
    if (/^[\u4e00-\u9fa5]{1}$/.test(line)) continue;
    if (/(合计|总计|实付|付款|微信|支付宝|现金|刷卡|会员|日期|时间|单号|打印|电话|地址|欢迎|谢谢)/i.test(line)) continue;
    if (!current.name && line.length >= 2 && line.length <= 30) { current.name = line; }
  }
  pushCurrent();
  return items;
}

function normalizeOcrText(text: string): string {
  let r = text;
  r = r.replace(/([\u4e00-\u9fa5])([a-zA-Z0-9¥￥])/g, '$1 $2');
  // Split English/number followed by Chinese: "T恤" → "T恤" (keep together for known items)
  // But split unknown: "ABC中文" → "ABC 中文"
  r = r.replace(/([a-zA-Z]{2,})([\u4e00-\u9fa5])/g, '$1 $2');
  // Single letter + Chinese: keep together (T恤, M码, S号)
  r = r.replace(/\)([a-zA-Z])/g, ') $1');
  // Split merged prices: "56.00129.00" → "56.00 129.00"
  r = r.replace(/(\d+\.\d{2})(\d)/g, '$1 $2');
  r = r.replace(/\s+/g, ' ').trim();
  return r;
}

function parseContinuous(text: string): ParsedItem[] {
  const items: ParsedItem[] = [];
  const cleaned = normalizeOcrText(text);
  const markerIndex = cleaned.search(/(确认签字|千万注意|出货|退货|总计|未付|欠款|地址.*?\d{11}|温馨提示|合计数量|合计金额|收款|找零|本次积分|消费积分|抵扣现金|剩余积分|清零|兑换|积分将|每年.*?清零|在此日期)/);
  const body = markerIndex > 0 ? cleaned.slice(0, markerIndex) : cleaned;
  const EXCLUDE = /^(合计|总计|实付|付款|退货|出货|未付|欠款|地址|电话|微信|温馨提示|累计|运费|确认|收报|打印|投诉|导购|小票编号|会员|本次|消费|抵扣|剩余|千万|退换|出示|所退|未弄|吊牌|货号|描述|品名|序号|分类)/;
  const COLORS = /(混色|混他|纯色|黑色|白色|红色|蓝色|绿色|黄色|粉色|灰色|棕色|米色|驼色|杏色|卡其|藏青|酒红|墨绿|焦糖|奶白|浅灰|深灰|米白|裸粉|豆沙|玫瑰|[\u4e00-\u9fa5]{1,3}色)/;
  const CLOTHING = /(防晒裤|西装裤|工装裤|休闲裤|牛仔裤|九分裤|七分裤|运动裤|打底裤|阔腿裤|直筒裤|铅笔裤|小脚裤|哈伦裤|背带裤|设计款|吊带蛋糕裙|连衣裙|半身裙|百褶裙|A字裙|鱼尾裙|蓬蓬裙|公主裙|背带裙|吊带裙|吊带|上衣|衬衫|衬衣|卫衣|毛衣|针织衫|开衫|马甲|背心|夹克|外套|大衣|风衣|棉服|羽绒服|皮衣|西服|西装|裤子|长裤|短裤|西裤|裙子|短裙|迷你裙|半裙|蛋糕裙|长袖|套装|T恤|T|两件套|三件套|衣|裤|裙)/;

  // Fix: compact spaces between letters and Chinese chars, and remove stray single letters
  const compactBody = body
    .replace(/([a-zA-Z])\s+([\u4e00-\u9fa5])/g, '$1$2')
    .replace(/([\u4e00-\u9fa5])\s+([a-zA-Z])/g, '$1$2')
    .replace(/i(?=\d)/g, '')  // Remove stray 'i' before numbers: 休闲裤i1 → 休闲裤1
    .replace(/数量\s*/g, '')  // Remove 数量 prefix: 美背吊带 数量 1 → 美背吊带 1
    .replace(/总价\s*/g, '')  // Remove 总价 prefix: 总价45.00 → 45.00
    // Handle ×NNN patterns: "长裤×1139" → "长裤 ×1 139" (split qty+price)
    .replace(/([\u4e00-\u9fa5])([×xX])/g, '$1 $2')  // Add space before ×: 长裤× → 长裤 ×
    .replace(/(\d+)\s*[件条个双套]/g, ' ×$1 ')  // "2件" → "×2 " for Strategy 0c
    .replace(/[¥￥]\s*/g, '')  // Remove ¥ symbol: ¥45.00 → 45.00
    .replace(/(混色|混他|纯色|黑色|白色|红色|蓝色|绿色|黄色|粉色|灰色|棕色|米色|驼色|杏色|卡其|藏青|酒红|墨绿|焦糖|奶白|浅灰|深灰|米白|裸粉|豆沙|玫瑰|[\u4e00-\u9fa5]{1,3}色)\s*[XSML]{0,4}\s*/g, ' ')  // Remove color+size: 白色M → space
    .replace(/[×xX]\s*(\d{4,})/g, (_, digits: string) => {
      // 4+ digits: first digit is qty, rest is price (e.g., ×1139 → ×1 139)
      return `×1 ${digits.slice(1)}`;
    })
    .replace(/[×xX]\s*(\d{1,3})(?!\s+\d)/g, ' ×1 $1')  // 1-3 digits NOT followed by space+digit (×69 → ×1 69, but ×1 139 untouched)
    .replace(/\s+/g, ' ');

  // Strategy 0: name + qty + price - handles "T恤1 39.00", "外套 1 59.00", "正常T 1 48.00"
  // Also handles "卫衣 49.00" (no qty, just price with decimal)
  const NON_ITEM = '品名|总价|合计|收款|找零|打印|投诉|会员|地址|导购';
  const NAME_PAT = '(?!' + NON_ITEM + ')([A-Za-z][\\u4e00-\\u9fa5]{1,7}|[\\u4e00-\\u9fa5]{1,7}[A-Za-z]|[\\u4e00-\\u9fa5]{2,8})';
  // qty is optional; price must have decimal to distinguish from qty
  const nameQtyPrice = new RegExp(NAME_PAT + '\\s*(?:(\\d{1,2})\\s+)?(\\d+\\.\\d{2})', 'g');
  let nqp;
  while ((nqp = nameQtyPrice.exec(compactBody)) !== null) {
    const [, rawName, qty, price] = nqp;
    const clothingMatch = rawName.match(CLOTHING);
    if (!clothingMatch) continue;
    const name = rawName;
    if (EXCLUDE.test(name)) continue;
    const p = parseFloat(price);
    if (p > 0 && p < 10000) items.push(finalizeItem({ name, qty: qty || '1', purchasePrice: String(p) }));
  }
  if (items.length > 0) return items;

  // Strategy 0b: name + integer price (no qty, qty=1) - handles "规版衬衣85" "卫衣 49"
  const namePrice = new RegExp(NAME_PAT + '\\s+(\\d{1,3})(?!\\.\\d)', 'g');
  let np;
  while ((np = namePrice.exec(compactBody)) !== null) {
    const [, rawName, price] = np;
    const clothingMatch = rawName.match(CLOTHING);
    if (!clothingMatch) continue;
    const name = rawName;
    if (EXCLUDE.test(name)) continue;
    if (items.some(i => i.name === name)) continue;
    const p = parseFloat(price);
    if (p > 0 && p < 10000) items.push(finalizeItem({ name, qty: '1', purchasePrice: String(p) }));
  }
  if (items.length > 0) return items;

  // Strategy 0c: name × qty price - handles "长裤 ×1 139" or "女装上衣 ×2 45.00"
  const nameQtyPrice3 = new RegExp(NAME_PAT + '\\s*×\\s*(\\d{1,2})\\s+(\\d+\\.?\\d*)', 'g');
  let nqp3;
  while ((nqp3 = nameQtyPrice3.exec(compactBody)) !== null) {
    const [, rawName, qty, price] = nqp3;
    const clothingMatch = rawName.match(CLOTHING);
    if (!clothingMatch) continue;
    const name = rawName;
    if (EXCLUDE.test(name)) continue;
    const p = parseFloat(price);
    if (p > 0 && p < 10000) items.push(finalizeItem({ name, qty: qty || '1', purchasePrice: String(p) }));
  }
  if (items.length > 0) return items;

  const qtyRegex = /(\d+)\s*件/g;
  let qm;
  while ((qm = qtyRegex.exec(body)) !== null) {
    const qty = qm[1]; const pos = qm.index; const len = qm[0].length;
    const chunkStart = Math.max(0, pos - 100); const chunk = body.slice(chunkStart, pos + len);
    const codeMatch = chunk.match(/(\d{5,8})(?=[\s\u4e00-\u9fa5])/);
    if (!codeMatch || codeMatch.index === undefined) continue;
    const code = codeMatch[1]; const codeEnd = chunkStart + codeMatch.index + code.length;
    const nameArea = body.slice(codeEnd, pos);
    const nameMatch = nameArea.match(/^([\u4e00-\u9fa5]{2,15}?)(?=\s*(混色|混他|纯色|黑色|白色|红色|蓝色|绿色|黄色|粉色|灰色|棕色|米色|驼色|杏色|卡其|藏青|酒红|墨绿|焦糖|奶白|浅灰|深灰|米白|裸粉|豆沙|玫瑰|[\u4e00-\u9fa5]{1,3}色|XXL|XS|S|M|L|XL|均码|\d))/);
    const name = nameMatch ? nameMatch[1] : nameArea.match(/^[\u4e00-\u9fa5]{2,15}/)?.[0] || '';
    if (!name || EXCLUDE.test(name)) continue;
    const afterName = nameArea.slice(name.length);
    const colorMatch = afterName.match(COLORS);
    const color = colorMatch ? colorMatch[0].replace(/色$/, '') : '';
    const sizeMatch = nameArea.match(/(XXL|XS|S|M|L|XL|均码)/i);
    const size = sizeMatch ? sizeMatch[1] : '';
    const afterQty = body.slice(pos + len, pos + len + 40);
    const priceMatch = afterQty.match(/[¥￥]\s*(\d+\.?\d*)/);
    const price = priceMatch ? priceMatch[1] : '';
    items.push(finalizeItem({ code, name, color, size, qty, purchasePrice: price }));
  }
  if (items.length > 0) return items;

  const seqRegex = /\d+\)\s*([A-Za-z0-9]+)\s+([\u4e00-\u9fa5]{2,6})\s+([\u4e00-\u9fa5]{2,4})\s+(\d+)\s+(\d+)\s+(\d+)/g;
  let sm;
  while ((sm = seqRegex.exec(body)) !== null) {
    const [, code, category, color, price, qty] = sm;
    const c = code.trim();
    if (EXCLUDE.test(c) || EXCLUDE.test(category)) continue;
    items.push(finalizeItem({ code: c, name: category, color, qty, purchasePrice: price }));
  }
  if (items.length > 0) return items;

  const codeRegex = /(?:[A-Za-z]\d{3,6}|\d{4,7}|\d{3}[A-Za-z])\s*(?=[\u4e00-\u9fa5])/g;
  const codes: { code: string; start: number; end: number }[] = [];
  let m;
  while ((m = codeRegex.exec(body)) !== null) { codes.push({ code: m[0].trim(), start: m.index, end: m.index + m[0].length }); }
  for (let i = 0; i < codes.length; i++) {
    const { code, end } = codes[i]; const chunkEnd = i + 1 < codes.length ? codes[i + 1].start : body.length; const chunk = body.slice(end, chunkEnd);
    let name = '';
    const clothingMatch = chunk.match(CLOTHING);
    if (clothingMatch) { name = clothingMatch[1]; } else { const colorIdx = chunk.search(COLORS); if (colorIdx > 0) { name = chunk.slice(0, colorIdx).trim(); } else { name = chunk.match(/^[\u4e00-\u9fa5]{2,20}/)?.[0] || ''; } }
    if (!name || EXCLUDE.test(name)) continue;
    const colorMatch = chunk.match(COLORS); const color = colorMatch ? colorMatch[0].replace(/色$/, '') : '';
    const nums = chunk.match(/-?\d+/g)?.map(Number) || [];
    let qty = '1'; let price = '';
    if (nums.length >= 2) { qty = String(nums[0]); price = String(Math.abs(nums[1])); } else if (nums.length === 1) { qty = String(nums[0]); }
    items.push(finalizeItem({ code, name, color, qty, purchasePrice: price }));
  }
  if (items.length > 0) return items;

  const simpleRegex = /([\u4e00-\u9fa5]{2,6})\s+(\d+)\s+(\d+\.?\d*)/g;
  let cm;
  while ((cm = simpleRegex.exec(body)) !== null) {
    const [, name, qty, price] = cm;
    if (EXCLUDE.test(name)) continue; if (Number(price) > 10000) continue; if (/\d{4}[-/]\d{2}/.test(name)) continue;
    items.push(finalizeItem({ name, qty, purchasePrice: price }));
  }
  return items;
}

function normalizeCode(code: string): string { return code.replace(/[oO]/g, '0'); }

function finalizeItem(raw: Partial<ParsedItem>): ParsedItem {
  return { id: genId('item'), name: raw.name || '', code: normalizeCode(raw.code || ''), category: raw.category || '', color: raw.color || '', size: raw.size || '', retailPrice: raw.retailPrice || '', purchasePrice: raw.purchasePrice || '', qty: raw.qty || '1', selected: true };
}

const SCREEN_WIDTH = Dimensions.get('window').width;

function SwipeableItem({ item, index, onToggle, onUpdate, onDelete, tc }: {
  item: ParsedItem; index: number; onToggle: (i: number) => void;
  onUpdate: (i: number, field: keyof ParsedItem, value: string) => void;
  onDelete: (i: number) => void; tc: any;
}) {
  const [expanded, setExpanded] = useState(false);
  const [showDeleteBtn, setShowDeleteBtn] = useState(false);

  const handleDelete = () => {
    onDelete(index);
  };

  return (
    <View style={[styles.itemCard, { backgroundColor: tc.card, borderColor: tc.border, marginBottom: 8 }]}>
      <TouchableOpacity
        style={styles.itemHeader}
        onPress={() => setExpanded(!expanded)}
        onLongPress={() => setShowDeleteBtn(!showDeleteBtn)}
        activeOpacity={0.7}
      >
        <Switch value={item.selected} onValueChange={() => onToggle(index)} trackColor={{ true: tc.primary }} />
        <View style={styles.itemInfo}>
          <Text style={[styles.itemName, { color: tc.text }]} numberOfLines={1}>{item.name || '未识别'}</Text>
          <Text style={[styles.itemMeta, { color: tc.subText }]}>
            {item.code ? `款号: ${item.code}` : ''}
            {item.purchasePrice ? ` 进¥${item.purchasePrice}` : item.retailPrice ? ` ¥${item.retailPrice}` : ''}
            {item.qty !== '1' ? ` ×${item.qty}` : ''}
          </Text>
        </View>
        <Text style={[styles.expandIcon, { color: tc.subText }]}>{expanded ? '▲' : '▼'}</Text>
      </TouchableOpacity>
      {expanded && item.selected && (
        <View style={styles.itemForm}>
          {[['名称', 'name'], ['款号', 'code'], ['分类', 'category'], ['颜色', 'color'], ['尺码', 'size'], ['零售价', 'retailPrice'], ['进货价', 'purchasePrice'], ['数量', 'qty']].map(([label, field]) => (
            <View key={field} style={styles.formRow}>
              <Text style={[styles.formLabel, { color: tc.subText }]}>{label}</Text>
              <TextInput style={[styles.formInput, { borderColor: tc.border, color: tc.text }]} value={String(item[field as keyof ParsedItem] ?? '')} onChangeText={(v) => onUpdate(index, field as keyof ParsedItem, v)} keyboardType={['零售价', '进货价', '数量'].includes(field as string) ? 'numeric' : 'default'} />
            </View>
          ))}
        </View>
      )}
      {showDeleteBtn && (
        <TouchableOpacity style={[styles.deleteInline, { backgroundColor: '#FF4444' }]} onPress={handleDelete}>
          <Text style={styles.deleteInlineText}>删除此商品</Text>
        </TouchableOpacity>
      )}
    </View>
  );
}

export default function OcrScreen() {
  const { status, result, error, recognize } = useOcr();
  const { addProducts, currentStoreId, markupPercent, theme } = useAppStore();
  const tc = THEMES[theme];
  const [imageUri, setImageUri] = useState<string | null>(null);
  const [parsedItems, setParsedItems] = useState<ParsedItem[]>([]);
  const [expandedIndex, setExpandedIndex] = useState<number | null>(null);
  const [rawText, setRawText] = useState<string>('');

  useEffect(() => {
    if (result?.text) {
      logInfo('OCR', `原始文本(${result.text.length}字符): ${result.text.slice(0, 200)}`);
      setRawText(result.text);
      const items = parseReceiptItems(result.text, result.items);
      logInfo('OCR', `解析出 ${items.length} 款商品`);
      setParsedItems(items);
    }
  }, [result]);

  const pickImage = async (useCamera: boolean) => {
    try {
      logInfo('OCR', `pickImage: useCamera=${useCamera}`);
      const permResult = useCamera ? await ImagePicker.requestCameraPermissionsAsync() : await ImagePicker.requestMediaLibraryPermissionsAsync();
      if (!permResult.granted) { Alert.alert('权限不足', useCamera ? '请允许使用相机' : '请允许访问相册'); return; }
      const res = useCamera ? await ImagePicker.launchCameraAsync({ quality: 0.8 }) : await ImagePicker.launchImageLibraryAsync({ quality: 0.8 });
      if (!res.canceled && res.assets[0]) {
        setImageUri(res.assets[0].uri);
        logInfo('OCR', `图片已选择: ${res.assets[0].uri}`);
        const response = await fetch(res.assets[0].uri);
        const buffer = await response.arrayBuffer();
        logInfo('OCR', `图片大小: ${buffer.byteLength} bytes, 开始识别`);
        await recognize(buffer, 60000);
        setImageUri(null);
      }
    } catch (err) {
      logError('OCR', `pickImage 失败: ${err instanceof Error ? err.message : String(err)}`);
      Alert.alert('错误', err instanceof Error ? err.message : String(err));
    }
  };

  const toggleItem = (index: number) => { setParsedItems(prev => prev.map((item, i) => i === index ? { ...item, selected: !item.selected } : item)); };
  const updateItem = (index: number, field: keyof ParsedItem, value: string) => { setParsedItems(prev => prev.map((item, i) => i === index ? { ...item, [field]: value } : item)); };
  const deleteItem = (index: number) => { setParsedItems(prev => prev.filter((_, i) => i !== index)); };

  const handleSave = () => {
    const selected = parsedItems.filter(item => item.selected && item.name.trim());
    if (selected.length === 0) { Alert.alert('提示', '没有可入库的商品'); return; }
    try {
      const { products: existing } = useAppStore.getState();
      const existingCodes = new Set(existing.filter(p => p.storeId === currentStoreId).map(p => p.code));
      
      // 检查重复款号
      const duplicateItems: string[] = [];
      const newItems: typeof selected = [];
      
      for (const item of selected) {
        const code = item.code.trim();
        if (code && existingCodes.has(code)) {
          duplicateItems.push(`${item.name} (${code})`);
        } else {
          newItems.push(item);
        }
      }
      
      // 如果有重复款号，提示用户
      if (duplicateItems.length > 0) {
        Alert.alert(
          '发现重复款号',
          `以下商品款号已存在：\n${duplicateItems.join('\n')}\n\n将累加库存，而非创建新品。`,
          [
            { text: '取消', style: 'cancel' },
            { 
              text: '继续入库', 
              onPress: () => {
                // 先按款号聚合本批数量
                const qtyByCode = new Map<string, number>();
                for (const item of selected) {
                  const code = item.code.trim();
                  if (code && existingCodes.has(code)) {
                    qtyByCode.set(code, (qtyByCode.get(code) || 0) + (Number(item.qty) || 1));
                  }
                }
                // 再逐个用「当前最新」的库存值累加
                for (const [code, qty] of qtyByCode) {
                  const latest = useAppStore.getState().products
                    .find(p => p.code === code && (p.storeId === currentStoreId || !p.storeId));
                  if (latest) updateProduct(latest.id, { stock: latest.stock + qty });
                }
                // 入库新品
                if (newItems.length > 0) {
                  const products = newItems.map(item => {
                    const purchasePrice = Number(item.purchasePrice) || 0;
                    const retailPrice = Number(item.retailPrice) || (purchasePrice > 0 && markupPercent > 0 ? Math.round(purchasePrice * (1 + markupPercent / 100)) : 0);
                    return { id: genId('p'), storeId: currentStoreId, name: item.name.trim(), code: item.code.trim() || genBarcode(), category: item.category.trim() || '未分类', retailPrice, purchasePrice, stock: Number(item.qty) || 1, warningStock: 5, isHot: false, unit: '件', createdAt: new Date().toISOString() };
                  });
                  addProducts(products);
                }
                Alert.alert('成功', `已累加 ${qtyByCode.size} 件重复商品库存，新增 ${newItems.length} 件商品`);
                setParsedItems([]);
                setImageUri(null);
              }
            },
          ]
        );
        return;
      }
      
      // 无重复款号，直接入库
      const products = selected.map(item => {
        const purchasePrice = Number(item.purchasePrice) || 0;
        const retailPrice = Number(item.retailPrice) || (purchasePrice > 0 && markupPercent > 0 ? Math.round(purchasePrice * (1 + markupPercent / 100)) : 0);
        return { id: genId('p'), storeId: currentStoreId, name: item.name.trim(), code: item.code.trim() || genBarcode(), category: item.category.trim() || '未分类', retailPrice, purchasePrice, stock: Number(item.qty) || 1, warningStock: 5, isHot: false, unit: '件', createdAt: new Date().toISOString() };
      });
      logInfo('OCR', `入库 ${products.length} 件`);
      addProducts(products);
      Alert.alert('成功', `已入库 ${selected.length} 件商品`);
      setParsedItems([]);
      setImageUri(null);
    } catch (err) { Alert.alert('错误', err instanceof Error ? err.message : '入库失败'); }
  };

  return (
    <FlatList
      style={[styles.container, { backgroundColor: tc.bg }]}
      data={parsedItems}
      keyExtractor={(item) => item.id}
      ListHeaderComponent={
        <>
          <View style={styles.actionRow}>
            <TouchableOpacity 
              style={[styles.actionBtn, { backgroundColor: tc.primary, opacity: (status === 'running' || status === 'initializing') ? 0.5 : 1 }]} 
              onPress={() => pickImage(true)}
              disabled={status === 'running' || status === 'initializing'}
            >
              <Text style={styles.actionIcon}>📷</Text>
              <Text style={[styles.actionText, { color: '#fff' }]}>拍照入库</Text>
            </TouchableOpacity>
            <TouchableOpacity 
              style={[styles.actionBtn, styles.actionBtnSecondary, { backgroundColor: tc.primaryLight, borderColor: tc.primary, opacity: (status === 'running' || status === 'initializing') ? 0.5 : 1 }]} 
              onPress={() => pickImage(false)}
              disabled={status === 'running' || status === 'initializing'}
            >
              <Text style={[styles.actionIcon, { color: tc.primary }]}>🖼️</Text>
              <Text style={[styles.actionText, { color: tc.primary }]}>相册选择</Text>
            </TouchableOpacity>
          </View>
          {imageUri && <View style={[styles.previewCard, { backgroundColor: tc.card }]}><Image source={{ uri: imageUri }} style={styles.preview} resizeMode="contain" /></View>}
          {status === 'initializing' && <View style={[styles.statusCard, { backgroundColor: tc.card }]}><ActivityIndicator size="small" color={tc.primary} /><Text style={[styles.statusText, { color: tc.subText }]}>模型加载中...</Text></View>}
          {status === 'running' && <View style={[styles.statusCard, { backgroundColor: tc.card }]}><ActivityIndicator size="small" color={tc.primary} /><Text style={[styles.statusText, { color: tc.subText }]}>识别中...</Text></View>}
          {error && <View style={styles.errorCard}><Text style={styles.errorText}>{error}</Text></View>}
          {result && parsedItems.length === 0 && !error && (
            <View style={styles.rawCard}>
              <Text style={styles.rawTitle}>OCR原始文本（未自动解析到商品）</Text>
              <Text style={styles.rawText} selectable>{rawText}</Text>
              <TouchableOpacity style={[styles.parseBtn, { backgroundColor: tc.primary }]} onPress={() => { const items = parseReceiptItems(rawText); if (items.length > 0) setParsedItems(items); else Alert.alert('提示', '仍未解析到商品，请检查图片内容'); }}>
                <Text style={styles.parseBtnText}>重新解析</Text>
              </TouchableOpacity>
            </View>
          )}
          {!imageUri && status === 'ready' && parsedItems.length === 0 && (
            <View style={[styles.hintCard, { backgroundColor: tc.primaryLight }]}>
              <Text style={[styles.hintText, { color: tc.primary }]}>拍摄小票或标签快速入库</Text>
              <Text style={[styles.hintSubtext, { color: tc.subText }]}>支持小票批量解析 · 自动识别款号、价格、数量</Text>
            </View>
          )}
          {parsedItems.length > 0 && (
            <View style={styles.resultHeader}>
              <Text style={[styles.resultTitle, { color: tc.text }]}>识别到 {parsedItems.length} 款商品 · 长按删除</Text>
              <View style={styles.resultBtns}>
                <TouchableOpacity style={[styles.selectBtn, { backgroundColor: tc.border }]} onPress={() => { const allSelected = parsedItems.every(i => i.selected); setParsedItems(prev => prev.map(i => ({ ...i, selected: !allSelected }))); }}>
                  <Text style={[styles.selectBtnText, { color: tc.subText }]}>{parsedItems.every(i => i.selected) ? '取消全选' : '全选'}</Text>
                </TouchableOpacity>
                <TouchableOpacity style={[styles.saveBtn, { backgroundColor: tc.primary }]} onPress={handleSave}>
                  <Text style={styles.saveBtnText}>入库 ({parsedItems.filter(i => i.selected).length})</Text>
                </TouchableOpacity>
              </View>
            </View>
          )}
          {result && parsedItems.length > 0 && rawText.length > 0 && (
            <View style={[styles.rawCard, { backgroundColor: tc.card }]}>
              <TouchableOpacity onPress={() => setExpandedIndex(expandedIndex === -99 ? null : -99)}>
                <Text style={[styles.rawTitle, { color: tc.primary }]}>{expandedIndex === -99 ? '▼' : '▶'} OCR原始文本 ({rawText.length}字符)</Text>
              </TouchableOpacity>
              {expandedIndex === -99 && <Text style={[styles.rawText, { color: tc.subText }]} selectable>{rawText}</Text>}
            </View>
          )}
        </>
      }
      renderItem={({ item, index }) => (
        <SwipeableItem item={item} index={index} onToggle={toggleItem} onUpdate={updateItem} onDelete={deleteItem} tc={tc} />
      )}
      contentContainerStyle={{ paddingBottom: 40 }}
    />
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 16 },
  actionRow: { flexDirection: 'row', gap: 12, marginBottom: 16 },
  actionBtn: { flex: 1, borderRadius: 12, padding: 20, alignItems: 'center', justifyContent: 'center' },
  actionBtnSecondary: { borderWidth: 1 },
  actionIcon: { fontSize: 32, marginBottom: 8 },
  actionText: { fontSize: 15, fontWeight: '600' },
  previewCard: { borderRadius: 12, padding: 8, marginBottom: 16, overflow: 'hidden' },
  preview: { width: '100%', height: 200, borderRadius: 8 },
  statusCard: { borderRadius: 12, padding: 16, marginBottom: 16, flexDirection: 'row', alignItems: 'center', gap: 12 },
  statusText: { fontSize: 14 },
  errorCard: { backgroundColor: '#FFF0F0', borderRadius: 12, padding: 16, marginBottom: 16 },
  errorText: { fontSize: 14, color: '#D32F2F' },
  hintCard: { borderRadius: 12, padding: 24, marginTop: 40, alignItems: 'center' },
  hintText: { fontSize: 16, fontWeight: '600', marginBottom: 8 },
  hintSubtext: { fontSize: 13, textAlign: 'center' },
  resultHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 },
  resultTitle: { fontSize: 14, fontWeight: '600' },
  resultBtns: { flexDirection: 'row', gap: 8 },
  selectBtn: { paddingHorizontal: 12, paddingVertical: 6, borderRadius: 6 },
  selectBtnText: { fontSize: 13 },
  saveBtn: { paddingHorizontal: 16, paddingVertical: 6, borderRadius: 6 },
  saveBtnText: { fontSize: 13, color: '#fff', fontWeight: '600' },
  itemCard: { borderRadius: 10, borderWidth: 1 },
  itemHeader: { flexDirection: 'row', alignItems: 'center', padding: 12, gap: 10 },
  itemInfo: { flex: 1 },
  itemName: { fontSize: 15, fontWeight: '600', marginBottom: 2 },
  itemMeta: { fontSize: 12 },
  expandIcon: { fontSize: 12 },
  itemForm: { padding: 12, paddingTop: 0, gap: 6 },
  formRow: { flexDirection: 'row', alignItems: 'center', gap: 8 },
  formLabel: { fontSize: 12, width: 45 },
  formInput: { flex: 1, borderWidth: 1, borderRadius: 6, padding: 8, fontSize: 13 },
  deleteBg: { position: 'absolute', right: 0, top: 0, bottom: 0, backgroundColor: '#FF4444', borderRadius: 10, justifyContent: 'center', alignItems: 'center' },
  deleteText: { color: '#fff', fontSize: 14, fontWeight: '600' },
  deleteInline: { marginTop: 6, marginHorizontal: 12, marginBottom: 8, borderRadius: 8, padding: 10, alignItems: 'center' },
  deleteInlineText: { color: '#fff', fontSize: 13, fontWeight: '600' },
  rawCard: { borderRadius: 12, padding: 16, marginBottom: 16 },
  rawTitle: { fontSize: 14, fontWeight: '600', marginBottom: 8 },
  rawText: { fontSize: 12, lineHeight: 18, maxHeight: 200 },
  parseBtn: { marginTop: 12, borderRadius: 8, padding: 10, alignItems: 'center' },
  parseBtnText: { color: '#fff', fontSize: 13, fontWeight: '600' },
});
