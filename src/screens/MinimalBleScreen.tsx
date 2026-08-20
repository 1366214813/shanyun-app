import React, { useState, useRef } from 'react';
import {
  View, Text, TouchableOpacity, StyleSheet, FlatList,
  ActivityIndicator, Modal, TextInput, ScrollView, Alert, Platform, PermissionsAndroid,
} from 'react-native';
import { BleManager, type Device, type Characteristic } from 'react-native-ble-plx';
import {
  listBondedDevices as sppListBonded,
  sppConnect,
  sppDisconnect,
  sppIsConnected,
  sppWrite,
  toBase64,
} from '../../modules/jindou-spp';

const manager = new BleManager();

type ScanDev = { id: string; name: string; rssi: number | null };
type LogLine = { id: number; text: string };

let logSeq = 0;

export default function MinimalBleScreen() {
  const [devices, setDevices] = useState<ScanDev[]>([]);
  const [scanning, setScanning] = useState(false);
  const [devListVisible, setDevListVisible] = useState(false);
  const [connectingId, setConnectingId] = useState<string | null>(null);
  const [connectedName, setConnectedName] = useState('');
  const [connectedId, setConnectedId] = useState('');
  const [writable, setWritable] = useState<{ service: string; char: string } | null>(null);
  const [logs, setLogs] = useState<LogLine[]>([]);
  const [cmd, setCmd] = useState('! 0 200 200 200 1\r\nTEXT 4 0 10 10 Hello\r\nFORM\r\nPRINT\r\n');

  const [sppDevices, setSppDevices] = useState<ScanDev[]>([]);
  const [sppListVisible, setSppListVisible] = useState(false);
  const [sppConnecting, setSppConnecting] = useState(false);
  const [sppConnectedAddr, setSppConnectedAddr] = useState('');
  const [sppConnectedName, setSppConnectedName] = useState('');
  const [hexInput, setHexInput] = useState('1b 40 1d 76 30 00 28 00 f0 00');
  const [useSpp, setUseSpp] = useState(false);

  const deviceRef = useRef<Device | null>(null);
  const writeCharRef = useRef<Characteristic | null>(null);
  const sppModeRef = useRef(false);

  const log = (text: string) => {
    setLogs((prev) => [{ id: ++logSeq, text }, ...prev].slice(0, 200));
  };

  const requestPerm = async (): Promise<boolean> => {
    if (Platform.OS !== 'android') return true;
    const apiLevel = Platform.Version as number;
    if (apiLevel >= 31) {
      const res = await PermissionsAndroid.requestMultiple([
        PermissionsAndroid.PERMISSIONS.BLUETOOTH_SCAN,
        PermissionsAndroid.PERMISSIONS.BLUETOOTH_CONNECT,
      ]);
      return (
        res[PermissionsAndroid.PERMISSIONS.BLUETOOTH_SCAN] === PermissionsAndroid.RESULTS.GRANTED &&
        res[PermissionsAndroid.PERMISSIONS.BLUETOOTH_CONNECT] === PermissionsAndroid.RESULTS.GRANTED
      );
    }
    const loc = await PermissionsAndroid.request(PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION);
    return loc === PermissionsAndroid.RESULTS.GRANTED;
  };

  const handleScan = async () => {
    const ok = await requestPerm();
    if (!ok) { Alert.alert('权限不足', '无法获取蓝牙权限'); return; }
    setScanning(true);
    setDevices([]);
    setDevListVisible(true);
    log('--- 开始扫描 ---');
    const map = new Map<string, ScanDev>();
    const timeout = setTimeout(() => {
      manager.stopDeviceScan();
      setScanning(false);
      log(`扫描结束，共 ${map.size} 个设备`);
    }, 10000);
    manager.startDeviceScan(null, null, (err, d) => {
      if (err) {
        log('扫描错误: ' + err.message);
        clearTimeout(timeout);
        manager.stopDeviceScan();
        setScanning(false);
        return;
      }
      if (!d) return;
      const name = d.name || d.localName || '';
      if (!name) return;
      log(`发现: ${name} (${d.id}) rssi=${d.rssi}`);
      map.set(d.id, { id: d.id, name, rssi: d.rssi });
      setDevices(Array.from(map.values()));
    });
  };

  const handleConnect = async (d: ScanDev) => {
    setConnectingId(d.id);
    log(`--- 连接 ${d.name} (${d.id}) ---`);
    try {
      await manager.cancelDeviceConnection(d.id).catch(() => {});
      const dev = await manager.connectToDevice(d.id, { autoConnect: false, timeout: 20000 });
      log('已建立 GATT 连接');
      await new Promise((r) => setTimeout(r, 800));
      await dev.discoverAllServicesAndCharacteristics();
      log('服务发现完成');

      const services = await dev.services();
      let foundWrite: { service: string; char: string } | null = null;
      for (const svc of services) {
        const chars = await svc.characteristics().catch(() => []);
        for (const c of chars) {
          const props: string[] = [];
          if (c.isWritableWithResponse || c.isWritableWithoutResponse) {
            foundWrite = { service: svc.uuid, char: c.uuid };
            props.push('WRITE');
          }
          if (c.isReadable) props.push('READ');
          if (c.isNotifiable) props.push('NOTIFY');
          log(`  服务=${svc.uuid} 特征=${c.uuid} [${props.join(', ')}]`);
        }
      }

      if (!foundWrite) {
        log('!!! 未找到可写特征 !!!');
        Alert.alert('提示', '未找到可写特征');
        return;
      }

      // 尝试协商 MTU
      try {
        const mtu = await dev.requestMTU(517);
        log(`MTU=${mtu.mtu}`);
      } catch (_) { log('MTU 协商失败'); }

      setWritable(foundWrite);
      deviceRef.current = dev;
      writeCharRef.current = await dev.writeCharacteristicWithoutResponseForService(
        foundWrite.service, foundWrite.char, 'AQ=='
      );
      setConnectedName(d.name);
      setConnectedId(d.id);
      dev.onDisconnected(() => {
        log('!! 设备断开 !!');
        setConnectedName('');
      });
      log('连接成功，写通道就绪');
      setDevListVisible(false);
    } catch (e) {
      log('连接失败: ' + (e instanceof Error ? e.message : String(e)));
      Alert.alert('连接失败', e instanceof Error ? e.message : String(e));
    } finally {
      setConnectingId(null);
    }
  };

  const handleDisconnect = () => {
    deviceRef.current?.cancelConnection().catch(() => {});
    deviceRef.current = null;
    writeCharRef.current = null;
    setConnectedName('');
    setConnectedId('');
    setWritable(null);
    log('--- 已断开 ---');
  };

  const b64 = (uint8: Uint8Array) => {
    let bin = '';
    for (let i = 0; i < uint8.length; i++) bin += String.fromCharCode(uint8[i]);
    return btoa(bin);
  };

  const handleSend = async (cmdText?: string) => {
    if (!writeCharRef.current) { Alert.alert('提示', '请先连接打印机'); return; }
    const send = cmdText ?? cmd;
    try {
      log(`发送 ${send.length} 字符: ` + JSON.stringify(send.replace(/\r/g, '\\r').replace(/\n/g, '\\n')));
      const bytes = new TextEncoder().encode(send);
      log(`字节长度: ${bytes.length}`);
      const chunk = 180;
      for (let i = 0; i < bytes.length; i += chunk) {
        const part = bytes.slice(i, i + chunk);
        await writeCharRef.current.writeWithoutResponse(b64(part));
        await new Promise((r) => setTimeout(r, 20));
      }
      log('发送完成');
    } catch (e) {
      log('发送失败: ' + (e instanceof Error ? e.message : String(e)));
      Alert.alert('发送失败', e instanceof Error ? e.message : String(e));
    }
  };

  const handleSendHex = async (hexRaw: string) => {
    if (!writeCharRef.current) { Alert.alert('提示', '请先连接打印机'); return; }
    try {
      const hex = hexRaw.replace(/\s/g, '');
      const bytes = new Uint8Array(hex.length / 2);
      for (let i = 0; i < bytes.length; i++) {
        bytes[i] = parseInt(hex.substr(i * 2, 2), 16);
      }
      log(`发送 HEX ${bytes.length} 字节`);
      const chunk = 180;
      for (let i = 0; i < bytes.length; i += chunk) {
        const part = bytes.slice(i, i + chunk);
        await writeCharRef.current.writeWithoutResponse(b64(part));
        await new Promise((r) => setTimeout(r, 20));
      }
      log('HEX 发送完成');
    } catch (e) {
      log('HEX 发送失败: ' + (e instanceof Error ? e.message : String(e)));
    }
  };

  const handleSppScan = async () => {
    const ok = await requestPerm();
    if (!ok) { Alert.alert('权限不足', '无法获取蓝牙权限'); return; }
    log('--- SPP 扫描已配对设备 ---');
    try {
      const bonded = await sppListBonded();
      setSppDevices(bonded.map(d => ({ id: d.id, name: d.name, rssi: null })));
      log(`已配对设备 ${bonded.length} 个: ${bonded.map(b => b.name).join(', ') || '无'}`);
      setSppListVisible(true);
    } catch (e) {
      log('SPP 读取已配对设备失败: ' + (e instanceof Error ? e.message : String(e)));
      Alert.alert('SPP 错误', e instanceof Error ? e.message : String(e));
    }
  };

  const handleSppConnect = async (d: ScanDev) => {
    setSppConnecting(true);
    log(`--- SPP 连接 ${d.name} (${d.id}) ---`);
    try {
      await sppDisconnect();
      const res = await sppConnect(d.id);
      if (res.connected) {
        log('SPP 连接成功，RFCOMM 通道就绪');
        setSppConnectedAddr(d.id);
        setSppConnectedName(res.name || d.name);
        sppModeRef.current = true;
        setUseSpp(true);
        setDevListVisible(false);
        setSppListVisible(false);
      } else {
        log('SPP 连接失败');
        Alert.alert('连接失败', 'SPP 连接失败');
      }
    } catch (e) {
      log('SPP 连接异常: ' + (e instanceof Error ? e.message : String(e)));
      Alert.alert('连接失败', e instanceof Error ? e.message : String(e));
    } finally {
      setSppConnecting(false);
    }
  };

  const handleSppDisconnect = async () => {
    await sppDisconnect();
    sppModeRef.current = false;
    setUseSpp(false);
    setSppConnectedAddr('');
    setSppConnectedName('');
    log('--- SPP 已断开 ---');
  };

  const handleSppSendHex = async (hexRaw: string) => {
    if (!sppConnectedAddr) { Alert.alert('提示', '请先连接 SPP'); return; }
    try {
      const hex = hexRaw.replace(/\s/g, '');
      const bytes = new Uint8Array(hex.length / 2);
      for (let i = 0; i < bytes.length; i++) {
        bytes[i] = parseInt(hex.substr(i * 2, 2), 16);
      }
      log(`SPP 发送 HEX ${bytes.length} 字节`);
      const chunk = 4096;
      for (let i = 0; i < bytes.length; i += chunk) {
        const part = bytes.slice(i, i + chunk);
        await sppWrite(toBase64(part));
        await new Promise((r) => setTimeout(r, 30));
      }
      log('SPP HEX 发送完成');
    } catch (e) {
      log('SPP HEX 发送失败: ' + (e instanceof Error ? e.message : String(e)));
      Alert.alert('发送失败', e instanceof Error ? e.message : String(e));
    }
  };

  const handleSppTestBlack = async () => {
    // 全黑 320x240 ESC/POS 标签 (GS v 0 m=0 + GS f 960)
    if (!sppConnectedAddr) { Alert.alert('提示', '请先连接 SPP'); return; }
    const w = 40, h = 240;
    const img = new Uint8Array(w * h).fill(0xff);
    const head = new Uint8Array([0x1b, 0x40, 0x1d, 0x76, 0x30, 0x00, w & 0xff, (w >> 8) & 0xff, h & 0xff, (h >> 8) & 0xff]);
    const tail = new Uint8Array([0x1d, 0x66, 0xc0, 0x03]);
    const full = new Uint8Array(head.length + img.length + tail.length);
    full.set(head, 0);
    full.set(img, head.length);
    full.set(tail, head.length + img.length);
    log(`SPP 发送全黑标签 ${full.length} 字节 (320x240)`);
    const chunk = 4096;
    for (let i = 0; i < full.length; i += chunk) {
      const part = full.slice(i, i + chunk);
      await sppWrite(toBase64(part));
      await new Promise((r) => setTimeout(r, 30));
    }
    log('全黑标签发送完成');
  };

  return (
    <View style={styles.container}>
      <View style={styles.topBar}>
        <View style={styles.statusRow}>
          <View style={[styles.dot, connectedName ? styles.dotOn : styles.dotOff]} />
          <Text style={styles.statusText}>{connectedName || '未连接'}</Text>
        </View>
        {writable && <Text style={styles.charInfo}>{writable.service} / {writable.char}</Text>}
      </View>

      <View style={styles.btnRow}>
        <TouchableOpacity style={styles.btn} onPress={handleScan}>
          <Text style={styles.btnText}>扫描</Text>
        </TouchableOpacity>
        <TouchableOpacity style={[styles.btn, { backgroundColor: connectedName ? '#E17055' : '#999' }]} onPress={connectedName ? handleDisconnect : undefined} disabled={!connectedName}>
          <Text style={styles.btnText}>断开</Text>
        </TouchableOpacity>
      </View>

      <Text style={styles.sectionLabel}>SPP (经典蓝牙, HM-T260LR)</Text>
      <View style={styles.btnRow}>
        <TouchableOpacity style={styles.btn} onPress={handleSppScan}>
          <Text style={styles.btnText}>SPP 已配对</Text>
        </TouchableOpacity>
        <TouchableOpacity style={[styles.btn, { backgroundColor: sppConnectedAddr ? '#E17055' : '#999' }]} onPress={sppConnectedAddr ? handleSppDisconnect : undefined} disabled={!sppConnectedAddr}>
          <Text style={styles.btnText}>SPP 断开</Text>
        </TouchableOpacity>
        <TouchableOpacity style={[styles.btn, { backgroundColor: sppConnectedAddr ? '#00B894' : '#999' }]} onPress={sppConnectedAddr ? handleSppTestBlack : undefined} disabled={!sppConnectedAddr}>
          <Text style={styles.btnText}>全黑测试</Text>
        </TouchableOpacity>
      </View>
      <Text style={styles.sppStatus}>{sppConnectedName ? `SPP 已连接: ${sppConnectedName}` : 'SPP 未连接'}</Text>
      <TextInput
        style={styles.cmdInput}
        multiline
        value={hexInput}
        onChangeText={setHexInput}
        autoCapitalize="none"
        autoCorrect={false}
      />
      <TouchableOpacity style={[styles.btn, { backgroundColor: sppConnectedAddr ? '#6C5CE7' : '#999' }]} onPress={() => handleSppSendHex(hexInput)} disabled={!sppConnectedAddr}>
        <Text style={styles.btnText}>SPP 发送 HEX</Text>
      </TouchableOpacity>

      <Text style={styles.sectionLabel}>发送命令 (CPCL 文本)</Text>
      <TextInput
        style={styles.cmdInput}
        multiline
        value={cmd}
        onChangeText={setCmd}
        autoCapitalize="none"
        autoCorrect={false}
      />
      <TouchableOpacity style={[styles.btn, { backgroundColor: connectedName ? '#6C5CE7' : '#999' }]} onPress={() => handleSend()} disabled={!connectedName}>
        <Text style={styles.btnText}>发送</Text>
      </TouchableOpacity>

      <Text style={styles.sectionLabel}>快捷指令</Text>
      <ScrollView horizontal style={styles.quickRow}>
        <TouchableOpacity style={styles.quickBtn} onPress={() => handleSendHex('1B 73 65 74 70 01')}>
          <Text style={styles.quickText}>setp(标签)</Text>
        </TouchableOpacity>
        <TouchableOpacity style={styles.quickBtn} onPress={() => handleSendHex('1B 73 65 74 70 00')}>
          <Text style={styles.quickText}>setp(连续)</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={styles.quickBtn}
          onPress={() => handleSend(`! 0 200 200 200 1\r\nTEXT 4 0 10 10 Hello\r\nFORM\r\nPRINT\r\n`)}
        >
          <Text style={styles.quickText}>TEXT Hello</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={styles.quickBtn}
          onPress={() => handleSend(`! 0 200 200 200 1\r\nBARCODE 128 1 1 40 10 20 ANNE\r\nFORM\r\nPRINT\r\n`)}
        >
          <Text style={styles.quickText}>BARCODE</Text>
        </TouchableOpacity>
      </ScrollView>

      <Text style={styles.sectionLabel}>日志</Text>
      <ScrollView style={styles.logBox}>
        {logs.map((l) => (
          <Text key={l.id} style={styles.logLine}>{l.text}</Text>
        ))}
      </ScrollView>

      <Modal visible={devListVisible} animationType="slide" transparent>
        <View style={styles.modalOverlay}>
          <View style={styles.modalBox}>
            <Text style={styles.modalTitle}>扫描结果</Text>
            {scanning && (
              <View style={styles.scanningRow}>
                <ActivityIndicator size="small" color="#6C5CE7" />
                <Text style={styles.scanningText}>扫描中，已发现 {devices.length} 个...</Text>
              </View>
            )}
            {!scanning && devices.length === 0 && (
              <Text style={styles.emptyText}>未发现设备</Text>
            )}
            <FlatList
              data={devices}
              keyExtractor={(i) => i.id}
              renderItem={({ item }) => (
                <TouchableOpacity style={styles.devItem} onPress={() => handleConnect(item)} disabled={connectingId === item.id}>
                  <View style={{ flex: 1 }}>
                    <Text style={styles.devName}>{item.name}</Text>
                    <Text style={styles.devId}>{item.id} · RSSI {item.rssi ?? '?'}</Text>
                  </View>
                  {connectingId === item.id ? <ActivityIndicator size="small" color="#6C5CE7" /> : <Text style={styles.devConnect}>连接</Text>}
                </TouchableOpacity>
              )}
            />
            <TouchableOpacity style={styles.cancelBtn} onPress={() => { setDevListVisible(false); setScanning(false); manager.stopDeviceScan(); }}>
              <Text style={styles.cancelText}>关闭</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>

      <Modal visible={sppListVisible} animationType="slide" transparent>
        <View style={styles.modalOverlay}>
          <View style={styles.modalBox}>
            <Text style={styles.modalTitle}>已配对设备 (SPP)</Text>
            {sppDevices.length === 0 && (
              <Text style={styles.emptyText}>未找到已配对设备，请先在系统蓝牙中配对打印机</Text>
            )}
            <FlatList
              data={sppDevices}
              keyExtractor={(i) => i.id}
              renderItem={({ item }) => (
                <TouchableOpacity style={styles.devItem} onPress={() => handleSppConnect(item)} disabled={sppConnecting}>
                  <View style={{ flex: 1 }}>
                    <Text style={styles.devName}>{item.name}</Text>
                    <Text style={styles.devId}>{item.id}</Text>
                  </View>
                  {sppConnecting ? <ActivityIndicator size="small" color="#6C5CE7" /> : <Text style={styles.devConnect}>连接</Text>}
                </TouchableOpacity>
              )}
            />
            <TouchableOpacity style={styles.cancelBtn} onPress={() => setSppListVisible(false)}>
              <Text style={styles.cancelText}>关闭</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#F5F6FA', padding: 12 },
  topBar: { backgroundColor: '#fff', borderRadius: 10, padding: 12, marginBottom: 10 },
  statusRow: { flexDirection: 'row', alignItems: 'center' },
  dot: { width: 10, height: 10, borderRadius: 5, marginRight: 8 },
  dotOn: { backgroundColor: '#00B894' },
  dotOff: { backgroundColor: '#E17055' },
  statusText: { fontSize: 15, fontWeight: '600', color: '#333' },
  charInfo: { fontSize: 11, color: '#999', marginTop: 4 },
  sppStatus: { fontSize: 12, color: '#00B894', marginBottom: 6, fontWeight: '600' },
  btnRow: { flexDirection: 'row', gap: 10, marginBottom: 12 },
  btn: { flex: 1, borderRadius: 8, padding: 12, backgroundColor: '#6C5CE7', alignItems: 'center' },
  btnText: { color: '#fff', fontSize: 15, fontWeight: '600' },
  sectionLabel: { fontSize: 13, color: '#666', marginTop: 8, marginBottom: 6 },
  cmdInput: { backgroundColor: '#fff', borderRadius: 8, borderWidth: 1, borderColor: '#E0E0E0', padding: 10, minHeight: 70, fontFamily: 'monospace', fontSize: 12 },
  quickRow: { marginBottom: 8 },
  quickBtn: { backgroundColor: '#E8E6FA', borderRadius: 14, paddingHorizontal: 12, paddingVertical: 8, marginRight: 8 },
  quickText: { color: '#6C5CE7', fontSize: 12, fontWeight: '600' },
  logBox: { flex: 1, backgroundColor: '#1a1a2e', borderRadius: 8, padding: 8 },
  logLine: { color: '#7CFC9A', fontSize: 11, fontFamily: 'monospace', marginBottom: 2 },
  modalOverlay: { flex: 1, backgroundColor: 'rgba(0,0,0,0.5)', justifyContent: 'center' },
  modalBox: { backgroundColor: '#fff', margin: 20, borderRadius: 12, padding: 16, maxHeight: '75%' },
  modalTitle: { fontSize: 16, fontWeight: '600', color: '#333', marginBottom: 10 },
  scanningRow: { flexDirection: 'row', alignItems: 'center', marginBottom: 8 },
  scanningText: { fontSize: 13, color: '#666', marginLeft: 8 },
  emptyText: { color: '#999', fontSize: 14, marginBottom: 12 },
  devItem: { flexDirection: 'row', alignItems: 'center', paddingVertical: 10, borderBottomWidth: 1, borderBottomColor: '#F0F0F0' },
  devName: { fontSize: 14, fontWeight: '600', color: '#333' },
  devId: { fontSize: 11, color: '#999', marginTop: 2 },
  devConnect: { color: '#6C5CE7', fontSize: 13, fontWeight: '600' },
  cancelBtn: { marginTop: 12, borderRadius: 8, padding: 12, backgroundColor: '#F0F0F0', alignItems: 'center' },
  cancelText: { color: '#666', fontSize: 14, fontWeight: '600' },
});