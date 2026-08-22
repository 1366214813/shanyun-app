import { Platform } from 'react-native';

export async function saveExcelToDownloads(base64Data: string, fileName: string): Promise<void> {
  const { EncodingType, writeAsStringAsync, Paths } = await import('expo-file-system');
  const fileUri = `${Paths.cache}/${fileName}`;
  await writeAsStringAsync(fileUri, base64Data, { encoding: EncodingType.Base64 });

  const { Share } = await import('react-native');
  await Share.share({
    url: fileUri,
    message: fileUri,
    title: `导出 ${fileName}`,
  });
}
