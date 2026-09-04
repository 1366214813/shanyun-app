import React, { useEffect } from 'react';
import { StatusBar } from 'expo-status-bar';
import { NavigationContainer, DefaultTheme, DarkTheme } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { Text, View } from 'react-native';
import { useAppStore, THEMES } from './src/store/useAppStore';
import { ErrorBoundary } from './src/components/ErrorBoundary';
import { logError } from './src/utils/logger';

import HomeScreen from './src/screens/HomeScreen';
import ProductsScreen from './src/screens/ProductsScreen';
import CustomersScreen from './src/screens/CustomersScreen';
import NewOrderScreen from './src/screens/NewOrderScreen';
import OrdersScreen from './src/screens/OrdersScreen';
import SettingsScreen from './src/screens/SettingsScreen';
import OcrScreen from './src/screens/OcrScreen';
import PrintScreen from './src/screens/PrintScreen';
import MinimalBleScreen from './src/screens/MinimalBleScreen';
import LabelEditorScreen from './src/screens/LabelEditorScreen';

const Tab = createBottomTabNavigator();
const Stack = createNativeStackNavigator();

const originalHandler = ErrorUtils.getGlobalHandler();
ErrorUtils.setGlobalHandler((error: Error, isFatal?: boolean) => {
  logError('GLOBAL', error.message, error.stack);
  if (originalHandler) originalHandler(error, isFatal);
});

function TabIcon({ label, focused }: { label: string; focused: boolean }) {
  const tc = THEMES[useAppStore((s) => s.theme)];
  const icons: Record<string, string> = { 首页: '🏠', OCR: '📸', 打印: '🏷️', 设置: '⚙️' };
  return (
    <View style={{ alignItems: 'center', justifyContent: 'center', paddingTop: 4 }}>
      <Text style={{ fontSize: focused ? 24 : 20, opacity: focused ? 1 : 0.5 }}>{icons[label] || '📄'}</Text>
      <Text style={{ fontSize: 10, color: focused ? tc.primary : '#999', fontWeight: focused ? '600' : '400', marginTop: 2 }}>{label}</Text>
    </View>
  );
}

function MainTabs() {
  const theme = useAppStore(s => s.theme);
  const tc = THEMES[theme];
  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        tabBarIcon: ({ focused }) => <TabIcon label={route.name} focused={focused} />,
        tabBarActiveTintColor: tc.primary,
        tabBarInactiveTintColor: '#999',
        headerStyle: { backgroundColor: tc.headerBg },
        headerTintColor: '#fff',
        headerTitleStyle: { fontWeight: '600' },
        tabBarLabelStyle: { display: 'none' },
        tabBarStyle: { paddingBottom: 4, height: 56, backgroundColor: tc.card },
      })}
    >
      <Tab.Screen name="首页" component={HomeScreen} options={{ tabBarIcon: ({ focused }) => <TabIcon label="首页" focused={focused} /> }} />
      <Tab.Screen name="OCR" component={OcrScreen} options={{ headerTitle: '拍照识别', tabBarIcon: ({ focused }) => <TabIcon label="OCR" focused={focused} /> }} />
      <Tab.Screen name="打印" component={PrintScreen} options={{ headerTitle: '吊牌打印', tabBarIcon: ({ focused }) => <TabIcon label="打印" focused={focused} /> }} />
      <Tab.Screen name="设置" component={SettingsScreen} options={{ tabBarIcon: ({ focused }) => <TabIcon label="设置" focused={focused} /> }} />
    </Tab.Navigator>
  );
}

export default function App() {
  const loadData = useAppStore((s) => s.loadData);
  const theme = useAppStore(s => s.theme);
  const tc = THEMES[theme];

  useEffect(() => { loadData(); }, []);

  return (
    <ErrorBoundary>
      <NavigationContainer theme={theme === 'dark' ? DarkTheme : DefaultTheme}>
        {/* 两种主题的 headerBg 都是深色，状态栏统一用亮色内容 */}
        <StatusBar style="light" backgroundColor={tc.headerBg} />
        <Stack.Navigator>
          <Stack.Screen name="Main" component={MainTabs} options={{ headerShown: false }} />
          <Stack.Screen name="商品" component={ProductsScreen} options={{ headerStyle: { backgroundColor: tc.headerBg }, headerTintColor: '#fff' }} />
          <Stack.Screen name="客户" component={CustomersScreen} options={{ headerStyle: { backgroundColor: tc.headerBg }, headerTintColor: '#fff' }} />
          <Stack.Screen name="开单" component={NewOrderScreen} options={{ headerTitle: '销售开单', headerStyle: { backgroundColor: tc.headerBg }, headerTintColor: '#fff' }} />
          <Stack.Screen name="订单记录" component={OrdersScreen} options={{ headerTitle: '订单记录', headerStyle: { backgroundColor: tc.headerBg }, headerTintColor: '#fff' }} />
          <Stack.Screen name="蓝牙调试" component={MinimalBleScreen} options={{ headerTitle: '蓝牙调试', headerStyle: { backgroundColor: tc.headerBg }, headerTintColor: '#fff' }} />
          <Stack.Screen name="标签编辑" component={LabelEditorScreen} options={{ headerTitle: '标签编辑', headerStyle: { backgroundColor: tc.headerBg }, headerTintColor: '#fff' }} />
        </Stack.Navigator>
      </NavigationContainer>
    </ErrorBoundary>
  );
}
