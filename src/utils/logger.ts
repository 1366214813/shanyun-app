import AsyncStorage from '@react-native-async-storage/async-storage';

const LOG_KEY = 'app_crash_logs';
const MAX_LOGS = 50;

export type LogEntry = {
  id: string;
  time: string;
  level: 'error' | 'warn' | 'info';
  tag: string;
  message: string;
  stack?: string;
};

export async function logError(tag: string, message: string, stack?: string) {
  await writeLog({ level: 'error', tag, message, stack });
}

export async function logWarn(tag: string, message: string) {
  await writeLog({ level: 'warn', tag, message });
}

export async function logInfo(tag: string, message: string) {
  await writeLog({ level: 'info', tag, message });
}

async function writeLog(entry: Omit<LogEntry, 'id' | 'time'>) {
  try {
    const logs = await getLogs();
    const newEntry: LogEntry = {
      id: Date.now().toString(36) + Math.random().toString(36).slice(2, 5),
      time: new Date().toLocaleString('zh-CN'),
      ...entry,
    };
    logs.unshift(newEntry);
    if (logs.length > MAX_LOGS) logs.length = MAX_LOGS;
    await AsyncStorage.setItem(LOG_KEY, JSON.stringify(logs));
  } catch {}
}

export async function getLogs(): Promise<LogEntry[]> {
  try {
    const raw = await AsyncStorage.getItem(LOG_KEY);
    return raw ? JSON.parse(raw) : [];
  } catch {
    return [];
  }
}

export async function clearLogs() {
  await AsyncStorage.removeItem(LOG_KEY);
}
