import React, { useState, useEffect, useMemo } from 'react';
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

const ITEM_HEIGHT = 84;

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

type SortKey = 'time' | 'name' | 'code' | 'price' | 'stock';
const SORT_OPTIONS: { key: SortKey; label: string }[] = [
  { key: 'time', label: '最新' },
  { key: 'name', label: '名称' },
  { key: 'code', label: '款号' },
  { key: 'price', label: '价格↓' },
  { key: 'stock', label: '库存↑' },
];

export default function PrintScreen({ navigation }: any) {
  const { products, currentStoreId, labelConfig, theme, labelTemplates, currentTemplateId, selectLabelTemplate, deleteLabelTemplate } = useAppStore();
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
  const [sortBy, setSortBy] = useState<SortKey>('time');
  const [catFilter, setCatFilter] = useState('all');
  const [connExpanded, setConnExpanded] = useState(false);

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

  // 分类 + 排序（按当前店铺过滤，避免多店铺数据串台）
  const categories = useMemo(
    () => ['all', ...new Set(products.filter((p) => !p.storeId || p.storeId === currentStoreId).map((p) => p.category).filter(Boolean))],
    [products, currentStoreId],
  );

  const sortedProducts = useMemo(() => {
    const list = products.filter((p) => !p.storeId || p.storeId === currentStoreId);
    const filtered = catFilter === 'all' ? list : list.filter((p) => (p.category || '未分类') === catFilter);
    return [...filtered].sort((a, b) => {
      switch (sortBy) {
        case 'name': return a.name.localeCompare(b.name, 'zh-CN');
        case 'code': return a.code.localeCompare(b.code);
        case 'price': return b.retailPrice - a.retailPrice;
        case 'stock': return a.stock - b.stock;
        case 'time': return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
        default: return 0;
      }
    });
  }, [products, currentStoreId, catFilter, sortBy]);

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
    setConnExpanded(false);
  };

  const toggleProduct = (id: string) => {
    setSelectedProducts(prev => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id); else next.add(id);
      return next;
    });
  };

  const changeQty = (id: string, delta: number) => {
    setQuantities(prev => {
      const next = new Map(prev);
      next.set(id, Math.max(1, Math.min(99, (next.get(id) || 1) + delta)));
      return next;
    });
  };

  const visibleIds = sortedProducts.map((p) => p.id);
  const allSelected = visibleIds.length > 0 && visibleIds.every((id) => selectedProducts.has(id));
  const toggleAll = () => {
    setSelectedProducts(prev => {
      const next = new Set(prev);
      if (allSelected) visibleIds.forEach((id) => next.delete(id));
      else visibleIds.forEach((id) => next.add(id));
      return next;
    });
  };
  const clearSelection = () => setSelectedProducts(new Set());

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
        style={[
          styles.productItem,
          { backgroundColor: tc.card, borderColor: tc.border },
          isSelected && { borderColor: tc.primary, backgroundColor: tc.primaryLight },
          isAbnormal && { borderLeftColor: tc.danger, borderLeftWidth: 3 },
        ]}
        onPress={() => toggleProduct(item.id)}
        activeOpacity={0.7}
      >
        <View style={styles.productInfo}>
          <View style={{ flexDirection: 'row', alignItems: 'center', gap: 6 }}>
            <Text style={[styles.productName, { color: tc.text }]} numberOfLines={1}>{item.name}</Text>
            {isAbnormal && <Text style={[styles.abnormalBadge, { color: tc.danger }]}>⚠ 异常</Text>}
          </View>
          <Text style={[styles.productMeta, { color: tc.subText }]}>{item.code} · {item.category || '未分类'}</Text>
          <View style={{ flexDirection: 'row', alignItems: 'center', gap: 8, marginTop: 4 }}>
            <Text style={[styles.productPrice, { color: tc.primary }]}>¥{item.retailPrice}</Text>
            <Text style={[styles.productStock, { color: tc.subText }]}>库存 {item.stock}</Text>
          </View>
        </View>
        <View style={[styles.qtyPill, { backgroundColor: tc.bg, borderColor: tc.border }]}>
          <TouchableOpacity style={styles.qtyBtn} onPress={() => changeQty(item.id, -1)} hitSlop={{ top: 6, bottom: 6, left: 6, right: 6 }}>
            <Text style={[styles.qtyBtnText, { color: tc.text }]}>−</Text>
          </TouchableOpacity>
          <Text style={[styles.qtyText, { color: tc.text }]}>{qty}</Text>
          <TouchableOpacity style={styles.qtyBtn} onPress={() => changeQty(item.id, 1)} hitSlop={{ top: 6, bottom: 6, left: 6, right: 6 }}>
            <Text style={[styles.qtyBtnText, { color: tc.text }]}>+</Text>
          </TouchableOpacity>
        </View>
        <TouchableOpacity
          style={[styles.printBtn, { backgroundColor: connected ? tc.primary : tc.border }, isPrinting && { backgroundColor: tc.subText }]}
          onPress={() => handlePrint(item)}
          disabled={isPrinting || !connected}
        >
          {isPrinting ? (
            <ActivityIndicator size="small" color="#fff" />
          ) : (
            <Text style={[styles.printBtnText, { color: connected ? '#fff' : tc.subText }]}>打印</Text>
          )}
        </TouchableOpacity>
      </TouchableOpacity>
    );
  };

  const renderDevice = ({ item }: { item: ScannedDevice }) => {
    const isConnecting = connectingId === item.id;
    return (
      <TouchableOpacity style={[styles.deviceItem, { borderBottomColor: tc.border }]} onPress={() => handleSelectDevice(item)} disabled={isConnecting}>
        <View style={styles.deviceInfo}>
          <Text style={[styles.deviceName, { color: tc.text }]}>{item.name}</Text>
          <Text style={[styles.deviceId, { color: tc.subText }]}>{item.id.slice(-8)} · RSSI {item.rssi ?? '?'}</Text>
        </View>
        {isConnecting ? <ActivityIndicator size="small" color={tc.primary} /> : <Text style={[styles.deviceConnect, { color: tc.primary }]}>连接</Text>}
      </TouchableOpacity>
    );
  };

  const totalQty = Array.from(selectedProducts).reduce((sum, id) => sum + (quantities.get(id) || 1), 0);

  return (
    <View style={[styles.container, { backgroundColor: tc.bg }]}>
      {/* 连接状态条：点击可展开详情 */}
      <TouchableOpacity
        style={[styles.connBar, { backgroundColor: tc.card }]}
        onPress={() => connected && setConnExpanded((v) => !v)}
        activeOpacity={connected ? 0.7 : 1}
      >
        <View style={styles.connRow}>
          <View style={styles.connStatus}>
            <View style={[styles.connDot, { backgroundColor: connected ? '#00B894' : tc.danger }]} />
            <Text style={[styles.connText, { color: tc.text }]}>{connected ? connectedName || '已连接' : '未连接'}</Text>
            {connected && battery && (
              <View style={styles.batteryContainer}>
                <View style={styles.batteryIcon}>
                  <View style={[styles.batteryLevel, {
                    width: `${Math.max(battery.level, 10)}%`,
                    backgroundColor: battery.level > 20 ? '#00B894' : tc.danger,
                  }]} />
                </View>
                <Text style={[styles.batteryText, { color: tc.subText }]}>{battery.level}%</Text>
              </View>
            )}
            {connected && (
              <Text style={[styles.connChevron, { color: tc.subText }]}>{connExpanded ? '收起' : '详情'}</Text>
            )}
          </View>
          <View style={styles.connBtns}>
            <TouchableOpacity style={[styles.settingsBtn, { borderColor: tc.primary }]} onPress={() => setSettingsVisible(true)}>
              <Text style={[styles.settingsBtnText, { color: tc.primary }]}>标签设置</Text>
            </TouchableOpacity>
            <TouchableOpacity
              style={[styles.connBtn, { backgroundColor: tc.primary }, scanning && { backgroundColor: tc.subText }]}
              onPress={connected ? handleDisconnect : handleScan}
              disabled={scanning}
            >
              <Text style={styles.connBtnText}>{scanning ? '扫描中...' : connected ? '断开' : '搜索'}</Text>
            </TouchableOpacity>
          </View>
        </View>

        {connected && connExpanded && (
          <View style={[styles.connDetail, { borderTopColor: tc.border }]}>
            <Text style={[styles.connDetailText, { color: tc.subText }]}>设备：{connectedName || '未知'}</Text>
            <Text style={[styles.connDetailText, { color: tc.subText }]}>
              通道：{Platform.OS === 'ios' ? 'BLE' : '经典蓝牙 SPP (RFCOMM)'}
            </Text>
            <Text style={[styles.connDetailText, { color: tc.subText }]}>
              电量：{battery ? `${battery.level}%` : 'SPP 模式不支持查询'}
            </Text>
          </View>
        )}

        {!connected && (
          <TouchableOpacity style={[styles.connectNowBtn, { backgroundColor: tc.danger }]} onPress={handleScan}>
            <Text style={styles.connectNowBtnText}>点此搜索并连接打印机</Text>
          </TouchableOpacity>
        )}
      </TouchableOpacity>

      {/* 分类筛选（横向滚动，不再被裁切） */}
      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        style={styles.filterRow}
        contentContainerStyle={{ paddingHorizontal: 0, gap: 6 }}
      >
        {categories.map((c) => {
          const active = catFilter === c;
          return (
            <TouchableOpacity
              key={c}
              style={[styles.chip, { borderColor: active ? tc.primary : tc.border, backgroundColor: active ? tc.primary : tc.card }]}
              onPress={() => setCatFilter(c)}
            >
              <Text style={[styles.chipText, { color: active ? '#fff' : tc.subText }]}>{c === 'all' ? '全部' : c}</Text>
            </TouchableOpacity>
          );
        })}
      </ScrollView>

      {/* 排序 + 全选 */}
      <View style={styles.sortRow}>
        <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={{ gap: 6 }}>
          {SORT_OPTIONS.map((o) => {
            const active = sortBy === o.key;
            return (
              <TouchableOpacity
                key={o.key}
                style={[styles.sortChip, { borderColor: active ? tc.primary : tc.border, backgroundColor: active ? tc.primaryLight : tc.card }]}
                onPress={() => setSortBy(o.key)}
              >
                <Text style={[styles.sortChipText, { color: active ? tc.primary : tc.subText }]}>{o.label}</Text>
              </TouchableOpacity>
            );
          })}
        </ScrollView>
        <TouchableOpacity onPress={toggleAll} style={styles.selectAllBtn}>
          <Text style={[styles.selectAllText, { color: tc.primary }]}>{allSelected ? '取消全选' : '全选'}</Text>
        </TouchableOpacity>
      </View>

      <FlatList
        data={sortedProducts}
        keyExtractor={(i) => i.id}
        renderItem={renderItem}
        getItemLayout={(_, index) => ({ length: ITEM_HEIGHT, offset: ITEM_HEIGHT * index, index })}
        contentContainerStyle={{ paddingBottom: selectedProducts.size > 0 ? 96 : 20 }}
        ListEmptyComponent={
          <View style={styles.empty}><Text style={[styles.emptyText, { color: tc.subText }]}>暂无商品，请先入库</Text></View>
        }
      />

      {/* 底部常驻汇总栏 */}
      {selectedProducts.size > 0 && (
        <View style={[styles.summaryBar, { backgroundColor: tc.card, borderTopColor: tc.border }]}>
          <View style={{ flex: 1 }}>
            <Text style={[styles.summaryText, { color: tc.text }]}>
              已选 <Text style={{ color: tc.primary, fontWeight: '700' }}>{selectedProducts.size}</Text> 种 · 共 <Text style={{ color: tc.primary, fontWeight: '700' }}>{totalQty}</Text> 张
            </Text>
          </View>
          <TouchableOpacity onPress={clearSelection} style={styles.summaryClear}>
            <Text style={[styles.summaryClearText, { color: tc.subText }]}>清空</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={[styles.summaryBtn, { backgroundColor: connected ? tc.primary : tc.border }]}
            onPress={handleBatchPrint}
            disabled={printing === 'batch' || !connected}
          >
            {printing === 'batch'
              ? <ActivityIndicator size="small" color="#fff" />
              : <Text style={[styles.summaryBtnText, { color: connected ? '#fff' : tc.subText }]}>批量打印</Text>}
          </TouchableOpacity>
        </View>
      )}

      <Modal visible={deviceListVisible} animationType="slide" transparent>
        <TouchableOpacity style={styles.modalOverlay} activeOpacity={1} onPress={() => { setDeviceListVisible(false); setScanning(false); }}>
          <View style={[styles.modalContent, { backgroundColor: tc.card }]} onStartShouldSetResponder={() => true}>
            <Text style={[styles.modalTitle, { color: tc.text }]}>选择打印机</Text>
            {scanning && (
              <View style={styles.scanningRow}>
                <ActivityIndicator size="small" color={tc.primary} />
                <Text style={[styles.scanningText, { color: tc.subText }]}>扫描中，发现 {devices.length} 个设备...</Text>
              </View>
            )}
            {!scanning && devices.length === 0 && (
              <Text style={[styles.emptyText, { color: tc.subText }, scanError && { color: tc.danger }]}>{scanError || '未发现蓝牙设备，请确认打印机已开机'}</Text>
            )}
            <FlatList data={devices} keyExtractor={(i) => i.id} renderItem={renderDevice} style={styles.deviceList} />
            <TouchableOpacity style={[styles.btnCancel, { backgroundColor: tc.border }]} onPress={() => { setDeviceListVisible(false); setScanning(false); }}>
              <Text style={{ color: tc.text }}>取消</Text>
            </TouchableOpacity>
          </View>
        </TouchableOpacity>
      </Modal>

      <Modal visible={settingsVisible} animationType="slide" transparent>
        <View style={styles.modalOverlay}>
          <View style={[styles.modalContent, { backgroundColor: tc.card, maxHeight: '85%' }]}>
            <Text style={[styles.modalTitle, { color: tc.text }]}>标签模板</Text>
            <ScrollView style={{ maxHeight: 300 }}>
              {labelTemplates.map((t) => {
                const active = t.id === currentTemplateId;
                return (
                  <TouchableOpacity
                    key={t.id}
                    style={[styles.tplCard, { borderColor: active ? tc.primary : tc.border }, active && { backgroundColor: tc.primaryLight }]}
                    onPress={() => { selectLabelTemplate(t.id); setConfig(t.config); }}
                  >
                    <View style={{ flex: 1 }}>
                      <Text style={[styles.tplName, { color: active ? tc.primary : tc.text }]}>{t.name}</Text>
                      <Text style={[styles.tplMeta, { color: tc.subText }]}>{t.config.size} · {t.config.elements?.length || 0} 个元素</Text>
                    </View>
                    <View style={styles.tplBtns}>
                      <TouchableOpacity
                        style={[styles.tplBtn, { backgroundColor: tc.primaryLight }]}
                        onPress={() => { setSettingsVisible(false); navigation.navigate('标签编辑', { templateId: t.id, templateName: t.name }); }}
                      >
                        <Text style={[styles.tplBtnText, { color: tc.primary }]}>编辑</Text>
                      </TouchableOpacity>
                      {t.id !== 'default' && !t.builtin && (
                        <TouchableOpacity
                          style={[styles.tplBtnDel, { backgroundColor: tc.primaryLight }]}
                          onPress={() => Alert.alert('删除模板', `删除「${t.name}」？`, [
                            { text: '取消', style: 'cancel' },
                            { text: '删除', style: 'destructive', onPress: () => { deleteLabelTemplate(t.id); setConfig(labelConfig); } },
                          ])}
                        >
                          <Text style={[styles.tplBtnDelText, { color: tc.danger }]}>删除</Text>
                        </TouchableOpacity>
                      )}
                    </View>
                  </TouchableOpacity>
                );
              })}
            </ScrollView>
            <View style={styles.tplActions}>
              <TouchableOpacity style={[styles.btnAddTpl, { borderColor: tc.primary }]} onPress={() => { setSettingsVisible(false); navigation.navigate('标签编辑', { templateId: null, templateName: '新模板' }); }}>
                <Text style={[styles.btnAddTplText, { color: tc.primary }]}>＋ 新建模板</Text>
              </TouchableOpacity>
            </View>
            <Text style={[styles.tplHint, { color: tc.primary }]}>当前使用: {labelTemplates.find(t => t.id === currentTemplateId)?.name || '未命名'}</Text>
            <LabelPreview data={sampleData} config={config} />
            <View style={styles.modalBtns}>
              <TouchableOpacity style={[styles.btnCancel, { backgroundColor: tc.border }]} onPress={() => setSettingsVisible(false)}>
                <Text style={{ color: tc.text }}>关闭</Text>
              </TouchableOpacity>
              <TouchableOpacity style={[styles.btnConfirm, { backgroundColor: tc.primary }]} onPress={() => { setSettingsVisible(false); navigation.navigate('标签编辑', { templateId: currentTemplateId }); }}>
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
  container: { flex: 1, padding: 16 },
  connBar: { borderRadius: 12, padding: 12, marginBottom: 10 },
  connRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  connStatus: { flexDirection: 'row', alignItems: 'center', gap: 8, flexShrink: 1 },
  connDot: { width: 8, height: 8, borderRadius: 4 },
  connText: { fontSize: 14, fontWeight: '600' },
  connChevron: { fontSize: 11, marginLeft: 2 },
  batteryContainer: { flexDirection: 'row', alignItems: 'center', gap: 4, marginLeft: 6 },
  batteryIcon: { width: 22, height: 11, borderWidth: 1, borderColor: '#999', borderRadius: 2, padding: 1 },
  batteryLevel: { height: '100%', borderRadius: 1 },
  batteryText: { fontSize: 11 },
  connBtns: { flexDirection: 'row', gap: 8 },
  settingsBtn: { borderRadius: 8, paddingHorizontal: 12, paddingVertical: 8, borderWidth: 1 },
  settingsBtnText: { fontSize: 13, fontWeight: '600' },
  connBtn: { borderRadius: 8, paddingHorizontal: 14, paddingVertical: 8 },
  connBtnText: { color: '#fff', fontSize: 13, fontWeight: '600' },
  connDetail: { marginTop: 10, paddingTop: 10, borderTopWidth: 1, gap: 3 },
  connDetailText: { fontSize: 12 },
  connectNowBtn: { marginTop: 10, borderRadius: 8, paddingVertical: 9, alignItems: 'center' },
  connectNowBtnText: { color: '#fff', fontSize: 13, fontWeight: '600' },
  filterRow: { marginBottom: 8, flexGrow: 0 },
  chip: { paddingHorizontal: 14, paddingVertical: 6, borderRadius: 14, borderWidth: 1 },
  chipText: { fontSize: 12, fontWeight: '500' },
  sortRow: { flexDirection: 'row', alignItems: 'center', marginBottom: 10 },
  sortChip: { paddingHorizontal: 12, paddingVertical: 5, borderRadius: 10, borderWidth: 1 },
  sortChipText: { fontSize: 12, fontWeight: '500' },
  selectAllBtn: { paddingHorizontal: 8, paddingVertical: 5, marginLeft: 8 },
  selectAllText: { fontSize: 12, fontWeight: '600' },
  productItem: {
    flexDirection: 'row', alignItems: 'center',
    borderRadius: 10, padding: 12, marginBottom: 8, borderWidth: 1, height: ITEM_HEIGHT - 8,
  },
  productInfo: { flex: 1, marginRight: 8 },
  productName: { fontSize: 15, fontWeight: '600', flexShrink: 1 },
  productMeta: { fontSize: 12, marginTop: 2 },
  productPrice: { fontSize: 15, fontWeight: '700' },
  productStock: { fontSize: 12 },
  abnormalBadge: { fontSize: 11, fontWeight: '600' },
  qtyPill: { flexDirection: 'row', alignItems: 'center', borderRadius: 18, borderWidth: 1, paddingHorizontal: 2, marginRight: 8, height: 36 },
  qtyBtn: { width: 30, height: 30, borderRadius: 15, alignItems: 'center', justifyContent: 'center' },
  qtyBtnText: { fontSize: 17, fontWeight: '700' },
  qtyText: { fontSize: 15, fontWeight: '700', minWidth: 22, textAlign: 'center' },
  printBtn: { borderRadius: 8, paddingHorizontal: 12, paddingVertical: 8, minWidth: 52, alignItems: 'center' },
  printBtnText: { fontSize: 13, fontWeight: '600' },
  empty: { alignItems: 'center', marginTop: 60 },
  emptyText: { fontSize: 14 },
  summaryBar: {
    position: 'absolute', left: 0, right: 0, bottom: 0, marginHorizontal: -16,
    flexDirection: 'row', alignItems: 'center', gap: 10,
    paddingHorizontal: 16, paddingVertical: 12, paddingBottom: 16,
    borderTopWidth: 1,
    shadowColor: '#000', shadowOffset: { width: 0, height: -2 }, shadowOpacity: 0.08, shadowRadius: 6, elevation: 8,
  },
  summaryText: { fontSize: 13 },
  summaryClear: { paddingHorizontal: 6, paddingVertical: 4 },
  summaryClearText: { fontSize: 13 },
  summaryBtn: { borderRadius: 10, paddingHorizontal: 18, paddingVertical: 10, minWidth: 96, alignItems: 'center' },
  summaryBtnText: { fontSize: 14, fontWeight: '700' },
  modalOverlay: { flex: 1, backgroundColor: 'rgba(0,0,0,0.5)', justifyContent: 'flex-end' },
  modalContent: { borderTopLeftRadius: 20, borderTopRightRadius: 20, padding: 20, maxHeight: '70%' },
  modalTitle: { fontSize: 18, fontWeight: '600', marginBottom: 12, textAlign: 'center' },
  scanningRow: { flexDirection: 'row', alignItems: 'center', gap: 8, marginBottom: 12, justifyContent: 'center' },
  scanningText: { fontSize: 13 },
  deviceList: { maxHeight: 300 },
  deviceItem: { flexDirection: 'row', alignItems: 'center', paddingVertical: 14, borderBottomWidth: 1 },
  deviceInfo: { flex: 1 },
  deviceName: { fontSize: 15, fontWeight: '600' },
  deviceId: { fontSize: 12, marginTop: 2 },
  deviceConnect: { fontSize: 14, fontWeight: '600' },
  btnCancel: { flex: 1, padding: 12, borderRadius: 8, alignItems: 'center' },
  btnConfirm: { flex: 1, padding: 12, borderRadius: 8, alignItems: 'center' },
  tplCard: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', borderWidth: 1, borderRadius: 10, padding: 10, marginBottom: 8 },
  tplName: { fontSize: 14, fontWeight: '600' },
  tplMeta: { fontSize: 11, marginTop: 2 },
  tplBtns: { flexDirection: 'row', gap: 6 },
  tplBtn: { borderRadius: 6, paddingHorizontal: 10, paddingVertical: 5 },
  tplBtnText: { fontSize: 12, fontWeight: '600' },
  tplBtnDel: { borderRadius: 6, paddingHorizontal: 10, paddingVertical: 5 },
  tplBtnDelText: { fontSize: 12, fontWeight: '600' },
  tplActions: { flexDirection: 'row', marginBottom: 8 },
  btnAddTpl: { flex: 1, borderWidth: 1, borderStyle: 'dashed', borderRadius: 10, padding: 10, alignItems: 'center' },
  btnAddTplText: { fontSize: 13, fontWeight: '600' },
  tplHint: { fontSize: 12, textAlign: 'center', marginBottom: 8 },
  modalBtns: { flexDirection: 'row', justifyContent: 'space-between', marginTop: 8, gap: 12 },
});
