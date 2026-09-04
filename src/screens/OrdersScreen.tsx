import React, { useState, useEffect } from 'react';
import { View, Text, FlatList, TouchableOpacity, StyleSheet, Alert } from 'react-native';
import { useAppStore } from '../store/useAppStore';
import { formatMoney } from '../utils/format';
import { Order } from '../types';

export default function OrdersScreen({ route }: any) {
  const { orders, currentStoreId, isLoading, loadData, deleteOrder, updateOrderStatus } = useAppStore();
  const [filter, setFilter] = useState<'all' | 'completed' | 'cancelled' | 'returned'>('all');
  const onlyToday = route?.params?.onlyToday;

  useEffect(() => { loadData(); }, []);

  const today = new Date().toISOString().slice(0, 10);
  const filtered = orders
    .filter((o) => !o.storeId || o.storeId === currentStoreId)
    .filter((o) => !onlyToday || o.date === today)
    .filter((o) => filter === 'all' || o.status === filter)
    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());

  const handleReturn = (id: string) => {
    Alert.alert('退货 / 取消', '选择操作后库存将自动回滚：', [
      { text: '取消操作', style: 'cancel' },
      { text: '标记退货', onPress: () => updateOrderStatus(id, 'returned') },
      { text: '标记取消', onPress: () => updateOrderStatus(id, 'cancelled') },
    ]);
  };

  const statusColor = (s: string) => {
    return { completed: '#00B894', cancelled: '#FF6B6B', returned: '#FDCB6E' }[s] || '#999';
  };

  const renderItem = ({ item }: { item: Order }) => (
    <View style={styles.card}>
      <View style={styles.cardHeader}>
        <Text style={styles.customerName}>{item.customerName || '散客'}</Text>
        <View style={[styles.statusBadge, { backgroundColor: statusColor(item.status) }]}>
          <Text style={styles.statusText}>{item.status === 'completed' ? '已完成' : item.status === 'cancelled' ? '已取消' : '已退货'}</Text>
        </View>
      </View>
      <Text style={styles.date}>{item.date} {item.payMethod}</Text>
      <View style={styles.itemsRow}>
        {item.items.map((it, idx) => (
          <Text key={idx} style={styles.itemText}>{it.productName} x{it.qty}</Text>
        ))}
      </View>
      <View style={styles.cardFooter}>
        <Text style={styles.total}>¥{formatMoney(item.total)}</Text>
        <Text style={styles.profit}>利润 ¥{formatMoney(item.profit)}</Text>
        {item.status === 'completed' && (
          <TouchableOpacity onPress={() => handleReturn(item.id)}>
            <Text style={styles.returnText}>退货/取消</Text>
          </TouchableOpacity>
        )}
      </View>
    </View>
  );

  return (
    <View style={styles.container}>
      <View style={styles.filterRow}>
        {(['all', 'completed', 'cancelled', 'returned'] as const).map((f) => (
          <TouchableOpacity key={f} style={[styles.filterBtn, filter === f && styles.filterBtnActive]} onPress={() => setFilter(f)}>
            <Text style={[styles.filterBtnText, filter === f && styles.filterBtnTextActive]}>{f === 'all' ? '全部' : f === 'completed' ? '已完成' : f === 'cancelled' ? '已取消' : '已退货'}</Text>
          </TouchableOpacity>
        ))}
      </View>
      <FlatList data={filtered} keyExtractor={(i) => i.id} renderItem={renderItem} contentContainerStyle={styles.list} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#F5F6FA' },
  filterRow: { flexDirection: 'row', padding: 12, gap: 8 },
  filterBtn: { paddingHorizontal: 14, paddingVertical: 6, borderRadius: 16, backgroundColor: '#fff' },
  filterBtnActive: { backgroundColor: '#6C5CE7' },
  filterBtnText: { fontSize: 13, color: '#666' },
  filterBtnTextActive: { color: '#fff' },
  list: { padding: 12 },
  card: { backgroundColor: '#fff', borderRadius: 12, padding: 14, marginBottom: 10 },
  cardHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  customerName: { fontSize: 15, fontWeight: '600', color: '#333' },
  statusBadge: { borderRadius: 4, paddingHorizontal: 6, paddingVertical: 2 },
  statusText: { color: '#fff', fontSize: 11 },
  date: { fontSize: 12, color: '#999', marginTop: 4 },
  itemsRow: { flexDirection: 'row', flexWrap: 'wrap', gap: 4, marginTop: 8 },
  itemText: { fontSize: 12, color: '#666', backgroundColor: '#F5F6FA', paddingHorizontal: 6, paddingVertical: 2, borderRadius: 4 },
  cardFooter: { flexDirection: 'row', alignItems: 'center', marginTop: 10, borderTopWidth: 1, borderTopColor: '#F0F0F0', paddingTop: 10 },
  total: { fontSize: 16, fontWeight: 'bold', color: '#6C5CE7', flex: 1 },
  profit: { fontSize: 13, color: '#00B894', marginRight: 12 },
  returnText: { color: '#FDCB6E', fontSize: 13 },
});
