import { requireNativeModule, NativeModulesProxy } from 'expo-modules-core';

let native;
try {
  native = requireNativeModule('JindouSpp');
} catch {
  native = NativeModulesProxy.JindouSpp;
}

export async function isSupported() {
  try {
    if (!native) return false;
    return !!(await native.nativeSupport());
  } catch {
    return false;
  }
}

export async function listBondedDevices() {
  if (!native) throw new Error('jindou-spp native module not available');
  return await native.listBondedDevices();
}

export async function sppConnect(address) {
  if (!native) throw new Error('jindou-spp native module not available');
  return await native.connect(address);
}

export async function sppDisconnect() {
  if (!native) return;
  try { await native.disconnect(); } catch {}
}

export async function sppIsConnected() {
  if (!native) return false;
  try { return !!(await native.isConnected()); } catch { return false; }
}

export async function sppWrite(base64) {
  if (!native) throw new Error('jindou-spp native module not available');
  return await native.write(base64);
}

export async function sppReadAvailable(count) {
  if (!native) return '';
  try { return await native.readAvailable(count); } catch { return ''; }
}

export function toBase64(bytes) {
  let bin = '';
  for (let i = 0; i < bytes.length; i++) bin += String.fromCharCode(bytes[i]);
  return btoa(bin);
}