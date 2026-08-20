export interface Product {
  id: string;
  storeId: string;
  name: string;
  code: string;
  category: string;
  retailPrice: number;
  purchasePrice: number;
  stock: number;
  warningStock: number;
  isHot: boolean;
  unit: string;
  imageUri?: string;
  createdAt: string;
}

export interface Customer {
  id: string;
  storeId: string;
  name: string;
  phone: string;
  level: 'normal' | 'vip' | 'gold' | 'platinum';
  points: number;
  balance: number;
  totalSpent: number;
  birthday: string;
  remark: string;
  tags: string[];
  createdAt: string;
}

export interface OrderItem {
  productId: string;
  productName: string;
  price: number;
  purchasePrice: number;
  qty: number;
}

export interface Order {
  id: string;
  storeId: string;
  customerId: string;
  customerName: string;
  items: OrderItem[];
  total: number;
  cost: number;
  profit: number;
  status: 'completed' | 'cancelled' | 'returned';
  payMethod: string;
  date: string;
  createdAt: string;
}

export interface Store {
  id: string;
  name: string;
  address: string;
  phone: string;
}

export interface AppState {
  products: Product[];
  customers: Customer[];
  orders: Order[];
  stores: Store[];
  currentStoreId: string;
  settings: {
    storeName: string;
    theme: 'light' | 'dark';
  };
}
