# 金豆库管 v1.4.0

服装店库存管理工具，支持 OCR 拍照识别小票/标签批量入库，蓝牙连接汉印T260打印吊牌，Supabase 云端同步。

## 功能

- **首页仪表盘** — 快捷操作按钮（开单/客户/商品/打印，权重 3:1:1:1，开单占50%宽度）、今日销售/利润/订单数/库存总量卡片（可点击跳转）、7日趋势图、最近订单、高价值客户、库存预警（可点击跳商品页）
- **商品管理** — 商品增删改查，分类筛选/搜索/排序（横向滚动，不再被裁切），图片管理，低库存预警，**入库自动分配 EAN-13 条码**，打印可选数量（1-99张，步进器+快捷预设）
- **客户管理** — 客户信息、会员等级（普通/VIP/黄金/铂金）、积分余额
- **销售开单** — 选择客户+商品，多种支付方式，实时库存校验
- **OCR 入库** — 拍照或选图识别小票/衣物标签，自动解析款号、品名、价格、数量，批量入库；**款号缺失/重复时自动补条码**
- **吊牌打印** — 蓝牙连接汉印T260热敏打印机（HM-T260LR），经典蓝牙 SPP (RFCOMM) + ESC/POS `GS v 0` 位图协议 (ESC_POLI 模式)，BLE 作为后备通道（iOS 走 BLE），电量检测，排序/分类筛选/全选，底部常驻汇总栏，连接状态条可展开
  - **画布式标签编辑器** — 像画布一样自由添加元素（文本/条码/二维码/横线/边框），拖动定位，右下角手柄缩放尺寸，实时预览
  - **多套吊牌模板** — 保存多套模板，打印时一键切换，支持新建/另存为/编辑/删除
  - **40x30 预设模板** — 新建 40x30 标签自动生成：随机文案 + 品名 + "我:XX块钱" + 条码
  - **40x30 信息模板** — 品名 + 款号+尺码同行 + 零售价 + 条码（无文案，信息密集型）
  - **180+ 条随机文案** — 每张标签自动轮换搞笑文案（如"穿上我你就是这条街最靓的崽"）
  - **数量选择器** — 每个商品可设置打印张数（1-99张，步进器+快捷预设），批量打印显示"X种, Y张"，底部常驻汇总栏
  - **商品页快捷打印** — 商品列表每行直接打印该商品的吊牌（用当前模板）
  - **异常数据标红** — 价格为0/未分类商品左侧红边框 + ⚠异常标签，打印页一目了然
- **云端同步** — Supabase 注册/登录，数据上传/拉取云端，多设备同步
- **数据管理** — 导出 Excel 表格（商品/客户/订单）、JSON 数据导入（追加模式，自动校验异常数据）、一键清空数据（需输入 CLEAR 二次确认）
- **设置** — 三标签页（基本信息/定价/数据），店铺信息编辑（名称/电话/地址），定价加价率设置（低于5%温馨提示），数据概览，深色模式（带开关状态标签），调试日志，操作 Toast 反馈
- **主题切换** — 深色/浅色主题，全面深色模式适配（所有页面颜色跟随主题，无硬编码），双击版本号查看日志

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| React Native | 0.83.10 | 跨平台框架 |
| Expo | 55.x | 开发工具链 |
| PaddleOCR | v4 (ch_PP-OCRv4) | 文字识别（ONNX Runtime） |
| ONNX Runtime | 1.24.3 | AI 推理引擎 |
| Zustand | 5.x | 状态管理 |
| AsyncStorage | 3.x | 本地数据持久化 |
| React Navigation | 7.x | 页面导航 |
| react-native-ble-plx | 3.5.1 | 蓝牙 BLE 通信 |
| jindou-spp | 1.0.0 | 经典蓝牙 SPP 模块（Expo Modules/Kotlin，RFCOMM 00001101） |
| xlsx | - | Excel 表格导出 |
| expo-document-picker | ~55.0.16 | JSON 数据导入 |
| @supabase/supabase-js | - | 云端认证 + 数据库同步 |

## 项目结构

```
shanyun-app/
├── App.tsx                 # 导航配置（认证页 + Stack Navigator）
├── app.json                # Expo 配置（com.jindou.warehouse）
├── supabase-schema.sql     # Supabase 数据库建表 SQL
├── react-native.config.js  # 自定义配置（onnxruntime cmake）
├── modules/
│   └── jindou-spp/         # 经典蓝牙 SPP 原生模块（Expo Modules/Kotlin）
│       ├── index.js              # JS 包装（纯 JS，Metro 兼容）
│       ├── index.d.ts            # 类型声明
│       ├── expo-module.config.json  # 全限定类名模块配置
│       └── android/              # Kotlin 实现（RFCOMM socket, 读/写/断开）
├── src/
│   ├── screens/            # 页面组件
│   │   ├── AuthScreen.tsx       # 登录/注册页（Supabase 认证）
│   │   ├── HomeScreen.tsx       # 首页仪表盘（快捷操作+订单+客户）
│   │   ├── OcrScreen.tsx        # OCR 入库（拍照+批量解析+自动补条码）
│   │   ├── ProductsScreen.tsx   # 商品管理（自动条码 + 快捷打印）
│   │   ├── CustomersScreen.tsx  # 客户管理
│   │   ├── NewOrderScreen.tsx   # 销售开单
│   │   ├── OrdersScreen.tsx     # 订单列表
│   │   ├── PrintScreen.tsx      # 吊牌打印（设备连接 + 多模板管理）
│   │   ├── LabelEditorScreen.tsx # 画布式标签编辑器（拖动/缩放/多模板保存）
│   │   ├── MinimalBleScreen.tsx # 蓝牙调试页（BLE 扫描 + SPP 配对连接 + HEX 发送 + 全黑测试）
│   │   └── SettingsScreen.tsx   # 设置（店铺信息/定价/外观/数据导入导出/日志）
│   ├── hooks/
│   │   └── useOcr.ts           # OCR hook（v4模型，单例模式）
│   ├── services/
│   │   ├── PrinterService.ts   # 蓝牙打印服务（SPP + BLE 双通道）
│   │   ├── PrinterServiceTypes.ts # 标签预设/元素类型/模板类型/旧配置迁移
│   │   ├── LabelRenderer.ts    # 标签渲染（逐元素绘制）+ buildESCPOLILabel（ESC/POS 位图命令）
│   │   └── CloudSync.ts       # Supabase 云端同步（上传/拉取/删除）
│   ├── config/
│   │   └── supabase.ts        # Supabase 客户端配置
│   ├── store/
│   │   └── useAppStore.ts      # Zustand 全局状态（含多套标签模板 labelTemplates）
│   ├── types/
│   │   └── index.ts            # TypeScript 类型定义
│   └── utils/
│       ├── format.ts           # 格式化工具 + genBarcode（生成 EAN-13 条码）
│       ├── logger.ts           # 日志工具
│       ├── errorHandler.ts     # 统一错误处理
│       └── exportData.ts       # Android Excel 导出（Share 分享）
├── assets/
│   └── models/             # OCR 模型文件
│       ├── v4_det.ort      # PaddleOCR v4 检测模型（4.6MB）
│       ├── v4_rec.ort      # PaddleOCR v4 识别模型（11MB）
│       ├── v4_dict.txt     # v4 字典（6625类）
│       ├── tiny_det.ort    # 旧版 v6 tiny 检测（未使用）
│       ├── tiny_rec.ort    # 旧版 v6 tiny 识别（未使用）
│       └── tiny_dict.txt   # 旧版 v6 tiny 字典（未使用）
├── docs/
│   ├── T260_PROTOCOL.md    # 汉印T260 CPCL 协议文档（旧版 BLE 方案，作参考）
│   └── ESC_POLI.md         # 汉印T260 ESC_POLI 位图协议（当前 SPP 打印方案）
└── package.json
```

## 构建部署

### 环境要求

- Node.js 22+
- GitHub Actions CI（自动构建 Android arm64 APK + iOS IPA）

### CI 构建流程（GitHub Actions）

推送 `main` 分支时自动并行构建 Android 和 iOS：

**Android**（`.github/workflows/android-build.yml`，`ubuntu-latest`）：
1. `npx expo prebuild --platform android --no-install`
2. `./gradlew assembleRelease -PreactNativeArchitectures=arm64-v8a`
3. 产物：`jindou-android-arm64.apk`

**iOS**（`.github/workflows/ios-build.yml`，`macos-15`）：
1. `npx expo prebuild --platform ios --no-install`
2. 降级 `IPHONEOS_DEPLOYMENT_TARGET = 15.1`
3. 切换 Xcode 26 + `pod install`
4. `xcodebuild -sdk iphoneos -configuration Release` → unsigned `.app`
5. 打包 `.ipa` → 产物：`jindou-ios-trollstore.ipa`

### TrollStore 安装（iOS 15.4.1 + iPhone 12 Pro）

1. GitHub Actions → 最新 successful run → Artifacts → 下载 `jindou-ios-trollstore.ipa`
2. 传到 iPhone（AirDrop / 文件分享）
3. TrollStore → 安装 ipa
4. app 名称：`金豆库管`（bundle: `com.jindou.warehouse`）

### 构建注意事项（jindou-spp 模块）

- `modules/jindou-spp/index.js` 是**纯 JS**（无 TS 注解），否则 Metro 报 `SyntaxError: Missing semicolon`
- `android/build.gradle` 必须含 `versionCode`/`versionName`，JVM target 需一致（17）
- `expo-module.config.json` 的 modules 用**全限定类名** `com.jindou.spp.JindouSppModule`（生成器拼 `ExpoModulesPackageList.kt`，裸类名会 `Unresolved reference`）
- **必须在 `android/settings.gradle` 配置 `expoAutolinking.searchPaths = ["./modules"]`**（在 `useExpoModules()` 之前）
- **JS 侧必须用 `requireNativeModule('JindouSpp')` 获取模块**（RN 0.83 New Architecture + Expo 55）
- **项目根目录必须有 `index.ts`**（`import { registerRootComponent } from 'expo'; registerRootComponent(App)`）

**iOS 蓝牙说明**：
- iOS **不支持经典蓝牙 SPP**（RFCOMM 被 Apple 封闭），打印机连接走 **BLE 通道**（react-native-ble-plx）
- `JindouSppModule.swift`：iOS 端为桩模块（`nativeSupport=false`），BLE 回退由 `PrintScreen.tsx` 处理
- BLE 写特征 `0000ff02`，MTU 协商，ESC_POLI 位图协议

**iOS 最低版本**：Expo SDK 55 要求 iOS ≥ 15.1，`IPHONEOS_DEPLOYMENT_TARGET = 15.1`

### 关键依赖兼容性

| 依赖 | 版本 | 备注 |
|------|------|------|
| react-native-screens | ~4.23.0 | RN 0.83.10 兼容 |
| react-native-svg | 15.15.3 | Expo 55 配套 |
| react-native-reanimated | 4.2.1 | RN 0.83.10 兼容 |
| react-native-worklets | 0.7.4 | RN 0.83.10 兼容 |

## OCR 说明

### 模型选择历程

| 版本 | 模型大小 | 识别效果 | 备注 |
|------|---------|---------|------|
| v6 tiny | det 1.8MB + rec 4.5MB | 乱码严重 | 已弃用 |
| v6 small | det 2.9MB + rec 11MB | 基本正确但有错字 | 测试用 |
| **v4 (PP-OCRv4)** | **det 4.6MB + rec 11MB** | **完美** | **当前使用** |

### v4 模型归一化

- 检测模型：`mean=[0.5,0.5,0.5], std=[0.5,0.5,0.5]`
- 识别模型：`(pixel/127.5)-1`
- 字典：6625类（6623字符 + blank + space），嵌入 ONNX 元数据

### OCR 文本解析

解析流程：`normalizeOcrText()` → `compactBody` → Strategy 0/0b/0c 依次匹配

- **Strategy 0**：名称 + 可选数量 + 小数价格（如 `T恤1 39.00`）
- **Strategy 0b**：名称 + 整数价格（如 `规版衬衣85`）
- **Strategy 0c**：名称 + ×数量 + 价格（如 `长裤 ×1 139`）
- **EXCLUDE_RE** 过滤表头/页脚（合计、收款、小票编号等）
- **CLOTHING_RE** 匹配服装品类（T恤、休闲裤、卫衣、外套等50+种）

## 蓝牙打印

- 支持型号：汉印 T260 蓝牙热敏打印机（HM-T260LR，双模 BR/EDR + LE）
- **首选通道（SPP，仅 Android）**：经典蓝牙 RFCOMM（`createInsecureRfcommSocketToServiceRecord`，UUID `00001101-0000-1000-8000-00805F9B34FB`），与官方 App 相同。**iOS 不支持 SPP**，走 BLE 通道。
- **打印协议（ESC_POLI）**：实测打印机只识别位图数据，纯文本/CPCL 命令无响应。
  ```
  ESC @           1b 40          初始化
  GS v 0 m=0      1d 76 30 00    位图命令
  WIDTH(2B LE) HEIGHT(2B LE)     尺寸（WIDTH=每行字节数，HEIGHT=像素）
  {1bpp 位图数据}                  黑=1, MSB-first
  GS f 960        1d 66 c0 03    走纸到标签间隙
  ```
  ⚠️ 走纸必须用 `GS f` 960（标签模式下 `ESC d` 会走一半）；连续纸用 `ESC ESC` 01 5A 00。
- **连接流程**：先在手机系统蓝牙配对打印机 → App"SPP 已配对"列表读取 bonded devices → 点选连接 → 打印
- **后备通道（BLE）**：`react-native-ble-plx` 直连，写特征 `0000ff02`，MTU 协商（517→400B 分块）；iOS 用 `writeWithResponse` + ESC/POS 格式确保可靠打印
- 吊牌尺寸：40x30mm / 60x40mm / 80x50mm / 100x60mm / 100x80mm
- 内置模板：40x30 小标签（文案+品名+价格+条码）、40x30 信息（品名+款号+尺码+价格+条码）、60x40 中号
- 打印数量可调：每个商品可设打印张数，批量打印支持多种多张
- 电量检测：30秒自动刷新（SSP 模式暂不支持电量查询）
- BLE 打印自动重连：断连后下次打印自动重连上次设备
- Android 权限：BLUETOOTH_SCAN、BLUETOOTH_CONNECT、BLUETOOTH_ADVERTISE、ACCESS_FINE_LOCATION
  - 打印页搜索设备前会自动请求蓝牙权限（不再需要先到蓝牙调试页）

### 标签模板

- **元素模型**：模板由多个元素组成，每个元素有类型、位置(x/y mm)、尺寸(w/h mm)：
  - `text` 文本 — 可绑定商品字段（名称/款号/分类/颜色/尺码/零售价/进货价）或自定义内容，可调字高、对齐、加粗
  - `barcode` 条码 — 默认绑定款号，Code128 编码，可换绑定字段或自定义内容
  - `qrcode` 二维码 — 默认绑定款号
  - `line` 横线 / `rect` 边框 — 可调线宽
- **画布编辑器**（打印页 → 标签设置 → 打开编辑器/编辑当前模板）：
  - 轻点元素选中、按住拖动定位、拖右下角圆点缩放尺寸（显示 W×H mm）
  - 下方面板实时编辑内容/绑定字段/字高/对齐/加粗/线宽
  - 可切换标签尺寸、恢复默认布局
  - 模板名输入 + 「保存」（覆盖当前/指定模板）或「另存为新模板」
- **多套模板管理**（打印页 → 标签设置）：
  - 模板列表：点击切换（实时预览）、编辑、删除；默认模板不可删除
  - 「＋新建模板」从空画布新建；编辑时用当前模板打印
  - 旧版单模板配置（fields/showBarcode 结构）会在加载时自动迁移为「默认模板」
- **商品页快捷打印**：商品列表每行「打印」按钮，用当前模板直接打印该商品吊牌（未连接时提示先到打印页连接）

### 真机验证记录（2026-08-20）

手机 iQOO Neo9S Pro + HM-T260LR 实测通过：

| 步骤 | 结果 |
|------|------|
| 系统蓝牙配对打印机 | ✅ 无需 PIN，点选即配对成功 |
| App"SPP 已配对"列表 | ✅ 列出 HM-T260LR-b175 |
| SPP 连接 | ✅ RFCOMM channel 1 |
| 全黑测试（320×240 全黑 + `GS f` 960 走纸） | ✅ 打印成功 |

**排错经验**（按出现顺序）：

1. **"SPP 已配对"列表为空** → 打印机从未在手机系统蓝牙配对，进系统蓝牙设置点选配对即可
2. **`jindou-spp native module not available`** → 原生模块未注册，两个原因：
   - `settings.gradle` 缺 `expoAutolinking.searchPaths = ["./modules"]`
   - JS 侧用了 `NativeModulesProxy` 而非 `requireNativeModule`（New Architecture）
3. **`read failed, socket might closed or timeout, read ret:-1`** → RFCOMM 连接时打印机被占用/主机不可达，关闭其他占用端（官方 App、服务器蓝牙）后重试
4. **`Broken pipe`** → 写入时 socket 已断（连接被打印机或系统踢掉），重新连接即可
5. **打印页搜索不到设备（列表为空）** → 打印页已内置蓝牙权限请求；若之前拒绝过权限，需去系统设置→应用→金豆库管→权限 手动开启，或先在蓝牙调试页请求一次
6. **吊牌打印（打印页）不出纸、但全黑测试正常** → 旧版打印页走 BLE+CPCL 文本命令，而 HM-T260LR 只认 ESC_POLI 位图（纯文本无响应）。已改为打印页走 SPP + ESC_POLI 位图（见下方"打印通道优先级"）

### 打印通道优先级

`printLabel()` 内部逻辑：
- **Android**：SPP 已连接 → 走 ESC_POLI（`buildESCPOLILabel`）；否则有 BLE 写特征 → 走 CPCL（`buildCPCLLabel`）。SPP 是首选通道。
- **iOS**：SPP 不可用（`nativeSupport=false`），直接走 BLE 通道（ESC_POLI 位图）。

## 数据存储

- 本地存储：AsyncStorage，Key: `jindou_data`
- 云端存储：Supabase PostgreSQL（行级安全，用户只能访问自己的数据）
- 支持邮箱注册/登录
- 支持云端上传/拉取同步（设置页 → 云端同步）
- 支持导出 Excel 表格（商品/客户/订单三个工作表）
- 支持 JSON 数据导入（追加模式，自动校验异常数据）
- 支持一键清空数据（需输入 CLEAR 二次确认）

## License

MIT
