-- 金豆库管 Supabase 数据库表结构
-- 在 Supabase Dashboard → SQL Editor 中执行

-- 商品表
CREATE TABLE IF NOT EXISTS products (
  id TEXT PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  name TEXT NOT NULL DEFAULT '',
  code TEXT DEFAULT '',
  category TEXT DEFAULT '',
  color TEXT DEFAULT '',
  size TEXT DEFAULT '',
  retail_price NUMERIC DEFAULT 0,
  purchase_price NUMERIC DEFAULT 0,
  stock INTEGER DEFAULT 0,
  warning_stock INTEGER DEFAULT 5,
  unit TEXT DEFAULT '件',
  barcode TEXT DEFAULT '',
  image_uri TEXT DEFAULT '',
  created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 客户表
CREATE TABLE IF NOT EXISTS customers (
  id TEXT PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  name TEXT NOT NULL DEFAULT '',
  phone TEXT DEFAULT '',
  level TEXT DEFAULT 'normal',
  points INTEGER DEFAULT 0,
  balance NUMERIC DEFAULT 0,
  total_spent NUMERIC DEFAULT 0,
  birthday TEXT DEFAULT '',
  created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
  id TEXT PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
  customer_name TEXT DEFAULT '',
  customer_id TEXT DEFAULT '',
  items JSONB DEFAULT '[]',
  total NUMERIC DEFAULT 0,
  cost NUMERIC DEFAULT 0,
  profit NUMERIC DEFAULT 0,
  pay_method TEXT DEFAULT 'cash',
  status TEXT DEFAULT 'completed',
  date TEXT DEFAULT '',
  created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 店铺信息表
CREATE TABLE IF NOT EXISTS stores (
  user_id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
  name TEXT DEFAULT '',
  phone TEXT DEFAULT '',
  address TEXT DEFAULT '',
  updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- 启用行级安全 (RLS)
ALTER TABLE products ENABLE ROW LEVEL SECURITY;
ALTER TABLE customers ENABLE ROW LEVEL SECURITY;
ALTER TABLE orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE stores ENABLE ROW LEVEL SECURITY;

-- RLS 策略：用户只能访问自己的数据（先删旧的再建）
DROP POLICY IF EXISTS "Users can CRUD own products" ON products;
CREATE POLICY "Users can CRUD own products" ON products
  FOR ALL USING (auth.uid() = user_id);

DROP POLICY IF EXISTS "Users can CRUD own customers" ON customers;
CREATE POLICY "Users can CRUD own customers" ON customers
  FOR ALL USING (auth.uid() = user_id);

DROP POLICY IF EXISTS "Users can CRUD own orders" ON orders;
CREATE POLICY "Users can CRUD own orders" ON orders
  FOR ALL USING (auth.uid() = user_id);

DROP POLICY IF EXISTS "Users can CRUD own store" ON stores;
CREATE POLICY "Users can CRUD own store" ON stores
  FOR ALL USING (auth.uid() = user_id);

-- 索引
CREATE INDEX IF NOT EXISTS idx_products_user ON products(user_id);
CREATE INDEX IF NOT EXISTS idx_customers_user ON customers(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_user ON orders(user_id);
