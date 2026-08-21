import { logError } from './logger';
import { Alert } from 'react-native';

export function handleError(err: unknown, ctx: string): void {
  const message = err instanceof Error ? err.message : String(err);
  logError(ctx, message);
  Alert.alert(`错误 – ${ctx}`, message);
}
