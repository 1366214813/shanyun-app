# 金豆库管 v1.1.0

服装店库存管理工具，支持 OCR 拍照识别小票/标签批量入库，蓝牙连接汉印T260打印吊牌。

## 功能

- **首页仪表盘** — 快捷操作按钮、今日销售/利润统计、7日趋势图、最近订单、高价值客户、库存预警
- **商品管理** — 商品增删改查，分类筛选/搜索/排序，图片管理，低库存预警，**入库自动分配 EAN-13 条码**
- **客户管理** — 客户信息、会员等级（普通/VIP/黄金/铂金）、积分余额
- **销售开单** — 选择客户+商品，多种支付方式，实时库存校验
- **OCR 入库** — 拍照或选图识别小票/衣物标签，自动解析款号、品名、价格、数量，批量入库；**款号缺失/重复时自动补条码**
- **吊牌打印** — 蓝牙连接汉印T260热敏打印机（HM-T260LR），经典蓝牙 SPP (RFCOMM) + ESC/POS `GS v 0` 位图协议 (ESC_POLI 模式)，BLE 作为后备通道，电量检测
  - **画布式标签编辑器** — 像画布一样自由添加元素（文本/条码/二维码/横线/边框），拖动定位，右下角手柄缩放尺寸，实时预览
  - **多套吊牌模板** — 保存多套模板，打印时一键切换，支持新建/另存为/编辑/删除
  - **商品页快捷打印** — 商品列表每行直接打印该商品的吊牌（用当前模板）
- **数据管理** — 导出 Excel 表格（商品/客户/订单）、一键清空数据
- **主题切换** — 深色/浅色主题，双击版本号查看日志

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| React Native | 0.85.3 | 跨平台框架 |
| Expo | 56.x | 开发工具链 |
| PaddleOCR | v4 (ch_PP-OCRv4) | 文字识别（ONNX Runtime） |
| ONNX Runtime | 1.24.3 | AI 推理引擎 |
| Zustand | 5.x | 状态管理 |
| AsyncStorage | 3.x | 本地数据持久化 |
| React Navigation | 7.x | 页面导航 |
| react-native-ble-plx | 3.5.1 | 蓝牙 BLE 通信 |
| jindou-spp | 1.0.0 | 经典蓝牙 SPP 模块（Expo Modules/Kotlin，RFCOMM 00001101） |
| xlsx | - | Excel 表格导出 |

## 项目结构

```
shanyun-app/
├── App.tsx                 # 导航配置（Stack Navigator: Main tabs + 商品/客户/开单）
├── app.json                # Expo 配置（com.jindou.warehouse）
├── react-native.config.js  # 自定义配置（onnxruntime cmake）
├── modules/
│   └── jindou-spp/         # 经典蓝牙 SPP 原生模块（Expo Modules/Kotlin）
│       ├── index.js              # JS 包装（纯 JS，Metro 兼容）
│       ├── index.d.ts            # 类型声明
│       ├── expo-module.config.json  # 全限定类名模块配置
│       └── android/              # Kotlin 实现（RFCOMM socket, 读/写/断开）
├── src/
│   ├── screens/            # 页面组件
│   │   ├── HomeScreen.tsx       # 首页仪表盘（快捷操作+订单+客户）
│   │   ├── OcrScreen.tsx        # OCR 入库（拍照+批量解析+自动补条码）
│   │   ├── ProductsScreen.tsx   # 商品管理（自动条码 + 快捷打印）
│   │   ├── CustomersScreen.tsx  # 客户管理
│   │   ├── NewOrderScreen.tsx   # 销售开单
│   │   ├── OrdersScreen.tsx     # 订单列表
│   │   ├── PrintScreen.tsx      # 吊牌打印（设备连接 + 多模板管理）
│   │   ├── LabelEditorScreen.tsx # 画布式标签编辑器（拖动/缩放/多模板保存）
│   │   ├── MinimalBleScreen.tsx # 蓝牙调试页（BLE 扫描 + SPP 配对连接 + HEX 发送 + 全黑测试）
│   │   └── SettingsScreen.tsx   # 设置（Excel导出）
│   ├── hooks/
│   │   └── useOcr.ts           # OCR hook（v4模型，单例模式）
│   ├── services/
│   │   ├── PrinterService.ts   # 蓝牙打印服务（SPP + BLE 双通道）
│   │   ├── PrinterServiceTypes.ts # 标签预设/元素类型/模板类型/旧配置迁移
│   │   └── LabelRenderer.ts    # 标签渲染（逐元素绘制）+ buildESCPOLILabel（ESC/POS 位图命令）
│   ├── store/
│   │   └── useAppStore.ts      # Zustand 全局状态（含多套标签模板 labelTemplates）
│   ├── types/
│   │   └── index.ts            # TypeScript 类型定义
│   └── utils/
│       ├── format.ts           # 格式化工具 + genBarcode（生成 EAN-13 条码）
│       └── logger.ts           # 日志工具
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

- Node.js 18+
- 构建服务器（x86_64 Linux，Java 17，Android SDK/NDK）
- Android SDK（NDK 27.1.12297006，CMake 3.22.1，Gradle 9.3.1）
- 当前构建服务器：`ssh Yw@100.66.1.3`，项目 `/home/Yw/shanyun-app`
- JDK17 位于 `~/android-sdk/jdk-17.0.13+11`，SDK 位于 `~/android-sdk`

### 构建流程（当前版本）

```bash
# 1. 本地修改后同步单个文件到服务器（scp），或全量 tar 同步
scp path/to/file Yw@100.66.1.3:/home/Yw/shanyun-app/path/to/file

# 2. 服务器构建 APK（仅 arm64）
cd /home/Yw/shanyun-app
export JAVA_HOME=~/android-sdk/jdk-17.0.13+11
export ANDROID_HOME=~/android-sdk
./android/gradlew -p android assembleRelease -PreactNativeArchitectures=arm64-v8a -x lint -x test
# 长任务可后台执行：nohup ... > /tmp/build.log 2>&1 &
# 备份/下载 APK：scp Yw@100.66.1.3:.../app-release.apk ./  (110MB，注意 scp 超时截断，下载后用 unzip -t 校验)

# 3. 输出
# /home/Yw/shanyun-app/android/app/build/outputs/apk/release/app-release.apk
# 通过 adb 安装到手机（手机需先配对打印机并允许 adb 无线调试）
# 无线 adb：adb tcpip 5555 && adb connect <手机IP>:5555
# 或用 adb push 推送到 /sdcard/Download/ 后手动安装
```

### 构建注意事项（jindou-spp 模块）

- `modules/jindou-spp/index.js` 是**纯 JS**（无 TS 注解），否则 Metro 报 `SyntaxError: Missing semicolon`
- `android/build.gradle` 必须含 `versionCode`/`versionName`，JVM target 需一致（17）
- `expo-module.config.json` 的 modules 用**全限定类名** `com.jindou.spp.JindouSppModule`（生成器拼 `ExpoModulesPackageList.kt`，裸类名会 `Unresolved reference`）
- `Exceptions.UnknownException` 在 expo-modules-core 56 不存在，改用 `Exceptions.IllegalStateException`
- **必须在 `android/settings.gradle` 配置 `expoAutolinking.searchPaths = ["./modules"]`**（在 `useExpoModules()` 之前）。否则 gradle 的 autolinking 不会发现 `modules/` 下的本地模块，运行时报 `jindou-spp native module not available`（原生模块未注册）。
- **JS 侧必须用 `requireNativeModule('JindouSpp')` 获取模块**（RN 0.85 New Architecture + Expo 56 下 `NativeModulesProxy.JindouSpp` 可能为 undefined）。代码片段：
  ```js
  import { requireNativeModule, NativeModulesProxy } from 'expo-modules-core';
  let native;
  try { native = requireNativeModule('JindouSpp'); } catch { native = NativeModulesProxy.JindouSpp; }
  ```
- **项目根目录必须有 `index.ts`**（`import { registerRootComponent } from 'expo'; registerRootComponent(App)`）。同步文件时注意别误覆盖它（`main` 字段指向 `index.ts`，缺失会导致 `Cannot resolve entry file` 构建失败）
- 构建失败排查：`strip` 异常 / Metro 失败时加 `REACT_NATIVE_MAX_WORKERS=1`
- **构建增量验证技巧**：若怀疑模块没打包进 APK，用 `dexdump` 检查 `ExpoModulesPackageList` 类里是否含 `const-class Lcom/jindou/spp/JindouSppModule`（模块真正注册的凭证）

### 老版本构建（BLE-only，供参考）

```bash
# 1. 同步源码到构建服务器
tar czf shanyun-sync.tar.gz \
  --exclude=node_modules --exclude=.expo --exclude=android \
  --exclude=web-build --exclude=patches .
scp shanyun-sync.tar.gz Yw@100.66.1.3:/home/Yw/

# 2. 解压并安装依赖
ssh Yw@100.66.1.3
cd /home/Yw
rm -rf shanyun-app && tar xzf shanyun-sync.tar.gz
cd shanyun-app
rm -rf node_modules android
npm install --legacy-peer-deps

# 3. 排除 ble-plx codegen（避免 cmake 编译错误）
cd node_modules/react-native-ble-plx
python3 -c "
import json
with open('package.json') as f:
    d = json.load(f)
d.pop('codegenConfig', None)
with open('package.json', 'w') as f:
    json.dump(d, f, indent=2)
"
cd ../..

# 4. Expo prebuild（不加 EXPO_USE_COMMUNITY_AUTOLINKING）
npx expo prebuild --platform android --clean

# 5. 应用 Patch（RN 版本兼容性）
sed -i 's/VersionNumber.parse(REACT_NATIVE_VERSION) < VersionNumber.parse("0.71")/REACT_NATIVE_MINOR_VERSION < 71/g' \
  node_modules/onnxruntime-react-native/android/build.gradle

# 6. 修复 hermesc 权限
chmod +x node_modules/hermes-compiler/hermesc/linux64-bin/hermesc

# 7. 构建 APK
cd android
echo 'sdk.dir=/home/Yw/android-sdk' > local.properties
ANDROID_HOME=/home/Yw/android-sdk ./gradlew assembleRelease \
  -PreactNativeArchitectures=arm64-v8a \
  -PnewArchEnabled=false \
  --no-daemon \
  -Dorg.gradle.jvmargs=-Xmx1024m \
  -Dorg.gradle.workers.max=2

# 8. 输出
cp app/build/outputs/apk/release/app-release.apk /vol1/1000/shanyun-build/shanyun.apk
```

### 老版本关键构建注意事项

- **不要** 加 `EXPO_USE_COMMUNITY_AUTOLINKING=1`，会导致 ble-plx codegen cmake 错误
- `react-native-ble-plx` 必须移除 `package.json` 中的 `codegenConfig`，否则 gradle autolinking 会生成不存在的 cmake 路径
- `onnxruntime-react-native` 需要 patch：`REACT_NATIVE_VERSION` 改为 `REACT_NATIVE_MINOR_VERSION`
- 仅编译 `arm64-v8a` 架构，节省编译时间
- Gradle 内存限制 1024MB，worker 数量 2，防止服务器 OOM

### 关键依赖兼容性

| 依赖 | 版本 | 备注 |
|------|------|------|
| react-native-screens | ^4.27.0 | RN 0.85.3 兼容 |
| react-native-svg | ^15.15.5 | Expo 56 配套 |
| react-native-reanimated | ~4.3.0 | RN 0.85.3 兼容 |
| react-native-worklets | ~0.8.0 | RN 0.85.3 兼容 |

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
- **首选通道（SPP）**：经典蓝牙 RFCOMM（`createInsecureRfcommSocketToServiceRecord`，UUID `00001101-0000-1000-8000-00805F9B34FB`），与官方 App 相同
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
- **后备通道（BLE）**：`react-native-ble-plx` 直连，写特征 `0000ff02`，MTU 协商；用于 BLE 直连调试与电量读取（CPCL `! U 1` + 电池服务 0x180F）
- 吊牌尺寸：40x30mm / 50x30mm / 60x40mm / 100x60mm / 100x80mm
- 电量检测：30秒自动刷新（SSP 模式暂不支持电量查询）
- SPP 打印在 4KB 分块 + 30ms 间隔发送，避免蓝牙缓冲压力
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

`printLabel()` 内部逻辑：SPP 已连接 → 走 ESC_POLI（`buildESCPOLILabel`）；否则有 BLE 写特征 → 走 CPCL（`buildCPCLLabel`）。SPP 是首选通道。

## 数据存储

- 全部数据存储在设备本地（AsyncStorage），Key: `jindou_data`
- 无后端服务器，无登录注册
- 支持导出 Excel 表格（商品/客户/订单三个工作表）
- 支持一键清空数据

## 源码备份

- 构建与备份服务器：`/home/Yw/shanyun-app`（100.66.1.3，曾为 192.168.1.9）
- APK 输出：`/home/Yw/shanyun-app/android/app/build/outputs/apk/release/app-release.apk`
- 本地草稿（Termux）：`/data/data/com.termux/files/home/shanyun-app`
- 手机固件备份：`/storage/emulated/0/TRIM/Download/飞牛下载/`

## License

MIT
