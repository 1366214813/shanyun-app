import React, { useState, useEffect } from 'react';
import { View, Text, FlatList, TextInput, TouchableOpacity, StyleSheet, Alert, Modal, ScrollView, KeyboardAvoidingView, Platform } from 'react-native';
import { useAppStore, THEMES } from '../store/useAppStore';
import { formatMoney, genId, levelText, levelColor } from '../utils/format';
import { Customer } from '../types';

export default function CustomersScreen() {
  const { customers, currentStoreId, isLoading, loadData, addCustomer, updateCustomer, deleteCustomer, theme } = useAppStore();
  const tc = THEMES[theme];
  const [search, setSearch] = useState('');
  const [levelFilter, setLevelFilter] = useState('all');
  const [modalVisible, setModalVisible] = useState(false);
  const [editing, setEditing] = useState<Customer | null>(null);
  const [formName, setFormName] = useState('');
  const [formPhone, setFormPhone] = useState('');
  const [formLevel, setFormLevel] = useState('normal');
  const [formPoints, setFormPoints] = useState('');
  const [formBalance, setFormBalance] = useState('');
  const [formBirthday, setFormBirthday] = useState('');

  useEffect(() => { loadData(); }, []);

  const filtered = customers
    .filter((c) => c.storeId === currentStoreId)
    .filter((c) => !search || c.name.includes(search) || c.phone.includes(search))
    .filter((c) => levelFilter === 'all' || c.level === levelFilter);

  const openAdd = () => { setEditing(null); setFormName(''); setFormPhone(''); setFormLevel('normal'); setFormPoints(''); setFormBalance(''); setFormBirthday(''); setModalVisible(true); };
  const openEdit = (c: Customer) => { setEditing(c); setFormName(c.name); setFormPhone(c.phone); setFormLevel(c.level); setFormPoints(String(c.points)); setFormBalance(String(c.balance)); setFormBirthday(c.birthday); setModalVisible(true); };

  const handleSave = () => {
    if (!formName.trim()) { Alert.alert('提示', '请输入客户名称'); return; }
    const data = { name: formName.trim(), phone: formPhone.trim(), level: formLevel as Customer['level'], points: Number(formPoints) || 0, balance: Number(formBalance) || 0, birthday: formBirthday.trim(), remark: '' };
    if (editing) updateCustomer(editing.id, data); else addCustomer({ id: genId('c'), storeId: currentStoreId, ...data, totalSpent: 0, tags: [], createdAt: new Date().toISOString() });
    setModalVisible(false);
  };

  const handleDelete = (id: string, name: string) => { Alert.alert('确认删除', `删除客户「${name}」？`, [{ text: '取消' }, { text: '删除', style: 'destructive', onPress: () => deleteCustomer(id) }]); };

  const renderItem = ({ item }: { item: Customer }) => (
    <TouchableOpacity style={[styles.card, { backgroundColor: tc.card }]} onPress={() => openEdit(item)} activeOpacity={0.7}>
      <View style={styles.cardTop}>
        <View style={[styles.avatar, { backgroundColor: tc.primary }]}>
          <Text style={styles.avatarText}>{item.name[0]}</Text>
        </View>
        <View style={styles.cardInfo}>
          <View style={styles.nameRow}>
            <Text style={[styles.cardName, { color: tc.text }]} numberOfLines={1}>{item.name}</Text>
            <View style={[styles.levelBadge, { backgroundColor: levelColor(item.level) }]}>
              <Text style={styles.levelText}>{levelText(item.level)}</Text>
            </View>
          </View>
          <Text style={[styles.cardPhone, { color: tc.subText }]}>{item.phone || '无电话'}</Text>
        </View>
      </View>
      <View style={[styles.cardStats, { borderTopColor: tc.border }]}>
        <View style={styles.statItem}>
          <Text style={[styles.statNum, { color: tc.text }]}>{item.points}</Text>
          <Text style={[styles.statLabel, { color: tc.subText }]}>积分</Text>
        </View>
        <View style={[styles.statDivider, { backgroundColor: tc.border }]} />
        <View style={styles.statItem}>
          <Text style={[styles.statNum, { color: tc.text }]}>¥{formatMoney(item.balance)}</Text>
          <Text style={[styles.statLabel, { color: tc.subText }]}>余额</Text>
        </View>
        <View style={[styles.statDivider, { backgroundColor: tc.border }]} />
        <View style={styles.statItem}>
          <Text style={[styles.statNum, { color: tc.primary }]}>¥{formatMoney(item.totalSpent)}</Text>
          <Text style={[styles.statLabel, { color: tc.subText }]}>累计消费</Text>
        </View>
      </View>
      <TouchableOpacity style={styles.deleteBtn} onPress={() => handleDelete(item.id, item.name)}>
        <Text style={[styles.deleteBtnText, { color: tc.danger }]}>删除</Text>
      </TouchableOpacity>
    </TouchableOpacity>
  );

  return (
    <View style={[styles.container, { backgroundColor: tc.bg }]}>
      <View style={[styles.searchRow, { backgroundColor: tc.card, borderColor: tc.border }]}>
        <Text style={{ fontSize: 16 }}>🔍</Text>
        <TextInput style={[styles.searchInput, { color: tc.text }]} placeholder="搜索姓名/电话" placeholderTextColor={tc.subText} value={search} onChangeText={setSearch} />
        {search.length > 0 && <TouchableOpacity onPress={() => setSearch('')}><Text style={{ color: tc.subText, fontSize: 16 }}>✕</Text></TouchableOpacity>}
      </View>

      <View style={styles.levelRow}>
        {['all', 'platinum', 'gold', 'vip', 'normal'].map((l) => (
          <TouchableOpacity key={l} style={[styles.levelBtn, levelFilter === l && { backgroundColor: tc.primary, borderColor: tc.primary }]} onPress={() => setLevelFilter(l)}>
            <Text style={[styles.levelBtnText, { color: levelFilter === l ? '#fff' : tc.subText }]}>{l === 'all' ? '全部' : levelText(l)}</Text>
          </TouchableOpacity>
        ))}
      </View>

      <Text style={[styles.countText, { color: tc.subText }]}>共 {filtered.length} 位客户</Text>

      <FlatList data={filtered} keyExtractor={(i) => i.id} renderItem={renderItem} contentContainerStyle={[styles.list, { paddingBottom: 80 }]} />

      <TouchableOpacity style={[styles.fab, { backgroundColor: tc.primary }]} onPress={openAdd}>
        <Text style={styles.fabText}>+</Text>
      </TouchableOpacity>

      <Modal visible={modalVisible} animationType="slide" transparent onRequestClose={() => setModalVisible(false)}>
        <KeyboardAvoidingView style={styles.modalOverlay} behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
          <TouchableOpacity activeOpacity={1} style={[styles.modalContent, { backgroundColor: tc.card }]} onPress={() => {}}>
            <ScrollView keyboardShouldPersistTaps="handled">
              <View style={styles.modalHeader}>
                <Text style={[styles.modalTitle, { color: tc.text }]}>{editing ? '编辑客户' : '新增客户'}</Text>
                <TouchableOpacity onPress={() => setModalVisible(false)} style={[styles.modalCloseBtn, { backgroundColor: tc.border }]}>
                  <Text style={{ fontSize: 16, color: tc.subText }}>✕</Text>
                </TouchableOpacity>
              </View>
              <Text style={[styles.label, { color: tc.subText }]}>姓名 *</Text>
              <TextInput style={[styles.input, { borderColor: tc.border, color: tc.text }]} value={formName} onChangeText={setFormName} />
              <Text style={[styles.label, { color: tc.subText }]}>电话</Text>
              <TextInput style={[styles.input, { borderColor: tc.border, color: tc.text }]} value={formPhone} onChangeText={setFormPhone} keyboardType="phone-pad" />
              <Text style={[styles.label, { color: tc.subText }]}>会员等级</Text>
              <View style={styles.levelPicker}>
                {['normal', 'vip', 'gold', 'platinum'].map((l) => (
                  <TouchableOpacity key={l} style={[styles.levelOption, formLevel === l && { backgroundColor: tc.primary, borderColor: tc.primary }]} onPress={() => setFormLevel(l)}>
                    <Text style={[styles.levelOptionText, { color: formLevel === l ? '#fff' : tc.subText }]}>{levelText(l)}</Text>
                  </TouchableOpacity>
                ))}
              </View>
              <View style={styles.priceRow}>
                <View style={{ flex: 1 }}>
                  <Text style={[styles.label, { color: tc.subText }]}>积分</Text>
                  <TextInput style={[styles.input, { borderColor: tc.border, color: tc.text }]} value={formPoints} onChangeText={setFormPoints} keyboardType="numeric" />
                </View>
                <View style={{ flex: 1, marginLeft: 8 }}>
                  <Text style={[styles.label, { color: tc.subText }]}>余额</Text>
                  <TextInput style={[styles.input, { borderColor: tc.border, color: tc.text }]} value={formBalance} onChangeText={setFormBalance} keyboardType="numeric" />
                </View>
              </View>
              <Text style={[styles.label, { color: tc.subText }]}>生日 (YYYY-MM-DD)</Text>
              <TextInput style={[styles.input, { borderColor: tc.border, color: tc.text }]} value={formBirthday} onChangeText={setFormBirthday} />
              <View style={styles.modalBtns}>
                <TouchableOpacity style={[styles.btnCancel, { backgroundColor: tc.border }]} onPress={() => setModalVisible(false)}><Text style={{ color: tc.text }}>取消</Text></TouchableOpacity>
                <TouchableOpacity style={[styles.btnConfirm, { backgroundColor: tc.primary }]} onPress={handleSave}><Text style={{ color: '#fff', fontWeight: '600' }}>保存</Text></TouchableOpacity>
              </View>
            </ScrollView>
          </TouchableOpacity>
        </KeyboardAvoidingView>
      </Modal>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
  searchRow: { flexDirection: 'row', alignItems: 'center', margin: 12, marginBottom: 6, paddingHorizontal: 12, padding: 8, borderRadius: 10, borderWidth: 1 },
  searchInput: { flex: 1, marginLeft: 8, fontSize: 14, padding: 4 },
  levelRow: { paddingHorizontal: 12, marginBottom: 4 },
  levelBtn: { paddingHorizontal: 12, paddingVertical: 6, borderRadius: 14, backgroundColor: '#fff', marginRight: 6, borderWidth: 1, borderColor: '#E8E8E8' },
  levelBtnText: { fontSize: 12, fontWeight: '500' },
  countText: { fontSize: 11, paddingHorizontal: 12, marginBottom: 4 },
  list: { padding: 12, paddingTop: 4 },
  card: { borderRadius: 10, padding: 12, marginBottom: 8, elevation: 1 },
  cardTop: { flexDirection: 'row', alignItems: 'center' },
  avatar: { width: 40, height: 40, borderRadius: 20, justifyContent: 'center', alignItems: 'center', marginRight: 10 },
  avatarText: { color: '#fff', fontSize: 16, fontWeight: '600' },
  cardInfo: { flex: 1 },
  nameRow: { flexDirection: 'row', alignItems: 'center', gap: 6 },
  cardName: { fontSize: 14, fontWeight: '600', flex: 1 },
  levelBadge: { borderRadius: 4, paddingHorizontal: 5, paddingVertical: 1 },
  levelText: { color: '#fff', fontSize: 10 },
  cardPhone: { fontSize: 12, marginTop: 2 },
  cardStats: { flexDirection: 'row', justifyContent: 'space-around', borderTopWidth: 1, paddingTop: 8, marginTop: 8 },
  statItem: { alignItems: 'center', flex: 1 },
  statNum: { fontSize: 13, fontWeight: '600' },
  statLabel: { fontSize: 10, marginTop: 2 },
  statDivider: { width: 1, height: '80%', alignSelf: 'center' },
  deleteBtn: { marginTop: 6, alignSelf: 'flex-end' },
  deleteBtnText: { fontSize: 12 },
  fab: { position: 'absolute', bottom: 24, right: 20, width: 52, height: 52, borderRadius: 26, justifyContent: 'center', alignItems: 'center', elevation: 6 },
  fabText: { fontSize: 26, color: '#fff', lineHeight: 28 },
  modalOverlay: { flex: 1, backgroundColor: 'rgba(0,0,0,0.5)', justifyContent: 'flex-end' },
  modalContent: { borderTopLeftRadius: 16, borderTopRightRadius: 16, padding: 16, maxHeight: '85%' },
  modalHeader: { flexDirection: 'row', alignItems: 'center', marginBottom: 8 },
  modalTitle: { fontSize: 16, fontWeight: '600', flex: 1 },
  modalCloseBtn: { width: 28, height: 28, borderRadius: 14, justifyContent: 'center', alignItems: 'center' },
  label: { fontSize: 12, marginTop: 6 },
  input: { borderWidth: 1, borderRadius: 8, padding: 8, fontSize: 13, marginTop: 3 },
  levelPicker: { flexDirection: 'row', gap: 6, marginTop: 4 },
  levelOption: { flex: 1, padding: 6, borderRadius: 6, borderWidth: 1, borderColor: '#E0E0E0', alignItems: 'center' },
  levelOptionText: { fontSize: 11 },
  priceRow: { flexDirection: 'row' },
  modalBtns: { flexDirection: 'row', marginTop: 16, gap: 10 },
  btnCancel: { flex: 1, padding: 10, borderRadius: 8, alignItems: 'center' },
  btnConfirm: { flex: 1, padding: 10, borderRadius: 8, alignItems: 'center' },
});
