import { Share, Alert } from 'react-native';
import * as FileSystem from 'expo-file-system';

export async function saveExcelToDownloads(base64Data: string, fileName: string): Promise<void> {
  try {
    const fileUri = FileSystem.cacheDirectory + fileName;
    await FileSystem.writeAsStringAsync(fileUri, base64Data, { encoding: FileSystem.EncodingType.Base64 });
    await Share.share({ url: fileUri, title: `导出 ${fileName}` });
  } catch (e: any) {
    Alert.alert('导出失败', e.message || '未知错误');
  }
}
