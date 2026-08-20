import { create } from 'zustand';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { Product, Customer, Order, Store } from '../types';
import { mockProducts, mockCustomers, mockOrders, defaultStore } from '../mock/data';
import { LabelConfig, DEFAULT_LABEL_CONFIG, migrateLabelConfig, buildDefaultConfig } from '../services/PrinterService';
import { localDateKey, genId } from '../utils/format';

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
};

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

  loadData: () => Promise<void>;
  addProduct: (product: Product) => void;
  addProducts: (products: Product[]) => void;
  updateProduct: (id: string, data: Partial<Product>) => void;
  deleteProduct: (id: string) => void;
  addCustomer: (customer: Customer) => void;
  updateCustomer: (id: string, data: Partial<Customer>) => void;
  deleteCustomer: (id: string) => void;
  addOrder: (order: Order) => void;
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
  labelConfig: DEFAULT_LABEL_CONFIG,
  labelTemplates: [{ id: 'default', name: '默认模板', config: DEFAULT_LABEL_CONFIG }],
  currentTemplateId: 'default',
  markupPercent: 0,
  storeInfo: { name: '金豆库管', phone: '', address: '' },

  loadData: async () => {
    try {
      const saved = await AsyncStorage.getItem(STORAGE_KEY);
      if (saved) {
        const data = JSON.parse(saved);
        let labelTemplates: LabelTemplate[] = [];
        if (Array.isArray(data.labelTemplates) && data.labelTemplates.length > 0) {
          labelTemplates = data.labelTemplates.map((t: any) => ({
            id: t.id || genId('tmpl'),
            name: t.name || '未命名模板',
            config: migrateLabelConfig(t.config),
          }));
        } else {
          const single = migrateLabelConfig(data.labelConfig);
          labelTemplates = [{ id: 'default', name: '默认模板', config: single }];
        }
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
        });
      } else {
        set({
          ...emptyData,
          stores: [defaultStore],
          isLoading: false,
        });
      }
    } catch {
      set({
        ...emptyData,
        stores: [defaultStore],
        isLoading: false,
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
      try { AsyncStorage.setItem(STORAGE_KEY, JSON.stringify({ ...s, ...next })); } catch {}
      return next;
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
      try {
        AsyncStorage.setItem(STORAGE_KEY, JSON.stringify({ ...s, ...next }));
      } catch (e) {
        console.error('[STORE] addProducts AsyncStorage write failed:', e);
      }
      return next;
    });
  },

  updateProduct: (id, data) => {
    set((s) => {
      const next = { products: s.products.map((p) => (p.id === id ? { ...p, ...data } : p)) };
      try { AsyncStorage.setItem(STORAGE_KEY, JSON.stringify({ ...s, ...next })); } catch {}
      return next;
    });
  },

  deleteProduct: (id) => {
    set((s) => {
      const next = { products: s.products.filter((p) => p.id !== id) };
      try { AsyncStorage.setItem(STORAGE_KEY, JSON.stringify({ ...s, ...next })); } catch {}
      return next;
    });
  },

  addCustomer: (customer) => {
    set((s) => {
      const next = { customers: [...s.customers, customer] };
      try { AsyncStorage.setItem(STORAGE_KEY, JSON.stringify({ ...s, ...next })); } catch {}
      return next;
    });
  },

  updateCustomer: (id, data) => {
    set((s) => {
      const next = { customers: s.customers.map((c) => (c.id === id ? { ...c, ...data } : c)) };
      try { AsyncStorage.setItem(STORAGE_KEY, JSON.stringify({ ...s, ...next })); } catch {}
      return next;
    });
  },

  deleteCustomer: (id) => {
    set((s) => {
      const next = { customers: s.customers.filter((c) => c.id !== id) };
      try { AsyncStorage.setItem(STORAGE_KEY, JSON.stringify({ ...s, ...next })); } catch {}
      return next;
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
      try { AsyncStorage.setItem(STORAGE_KEY, JSON.stringify({ ...s, ...full })); } catch {}
      return full;
    });
  },

  deleteOrder: (id) => {
    set((s) => {
      const target = s.orders.find((o) => o.id === id);
      let customers = s.customers;
      if (target && target.customerId && target.status === 'completed') {
        const points = Math.floor(target.total / 10);
        customers = s.customers.map((c) => c.id === target.customerId
          ? { ...c, totalSpent: Math.max(0, c.totalSpent - target.total), points: Math.max(0, c.points - points) }
          : c);
      }
      const next = { orders: s.orders.filter((o) => o.id !== id), customers };
      try { AsyncStorage.setItem(STORAGE_KEY, JSON.stringify({ ...s, ...next })); } catch {}
      return next;
    });
  },

  setTheme: (theme) => {
    set({ theme });
    try { AsyncStorage.setItem(STORAGE_KEY, JSON.stringify({ ...get(), theme })); } catch {}
  },

  setLabelConfig: (labelConfig) => {
    set((s) => {
      const templates = s.currentTemplateId
        ? s.labelTemplates.map(t => t.id === s.currentTemplateId ? { ...t, config: labelConfig } : t)
        : s.labelTemplates;
      const next = { labelConfig, labelTemplates: templates };
      try { AsyncStorage.setItem(STORAGE_KEY, JSON.stringify({ ...s, ...next })); } catch {}
      return next;
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
      try { AsyncStorage.setItem(STORAGE_KEY, JSON.stringify({ ...s, ...next })); } catch {}
      return next;
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
      try { AsyncStorage.setItem(STORAGE_KEY, JSON.stringify({ ...s, ...next })); } catch {}
      return next;
    });
    return id;
  },

  deleteLabelTemplate: (id) => {
    set((s) => {
      const templates = s.labelTemplates.filter(t => t.id !== id);
      const fallback = templates[0] || { id: 'default', name: '默认模板', config: DEFAULT_LABEL_CONFIG };
      const next = {
        labelTemplates: templates.length ? templates : [fallback],
        currentTemplateId: s.currentTemplateId === id ? fallback.id : s.currentTemplateId,
        labelConfig: s.currentTemplateId === id ? fallback.config : s.labelConfig,
      };
      try { AsyncStorage.setItem(STORAGE_KEY, JSON.stringify({ ...s, ...next })); } catch {}
      return next;
    });
  },

  selectLabelTemplate: (id) => {
    set((s) => {
      const t = s.labelTemplates.find(t => t.id === id);
      if (!t) return {};
      const next = { currentTemplateId: id, labelConfig: t.config };
      try { AsyncStorage.setItem(STORAGE_KEY, JSON.stringify({ ...s, ...next })); } catch {}
      return next;
    });
  },

  setMarkupPercent: (markupPercent) => {
    set({ markupPercent });
    try { AsyncStorage.setItem(STORAGE_KEY, JSON.stringify({ ...get(), markupPercent })); } catch {}
  },

  setStoreInfo: (info) => {
    set((s) => {
      const next = { storeInfo: { ...s.storeInfo, ...info } };
      try { AsyncStorage.setItem(STORAGE_KEY, JSON.stringify({ ...s, ...next })); } catch {}
      return next;
    });
  },

  clearAllData: async () => {
    await AsyncStorage.removeItem(STORAGE_KEY);
    set({
      ...emptyData,
      stores: [defaultStore],
      currentStoreId: 'store_main',
      theme: 'light',
      labelConfig: DEFAULT_LABEL_CONFIG,
      labelTemplates: [{ id: 'default', name: '默认模板', config: DEFAULT_LABEL_CONFIG }],
      currentTemplateId: 'default',
    });
  },

  getTodayStats: () => {
    const { orders } = get();
    const today = localDateKey();
    const todayOrders = orders.filter((o) => o.date === today && o.status === 'completed');
    return {
      sales: todayOrders.reduce((s, o) => s + o.total, 0),
      profit: todayOrders.reduce((s, o) => s + o.profit, 0),
      orderCount: todayOrders.length,
    };
  },

  getWeekTrend: () => {
    const { orders } = get();
    const result = [];
    for (let i = 6; i >= 0; i--) {
      const d = new Date();
      d.setDate(d.getDate() - i);
      const ds = localDateKey(d);
      const dayOrders = orders.filter((o) => o.date === ds && o.status === 'completed');
      result.push({
        date: `${d.getMonth() + 1}/${d.getDate()}`,
        sales: dayOrders.reduce((s, o) => s + o.total, 0),
        profit: dayOrders.reduce((s, o) => s + o.profit, 0),
      });
    }
    return result;
  },
}));
