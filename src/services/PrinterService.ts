import { BleManager, type Device, type Characteristic } from 'react-native-ble-plx';
import { Platform, PermissionsAndroid } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { logInfo, logError } from '../utils/logger';
import { renderLabelBitmap, buildCPCLLabel, buildESCPOLILabel } from './LabelRenderer';
import {
  listBondedDevices as sppListBonded,
  sppConnect,
  sppDisconnect,
  sppIsConnected,
  sppWrite,
} from '../../modules/jindou-spp';
import {
  LABEL_PRESETS,
  DEFAULT_LABEL_CONFIG,
  migrateLabelConfig,
  buildDefaultConfig,
  FIELD_KEYS,
  genElementId,
  fieldValue,
  getRandomSlogan,
  type LabelConfig,
  type LabelData,
  type LabelElement,
} from './PrinterServiceTypes';

export type ScannedDevice = {
  id: string;
  name: string;
  rssi: number | null;
};

export type BatteryInfo = {
  level: number;      // 0-100 百分比
  voltage: number;    // 电压 mV
  isCharging: boolean;
  timestamp: number;
  isLow?: boolean;    // 是否低电量
  status?: string;    // 电量状态
  raw?: string;       // 原始数据
};

// 重导出类型，保持外部引用不变
export {
  LABEL_PRESETS,
  DEFAULT_LABEL_CONFIG,
  migrateLabelConfig,
  buildDefaultConfig,
  FIELD_KEYS,
  genElementId,
  type LabelSize,
  type LabelConfig,
  type LabelData,
  type LabelElement,
  type LabelElementType,
} from './PrinterServiceTypes';

export { fieldValue, getRandomSlogan };

/** 兼容旧版字段引用（已废弃，保留类型以便迁移代码编译） */
export type LabelField = {
  key: string;
  label: string;
  show: boolean;
  fontSize: number;
  bold: boolean;
};

const manager = new BleManager();
let connectedDevice: Device | null = null;
let writeChar: Characteristic | null = null;
let readChar: Characteristic | null = null;
let lastBatteryInfo: BatteryInfo | null = null;
let disconnectListener: (() => void) | null = null;
let onConnectionChange: ((connected: boolean) => void) | null = null;
let writeMtu = 200; // 实际可写负载字节数（协商后更新）

// SPP（经典蓝牙）通道状态。HM-T260LR 是双模设备，官方 App 走 SPP/RFCOMM。
let sppAddress: string | null = null;
let sppName: string | null = null;
let sppWriteUsed = false; // 本次会话是否已用 SPP 通道（打印走 SPP 时置位）

const LAST_DEVICE_KEY = 'jindou_last_printer';
let lastDevice: ScannedDevice | null = null;

// BLE 写入块大小（配合 BLE MTU，避免写入失败）
const WRITE_CHUNK_SIZE = 400; // MTU 协商后通常 514B，400B 安全
const WRITE_DELAY_MS = 10; // writeWithResponse 已有确认，减少额外延时

export function setOnConnectionChange(cb: ((connected: boolean) => void) | null) {
  onConnectionChange = cb;
}

async function requestBluetoothPermission(): Promise<boolean> {
  if (Platform.OS === 'android') {
    const apiLevel = Platform.Version;
    if (apiLevel >= 31) {
      // 扫描仅需 SCAN + CONNECT。ADVERTISE 与扫描无关，不应作为必要条件；
      // Android 12+ 位置权限不再需要用于 BLE 扫描（由 BLUETOOTH_SCAN 承担）。
      const results = await PermissionsAndroid.requestMultiple([
        PermissionsAndroid.PERMISSIONS.BLUETOOTH_SCAN,
        PermissionsAndroid.PERMISSIONS.BLUETOOTH_CONNECT,
      ]);
      const scanGranted = results[PermissionsAndroid.PERMISSIONS.BLUETOOTH_SCAN] === PermissionsAndroid.RESULTS.GRANTED;
      const connectGranted = results[PermissionsAndroid.PERMISSIONS.BLUETOOTH_CONNECT] === PermissionsAndroid.RESULTS.GRANTED;
      if (!scanGranted || !connectGranted) {
        logError('PRINTER', `蓝牙权限被拒绝: scan=${scanGranted} connect=${connectGranted}`);
        return false;
      }
      return true;
    } else {
      // API <= 30：BLUETOOTH_SCAN 权限不存在（API 31 引入），扫描只需位置权限
      const loc = await PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
        { title: '位置权限', message: '需要位置权限来搜索蓝牙设备', buttonPositive: '允许' }
      );
      return loc === PermissionsAndroid.RESULTS.GRANTED;
    }
  }
  return true;
}

export async function scanDevices(onFound?: (devices: ScannedDevice[]) => void): Promise<ScannedDevice[]> {
  const ok = await requestBluetoothPermission();
  if (!ok) {
    throw new Error('蓝牙权限被拒绝，请在系统设置中允许蓝牙权限');
  }

  try {
    const saved = await AsyncStorage.getItem(LAST_DEVICE_KEY);
    if (saved) lastDevice = JSON.parse(saved);
  } catch (_) {}

  logInfo('PRINTER', '开始扫描蓝牙设备...');
  const deviceMap = new Map<string, ScannedDevice>();

  // 记住的上次设备也纳入列表（即使当前未广播，仍可尝试直连）
  if (lastDevice && lastDevice.id) {
    deviceMap.set(lastDevice.id, { ...lastDevice, rssi: null });
  }

  return new Promise((resolve) => {
    let settled = false;
    const finish = () => {
      if (settled) return;
      settled = true;
      clearTimeout(timeout);
      manager.stopDeviceScan().catch(() => {});
      const list = Array.from(deviceMap.values()).sort((a, b) => (b.rssi ?? -999) - (a.rssi ?? -999));
      onFound?.(list);
      resolve(list);
    };

    const timeout = setTimeout(finish, 10000);

    manager.startDeviceScan(null, null, (err, d) => {
      if (err) {
        logError('PRINTER', `扫描错误: ${err.message}`);
        finish();
        return;
      }
      if (!d) return;
      const name = d.name || d.localName || '';
      if (!name) return;
      logInfo('PRINTER', `发现: ${name} (${d.id.slice(-8)}) rssi=${d.rssi}`);
      const info: ScannedDevice = { id: d.id, name, rssi: d.rssi };
      deviceMap.set(d.id, info);
      onFound?.(Array.from(deviceMap.values()).sort((a, b) => (b.rssi ?? -999) - (a.rssi ?? -999)));
    });
  });
}

export async function connectToDevice(device: ScannedDevice): Promise<boolean> {
  try {
    logInfo('PRINTER', `连接: ${device.name} (${device.id})...`);

    try { await manager.cancelDeviceConnection(device.id); } catch (_) {}

    const connected = await manager.connectToDevice(device.id, {
      autoConnect: false,
      timeout: 15000,
    });
    logInfo('PRINTER', `已连接, 发现服务...`);

    await new Promise(r => setTimeout(r, 800));

    const isConn = await connected.isConnected();
    logInfo('PRINTER', `连接状态: ${isConn}`);
    if (!isConn) {
      logError('PRINTER', '连接后状态检查失败');
      return false;
    }

    // 协商更大的 MTU（默认 23-3=20B 太小），失败时静默降级
    try {
      const mtuResult = await connected.requestMTU(517);
      const mtu = Math.max(20, mtuResult.mtu - 3);
      writeMtu = mtu;
      logInfo('PRINTER', `MTU 协商完成: ${mtuResult.mtu} (有效负载 ${mtu}B)`);
    } catch (_) {
      logInfo('PRINTER', 'MTU 协商失败，使用默认分块');
    }

    try {
      await connected.discoverAllServicesAndCharacteristics();
      logInfo('PRINTER', '服务发现完成');
    } catch (discErr) {
      logError('PRINTER', `服务发现失败: ${discErr}, 尝试继续...`);
    }

    const services = await connected.services();
    logInfo('PRINTER', `发现 ${services.length} 个服务`);
    for (const svc of services) {
      logInfo('PRINTER', `服务: ${svc.uuid}`);
      try {
        const chars = await svc.characteristics();
        for (const c of chars) {
          logInfo('PRINTER', `  特征: ${c.uuid} write=${c.isWritableWithoutResponse}/${c.isWritableWithResponse} read=${c.isReadable} notify=${c.isNotifiable}`);
          if (c.isWritableWithoutResponse || c.isWritableWithResponse) {
            writeChar = c;
          }
          if (c.isReadable || c.isNotifiable) {
            readChar = c;
          }
        }
      } catch (charErr) {
        logError('PRINTER', `读取特征失败: ${charErr}`);
      }
      if (writeChar && readChar) break;
    }

    if (!writeChar) {
      logInfo('PRINTER', '未找到标准写特征，尝试汉印常见服务/特征...');
      // 汉印(HPRT)常见: 服务 0000ffe0, 写特征 0000ffe1
      const HP_CHAR_UUIDS = [
        '0000ffe1-0000-1000-8000-00805f9b34fb',
        '0000fff2-0000-1000-8000-00805f9b34fb',
        '0000fee1-0000-1000-8000-00805f9b34fb',
      ];
      for (const svc of services) {
        for (const uuid of HP_CHAR_UUIDS) {
          try {
            const c = await connected.writeCharacteristicWithoutResponseForService(svc.uuid, uuid, 'AQ==');
            writeChar = c;
            logInfo('PRINTER', `通过汉印特征写入成功: ${uuid}`);
            break;
          } catch (_) {}
        }
        if (writeChar) break;
      }
    }

    if (!writeChar) {
      logError('PRINTER', '未找到可写特征值，所有服务:');
      for (const svc of services) {
        try {
          const chars = await svc.characteristics();
          logError('PRINTER', `  ${svc.uuid}: ${chars.map(c => c.uuid).join(', ')}`);
        } catch (_) {}
      }
      await connected.cancelConnection();
      return false;
    }

    connectedDevice = connected;
    lastDevice = { id: device.id, name: device.name, rssi: device.rssi };
    try { AsyncStorage.setItem(LAST_DEVICE_KEY, JSON.stringify(lastDevice)); } catch (_) {}
    logInfo('PRINTER', `已连接: ${device.name}`);

    if (disconnectListener) disconnectListener();
    const sub = connected.onDisconnected(() => {
      logInfo('PRINTER', '设备断开连接');
      connectedDevice = null;
      writeChar = null;
      readChar = null;
      writeMtu = 200;
      onConnectionChange?.(false);
    });
    disconnectListener = () => sub.remove();

    onConnectionChange?.(true);
    return true;
  } catch (err) {
    logError('PRINTER', `连接失败: ${err instanceof Error ? err.message : String(err)}`);
    return false;
  }
}

export function isConnected(): boolean {
  return (connectedDevice !== null && writeChar !== null) || sppAddress !== null;
}

/**
 * 通过经典蓝牙 SPP（RFCOMM 00001101）连接 HM-T260LR。
 * 官方 App 即走此通道（createInsecureRfcommSocketToServiceRecord）。
 */
export async function connectToDeviceSpp(address: string, name?: string): Promise<boolean> {
  try {
    logInfo('PRINTER', `SPP 连接: ${name || address} (${address})...`);
    await sppDisconnect();
    const res = await sppConnect(address);
    if (res.connected) {
      sppAddress = address;
      sppName = res.name || name || address;
      sppWriteUsed = false;
      lastDevice = { id: address, name: sppName, rssi: null };
      try { AsyncStorage.setItem(LAST_DEVICE_KEY, JSON.stringify(lastDevice)); } catch (_) {}
      logInfo('PRINTER', `SPP 已连接: ${sppName}`);
      onConnectionChange?.(true);
      return true;
    }
    logError('PRINTER', `SPP 连接失败: ${address}`);
    return false;
  } catch (err) {
    logError('PRINTER', `SPP 连接异常: ${err instanceof Error ? err.message : String(err)}`);
    return false;
  }
}

/** 扫描/获取可用经典蓝牙设备（已配对 + 通过 BLE 扫描发现的同型号设备补全） */
export async function scanSppDevices(): Promise<ScannedDevice[]> {
  try {
    const bonded = await sppListBonded();
    const list: ScannedDevice[] = bonded.map(d => ({ id: d.id, name: d.name, rssi: null }));
    if (lastDevice && !list.some(d => d.id === lastDevice!.id)) {
      list.push(lastDevice);
    }
    return list;
  } catch (err) {
    logError('PRINTER', `SPP 扫描异常: ${err}`);
    return [];
  }
}

async function writeSppChunks(data: Uint8Array): Promise<void> {
  if (!sppAddress) throw new Error('SPP 未连接');
  // 单次 Base64 写入由原生层处理大包；分块发送更稳，避免蓝牙缓冲压力
  const chunkSize = 4096;
  const delay = 30;
  for (let i = 0; i < data.length; i += chunkSize) {
    const chunk = data.slice(i, i + chunkSize);
    await sppWrite(toBase64(chunk));
    if (delay > 0) await new Promise(r => setTimeout(r, delay));
  }
}

function toBase64(arr: Uint8Array): string {
  let bin = '';
  for (let i = 0; i < arr.length; i++) bin += String.fromCharCode(arr[i]);
  return btoa(bin);
}

export async function checkConnection(): Promise<boolean> {
  if (sppAddress !== null) {
    const ok = await sppIsConnected();
    if (!ok) {
      logInfo('PRINTER', 'SPP 设备已断开 (checkConnection)');
      sppAddress = null;
      sppName = null;
      onConnectionChange?.(false);
    }
    return ok;
  }
  if (!connectedDevice) return false;
  try {
    const state = await connectedDevice.isConnected();
    if (!state) {
      logInfo('PRINTER', '设备已断开 (checkConnection)');
      connectedDevice = null;
      writeChar = null;
      readChar = null;
      onConnectionChange?.(false);
      return false;
    }
    return true;
  } catch (e) {
    logInfo('PRINTER', `连接检查失败: ${e}`);
    connectedDevice = null;
    writeChar = null;
    readChar = null;
    onConnectionChange?.(false);
    return false;
  }
}

export async function queryBattery(): Promise<BatteryInfo | null> {
  if (sppAddress !== null) {
    logInfo('PRINTER', 'SPP 模式暂不支持电量查询');
    return null;
  }
  if (!writeChar) {
    logError('PRINTER', '未连接打印机');
    return null;
  }

  try {
    // CPCL 命令: ! U 1 查询打印机状态
    const cmd = new TextEncoder().encode('! U 1\r\n');
    const base64 = btoa(String.fromCharCode(...cmd));
    if (writeChar.isWritableWithResponse) {
      await writeChar.writeWithResponse(base64);
    } else {
      await writeChar.writeWithoutResponse(base64);
    }

    await new Promise(r => setTimeout(r, 300));

    if (readChar) {
      try {
        const updatedChar = await readChar.read();
        const value = updatedChar.value;
        if (value) {
          const binary = atob(value);
          const bytes = Array.from(binary, c => c.charCodeAt(0));
          logInfo('PRINTER', `状态响应: [${bytes.join(', ')}]`);
          const info = parseBatteryFromResponse(bytes);
          if (info) lastBatteryInfo = info;
          return info;
        }
      } catch (e) {
        logInfo('PRINTER', `读取响应失败: ${e}`);
      }
    }

    // 备用方案: 通过 BLE 电池服务获取
    if (connectedDevice) {
      try {
        const services = await connectedDevice.services();
        for (const service of services) {
          if (service.uuid.includes('180f')) {
            const chars = await service.characteristics();
            for (const c of chars) {
              if (c.uuid.includes('2a19')) {
                await c.read();
                const val = c.value;
                if (val) {
                  // ble-plx 的 value 是 base64 编码，需解码后取首字节
                  const decoded = atob(val);
                  const battery = decoded.length > 0 ? decoded.charCodeAt(0) : 0;
                  const info: BatteryInfo = {
                    level: battery,
                    voltage: 0,
                    isCharging: false,
                    timestamp: Date.now(),
                    isLow: battery < 20,
                    status: battery >= 50 ? '高' : battery >= 20 ? '中' : '低',
                    raw: `BLE电池服务: ${battery}%`,
                  };
                  logInfo('PRINTER', `电池(BLE): ${battery}%`);
                  return info;
                }
              }
            }
          }
        }
      } catch (e) {
        logInfo('PRINTER', `BLE电池查询失败: ${e}`);
      }
    }

    logInfo('PRINTER', '未获取到电池信息');
    return null;
  } catch (err) {
    logError('PRINTER', `查询电量失败: ${err instanceof Error ? err.message : String(err)}`);
    return null;
  }
}

function parseBatteryFromResponse(bytes: number[]): BatteryInfo {
  let level = 100;
  let voltage = 0;
  let isCharging = false;

  if (bytes.length >= 2) {
    level = bytes[1];
    if (level > 100) level = 100;
    if (level < 0) level = 0;
  }

  if (bytes.length >= 4) {
    voltage = (bytes[2] << 8) | bytes[3];
  }

  if (bytes.length >= 1) {
    isCharging = (bytes[0] & 0x10) !== 0;
  }

  return { level, voltage, isCharging, timestamp: Date.now() };
}

export function getLastBatteryInfo(): BatteryInfo | null {
  return lastBatteryInfo;
}

// T260 打印前必须设置打印页面类型（官方 CPCLT260PrintManager.printBitmap 前置指令）
// 指令: \x1B + "setp" + type  (0=连续纸 1=标签纸)
export async function sendSetPageType(type: number): Promise<boolean> {
  if (!writeChar) { logError('PRINTER', '未连接打印机'); return false; }
  try {
    const cmd = new Uint8Array([27, 115, 101, 116, 112, type]);
    const base64 = btoa(String.fromCharCode(...cmd));
    if (writeChar.isWritableWithResponse) {
      await writeChar.writeWithResponse(base64);
    } else {
      await writeChar.writeWithoutResponse(base64);
    }
    logInfo('PRINTER', `已发送 setPrintPageType type=${type}`);
    return true;
  } catch (err) {
    logError('PRINTER', `发送 setPrintPageType 失败: ${err instanceof Error ? err.message : String(err)}`);
    return false;
  }
}

export function getConnectedName(): string {
  if (sppName) return sppName;
  return connectedDevice?.name || connectedDevice?.id || '';
}

export function getLastDevice(): ScannedDevice | null {
  return lastDevice;
}

export function disconnect() {
  if (disconnectListener) { disconnectListener(); disconnectListener = null; }
  connectedDevice?.cancelConnection().catch(() => {});
  connectedDevice = null;
  writeChar = null;
  readChar = null;
  writeMtu = 200;
  sppDisconnect().catch(() => {});
  sppAddress = null;
  sppName = null;
  sppWriteUsed = false;
  manager.stopDeviceScan().catch(() => {});
  onConnectionChange?.(false);
  logInfo('PRINTER', '已断开');
}

export function destroyManager() {
  disconnect();
  manager.destroy().catch(() => {});
}

export async function printLabel(data: LabelData, config?: LabelConfig): Promise<boolean> {
  const cfg = config || DEFAULT_LABEL_CONFIG;

  // SPP 通道（经典蓝牙）：HM-T260LR 双模，官方 App 走 SPP，用 ESC/POS GS v 0 位图
  if (sppAddress !== null) {
    try {
      logInfo('PRINTER', 'SPP 打印: 渲染 ESC_POLI 位图...');
      const bitmap = await renderLabelBitmap(data, cfg);
      if (!bitmap) {
        logError('PRINTER', '标签渲染失败 (skia surface 创建失败)');
        return false;
      }
      const cmd = buildESCPOLILabel(bitmap);
      logInfo('PRINTER', `SPP 发送 ESC/POS 指令 (${cmd.length} bytes, 位图 ${bitmap.widthPx}x${bitmap.heightPx})...`);
      await writeSppChunks(cmd);
      sppWriteUsed = true;
      logInfo('PRINTER', 'SPP 打印完成');
      return true;
    } catch (err) {
      logError('PRINTER', `SPP 打印失败: ${err instanceof Error ? err.message : String(err)}`);
      return false;
    }
  }

  if (!writeChar) {
    // 尝试自动重连（打印完 BLE 可能超时断开）
    if (lastDevice) {
      logInfo('PRINTER', 'BLE 未连接，尝试自动重连...');
      try { await connectToDevice(lastDevice); } catch (_) {}
    }
    if (!writeChar) { logError('PRINTER', '未连接打印机'); return false; }
  }

  try {
    const bitmap = await renderLabelBitmap(data, cfg);
    if (!bitmap) {
      logError('PRINTER', '标签渲染失败 (skia surface 创建失败)');
      return false;
    }

    // BLE 通道也用 ESC/POS 格式（和 SPP 一致），CPCL 在 T260 上不出纸
    const cmd = buildESCPOLILabel(bitmap);
    logInfo('PRINTER', `BLE 发送 ESC/POS 指令 (${cmd.length} bytes, 位图 ${bitmap.widthPx}x${bitmap.heightPx})...`);

    await writeChunks(cmd);

    logInfo('PRINTER', 'BLE 打印完成');
    return true;
  } catch (err) {
    logError('PRINTER', `BLE 打印失败: ${err instanceof Error ? err.message : String(err)}`);
    return false;
  }
}

async function writeChunks(data: Uint8Array): Promise<void> {
  const uint8ArrayToBase64 = (arr: Uint8Array): string =>
    btoa(String.fromCharCode(...arr));

  const chunkSize = Math.min(writeMtu, WRITE_CHUNK_SIZE);
  for (let i = 0; i < data.length; i += chunkSize) {
    const chunk = data.slice(i, i + chunkSize);
    if (writeChar!.isWritableWithResponse) {
      await writeChar!.writeWithResponse(uint8ArrayToBase64(chunk));
    } else {
      await writeChar!.writeWithoutResponse(uint8ArrayToBase64(chunk));
    }
    if (WRITE_DELAY_MS > 0) {
      await new Promise(r => setTimeout(r, WRITE_DELAY_MS));
    }
  }
}

export function buildCPCLPreviewText(data: LabelData, config: LabelConfig): string[] {
  const { w, h } = LABEL_PRESETS[config.size];
  const lines: string[] = [];
  lines.push(`[标签 ${w}x${h}mm · CPCL 位图 200DPI]`);
  for (const el of config.elements) {
    if (el.type === 'text') {
      const value = el.fieldKey ? fieldValue(data, el.fieldKey) : (el.text || '');
      if (value) lines.push(`${value}`);
    } else if (el.type === 'barcode' || el.type === 'qrcode') {
      const value = el.fieldKey ? fieldValue(data, el.fieldKey) : (el.text || '');
      if (value) lines.push(`[${el.type === 'barcode' ? '条码' : '二维码'}: ${value}]`);
    } else if (el.type === 'line') {
      lines.push('[横线]');
    }
  }
  return lines;
}