import { Skia, PaintStyle, TextAlign, type SkCanvas } from '@shopify/react-native-skia';
import { toQR } from 'toqr';
import { encodeCode128, type Code128Result } from './Barcode128';
import {
  LABEL_PRESETS,
  fieldValue,
  type LabelConfig,
  type LabelData,
  type LabelElement,
} from './PrinterServiceTypes';

const DPI = 200;
const PX_PER_MM = DPI / 25.4;

const mmToPx = (mm: number) => Math.round(mm * PX_PER_MM);

export interface LabelBitmap {
  widthPx: number;
  heightPx: number;
  widthBytes: number;
  mono: Uint8Array;
}

function drawText(
  canvas: SkCanvas,
  text: string,
  x: number,
  y: number,
  maxWidth: number,
  fontSize: number,
  bold: boolean,
  align: TextAlign = TextAlign.Left,
): void {
  if (!text) return;
  const width = Math.max(1, Math.floor(maxWidth));
  const pb = Skia.ParagraphBuilder.Make({
    textAlign: align,
  });
  pb.pushStyle({
    color: Skia.Color('black'),
    fontFamilies: ['sans-serif'],
    fontSize,
    fontStyle: { weight: bold ? 700 : 400 },
  });
  pb.addText(text);
  const para = pb.build();
  para.layout(width);
  para.paint(canvas, x, y);
}

function drawBarcode(canvas: SkCanvas, value: string, x: number, y: number, wPx: number, hPx: number) {
  if (!value || wPx < 10) return;
  const res = encodeCode128(value);
  const totalModules = res.patterns.reduce((s, p) => s + p.length, 0);
  const quiet = Math.max(1, Math.round(wPx * 0.04));
  const usable = wPx - quiet * 2;
  const modulePx = Math.max(1, Math.floor(usable / totalModules));
  if (modulePx < 1 || totalModules * modulePx > usable) return;
  let bx = x + quiet;
  const barY = y;
  for (const pattern of res.patterns) {
    for (const ch of pattern) {
      if (ch === '1') {
        const paint = Skia.Paint();
        paint.setColor(Skia.Color('black'));
        canvas.drawRect(Skia.XYWHRect(bx, barY, modulePx, hPx), paint);
      }
      bx += modulePx;
    }
  }
  const labelSize = Math.max(9, Math.round(hPx * 0.28));
  drawText(canvas, value, x, y + hPx + 2, wPx, labelSize, false, TextAlign.Center);
}

function drawQrcode(canvas: SkCanvas, value: string, x: number, y: number, wPx: number, hPx: number) {
  if (!value) return;
  const qr = toQR(String(value));
  const dim = Math.round(Math.sqrt(qr.length));
  if (dim < 1) return;
  const sizePx = Math.min(wPx, hPx);
  const scale = Math.max(1, Math.floor((sizePx - 4) / dim));
  const actual = dim * scale;
  const qx = x + Math.round((wPx - actual) / 2);
  const qy = y + Math.round((hPx - actual) / 2);
  for (let ry = 0; ry < dim; ry++) {
    for (let rx = 0; rx < dim; rx++) {
      if (qr[ry * dim + rx] === 1) {
        const paint = Skia.Paint();
        paint.setColor(Skia.Color('black'));
        canvas.drawRect(Skia.XYWHRect(qx + rx * scale, qy + ry * scale, scale, scale), paint);
      }
    }
  }
}

function drawLine(canvas: SkCanvas, el: LabelElement, x: number, y: number, wPx: number, hPx: number) {
  const paint = Skia.Paint();
  paint.setColor(Skia.Color('black'));
  paint.setStrokeWidth(Math.max(1, Math.round((el.thicknessMm || 0.4) * PX_PER_MM)));
  const midY = y + Math.round(hPx / 2);
  canvas.drawLine(x, midY, x + wPx, midY, paint);
}

function drawRect(canvas: SkCanvas, el: LabelElement, x: number, y: number, wPx: number, hPx: number) {
  const paint = Skia.Paint();
  paint.setStyle(PaintStyle.Stroke);
  paint.setColor(Skia.Color('black'));
  paint.setStrokeWidth(Math.max(1, Math.round((el.thicknessMm || 0.4) * PX_PER_MM)));
  canvas.drawRect(Skia.XYWHRect(x, y, wPx, hPx), paint);
}

/**
 * 渲染标签为 200DPI 位图并转换为 1-bit 单色数据（匹配官方 GetImageDataRasterMono 打包）：
 * 每像素 1 位，MSB-first，0=白 1=黑。
 */
export async function renderLabelBitmap(data: LabelData, config: LabelConfig): Promise<LabelBitmap | null> {
  const { w, h } = LABEL_PRESETS[config.size];
  const widthPx = mmToPx(w);
  const heightPx = mmToPx(h);
  const widthBytes = Math.ceil(widthPx / 8);

  const surface = Skia.Surface.Make(widthPx, heightPx);
  if (!surface) return null;
  const canvas = surface.getCanvas();

  canvas.clear(Skia.Color('white'));

  const els = config.elements || [];
  for (const el of els) {
    const x = Math.round((el.x + (el.offsetX || 0)) * PX_PER_MM);
    const y = Math.round((el.y + (el.offsetY || 0)) * PX_PER_MM);
    const wPx = Math.max(1, Math.round(el.w * PX_PER_MM));
    const hPx = Math.max(1, Math.round(el.h * PX_PER_MM));

    switch (el.type) {
      case 'text': {
        const value = el.fieldKey ? fieldValue(data, el.fieldKey) : (el.text || '');
        const fontSize = Math.max(6, Math.round((el.fontSizeMm || 4) * PX_PER_MM));
        const align = el.align === 'center' ? TextAlign.Center : el.align === 'right' ? TextAlign.Right : TextAlign.Left;
        drawText(canvas, value, x, y, wPx, fontSize, !!el.bold, align);
        break;
      }
      case 'barcode': {
        const value = el.fieldKey ? fieldValue(data, el.fieldKey) : (el.text || '');
        drawBarcode(canvas, value, x, y, wPx, hPx);
        break;
      }
      case 'qrcode': {
        const value = el.fieldKey ? fieldValue(data, el.fieldKey) : (el.text || '');
        drawQrcode(canvas, value, x, y, wPx, hPx);
        break;
      }
      case 'line':
        drawLine(canvas, el, x, y, wPx, hPx);
        break;
      case 'rect':
        drawRect(canvas, el, x, y, wPx, hPx);
        break;
    }
  }

  const image = surface.makeImageSnapshot();
  const pixels = image.readPixels(0, 0, {
    width: widthPx,
    height: heightPx,
    alphaType: 3, // Unpremul
    colorType: 4, // RGBA_8888
  });

  if (!pixels) {
    return null;
  }

  // RGBA -> 1-bit mono, MSB-first，黑色(gray<=128) 记为 1
  const mono = new Uint8Array(widthBytes * heightPx);
  let byteIdx = 0;
  for (let row = 0; row < heightPx; row++) {
    let bit = 0;
    for (let col = 0; col < widthPx; col++) {
      const idx = (row * widthPx + col) * 4;
      const r = pixels[idx];
      const g = pixels[idx + 1];
      const b = pixels[idx + 2];
      const gray = r * 0.29891 + g * 0.58661 + b * 0.11448;
      if (gray <= 128) bit |= 128 >> (col % 8);
      if ((col + 1) % 8 === 0 || col === widthPx - 1) {
        mono[byteIdx++] = bit;
        bit = 0;
      }
    }
  }

  return { widthPx, heightPx, widthBytes, mono };
}

/**
 * 组装 CPCL 打印指令（匹配官方 printBitmapCPCL）：
 *   ! 0 200 200 {heightPx} {copies}\r\n
 *   TONE {density}\r\n              (可选)
 *   CG {widthBytes} {heightPx} {x} {y} {mono data}
 *   FORM\r\n
 *   PRINT\r\n
 */
export function buildCPCLLabel(
  bitmap: LabelBitmap,
  copies = 1,
  density = 55,
): Uint8Array {
  const header = new TextEncoder().encode(`! 0 200 200 ${bitmap.heightPx} ${copies}\r\n`);
  const tone = new TextEncoder().encode(`TONE ${density}\r\n`);
  const cgHead = new TextEncoder().encode(`CG ${bitmap.widthBytes} ${bitmap.heightPx} 0 0 `);
  const form = new TextEncoder().encode('FORM\r\nPRINT\r\n');

  const total = header.length + tone.length + cgHead.length + bitmap.mono.length + form.length;
  const out = new Uint8Array(total);
  let off = 0;
  out.set(header, off); off += header.length;
  out.set(tone, off); off += tone.length;
  out.set(cgHead, off); off += cgHead.length;
  out.set(bitmap.mono, off); off += bitmap.mono.length;
  out.set(form, off); off += form.length;
  return out;
}

/**
 * 组装 ESC/POS 标签指令（匹配官方 ESC_POLI 打印路径）：
 *   ESC @              1b 40                     初始化
 *   GS v 0 m=0         1d 76 30 00              位图命令
 *   widthBytes(2B LE) heightPx(2B LE)           尺寸
 *   {mono 位图数据}                               1bpp 黑=1
 *   GS f 960           1d 66 c0 03              走纸到标签间隙
 */
export function buildESCPOLILabel(
  bitmap: LabelBitmap,
): Uint8Array {
  const wLo = bitmap.widthBytes & 0xff;
  const wHi = (bitmap.widthBytes >> 8) & 0xff;
  const hLo = bitmap.heightPx & 0xff;
  const hHi = (bitmap.heightPx >> 8) & 0xff;

  const total = 4 + 4 + 4 + bitmap.mono.length + 4;
  const out = new Uint8Array(total);
  let off = 0;
  out.set([0x1b, 0x40], off); off += 2;                       // ESC @
  out.set([0x1d, 0x76, 0x30, 0x00], off); off += 4;            // GS v 0 m=0
  out.set([wLo, wHi, hLo, hHi], off); off += 4;               // WIDTH x HEIGHT
  out.set(bitmap.mono, off); off += bitmap.mono.length;       // 位图数据
  out.set([0x1d, 0x66, 0xc0, 0x03], off); off += 4;           // GS f 960 走纸
  return out;
}

export { encodeCode128, type Code128Result };
