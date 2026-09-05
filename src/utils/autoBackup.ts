import AsyncStorage from '@react-native-async-storage/async-storage';
import { File, Paths } from 'expo-file-system';
import * as XLSX from 'xlsx';
import { localDateKey } from './format';

const BACKUP_KEY = 'last_backup_date';
const BACKUP_ENABLED_KEY = 'auto_backup_enabled';

// 检查是否启用自动备份
export async function isAutoBackupEnabled(): Promise<boolean> {
  try {
    const value = await AsyncStorage.getItem(BACKUP_ENABLED_KEY);
    return value === 'true';
  } catch {
    return true; // 默认启用
  }
}

// 设置自动备份开关
export async function setAutoBackupEnabled(enabled: boolean): Promise<void> {
  await AsyncStorage.setItem(BACKUP_ENABLED_KEY, enabled ? 'true' : 'false');
}

// 获取上次备份日期
async function getLastBackupDate(): Promise<string | null> {
  try {
    return await AsyncStorage.getItem(BACKUP_KEY);
  } catch {
    return null;
  }
}

// 设置上次备份日期
async function setLastBackupDate(date: string): Promise<void> {
  await AsyncStorage.setItem(BACKUP_KEY, date);
}

// 执行自动备份
export async function autoBackupIfNeeded(
  products: any[],
  customers: any[],
  orders: any[]
): Promise<{ backedUp: boolean; fileName?: string }> {
  try {
    // 检查是否启用自动备份
    const enabled = await isAutoBackupEnabled();
    if (!enabled) return { backedUp: false };

    // 检查是否今天已经备份过
    const today = localDateKey();
    const lastBackup = await getLastBackupDate();
    if (lastBackup === today) return { backedUp: false };

    // 检查是否有数据
    if ((!products || products.length === 0) && 
        (!customers || customers.length === 0) && 
        (!orders || orders.length === 0)) {
      return { backedUp: false };
    }

    // 生成Excel
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

    // 保存文件
    const wbout = XLSX.write(wb, { type: 'base64', bookType: 'xlsx' });
    const fileName = `金豆库管_备份_${today}.xlsx`;
    const file = new File(Paths.document, fileName);
    
    if (file.exists) file.delete();
    await file.write(wbout, { encoding: 'base64' });

    // 检查文件是否保存成功
    const fileExists = await file.exists();
    if (fileExists) {
      await setLastBackupDate(today);
      return { backedUp: true, fileName };
    }
    
    return { backedUp: false };
  } catch (e) {
    console.log('自动备份失败:', e);
    return { backedUp: false };
  }
}
