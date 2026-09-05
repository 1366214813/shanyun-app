import { Share, Platform, Alert } from 'react-native';
import { File, Paths } from 'expo-file-system';

export async function saveExcelToDownloads(base64Data: string, fileName: string): Promise<void> {
  // 先保存到Documents目录（用户可以在文件管理器中找到）
  const docFile = new File(Paths.document, fileName);
  if (docFile.exists) docFile.delete();
  await docFile.write(base64Data, { encoding: 'base64' });
  
  // 检查文件是否保存成功
  const docFileExists = await docFile.exists();
  const docFileInfo = docFileExists ? await docFile.info() : null;
  const docFileSize = docFileInfo?.size || 0;
  
  // 同时保存到缓存目录用于分享
  const cacheFile = new File(Paths.cache, fileName);
  if (cacheFile.exists) cacheFile.delete();
  await cacheFile.write(base64Data, { encoding: 'base64' });
  
  // 检查缓存文件
  const cacheFileExists = await cacheFile.exists();
  const cacheFileInfo = cacheFileExists ? await cacheFile.info() : null;
  const cacheFileSize = cacheFileInfo?.size || 0;
  
  // 弹出分享菜单
  await Share.share({ url: cacheFile.uri, title: `导出 ${fileName}` });
  
  // 显示详细结果
  const status = docFileExists && cacheFileExists ? '成功' : '部分失败';
  const details = [
    `Documents: ${docFileExists ? '✓ ' + docFileSize + ' bytes' : '✗ 保存失败'}`,
    `Cache: ${cacheFileExists ? '✓ ' + cacheFileSize + ' bytes' : '✗ 保存失败'}`,
  ].join('\n');
  
  Alert.alert(
    `导出${status}`,
    `文件：${fileName}\n\n${details}\n\n路径：\n${docFile.uri}`,
    [{ text: '确定' }]
  );
}
