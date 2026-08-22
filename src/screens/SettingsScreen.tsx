import React, { useState, useCallback, useRef } from 'react';
import { View, Text, TouchableOpacity, StyleSheet, Alert, ScrollView, Switch, Modal, FlatList, TextInput, Share, Platform, ActivityIndicator } from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import * as XLSX from 'xlsx';
import * as DocumentPicker from 'expo-document-picker';
import { useAppStore, THEMES, type ThemeColors, type StoreInfo } from '../store/useAppStore';
import { getLogs, clearLogs, type LogEntry } from '../utils/logger';
import { useFocusEffect, useNavigation } from '@react-navigation/native';
import { formatMoney, localDateKey } from '../utils/format';
import { saveExcelToDownloads } from '../utils/exportData';
import { supabase } from '../config/supabase';
import { syncToCloud, pullFromCloud } from '../services/CloudSync';

type Tab = 'basic' | 'pricing' | 'data';

function Toast({ message, visible }: { message: string; visible: boolean }) {
  if (!visible) return null;
  return (
    <View style={toastStyles.container}>
      <Text style={toastStyles.text}>{message}</Text>
    </View>
  );
}
const toastStyles = StyleSheet.create({
  container: { position: 'absolute', bottom: 80, alignSelf: 'center', backgroundColor: '#333', paddingHorizontal: 20, paddingVertical: 10, borderRadius: 20, zIndex: 999 },
  text: { color: '#fff', fontSize: 14, fontWeight: '500' },
});

export default function SettingsScreen() {
  const { theme, setTheme, clearAllData, markupPercent, setMarkupPercent, storeInfo, setStoreInfo, products, customers, orders, importData } = useAppStore();
  const tc: ThemeColors = THEMES[theme];
  const [activeTab, setActiveTab] = useState<Tab>('basic');
  const [toast, setToast] = useState<{ msg: string; visible: boolean }>({ msg: '', visible: false });
  const [logs, setLogs] = useState<LogEntry[]>([]);
  const [syncing, setSyncing] = useState(false);
  const [userEmail, setUserEmail] = useState('');
  const [logVisible, setLogVisible] = useState(false);
  const [markupInput, setMarkupInput] = useState(String(markupPercent));
  const [editingStore, setEditingStore] = useState(false);
  const [storeName, setStoreName] = useState(storeInfo.name);
  const [storePhone, setStorePhone] = useState(storeInfo.phone);
  const [storeAddr, setStoreAddr] = useState(storeInfo.address);
  const [clearConfirmVisible, setClearConfirmVisible] = useState(false);
  const [clearInput, setClearInput] = useState('');
  const [showLogs, setShowLogs] = useState(false);
  const lastTapRef = useRef(0);
  const navigation = useNavigation();

  const showToast = (msg: string) => {
    setToast({ msg, visible: true });
    setTimeout(() => setToast({ msg: '', visible: false }), 2000);
  };

  useFocusEffect(
    useCallback(() => {
      getLogs().then(setLogs);
      setMarkupInput(String(markupPercent));
      setStoreName(storeInfo.name);
      setStorePhone(storeInfo.phone);
      setStoreAddr(storeInfo.address);
      supabase.auth.getUser().then(({ data }: { data: { user: any } }) => {
        setUserEmail(data.user?.email || '');
      });
    }, [markupPercent, storeInfo])
  );

  const handleSyncToCloud = async () => {
    setSyncing(true);
    try {
      await syncToCloud({ products, customers, orders, storeInfo });
      showToast('数据已同步到云端');
    } catch (e: any) {
      Alert.alert('同步失败', e.message || '网络异常');
    } finally {
      setSyncing(false);
    }
  };

  const handlePullFromCloud = async () => {
    Alert.alert('拉取云端数据', '将覆盖本地数据，确定继续？', [
      { text: '取消', style: 'cancel' },
      {
        text: '确定', onPress: async () => {
          setSyncing(true);
          try {
            const cloudData = await pullFromCloud();
            if (cloudData) {
              await importData(cloudData);
              Alert.alert('拉取成功', `商品 ${cloudData.products.length} 个，客户 ${cloudData.customers.length} 个，订单 ${cloudData.orders.length} 笔`);
            }
          } catch (e: any) {
            Alert.alert('拉取失败', e.message || '网络异常');
          } finally {
            setSyncing(false);
          }
        },
      },
    ]);
  };

  const handleLogout = async () => {
    Alert.alert('退出登录', '退出后需重新登录才能同步数据', [
      { text: '取消', style: 'cancel' },
      { text: '退出', style: 'destructive', onPress: () => supabase.auth.signOut() },
    ]);
  };

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
    if (val < 5) {
      Alert.alert('温馨提示', `当前加价率 ${val}%，利润接近 0，建议设定更高的加价率`, [
        { text: '仍然保存', onPress: () => { setMarkupPercent(val); showToast(`加价率已设为 ${val}%`); } },
        { text: '重新输入', style: 'cancel' },
      ]);
      return;
    }
    setMarkupPercent(val);
    showToast(`加价率已保存: ${val}%`);
  };

  const handleReset = () => {
    setClearConfirmVisible(true);
    setClearInput('');
  };

  const doClearData = async () => {
    setClearConfirmVisible(false);
    await clearAllData();
    await AsyncStorage.removeItem('jindou_data');
    showToast('数据已清空');
  };

  const handleExport = async () => {
    try {
      const rawData = await AsyncStorage.getItem('jindou_data');
      if (!rawData) { Alert.alert('提示', '暂无数据'); return; }
      
      const data = JSON.parse(rawData);
      const wb = XLSX.utils.book_new();
      
      if (data.products && data.products.length > 0) {
        const productData = data.products.map((p: any) => ({
          '商品名称': p.name, '款号': p.code, '分类': p.category,
          '零售价': p.retailPrice, '进货价': p.purchasePrice, '库存': p.stock,
          '预警库存': p.warningStock, '单位': p.unit,
        }));
        XLSX.utils.book_append_sheet(wb, XLSX.utils.json_to_sheet(productData), '商品');
      }
      
      if (data.customers && data.customers.length > 0) {
        const customerData = data.customers.map((c: any) => ({
          '客户姓名': c.name, '电话': c.phone,
          '会员等级': c.level === 'platinum' ? '铂金会员' : c.level === 'gold' ? '黄金会员' : c.level === 'vip' ? 'VIP' : '普通会员',
          '积分': c.points, '余额': c.balance, '累计消费': c.totalSpent, '生日': c.birthday,
        }));
        XLSX.utils.book_append_sheet(wb, XLSX.utils.json_to_sheet(customerData), '客户');
      }
      
      if (data.orders && data.orders.length > 0) {
        const orderData = data.orders.map((o: any) => ({
          '订单日期': o.date, '客户': o.customerName,
          '商品明细': o.items.map((i: any) => `${i.productName}×${i.qty}`).join(', '),
          '订单金额': o.total, '成本': o.cost, '利润': o.profit, '支付方式': o.payMethod,
          '状态': o.status === 'completed' ? '完成' : o.status === 'cancelled' ? '取消' : '退货',
        }));
        XLSX.utils.book_append_sheet(wb, XLSX.utils.json_to_sheet(orderData), '订单');
      }
      
      const wbout = XLSX.write(wb, { type: 'base64', bookType: 'xlsx' });
      const fileName = `金豆库管_${localDateKey()}.xlsx`;
      
      if (Platform.OS === 'android') {
        await saveExcelToDownloads(wbout, fileName);
        Alert.alert('导出成功', `文件已导出：${fileName}`);
      } else {
        const { File, Paths } = await import('expo-file-system');
        const file = new File(Paths.cache, fileName);
        if (file.exists) file.delete();
        await file.write(wbout, { encoding: 'base64' });
        await Share.share({ url: file.uri, title: '金豆库管数据导出' });
      }
    } catch (e: any) {
      Alert.alert('导出失败', e.message || '未知错误');
    }
  };

  const handleImport = async () => {
    try {
      const result = await DocumentPicker.getDocumentAsync({
        type: 'application/json',
        copyToCacheDirectory: true,
      });
      
      if (result.canceled || !result.assets?.[0]) return;
      
      const fileUri = result.assets[0].uri;
      const { File } = await import('expo-file-system');
      const file = new File(fileUri);
      const jsonStr = await file.text();
      const data = JSON.parse(jsonStr);
      
      if (!data.products && !data.customers && !data.orders) {
        Alert.alert('格式错误', '文件中未找到有效的商品/客户/订单数据');
        return;
      }

      const warnings: string[] = [];
      if (data.products) {
        data.products.forEach((p: any, i: number) => {
          if (!p.name) warnings.push(`商品#${i + 1}: 缺少名称`);
          if (p.retailPrice !== undefined && p.retailPrice < 0) warnings.push(`${p.name || '商品'}: 零售价为负数`);
          if (p.stock !== undefined && p.stock < 0) warnings.push(`${p.name || '商品'}: 库存为负数`);
        });
      }
      if (warnings.length > 0) {
        Alert.alert('数据校验', `发现 ${warnings.length} 个问题:\n${warnings.slice(0, 5).join('\n')}${warnings.length > 5 ? '\n...' : ''}`, [
          { text: '取消', style: 'cancel' },
          { text: '仍然导入', onPress: () => doImport(data) },
        ]);
        return;
      }

      const counts: string[] = [];
      if (data.products?.length) counts.push(`${data.products.length} 个商品`);
      if (data.customers?.length) counts.push(`${data.customers.length} 个客户`);
      if (data.orders?.length) counts.push(`${data.orders.length} 笔订单`);
      
      Alert.alert(
        '确认导入',
        `将导入：${counts.join('、')}？\n数据将追加到现有数据中。`,
        [
          { text: '取消', style: 'cancel' },
          { text: '导入', onPress: () => doImport(data) },
        ]
      );
    } catch (e: any) {
      Alert.alert('导入失败', e.message || '文件格式错误');
    }
  };

  const doImport = async (data: any) => {
    await importData(data);
    showToast('导入成功');
  };

  const handleSaveStore = () => {
    setStoreInfo({ name: storeName, phone: storePhone, address: storeAddr });
    setEditingStore(false);
    showToast('店铺信息已保存');
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
  const totalStock = products.reduce((s, p) => s + (p.stock || 0), 0);

  return (
    <View style={[styles.container, { backgroundColor: tc.bg }]}>
      <Toast message={toast.msg} visible={toast.visible} />

      <View style={[styles.tabBar, { backgroundColor: tc.card }]}>
        {([['basic', '基本信息'], ['pricing', '定价'], ['data', '数据']] as [Tab, string][]).map(([key, label]) => (
          <TouchableOpacity key={key} style={[styles.tab, activeTab === key && styles.tabActive]} onPress={() => setActiveTab(key)}>
            <Text style={[styles.tabText, { color: activeTab === key ? tc.primary : tc.subText }]}>{label}</Text>
          </TouchableOpacity>
        ))}
      </View>

      <ScrollView contentContainerStyle={{ paddingBottom: 24 }}>
      {activeTab === 'basic' && (<>
      <View style={[styles.section, { backgroundColor: tc.card }]}>
        <View style={styles.sectionHeader}>
          <Text style={styles.sectionIcon}>🏪</Text>
          <Text style={[styles.sectionTitle, { color: tc.subText }]}>店铺信息</Text>
        </View>
        <TouchableOpacity style={[styles.item, { borderBottomColor: tc.border }]} onPress={openStoreEdit}>
          <Text style={[styles.itemLabel, { color: tc.text }]}>店铺名称</Text>
          <Text style={[styles.itemValue, { color: tc.subText }]}>{storeInfo.name || '未设置'} ▸</Text>
        </TouchableOpacity>
        <TouchableOpacity style={[styles.item, { borderBottomColor: tc.border }]} onPress={openStoreEdit}>
          <Text style={[styles.itemLabel, { color: tc.text }]}>联系电话</Text>
          <Text style={[styles.itemValue, { color: tc.subText }]}>{storeInfo.phone || '未设置'} ▸</Text>
        </TouchableOpacity>
        <TouchableOpacity style={[styles.item, { borderBottomWidth: 0 }]} onPress={openStoreEdit}>
          <Text style={[styles.itemLabel, { color: tc.text }]}>地址</Text>
          <Text style={[styles.itemValue, { color: tc.subText, maxWidth: '60%', textAlign: 'right' }]} numberOfLines={1}>{storeInfo.address || '未设置'} ▸</Text>
        </TouchableOpacity>
      </View>

      <View style={[styles.section, { backgroundColor: tc.card }]}>
        <View style={styles.sectionHeader}>
          <Text style={styles.sectionIcon}>📊</Text>
          <Text style={[styles.sectionTitle, { color: tc.subText }]}>数据概览</Text>
        </View>
        <View style={[styles.item, { borderBottomColor: tc.border }]}>
          <Text style={[styles.itemLabel, { color: tc.text }]}>商品数</Text>
          <Text style={[styles.statVal, { color: tc.primary }]}>{products.length} 款</Text>
        </View>
        <View style={[styles.item, { borderBottomColor: tc.border }]}>
          <Text style={[styles.itemLabel, { color: tc.text }]}>客户数</Text>
          <Text style={[styles.statVal, { color: tc.primary }]}>{customers.length} 人</Text>
        </View>
        <View style={[styles.item, { borderBottomColor: tc.border }]}>
          <Text style={[styles.itemLabel, { color: tc.text }]}>总订单</Text>
          <Text style={[styles.statVal, { color: tc.primary }]}>{orders.length} 笔</Text>
        </View>
        <View style={[styles.item, { borderBottomWidth: 0 }]}>
          <Text style={[styles.itemLabel, { color: tc.text }]}>库存总量</Text>
          <Text style={[styles.statVal, { color: tc.primary }]}>{totalStock} 件</Text>
        </View>
      </View>
      </>)}

      {activeTab === 'pricing' && (<>
      <View style={[styles.section, { backgroundColor: tc.card }]}>
        <View style={styles.sectionHeader}>
          <Text style={styles.sectionIcon}>💰</Text>
          <Text style={[styles.sectionTitle, { color: tc.subText }]}>定价设置</Text>
        </View>
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
      </>)}

      {activeTab === 'data' && (<>
      <View style={[styles.section, { backgroundColor: tc.card }]}>
        <View style={styles.sectionHeader}>
          <Text style={styles.sectionIcon}>🎨</Text>
          <Text style={[styles.sectionTitle, { color: tc.subText }]}>外观设置</Text>
        </View>
        <View style={[styles.item, { borderBottomColor: tc.border }]}>
          <Text style={[styles.itemLabel, { color: tc.text }]}>深色模式</Text>
          <View style={{ flexDirection: 'row', alignItems: 'center', gap: 8 }}>
            <Text style={{ color: tc.subText, fontSize: 13 }}>{theme === 'dark' ? '开启' : '关闭'}</Text>
            <Switch value={theme === 'dark'} onValueChange={(v) => setTheme(v ? 'dark' : 'light')} trackColor={{ true: tc.primary, false: '#ccc' }} />
          </View>
        </View>
      </View>

      {userEmail ? (
        <View style={[styles.section, { backgroundColor: tc.card }]}>
          <View style={styles.sectionHeader}>
            <Text style={styles.sectionIcon}>☁️</Text>
            <Text style={[styles.sectionTitle, { color: tc.subText }]}>云端同步</Text>
          </View>
          <View style={[styles.item, { borderBottomColor: tc.border }]}>
            <Text style={[styles.itemLabel, { color: tc.text }]}>账号</Text>
            <Text style={[styles.itemValue, { color: tc.subText }]}>{userEmail}</Text>
          </View>
          <TouchableOpacity style={[styles.itemBtn, { borderBottomColor: tc.border }]} onPress={handleSyncToCloud} disabled={syncing}>
            <View style={styles.logBtnRow}>
              <Text style={[styles.itemBtnText, { color: tc.primary }]}>
                {syncing ? '同步中...' : '上传数据到云端'}
              </Text>
              {syncing ? <ActivityIndicator size="small" color={tc.primary} /> : <Text style={{ color: '#ccc', fontSize: 12 }}>↑</Text>}
            </View>
          </TouchableOpacity>
          <TouchableOpacity style={[styles.itemBtn, { borderBottomColor: tc.border }]} onPress={handlePullFromCloud} disabled={syncing}>
            <View style={styles.logBtnRow}>
              <Text style={[styles.itemBtnText, { color: tc.primary }]}>从云端拉取数据</Text>
              {syncing ? <ActivityIndicator size="small" color={tc.primary} /> : <Text style={{ color: '#ccc', fontSize: 12 }}>↓</Text>}
            </View>
          </TouchableOpacity>
          <TouchableOpacity style={[styles.itemBtn, { borderBottomWidth: 0 }]} onPress={handleLogout}>
            <Text style={[styles.itemBtnText, { color: '#FF6B6B' }]}>退出登录</Text>
          </TouchableOpacity>
        </View>
      ) : null}

      <View style={[styles.section, { backgroundColor: tc.card }]}>
        <View style={styles.sectionHeader}>
          <Text style={styles.sectionIcon}>🔧</Text>
          <Text style={[styles.sectionTitle, { color: tc.subText }]}>数据管理</Text>
        </View>
        <TouchableOpacity style={[styles.itemBtn, { borderBottomColor: tc.border }]} onPress={handleExport}>
          <View style={styles.logBtnRow}>
            <Text style={[styles.itemBtnText, { color: tc.primary }]}>导出 Excel 表格</Text>
            <Text style={{ color: '#ccc', fontSize: 12 }}>↓</Text>
          </View>
        </TouchableOpacity>
        <TouchableOpacity style={[styles.itemBtn, { borderBottomColor: tc.border }]} onPress={handleImport}>
          <View style={styles.logBtnRow}>
            <Text style={[styles.itemBtnText, { color: tc.primary }]}>导入数据 (JSON)</Text>
            <Text style={{ color: '#ccc', fontSize: 12 }}>↑</Text>
          </View>
        </TouchableOpacity>
        <TouchableOpacity style={[styles.itemBtn, { borderBottomWidth: 0 }]} onPress={handleReset}>
          <Text style={[styles.itemBtnText, { color: '#FF6B6B' }]}>清空所有数据</Text>
        </TouchableOpacity>
      </View>

      {showLogs && (
        <View style={[styles.section, { backgroundColor: tc.card }]}>
          <View style={styles.sectionHeader}>
            <Text style={styles.sectionIcon}>🔍</Text>
            <Text style={[styles.sectionTitle, { color: tc.subText }]}>调试</Text>
          </View>
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
      </>)}

      <View style={[styles.section, { backgroundColor: tc.card }]}>
        <View style={styles.sectionHeader}>
          <Text style={styles.sectionIcon}>ℹ️</Text>
          <Text style={[styles.sectionTitle, { color: tc.subText }]}>关于</Text>
        </View>
        <TouchableOpacity style={[styles.item, { borderBottomColor: tc.border }]} onPress={handleVersionTap} activeOpacity={1}>
          <Text style={[styles.itemLabel, { color: tc.text }]}>版本</Text>
          <Text style={[styles.itemValue, { color: tc.subText }]}>1.3.0</Text>
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

      <Modal visible={clearConfirmVisible} transparent animationType="fade">
        <View style={{ flex: 1, backgroundColor: 'rgba(0,0,0,0.5)', justifyContent: 'center', alignItems: 'center', padding: 24 }}>
          <View style={{ backgroundColor: '#fff', borderRadius: 16, padding: 24, width: '100%', maxWidth: 360 }}>
            <Text style={{ fontSize: 17, fontWeight: '600', color: '#333', marginBottom: 8 }}>确认清空数据</Text>
            <Text style={{ fontSize: 14, color: '#666', marginBottom: 16 }}>此操作不可恢复。请输入 CLEAR 确认：</Text>
            <TextInput
              style={{ borderWidth: 1, borderColor: '#E0E0E0', borderRadius: 8, padding: 10, fontSize: 16, marginBottom: 16, textAlign: 'center', letterSpacing: 4, fontWeight: '600' }}
              value={clearInput}
              onChangeText={setClearInput}
              placeholder="CLEAR"
              autoCapitalize="characters"
            />
            <View style={{ flexDirection: 'row', gap: 12 }}>
              <TouchableOpacity style={{ flex: 1, padding: 12, borderRadius: 8, backgroundColor: '#F0F0F0', alignItems: 'center' }} onPress={() => setClearConfirmVisible(false)}>
                <Text style={{ fontSize: 14 }}>取消</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={{ flex: 1, padding: 12, borderRadius: 8, backgroundColor: clearInput === 'CLEAR' ? '#FF6B6B' : '#ccc', alignItems: 'center' }}
                onPress={doClearData}
                disabled={clearInput !== 'CLEAR'}
              >
                <Text style={{ color: '#fff', fontSize: 14, fontWeight: '600' }}>清空</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>
    </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, padding: 0, paddingTop: 0 },
  tabBar: { flexDirection: 'row', marginHorizontal: 16, marginTop: 12, borderRadius: 10, padding: 4, gap: 4 },
  tab: { flex: 1, paddingVertical: 8, alignItems: 'center', borderRadius: 8 },
  tabActive: { backgroundColor: '#F0EDFF' },
  tabText: { fontSize: 14, fontWeight: '600' },
  section: { borderRadius: 12, marginBottom: 16, overflow: 'hidden', marginHorizontal: 16 },
  sectionHeader: { flexDirection: 'row', alignItems: 'center', paddingHorizontal: 16, paddingTop: 14, paddingBottom: 4, gap: 6 },
  sectionIcon: { fontSize: 15 },
  sectionTitle: { fontSize: 13 },
  item: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', paddingVertical: 14, paddingHorizontal: 16, borderBottomWidth: StyleSheet.hairlineWidth },
  statVal: { fontSize: 14, fontWeight: '600' },
  itemCol: { paddingVertical: 14, paddingHorizontal: 16 },
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
