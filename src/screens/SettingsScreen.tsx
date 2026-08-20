import React, { useState, useCallback, useRef } from 'react';
import { View, Text, TouchableOpacity, StyleSheet, Alert, ScrollView, Switch, Modal, FlatList, TextInput, Share } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import * as XLSX from 'xlsx';
import { useAppStore, THEMES, type ThemeColors, type StoreInfo } from '../store/useAppStore';
import { getLogs, clearLogs, type LogEntry } from '../utils/logger';
import { useFocusEffect, useNavigation } from '@react-navigation/native';
import { formatMoney, localDateKey } from '../utils/format';

export default function SettingsScreen() {
  const { theme, setTheme, clearAllData, markupPercent, setMarkupPercent, storeInfo, setStoreInfo } = useAppStore();
  const tc: ThemeColors = THEMES[theme];
  const [logs, setLogs] = useState<LogEntry[]>([]);
  const [logVisible, setLogVisible] = useState(false);
  const [markupInput, setMarkupInput] = useState(String(markupPercent));
  const [editingStore, setEditingStore] = useState(false);
  const [storeName, setStoreName] = useState(storeInfo.name);
  const [storePhone, setStorePhone] = useState(storeInfo.phone);
  const [storeAddr, setStoreAddr] = useState(storeInfo.address);
  const [showLogs, setShowLogs] = useState(false);
  const lastTapRef = useRef(0);
  const navigation = useNavigation();

  useFocusEffect(
    useCallback(() => {
      getLogs().then(setLogs);
      setMarkupInput(String(markupPercent));
      setStoreName(storeInfo.name);
      setStorePhone(storeInfo.phone);
      setStoreAddr(storeInfo.address);
    }, [markupPercent, storeInfo])
  );

  const handleVersionTap = () => {
    const now = Date.now();
    if (now - lastTapRef.current < 300) {
      setShowLogs(prev => !prev);
    }
    lastTapRef.current = now;
  };

  const handleMarkupSave = () => {
    const val = Number(markupInput);
    if (isNaN(val) || val < 0) {
      Alert.alert('提示', '请输入有效的加价百分比');
      return;
    }
    setMarkupPercent(val);
    Alert.alert('已保存', `加价率 ${val}%\n例：进价100 → 售价 ${100 * (1 + val / 100)}`);
  };

  const handleReset = () => {
    Alert.alert('确认重置', '将清空所有数据？', [
      { text: '取消' },
      {
        text: '重置', style: 'destructive', onPress: async () => {
          await clearAllData();
          await AsyncStorage.removeItem('jindou_data');
          Alert.alert('成功', '数据已清空');
        },
      },
    ]);
  };

  const handleExport = async () => {
    try {
      const rawData = await AsyncStorage.getItem('jindou_data');
      if (!rawData) { Alert.alert('提示', '暂无数据'); return; }
      
      const data = JSON.parse(rawData);
      const wb = XLSX.utils.book_new();
      
      // 商品表
      if (data.products && data.products.length > 0) {
        const productData = data.products.map((p: any) => ({
          '商品名称': p.name,
          '款号': p.code,
          '分类': p.category,
          '零售价': p.retailPrice,
          '进货价': p.purchasePrice,
          '库存': p.stock,
          '预警库存': p.warningStock,
          '单位': p.unit,
        }));
        const wsProducts = XLSX.utils.json_to_sheet(productData);
        XLSX.utils.book_append_sheet(wb, wsProducts, '商品');
      }
      
      // 客户表
      if (data.customers && data.customers.length > 0) {
        const customerData = data.customers.map((c: any) => ({
          '客户姓名': c.name,
          '电话': c.phone,
          '会员等级': c.level === 'platinum' ? '铂金会员' : c.level === 'gold' ? '黄金会员' : c.level === 'vip' ? 'VIP' : '普通会员',
          '积分': c.points,
          '余额': c.balance,
          '累计消费': c.totalSpent,
          '生日': c.birthday,
        }));
        const wsCustomers = XLSX.utils.json_to_sheet(customerData);
        XLSX.utils.book_append_sheet(wb, wsCustomers, '客户');
      }
      
      // 订单表
      if (data.orders && data.orders.length > 0) {
        const orderData = data.orders.map((o: any) => ({
          '订单日期': o.date,
          '客户': o.customerName,
          '商品明细': o.items.map((i: any) => `${i.productName}×${i.qty}`).join(', '),
          '订单金额': o.total,
          '成本': o.cost,
          '利润': o.profit,
          '支付方式': o.payMethod,
          '状态': o.status === 'completed' ? '完成' : o.status === 'cancelled' ? '取消' : '退货',
        }));
        const wsOrders = XLSX.utils.json_to_sheet(orderData);
        XLSX.utils.book_append_sheet(wb, wsOrders, '订单');
      }
      
      // 生成Excel文件
      const wbout = XLSX.write(wb, { type: 'base64', bookType: 'xlsx' });
      
      // 保存文件并分享
      const { File, Paths } = await import('expo-file-system');
      const fileName = `金豆库管_${localDateKey()}.xlsx`;
      const file = new File(Paths.cache, fileName);
      file.write(wbout, { encoding: 'base64' });
      
      const result = await Share.share({
        url: file.uri,
        title: '金豆库管数据导出',
      });
      
    } catch (e: any) {
      Alert.alert('导出失败', e.message || '未知错误');
    }
  };

  const handleSaveStore = () => {
    setStoreInfo({ name: storeName, phone: storePhone, address: storeAddr });
    setEditingStore(false);
    Alert.alert('已保存');
  };

  const openStoreEdit = () => {
    setStoreName(storeInfo.name);
    setStorePhone(storeInfo.phone);
    setStoreAddr(storeInfo.address);
    setEditingStore(true);
  };

  const cancelStoreEdit = () => {
    setStoreName(storeInfo.name);
    setStorePhone(storeInfo.phone);
    setStoreAddr(storeInfo.address);
    setEditingStore(false);
  };

  const handleClearLogs = () => {
    Alert.alert('确认', '清空日志？', [
      { text: '取消' },
      { text: '清空', style: 'destructive', onPress: async () => { await clearLogs(); setLogs([]); } },
    ]);
  };

  const levelColor = (level: string) => {
    return { error: '#D32F2F', warn: '#F57C00', info: '#6C5CE7' }[level] || '#999';
  };

  const examplePrice = 100 * (1 + (Number(markupInput) || 0) / 100);

  return (
    <ScrollView style={[styles.container, { backgroundColor: tc.bg }]}>
      <View style={[styles.section, { backgroundColor: tc.card }]}>
        <Text style={[styles.sectionTitle, { color: tc.subText }]}>店铺信息</Text>
        <TouchableOpacity style={[styles.item, { borderBottomColor: tc.border }]} onPress={openStoreEdit}>
          <Text style={[styles.itemLabel, { color: tc.text }]}>店铺名称</Text>
          <Text style={[styles.itemValue, { color: tc.subText }]}>{storeInfo.name || '未设置'} ▸</Text>
        </TouchableOpacity>
        <View style={[styles.item, { borderBottomColor: tc.border }]}>
          <Text style={[styles.itemLabel, { color: tc.text }]}>应用名称</Text>
          <Text style={[styles.itemValue, { color: tc.subText }]}>金豆库管</Text>
        </View>
      </View>

      <View style={[styles.section, { backgroundColor: tc.card }]}>
        <Text style={[styles.sectionTitle, { color: tc.subText }]}>定价设置</Text>
        <View style={[styles.itemCol, { borderBottomColor: tc.border }]}>
          <Text style={[styles.itemLabel, { color: tc.text }]}>进货价加价率 (%)</Text>
          <Text style={[styles.itemHint, { color: tc.subText }]}>零售价 = 进货价 + 进货价 × 加价率%</Text>
          <View style={styles.markupRow}>
            <TextInput
              style={[styles.markupInput, { borderColor: tc.border, color: tc.text }]}
              value={markupInput}
              onChangeText={setMarkupInput}
              keyboardType="numeric"
              placeholder="0"
            />
            <Text style={[styles.markupPercent, { color: tc.text }]}>%</Text>
            <TouchableOpacity style={[styles.markupSaveBtn, { backgroundColor: tc.primary }]} onPress={handleMarkupSave}>
              <Text style={styles.markupSaveBtnText}>保存</Text>
            </TouchableOpacity>
          </View>
          <Text style={[styles.markupExample, { color: tc.primary }]}>
            示例：进价 ¥100 → 售价 ¥{examplePrice.toFixed(0)}
          </Text>
        </View>
      </View>

      <View style={[styles.section, { backgroundColor: tc.card }]}>
        <Text style={[styles.sectionTitle, { color: tc.subText }]}>外观设置</Text>
        <View style={[styles.item, { borderBottomColor: tc.border }]}>
          <Text style={[styles.itemLabel, { color: tc.text }]}>深色模式</Text>
          <Switch value={theme === 'dark'} onValueChange={(v) => setTheme(v ? 'dark' : 'light')} trackColor={{ true: tc.primary, false: '#ccc' }} />
        </View>
      </View>

      <View style={[styles.section, { backgroundColor: tc.card }]}>
        <Text style={[styles.sectionTitle, { color: tc.subText }]}>数据管理</Text>
        <TouchableOpacity style={[styles.itemBtn, { borderBottomColor: tc.border }]} onPress={handleExport}>
          <Text style={[styles.itemBtnText, { color: tc.primary }]}>导出数据</Text>
        </TouchableOpacity>
        <TouchableOpacity style={[styles.itemBtn, { borderBottomWidth: 0 }]} onPress={handleReset}>
          <Text style={[styles.itemBtnText, { color: '#FF6B6B' }]}>清空所有数据</Text>
        </TouchableOpacity>
      </View>

      {showLogs && (
        <View style={[styles.section, { backgroundColor: tc.card }]}>
          <Text style={[styles.sectionTitle, { color: tc.subText }]}>调试</Text>
          <TouchableOpacity style={styles.itemBtn} onPress={() => { getLogs().then(setLogs); setLogVisible(true); }}>
            <View style={styles.logBtnRow}>
              <Text style={[styles.itemBtnText, { color: tc.primary }]}>运行日志</Text>
              {logs.length > 0 && <View style={styles.logBadge}><Text style={styles.logBadgeText}>{logs.length}</Text></View>}
            </View>
          </TouchableOpacity>
          <TouchableOpacity style={styles.itemBtn} onPress={() => navigation.navigate('蓝牙调试' as never)}>
            <View style={styles.logBtnRow}>
              <Text style={[styles.itemBtnText, { color: tc.primary }]}>蓝牙调试</Text>
              <Text style={{ color: tc.subText, fontSize: 12 }}>▸</Text>
            </View>
          </TouchableOpacity>
        </View>
      )}

      <View style={[styles.section, { backgroundColor: tc.card }]}>
        <Text style={[styles.sectionTitle, { color: tc.subText }]}>关于</Text>
        <TouchableOpacity style={[styles.item, { borderBottomColor: tc.border }]} onPress={handleVersionTap} activeOpacity={1}>
          <Text style={[styles.itemLabel, { color: tc.text }]}>版本</Text>
          <Text style={[styles.itemValue, { color: tc.subText }]}>1.0.0</Text>
        </TouchableOpacity>
      </View>

      {/* Store Info Edit Modal */}
      <Modal visible={editingStore} animationType="slide" onRequestClose={cancelStoreEdit}>
        <View style={[styles.modalContainer, { backgroundColor: tc.bg }]}>
          <View style={[styles.modalHeader, { backgroundColor: tc.headerBg }]}>
            <TouchableOpacity onPress={cancelStoreEdit} style={{ padding: 4 }}>
              <Text style={{ color: '#fff', fontSize: 28 }}>{'<'}</Text>
            </TouchableOpacity>
            <Text style={styles.modalTitle}>编辑店铺信息</Text>
            <TouchableOpacity onPress={handleSaveStore}>
              <Text style={{ color: '#fff', fontSize: 15 }}>保存</Text>
            </TouchableOpacity>
          </View>
          <View style={[styles.modalBody, { backgroundColor: tc.card }]}>
            <Text style={[styles.formLabel, { color: tc.text }]}>店铺名称</Text>
            <TextInput style={[styles.formInput, { borderColor: tc.border, color: tc.text }]} value={storeName} onChangeText={setStoreName} placeholder="请输入店铺名称" />
            <Text style={[styles.formLabel, { color: tc.text }]}>联系电话</Text>
            <TextInput style={[styles.formInput, { borderColor: tc.border, color: tc.text }]} value={storePhone} onChangeText={setStorePhone} placeholder="请输入联系电话" keyboardType="phone-pad" />
            <Text style={[styles.formLabel, { color: tc.text }]}>地址</Text>
            <TextInput style={[styles.formInput, { borderColor: tc.border, color: tc.text }]} value={storeAddr} onChangeText={setStoreAddr} placeholder="请输入地址" />
          </View>
        </View>
      </Modal>

      <Modal visible={logVisible} animationType="slide" onRequestClose={() => setLogVisible(false)}>
        <View style={styles.logContainer}>
          <View style={styles.logHeader}>
            <TouchableOpacity onPress={() => setLogVisible(false)} style={{ padding: 4 }}>
              <Text style={{ color: '#fff', fontSize: 28 }}>{'<'}</Text>
            </TouchableOpacity>
            <Text style={styles.logTitle}>运行日志 ({logs.length})</Text>
            <View style={{ flexDirection: 'row', gap: 12 }}>
              <TouchableOpacity onPress={handleClearLogs}><Text style={{ color: '#FF6B6B', fontSize: 14 }}>清空</Text></TouchableOpacity>
            </View>
          </View>
          {logs.length === 0 ? (
            <View style={styles.logEmpty}><Text style={{ color: '#999', fontSize: 14 }}>暂无日志</Text></View>
          ) : (
            <FlatList
              data={logs}
              keyExtractor={(i) => i.id}
              renderItem={({ item }) => (
                <View style={styles.logItem}>
                  <View style={styles.logItemHeader}>
                    <View style={[styles.logLevelBadge, { backgroundColor: levelColor(item.level) }]}>
                      <Text style={styles.logLevelText}>{item.level.toUpperCase()}</Text>
                    </View>
                    <Text style={styles.logTag}>{item.tag}</Text>
                    <Text style={styles.logTime}>{item.time}</Text>
                  </View>
                  <Text style={styles.logMessage}>{item.message}</Text>
                  {item.stack ? <Text style={styles.logStack} numberOfLines={5}>{item.stack}</Text> : null}
                </View>
              )}
            />
          )}
        </View>
      </Modal>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 16 },
  section: { borderRadius: 12, marginBottom: 16, overflow: 'hidden' },
  sectionTitle: { fontSize: 13, paddingHorizontal: 16, paddingTop: 12, paddingBottom: 4 },
  item: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', paddingVertical: 14, paddingHorizontal: 16, borderBottomWidth: 1 },
  itemCol: { paddingVertical: 14, paddingHorizontal: 16, borderBottomWidth: 1 },
  itemLabel: { fontSize: 14 },
  itemValue: { fontSize: 14 },
  itemHint: { fontSize: 12, marginTop: 4 },
  markupRow: { flexDirection: 'row', alignItems: 'center', marginTop: 10, gap: 8 },
  markupInput: { width: 80, borderWidth: 1, borderRadius: 8, padding: 8, fontSize: 16, textAlign: 'center', fontWeight: '600' },
  markupPercent: { fontSize: 16, fontWeight: '600' },
  markupSaveBtn: { borderRadius: 8, paddingHorizontal: 16, paddingVertical: 8 },
  markupSaveBtnText: { color: '#fff', fontSize: 14, fontWeight: '600' },
  markupExample: { fontSize: 12, marginTop: 8 },
  itemBtn: { paddingVertical: 14, paddingHorizontal: 16, borderBottomWidth: 1, borderBottomColor: '#F5F5F5' },
  itemBtnText: { fontSize: 14 },
  logBtnRow: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' },
  logBadge: { backgroundColor: '#FF6B6B', borderRadius: 10, paddingHorizontal: 8, paddingVertical: 2 },
  logBadgeText: { color: '#fff', fontSize: 11, fontWeight: '600' },
  modalContainer: { flex: 1 },
  modalHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', padding: 16 },
  modalTitle: { color: '#fff', fontSize: 17, fontWeight: '600' },
  modalBody: { flex: 1, padding: 16, borderTopLeftRadius: 16, borderTopRightRadius: 16 },
  formLabel: { fontSize: 13, marginTop: 12, marginBottom: 4 },
  formInput: { borderWidth: 1, borderRadius: 8, padding: 10, fontSize: 14 },
  logContainer: { flex: 1, backgroundColor: '#F5F6FA' },
  logHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', padding: 16, backgroundColor: '#6C5CE7' },
  logTitle: { color: '#fff', fontSize: 16, fontWeight: '600' },
  logEmpty: { flex: 1, justifyContent: 'center', alignItems: 'center' },
  logItem: { backgroundColor: '#fff', marginHorizontal: 12, marginTop: 8, borderRadius: 8, padding: 12 },
  logItemHeader: { flexDirection: 'row', alignItems: 'center', gap: 8, marginBottom: 6 },
  logLevelBadge: { borderRadius: 4, paddingHorizontal: 6, paddingVertical: 2 },
  logLevelText: { color: '#fff', fontSize: 10, fontWeight: '600' },
  logTag: { fontSize: 12, fontWeight: '600', color: '#333' },
  logTime: { fontSize: 11, color: '#999', marginLeft: 'auto' },
  logMessage: { fontSize: 13, color: '#333', lineHeight: 18 },
  logStack: { fontSize: 10, color: '#999', fontFamily: 'monospace', marginTop: 6, backgroundColor: '#F8F8F8', padding: 6, borderRadius: 4 },
});
