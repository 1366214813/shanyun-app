import { Share, Alert } from 'react-native';
import { File, Paths } from 'expo-file-system';

export async function saveExcelToDownloads(base64Data: string, fileName: string): Promise<void> {
  try {
    // 保存到缓存目录
    const file = new File(Paths.cache, fileName);
    if (file.exists) file.delete();
    await file.write(base64Data, { encoding: 'base64' });

    // 弹出分享菜单
    const result = await Share.share({ url: file.uri, title: `导出 ${fileName}` });

    // Android 提示文件位置
    if (result.action === Share.dismissedAction) {
      Alert.alert('导出成功', `文件已保存，请在文件管理器中查看`);
    }
  } catch (e: any) {
    Alert.alert('导出失败', e.message || '未知错误');
  }
}
