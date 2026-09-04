import AsyncStorage from '@react-native-async-storage/async-storage';

const LOG_KEY = 'app_crash_logs';
const MAX_LOGS = 50;
const FLUSH_INTERVAL = 2000; // 2秒批量落盘
const MAX_BUFFER_SIZE = 20; // 缓冲区最大条数

export type LogEntry = {
  id: string;
  time: string;
  level: 'error' | 'warn' | 'info';
  tag: string;
  message: string;
  stack?: string;
};

// 内存缓冲区
let buffer: LogEntry[] = [];
let flushTimer: ReturnType<typeof setTimeout> | null = null;
let isFlushing = false;

export function logError(tag: string, message: string, stack?: string) {
  writeLog({ level: 'error', tag, message, stack });
}

export function logWarn(tag: string, message: string) {
  writeLog({ level: 'warn', tag, message });
}

export function logInfo(tag: string, message: string) {
  writeLog({ level: 'info', tag, message });
}

function writeLog(entry: Omit<LogEntry, 'id' | 'time'>) {
  const newEntry: LogEntry = {
    id: Date.now().toString(36) + Math.random().toString(36).slice(2, 5),
    time: new Date().toLocaleString('zh-CN'),
    ...entry,
  };
  
  buffer.unshift(newEntry);
  
  // 限制缓冲区大小
  if (buffer.length > MAX_BUFFER_SIZE) {
    buffer.length = MAX_BUFFER_SIZE;
  }
  
  // error级别立即落盘：App可能马上崩溃，等不到定时器
  if (entry.level === 'error') {
    flushBuffer();
    return;
  }
  
  // 如果没有定时器，设置定时器
  if (!flushTimer && !isFlushing) {
    flushTimer = setTimeout(flushBuffer, FLUSH_INTERVAL);
  }
}

async function flushBuffer() {
  if (isFlushing || buffer.length === 0) {
    flushTimer = null;
    return;
  }
  
  isFlushing = true;
  flushTimer = null;
  
  try {
    const pending = buffer;
    buffer = [];
    
    const logs = await getLogs();
    const newLogs = [...pending, ...logs].slice(0, MAX_LOGS);
    await AsyncStorage.setItem(LOG_KEY, JSON.stringify(newLogs));
  } catch {} finally {
    isFlushing = false;
    // 如果flush期间有新日志进入，补一个定时器
    if (buffer.length > 0 && !flushTimer) {
      flushTimer = setTimeout(flushBuffer, FLUSH_INTERVAL);
    }
  }
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
  buffer = [];
  if (flushTimer) {
    clearTimeout(flushTimer);
    flushTimer = null;
  }
  await AsyncStorage.removeItem(LOG_KEY);
}
