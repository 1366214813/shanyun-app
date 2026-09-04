import React, { useState } from 'react';
import { View, Text, FlatList, TextInput, TouchableOpacity, StyleSheet, Alert, ScrollView, Modal } from 'react-native';
import { useAppStore, THEMES } from '../store/useAppStore';
import { formatMoney, genId, localDateKey } from '../utils/format';
import { OrderItem } from '../types';
import OrdersScreen from './OrdersScreen';

export default function NewOrderScreen() {
  const { products, customers, currentStoreId, addOrder, updateProduct, theme } = useAppStore();
  const tc = THEMES[theme];
  const [selectedCustomer, setSelectedCustomer] = useState<{ id: string; name: string } | null>(null);
  const [items, setItems] = useState<OrderItem[]>([]);
  const [payMethod, setPayMethod] = useState('微信支付');
  const [search, setSearch] = useState('');
  const [ordersVisible, setOrdersVisible] = useState(false);
  const [showCart, setShowCart] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const storeProducts = products.filter((p) => (!p.storeId || p.storeId === currentStoreId) && p.stock > 0);
  const filtered = search
    ? storeProducts.filter((p) => p.name.includes(search) || p.code.includes(search))
    : storeProducts;

  const total = items.reduce((s, i) => s + i.price * i.qty, 0);
  const totalQty = items.reduce((s, i) => s + i.qty, 0);

  const addItem = (p: typeof storeProducts[0]) => {
    const existing = items.find((i) => i.productId === p.id);
    if (existing) {
      if (existing.qty >= p.stock) { Alert.alert('提示', '库存不足'); return; }
      setItems(items.map((i) => i.productId === p.id ? { ...i, qty: i.qty + 1 } : i));
    } else {
      setItems([...items, { productId: p.id, productName: p.name, price: p.retailPrice, purchasePrice: p.purchasePrice, qty: 1 }]);
    }
  };

  const removeItem = (productId: string) => { setItems(items.filter((i) => i.productId !== productId)); };

  const updateQty = (productId: string, qty: number) => {
    if (qty <= 0) { removeItem(productId); return; }
    const p = products.find((x) => x.id === productId);
    if (p && qty > p.stock) { Alert.alert('提示', '库存不足'); return; }
    setItems(items.map((i) => i.productId === productId ? { ...i, qty } : i));
  };

  const handleSubmit = () => {
    if (isSubmitting) return; // 防重
    if (items.length === 0) { Alert.alert('提示', '请添加商品'); return; }
    
    // 复检库存
    const insufficientItems: string[] = [];
    for (const item of items) {
      const p = products.find(x => x.id === item.productId);
      if (!p || p.stock < item.qty) {
        insufficientItems.push(item.productName);
      }
    }
    if (insufficientItems.length > 0) {
      Alert.alert('库存不足', `以下商品库存不足：\n${insufficientItems.join('、')}`);
      return;
    }
    
    setIsSubmitting(true);
    const order = {
      id: genId('o'), storeId: currentStoreId,
      customerId: selectedCustomer?.id || '', customerName: selectedCustomer?.name || '散客',
      items, total, cost: items.reduce((s, i) => s + i.purchasePrice * i.qty, 0),
      profit: total - items.reduce((s, i) => s + i.purchasePrice * i.qty, 0),
      status: 'completed' as const, payMethod,
      date: localDateKey(),
      createdAt: new Date().toISOString(),
    };
    items.forEach((item) => { const p = products.find((x) => x.id === item.productId); if (p) updateProduct(p.id, { stock: p.stock - item.qty }); });
    addOrder(order);
    Alert.alert('成功', `订单已创建 ¥${formatMoney(total)}`, [
      { text: '确定', onPress: () => { setItems([]); setSelectedCustomer(null); setOrdersVisible(true); setIsSubmitting(false); } },
    ]);
  };

  return (
    <View style={[styles.container, { backgroundColor: tc.bg }]}>
      {/* Cart floating button */}
      {items.length > 0 && (
        <TouchableOpacity style={[styles.cartFab, { backgroundColor: tc.primary }]} onPress={() => setShowCart(!showCart)}>
          <Text style={styles.cartFabText}>🛒 {totalQty}</Text>
        </TouchableOpacity>
      )}

      {/* Product list */}
      <ScrollView style={styles.productList}>
        <View style={styles.sectionHeader}>
          <Text style={[styles.sectionTitle, { color: tc.text }]}>选择商品</Text>
          <TouchableOpacity style={[styles.ordersBtn, { backgroundColor: tc.primary }]} onPress={() => setOrdersVisible(true)}>
            <Text style={styles.ordersBtnText}>📋 订单</Text>
          </TouchableOpacity>
        </View>

        {/* Customer row */}
        <ScrollView horizontal showsHorizontalScrollIndicator={false} style={styles.custRow}>
          <TouchableOpacity style={[styles.custBtn, !selectedCustomer && { backgroundColor: tc.primary }]} onPress={() => setSelectedCustomer(null)}>
            <Text style={[styles.custBtnText, !selectedCustomer && { color: '#fff' }]}>散客</Text>
          </TouchableOpacity>
          {customers.filter((c) => !c.storeId || c.storeId === currentStoreId).map((c) => (
            <TouchableOpacity key={c.id} style={[styles.custBtn, selectedCustomer?.id === c.id && { backgroundColor: tc.primary }]} onPress={() => setSelectedCustomer({ id: c.id, name: c.name })}>
              <Text style={[styles.custBtnText, selectedCustomer?.id === c.id && { color: '#fff' }]}>{c.name}</Text>
            </TouchableOpacity>
          ))}
        </ScrollView>

        <TextInput style={[styles.search, { backgroundColor: tc.card, color: tc.text, borderColor: tc.border }]} placeholder="搜索商品" placeholderTextColor={tc.subText} value={search} onChangeText={setSearch} />
        {filtered.map((p) => (
          <TouchableOpacity key={p.id} style={[styles.productItem, { backgroundColor: tc.card, borderColor: tc.border }]} onPress={() => addItem(p)}>
            <Text style={[styles.productName, { color: tc.text }]} numberOfLines={1}>{p.name}</Text>
            <Text style={[styles.productPrice, { color: tc.primary }]}>¥{formatMoney(p.retailPrice)}</Text>
            <Text style={[styles.productStock, { color: tc.subText }]}>库存{p.stock}</Text>
          </TouchableOpacity>
        ))}
      </ScrollView>

      {/* Bottom summary bar */}
      <View style={[styles.bottomBar, { backgroundColor: tc.card, borderTopColor: tc.border }]}>
        <View style={styles.bottomInfo}>
          <Text style={[styles.bottomTotal, { color: tc.text }]}>合计 <Text style={{ color: tc.primary, fontSize: 20, fontWeight: 'bold' }}>¥{formatMoney(total)}</Text></Text>
          <Text style={[styles.bottomCount, { color: tc.subText }]}>{totalQty} 件商品</Text>
        </View>
        <TouchableOpacity style={[styles.submitBtn, { backgroundColor: tc.primary }]} onPress={() => setShowCart(true)}>
          <Text style={styles.submitBtnText}>去结算</Text>
        </TouchableOpacity>
      </View>

      {/* Cart modal */}
      <Modal visible={showCart} animationType="slide" onRequestClose={() => setShowCart(false)}>
        <View style={[styles.cartModal, { backgroundColor: tc.bg }]}>
          <View style={[styles.cartHeader, { backgroundColor: tc.headerBg }]}>
            <TouchableOpacity onPress={() => setShowCart(false)} style={{ padding: 4 }}>
              <Text style={{ color: '#fff', fontSize: 28 }}>{'<'}</Text>
            </TouchableOpacity>
            <Text style={styles.cartTitle}>购物车</Text>
            <View style={{ width: 40 }} />
          </View>

          {items.length === 0 ? (
            <View style={styles.cartEmpty}><Text style={{ color: tc.subText }}>购物车为空</Text></View>
          ) : (
            <FlatList
              data={items}
              keyExtractor={(i) => i.productId}
              style={{ flex: 1 }}
              renderItem={({ item }) => (
                <View style={[styles.cartItem, { borderBottomColor: tc.border }]}>
                  <View style={styles.cartItemInfo}>
                    <Text style={[styles.cartName, { color: tc.text }]} numberOfLines={1}>{item.productName}</Text>
                    <Text style={[styles.cartPrice, { color: tc.primary }]}>¥{formatMoney(item.price)}</Text>
                  </View>
                  <View style={styles.cartItemBottom}>
                    <View style={styles.qtyRow}>
                      <TouchableOpacity style={[styles.qtyBtn, { backgroundColor: tc.border }]} onPress={() => updateQty(item.productId, item.qty - 1)}>
                        <Text style={{ color: tc.text }}>-</Text>
                      </TouchableOpacity>
                      <Text style={[styles.qtyText, { color: tc.text }]}>{item.qty}</Text>
                      <TouchableOpacity style={[styles.qtyBtn, { backgroundColor: tc.border }]} onPress={() => updateQty(item.productId, item.qty + 1)}>
                        <Text style={{ color: tc.text }}>+</Text>
                      </TouchableOpacity>
                    </View>
                    <Text style={[styles.cartSubtotal, { color: tc.text }]}>¥{formatMoney(item.price * item.qty)}</Text>
                  </View>
                </View>
              )}
            />
          )}

          <View style={[styles.paySection, { backgroundColor: tc.card, borderTopColor: tc.border }]}>
            <ScrollView horizontal showsHorizontalScrollIndicator={false} style={{ marginBottom: 12 }}>
              {['微信支付', '支付宝', '现金', '银行卡', '赊账'].map((m) => (
                <TouchableOpacity key={m} style={[styles.payBtn, payMethod === m && { backgroundColor: tc.primary }]} onPress={() => setPayMethod(m)}>
                  <Text style={[styles.payBtnText, payMethod === m && { color: '#fff' }]}>{m}</Text>
                </TouchableOpacity>
              ))}
            </ScrollView>
            <View style={styles.totalRow}>
              <Text style={[styles.totalLabel, { color: tc.subText }]}>合计</Text>
              <Text style={[styles.totalValue, { color: tc.primary }]}>¥{formatMoney(total)}</Text>
            </View>
            <TouchableOpacity 
              style={[styles.payBtnSubmit, { backgroundColor: tc.primary, opacity: isSubmitting ? 0.6 : 1 }]} 
              onPress={() => { setShowCart(false); handleSubmit(); }}
              disabled={isSubmitting}
            >
              <Text style={styles.payBtnSubmitText}>{isSubmitting ? '处理中...' : `确认收款 ¥${formatMoney(total)}`}</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>

      {/* Orders Modal */}
      <Modal visible={ordersVisible} animationType="slide" onRequestClose={() => setOrdersVisible(false)}>
        <View style={[styles.ordersModal, { backgroundColor: tc.bg }]}>
          <View style={[styles.ordersHeader, { backgroundColor: tc.headerBg }]}>
            <TouchableOpacity onPress={() => setOrdersVisible(false)} style={{ padding: 4 }}>
              <Text style={{ color: '#fff', fontSize: 15 }}>← 返回</Text>
            </TouchableOpacity>
            <Text style={styles.ordersTitle}>订单记录</Text>
            <View style={{ width: 60 }} />
          </View>
          <OrdersScreen />
        </View>
      </Modal>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
  productList: { flex: 1, padding: 12 },
  sectionHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 },
  sectionTitle: { fontSize: 15, fontWeight: '600' },
  ordersBtn: { borderRadius: 8, paddingHorizontal: 12, paddingVertical: 6 },
  ordersBtnText: { color: '#fff', fontSize: 13, fontWeight: '600' },
  custRow: { marginBottom: 8 },
  custBtn: { paddingHorizontal: 12, paddingVertical: 6, borderRadius: 16, backgroundColor: '#F0F0F0', marginRight: 8 },
  custBtnText: { fontSize: 13, color: '#666' },
  search: { padding: 10, borderRadius: 8, fontSize: 13, marginBottom: 8, borderWidth: 1 },
  productItem: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', padding: 10, borderRadius: 8, marginBottom: 6, borderWidth: 1 },
  productName: { flex: 1, fontSize: 13 },
  productPrice: { fontSize: 14, fontWeight: '600', marginHorizontal: 8 },
  productStock: { fontSize: 12 },
  cartFab: { position: 'absolute', top: 12, right: 12, zIndex: 10, borderRadius: 20, paddingHorizontal: 14, paddingVertical: 8 },
  cartFabText: { color: '#fff', fontSize: 16, fontWeight: '600' },
  bottomBar: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between', paddingHorizontal: 16, paddingVertical: 12, borderTopWidth: 1 },
  bottomInfo: { flex: 1 },
  bottomTotal: { fontSize: 14, fontWeight: '600' },
  bottomCount: { fontSize: 12, marginTop: 2 },
  submitBtn: { borderRadius: 8, paddingHorizontal: 20, paddingVertical: 12 },
  submitBtnText: { color: '#fff', fontSize: 15, fontWeight: '600' },
  cartModal: { flex: 1 },
  cartHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', padding: 16 },
  cartTitle: { color: '#fff', fontSize: 17, fontWeight: '600' },
  cartEmpty: { flex: 1, justifyContent: 'center', alignItems: 'center' },
  cartItem: { paddingHorizontal: 16, paddingVertical: 10, borderBottomWidth: 1 },
  cartItemInfo: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 6 },
  cartName: { flex: 1, fontSize: 14, fontWeight: '500' },
  cartPrice: { fontSize: 14, fontWeight: '600' },
  cartItemBottom: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  qtyRow: { flexDirection: 'row', alignItems: 'center', gap: 12 },
  qtyBtn: { width: 28, height: 28, borderRadius: 14, justifyContent: 'center', alignItems: 'center' },
  qtyText: { fontSize: 16, fontWeight: '600', minWidth: 20, textAlign: 'center' },
  cartSubtotal: { fontSize: 15, fontWeight: '600' },
  paySection: { padding: 16, borderTopWidth: 1 },
  payBtn: { paddingHorizontal: 12, paddingVertical: 8, borderRadius: 8, backgroundColor: '#F0F0F0', marginRight: 8 },
  payBtnText: { fontSize: 13, color: '#666' },
  totalRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 },
  totalLabel: { fontSize: 14 },
  totalValue: { fontSize: 22, fontWeight: 'bold' },
  payBtnSubmit: { borderRadius: 8, padding: 14, alignItems: 'center' },
  payBtnSubmitText: { color: '#fff', fontSize: 16, fontWeight: '600' },
  ordersModal: { flex: 1 },
  ordersHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', padding: 16 },
  ordersTitle: { color: '#fff', fontSize: 17, fontWeight: '600' },
});
