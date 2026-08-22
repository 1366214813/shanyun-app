import { supabase } from '../config/supabase';

export async function syncToCloud(data: {
  products?: any[];
  customers?: any[];
  orders?: any[];
  storeInfo?: any;
}) {
  const { data: { user } } = await supabase.auth.getUser();
  if (!user) return;

  const userId = user.id;

  if (data.products) {
    await supabase.from('products').upsert(
      data.products.map((p) => ({ ...p, user_id: userId, id: p.id })),
      { onConflict: 'id' }
    );
  }

  if (data.customers) {
    await supabase.from('customers').upsert(
      data.customers.map((c) => ({ ...c, user_id: userId, id: c.id })),
      { onConflict: 'id' }
    );
  }

  if (data.orders) {
    await supabase.from('orders').upsert(
      data.orders.map((o) => ({ ...o, user_id: userId, id: o.id })),
      { onConflict: 'id' }
    );
  }

  if (data.storeInfo) {
    await supabase.from('stores').upsert(
      { user_id: userId, ...data.storeInfo },
      { onConflict: 'user_id' }
    );
  }
}

export async function pullFromCloud(): Promise<{
  products: any[];
  customers: any[];
  orders: any[];
  storeInfo: any;
} | null> {
  const { data: { user } } = await supabase.auth.getUser();
  if (!user) return null;

  const userId = user.id;

  const [productsRes, customersRes, ordersRes, storesRes] = await Promise.all([
    supabase.from('products').select('*').eq('user_id', userId),
    supabase.from('customers').select('*').eq('user_id', userId),
    supabase.from('orders').select('*').eq('user_id', userId),
    supabase.from('stores').select('*').eq('user_id', userId).maybeSingle(),
  ]);

  return {
    products: productsRes.data || [],
    customers: customersRes.data || [],
    orders: ordersRes.data || [],
    storeInfo: storesRes.data || null,
  };
}

export async function deleteUserAccount() {
  const { data: { user } } = await supabase.auth.getUser();
  if (!user) return;

  const userId = user.id;
  await supabase.from('products').delete().eq('user_id', userId);
  await supabase.from('customers').delete().eq('user_id', userId);
  await supabase.from('orders').delete().eq('user_id', userId);
  await supabase.from('stores').delete().eq('user_id', userId);
  await supabase.auth.signOut();
}
