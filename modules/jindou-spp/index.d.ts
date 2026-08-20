export type SppDevice = {
  id: string;
  name: string;
  bonded: boolean;
};

export function isSupported(): Promise<boolean>;
export function listBondedDevices(): Promise<SppDevice[]>;
export function sppConnect(address: string): Promise<{ connected: boolean; name?: string }>;
export function sppDisconnect(): Promise<void>;
export function sppIsConnected(): Promise<boolean>;
export function sppWrite(base64: string): Promise<{ written: number }>;
export function sppReadAvailable(count: number): Promise<string>;
export function toBase64(bytes: Uint8Array): string;
