package com.prt.print.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.util.Log;
import com.alipay.sdk.m.s.e;
import com.google.mlkit.common.sdkinternal.OptionalModuleUtils;
import com.prt.base.common.ConnectMethod;
import com.taobao.weex.common.Constants;
import com.tencent.open.SocialConstants;
import com.umeng.analytics.pro.f;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: BluetoothScannerMonitor.kt */
/* JADX INFO: loaded from: classes6.dex */
@Metadata(d1 = {"\u0000C\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\b\t*\u0001\u0018\u0018\u00002\u00020\u0001BG\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00126\u0010\u0004\u001a2\u0012\u0013\u0012\u00110\u0006¢\u0006\f\b\u0007\u0012\b\b\b\u0012\u0004\b\b(\t\u0012\u0013\u0012\u00110\n¢\u0006\f\b\u0007\u0012\b\b\b\u0012\u0004\b\b(\u000b\u0012\u0004\u0012\u00020\f0\u0005¢\u0006\u0004\b\r\u0010\u000eJ\u0006\u0010\u001a\u001a\u00020\fJ\u0006\u0010\u001b\u001a\u00020\fJ\b\u0010\u001c\u001a\u00020\fH\u0002J\b\u0010\u001d\u001a\u00020\fH\u0002J\b\u0010\u001e\u001a\u00020\fH\u0007J\u0010\u0010\u001f\u001a\u00020\n2\u0006\u0010\t\u001a\u00020\u0006H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R>\u0010\u0004\u001a2\u0012\u0013\u0012\u00110\u0006¢\u0006\f\b\u0007\u0012\b\b\b\u0012\u0004\b\b(\t\u0012\u0013\u0012\u00110\n¢\u0006\f\b\u0007\u0012\b\b\b\u0012\u0004\b\b(\u000b\u0012\u0004\u0012\u00020\f0\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082D¢\u0006\u0002\n\u0000R\u001d\u0010\u0011\u001a\u0004\u0018\u00010\u00128BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u0015\u0010\u0016\u001a\u0004\b\u0013\u0010\u0014R\u0010\u0010\u0017\u001a\u00020\u0018X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0019¨\u0006 "}, d2 = {"Lcom/prt/print/utils/BluetoothScannerMonitor;", "", f.X, "Landroid/content/Context;", "onScannerStateChanged", "Lkotlin/Function2;", "Landroid/bluetooth/BluetoothDevice;", "Lkotlin/ParameterName;", "name", e.p, "", "connected", "", "<init>", "(Landroid/content/Context;Lkotlin/jvm/functions/Function2;)V", "TAG", "", "adapter", "Landroid/bluetooth/BluetoothAdapter;", "getAdapter", "()Landroid/bluetooth/BluetoothAdapter;", "adapter$delegate", "Lkotlin/Lazy;", SocialConstants.PARAM_RECEIVER, "com/prt/print/utils/BluetoothScannerMonitor$receiver$1", "Lcom/prt/print/utils/BluetoothScannerMonitor$receiver$1;", "start", Constants.Value.STOP, "registerReceivers", "unregisterReceivers", "checkPairedDevicesQuick", "isLikelyScanner", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class BluetoothScannerMonitor {
    private final String TAG;

    /* JADX INFO: renamed from: adapter$delegate, reason: from kotlin metadata */
    private final Lazy adapter;
    private final Context context;
    private final Function2<BluetoothDevice, Boolean, Unit> onScannerStateChanged;
    private final BluetoothScannerMonitor$receiver$1 receiver;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v4, types: [com.prt.print.utils.BluetoothScannerMonitor$receiver$1] */
    public BluetoothScannerMonitor(Context context, Function2<? super BluetoothDevice, ? super Boolean, Unit> onScannerStateChanged) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(onScannerStateChanged, "onScannerStateChanged");
        this.context = context;
        this.onScannerStateChanged = onScannerStateChanged;
        this.TAG = "BTScannerMonitor";
        this.adapter = LazyKt.lazy(new Function0() { // from class: com.prt.print.utils.BluetoothScannerMonitor$$ExternalSyntheticLambda0
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                return BluetoothScannerMonitor.adapter_delegate$lambda$0(this.f$0);
            }
        });
        this.receiver = new BroadcastReceiver() { // from class: com.prt.print.utils.BluetoothScannerMonitor$receiver$1
            /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context ctx, Intent intent) {
                String action = intent != null ? intent.getAction() : null;
                BluetoothDevice bluetoothDevice = intent != null ? (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE") : null;
                if (action != null) {
                    switch (action.hashCode()) {
                        case -1530327060:
                            if (action.equals("android.bluetooth.adapter.action.STATE_CHANGED")) {
                                int intExtra = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
                                Log.d(this.this$0.TAG, "Adapter state changed: " + intExtra);
                                break;
                            }
                            break;
                        case -301431627:
                            if (action.equals("android.bluetooth.device.action.ACL_CONNECTED") && bluetoothDevice != null) {
                                BluetoothScannerMonitor bluetoothScannerMonitor = this.this$0;
                                if (bluetoothScannerMonitor.isLikelyScanner(bluetoothDevice) && BluetoothDeviceExtensionsKt.hasBluetoothConnectPermission(bluetoothScannerMonitor.context)) {
                                    String strSafeName = BluetoothDeviceExtensionsKt.safeName(bluetoothDevice, bluetoothScannerMonitor.context);
                                    if (strSafeName == null) {
                                        strSafeName = bluetoothDevice.getAddress();
                                    }
                                    Log.d(bluetoothScannerMonitor.TAG, "Broadcast: ACL_CONNECTED -> " + bluetoothDevice.getAddress() + " / " + strSafeName);
                                    bluetoothScannerMonitor.onScannerStateChanged.invoke(bluetoothDevice, true);
                                    break;
                                }
                            }
                            break;
                        case 1821585647:
                            if (action.equals("android.bluetooth.device.action.ACL_DISCONNECTED") && bluetoothDevice != null) {
                                BluetoothScannerMonitor bluetoothScannerMonitor2 = this.this$0;
                                if (bluetoothScannerMonitor2.isLikelyScanner(bluetoothDevice)) {
                                    String strSafeName2 = BluetoothDeviceExtensionsKt.safeName(bluetoothDevice, bluetoothScannerMonitor2.context);
                                    if (strSafeName2 == null) {
                                        strSafeName2 = bluetoothDevice.getAddress();
                                    }
                                    Log.d(bluetoothScannerMonitor2.TAG, "Broadcast: ACL_DISCONNECTED -> " + bluetoothDevice.getAddress() + " / " + strSafeName2);
                                    bluetoothScannerMonitor2.onScannerStateChanged.invoke(bluetoothDevice, false);
                                }
                            }
                            break;
                        case 2116862345:
                            if (action.equals("android.bluetooth.device.action.BOND_STATE_CHANGED") && bluetoothDevice != null) {
                                BluetoothScannerMonitor bluetoothScannerMonitor3 = this.this$0;
                                String strSafeName3 = BluetoothDeviceExtensionsKt.safeName(bluetoothDevice, bluetoothScannerMonitor3.context);
                                if (strSafeName3 == null) {
                                    strSafeName3 = bluetoothDevice.getAddress();
                                }
                                Log.d(bluetoothScannerMonitor3.TAG, "BOND state change: " + strSafeName3 + " / bond=" + bluetoothDevice.getBondState());
                            }
                            break;
                    }
                }
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final BluetoothAdapter getAdapter() {
        return (BluetoothAdapter) this.adapter.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final BluetoothAdapter adapter_delegate$lambda$0(BluetoothScannerMonitor bluetoothScannerMonitor) {
        Object systemService = bluetoothScannerMonitor.context.getSystemService(ConnectMethod.BLUETOOTH);
        Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.bluetooth.BluetoothManager");
        return ((BluetoothManager) systemService).getAdapter();
    }

    public final void start() {
        registerReceivers();
        checkPairedDevicesQuick();
    }

    public final void stop() {
        unregisterReceivers();
    }

    private final void registerReceivers() {
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.bluetooth.device.action.ACL_CONNECTED");
            intentFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
            intentFilter.addAction("android.bluetooth.device.action.BOND_STATE_CHANGED");
            intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
            this.context.registerReceiver(this.receiver, intentFilter);
        } catch (Exception e) {
            Log.w(this.TAG, "registerReceivers error: " + e.getMessage());
        }
    }

    private final void unregisterReceivers() {
        try {
            this.context.unregisterReceiver(this.receiver);
        } catch (Exception unused) {
        }
    }

    public final void checkPairedDevicesQuick() {
        Set<BluetoothDevice> setEmptySet;
        if (!BluetoothDeviceExtensionsKt.hasBluetoothConnectPermission(this.context)) {
            Log.d(this.TAG, "Skip quick scan: BLUETOOTH_CONNECT permission not granted");
            return;
        }
        BluetoothAdapter adapter = getAdapter();
        if (adapter == null || (setEmptySet = adapter.getBondedDevices()) == null) {
            setEmptySet = SetsKt.emptySet();
        }
        if (setEmptySet.isEmpty()) {
            Log.d(this.TAG, "No bonded devices");
            return;
        }
        for (final BluetoothDevice bluetoothDevice : setEmptySet) {
            Intrinsics.checkNotNull(bluetoothDevice);
            if (isLikelyScanner(bluetoothDevice)) {
                final Ref.BooleanRef booleanRef = new Ref.BooleanRef();
                String strSafeName = BluetoothDeviceExtensionsKt.safeName(bluetoothDevice, this.context);
                if (strSafeName == null) {
                    strSafeName = bluetoothDevice.getAddress();
                }
                int i = 0;
                try {
                    Iterator it2 = CollectionsKt.listOf((Object[]) new Integer[]{1, 2, 7}).iterator();
                    while (it2.hasNext()) {
                        int iIntValue = ((Number) it2.next()).intValue();
                        final Object obj = new Object();
                        BluetoothAdapter adapter2 = getAdapter();
                        if (adapter2 != null) {
                            adapter2.getProfileProxy(this.context, new BluetoothProfile.ServiceListener() { // from class: com.prt.print.utils.BluetoothScannerMonitor$checkPairedDevicesQuick$1$1
                                /* JADX WARN: Code restructure failed: missing block: B:22:0x0045, code lost:
                                
                                    r1.element = true;
                                 */
                                @Override // android.bluetooth.BluetoothProfile.ServiceListener
                                /*
                                    Code decompiled incorrectly, please refer to instructions dump.
                                */
                                public void onServiceConnected(int profile, BluetoothProfile proxy) {
                                    List<BluetoothDevice> connectedDevices;
                                    if (proxy != null) {
                                        try {
                                            try {
                                                connectedDevices = proxy.getConnectedDevices();
                                            } catch (Exception e) {
                                                Log.w(this.TAG, "profile check error: " + e.getMessage());
                                                BluetoothAdapter adapter3 = this.getAdapter();
                                                if (adapter3 != null) {
                                                    adapter3.closeProfileProxy(profile, proxy);
                                                }
                                                Object obj2 = obj;
                                                synchronized (obj2) {
                                                    obj2.notify();
                                                    Unit unit = Unit.INSTANCE;
                                                    return;
                                                }
                                            }
                                        } catch (Throwable th) {
                                            BluetoothAdapter adapter4 = this.getAdapter();
                                            if (adapter4 != null) {
                                                adapter4.closeProfileProxy(profile, proxy);
                                            }
                                            Object obj3 = obj;
                                            synchronized (obj3) {
                                                obj3.notify();
                                                Unit unit2 = Unit.INSTANCE;
                                                throw th;
                                            }
                                        }
                                    } else {
                                        connectedDevices = null;
                                    }
                                    if (!booleanRef.element && connectedDevices != null) {
                                        List<BluetoothDevice> list = connectedDevices;
                                        BluetoothDevice bluetoothDevice2 = bluetoothDevice;
                                        if (!(list instanceof Collection) || !list.isEmpty()) {
                                            Iterator<T> it3 = list.iterator();
                                            while (true) {
                                                if (!it3.hasNext()) {
                                                    break;
                                                } else if (Intrinsics.areEqual(((BluetoothDevice) it3.next()).getAddress(), bluetoothDevice2.getAddress())) {
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    BluetoothAdapter adapter5 = this.getAdapter();
                                    if (adapter5 != null) {
                                        adapter5.closeProfileProxy(profile, proxy);
                                    }
                                    Object obj4 = obj;
                                    synchronized (obj4) {
                                        obj4.notify();
                                        Unit unit3 = Unit.INSTANCE;
                                    }
                                }

                                @Override // android.bluetooth.BluetoothProfile.ServiceListener
                                public void onServiceDisconnected(int profile) {
                                    Object obj2 = obj;
                                    synchronized (obj2) {
                                        obj2.notify();
                                        Unit unit = Unit.INSTANCE;
                                    }
                                }
                            }, iIntValue);
                        }
                        try {
                            synchronized (obj) {
                                obj.wait(300L);
                                Unit unit = Unit.INSTANCE;
                            }
                        } catch (Exception unused) {
                        }
                        if (!booleanRef.element) {
                        }
                    }
                } catch (Exception e) {
                    Log.w(this.TAG, "profile proxy attempt failed: " + e.getMessage());
                }
                try {
                    ParcelUuid[] uuids = bluetoothDevice.getUuids();
                    if (!booleanRef.element && uuids != null) {
                        int length = uuids.length;
                        while (true) {
                            if (i >= length) {
                                break;
                            }
                            if (StringsKt.equals(uuids[i].getUuid().toString(), "00001101-0000-1000-8000-00805F9B34FB", true)) {
                                Log.d(this.TAG, "Device " + strSafeName + " advertises SPP UUID");
                                break;
                            }
                            i++;
                        }
                    }
                } catch (Exception unused2) {
                }
                if (!booleanRef.element) {
                    try {
                        Object objInvoke = bluetoothDevice.getClass().getMethod("isConnected", null).invoke(bluetoothDevice, null);
                        Boolean bool = objInvoke instanceof Boolean ? (Boolean) objInvoke : null;
                        if (Intrinsics.areEqual((Object) bool, (Object) true)) {
                            booleanRef.element = true;
                        }
                        Log.d(this.TAG, "Reflection isConnected for " + strSafeName + " -> " + bool);
                    } catch (NoSuchMethodException unused3) {
                    } catch (Exception e2) {
                        Log.w(this.TAG, "reflection check failed: " + e2.getMessage());
                    }
                }
                Log.d(this.TAG, "Device " + strSafeName + " (" + bluetoothDevice.getAddress() + ") likelyScanner=" + isLikelyScanner(bluetoothDevice) + " connected=" + booleanRef.element);
                this.onScannerStateChanged.invoke(bluetoothDevice, Boolean.valueOf(booleanRef.element));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean isLikelyScanner(BluetoothDevice device) {
        String strSafeName = BluetoothDeviceExtensionsKt.safeName(device, this.context);
        if (strSafeName != null) {
            String lowerCase = strSafeName.toLowerCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue(lowerCase, "toLowerCase(...)");
            if (lowerCase != null) {
                String str = lowerCase;
                return StringsKt.contains$default((CharSequence) str, (CharSequence) "scan", false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, (CharSequence) "scanner", false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, (CharSequence) OptionalModuleUtils.BARCODE, false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, (CharSequence) "gun", false, 2, (Object) null);
            }
        }
        return false;
    }
}
