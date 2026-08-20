package com.prt.print.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import com.alipay.sdk.m.s.e;
import com.blankj.utilcode.util.LogUtils;
import com.prt.base.utils.UniAppManager;
import com.taobao.accs.utl.BaseMonitor;
import com.taobao.weex.el.parse.Operators;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: ScanGunManager.kt */
/* JADX INFO: loaded from: classes6.dex */
@Metadata(d1 = {"\u0000h\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\n\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J>\u0010%\u001a\u00020\u00142\b\u0010&\u001a\u0004\u0018\u00010'2\u0006\u0010(\u001a\u00020\u00132\u0018\u0010)\u001a\u0014\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00140\u00122\b\b\u0002\u0010*\u001a\u00020+H\u0007J\u0018\u0010,\u001a\u00020\f2\u0006\u0010-\u001a\u00020.2\u0006\u0010*\u001a\u00020+H\u0003J \u0010/\u001a\u00020\f2\u0006\u0010-\u001a\u00020.2\u0006\u00100\u001a\u00020+2\u0006\u0010*\u001a\u00020+H\u0003J\b\u00101\u001a\u00020\u0014H\u0003J\u0010\u00102\u001a\u00020\u00142\u0006\u00103\u001a\u00020\u0013H\u0002J\u0006\u00104\u001a\u00020\u0014J\b\u00105\u001a\u00020\u0014H\u0002J\u0006\u00106\u001a\u00020\u0014J\u0006\u0010\u000b\u001a\u00020\fJ\u0006\u00107\u001a\u00020\u0013R\u0016\u0010\u0004\u001a\n \u0006*\u0004\u0018\u00010\u00050\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000R0\u0010\u0011\u001a\u0018\u0012\u0004\u0012\u00020\f\u0012\u0006\u0012\u0004\u0018\u00010\u0013\u0012\u0004\u0012\u00020\u0014\u0018\u00010\u0012X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018R(\u0010\u0019\u001a\u0010\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u0014\u0018\u00010\u001aX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u001c\"\u0004\b\u001d\u0010\u001eR\"\u0010\u001f\u001a\n\u0012\u0004\u0012\u00020\u0014\u0018\u00010 X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b!\u0010\"\"\u0004\b#\u0010$¨\u00068"}, d2 = {"Lcom/prt/print/utils/ScanGunManager;", "", "<init>", "()V", "SPP_UUID", "Ljava/util/UUID;", "kotlin.jvm.PlatformType", "bluetoothSocket", "Landroid/bluetooth/BluetoothSocket;", "listenThread", "Ljava/lang/Thread;", "isConnected", "", "connectionThread", "executorService", "Ljava/util/concurrent/ExecutorService;", "pairingInProgress", "onConnectionStateChanged", "Lkotlin/Function2;", "", "", "getOnConnectionStateChanged", "()Lkotlin/jvm/functions/Function2;", "setOnConnectionStateChanged", "(Lkotlin/jvm/functions/Function2;)V", "onScanDataReceived", "Lkotlin/Function1;", "getOnScanDataReceived", "()Lkotlin/jvm/functions/Function1;", "setOnScanDataReceived", "(Lkotlin/jvm/functions/Function1;)V", "onPairingRequest", "Lkotlin/Function0;", "getOnPairingRequest", "()Lkotlin/jvm/functions/Function0;", "setOnPairingRequest", "(Lkotlin/jvm/functions/Function0;)V", BaseMonitor.ALARM_POINT_CONNECT, "bluetoothAdapter", "Landroid/bluetooth/BluetoothAdapter;", "deviceAddress", "onResult", "timeoutSeconds", "", "tryConnectWithTimeout", e.p, "Landroid/bluetooth/BluetoothDevice;", "tryReflectConnectWithTimeout", "channel", "startListening", "handleScanData", "data", "disconnect", "cleanup", "shutdown", "getConnectionStatus", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class ScanGunManager {
    public static final ScanGunManager INSTANCE = new ScanGunManager();
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static BluetoothSocket bluetoothSocket;
    private static Thread connectionThread;
    private static final ExecutorService executorService;
    private static boolean isConnected;
    private static Thread listenThread;
    private static Function2<? super Boolean, ? super String, Unit> onConnectionStateChanged;
    private static Function0<Unit> onPairingRequest;
    private static Function1<? super String, Unit> onScanDataReceived;
    private static volatile boolean pairingInProgress;

    private ScanGunManager() {
    }

    static {
        ExecutorService executorServiceNewFixedThreadPool = Executors.newFixedThreadPool(2);
        Intrinsics.checkNotNullExpressionValue(executorServiceNewFixedThreadPool, "newFixedThreadPool(...)");
        executorService = executorServiceNewFixedThreadPool;
    }

    public final Function2<Boolean, String, Unit> getOnConnectionStateChanged() {
        return onConnectionStateChanged;
    }

    public final void setOnConnectionStateChanged(Function2<? super Boolean, ? super String, Unit> function2) {
        onConnectionStateChanged = function2;
    }

    public final Function1<String, Unit> getOnScanDataReceived() {
        return onScanDataReceived;
    }

    public final void setOnScanDataReceived(Function1<? super String, Unit> function1) {
        onScanDataReceived = function1;
    }

    public final Function0<Unit> getOnPairingRequest() {
        return onPairingRequest;
    }

    public final void setOnPairingRequest(Function0<Unit> function0) {
        onPairingRequest = function0;
    }

    public static /* synthetic */ void connect$default(ScanGunManager scanGunManager, BluetoothAdapter bluetoothAdapter, String str, Function2 function2, int i, int i2, Object obj) {
        if ((i2 & 8) != 0) {
            i = 10;
        }
        scanGunManager.connect(bluetoothAdapter, str, function2, i);
    }

    public final void connect(final BluetoothAdapter bluetoothAdapter, final String deviceAddress, final Function2<? super Boolean, ? super String, Unit> onResult, final int timeoutSeconds) {
        Intrinsics.checkNotNullParameter(deviceAddress, "deviceAddress");
        Intrinsics.checkNotNullParameter(onResult, "onResult");
        if (isConnected) {
            disconnect();
        }
        Thread thread = new Thread(new Runnable() { // from class: com.prt.print.utils.ScanGunManager$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ScanGunManager.connect$lambda$0(bluetoothAdapter, deviceAddress, timeoutSeconds, onResult);
            }
        });
        connectionThread = thread;
        thread.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void connect$lambda$0(BluetoothAdapter bluetoothAdapter, String str, int i, Function2 function2) {
        String message;
        if (bluetoothAdapter != null) {
            try {
                if (bluetoothAdapter.isEnabled()) {
                    if (bluetoothAdapter.isDiscovering()) {
                        bluetoothAdapter.cancelDiscovery();
                    }
                    while (bluetoothAdapter.isDiscovering()) {
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(str);
                    Intrinsics.checkNotNull(remoteDevice);
                    String strSafeName$default = BluetoothDeviceExtensionsKt.safeName$default(remoteDevice, null, 1, null);
                    LogUtils.d("ScanGunManager", "🔗 开始连接: " + (strSafeName$default == null ? remoteDevice.getAddress() : strSafeName$default));
                    if (remoteDevice.getBondState() != 12) {
                        LogUtils.d("ScanGunManager", "⚠ 设备未配对，当前状态: " + remoteDevice.getBondState());
                        LogUtils.d("ScanGunManager", "尝试发起配对...");
                        boolean zCreateBond = remoteDevice.createBond();
                        LogUtils.d("ScanGunManager", "配对请求已发送，返回: " + zCreateBond);
                        if (!zCreateBond) {
                            LogUtils.w("ScanGunManager", "⚠ 配对请求失败，但仍会尝试连接");
                        } else {
                            int i2 = 0;
                            while (true) {
                                if (i2 < 40000) {
                                    switch (remoteDevice.getBondState()) {
                                        case 10:
                                            LogUtils.w("ScanGunManager", "等待配对...");
                                            continue;
                                            Thread.sleep(500L);
                                            i2 += 500;
                                            break;
                                        case 11:
                                            if (i2 % 2000 == 0) {
                                                LogUtils.d("ScanGunManager", "等待用户确认配对... (" + (i2 / 1000) + "s)");
                                            } else {
                                                continue;
                                            }
                                            Thread.sleep(500L);
                                            i2 += 500;
                                            break;
                                        case 12:
                                            LogUtils.d("ScanGunManager", "✓ 配对成功！等待设备就绪...");
                                            Thread.sleep(2000L);
                                            break;
                                        default:
                                            continue;
                                            Thread.sleep(500L);
                                            i2 += 500;
                                            break;
                                    }
                                }
                            }
                            if (i2 >= 40000 && remoteDevice.getBondState() == 11) {
                                LogUtils.w("ScanGunManager", "⚠ 配对超时，但仍会尝试连接");
                            }
                        }
                        LogUtils.d("ScanGunManager", "配对流程结束，最终状态: " + remoteDevice.getBondState());
                    } else {
                        LogUtils.d("ScanGunManager", "✓ 设备已配对，直接连接");
                    }
                    ScanGunManager scanGunManager = INSTANCE;
                    if (scanGunManager.tryConnectWithTimeout(remoteDevice, i)) {
                        isConnected = true;
                        LogUtils.d("ScanGunManager", "✓ 连接成功: " + (strSafeName$default == null ? remoteDevice.getAddress() : strSafeName$default));
                        Function2<? super Boolean, ? super String, Unit> function22 = onConnectionStateChanged;
                        if (function22 != null) {
                            function22.invoke(true, strSafeName$default);
                        }
                        scanGunManager.startListening();
                        function2.invoke(true, "连接成功");
                        return;
                    }
                    throw new IOException("连接超时或被设备拒绝");
                }
            } catch (Exception e2) {
                LogUtils.e("ScanGunManager", "✗ 连接失败: " + e2.getMessage());
                e2.printStackTrace();
                ScanGunManager scanGunManager2 = INSTANCE;
                isConnected = false;
                Function2<? super Boolean, ? super String, Unit> function23 = onConnectionStateChanged;
                if (function23 != null) {
                    function23.invoke(false, null);
                }
                String message2 = e2.getMessage();
                if (message2 == null || !StringsKt.contains$default((CharSequence) message2, (CharSequence) "timeout", false, 2, (Object) null)) {
                    String message3 = e2.getMessage();
                    if (message3 == null || !StringsKt.contains$default((CharSequence) message3, (CharSequence) "Connection refused", false, 2, (Object) null)) {
                        String message4 = e2.getMessage();
                        if (message4 == null || !StringsKt.contains$default((CharSequence) message4, (CharSequence) "read failed", false, 2, (Object) null)) {
                            message = e2.getMessage();
                            if (message == null) {
                                message = "未知错误";
                            }
                        } else {
                            message = "连接中断，设备可能断开了";
                        }
                    } else {
                        message = "设备拒绝连接";
                    }
                } else {
                    message = "连接超时，请确认设备已开启";
                }
                function2.invoke(false, message);
                scanGunManager2.cleanup();
                return;
            }
        }
        throw new IOException("蓝牙不可用");
    }

    private final boolean tryConnectWithTimeout(final BluetoothDevice device, int timeoutSeconds) {
        Future futureSubmit;
        final BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        final boolean z = false;
        LogUtils.d("ScanGunManager", "【方案1】标准RFCOMM (超时: " + timeoutSeconds + "s)...");
        try {
            futureSubmit = executorService.submit(new Callable() { // from class: com.prt.print.utils.ScanGunManager$$ExternalSyntheticLambda3
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    return ScanGunManager.tryConnectWithTimeout$lambda$1(z, device, defaultAdapter);
                }
            });
        } catch (Exception e) {
            e = e;
            futureSubmit = null;
        }
        try {
            if (((Boolean) futureSubmit.get(timeoutSeconds, TimeUnit.SECONDS)).booleanValue()) {
                return true;
            }
        } catch (Exception e2) {
            e = e2;
            LogUtils.w("ScanGunManager", "  ⏱ 标准方式超时或异常: " + e.getClass().getSimpleName());
            if (futureSubmit != null) {
                futureSubmit.cancel(true);
            }
            try {
                BluetoothSocket bluetoothSocket2 = bluetoothSocket;
                if (bluetoothSocket2 != null) {
                    bluetoothSocket2.close();
                }
            } catch (Exception unused) {
            }
            bluetoothSocket = null;
        }
        LogUtils.d("ScanGunManager", "【方案2】反射方式 channel 1...");
        return tryReflectConnectWithTimeout(device, 1, timeoutSeconds);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Boolean tryConnectWithTimeout$lambda$1(boolean z, BluetoothDevice bluetoothDevice, BluetoothAdapter bluetoothAdapter) {
        BluetoothSocket bluetoothSocketCreateInsecureRfcommSocketToServiceRecord;
        try {
            if (z) {
                LogUtils.d("ScanGunManager", "  使用 createRfcommSocketToServiceRecord (旧版)");
                bluetoothSocketCreateInsecureRfcommSocketToServiceRecord = bluetoothDevice.createRfcommSocketToServiceRecord(SPP_UUID);
            } else {
                LogUtils.d("ScanGunManager", "  使用 createInsecureRfcommSocketToServiceRecord (新版)");
                bluetoothSocketCreateInsecureRfcommSocketToServiceRecord = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(SPP_UUID);
            }
            bluetoothSocket = bluetoothSocketCreateInsecureRfcommSocketToServiceRecord;
            bluetoothAdapter.cancelDiscovery();
            if (bluetoothAdapter.isDiscovering()) {
                int i = 0;
                while (i < 5) {
                    Thread.sleep(100L);
                    i++;
                    if (bluetoothAdapter.cancelDiscovery()) {
                        break;
                    }
                }
            }
            LogUtils.d("ScanGunManager", "  开始 connect()...");
            BluetoothSocket bluetoothSocket2 = bluetoothSocket;
            if (bluetoothSocket2 != null) {
                bluetoothSocket2.connect();
            }
            LogUtils.d("ScanGunManager", "  ✓ connect() 成功");
            return true;
        } catch (Exception e) {
            LogUtils.w("ScanGunManager", "  ✗ 标准方式失败: " + e.getMessage());
            try {
                BluetoothSocket bluetoothSocket3 = bluetoothSocket;
                if (bluetoothSocket3 != null) {
                    bluetoothSocket3.close();
                }
            } catch (Exception unused) {
            }
            bluetoothSocket = null;
            return false;
        }
    }

    private final boolean tryReflectConnectWithTimeout(final BluetoothDevice device, final int channel, int timeoutSeconds) {
        Future futureSubmit;
        final BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        LogUtils.d("ScanGunManager", "【方案2】反射 channel " + channel + " (超时: " + timeoutSeconds + "s)...");
        try {
            futureSubmit = executorService.submit(new Callable() { // from class: com.prt.print.utils.ScanGunManager$$ExternalSyntheticLambda2
                @Override // java.util.concurrent.Callable
                public final Object call() {
                    return ScanGunManager.tryReflectConnectWithTimeout$lambda$2(device, channel, defaultAdapter);
                }
            });
        } catch (Exception unused) {
            futureSubmit = null;
        }
        try {
        } catch (Exception unused2) {
            LogUtils.w("ScanGunManager", "  ⏱ 反射方式超时或异常");
            if (futureSubmit != null) {
                try {
                    futureSubmit.cancel(true);
                } catch (Exception unused3) {
                    bluetoothSocket = null;
                }
            }
            BluetoothSocket bluetoothSocket2 = bluetoothSocket;
            if (bluetoothSocket2 != null) {
                bluetoothSocket2.close();
            }
            bluetoothSocket = null;
        }
        return ((Boolean) futureSubmit.get((long) timeoutSeconds, TimeUnit.SECONDS)).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Boolean tryReflectConnectWithTimeout$lambda$2(BluetoothDevice bluetoothDevice, int i, BluetoothAdapter bluetoothAdapter) {
        try {
            Object objInvoke = bluetoothDevice.getClass().getMethod("createRfcommSocket", Integer.TYPE).invoke(bluetoothDevice, Integer.valueOf(i));
            Intrinsics.checkNotNull(objInvoke, "null cannot be cast to non-null type android.bluetooth.BluetoothSocket");
            bluetoothSocket = (BluetoothSocket) objInvoke;
            if (bluetoothAdapter.isDiscovering()) {
                int i2 = 0;
                while (i2 < 5) {
                    Thread.sleep(100L);
                    i2++;
                    if (bluetoothAdapter.cancelDiscovery()) {
                        break;
                    }
                }
            }
            LogUtils.d("ScanGunManager", "  Socket 已创建 (channel " + i + Operators.BRACKET_END_STR);
            LogUtils.d("ScanGunManager", "  开始 connect()...");
            BluetoothSocket bluetoothSocket2 = bluetoothSocket;
            if (bluetoothSocket2 != null) {
                bluetoothSocket2.connect();
            }
            LogUtils.d("ScanGunManager", "  ✓ connect() 成功");
            return true;
        } catch (Exception e) {
            LogUtils.w("ScanGunManager", "  ✗ 反射方式失败: " + e.getMessage());
            try {
                BluetoothSocket bluetoothSocket3 = bluetoothSocket;
                if (bluetoothSocket3 != null) {
                    bluetoothSocket3.close();
                }
            } catch (Exception unused) {
            }
            bluetoothSocket = null;
            return false;
        }
    }

    private final void startListening() {
        Thread thread = new Thread(new Runnable() { // from class: com.prt.print.utils.ScanGunManager$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ScanGunManager.startListening$lambda$3();
            }
        });
        listenThread = thread;
        thread.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x004b, code lost:
    
        r8 = new java.lang.String(r6, 0, r9, kotlin.text.Charsets.UTF_8);
        com.blankj.utilcode.util.LogUtils.d("ScanGunManager", "📨 收到: " + r8 + " (" + r9 + " bytes)");
        r7.append(r8);
        r8 = r7.toString();
        kotlin.jvm.internal.Intrinsics.checkNotNullExpressionValue(r8, "toString(...)");
     */
    /* JADX WARN: Code restructure failed: missing block: B:24:0x0092, code lost:
    
        if (kotlin.text.StringsKt.contains$default((java.lang.CharSequence) r8, (java.lang.CharSequence) "\n", false, 2, (java.lang.Object) null) != false) goto L60;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x009f, code lost:
    
        if (kotlin.text.StringsKt.contains$default((java.lang.CharSequence) r8, (java.lang.CharSequence) "\r", false, 2, (java.lang.Object) null) == false) goto L63;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x00a1, code lost:
    
        r8 = kotlin.text.StringsKt.trim((java.lang.CharSequence) r8).toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x00b2, code lost:
    
        if (r8.length() <= 0) goto L30;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x00b4, code lost:
    
        com.blankj.utilcode.util.LogUtils.d("ScanGunManager", "✓ 完整数据: " + r8);
        com.prt.print.utils.ScanGunManager.INSTANCE.handleScanData(r8);
     */
    /* JADX WARN: Code restructure failed: missing block: B:30:0x00d4, code lost:
    
        kotlin.text.StringsKt.clear(r7);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final void startListening$lambda$3() {
        int i;
        try {
            BluetoothSocket bluetoothSocket2 = bluetoothSocket;
            InputStream inputStream = bluetoothSocket2 != null ? bluetoothSocket2.getInputStream() : null;
            byte[] bArr = new byte[1024];
            StringBuilder sb = new StringBuilder();
            LogUtils.d("ScanGunManager", "🎧 开始监听数据...");
            loop0: while (true) {
                int i2 = 0;
                while (true) {
                    BluetoothSocket bluetoothSocket3 = bluetoothSocket;
                    if (bluetoothSocket3 == null || !bluetoothSocket3.isConnected() || Thread.currentThread().isInterrupted() || !isConnected) {
                        break loop0;
                    }
                    if (inputStream != null) {
                        try {
                            i = inputStream.read(bArr);
                        } catch (IOException e) {
                            LogUtils.e("ScanGunManager", "读取异常: " + e.getMessage());
                            isConnected = false;
                            Function2<? super Boolean, ? super String, Unit> function2 = onConnectionStateChanged;
                            if (function2 != null) {
                                function2.invoke(false, null);
                            }
                        }
                    } else {
                        i = 0;
                    }
                    if (i > 0) {
                        break;
                    }
                    if (i == -1) {
                        LogUtils.w("ScanGunManager", "连接已关闭 (read = -1)");
                        isConnected = false;
                        Function2<? super Boolean, ? super String, Unit> function22 = onConnectionStateChanged;
                        if (function22 != null) {
                            function22.invoke(false, null);
                        }
                    } else {
                        i2++;
                        if (i2 > 100) {
                            LogUtils.w("ScanGunManager", "连续空读过多，认为连接断开");
                            isConnected = false;
                            Function2<? super Boolean, ? super String, Unit> function23 = onConnectionStateChanged;
                            if (function23 != null) {
                                function23.invoke(false, null);
                            }
                        } else {
                            Thread.sleep(10L);
                        }
                    }
                }
            }
            LogUtils.d("ScanGunManager", "🎧 监听线程结束");
        } catch (Exception e2) {
            LogUtils.e("ScanGunManager", "监听线程异常: " + e2.getMessage());
            e2.printStackTrace();
        }
    }

    private final void handleScanData(String data) {
        Function1<? super String, Unit> function1 = onScanDataReceived;
        if (function1 != null) {
            function1.invoke(data);
        }
        try {
            UniAppManager.INSTANCE.sendScanData(data);
            LogUtils.d("ScanGunManager", "✓ 数据已发送到 UniApp");
        } catch (Exception e) {
            LogUtils.e("ScanGunManager", "发送失败: " + e.getMessage());
        }
    }

    public final void disconnect() {
        LogUtils.d("ScanGunManager", "🔌 断开连接");
        isConnected = false;
        cleanup();
        Function2<? super Boolean, ? super String, Unit> function2 = onConnectionStateChanged;
        if (function2 != null) {
            function2.invoke(false, null);
        }
    }

    private final void cleanup() {
        try {
            Thread thread = listenThread;
            if (thread != null) {
                thread.interrupt();
            }
            listenThread = null;
            Thread thread2 = connectionThread;
            if (thread2 != null) {
                thread2.interrupt();
            }
            connectionThread = null;
            BluetoothSocket bluetoothSocket2 = bluetoothSocket;
            if (bluetoothSocket2 != null) {
                bluetoothSocket2.close();
            }
            bluetoothSocket = null;
            LogUtils.d("ScanGunManager", "✓ 资源已清理");
        } catch (Exception e) {
            LogUtils.e("ScanGunManager", "清理失败: " + e.getMessage());
        }
    }

    public final void shutdown() {
        try {
            disconnect();
            ExecutorService executorService2 = executorService;
            executorService2.shutdown();
            if (!executorService2.awaitTermination(5L, TimeUnit.SECONDS)) {
                executorService2.shutdownNow();
            }
            LogUtils.d("ScanGunManager", "✓ 线程池已关闭");
        } catch (Exception e) {
            LogUtils.e("ScanGunManager", "关闭线程池失败: " + e.getMessage());
        }
    }

    public final boolean isConnected() {
        return isConnected;
    }

    public final String getConnectionStatus() {
        return isConnected ? "已连接 ✓" : "未连接 ✗";
    }
}
