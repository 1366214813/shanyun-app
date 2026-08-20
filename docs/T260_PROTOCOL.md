# 汉印 T260 CPCL 协议文档

## 关键发现

**T260 使用标准 CPCL 协议**（不是"汉码打印协议"），与大多数标签打印机兼容。

## 蓝牙连接

- **UUID**: `00001101-0000-1000-8000-00805F9B34FB` (标准 SPP)
- **连接方式**: RFCOMM (经典蓝牙)
- **波特率**: 默认即可

## CPCL 命令参考

### 1. 打印作业控制

```
! 0 200 200 {height} {copies}\r\n   # 开始打印作业
FORM\r\n                             # 换页
PRINT\r\n                            # 打印并出纸
```

**参数说明**:
- `!` - CPCL 命令起始
- `0` - X 起始位置
- `200 200` - DPI 设置（通常为 200x200）
- `height` - 标签高度（点数）
- `copies` - 打印份数

### 2. 打印密度

```
TONE {density}\r\n
```

**density 值**:
- `35` - 低密度 (Level 1)
- `55` - 中密度 (Level 2)
- `75` - 高密度 (Level 3)
- `95` - 最高密度 (Level 4)

### 3. 打印宽度

```
PW {width}\r\n
```
设置打印宽度（点数），T260 最大为 54mm (约 648 点 @300dpi)

### 4. 图形打印 (CG)

```
CG {width} {height} {x} {y} {bitmap_data}\r\n
```

**参数**:
- `width` - 图形宽度（字节数，每字节8点）
- `height` - 图形高度（点数）
- `x` - X 坐标
- `y` - Y 坐标
- `bitmap_data` - 二进制位图数据

### 5. LZO 压缩图形

```
CGLZO {width} {height} {x} {y} {compressed_size}\r\n{compressed_data}
```

### 6. 纸张类型设置

```
setL\r\n   # 标签纸（有间距）
setK\r\n   # 连续纸（无间距）
```

对应字节: `0x1B 0x73 0x65 0x74 0x4C` / `0x1B 0x73 0x65 0x74 0x4B`

### 7. 查询命令

```
ID\r\n                    # 查询打印机 ID
getval "serial_no"\r\n   # 查询序列号
getval "tph_model"\r\n   # 查询打印头型号
```

## 完整打印流程

```javascript
// 1. 连接蓝牙
const socket = await BluetoothDevice.createRfcommSocketToServiceRecord(SPP_UUID);
await socket.connect();
const output = socket.getOutputStream();
const input = socket.getInputStream();

// 2. 设置标签纸
await output.write([0x1B, 0x73, 0x65, 0x74, 0x4C]); // setL (标签纸)

// 3. 发送打印作业
const label = `! 0 200 200 400 1\r\n` +   // 开始作业，高度400点，1份
              `TONE 55\r\n` +              // 中等密度
              `PW 648\r\n` +               // 打印宽度
              `CG 8 20 0 0 ` +             // 图形命令
              bitmapData +                  // 位图数据
              `FORM\r\n` +                 // 换页
              `PRINT\r\n`;                 // 打印

await output.write(Buffer.from(label));
```

## 位图数据格式

1. **单色 1-bit**: 每个像素 1 位（黑/白）
2. **行对齐**: 每行宽度必须是 8 的倍数
3. **字节序**: 每字节从高位到低位对应从左到右的像素
4. **颜色**: 0=白色, 1=黑色

## 设备信息

- **型号**: HM-T260
- **分辨率**: 300 DPI
- **打印宽度**: 最大 54mm (2 英寸)
- **打印速度**: 40mm/s
- **内存**: 2MB RAM, 768KB Flash
- **协议**: CPCL (T260CPCL)
- **压缩**: 支持 LZO 压缩

## 注意事项

1. T260 使用 CPCL 协议，不是 ESC/POS
2. 图形数据需要转换为单色位图格式
3. 每个打印作业必须有 `FORM\r\nPRINT\r\n` 结尾
4. 建议打印前先执行纸张学习 (`setL`)
5. 密度值影响打印深浅，55 是推荐值
