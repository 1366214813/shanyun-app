import React, { useState, useEffect } from 'react';
import {
  View, Text, TouchableOpacity, StyleSheet, FlatList,
  Alert, ActivityIndicator, Modal, ScrollView,
  Platform, PermissionsAndroid,
} from 'react-native';
import { useAppStore, THEMES } from '../store/useAppStore';
import {
  scanSppDevices, connectToDeviceSpp, scanDevices, connectToDevice, printLabel, disconnect, isConnected, getConnectedName,
  queryBattery, getLastBatteryInfo, setOnConnectionChange, checkConnection,
  LABEL_PRESETS, fieldValue, getRandomSlogan,
  type ScannedDevice, type LabelConfig, type LabelData, type BatteryInfo,
} from '../services/PrinterService';
import type { Product } from '../types';

function LabelPreview({ data, config }: { data: LabelData; config: LabelConfig }) {
  const { w, h } = LABEL_PRESETS[config.size];
  const scale = 2.5;
  const boxW = w * scale;
  const boxH = h * scale;

  const elValue = (el: any): string => {
    if (el.type !== 'text') return '';
    if (el.fieldKey) return fieldValue(data, el.fieldKey);
    return el.text || '';
  };

  return (
    <View style={[previewStyles.box, { width: boxW, height: boxH }]}>
      {(config.elements || []).map((el) => {
        const x = el.x * scale;
        const y = el.y * scale;
        const ew = el.w * scale;
        const eh = el.h * scale;
        switch (el.type) {
          case 'text': {
            const value = elValue(el);
            if (!value) return null;
            return (
              <Text key={el.id} style={[previewStyles.text, {
                position: 'absolute', left: x, top: y, width: ew,
                fontSize: (el.fontSizeMm || 4) * scale,
                fontWeight: el.bold ? '700' : '400',
                textAlign: el.align || 'left',
              }]} numberOfLines={3}>{value}</Text>
            );
          }
          case 'barcode': {
            const value = el.fieldKey ? fieldValue(data, el.fieldKey) : (el.text || '');
            return (
              <View key={el.id} style={{ position: 'absolute', left: x, top: y, width: ew, height: eh, alignItems: 'center', justifyContent: 'center' }}>
                <View style={{ flexDirection: 'row', gap: 1, marginBottom: 4 }}>
                  {Array.from({ length: 30 }).map((_, i) => (
                    <View key={i} style={{ width: i % 3 === 0 ? 2 : 1, height: eh * 0.6, backgroundColor: '#000' }} />
                  ))}
                </View>
                <Text style={{ fontSize: 10, fontFamily: 'monospace', color: '#000' }}>{value}</Text>
              </View>
            );
          }
          case 'line':
            return <View key={el.id} style={{ position: 'absolute', left: x, top: y + eh / 2, width: ew, height: (el.thicknessMm || 0.4) * scale, backgroundColor: '#000' }} />;
          case 'rect':
            return <View key={el.id} style={{ position: 'absolute', left: x, top: y, width: ew, height: eh, borderWidth: (el.thicknessMm || 0.4) * scale, borderColor: '#000' }} />;
          default:
            return null;
        }
      })}
    </View>
  );
}

const previewStyles = StyleSheet.create({
  box: { backgroundColor: '#fff', borderWidth: 1, borderColor: '#ddd', borderRadius: 4, padding: 6, alignSelf: 'center', marginVertical: 8, overflow: 'hidden' },
  border: { position: 'absolute', top: 4, left: 4, borderWidth: 1, borderColor: '#ccc', borderRadius: 2 },
  text: { color: '#000', marginBottom: 2 },
  barcode: { marginTop: 4, alignItems: 'center' },
  barcodeText: { fontSize: 10, fontFamily: 'monospace', letterSpacing: 1 },
  qrcode: { width: 30, height: 30, borderWidth: 1, borderColor: '#000', alignItems: 'center', justifyContent: 'center', marginTop: 4 },
  qrText: { fontSize: 8, fontWeight: '700' },
});

export default function PrintScreen({ navigation }: any) {
  const { products, labelConfig, theme, labelTemplates, currentTemplateId, selectLabelTemplate, deleteLabelTemplate } = useAppStore();
  const tc = THEMES[theme];
  const [connected, setConnected] = useState(false);
  const [connectedName, setConnectedName] = useState('');
  const [battery, setBattery] = useState<BatteryInfo | null>(null);
  const [scanning, setScanning] = useState(false);
  const [printing, setPrinting] = useState<string | null>(null);
  const [selectedProducts, setSelectedProducts] = useState<Set<string>>(new Set());
  const [deviceListVisible, setDeviceListVisible] = useState(false);
  const [devices, setDevices] = useState<ScannedDevice[]>([]);
  const [scanError, setScanError] = useState<string | null>(null);
  const [connectingId, setConnectingId] = useState<string | null>(null);
  const [settingsVisible, setSettingsVisible] = useState(false);
  const [config, setConfig] = useState<LabelConfig>(labelConfig);
  const [quantities, setQuantities] = useState<Map<string, number>>(new Map());

  useEffect(() => {
    setConnected(isConnected());
    setConnectedName(getConnectedName());
    setOnConnectionChange((conn) => {
      setConnected(conn);
      if (!conn) setConnectedName('');
    });

    // 定期检查连接是否真实有效
    const connCheck = setInterval(async () => {
      if (isConnected()) {
        const ok = await checkConnection();
        if (!ok) {
          setConnected(false);
          setConnectedName('');
        }
      }
    }, 5000);

    return () => {
      setOnConnectionChange(null);
      clearInterval(connCheck);
    };
  }, []);

  // 定时查询电量
  useEffect(() => {
    if (!connected) {
      setBattery(null);
      return;
    }

    const queryBatteryLevel = async () => {
      const info = await queryBattery();
      if (info) setBattery(info);
    };

    queryBatteryLevel();
    const interval = setInterval(queryBatteryLevel, 30000); // 每30秒查询一次

    return () => clearInterval(interval);
  }, [connected]);

  useEffect(() => { setConfig(labelConfig); }, [labelConfig]);

  const requestBluetoothPerm = async (): Promise<boolean> => {
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
    const ok = await requestBluetoothPerm();
    if (!ok) { Alert.alert('权限不足', '无法获取蓝牙权限'); return; }
    setScanning(true);
    setDevices([]);
    setScanError(null);
    setDeviceListVisible(true);
    try {
      const list = Platform.OS === 'ios' ? await scanDevices() : await scanSppDevices();
      setDevices(list);
      if (list.length === 0) {
        setScanError(Platform.OS === 'ios'
          ? '未发现打印机，请开机并靠近后重试'
          : '未发现已配对设备，请先在系统蓝牙与打印机配对后重试');
      }
    } catch (err) {
      setScanError(err instanceof Error ? err.message : '扫描失败，请检查蓝牙权限');
    } finally {
      setScanning(false);
    }
  };

  const handleSelectDevice = async (device: ScannedDevice) => {
    setConnectingId(device.id);
    const ok = Platform.OS === 'ios'
      ? await connectToDevice(device)
      : await connectToDeviceSpp(device.id, device.name);
    setConnectingId(null);
    if (ok) {
      setDeviceListVisible(false);
    } else {
      Alert.alert('连接失败', `无法连接到 ${device.name}`);
    }
  };

  const handleDisconnect = () => {
    disconnect();
    setConnected(false);
    setConnectedName('');
  };

  const toggleProduct = (id: string) => {
    setSelectedProducts(prev => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id); else next.add(id);
      return next;
    });
  };

  const buildData = (p: Product): LabelData => ({
    name: p.name, code: p.code, category: p.category,
    color: '', size: '', retailPrice: p.retailPrice, purchasePrice: p.purchasePrice,
  });

  const handlePrint = async (product: Product) => {
    if (!connected) { Alert.alert('提示', '请先连接打印机'); return; }
    const qty = quantities.get(product.id) || 1;
    setPrinting(product.id);
    let success = 0;
    for (let i = 0; i < qty; i++) {
      const ok = await printLabel(buildData(product), config);
      if (ok) success++;
      if (i < qty - 1) await new Promise(r => setTimeout(r, 200));
    }
    setPrinting(null);
    if (success === qty) Alert.alert('成功', `已打印 ${qty} 张: ${product.name}`);
    else Alert.alert('完成', `打印 ${success}/${qty} 张: ${product.name}`);
  };

  const handleBatchPrint = async () => {
    if (!connected) { Alert.alert('提示', '请先连接打印机'); return; }
    const selected = products.filter(p => selectedProducts.has(p.id));
    if (selected.length === 0) { Alert.alert('提示', '请选择要打印的商品'); return; }

    setPrinting('batch');
    let success = 0;
    let total = 0;
    for (const p of selected) {
      const qty = quantities.get(p.id) || 1;
      total += qty;
      for (let i = 0; i < qty; i++) {
        const ok = await printLabel(buildData(p), config);
        if (ok) success++;
        if (i < qty - 1) await new Promise(r => setTimeout(r, 200));
      }
      await new Promise(r => setTimeout(r, 200));
    }
    setPrinting(null);
    Alert.alert('完成', `成功打印 ${success}/${total} 张吊牌`);
    setSelectedProducts(new Set());
  };

  const sampleData: LabelData = {
    name: '示例连衣裙', code: 'YL2026001', category: '女装',
    color: '白色', size: 'M', retailPrice: 299, purchasePrice: 159,
  };

  const renderItem = ({ item }: { item: Product }) => {
    const isSelected = selectedProducts.has(item.id);
    const isPrinting = printing === item.id;
    const qty = quantities.get(item.id) || 1;
    const isAbnormal = item.retailPrice <= 0 || !item.category || item.category === '未分类';
    return (
      <TouchableOpacity
        style={[styles.productItem, isSelected && styles.productSelected, isAbnormal && styles.productAbnormal]}
        onPress={() => toggleProduct(item.id)}
        activeOpacity={0.7}
      >
        <View style={styles.productInfo}>
          <Text style={styles.productName} numberOfLines={1}>{item.name}</Text>
          <Text style={styles.productMeta}>{item.code} | {item.category || '未分类'}</Text>
          <View style={{ flexDirection: 'row', alignItems: 'center', gap: 6, marginTop: 4 }}>
            <Text style={styles.productPrice}>¥{item.retailPrice}</Text>
            {isAbnormal && <Text style={styles.abnormalBadge}>⚠ 异常</Text>}
          </View>
        </View>
        <View style={styles.qtyRow}>
          <TouchableOpacity
            style={styles.qtyBtn}
            onPress={() => setQuantities(prev => {
              const next = new Map(prev);
              next.set(item.id, Math.max(1, (next.get(item.id) || 1) - 1));
              return next;
            })}
          >
            <Text style={styles.qtyBtnText}>-</Text>
          </TouchableOpacity>
          <Text style={styles.qtyText}>{qty}</Text>
          <TouchableOpacity
            style={styles.qtyBtn}
            onPress={() => setQuantities(prev => {
              const next = new Map(prev);
              next.set(item.id, (next.get(item.id) || 1) + 1);
              return next;
            })}
          >
            <Text style={styles.qtyBtnText}>+</Text>
          </TouchableOpacity>
        </View>
        <TouchableOpacity
          style={[styles.printBtn, isPrinting && styles.printingBtn]}
          onPress={() => handlePrint(item)}
          disabled={isPrinting || !connected}
        >
          {isPrinting ? (
            <ActivityIndicator size="small" color="#fff" />
          ) : (
            <Text style={styles.printBtnText}>打印</Text>
          )}
        </TouchableOpacity>
      </TouchableOpacity>
    );
  };

  const renderDevice = ({ item }: { item: ScannedDevice }) => {
    const isConnecting = connectingId === item.id;
    return (
      <TouchableOpacity style={styles.deviceItem} onPress={() => handleSelectDevice(item)} disabled={isConnecting}>
        <View style={styles.deviceInfo}>
          <Text style={styles.deviceName}>{item.name}</Text>
          <Text style={styles.deviceId}>{item.id.slice(-8)} · RSSI {item.rssi ?? '?'}</Text>
        </View>
        {isConnecting ? <ActivityIndicator size="small" color="#6C5CE7" /> : <Text style={styles.deviceConnect}>连接</Text>}
      </TouchableOpacity>
    );
  };

  return (
    <View style={[styles.container, { backgroundColor: tc.bg }]}>
      <View style={styles.connBar}>
        <View style={styles.connStatus}>
          <View style={[styles.connDot, { backgroundColor: connected ? '#00B894' : '#E17055' }]} />
          <Text style={styles.connText}>{connected ? connectedName || '已连接' : '未连接'}</Text>
          {!connected && (
            <TouchableOpacity style={styles.connectNowBtn} onPress={handleScan}>
              <Text style={styles.connectNowBtnText}>连接</Text>
            </TouchableOpacity>
          )}
          {connected && battery && (
            <View style={styles.batteryContainer}>
              <View style={styles.batteryIcon}>
                <View style={[styles.batteryLevel, {
                  width: `${Math.max(battery.level, 10)}%`,
                  backgroundColor: battery.level > 20 ? '#00B894' : '#E17055',
                }]} />
              </View>
              <Text style={styles.batteryText}>{battery.level}%</Text>
            </View>
          )}
        </View>
        <View style={styles.connBtns}>
          <TouchableOpacity style={styles.settingsBtn} onPress={() => setSettingsVisible(true)}>
            <Text style={styles.settingsBtnText}>标签设置</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={[styles.connBtn, scanning && styles.connBtnActive]}
            onPress={connected ? handleDisconnect : handleScan}
            disabled={scanning}
          >
            <Text style={styles.connBtnText}>{scanning ? '扫描中...' : connected ? '断开' : '搜索'}</Text>
          </TouchableOpacity>
        </View>
      </View>

      {selectedProducts.size > 0 && (() => {
        const totalQty = Array.from(selectedProducts).reduce((sum, id) => sum + (quantities.get(id) || 1), 0);
        return (
          <TouchableOpacity style={styles.batchBtn} onPress={handleBatchPrint} disabled={printing === 'batch'}>
            {printing === 'batch' ? (
              <ActivityIndicator size="small" color="#fff" />
            ) : (
              <Text style={styles.batchBtnText}>批量打印 ({selectedProducts.size} 种, {totalQty} 张)</Text>
            )}
          </TouchableOpacity>
        );
      })()}

      <FlatList
        data={products}
        keyExtractor={(i) => i.id}
        renderItem={renderItem}
        contentContainerStyle={{ paddingBottom: 20 }}
        ListEmptyComponent={
          <View style={styles.empty}><Text style={styles.emptyText}>暂无商品，请先入库</Text></View>
        }
      />

      <Modal visible={deviceListVisible} animationType="slide" transparent>
        <TouchableOpacity style={styles.modalOverlay} activeOpacity={1} onPress={() => { setDeviceListVisible(false); setScanning(false); }}>
          <View style={styles.modalContent} onStartShouldSetResponder={() => true}>
            <Text style={styles.modalTitle}>选择打印机</Text>
            {scanning && (
              <View style={styles.scanningRow}>
                <ActivityIndicator size="small" color="#6C5CE7" />
                <Text style={styles.scanningText}>扫描中，发现 {devices.length} 个设备...</Text>
              </View>
            )}
            {!scanning && devices.length === 0 && (
              <Text style={[styles.emptyText, scanError ? styles.emptyErrorText : null]}>{scanError || '未发现蓝牙设备，请确认打印机已开机'}</Text>
            )}
            <FlatList data={devices} keyExtractor={(i) => i.id} renderItem={renderDevice} style={styles.deviceList} />
            <TouchableOpacity style={styles.btnCancel} onPress={() => { setDeviceListVisible(false); setScanning(false); }}>
              <Text>取消</Text>
            </TouchableOpacity>
          </View>
        </TouchableOpacity>
      </Modal>

      <Modal visible={settingsVisible} animationType="slide" transparent>
        <View style={styles.modalOverlay}>
          <View style={[styles.modalContent, { maxHeight: '85%' }]}>
            <Text style={styles.modalTitle}>标签模板</Text>
            <ScrollView style={{ maxHeight: 300 }}>
              {labelTemplates.map((t) => {
                const active = t.id === currentTemplateId;
                return (
                  <TouchableOpacity
                    key={t.id}
                    style={[styles.tplCard, active && { borderColor: tc.primary, backgroundColor: '#F0EDFF' }]}
                    onPress={() => { selectLabelTemplate(t.id); setConfig(t.config); }}
                  >
                    <View style={{ flex: 1 }}>
                      <Text style={[styles.tplName, { color: active ? tc.primary : '#333' }]}>{t.name}</Text>
                      <Text style={styles.tplMeta}>{t.config.size} · {t.config.elements?.length || 0} 个元素</Text>
                    </View>
                    <View style={styles.tplBtns}>
                      <TouchableOpacity
                        style={styles.tplBtn}
                        onPress={() => { setSettingsVisible(false); navigation.navigate('标签编辑', { templateId: t.id, templateName: t.name }); }}
                      >
                        <Text style={styles.tplBtnText}>编辑</Text>
                      </TouchableOpacity>
                      {t.id !== 'default' && (
                        <TouchableOpacity
                          style={styles.tplBtnDel}
                          onPress={() => Alert.alert('删除模板', `删除「${t.name}」？`, [
                            { text: '取消', style: 'cancel' },
                            { text: '删除', style: 'destructive', onPress: () => { deleteLabelTemplate(t.id); setConfig(labelConfig); } },
                          ])}
                        >
                          <Text style={styles.tplBtnDelText}>删除</Text>
                        </TouchableOpacity>
                      )}
                    </View>
                  </TouchableOpacity>
                );
              })}
            </ScrollView>
            <View style={styles.tplActions}>
              <TouchableOpacity style={styles.btnAddTpl} onPress={() => { setSettingsVisible(false); navigation.navigate('标签编辑', { templateId: null, templateName: '新模板' }); }}>
                <Text style={styles.btnAddTplText}>＋ 新建模板</Text>
              </TouchableOpacity>
            </View>
            <Text style={styles.tplHint}>当前使用: {labelTemplates.find(t => t.id === currentTemplateId)?.name || '未命名'}</Text>
            <LabelPreview data={sampleData} config={config} />
            <View style={styles.modalBtns}>
              <TouchableOpacity style={styles.btnCancel} onPress={() => setSettingsVisible(false)}>
                <Text>关闭</Text>
              </TouchableOpacity>
              <TouchableOpacity style={styles.btnConfirm} onPress={() => { setSettingsVisible(false); navigation.navigate('标签编辑', { templateId: currentTemplateId }); }}>
                <Text style={{ color: '#fff' }}>编辑当前模板</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#F5F6FA', padding: 16 },
  connBar: {
    flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center',
    backgroundColor: '#fff', borderRadius: 12, padding: 12, marginBottom: 12,
  },
  connStatus: { flexDirection: 'row', alignItems: 'center', gap: 8 },
  connDot: { width: 8, height: 8, borderRadius: 4 },
  connText: { fontSize: 14, color: '#333' },
  connectNowBtn: { backgroundColor: '#E17055', borderRadius: 6, paddingHorizontal: 10, paddingVertical: 4, marginLeft: 8 },
  connectNowBtnText: { color: '#fff', fontSize: 12, fontWeight: '600' },
  batteryContainer: { flexDirection: 'row', alignItems: 'center', gap: 4, marginLeft: 8 },
  batteryIcon: { width: 24, height: 12, borderWidth: 1, borderColor: '#999', borderRadius: 2, padding: 1 },
  batteryLevel: { height: '100%', borderRadius: 1 },
  batteryText: { fontSize: 11, color: '#666' },
  connBtns: { flexDirection: 'row', gap: 8 },
  settingsBtn: { backgroundColor: '#F0EDFF', borderRadius: 8, paddingHorizontal: 12, paddingVertical: 8, borderWidth: 1, borderColor: '#6C5CE7' },
  settingsBtnText: { color: '#6C5CE7', fontSize: 13, fontWeight: '600' },
  connBtn: { backgroundColor: '#6C5CE7', borderRadius: 8, paddingHorizontal: 14, paddingVertical: 8 },
  connBtnActive: { backgroundColor: '#999' },
  connBtnText: { color: '#fff', fontSize: 13, fontWeight: '600' },
  batchBtn: { backgroundColor: '#00B894', borderRadius: 12, padding: 14, alignItems: 'center', marginBottom: 12 },
  batchBtnText: { color: '#fff', fontSize: 15, fontWeight: '600' },
  productItem: {
    flexDirection: 'row', alignItems: 'center', backgroundColor: '#fff',
    borderRadius: 10, padding: 12, marginBottom: 8, borderWidth: 1, borderColor: 'transparent',
  },
  productSelected: { borderColor: '#6C5CE7', backgroundColor: '#F8F6FF' },
  productAbnormal: { borderColor: '#FF6B6B', borderLeftWidth: 3 },
  productInfo: { flex: 1 },
  productName: { fontSize: 15, fontWeight: '600', color: '#333', marginBottom: 4 },
  productMeta: { fontSize: 12, color: '#999' },
  productPrice: { fontSize: 14, color: '#E17055', fontWeight: '600' },
  abnormalBadge: { fontSize: 11, color: '#FF6B6B', fontWeight: '600', backgroundColor: '#FFF0F0', paddingHorizontal: 6, paddingVertical: 2, borderRadius: 4 },
  qtyRow: { flexDirection: 'row', alignItems: 'center', marginRight: 8 },
  qtyBtn: { width: 44, height: 44, borderRadius: 22, backgroundColor: '#F0F0F0', alignItems: 'center', justifyContent: 'center' },
  qtyBtnText: { fontSize: 18, fontWeight: '700', color: '#333' },
  qtyText: { fontSize: 15, fontWeight: '700', color: '#333', marginHorizontal: 8, minWidth: 24, textAlign: 'center' },
  printBtn: { backgroundColor: '#6C5CE7', borderRadius: 8, paddingHorizontal: 14, paddingVertical: 8, minWidth: 60, alignItems: 'center' },
  printingBtn: { backgroundColor: '#999' },
  printBtnText: { color: '#fff', fontSize: 13, fontWeight: '600' },
  empty: { alignItems: 'center', marginTop: 60 },
  emptyText: { color: '#999', fontSize: 14 },
  emptyErrorText: { color: '#FF6B6B', fontSize: 14 },
  modalOverlay: { flex: 1, backgroundColor: 'rgba(0,0,0,0.5)', justifyContent: 'flex-end' },
  modalContent: { backgroundColor: '#fff', borderTopLeftRadius: 20, borderTopRightRadius: 20, padding: 20, maxHeight: '70%' },
  modalTitle: { fontSize: 18, fontWeight: '600', marginBottom: 12, textAlign: 'center' },
  scanningRow: { flexDirection: 'row', alignItems: 'center', gap: 8, marginBottom: 12, justifyContent: 'center' },
  scanningText: { fontSize: 13, color: '#666' },
  deviceList: { maxHeight: 300 },
  deviceItem: { flexDirection: 'row', alignItems: 'center', paddingVertical: 14, borderBottomWidth: 1, borderBottomColor: '#F0F0F0' },
  deviceInfo: { flex: 1 },
  deviceName: { fontSize: 15, fontWeight: '600', color: '#333' },
  deviceId: { fontSize: 12, color: '#999', marginTop: 2 },
  deviceConnect: { color: '#6C5CE7', fontSize: 14, fontWeight: '600' },
  btnCancel: { flex: 1, padding: 12, borderRadius: 8, backgroundColor: '#F0F0F0', alignItems: 'center' },
  btnConfirm: { flex: 1, padding: 12, borderRadius: 8, backgroundColor: '#6C5CE7', alignItems: 'center' },
  templateHint: { fontSize: 12, color: '#999', textAlign: 'center', marginVertical: 8 },
  tplCard: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', borderWidth: 1, borderColor: '#E0E0E0', borderRadius: 10, padding: 10, marginBottom: 8, backgroundColor: '#fff' },
  tplName: { fontSize: 14, fontWeight: '600' },
  tplMeta: { fontSize: 11, color: '#999', marginTop: 2 },
  tplBtns: { flexDirection: 'row', gap: 6 },
  tplBtn: { backgroundColor: '#F0EDFF', borderRadius: 6, paddingHorizontal: 10, paddingVertical: 5 },
  tplBtnText: { color: '#6C5CE7', fontSize: 12, fontWeight: '600' },
  tplBtnDel: { backgroundColor: '#FFF0F0', borderRadius: 6, paddingHorizontal: 10, paddingVertical: 5 },
  tplBtnDelText: { color: '#E17055', fontSize: 12, fontWeight: '600' },
  tplActions: { flexDirection: 'row', marginBottom: 8 },
  btnAddTpl: { flex: 1, borderWidth: 1, borderColor: '#6C5CE7', borderStyle: 'dashed', borderRadius: 10, padding: 10, alignItems: 'center' },
  btnAddTplText: { color: '#6C5CE7', fontSize: 13, fontWeight: '600' },
  tplHint: { fontSize: 12, color: '#6C5CE7', textAlign: 'center', marginBottom: 8 },
  modalBtns: { flexDirection: 'row', justifyContent: 'space-between', marginTop: 8, gap: 12 },
  sectionTitle: { fontSize: 14, fontWeight: '600', color: '#333', marginTop: 16, marginBottom: 8 },
  sizeGrid: { flexDirection: 'row', flexWrap: 'wrap', gap: 8 },
  sizeBtn: { paddingVertical: 8, paddingHorizontal: 12, borderRadius: 8, borderWidth: 1, borderColor: '#E0E0E0', backgroundColor: '#FAFAFA' },
  sizeBtnActive: { borderColor: '#6C5CE7', backgroundColor: '#F0EDFF' },
  sizeBtnText: { fontSize: 13, color: '#333', fontWeight: '500' },
  sizeBtnTextActive: { color: '#6C5CE7' },
  sizeBtnSub: { fontSize: 11, color: '#999', marginTop: 2 },
  fieldRow: { flexDirection: 'row', alignItems: 'center', gap: 10, paddingVertical: 8, borderBottomWidth: 1, borderBottomColor: '#F5F5F5' },
  fieldLabel: { flex: 1, fontSize: 14, color: '#333' },
  fontSizeBtns: { flexDirection: 'row', gap: 4 },
  fontSizeBtn: { paddingHorizontal: 8, paddingVertical: 4, borderRadius: 4, backgroundColor: '#F0F0F0' },
  fontSizeBtnActive: { backgroundColor: '#6C5CE7' },
  fontSizeBtnText: { fontSize: 11, color: '#666' },
  fontSizeBtnTextActive: { color: '#fff' },
});
