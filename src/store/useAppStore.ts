import { create } from 'zustand';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { Product, Customer, Order, Store } from '../types';
import { mockProducts, mockCustomers, mockOrders, defaultStore } from '../mock/data';
import { LabelConfig, DEFAULT_LABEL_CONFIG, migrateLabelConfig, buildDefaultConfig } from '../services/PrinterService';
import { localDateKey, genId } from '../utils/format';
import { logError } from '../utils/logger';

// 串行写队列，杜绝并发写
let writeChain: Promise<void> = Promise.resolve();
let writeVersion = 0;

function saveData(state: any) {
  const v = (state._version || 0) + 1;
  writeChain = writeChain.then(async () => {
    try {
      await AsyncStorage.setItem(STORAGE_KEY, JSON.stringify({ ...state, _version: v }));
    } catch (e) {
      logError('STORE', `AsyncStorage write failed: ${e}`);
    }
  });
  return v;
}

function commit(s: any, next: any) {
  const v = saveData({ ...s, ...next });
  return { ...next, _version: v };
}

export type ThemeColors = {
  bg: string; card: string; text: string; subText: string; border: string;
  primary: string; primaryLight: string; danger: string; headerBg: string;
};

export const THEMES: Record<'light' | 'dark', ThemeColors> = {
  light: {
    bg: '#F5F6FA', card: '#fff', text: '#333', subText: '#999', border: '#F0F0F0',
    primary: '#6C5CE7', primaryLight: '#F0EDFF', danger: '#FF6B6B', headerBg: '#6C5CE7',
  },
  dark: {
    bg: '#1a1a2e', card: '#2d2d44', text: '#e0e0e0', subText: '#888', border: '#3d3d5c',
    primary: '#7c6cf7', primaryLight: '#2d2d44', danger: '#ff6b6b', headerBg: '#2d2d44',
  },
};

export type StoreInfo = { name: string; phone: string; address: string };

export type LabelTemplate = {
  id: string;
  name: string;
  config: LabelConfig;
  builtin?: boolean;
};

export const BUILTIN_TEMPLATES: LabelTemplate[] = [
  { id: 'builtin_40x30', name: '40×30 小标签', config: buildDefaultConfig('40x30'), builtin: true },
  { id: 'builtin_40x30_info', name: '40×30 信息', config: buildDefaultConfig('40x30_info'), builtin: true },
  { id: 'builtin_60x40', name: '60×40 中号', config: buildDefaultConfig('60x40'), builtin: true },
];

interface AppStore {
  products: Product[];
  customers: Customer[];
  orders: Order[];
  stores: Store[];
  currentStoreId: string;
  theme: 'light' | 'dark';
  isLoading: boolean;
  labelConfig: LabelConfig;
  labelTemplates: LabelTemplate[];
  currentTemplateId: string | null;
  markupPercent: number;
  storeInfo: StoreInfo;
  _version: number;

  loadData: () => Promise<void>;
  addProduct: (product: Product) => void;
  addProducts: (products: Product[]) => void;
  updateProduct: (id: string, data: Partial<Product>) => void;
  deleteProduct: (id: string) => void;
  addCustomer: (customer: Customer) => void;
  updateCustomer: (id: string, data: Partial<Customer>) => void;
  deleteCustomer: (id: string) => void;
  addOrder: (order: Order) => void;
  updateOrderStatus: (id: string, status: 'completed' | 'cancelled' | 'returned') => void;
  deleteOrder: (id: string) => void;
  setTheme: (theme: 'light' | 'dark') => void;
  setLabelConfig: (config: LabelConfig) => void;
  saveLabelTemplate: (id: string, name: string, config: LabelConfig) => void;
  addLabelTemplate: (name: string, config: LabelConfig) => string;
  deleteLabelTemplate: (id: string) => void;
  selectLabelTemplate: (id: string) => void;
  setMarkupPercent: (percent: number) => void;
  setStoreInfo: (info: Partial<StoreInfo>) => void;
  clearAllData: () => Promise<void>;
  importData: (data: { products?: Product[]; customers?: Customer[]; orders?: Order[] }) => Promise<void>;
  getTodayStats: () => { sales: number; profit: number; orderCount: number };
  getWeekTrend: () => { date: string; sales: number; profit: number }[];
}

const STORAGE_KEY = 'jindou_data';

const emptyData = {
  products: [] as Product[],
  customers: [] as Customer[],
  orders: [] as Order[],
};

export const useAppStore = create<AppStore>((set, get) => ({
  products: [],
  customers: [],
  orders: [],
  stores: [defaultStore],
  currentStoreId: 'store_main',
  theme: 'light',
  isLoading: true,
  labelConfig: BUILTIN_TEMPLATES[0].config,
  labelTemplates: BUILTIN_TEMPLATES,
  currentTemplateId: 'builtin_40x30',
  markupPercent: 0,
  storeInfo: { name: '金豆库管', phone: '', address: '' },
  _version: 0,

  loadData: async () => {
    try {
      const saved = await AsyncStorage.getItem(STORAGE_KEY);
      if (saved) {
        const data = JSON.parse(saved);
        const currentState = get();
        
        // 检查版本号，避免覆盖更新的数据
        if (data._version && currentState._version && data._version < currentState._version) {
          set({ isLoading: false });
          return;
        }
        
        let userTemplates: LabelTemplate[] = [];
        if (Array.isArray(data.labelTemplates) && data.labelTemplates.length > 0) {
          userTemplates = data.labelTemplates.map((t: any) => ({
            id: t.id || genId('tmpl'),
            name: t.name || '未命名模板',
            config: migrateLabelConfig(t.config),
            builtin: t.builtin,
          }));
        } else {
          const single = migrateLabelConfig(data.labelConfig);
          userTemplates = [{ id: 'default', name: '默认模板', config: single }];
        }
        const labelTemplates = [...BUILTIN_TEMPLATES, ...userTemplates.filter(t => !t.builtin && t.id !== 'default')];
        const currentTemplateId = data.currentTemplateId && labelTemplates.some(t => t.id === data.currentTemplateId)
          ? data.currentTemplateId
          : (labelTemplates[0]?.id || 'default');
        set({
          ...emptyData,
          ...data,
          stores: Array.isArray(data.stores) && data.stores.length > 0 ? data.stores : [defaultStore],
          currentStoreId: data.currentStoreId || 'store_main',
          theme: data.theme || 'light',
          labelTemplates,
          currentTemplateId,
          labelConfig: labelTemplates.find(t => t.id === currentTemplateId)?.config || DEFAULT_LABEL_CONFIG,
          markupPercent: typeof data.markupPercent === 'number' ? data.markupPercent : 0,
          storeInfo: { name: '金豆库管', phone: '', address: '', ...(data.storeInfo || {}) },
          isLoading: false,
          _version: data._version || 0,
        });
      } else {
        set({
          ...emptyData,
          stores: [defaultStore],
          labelTemplates: BUILTIN_TEMPLATES,
          currentTemplateId: 'builtin_40x30',
          labelConfig: BUILTIN_TEMPLATES[0].config,
          isLoading: false,
          _version: 0,
        });
      }
    } catch {
      set({
        ...emptyData,
        stores: [defaultStore],
        labelTemplates: BUILTIN_TEMPLATES,
        currentTemplateId: 'builtin_40x30',
        labelConfig: BUILTIN_TEMPLATES[0].config,
        isLoading: false,
        _version: 0,
      });
    }
  },

  addProduct: (product) => {
    set((s) => {
      const idx = s.products.findIndex(p => p.code && p.code === product.code && p.storeId === product.storeId);
      let nextProducts;
      if (idx >= 0) {
        nextProducts = s.products.map((p, i) => i === idx ? {
          ...p, stock: p.stock + product.stock,
          purchasePrice: product.purchasePrice || p.purchasePrice,
          retailPrice: product.retailPrice || p.retailPrice,
          name: product.name || p.name,
        } : p);
      } else {
        nextProducts = [...s.products, product];
      }
      const next = { products: nextProducts };
      return commit(s, next);
    });
  },

  addProducts: (newProducts) => {
    set((s) => {
      const existing = [...s.products];
      for (const np of newProducts) {
        const idx = existing.findIndex(p => p.code && p.code === np.code && p.storeId === np.storeId);
        if (idx >= 0) {
          existing[idx] = {
            ...existing[idx],
            stock: existing[idx].stock + np.stock,
            purchasePrice: np.purchasePrice || existing[idx].purchasePrice,
            retailPrice: np.retailPrice || existing[idx].retailPrice,
            name: np.name || existing[idx].name,
          };
        } else {
          existing.push(np);
        }
      }
      const next = { products: existing };
      return commit(s, next);
    });
  },

  updateProduct: (id, data) => {
    set((s) => {
      const next = { products: s.products.map((p) => (p.id === id ? { ...p, ...data } : p)) };
      return commit(s, next);
    });
  },

  deleteProduct: (id) => {
    set((s) => {
      const next = { products: s.products.filter((p) => p.id !== id) };
      return commit(s, next);
    });
  },

  addCustomer: (customer) => {
    set((s) => {
      const next = { customers: [...s.customers, customer] };
      return commit(s, next);
    });
  },

  updateCustomer: (id, data) => {
    set((s) => {
      const next = { customers: s.customers.map((c) => (c.id === id ? { ...c, ...data } : c)) };
      return commit(s, next);
    });
  },

  deleteCustomer: (id) => {
    set((s) => {
      const next = { customers: s.customers.filter((c) => c.id !== id) };
      return commit(s, next);
    });
  },

  addOrder: (order) => {
    set((s) => {
      const next = { orders: [order, ...s.orders] };
      let customers = s.customers;
      if (order.customerId && order.status === 'completed') {
        const points = Math.floor(order.total / 10);
        customers = s.customers.map((c) => c.id === order.customerId
          ? { ...c, totalSpent: c.totalSpent + order.total, points: c.points + points }
          : c);
      }
      const full = { ...next, customers };
      return commit(s, full);
    });
  },

  updateOrderStatus: (id, status) => {
    set((s) => {
      const target = s.orders.find((o) => o.id === id);
      if (!target) return {};
      
      // 如果是取消/退货状态，且原状态是completed，则回滚库存和积分
      let products = s.products;
      let customers = s.customers;
      if (status === 'cancelled' || status === 'returned') {
        if (target.status === 'completed') {
          // 回滚库存
          products = s.products.map(p => {
            const orderItem = target.items.find(item => item.productId === p.id);
            if (orderItem) {
              return { ...p, stock: p.stock + orderItem.qty };
            }
            return p;
          });
          // 回滚积分和消费
          if (target.customerId) {
            const points = Math.floor(target.total / 10);
            customers = s.customers.map(c => c.id === target.customerId
              ? { ...c, totalSpent: Math.max(0, c.totalSpent - target.total), points: Math.max(0, c.points - points) }
              : c);
          }
        }
      }
      
      const next = {
        orders: s.orders.map(o => o.id === id ? { ...o, status } : o),
        products,
        customers,
      };
      return commit(s, next);
    });
  },

  deleteOrder: (id) => {
    set((s) => {
      const target = s.orders.find((o) => o.id === id);
      let products = s.products;
      let customers = s.customers;
      // 兜底回滚：如果订单是completed状态，回滚库存和积分
      if (target && target.status === 'completed') {
        products = s.products.map((p) => {
          const it = target.items.find((i) => i.productId === p.id);
          return it ? { ...p, stock: p.stock + it.qty } : p;
        });
        if (target.customerId) {
          const points = Math.floor(target.total / 10);
          customers = s.customers.map((c) => c.id === target.customerId
            ? { ...c, totalSpent: Math.max(0, c.totalSpent - target.total), points: Math.max(0, c.points - points) }
            : c);
        }
      }
      const next = { orders: s.orders.filter((o) => o.id !== id), products, customers };
      return commit(s, next);
    });
  },

  setTheme: (theme) => {
    set((s) => commit(s, { theme }));
  },

  setLabelConfig: (labelConfig) => {
    set((s) => {
      const templates = s.currentTemplateId
        ? s.labelTemplates.map(t => t.id === s.currentTemplateId ? { ...t, config: labelConfig } : t)
        : s.labelTemplates;
      const next = { labelConfig, labelTemplates: templates };
      return commit(s, next);
    });
  },

  saveLabelTemplate: (id, name, config) => {
    set((s) => {
      const exists = s.labelTemplates.some(t => t.id === id);
      const templates = exists
        ? s.labelTemplates.map(t => t.id === id ? { ...t, name, config } : t)
        : [...s.labelTemplates, { id, name, config }];
      const next = {
        labelTemplates: templates,
        currentTemplateId: id,
        labelConfig: config,
      };
      return commit(s, next);
    });
  },

  addLabelTemplate: (name, config) => {
    const id = genId('tmpl');
    set((s) => {
      const next = {
        labelTemplates: [...s.labelTemplates, { id, name, config }],
        currentTemplateId: id,
        labelConfig: config,
      };
      return commit(s, next);
    });
    return id;
  },

  deleteLabelTemplate: (id) => {
    if (BUILTIN_TEMPLATES.some(t => t.id === id)) return;
    set((s) => {
      const templates = s.labelTemplates.filter(t => t.id !== id);
      const fallback = templates[0] || { id: 'default', name: '默认模板', config: DEFAULT_LABEL_CONFIG };
      const next = {
        labelTemplates: templates.length ? templates : [fallback],
        currentTemplateId: s.currentTemplateId === id ? fallback.id : s.currentTemplateId,
        labelConfig: s.currentTemplateId === id ? fallback.config : s.labelConfig,
      };
      return commit(s, next);
    });
  },

  selectLabelTemplate: (id) => {
    set((s) => {
      const t = s.labelTemplates.find(t => t.id === id);
      if (!t) return {};
      const next = { currentTemplateId: id, labelConfig: t.config };
      return commit(s, next);
    });
  },

  setMarkupPercent: (markupPercent) => {
    set((s) => commit(s, { markupPercent }));
  },

  setStoreInfo: (info) => {
    set((s) => {
      const next = { storeInfo: { ...s.storeInfo, ...info } };
      return commit(s, next);
    });
  },

  clearAllData: async () => {
    await AsyncStorage.removeItem(STORAGE_KEY);
    set({
      ...emptyData,
      stores: [defaultStore],
      currentStoreId: 'store_main',
      theme: 'light',
      labelConfig: BUILTIN_TEMPLATES[0].config,
      labelTemplates: BUILTIN_TEMPLATES,
      currentTemplateId: 'builtin_40x30',
      _version: 0,
    });
  },

  importData: async (data) => {
    set((s) => {
      const nextProducts = data.products && data.products.length > 0
        ? [...s.products, ...data.products.map(p => ({ ...p, id: p.id || genId('p'), storeId: p.storeId || s.currentStoreId }))]
        : s.products;
      const nextCustomers = data.customers && data.customers.length > 0
        ? [...s.customers, ...data.customers.map(c => ({ ...c, id: c.id || genId('c') }))]
        : s.customers;
      const nextOrders = data.orders && data.orders.length > 0
        ? [...s.orders, ...data.orders.map(o => ({ ...o, id: o.id || genId('o') }))]
        : s.orders;
      const next = { products: nextProducts, customers: nextCustomers, orders: nextOrders };
      return commit(s, next);
    });
  },

  getTodayStats: () => {
    const { orders, currentStoreId } = get();
    const today = localDateKey();
    const todayOrders = orders.filter(
      (o) => o.date === today
          && o.status === 'completed'
          && (!o.storeId || o.storeId === currentStoreId)
    );
    return {
      sales: todayOrders.reduce((s, o) => s + o.total, 0),
      profit: todayOrders.reduce((s, o) => s + o.profit, 0),
      orderCount: todayOrders.length,
    };
  },

  getWeekTrend: () => {
    const { orders, currentStoreId } = get();
    const result = [];
    for (let i = 6; i >= 0; i--) {
      const d = new Date();
      d.setDate(d.getDate() - i);
      const ds = localDateKey(d);
      const dayOrders = orders.filter(
        (o) => o.date === ds
            && o.status === 'completed'
            && (!o.storeId || o.storeId === currentStoreId)
      );
      result.push({
        date: `${d.getMonth() + 1}/${d.getDate()}`,
        sales: dayOrders.reduce((s, o) => s + o.total, 0),
        profit: dayOrders.reduce((s, o) => s + o.profit, 0),
      });
    }
    return result;
  },
}));
