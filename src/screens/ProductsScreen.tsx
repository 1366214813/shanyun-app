import React, { useState, useEffect, useMemo, useRef } from 'react';
import { View, Text, FlatList, TextInput, TouchableOpacity, StyleSheet, Alert, Modal, ScrollView, Image, Dimensions, KeyboardAvoidingView, Platform, ActivityIndicator } from 'react-native';
import { useAppStore, THEMES } from '../store/useAppStore';
import { formatMoney, genId, genBarcode, categoryEmoji } from '../utils/format';
import { Product } from '../types';
import * as ImagePicker from 'expo-image-picker';
import { File, Directory, Paths } from 'expo-file-system';
import { printLabel, isConnected } from '../services/PrinterService';

const SCREEN_W = Dimensions.get('window').width;
const THUMB_SIZE = 52;

const SORT_OPTIONS: [ 'name' | 'code' | 'price' | 'stock' | 'time', string ][] = [
  ['name', '名称'], ['code', '款号'], ['price', '价格↓'], ['stock', '库存↑'], ['time', '最新'],
];
const QTY_PRESETS = [1, 2, 3, 5, 10];

function getDocDir(): Directory { return new Directory(Paths.document, 'product_images'); }
async function ensureDocDir(): Promise<Directory> {
  const dir = getDocDir();
  if (!dir.exists) dir.create({ idempotent: true });
  return dir;
}
async function saveImagePermanent(uri: string, productId: string): Promise<string> {
  const dir = await ensureDocDir();
  const ext = uri.split('.').pop() || 'jpg';
  const dest = new File(dir, `${productId}.${ext}`);
  new File(uri).copy(dest);
  return dest.uri;
}

export default function ProductsScreen() {
  const { products, currentStoreId, isLoading, loadData, addProduct, updateProduct, deleteProduct, markupPercent, theme, labelConfig } = useAppStore();
  const tc = THEMES[theme];
  const [search, setSearch] = useState('');
  const [catFilter, setCatFilter] = useState('all');
  const [sortBy, setSortBy] = useState<'name' | 'code' | 'price' | 'stock' | 'time'>('name');
  const [modalVisible, setModalVisible] = useState(false);
  const [editing, setEditing] = useState<Product | null>(null);
  const [viewImage, setViewImage] = useState<string | null>(null);
  const [formName, setFormName] = useState('');
  const [formCode, setFormCode] = useState('');
  const [formCategory, setFormCategory] = useState('上衣');
  const [formRetailPrice, setFormRetailPrice] = useState('');
  const [formPurchasePrice, setFormPurchasePrice] = useState('');
  const [formStock, setFormStock] = useState('');
  const [formWarningStock, setFormWarningStock] = useState('10');
  const [formImageUri, setFormImageUri] = useState('');

  useEffect(() => { loadData(); }, []);

  const filtered = useMemo(() => products
    .filter((p) => !p.storeId || p.storeId === currentStoreId)
    .filter((p) => !search || p.name.includes(search) || p.code.includes(search))
    .filter((p) => catFilter === 'all' || p.category === catFilter)
    .sort((a, b) => {
      switch (sortBy) {
        case 'name': return a.name.localeCompare(b.name, 'zh-CN');
        case 'code': return a.code.localeCompare(b.code);
        case 'price': return b.retailPrice - a.retailPrice;
        case 'stock': return a.stock - b.stock;
        case 'time': return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
        default: return 0;
      }
    }), [products, currentStoreId, search, catFilter, sortBy]);

  const categories = useMemo(
    () => ['all', ...new Set(products.filter(p => !p.storeId || p.storeId === currentStoreId).map((p) => p.category).filter(Boolean))],
    [products, currentStoreId],
  );

  const openAdd = () => { setEditing(null); setFormName(''); setFormCode(genBarcode()); setFormCategory('上衣'); setFormRetailPrice(''); setFormPurchasePrice(''); setFormStock(''); setFormWarningStock('10'); setFormImageUri(''); setModalVisible(true); };
  const openEdit = (p: Product) => { setEditing(p); setFormName(p.name); setFormCode(p.code); setFormCategory(p.category); setFormRetailPrice(String(p.retailPrice)); setFormPurchasePrice(String(p.purchasePrice)); setFormStock(String(p.stock)); setFormWarningStock(String(p.warningStock)); setFormImageUri(p.imageUri || ''); setModalVisible(true); };

  const pickFormImage = async (useCamera: boolean) => {
    try {
      const perm = useCamera ? await ImagePicker.requestCameraPermissionsAsync() : await ImagePicker.requestMediaLibraryPermissionsAsync();
      if (!perm.granted) { Alert.alert('权限不足', useCamera ? '请允许使用相机' : '请允许访问相册'); return; }
      const res = useCamera ? await ImagePicker.launchCameraAsync({ quality: 0.7 }) : await ImagePicker.launchImageLibraryAsync({ quality: 0.7 });
      if (!res.canceled && res.assets[0]) setFormImageUri(res.assets[0].uri);
    } catch { Alert.alert('错误', '选择图片失败'); }
  };

  const handleSave = async () => {
    if (!formName.trim()) { Alert.alert('提示', '请输入商品名称'); return; }
    const id = editing ? editing.id : genId('p');
let savedUri = formImageUri;
  if (formImageUri && formImageUri !== editing?.imageUri) {
    try { savedUri = await saveImagePermanent(formImageUri, id); }
    catch { Alert.alert('提示', '图片保存失败，使用原始路径'); savedUri = formImageUri; }
  }
    const code = formCode.trim() || genBarcode();
    const data = { name: formName.trim(), code, category: formCategory, retailPrice: Number(formRetailPrice) || 0, purchasePrice: Number(formPurchasePrice) || 0, stock: Number(formStock) || 0, warningStock: Number(formWarningStock) || 10, imageUri: savedUri || '' };
    if (editing) updateProduct(editing.id, data); else addProduct({ id, storeId: currentStoreId, ...data, isHot: false, unit: '件', createdAt: new Date().toISOString() });
    setModalVisible(false);
  };

  const handleDelete = (id: string, name: string) => { Alert.alert('确认删除', `删除「${name}」？`, [{ text: '取消' }, { text: '删除', style: 'destructive', onPress: () => deleteProduct(id) }]); };

  const [printingId, setPrintingId] = useState<string | null>(null);
  const [printTarget, setPrintTarget] = useState<Product | null>(null);
  const [printQtyText, setPrintQtyText] = useState('1');
  const printCancelRef = useRef(false);
  const [printProgress, setPrintProgress] = useState<{ done: number; total: number } | null>(null);
  const qtyDisplay = printQtyText === '' ? '1' : printQtyText;

  const openPrintSheet = (item: Product) => {
    if (!isConnected()) { Alert.alert('未连接打印机', '请先到「打印」页连接打印机后再打印'); return; }
    if (!labelConfig) { Alert.alert('提示', '请先在设置页配置标签模板后再打印'); return; }
    setPrintTarget(item);
    setPrintQtyText('1');
  };

  const handlePrintItem = async (item: Product, qtyStr: string) => {
    const qty = Math.max(1, Math.min(999, parseInt(qtyStr, 10) || 1));
    setPrintTarget(null);
    setPrintingId(item.id);
    printCancelRef.current = false;
    setPrintProgress({ done: 0, total: qty });

    let success = 0;
    let failStreak = 0;
    try {
      for (let i = 0; i < qty; i++) {
        if (printCancelRef.current) break;

        const ok = await printLabel({
          name: item.name, code: item.code, category: item.category,
          color: '', size: '', retailPrice: item.retailPrice, purchasePrice: item.purchasePrice,
        }, labelConfig);

        if (ok) {
          success++;
          failStreak = 0;
        } else {
          failStreak++;
          if (failStreak >= 3) break;
        }

        setPrintProgress({ done: i + 1, total: qty });
        if (i < qty - 1) await new Promise((r) => setTimeout(r, 200));
      }
    } finally {
      setPrintingId(null);
      setPrintProgress(null);
    }

    if (printCancelRef.current) {
      Alert.alert('已取消', `已打印 ${success}/${qty} 张`);
    } else if (success === qty) {
      Alert.alert('成功', `已打印 ${qty} 张: ${item.name}`);
    } else {
      Alert.alert('完成', `打印 ${success}/${qty} 张: ${item.name}${failStreak >= 3 ? '\n\n连续 3 张失败，已中止。请检查打印机连接与纸张。' : ''}`);
    }
  };

  const renderItem = ({ item }: { item: Product }) => (
    <TouchableOpacity style={[styles.card, { backgroundColor: tc.card }]} onPress={() => openEdit(item)} activeOpacity={0.7}>
      <View style={styles.cardRow}>
        {item.imageUri ? (
          <TouchableOpacity onPress={() => setViewImage(item.imageUri || null)}>
            <Image source={{ uri: item.imageUri }} style={styles.thumb} />
          </TouchableOpacity>
        ) : (
          <View style={[styles.thumb, { backgroundColor: tc.primaryLight, borderColor: tc.border }]}>
            <Text style={styles.thumbEmoji}>{categoryEmoji(item.category)}</Text>
          </View>
        )}
        <View style={styles.cardInfo}>
          <View style={styles.cardHeader}>
            <Text style={[styles.cardName, { color: tc.text }]} numberOfLines={1}>{item.name}</Text>
            {item.stock <= item.warningStock && item.stock > 0 && <View style={styles.warnBadge}><Text style={styles.warnText}>库存紧张</Text></View>}
            {item.stock === 0 && <View style={[styles.warnBadge, { backgroundColor: '#999' }]}><Text style={styles.warnText}>已售罄</Text></View>}
          </View>
          <Text style={[styles.cardCode, { color: tc.subText }]}>款号: {item.code}</Text>
          <View style={styles.cardBody}>
            <Text style={[styles.cardPrice, { color: tc.primary }]}>¥{formatMoney(item.retailPrice)}</Text>
            <Text style={[styles.cardCost, { color: tc.subText }]}>进¥{formatMoney(item.purchasePrice)}</Text>
            <Text style={[styles.cardStock, { color: item.stock <= item.warningStock ? '#F57C00' : tc.subText }]}>库存{item.stock}</Text>
          </View>
        </View>
        <View style={styles.cardActions}>
          <TouchableOpacity
            style={[styles.printBtn, { backgroundColor: printingId === item.id ? '#FF6B6B' : tc.primary }]}
            onPress={() => {
              if (printingId === item.id) printCancelRef.current = true;
              else openPrintSheet(item);
            }}
            hitSlop={{ top: 6, bottom: 6, left: 8, right: 8 }}
          >
            {printingId === item.id ? (
              <Text style={styles.printBtnText}>
                {printProgress ? `${printProgress.done}/${printProgress.total} 取消` : '取消'}
              </Text>
            ) : (
              <Text style={styles.printBtnText}>打印</Text>
            )}
          </TouchableOpacity>
          <TouchableOpacity
            style={[styles.deleteBtn, { backgroundColor: tc.border }]}
            onPress={() => handleDelete(item.id, item.name)}
            hitSlop={{ top: 8, bottom: 8, left: 8, right: 8 }}
          >
            <Text style={[styles.deleteBtnText, { color: tc.subText }]}>×</Text>
          </TouchableOpacity>
        </View>
      </View>
    </TouchableOpacity>
  );

  return (
    <View style={[styles.container, { backgroundColor: tc.bg }]}>
      <View style={[styles.searchRow, { backgroundColor: tc.card, borderColor: tc.border }]}>
        <Text style={{ fontSize: 16 }}>🔍</Text>
        <TextInput style={[styles.searchInput, { color: tc.text }]} placeholder="搜索名称/款号" placeholderTextColor={tc.subText} value={search} onChangeText={setSearch} />
        {search.length > 0 && <TouchableOpacity onPress={() => setSearch('')}><Text style={{ color: tc.subText, fontSize: 16 }}>✕</Text></TouchableOpacity>}
      </View>

      <ScrollView horizontal showsHorizontalScrollIndicator={false} style={styles.catRow} contentContainerStyle={{ gap: 6 }}>
        {categories.map((c) => {
          const active = catFilter === c;
          return (
            <TouchableOpacity
              key={c}
              style={[styles.catBtn, { borderColor: active ? tc.primary : tc.border, backgroundColor: active ? tc.primary : tc.card }]}
              onPress={() => setCatFilter(c)}
            >
              <Text style={[styles.catBtnText, { color: active ? '#fff' : tc.subText }]}>{c === 'all' ? '全部' : c}</Text>
            </TouchableOpacity>
          );
        })}
      </ScrollView>

      <ScrollView horizontal showsHorizontalScrollIndicator={false} style={styles.sortRow} contentContainerStyle={{ gap: 6 }}>
        {SORT_OPTIONS.map(([key, label]) => {
          const active = sortBy === key;
          return (
            <TouchableOpacity
              key={key}
              style={[styles.sortBtn, { borderColor: active ? tc.primary : tc.border, backgroundColor: active ? tc.primaryLight : tc.card }]}
              onPress={() => setSortBy(key)}
            >
              <Text style={[styles.sortBtnText, { color: active ? tc.primary : tc.subText }]}>{label}</Text>
            </TouchableOpacity>
          );
        })}
      </ScrollView>

      <Text style={[styles.countText, { color: tc.subText }]}>
        {filtered.length === products.filter(p => !p.storeId || p.storeId === currentStoreId).length 
          ? `共 ${products.filter(p => !p.storeId || p.storeId === currentStoreId).length} 款商品` 
          : `筛选出 ${filtered.length} / ${products.filter(p => !p.storeId || p.storeId === currentStoreId).length} 款`}
      </Text>

      <FlatList data={filtered} keyExtractor={(i) => i.id} renderItem={renderItem} contentContainerStyle={[styles.list, { paddingBottom: 80 }]} />

      <TouchableOpacity style={[styles.fab, { backgroundColor: tc.primary }]} onPress={openAdd}>
        <Text style={styles.fabText}>+</Text>
      </TouchableOpacity>

      {/* 打印数量选择 */}
      <Modal visible={!!printTarget} transparent animationType="fade" onRequestClose={() => setPrintTarget(null)}>
        <TouchableOpacity style={styles.sheetOverlay} activeOpacity={1} onPress={() => setPrintTarget(null)}>
          <TouchableOpacity activeOpacity={1} style={[styles.sheetCard, { backgroundColor: tc.card }]} onPress={() => {}}>
            <Text style={[styles.sheetTitle, { color: tc.text }]}>打印吊牌</Text>
            <Text style={[styles.sheetSub, { color: tc.subText }]} numberOfLines={1}>{printTarget?.name}</Text>

            <View style={styles.sheetQtyRow}>
              <TouchableOpacity style={[styles.sheetQtyBtn, { borderColor: tc.border }]} onPress={() => { const q = Math.max(1, (parseInt(printQtyText, 10) || 1) - 1); setPrintQtyText(String(q)); }}>
                <Text style={[styles.sheetQtyBtnText, { color: tc.text }]}>−</Text>
              </TouchableOpacity>
              <TextInput
                value={printQtyText}
                onChangeText={(t) => setPrintQtyText(t.replace(/[^0-9]/g, '').slice(0, 4))}
                onBlur={() => { if (!printQtyText) setPrintQtyText('1'); }}
                keyboardType="number-pad"
                maxLength={4}
                selectTextOnFocus
                style={[styles.sheetQtyInput, { color: tc.text, borderColor: tc.border }]}
                onTouchStart={(e) => e.stopPropagation()}
              />
              <TouchableOpacity style={[styles.sheetQtyBtn, { borderColor: tc.border }]} onPress={() => { const q = Math.min(999, (parseInt(printQtyText, 10) || 1) + 1); setPrintQtyText(String(q)); }}>
                <Text style={[styles.sheetQtyBtnText, { color: tc.text }]}>+</Text>
              </TouchableOpacity>
              <Text style={[styles.sheetQtyUnit, { color: tc.subText }]}>张</Text>
            </View>

            <View style={styles.sheetPresets}>
              {QTY_PRESETS.map((n) => (
                <TouchableOpacity
                  key={n}
                  style={[styles.sheetPreset, { borderColor: printQtyText === String(n) ? tc.primary : tc.border, backgroundColor: printQtyText === String(n) ? tc.primaryLight : 'transparent' }]}
                  onPress={() => setPrintQtyText(String(n))}
                >
                  <Text style={[styles.sheetPresetText, { color: printQtyText === String(n) ? tc.primary : tc.subText }]}>{n}</Text>
                </TouchableOpacity>
              ))}
            </View>

            <View style={styles.sheetBtns}>
              <TouchableOpacity style={[styles.sheetBtn, { backgroundColor: tc.border }]} onPress={() => setPrintTarget(null)}>
                <Text style={{ color: tc.text }}>取消</Text>
              </TouchableOpacity>
              <TouchableOpacity style={[styles.sheetBtn, { backgroundColor: tc.primary }]} onPress={() => printTarget && handlePrintItem(printTarget, qtyDisplay)}>
                <Text style={{ color: '#fff', fontWeight: '700' }}>打印 {qtyDisplay} 张</Text>
              </TouchableOpacity>
            </View>
          </TouchableOpacity>
        </TouchableOpacity>
      </Modal>

      <Modal visible={modalVisible} animationType="slide" transparent onRequestClose={() => setModalVisible(false)}>
        <KeyboardAvoidingView style={styles.modalOverlay} behavior={Platform.OS === 'ios' ? 'padding' : 'height'}>
          <TouchableOpacity activeOpacity={1} style={[styles.modalContent, { backgroundColor: tc.card }]} onPress={() => {}}>
            <ScrollView keyboardShouldPersistTaps="handled">
              <View style={styles.modalHeader}>
                <Text style={[styles.modalTitle, { color: tc.text }]}>{editing ? '编辑商品' : '新增商品'}</Text>
                <TouchableOpacity onPress={() => setModalVisible(false)} style={[styles.modalCloseBtn, { backgroundColor: tc.border }]}>
                  <Text style={{ fontSize: 16, color: tc.subText }}>✕</Text>
                </TouchableOpacity>
              </View>
              <TouchableOpacity style={styles.imagePicker} onPress={() => Alert.alert('选择图片', '', [{ text: '拍照', onPress: () => pickFormImage(true) }, { text: '相册', onPress: () => pickFormImage(false) }, { text: '取消', style: 'cancel' }])}>
                {formImageUri ? <Image source={{ uri: formImageUri }} style={styles.formImage} /> : <View style={[styles.formImagePlaceholder, { borderColor: tc.border }]}><Text style={{ fontSize: 32 }}>📷</Text><Text style={{ fontSize: 13, color: tc.subText }}>添加图片</Text></View>}
              </TouchableOpacity>
              <Text style={[styles.label, { color: tc.subText }]}>名称 *</Text>
              <TextInput style={[styles.input, { borderColor: tc.border, color: tc.text }]} value={formName} onChangeText={setFormName} />
              <Text style={[styles.label, { color: tc.subText }]}>款号/条码</Text>
              <View style={styles.codeRow}>
                <TextInput style={[styles.input, { borderColor: tc.border, color: tc.text, flex: 1 }]} value={formCode} onChangeText={setFormCode} />
                <TouchableOpacity style={[styles.genCodeBtn, { borderColor: tc.primary }]} onPress={() => setFormCode(genBarcode())}>
                  <Text style={[styles.genCodeBtnText, { color: tc.primary }]}>生成条码</Text>
                </TouchableOpacity>
              </View>
              <Text style={{ fontSize: 11, color: tc.subText, marginTop: 2 }}>{editing ? '可重新生成条码' : '已自动生成条码，可扫码打印'}</Text>
              <Text style={[styles.label, { color: tc.subText }]}>分类</Text>
              <TextInput style={[styles.input, { borderColor: tc.border, color: tc.text }]} value={formCategory} onChangeText={setFormCategory} />
              <View style={styles.priceRow}>
                <View style={{ flex: 1 }}>
                  <Text style={[styles.label, { color: tc.subText }]}>进货价</Text>
                  <TextInput style={[styles.input, { borderColor: tc.border, color: tc.text }]} value={formPurchasePrice} onChangeText={(v) => { setFormPurchasePrice(v); const p = Number(v); if (p > 0 && markupPercent > 0) setFormRetailPrice(String(Math.round(p * (1 + markupPercent / 100)))); }} keyboardType="numeric" />
                </View>
                <View style={{ flex: 1, marginLeft: 8 }}>
                  <Text style={[styles.label, { color: tc.subText }]}>零售价</Text>
                  <TextInput style={[styles.input, { borderColor: tc.border, color: tc.text }]} value={formRetailPrice} onChangeText={setFormRetailPrice} keyboardType="numeric" />
                </View>
              </View>
              {markupPercent > 0 && <Text style={{ fontSize: 12, color: tc.primary, marginTop: 2 }}>加价率 {markupPercent}%，自动计算零售价</Text>}
              <View style={styles.priceRow}>
                <View style={{ flex: 1 }}>
                  <Text style={[styles.label, { color: tc.subText }]}>库存</Text>
                  <TextInput style={[styles.input, { borderColor: tc.border, color: tc.text }]} value={formStock} onChangeText={setFormStock} keyboardType="numeric" />
                </View>
                <View style={{ flex: 1, marginLeft: 8 }}>
                  <Text style={[styles.label, { color: tc.subText }]}>预警库存</Text>
                  <TextInput style={[styles.input, { borderColor: tc.border, color: tc.text }]} value={formWarningStock} onChangeText={setFormWarningStock} keyboardType="numeric" />
                </View>
              </View>
              <View style={styles.modalBtns}>
                <TouchableOpacity style={[styles.btnCancel, { backgroundColor: tc.border }]} onPress={() => setModalVisible(false)}><Text style={{ color: tc.text }}>取消</Text></TouchableOpacity>
                <TouchableOpacity style={[styles.btnConfirm, { backgroundColor: tc.primary }]} onPress={handleSave}><Text style={{ color: '#fff', fontWeight: '600' }}>保存</Text></TouchableOpacity>
              </View>
            </ScrollView>
          </TouchableOpacity>
        </KeyboardAvoidingView>
      </Modal>

      <Modal visible={!!viewImage} transparent onRequestClose={() => setViewImage(null)}>
        <TouchableOpacity style={styles.imageModalOverlay} activeOpacity={1} onPress={() => setViewImage(null)}>
          {viewImage && <Image source={{ uri: viewImage }} style={styles.fullImage} resizeMode="contain" />}
        </TouchableOpacity>
      </Modal>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
  searchRow: { flexDirection: 'row', alignItems: 'center', margin: 12, marginBottom: 6, paddingHorizontal: 12, padding: 8, borderRadius: 10, borderWidth: 1 },
  searchInput: { flex: 1, marginLeft: 8, fontSize: 14, padding: 4 },
  catRow: { paddingHorizontal: 12, marginBottom: 8, flexGrow: 0 },
  catBtn: { paddingHorizontal: 14, paddingVertical: 6, borderRadius: 14, borderWidth: 1 },
  catBtnText: { fontSize: 12, fontWeight: '500' },
  sortRow: { paddingHorizontal: 12, marginBottom: 6, flexGrow: 0 },
  sortBtn: { paddingHorizontal: 12, paddingVertical: 5, borderRadius: 10, borderWidth: 1 },
  sortBtnText: { fontSize: 12, fontWeight: '500' },
  countText: { fontSize: 11, paddingHorizontal: 12, marginBottom: 4 },
  list: { padding: 12, paddingTop: 4 },
  card: { borderRadius: 12, padding: 12, marginBottom: 10, elevation: 1 },
  cardRow: { flexDirection: 'row', alignItems: 'center' },
  thumb: { width: THUMB_SIZE, height: THUMB_SIZE, borderRadius: 8, marginRight: 10, justifyContent: 'center', alignItems: 'center' },
  thumbEmoji: { fontSize: 24 },
  cardInfo: { flex: 1 },
  cardHeader: { flexDirection: 'row', alignItems: 'center', gap: 6 },
  cardName: { fontSize: 15, fontWeight: '600', flex: 1 },
  cardCode: { fontSize: 11, marginTop: 1 },
  warnBadge: { backgroundColor: '#FFF0F0', borderRadius: 4, paddingHorizontal: 5, paddingVertical: 1, borderWidth: 1, borderColor: '#FFD0D0' },
  warnText: { color: '#D32F2F', fontSize: 9, fontWeight: '500' },
  cardBody: { flexDirection: 'row', gap: 12, marginTop: 4 },
  cardPrice: { fontSize: 15, fontWeight: 'bold' },
  cardCost: { fontSize: 12 },
  cardStock: { fontSize: 12 },
  cardActions: { flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: 8, marginLeft: 4 },
  printBtn: { borderRadius: 6, paddingHorizontal: 10, paddingVertical: 6, minWidth: 48, alignItems: 'center' },
  printBtnText: { color: '#fff', fontSize: 12, fontWeight: '600' },
  deleteBtn: { width: 28, height: 28, borderRadius: 14, justifyContent: 'center', alignItems: 'center' },
  deleteBtnText: { fontSize: 16, fontWeight: '600', lineHeight: 18 },
  fab: { position: 'absolute', bottom: 24, right: 20, width: 52, height: 52, borderRadius: 26, justifyContent: 'center', alignItems: 'center', elevation: 6 },
  fabText: { fontSize: 26, color: '#fff', lineHeight: 28 },
  modalOverlay: { flex: 1, backgroundColor: 'rgba(0,0,0,0.5)', justifyContent: 'flex-end' },
  modalContent: { borderTopLeftRadius: 16, borderTopRightRadius: 16, padding: 16, maxHeight: '85%' },
  modalHeader: { flexDirection: 'row', alignItems: 'center', marginBottom: 8 },
  modalTitle: { fontSize: 16, fontWeight: '600', flex: 1 },
  modalCloseBtn: { width: 28, height: 28, borderRadius: 14, justifyContent: 'center', alignItems: 'center' },
  imagePicker: { width: '100%', height: 120, borderRadius: 10, overflow: 'hidden', marginBottom: 10, backgroundColor: '#F5F6FA' },
  formImage: { width: '100%', height: '100%', resizeMode: 'cover' },
  formImagePlaceholder: { flex: 1, justifyContent: 'center', alignItems: 'center', borderWidth: 2, borderStyle: 'dashed', borderRadius: 10 },
  label: { fontSize: 12, marginTop: 6 },
  input: { borderWidth: 1, borderRadius: 8, padding: 8, fontSize: 13, marginTop: 3 },
  priceRow: { flexDirection: 'row' },
  codeRow: { flexDirection: 'row', alignItems: 'center', gap: 8 },
  genCodeBtn: { borderWidth: 1, borderRadius: 8, paddingHorizontal: 12, paddingVertical: 9, backgroundColor: '#fff' },
  genCodeBtnText: { fontSize: 13, fontWeight: '600' },
  modalBtns: { flexDirection: 'row', marginTop: 16, gap: 10 },
  btnCancel: { flex: 1, padding: 10, borderRadius: 8, alignItems: 'center' },
  btnConfirm: { flex: 1, padding: 10, borderRadius: 8, alignItems: 'center' },
  imageModalOverlay: { flex: 1, backgroundColor: 'rgba(0,0,0,0.9)', justifyContent: 'center', alignItems: 'center' },
  fullImage: { width: SCREEN_W, height: SCREEN_W },
  sheetOverlay: { flex: 1, backgroundColor: 'rgba(0,0,0,0.45)', justifyContent: 'center', alignItems: 'center', paddingHorizontal: 32 },
  sheetCard: { width: '100%', borderRadius: 16, padding: 20 },
  sheetTitle: { fontSize: 17, fontWeight: '700', textAlign: 'center' },
  sheetSub: { fontSize: 13, textAlign: 'center', marginTop: 4, marginBottom: 16 },
  sheetQtyRow: { flexDirection: 'row', alignItems: 'center', justifyContent: 'center', gap: 14 },
  sheetQtyBtn: { width: 42, height: 42, borderRadius: 21, borderWidth: 1, alignItems: 'center', justifyContent: 'center' },
  sheetQtyBtnText: { fontSize: 22, fontWeight: '600', lineHeight: 24 },
  sheetQtyInput: { fontSize: 26, fontWeight: '700', minWidth: 60, textAlign: 'center', borderWidth: 1, borderRadius: 8, padding: 4 },
  sheetQtyUnit: { fontSize: 14, marginLeft: -6 },
  sheetPresets: { flexDirection: 'row', justifyContent: 'center', gap: 8, marginTop: 16, marginBottom: 18 },
  sheetPreset: { width: 40, height: 34, borderRadius: 8, borderWidth: 1, alignItems: 'center', justifyContent: 'center' },
  sheetPresetText: { fontSize: 13, fontWeight: '600' },
  sheetBtns: { flexDirection: 'row', gap: 10 },
  sheetBtn: { flex: 1, paddingVertical: 12, borderRadius: 10, alignItems: 'center' },
});
