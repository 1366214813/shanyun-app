import React, { useEffect } from 'react';
import { View, Text, ScrollView, StyleSheet, Dimensions, TouchableOpacity, ActivityIndicator } from 'react-native';
import { LineChart } from 'react-native-chart-kit';
import { useAppStore, THEMES } from '../store/useAppStore';
import { formatMoney, formatDisplayDate, levelText, levelColor } from '../utils/format';
import { useNavigation, useIsFocused } from '@react-navigation/native';

const screenWidth = Dimensions.get('window').width;

// 统一卡片阴影，避免各页面深浅不一
const CARD_SHADOW = {
  shadowColor: '#000',
  shadowOffset: { width: 0, height: 1 },
  shadowOpacity: 0.06,
  shadowRadius: 4,
  elevation: 2,
};

export default function HomeScreen() {
  const { products, customers, orders, isLoading, loadData, getTodayStats, getWeekTrend, theme, currentStoreId } = useAppStore();
  const tc = THEMES[theme];
  const navigation = useNavigation<any>();
  const isFocused = useIsFocused();

  useEffect(() => { if (isFocused) loadData(); }, [isFocused]);

  if (isLoading) {
    return (
      <View style={styles.loading}>
        <ActivityIndicator size="large" color={tc.primary} />
        <Text style={{ marginTop: 8, color: tc.subText }}>加载中...</Text>
      </View>
    );
  }

  const stats = getTodayStats();
  const trend = getWeekTrend();
  const storeProducts = products.filter(p => !p.storeId || p.storeId === currentStoreId);
  const storeOrders = orders.filter(o => !o.storeId || o.storeId === currentStoreId);
  const storeCustomers = customers.filter(c => !c.storeId || c.storeId === currentStoreId);
  const lowStock = storeProducts.filter((p) => p.stock <= p.warningStock);
  const totalStock = storeProducts.reduce((s, p) => s + (p.stock ?? 0), 0);
  const recentOrders = [...storeOrders].sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()).slice(0, 5);
  const topCustomers = [...storeCustomers].sort((a, b) => b.totalSpent - a.totalSpent).slice(0, 5);

  const chartData = {
    labels: trend.map((t) => t.date),
    datasets: [
      { data: trend.map((t) => t.sales || 0), color: () => tc.primary, strokeWidth: 2 },
      { data: trend.map((t) => t.profit || 0), color: () => '#00B894', strokeWidth: 2 },
    ],
    legend: ['销售额', '利润'],
  };

  // 库存预警配色：深色模式下不能用浅橙底，否则刺眼
  const alarm = theme === 'dark'
    ? { bg: '#3A2E22', title: '#FFB74D', item: '#D7BFA3', more: '#FFB74D' }
    : { bg: '#FFF3E0', title: '#E65100', item: '#BF360C', more: '#E65100' };

  return (
    <ScrollView style={[styles.container, { backgroundColor: tc.bg }]}>
      <Text style={[styles.date, { color: tc.text }]}>{formatDisplayDate(new Date())}</Text>

      {/* 快捷操作按钮：开单为主操作，占 50% 宽度 */}
      <View style={styles.quickActions}>
        <TouchableOpacity
          style={[styles.quickBtn, styles.quickBtnMain, { backgroundColor: tc.primary }]}
          onPress={() => navigation.navigate('开单')}
        >
          <Text style={styles.quickBtnIcon}>🧾</Text>
          <Text style={styles.quickBtnText}>开单</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={[styles.quickBtn, { backgroundColor: '#00B894' }]}
          onPress={() => navigation.navigate('商品')}
        >
          <Text style={styles.quickBtnIcon}>👕</Text>
          <Text style={styles.quickBtnText}>商品</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={[styles.quickBtn, { backgroundColor: '#FDCB6E' }]}
          onPress={() => navigation.navigate('客户')}
        >
          <Text style={styles.quickBtnIcon}>👥</Text>
          <Text style={[styles.quickBtnText, { color: '#333' }]}>客户</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={[styles.quickBtn, { backgroundColor: '#E17055' }]}
          onPress={() => navigation.navigate('打印')}
        >
          <Text style={styles.quickBtnIcon}>🏷️</Text>
          <Text style={styles.quickBtnText}>打印</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.statsRow}>
        <TouchableOpacity style={[styles.statCard, { backgroundColor: tc.primary }]} onPress={() => navigation.navigate('订单记录', { onlyToday: true })}>
          <Text style={styles.statLabel}>今日销售</Text>
          <Text style={styles.statValue}>¥{formatMoney(stats.sales)}</Text>
        </TouchableOpacity>
        <TouchableOpacity style={[styles.statCard, { backgroundColor: '#00B894' }]} onPress={() => navigation.navigate('订单记录', { onlyToday: true })}>
          <Text style={styles.statLabel}>今日利润</Text>
          <Text style={styles.statValue}>¥{formatMoney(stats.profit)}</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.statsRow}>
        <TouchableOpacity style={[styles.statCard, { backgroundColor: '#FDCB6E' }]} onPress={() => navigation.navigate('订单记录', { onlyToday: true })}>
          <Text style={[styles.statLabel, { color: '#333' }]}>订单数</Text>
          <Text style={[styles.statValue, { color: '#333' }]}>{stats.orderCount} 单</Text>
        </TouchableOpacity>
        <TouchableOpacity style={[styles.statCard, { backgroundColor: '#E17055' }]} onPress={() => navigation.navigate('商品')}>
          <Text style={styles.statLabel}>库存总量</Text>
          <Text style={styles.statValue}>{totalStock} 件</Text>
        </TouchableOpacity>
      </View>

      {trend.length > 0 && trend.some((t) => t.sales > 0) ? (
        <View style={[styles.chartCard, { backgroundColor: tc.card }]}>
          <Text style={[styles.sectionTitle, { color: tc.text }]}>7日销售趋势</Text>
          <LineChart
            data={chartData}
            width={screenWidth - 48}
            height={200}
            chartConfig={{
              backgroundColor: tc.card,
              backgroundGradientFrom: tc.card,
              backgroundGradientTo: tc.card,
              decimalPlaces: 0,
              color: (opacity = 1) => `rgba(108, 92, 231, ${opacity})`,
              labelColor: () => tc.subText,
              propsForDots: { r: '4', strokeWidth: '2', stroke: tc.primary },
            }}
            bezier
            style={styles.chart}
          />
        </View>
      ) : (
        <View style={[styles.chartCard, { backgroundColor: tc.card }]}>
          <Text style={[styles.sectionTitle, { color: tc.text }]}>7日销售趋势</Text>
          <Text style={{ textAlign: 'center', color: tc.subText, marginTop: 40 }}>暂无统计数据</Text>
        </View>
      )}

      {/* 概览信息 */}
      <View style={[styles.infoCard, { backgroundColor: tc.card }]}>
        <Text style={[styles.sectionTitle, { color: tc.text }]}>概览</Text>
        <View style={[styles.infoRow, { borderBottomColor: tc.border }]}>
          <Text style={[styles.infoLabel, { color: tc.subText }]}>商品数</Text>
          <Text style={[styles.infoValue, { color: tc.text }]}>{storeProducts.length} 款</Text>
        </View>
        <View style={[styles.infoRow, { borderBottomColor: tc.border }]}>
          <Text style={[styles.infoLabel, { color: tc.subText }]}>客户数</Text>
          <Text style={[styles.infoValue, { color: tc.text }]}>{storeCustomers.length} 人</Text>
        </View>
        <View style={styles.infoRow}>
          <Text style={[styles.infoLabel, { color: tc.subText }]}>总订单</Text>
          <Text style={[styles.infoValue, { color: tc.text }]}>{storeOrders.length} 笔</Text>
        </View>
      </View>

      {/* 最近订单 */}
      {recentOrders.length > 0 && (
        <View style={[styles.card, { backgroundColor: tc.card }]}>
          <View style={styles.cardHeader}>
            <Text style={[styles.sectionTitle, { color: tc.text }]}>最近订单</Text>
            <TouchableOpacity onPress={() => navigation.navigate('订单记录')}>
              <Text style={{ color: tc.primary, fontSize: 13 }}>查看全部 →</Text>
            </TouchableOpacity>
          </View>
          {recentOrders.map((order) => (
            <View key={order.id} style={[styles.orderRow, { borderBottomColor: tc.border }]}>
              <View style={styles.orderInfo}>
                <Text style={[styles.orderCustomer, { color: tc.text }]} numberOfLines={1}>{order.customerName}</Text>
                <Text style={[styles.orderDate, { color: tc.subText }]}>{order.date}</Text>
              </View>
              <View style={styles.orderRight}>
                <Text style={[styles.orderTotal, { color: tc.primary }]}>¥{formatMoney(order.total)}</Text>
                <Text style={[styles.orderItems, { color: tc.subText }]}>{order.items.length}件</Text>
              </View>
            </View>
          ))}
        </View>
      )}

      {/* 高价值客户 */}
      {topCustomers.length > 0 && (
        <View style={[styles.card, { backgroundColor: tc.card }]}>
          <View style={styles.cardHeader}>
            <Text style={[styles.sectionTitle, { color: tc.text }]}>高价值客户</Text>
            <TouchableOpacity onPress={() => navigation.navigate('客户')}>
              <Text style={{ color: tc.primary, fontSize: 13 }}>查看全部 →</Text>
            </TouchableOpacity>
          </View>
          {topCustomers.map((customer) => (
            <View key={customer.id} style={[styles.customerRow, { borderBottomColor: tc.border }]}>
              <View style={[styles.customerAvatar, { backgroundColor: levelColor(customer.level) }]}>
                <Text style={styles.customerAvatarText}>{customer.name[0]}</Text>
              </View>
              <View style={styles.customerInfo}>
                <Text style={[styles.customerName, { color: tc.text }]} numberOfLines={1}>{customer.name}</Text>
                <Text style={[styles.customerLevel, { color: tc.subText }]}>{levelText(customer.level)}</Text>
              </View>
              <Text style={[styles.customerSpent, { color: tc.primary }]}>¥{formatMoney(customer.totalSpent)}</Text>
            </View>
          ))}
        </View>
      )}

      {/* 库存预警 */}
      {lowStock.length > 0 && (
        <TouchableOpacity
          style={[styles.alertCard, { backgroundColor: alarm.bg }]}
          onPress={() => navigation.navigate('商品')}
          activeOpacity={0.8}
        >
          <View style={styles.cardHeader}>
            <Text style={[styles.alertTitle, { color: alarm.title }]}>库存预警 ({lowStock.length})</Text>
            <Text style={{ color: alarm.more, fontSize: 13 }}>去补货 →</Text>
          </View>
          {lowStock.slice(0, 5).map((p) => (
            <Text key={p.id} style={[styles.alertItem, { color: alarm.item }]}>
              {p.name} - 仅剩 {p.stock} {p.unit}
            </Text>
          ))}
          {lowStock.length > 5 && (
            <Text style={[styles.alertMore, { color: alarm.more }]}>还有 {lowStock.length - 5} 项...</Text>
          )}
        </TouchableOpacity>
      )}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#F5F6FA', padding: 16 },
  loading: { flex: 1, justifyContent: 'center', alignItems: 'center' },
  date: { fontSize: 14, color: '#666', marginBottom: 16 },
  quickActions: { flexDirection: 'row', gap: 10, marginBottom: 16 },
  quickBtn: { flex: 1, padding: 12, borderRadius: 12, alignItems: 'center', ...CARD_SHADOW },
  quickBtnMain: { flex: 3 },
  quickBtnIcon: { fontSize: 24 },
  quickBtnText: { fontSize: 12, color: '#fff', fontWeight: '600', marginTop: 4 },
  statsRow: { flexDirection: 'row', gap: 12, marginBottom: 12 },
  statCard: { flex: 1, padding: 16, borderRadius: 12, ...CARD_SHADOW },
  statLabel: { fontSize: 13, color: '#fff', opacity: 0.8 },
  statValue: { fontSize: 20, fontWeight: 'bold', color: '#fff', marginTop: 4 },
  chartCard: { borderRadius: 12, padding: 16, marginBottom: 12, ...CARD_SHADOW },
  chart: { marginTop: 8, borderRadius: 8 },
  sectionTitle: { fontSize: 16, fontWeight: '600' },
  infoCard: { borderRadius: 12, padding: 16, marginBottom: 12, ...CARD_SHADOW },
  infoRow: { flexDirection: 'row', justifyContent: 'space-between', paddingVertical: 8, borderBottomWidth: 1 },
  infoLabel: { fontSize: 14 },
  infoValue: { fontSize: 14, fontWeight: '600' },
  card: { borderRadius: 12, padding: 16, marginBottom: 12, ...CARD_SHADOW },
  cardHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 },
  orderRow: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', paddingVertical: 10, borderBottomWidth: 1, borderBottomColor: '#F0F0F0' },
  orderInfo: { flex: 1 },
  orderCustomer: { fontSize: 14, fontWeight: '500' },
  orderDate: { fontSize: 11, marginTop: 2 },
  orderRight: { alignItems: 'flex-end' },
  orderTotal: { fontSize: 14, fontWeight: '600' },
  orderItems: { fontSize: 11, marginTop: 2 },
  customerRow: { flexDirection: 'row', alignItems: 'center', paddingVertical: 10, borderBottomWidth: 1, borderBottomColor: '#F0F0F0' },
  customerAvatar: { width: 36, height: 36, borderRadius: 18, justifyContent: 'center', alignItems: 'center', marginRight: 10 },
  customerAvatarText: { fontSize: 14, color: '#fff', fontWeight: '600' },
  customerInfo: { flex: 1 },
  customerName: { fontSize: 14, fontWeight: '500' },
  customerLevel: { fontSize: 11, marginTop: 2 },
  customerSpent: { fontSize: 14, fontWeight: '600' },
  alertCard: { borderRadius: 12, padding: 16, marginBottom: 24, ...CARD_SHADOW },
  alertTitle: { fontSize: 15, fontWeight: '600' },
  alertItem: { fontSize: 13, paddingVertical: 2 },
  alertMore: { fontSize: 12, marginTop: 4, fontStyle: 'italic' },
});
