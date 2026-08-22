import { Share } from 'react-native';
import { File, Paths } from 'expo-file-system';

export async function saveExcelToDownloads(base64Data: string, fileName: string): Promise<void> {
  const file = new File(Paths.cache, fileName);
  await file.write(base64Data, { encoding: 'base64', overwrite: true });
  await Share.share({ url: file.uri, title: `导出 ${fileName}` });
}
