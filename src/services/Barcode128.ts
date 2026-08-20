const CODE128_PATTERNS: string[] = [
  '11011001100', '11001101100', '11001100110', '10010011000', '10010001100',
  '10001001100', '10011001000', '10011000100', '10001100100', '11001001000',
  '11001000100', '11000100100', '10110011100', '10011011100', '10011001110',
  '10111001100', '10011101100', '10011100110', '11001110010', '11001011100',
  '11001001110', '11011100100', '11001110100', '11101101110', '11101001100',
  '11100101100', '11100100110', '11101100100', '11100110100', '11100110010',
  '11011011000', '11011000110', '11000110110', '10100011000', '10001011000',
  '10001000110', '10110001000', '10001101000', '10001100010', '11010001000',
  '11000101000', '11000100010', '10110111000', '10110001110', '10001101110',
  '10111011000', '10111000110', '10001110110', '11101110110', '11010001110',
  '11000101110', '11011101000', '11011100010', '11011101110', '11101011000',
  '11101000110', '11100010110', '11101101000', '11101100010', '11100011010',
  '11101111010', '11001000010', '11110001010', '10100110000', '10100001100',
  '10010110000', '10010000110', '10000101100', '10000100110', '10110010000',
  '10110000100', '10011010000', '10011000010', '10000110100', '10000110010',
  '11000010010', '11001010000', '11110111010', '11000010100', '10001111010',
  '10100111100', '10010111100', '10010011110', '10111100100', '10011110100',
  '10011110010', '11110100100', '11110010100', '11110010010', '11011011110',
  '11011110110', '11110110110', '10101111000', '10100011110', '10001011110',
  '10111101000', '10111100010', '11110101000', '11110100010', '10111011110',
  '10111101110', '11101011110', '11110101110', '11010000100', '11010010000',
  '11010011100',
];
const CODE128_STOP = '1100011101011';

function isDigit(ch: string): boolean {
  return ch >= '0' && ch <= '9';
}

function codeAValue(ch: string): number | null {
  const c = ch.charCodeAt(0);
  if (c >= 32 && c <= 95) return c - 32;
  if (c >= 96 && c <= 127) return c + 64;
  return null;
}

function codeBValue(ch: string): number | null {
  const c = ch.charCodeAt(0);
  if (c >= 32 && c <= 127) return c - 32;
  return null;
}

export interface Code128Result {
  patterns: string[];
}

/**
 * 编码 Code128 (自动选择 A/B/C 模式，含校验位)
 * 校验位 = (StartValue + sum(value[i] * (i+1))) % 103
 */
export function encodeCode128(input: string): Code128Result {
  const clean = String(input).replace(/[^\x20-\x7f]/g, '');
  if (!clean) {
    return { patterns: [CODE128_PATTERNS[104], CODE128_STOP] };
  }
  const chars = Array.from(clean);

  // 初始模式选择：全数字且偶数位 -> Code C；否则含小写/大写 ASCII -> Code B；超出 B 范围且仅含 A 字符 -> Code A
  let startMode: 'B' | 'C' | 'A' = 'B';
  const allDigits = chars.every(isDigit);
  if (allDigits && chars.length >= 4 && chars.length % 2 === 0) {
    startMode = 'C';
  } else {
    for (const ch of chars) {
      if (codeBValue(ch) === null) {
        startMode = codeAValue(ch) !== null ? 'A' : 'B';
        break;
      }
    }
  }

  const values: { v: number }[] = [];
  let mode = startMode;
  let i = 0;

  while (i < chars.length) {
    const ch = chars[i];

    if (mode === 'C') {
      if (i + 1 < chars.length && isDigit(ch) && isDigit(chars[i + 1])) {
        values.push({ v: parseInt(ch + chars[i + 1], 10) });
        i += 2;
        continue;
      }
      // Code C 中遇到非数字：先看剩余能否切换
      if (codeBValue(ch) !== null) {
        values.push({ v: 100 }); // ToB
        mode = 'B';
      } else if (codeAValue(ch) !== null) {
        values.push({ v: 101 }); // ToA
        mode = 'A';
      } else {
        i++;
      }
      continue;
    }

    if (mode === 'B') {
      let run = 0;
      while (i + run < chars.length && isDigit(chars[i + run])) run++;
      // 连续 >=4 位数字时切到 Code C（仅在 C 模式能成对消费时切换）
      if (run >= 4) {
        values.push({ v: 99 }); // ToC
        mode = 'C';
        continue;
      }
      const v = codeBValue(ch);
      if (v !== null) {
        values.push({ v });
        i++;
      } else if (codeAValue(ch) !== null) {
        values.push({ v: 101 }); // ToA
        mode = 'A';
      } else {
        i++;
      }
      continue;
    }

    // mode === 'A'
    let run = 0;
    while (i + run < chars.length && isDigit(chars[i + run])) run++;
    if (run >= 4) {
      values.push({ v: 99 }); // ToC
      mode = 'C';
      continue;
    }
    const v = codeAValue(ch);
    if (v !== null) {
      values.push({ v });
      i++;
    } else if (codeBValue(ch) !== null) {
      values.push({ v: 100 }); // ToB
      mode = 'B';
    } else {
      i++;
    }
  }

  const start = startMode === 'C' ? 105 : startMode === 'B' ? 104 : 103;
  let checksum = start;
  for (let k = 0; k < values.length; k++) {
    checksum += values[k].v * (k + 1);
  }
  checksum %= 103;

  const allValues = [start, ...values.map(e => e.v), checksum];
  const patterns = allValues.map(v => CODE128_PATTERNS[v]).concat(CODE128_STOP);
  return { patterns };
}

export function code128Modules(patterns: string[]): number {
  return patterns.reduce((sum, p) => sum + p.length, 0);
}