import AsyncStorage from '@react-native-async-storage/async-storage';
import * as FileSystem from 'expo-file-system';
import * as XLSX from 'xlsx';
import { localDateKey } from './format';

const BACKUP_KEY = 'last_backup_date';
const BACKUP_ENABLED_KEY = 'auto_backup_enabled';

export async function isAutoBackupEnabled(): Promise<boolean> {
  try {
    const value = await AsyncStorage.getItem(BACKUP_ENABLED_KEY);
    return value === 'true';
  } catch {
    return true;
  }
}

export async function setAutoBackupEnabled(enabled: boolean): Promise<void> {
  await AsyncStorage.setItem(BACKUP_ENABLED_KEY, enabled ? 'true' : 'false');
}

async function getLastBackupDate(): Promise<string | null> {
  try {
    return await AsyncStorage.getItem(BACKUP_KEY);
  } catch {
    return null;
  }
}

async function setLastBackupDate(date: string): Promise<void> {
  await AsyncStorage.setItem(BACKUP_KEY, date);
}

export async function autoBackupIfNeeded(
  products: any[],
  customers: any[],
  orders: any[]
): Promise<{ backedUp: boolean; fileName?: string }> {
  try {
    const enabled = await isAutoBackupEnabled();
    if (!enabled) return { backedUp: false };

    const today = localDateKey();
    const lastBackup = await getLastBackupDate();
    if (lastBackup === today) return { backedUp: false };

    if ((!products || products.length === 0) && 
        (!customers || customers.length === 0) && 
        (!orders || orders.length === 0)) {
      return { backedUp: false };
    }

    const wb = XLSX.utils.book_new();
    
    if (products && products.length > 0) {
      const productData = products.map((p: any) => ({
        '商品名称': p.name, '款号': p.code, '分类': p.category,
        '零售价': p.retailPrice, '进货价': p.purchasePrice, '库存': p.stock,
        '预警库存': p.warningStock, '单位': p.unit,
      }));
      XLSX.utils.book_append_sheet(wb, XLSX.utils.json_to_sheet(productData), '商品');
    }
    
    if (customers && customers.length > 0) {
      const customerData = customers.map((c: any) => ({
        '客户姓名': c.name, '电话': c.phone,
        '会员等级': c.level === 'platinum' ? '铂金会员' : c.level === 'gold' ? '黄金会员' : c.level === 'vip' ? 'VIP' : '普通会员',
        '积分': c.points, '余额': c.balance, '累计消费': c.totalSpent, '生日': c.birthday,
      }));
      XLSX.utils.book_append_sheet(wb, XLSX.utils.json_to_sheet(customerData), '客户');
    }
    
    if (orders && orders.length > 0) {
      const orderData = orders.map((o: any) => ({
        '订单日期': o.date, '客户': o.customerName,
        '商品明细': o.items.map((i: any) => `${i.productName}×${i.qty}`).join(', '),
        '订单金额': o.total, '成本': o.cost, '利润': o.profit, '支付方式': o.payMethod,
        '状态': o.status === 'completed' ? '完成' : o.status === 'cancelled' ? '取消' : '退货',
      }));
      XLSX.utils.book_append_sheet(wb, XLSX.utils.json_to_sheet(orderData), '订单');
    }

    const wbout = XLSX.write(wb, { type: 'base64', bookType: 'xlsx' });
    const fileName = `金豆库管_备份_${today}.xlsx`;
    const fileUri = FileSystem.documentDirectory + fileName;
    
    await FileSystem.writeAsStringAsync(fileUri, wbout, { encoding: FileSystem.EncodingType.Base64 });

    await setLastBackupDate(today);
    return { backedUp: true, fileName };
  } catch (e) {
    console.log('自动备份失败:', e);
    return { backedUp: false };
  }
}
