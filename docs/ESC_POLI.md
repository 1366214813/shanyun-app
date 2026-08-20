# 汉印 T260 ESC_POLI 位图协议

当前 App 的 SPP 打印方案（真机实测验证）。

## 背景

T260（HM-T260LR）在经典蓝牙 SPP (RFCOMM) 通道上采用 **ESC_POLI 模式**。
实测打印机**只响应位图数据**，纯文本/CPCL 命令无反应。

## 蓝牙连接

- **UUID**: `00001101-0000-1000-8000-00805F9B34FB` (标准 SPP)
- **连接方式**: RFCOMM 经典蓝牙，`createInsecureRfcommSocketToServiceRecord`
- 官方 App 即走此通道
- 手机需先在系统蓝牙设置中对打印机完成配对（本机 iQOO 实测无需 PIN）

## 打印指令格式

```
ESC @           0x1B 0x40                        初始化
GS v 0 m=0      0x1D 0x76 0x30 0x00              位图命令
WIDTH (2B LE)   每行字节数
HEIGHT (2B LE)  像素高度
{1bpp 位图数据}  黑=1，MSB-first，行宽按字节对齐
GS f 960        0x1D 0x66 0xC0 0x03              走纸到标签间隙
```

### 参数说明

- `WIDTH` 是**每行字节数**（不是像素列数）。如 320 像素宽 → WIDTH=40（320/8），换算 `widthBytes = ceil(widthPx/8)`
- `HEIGHT` 是像素高度
- 位图数据按行排列，每行 `WIDTH` 字节，共 `HEIGHT` 行
- MSB-first：每字节高位对应像素最左侧

### 走纸（关键坑）

| 场景 | 命令 | 说明 |
|------|------|------|
| 标签纸（有间隙） | `GS f` 960 (1d 66 c0 03) | **必须用这个**，走纸到标签间隙 |
| 连续纸 | `ESC ESC` 01 5A 00 | 只走打印内容长度 |

⚠️ 标签模式下用 `ESC d` 走纸会"走一半"，卡在间隙中间。

## 实测记录

| 测试 | 数据 | 结果 |
|------|------|------|
| 320x240 全黑 | 9613B | ✅ 打印成功 |
| 320x960 条纹 | 38413B | ✅ 打印成功 |
| `GS f` 960 走纸 | 19214B | ✅ 走纸完整到标签间隙 |
| 纯文本命令 | - | ❌ 无反应（只认位图） |

## 注意事项

1. T260 打印**前必须**让打印机处于标签间隙对齐状态（`GS f` 走纸）
2. 不要在 `GS v 0` 命令中混入文本/CPCL（忽略）
3. App 内实现见 `src/services/LabelRenderer.ts::buildESCPOLILabel`
4. 与 BLE 通道的 CPCL 方案不同，SPP 走 ESC_POLI（详见 T260_PROTOCOL.md，旧方案仅供参考）