package HPRTAndroidSDK;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.util.Log;
import com.bhm.ble.BleManager;
import com.bhm.ble.callback.BleConnectCallback;
import com.bhm.ble.callback.BleReadCallback;
import com.bhm.ble.callback.BleWriteCallback;
import com.bhm.ble.data.BleConnectFailType;
import com.bhm.ble.device.BleDevice;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.prt.base.common.ConnectMethod;
import com.prt.print.data.bean.CharacteristicNode;
import com.taobao.weex.ui.component.WXComponent;
import com.umeng.analytics.pro.f;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import utils.BleUtils;

/* JADX INFO: compiled from: BTOperator.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0012\n\u0002\b\b\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b!\b\u0007\u0018\u0000 Y2\u00020\u0001:\u0002XYB\u0013\b\u0016\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u0004\u0010\u0005B\u001b\b\u0016\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\u0004\u0010\bJ\u0010\u0010/\u001a\u0002002\u0006\u00101\u001a\u00020\u001cH\u0016J\b\u00102\u001a\u000200H\u0016J\u0010\u00103\u001a\u0002002\u0006\u00104\u001a\u00020\u001eH\u0016J\u0010\u00105\u001a\u0002002\u0006\u00106\u001a\u00020\u001eH\u0016J\u0010\u00107\u001a\u00020\u001c2\u0006\u00108\u001a\u000209H\u0016J\u0018\u00107\u001a\u00020\u001c2\u0006\u0010:\u001a\u00020\u00072\u0006\u0010;\u001a\u00020\u0007H\u0016J\u0010\u00107\u001a\u00020\u001c2\u0006\u0010:\u001a\u00020\u0007H\u0017J\u0010\u0010<\u001a\u00020\u001e2\u0006\u0010:\u001a\u00020\u0007H\u0016J\b\u0010=\u001a\u00020\u001cH\u0016J\u0010\u0010>\u001a\u00020\u001e2\u0006\u0010?\u001a\u00020'H\u0016J\u0018\u0010>\u001a\u00020\u001e2\u0006\u0010?\u001a\u00020'2\u0006\u0010@\u001a\u00020\u001eH\u0016J \u0010>\u001a\u00020\u001e2\u0006\u0010?\u001a\u00020'2\u0006\u0010A\u001a\u00020\u001e2\u0006\u0010@\u001a\u00020\u001eH\u0016J\u0010\u0010B\u001a\u00020\u001e2\u0006\u0010C\u001a\u00020'H\u0002J\u0010\u0010D\u001a\u00020'2\u0006\u0010E\u001a\u00020\u001eH\u0016J\u0010\u0010F\u001a\u00020'2\u0006\u0010G\u001a\u00020\u001eH\u0016J\b\u0010H\u001a\u00020\u001cH\u0016J\b\u0010I\u001a\u00020\u0007H\u0016J\b\u0010J\u001a\u00020\u0007H\u0016J\b\u0010K\u001a\u00020\u001cH\u0002J\b\u0010L\u001a\u00020\u0007H\u0016J\u0010\u0010M\u001a\u0002002\u0006\u0010.\u001a\u00020\u001cH\u0016J\u0018\u0010N\u001a\u0002002\u0006\u0010O\u001a\u00020\u001e2\u0006\u0010P\u001a\u00020\u001eH\u0016J\b\u0010Q\u001a\u00020\fH\u0016J\b\u0010R\u001a\u00020\u000eH\u0016J\u0010\u0010S\u001a\u00020\u001e2\b\u0010?\u001a\u0004\u0018\u00010'J\b\u0010T\u001a\u00020\u001cH\u0002J\b\u0010U\u001a\u00020\u001eH\u0002J\b\u0010V\u001a\u00020\u001cH\u0002J\b\u0010W\u001a\u00020\u001cH\u0016R\u000e\u0010\t\u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u0012X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0014X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0007X\u0082D¢\u0006\u0002\n\u0000R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u001cX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u001eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020\u001cX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010 \u001a\u0004\u0018\u00010!X\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\"\u001a\b\u0018\u00010#R\u00020\u0000X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020\u001eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010%\u001a\u00020\u001cX\u0082\u000e¢\u0006\u0002\n\u0000R\u001c\u0010&\u001a\u0004\u0018\u00010'X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b(\u0010)\"\u0004\b*\u0010+R\u000e\u0010,\u001a\u00020\u001eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010-\u001a\u00020\u001eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010.\u001a\u00020\u001cX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006Z"}, d2 = {"LHPRTAndroidSDK/BTOperator;", "LHPRTAndroidSDK/BaseOperator;", f.X, "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "strPrinterName", "", "(Landroid/content/Context;Ljava/lang/String;)V", "mBluetoothAdapter", "Landroid/bluetooth/BluetoothAdapter;", "mmInStream", "Ljava/io/InputStream;", "mmOutStream", "Ljava/io/OutputStream;", "mmSocket", "Landroid/bluetooth/BluetoothSocket;", "mmDevice", "Landroid/bluetooth/BluetoothDevice;", "bleDevice", "Lcom/bhm/ble/device/BleDevice;", "serviceUUID", "characteristicUUID", "serviceNotifyUUID", "characteristicNotifyUUID", "ACTION_PAIRING_REQUEST", "PreContext", "blnOpenPort", "", "IsReConnect", "", "Is_BLE_Type", "timing1", "Ljava/lang/Thread;", "readerthread", "LHPRTAndroidSDK/BTOperator$Readerthread;", "readDataN", "Isokread", "readData1", "", "getReadData1", "()[B", "setReadData1", "([B)V", "n", WXComponent.PROP_FS_MATCH_PARENT, "isFrist", "IsBLEType", "", "isBLEType", "InitPort", "SetReadTimeout", "readTimeout", "SetWriteTimeout", "writeTimeout", "OpenPort", "usbdevice", "Landroid/hardware/usb/UsbDevice;", "PortParam", "PortNumber", "OpenPortTest", "ClosePort", "WriteData", "Data", "intDataLength", "intOffset", "writeData", "data", "ReadData", "second", "ReadDataMillisecond", "millisecond", "IsOpen", "GetPortType", "GetPrinterName", "GetIOInterface", "GetPrinterModel", "setIsFirst", "setKey", "nKey", "mKey", "getInputStream", "getOutputStream", "Readdata", "ChackHands", "ChackHandsTest", "CheckPrinter", "reConnect", "Readerthread", "Companion", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class BTOperator extends BaseOperator {

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static String InPrinterName;
    private static final UUID MY_UUID;
    private static final UUID MY_UUID2;
    private static String PrinterName;
    private static int bluetooth;
    private static String bluetoothAddress;
    private static boolean isShake;
    private final String ACTION_PAIRING_REQUEST;
    private int IsReConnect;
    private boolean Is_BLE_Type;
    private boolean Isokread;
    private Context PreContext;
    private BleDevice bleDevice;
    private boolean blnOpenPort;
    private String characteristicNotifyUUID;
    private String characteristicUUID;
    private boolean isFrist;
    private int m;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mmDevice;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private BluetoothSocket mmSocket;
    private int n;
    private byte[] readData1;
    private int readDataN;
    private Readerthread readerthread;
    private String serviceNotifyUUID;
    private String serviceUUID;
    private Thread timing1;

    @Override // HPRTAndroidSDK.IPort
    public void InitPort() {
    }

    @Override // HPRTAndroidSDK.IPort
    public boolean OpenPort(UsbDevice usbdevice) {
        Intrinsics.checkNotNullParameter(usbdevice, "usbdevice");
        return false;
    }

    @Override // HPRTAndroidSDK.IPort
    public boolean OpenPort(String PortParam, String PortNumber) {
        Intrinsics.checkNotNullParameter(PortParam, "PortParam");
        Intrinsics.checkNotNullParameter(PortNumber, "PortNumber");
        return false;
    }

    @Override // HPRTAndroidSDK.IPort
    public int OpenPortTest(String PortParam) {
        Intrinsics.checkNotNullParameter(PortParam, "PortParam");
        return -1;
    }

    @Override // HPRTAndroidSDK.IPort
    public void SetReadTimeout(int readTimeout) {
    }

    @Override // HPRTAndroidSDK.IPort
    public void SetWriteTimeout(int writeTimeout) {
    }

    public final byte[] getReadData1() {
        return this.readData1;
    }

    public final void setReadData1(byte[] bArr) {
        this.readData1 = bArr;
    }

    public BTOperator(Context context) {
        this.serviceUUID = "";
        this.characteristicUUID = "";
        this.serviceNotifyUUID = "";
        this.characteristicNotifyUUID = "";
        this.ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";
        this.PreContext = context;
        InPrinterName = "HPRT";
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        Intrinsics.checkNotNullExpressionValue(defaultAdapter, "getDefaultAdapter(...)");
        this.mBluetoothAdapter = defaultAdapter;
    }

    public BTOperator(Context context, String strPrinterName) {
        Intrinsics.checkNotNullParameter(strPrinterName, "strPrinterName");
        this.serviceUUID = "";
        this.characteristicUUID = "";
        this.serviceNotifyUUID = "";
        this.characteristicNotifyUUID = "";
        this.ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";
        this.PreContext = context;
        PrinterName = strPrinterName;
        InPrinterName = strPrinterName;
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        Intrinsics.checkNotNullExpressionValue(defaultAdapter, "getDefaultAdapter(...)");
        this.mBluetoothAdapter = defaultAdapter;
    }

    @Override // HPRTAndroidSDK.IPort
    public void IsBLEType(boolean isBLEType) {
        this.Is_BLE_Type = isBLEType;
    }

    @Override // HPRTAndroidSDK.IPort
    public boolean OpenPort(String PortParam) {
        Class<?> cls;
        Intrinsics.checkNotNullParameter(PortParam, "PortParam");
        if (this.mBluetoothAdapter.isDiscovering()) {
            this.mBluetoothAdapter.cancelDiscovery();
        }
        while (this.mBluetoothAdapter.isDiscovering()) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        bluetoothAddress = PortParam;
        if (PortParam == null) {
            return false;
        }
        if (!StringsKt.contains$default((CharSequence) PortParam, (CharSequence) ":", false, 2, (Object) null) || bluetoothAddress.length() != 17) {
            return false;
        }
        try {
            try {
            } catch (Exception e2) {
                Log.d("PRTLIB", "BTO_ConnectDevice --> create " + e2.getMessage());
                return false;
            }
        } catch (Exception unused) {
            BluetoothDevice bluetoothDevice = this.mmDevice;
            Method method = (bluetoothDevice == null || (cls = bluetoothDevice.getClass()) == null) ? null : cls.getMethod("createRfcommSocket", Integer.TYPE);
            Object objInvoke = method != null ? method.invoke(this.mmDevice, 1) : null;
            Intrinsics.checkNotNull(objInvoke, "null cannot be cast to non-null type android.bluetooth.BluetoothSocket");
            this.mmSocket = (BluetoothSocket) objInvoke;
            if (this.mBluetoothAdapter.isDiscovering()) {
                int i = 0;
                while (i < 5) {
                    Thread.sleep(100L);
                    i++;
                    if (this.mBluetoothAdapter.cancelDiscovery()) {
                        break;
                    }
                }
            }
            BluetoothSocket bluetoothSocket = this.mmSocket;
            if (bluetoothSocket != null) {
                bluetoothSocket.connect();
                Unit unit = Unit.INSTANCE;
            }
        }
        if (this.Is_BLE_Type) {
            Ref.BooleanRef booleanRef = new Ref.BooleanRef();
            Ref.BooleanRef booleanRef2 = new Ref.BooleanRef();
            BleManager.INSTANCE.get().connect(BleManager.INSTANCE.get().buildBleDeviceByDeviceAddress(bluetoothAddress), false, (Function1<? super BleConnectCallback, Unit>) new BTOperator$$ExternalSyntheticLambda1(this, booleanRef, booleanRef2));
            while (!booleanRef.element) {
                Thread.sleep(50L);
            }
            return booleanRef2.element;
        }
        BluetoothDevice remoteDevice = this.mBluetoothAdapter.getRemoteDevice(bluetoothAddress);
        this.mmDevice = remoteDevice;
        this.mmSocket = remoteDevice != null ? remoteDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID) : null;
        this.mBluetoothAdapter.cancelDiscovery();
        if (this.mBluetoothAdapter.isDiscovering()) {
            int i2 = 0;
            while (i2 < 5) {
                Thread.sleep(100L);
                i2++;
                if (this.mBluetoothAdapter.cancelDiscovery()) {
                    break;
                }
            }
        }
        BluetoothSocket bluetoothSocket2 = this.mmSocket;
        if (bluetoothSocket2 != null) {
            bluetoothSocket2.connect();
        }
        try {
            BluetoothDevice bluetoothDevice2 = this.mmDevice;
            PrinterName = String.valueOf(bluetoothDevice2 != null ? bluetoothDevice2.getName() : null);
            boolean zGetIOInterface = GetIOInterface();
            this.blnOpenPort = zGetIOInterface;
            return zGetIOInterface;
        } catch (Exception e3) {
            Log.d("PRTLIB", "BTO_ConnectDevice --> error " + e3.getMessage());
            e3.printStackTrace();
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit OpenPort$lambda$5(BTOperator bTOperator, Ref.BooleanRef booleanRef, final Ref.BooleanRef booleanRef2, BleConnectCallback connect) {
        Intrinsics.checkNotNullParameter(connect, "$this$connect");
        connect.onConnectSuccess(new BTOperator$$ExternalSyntheticLambda8(bTOperator, booleanRef, booleanRef2));
        connect.onDisConnecting(new BTOperator$$ExternalSyntheticLambda9(booleanRef2));
        connect.onConnectFail(new Function2() { // from class: HPRTAndroidSDK.BTOperator$$ExternalSyntheticLambda10
            @Override // kotlin.jvm.functions.Function2
            public final Object invoke(Object obj, Object obj2) {
                return BTOperator.OpenPort$lambda$5$lambda$4(booleanRef2, (BleDevice) obj, (BleConnectFailType) obj2);
            }
        });
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit OpenPort$lambda$5$lambda$2(BTOperator bTOperator, Ref.BooleanRef booleanRef, Ref.BooleanRef booleanRef2, BleDevice bleDevice, BluetoothGatt bluetoothGatt) {
        Intrinsics.checkNotNullParameter(bleDevice, "bleDevice");
        HPRTPrinterHelper.INSTANCE.logcat("OpenPort: 连接成功");
        bTOperator.bleDevice = bleDevice;
        Iterator<T> it2 = BleUtils.INSTANCE.getListData(bleDevice).iterator();
        while (it2.hasNext()) {
            List<BaseNode> childNode = ((BaseNode) it2.next()).getChildNode();
            if (childNode != null) {
                for (BaseNode baseNode : childNode) {
                    Intrinsics.checkNotNull(baseNode, "null cannot be cast to non-null type com.prt.print.data.bean.CharacteristicNode");
                    CharacteristicNode characteristicNode = (CharacteristicNode) baseNode;
                    boolean enableWrite = characteristicNode.getEnableWrite();
                    boolean enableRead = characteristicNode.getEnableRead();
                    if (enableWrite && bTOperator.serviceUUID.length() == 0 && bTOperator.characteristicUUID.length() == 0) {
                        bTOperator.serviceUUID = characteristicNode.getServiceUUID();
                        bTOperator.characteristicUUID = characteristicNode.getCharacteristicUUID();
                    }
                    if (enableRead && bTOperator.serviceNotifyUUID.length() == 0 && bTOperator.characteristicNotifyUUID.length() == 0) {
                        bTOperator.serviceNotifyUUID = characteristicNode.getServiceUUID();
                        bTOperator.characteristicNotifyUUID = characteristicNode.getCharacteristicUUID();
                    }
                }
            }
        }
        booleanRef.element = true;
        booleanRef2.element = true;
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit OpenPort$lambda$5$lambda$3(Ref.BooleanRef booleanRef, boolean z, BleDevice bleDevice, BluetoothGatt bluetoothGatt, int i) {
        Intrinsics.checkNotNullParameter(bleDevice, "bleDevice");
        HPRTPrinterHelper.INSTANCE.logcat("OpenPort: 正在断开");
        booleanRef.element = false;
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit OpenPort$lambda$5$lambda$4(Ref.BooleanRef booleanRef, BleDevice bleDevice, BleConnectFailType connectFailType) {
        Intrinsics.checkNotNullParameter(bleDevice, "bleDevice");
        Intrinsics.checkNotNullParameter(connectFailType, "connectFailType");
        HPRTPrinterHelper.INSTANCE.logcat("OpenPort: 连接失败");
        booleanRef.element = false;
        return Unit.INSTANCE;
    }

    @Override // HPRTAndroidSDK.IPort
    public boolean ClosePort() {
        try {
            InputStream inputStream = this.mmInStream;
            if (inputStream != null) {
                if (inputStream != null) {
                    inputStream.close();
                }
                this.mmInStream = null;
            }
            OutputStream outputStream = this.mmOutStream;
            if (outputStream != null) {
                if (outputStream != null) {
                    outputStream.close();
                }
                this.mmOutStream = null;
            }
            BluetoothSocket bluetoothSocket = this.mmSocket;
            if (bluetoothSocket != null && bluetoothSocket != null && bluetoothSocket.isConnected()) {
                BluetoothSocket bluetoothSocket2 = this.mmSocket;
                Intrinsics.checkNotNull(bluetoothSocket2);
                bluetoothSocket2.close();
                this.mmSocket = null;
            }
            return true;
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder("BTO_ConnectDevice close ");
            sb.append(e.getMessage());
            System.out.println(sb);
            return false;
        }
    }

    @Override // HPRTAndroidSDK.IPort
    public int WriteData(byte[] Data) {
        Intrinsics.checkNotNullParameter(Data, "Data");
        HPRTPrinterHelper.INSTANCE.logcat("------------------------------------------------");
        HPRTPrinterHelper.INSTANCE.logcat("指令:" + Tools.byteToHexWithEmpty(Data));
        HPRTPrinterHelper.INSTANCE.logcatI("指令:".concat(new String(Data, Charsets.UTF_8)));
        return WriteData(Data, 0, Data.length);
    }

    @Override // HPRTAndroidSDK.IPort
    public int WriteData(byte[] Data, int intDataLength) {
        Intrinsics.checkNotNullParameter(Data, "Data");
        HPRTPrinterHelper.INSTANCE.logcat("指令:" + Tools.byteToHexWithEmpty(Data));
        return WriteData(Data, 0, intDataLength);
    }

    @Override // HPRTAndroidSDK.IPort
    public int WriteData(byte[] Data, int intOffset, int intDataLength) {
        Intrinsics.checkNotNullParameter(Data, "Data");
        try {
            if (this.mmOutStream == null) {
                return -1;
            }
            if (HPRTConst.isShack) {
                if (this.isFrist) {
                    HPRTPrinterHelper.INSTANCE.logcat("不加密");
                    if (writeData(Data) == -1) {
                        return -1;
                    }
                } else {
                    HPRTPrinterHelper.INSTANCE.logcat("加密");
                    byte[] bArr = new byte[Data.length];
                    int length = Data.length;
                    for (int i = 0; i < length; i++) {
                        bArr[i] = (byte) (Data[i] ^ (this.n + this.m));
                    }
                    if (writeData(bArr) == -1) {
                        return -1;
                    }
                }
            } else {
                HPRTPrinterHelper.INSTANCE.logcat("去掉校验");
                if (writeData(Data) == -1) {
                    return -1;
                }
            }
            this.IsReConnect = 0;
            return intDataLength;
        } catch (Exception e) {
            Log.d("PRTLIB", "WriteData --> error " + e.getMessage());
            return -1;
        }
    }

    private final int writeData(byte[] data) {
        byte[] bArr;
        try {
            byte[] bArr2 = new byte[bluetooth];
            int length = data.length;
            if (this.Is_BLE_Type) {
                Ref.BooleanRef booleanRef = new Ref.BooleanRef();
                Ref.BooleanRef booleanRef2 = new Ref.BooleanRef();
                BleDevice bleDevice = this.bleDevice;
                if (bleDevice != null) {
                    BleManager.INSTANCE.get().writeData(bleDevice, this.serviceUUID, this.characteristicUUID, bArr2, new BTOperator$$ExternalSyntheticLambda11(booleanRef, booleanRef2));
                }
                while (!booleanRef2.element) {
                    Thread.sleep(50L);
                }
                if (booleanRef.element) {
                    return data.length;
                }
                return -1;
            }
            while (length > 0) {
                int iMin = (int) Math.min(length, bluetooth);
                System.arraycopy(data, data.length - length, bArr2, 0, iMin);
                OutputStream outputStream = this.mmOutStream;
                Intrinsics.checkNotNull(outputStream);
                outputStream.write(bArr2, 0, iMin);
                OutputStream outputStream2 = this.mmOutStream;
                Intrinsics.checkNotNull(outputStream2);
                outputStream2.flush();
                length -= iMin;
            }
            if (HPRTPrinterHelper.isLog) {
                HPRTPrinterHelper.INSTANCE.logcat("Write:" + Tools.byteToHexWithEmpty(data));
            }
            if (HPRTPrinterHelper.isWriteLog) {
                if (HPRTPrinterHelper.isHex) {
                    String str = "Write:" + Tools.byteToHexWithEmpty(data);
                    Context context = this.PreContext;
                    byte[] bytes = str.getBytes(Charsets.UTF_8);
                    Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
                    LogUlit.writeFileToSDCard(context, bytes, HPRTConst.FOLDER, HPRTConst.FOLDER_NAME, true, true);
                    bArr = data;
                } else {
                    bArr = data;
                    LogUlit.writeFileToSDCard(this.PreContext, bArr, HPRTConst.FOLDER, HPRTConst.FOLDER_NAME, true, true);
                }
                Context context2 = this.PreContext;
                byte[] bytes2 = new String(bArr, Charsets.UTF_8).getBytes(Charsets.UTF_8);
                Intrinsics.checkNotNullExpressionValue(bytes2, "getBytes(...)");
                LogUlit.writeFileToSDCard(context2, bytes2, HPRTConst.FOLDER, HPRTConst.FOLDER_NAME, true, true);
            } else {
                bArr = data;
            }
            return bArr.length;
        } catch (Exception e) {
            String message = e.getMessage();
            Intrinsics.checkNotNull(message);
            StringsKt.contains$default((CharSequence) message, (CharSequence) "EPIPE", false, 2, (Object) null);
            return -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit writeData$lambda$10$lambda$9(final Ref.BooleanRef booleanRef, Ref.BooleanRef booleanRef2, BleWriteCallback writeData) {
        Intrinsics.checkNotNullParameter(writeData, "$this$writeData");
        writeData.onWriteSuccess(new BTOperator$$ExternalSyntheticLambda0(booleanRef));
        writeData.onWriteComplete(new BTOperator$$ExternalSyntheticLambda5(booleanRef2));
        writeData.onWriteFail(new Function4() { // from class: HPRTAndroidSDK.BTOperator$$ExternalSyntheticLambda6
            @Override // kotlin.jvm.functions.Function4
            public final Object invoke(Object obj, Object obj2, Object obj3, Object obj4) {
                return BTOperator.writeData$lambda$10$lambda$9$lambda$8(booleanRef, (BleDevice) obj, ((Integer) obj2).intValue(), ((Integer) obj3).intValue(), (Throwable) obj4);
            }
        });
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit writeData$lambda$10$lambda$9$lambda$6(Ref.BooleanRef booleanRef, BleDevice bleDevice, int i, int i2, byte[] justWrite) {
        Intrinsics.checkNotNullParameter(bleDevice, "<unused var>");
        Intrinsics.checkNotNullParameter(justWrite, "justWrite");
        HPRTPrinterHelper.INSTANCE.logcat("Write: " + justWrite);
        booleanRef.element = true;
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit writeData$lambda$10$lambda$9$lambda$7(Ref.BooleanRef booleanRef, BleDevice bleDevice, boolean z) {
        Intrinsics.checkNotNullParameter(bleDevice, "bleDevice");
        HPRTPrinterHelper.INSTANCE.logcat("Write: " + z);
        booleanRef.element = true;
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit writeData$lambda$10$lambda$9$lambda$8(Ref.BooleanRef booleanRef, BleDevice bleDevice, int i, int i2, Throwable throwable) {
        Intrinsics.checkNotNullParameter(bleDevice, "bleDevice");
        Intrinsics.checkNotNullParameter(throwable, "throwable");
        HPRTPrinterHelper.INSTANCE.logcat("Write: " + throwable);
        booleanRef.element = false;
        return Unit.INSTANCE;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v11, types: [T, byte[]] */
    /* JADX WARN: Type inference failed for: r3v0, types: [T, byte[]] */
    @Override // HPRTAndroidSDK.IPort
    public byte[] ReadData(int second) throws InterruptedException {
        Ref.ObjectRef objectRef = new Ref.ObjectRef();
        int i = 0;
        objectRef.element = new byte[0];
        if (this.Is_BLE_Type) {
            BleDevice bleDevice = this.bleDevice;
            if (bleDevice != null) {
                Ref.BooleanRef booleanRef = new Ref.BooleanRef();
                BleManager.INSTANCE.get().readData(bleDevice, this.serviceNotifyUUID, this.characteristicNotifyUUID, new BTOperator$$ExternalSyntheticLambda7(booleanRef, objectRef));
                while (!booleanRef.element) {
                    Thread.sleep(50L);
                }
                return (byte[]) objectRef.element;
            }
            return (byte[]) objectRef.element;
        }
        if (this.mmInStream == null) {
            return (byte[]) objectRef.element;
        }
        if (this.IsReConnect >= 2) {
            return (byte[]) objectRef.element;
        }
        while (true) {
            int i2 = second * 10;
            if (i >= i2) {
                break;
            }
            try {
                InputStream inputStream = this.mmInStream;
                if (inputStream == null) {
                    return (byte[]) objectRef.element;
                }
                Intrinsics.checkNotNull(inputStream);
                int iAvailable = inputStream.available();
                if (iAvailable > 0) {
                    objectRef.element = new byte[iAvailable];
                    InputStream inputStream2 = this.mmInStream;
                    Intrinsics.checkNotNull(inputStream2);
                    inputStream2.read((byte[]) objectRef.element);
                    if (HPRTPrinterHelper.isLog) {
                        HPRTPrinterHelper.INSTANCE.logcat("Read:" + Tools.byteToHexWithEmpty((byte[]) objectRef.element));
                        String str = new String((byte[]) objectRef.element, Charsets.UTF_8);
                        HPRTPrinterHelper.INSTANCE.logcat("Read:" + str);
                    }
                    if (HPRTPrinterHelper.isWriteLog) {
                        if (HPRTPrinterHelper.isHex) {
                            String str2 = "Read:" + Tools.byteToHexWithEmpty((byte[]) objectRef.element);
                            Context context = this.PreContext;
                            byte[] bytes = str2.getBytes(Charsets.UTF_8);
                            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
                            LogUlit.writeFileToSDCard(context, bytes, HPRTConst.FOLDER, HPRTConst.FOLDER_NAME, true, true);
                        } else {
                            LogUlit.writeFileToSDCard(this.PreContext, (byte[]) objectRef.element, HPRTConst.FOLDER, HPRTConst.FOLDER_NAME, true, true);
                        }
                        Context context2 = this.PreContext;
                        byte[] bytes2 = new String((byte[]) objectRef.element, Charsets.UTF_8).getBytes(Charsets.UTF_8);
                        Intrinsics.checkNotNullExpressionValue(bytes2, "getBytes(...)");
                        LogUlit.writeFileToSDCard(context2, bytes2, HPRTConst.FOLDER, HPRTConst.FOLDER_NAME, true, true);
                    }
                    i = i2 + 1;
                } else {
                    Thread.sleep(100L);
                    i++;
                }
            } catch (IOException unused) {
                return (byte[]) objectRef.element;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return (byte[]) objectRef.element;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit ReadData$lambda$14$lambda$13(final Ref.BooleanRef booleanRef, Ref.ObjectRef objectRef, BleReadCallback readData) {
        Intrinsics.checkNotNullParameter(readData, "$this$readData");
        readData.onReadSuccess(new BTOperator$$ExternalSyntheticLambda2(booleanRef, objectRef));
        readData.onReadFail(new Function2() { // from class: HPRTAndroidSDK.BTOperator$$ExternalSyntheticLambda3
            @Override // kotlin.jvm.functions.Function2
            public final Object invoke(Object obj, Object obj2) {
                return BTOperator.ReadData$lambda$14$lambda$13$lambda$12(booleanRef, (BleDevice) obj, (Throwable) obj2);
            }
        });
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public static final Unit ReadData$lambda$14$lambda$13$lambda$11(Ref.BooleanRef booleanRef, Ref.ObjectRef objectRef, BleDevice bleDevice, byte[] data) {
        Intrinsics.checkNotNullParameter(bleDevice, "bleDevice");
        Intrinsics.checkNotNullParameter(data, "data");
        booleanRef.element = true;
        objectRef.element = data;
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit ReadData$lambda$14$lambda$13$lambda$12(Ref.BooleanRef booleanRef, BleDevice bleDevice, Throwable throwable) {
        Intrinsics.checkNotNullParameter(bleDevice, "bleDevice");
        Intrinsics.checkNotNullParameter(throwable, "throwable");
        booleanRef.element = true;
        HPRTPrinterHelper.INSTANCE.logcat("Read: " + throwable);
        return Unit.INSTANCE;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v0, types: [T, byte[]] */
    /* JADX WARN: Type inference failed for: r4v6, types: [T, byte[]] */
    @Override // HPRTAndroidSDK.IPort
    public byte[] ReadDataMillisecond(int millisecond) throws InterruptedException {
        Ref.ObjectRef objectRef = new Ref.ObjectRef();
        objectRef.element = new byte[0];
        if (this.Is_BLE_Type) {
            BleDevice bleDevice = this.bleDevice;
            if (bleDevice != null) {
                Ref.BooleanRef booleanRef = new Ref.BooleanRef();
                BleManager.INSTANCE.get().readData(bleDevice, this.serviceNotifyUUID, this.characteristicNotifyUUID, new BTOperator$$ExternalSyntheticLambda4(booleanRef, objectRef));
                while (!booleanRef.element) {
                    Thread.sleep(50L);
                }
                return (byte[]) objectRef.element;
            }
            return (byte[]) objectRef.element;
        }
        if (this.mmInStream == null) {
            return (byte[]) objectRef.element;
        }
        if (this.IsReConnect >= 2) {
            return (byte[]) objectRef.element;
        }
        int i = 0;
        while (i < millisecond) {
            try {
                InputStream inputStream = this.mmInStream;
                if (inputStream == null) {
                    return (byte[]) objectRef.element;
                }
                int iAvailable = inputStream != null ? inputStream.available() : 0;
                if (iAvailable > 0) {
                    objectRef.element = new byte[iAvailable];
                    InputStream inputStream2 = this.mmInStream;
                    if (inputStream2 != null) {
                        inputStream2.read((byte[]) objectRef.element);
                    }
                    if (HPRTPrinterHelper.isLog) {
                        HPRTPrinterHelper.INSTANCE.logcat("Read:" + Tools.byteToHexWithEmpty((byte[]) objectRef.element));
                        String str = new String((byte[]) objectRef.element, Charsets.UTF_8);
                        HPRTPrinterHelper.INSTANCE.logcatE("Read:" + str);
                    }
                    if (HPRTPrinterHelper.isWriteLog) {
                        if (HPRTPrinterHelper.isHex) {
                            String str2 = "Read:" + Tools.byteToHexWithEmpty((byte[]) objectRef.element);
                            Context context = this.PreContext;
                            byte[] bytes = str2.getBytes(Charsets.UTF_8);
                            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
                            LogUlit.writeFileToSDCard(context, bytes, HPRTConst.FOLDER, HPRTConst.FOLDER_NAME, true, true);
                        } else {
                            LogUlit.writeFileToSDCard(this.PreContext, (byte[]) objectRef.element, HPRTConst.FOLDER, HPRTConst.FOLDER_NAME, true, true);
                        }
                        Context context2 = this.PreContext;
                        byte[] bytes2 = new String((byte[]) objectRef.element, Charsets.UTF_8).getBytes(Charsets.UTF_8);
                        Intrinsics.checkNotNullExpressionValue(bytes2, "getBytes(...)");
                        LogUlit.writeFileToSDCard(context2, bytes2, HPRTConst.FOLDER, HPRTConst.FOLDER_NAME, true, true);
                    }
                    i = millisecond + 1;
                } else if (millisecond / 10 == 0) {
                    Thread.sleep(1L);
                    i++;
                } else {
                    Thread.sleep(millisecond / 10);
                    i += millisecond / 10;
                }
            } catch (IOException unused) {
                return (byte[]) objectRef.element;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return (byte[]) objectRef.element;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit ReadDataMillisecond$lambda$18$lambda$17(final Ref.BooleanRef booleanRef, Ref.ObjectRef objectRef, BleReadCallback readData) {
        Intrinsics.checkNotNullParameter(readData, "$this$readData");
        readData.onReadSuccess(new BTOperator$$ExternalSyntheticLambda12(booleanRef, objectRef));
        readData.onReadFail(new Function2() { // from class: HPRTAndroidSDK.BTOperator$$ExternalSyntheticLambda13
            @Override // kotlin.jvm.functions.Function2
            public final Object invoke(Object obj, Object obj2) {
                return BTOperator.ReadDataMillisecond$lambda$18$lambda$17$lambda$16(booleanRef, (BleDevice) obj, (Throwable) obj2);
            }
        });
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public static final Unit ReadDataMillisecond$lambda$18$lambda$17$lambda$15(Ref.BooleanRef booleanRef, Ref.ObjectRef objectRef, BleDevice bleDevice, byte[] data) {
        Intrinsics.checkNotNullParameter(bleDevice, "<unused var>");
        Intrinsics.checkNotNullParameter(data, "data");
        booleanRef.element = true;
        objectRef.element = data;
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit ReadDataMillisecond$lambda$18$lambda$17$lambda$16(Ref.BooleanRef booleanRef, BleDevice bleDevice, Throwable throwable) {
        Intrinsics.checkNotNullParameter(bleDevice, "<unused var>");
        Intrinsics.checkNotNullParameter(throwable, "throwable");
        booleanRef.element = true;
        HPRTPrinterHelper.INSTANCE.logcat("Read: " + throwable);
        return Unit.INSTANCE;
    }

    @Override // HPRTAndroidSDK.IPort
    /* JADX INFO: renamed from: IsOpen, reason: from getter */
    public boolean getBlnOpenPort() {
        return this.blnOpenPort;
    }

    @Override // HPRTAndroidSDK.IPort
    public String GetPortType() {
        return "Bluetooth";
    }

    @Override // HPRTAndroidSDK.IPort
    public String GetPrinterName() {
        return PrinterName;
    }

    private final boolean GetIOInterface() {
        Log.d("PRTLIB", "BTO_GetIOInterface...");
        try {
            BluetoothSocket bluetoothSocket = this.mmSocket;
            this.mmInStream = bluetoothSocket != null ? bluetoothSocket.getInputStream() : null;
            BluetoothSocket bluetoothSocket2 = this.mmSocket;
            this.mmOutStream = bluetoothSocket2 != null ? bluetoothSocket2.getOutputStream() : null;
            return true;
        } catch (IOException e) {
            Log.d("PRTLIB", "BTO_GetIOInterface " + e.getMessage());
            return false;
        }
    }

    @Override // HPRTAndroidSDK.IPort
    public String GetPrinterModel() {
        return PrinterName;
    }

    @Override // HPRTAndroidSDK.IPort
    public void setIsFirst(boolean isFrist) {
        this.isFrist = isFrist;
    }

    @Override // HPRTAndroidSDK.IPort
    public void setKey(int nKey, int mKey) {
        this.n = nKey;
        this.m = mKey;
    }

    @Override // HPRTAndroidSDK.IPort
    /* JADX INFO: renamed from: getInputStream */
    public InputStream getMmInStream() {
        InputStream inputStream = this.mmInStream;
        Intrinsics.checkNotNull(inputStream);
        return inputStream;
    }

    @Override // HPRTAndroidSDK.IPort
    /* JADX INFO: renamed from: getOutputStream */
    public OutputStream getMmOutStream() {
        OutputStream outputStream = this.mmOutStream;
        Intrinsics.checkNotNull(outputStream);
        return outputStream;
    }

    /* JADX INFO: compiled from: BTOperator.kt */
    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\u0006\u001a\u00020\u0007H\u0016¨\u0006\b"}, d2 = {"LHPRTAndroidSDK/BTOperator$Readerthread;", "Ljava/lang/Thread;", "Data", "", "<init>", "(LHPRTAndroidSDK/BTOperator;[B)V", "run", "", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public final class Readerthread extends Thread {
        final /* synthetic */ BTOperator this$0;

        public Readerthread(BTOperator bTOperator, byte[] Data) {
            Intrinsics.checkNotNullParameter(Data, "Data");
            this.this$0 = bTOperator;
            bTOperator.setReadData1(Data);
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            BTOperator bTOperator = this.this$0;
            final BTOperator bTOperator2 = this.this$0;
            bTOperator.timing1 = new Thread() { // from class: HPRTAndroidSDK.BTOperator$Readerthread$run$1
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    for (int i = 0; i < 2; i++) {
                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException unused) {
                            bTOperator2.readDataN = -1;
                            bTOperator2.Isokread = false;
                            return;
                        }
                    }
                    bTOperator2.readDataN = -1;
                    bTOperator2.Isokread = false;
                }
            };
            Thread thread = this.this$0.timing1;
            if (thread != null) {
                thread.start();
            }
            try {
                BTOperator bTOperator3 = this.this$0;
                InputStream inputStream = bTOperator3.mmInStream;
                Intrinsics.checkNotNull(inputStream);
                bTOperator3.readDataN = inputStream.read(this.this$0.getReadData1());
                this.this$0.Isokread = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public final int Readdata(byte[] Data) {
        Readerthread readerthread;
        this.Isokread = true;
        this.readDataN = 0;
        if (Data == null) {
            Data = new byte[0];
        }
        Readerthread readerthread2 = new Readerthread(this, Data);
        this.readerthread = readerthread2;
        Intrinsics.checkNotNull(readerthread2);
        readerthread2.start();
        while (true) {
            boolean z = this.Isokread;
            if (!z) {
                return this.readDataN;
            }
            if (!z && (readerthread = this.readerthread) != null) {
                Intrinsics.checkNotNull(readerthread);
                this.readerthread = null;
                readerthread.interrupt();
                Thread thread = this.timing1;
                this.timing1 = null;
                Intrinsics.checkNotNull(thread);
                thread.interrupt();
            }
        }
    }

    private final boolean ChackHands() throws InterruptedException {
        this.n = new Random().nextInt(100) + 1;
        int iNextInt = new Random().nextInt(100) + 1;
        this.m = iNextInt;
        this.isFrist = true;
        if (WriteData(new byte[]{27, 28, 115, 101, 116, 32, 109, 109, (byte) this.n, (byte) iNextInt}) == -1) {
            return false;
        }
        byte[] bArrReadData = ReadData(3);
        if (bArrReadData.length != 2) {
            if (WriteData(new byte[]{27, 28, 115, 101, 116, 32, 109, 109, (byte) this.n, (byte) this.m}) == -1) {
                return false;
            }
            bArrReadData = ReadData(3);
            if (bArrReadData.length != 2) {
                this.isFrist = false;
                return false;
            }
        }
        HPRTPrinterHelper.INSTANCE.logcat("成功：" + Tools.byteToHex(bArrReadData));
        byte b = bArrReadData[0];
        if (b == 79 && bArrReadData[1] == 75) {
            this.isFrist = false;
            return true;
        }
        if (b == 78 && bArrReadData[1] == 71) {
            return CheckPrinter();
        }
        return false;
    }

    private final int ChackHandsTest() throws InterruptedException {
        this.n = new Random().nextInt(100) + 1;
        int iNextInt = new Random().nextInt(100) + 1;
        this.m = iNextInt;
        this.isFrist = true;
        if (WriteData(new byte[]{27, 28, 115, 101, 116, 32, 109, 109, (byte) this.n, (byte) iNextInt}) == -1) {
            return -3;
        }
        byte[] bArrReadData = ReadData(3);
        if (bArrReadData == null || bArrReadData.length != 2) {
            if (WriteData(new byte[]{27, 28, 115, 101, 116, 32, 109, 109, (byte) this.n, (byte) this.m}) == -1) {
                return -3;
            }
            bArrReadData = ReadData(3);
            if (bArrReadData == null || bArrReadData.length != 2) {
                this.isFrist = false;
                return -4;
            }
        }
        HPRTPrinterHelper.INSTANCE.logcat("成功：" + Tools.byteToHex(bArrReadData));
        byte b = bArrReadData[0];
        if (b == 79 && bArrReadData[1] == 75) {
            this.isFrist = false;
            return 0;
        }
        if (b == 78 && bArrReadData[1] == 71) {
            return CheckPrinter() ? 0 : -6;
        }
        return -5;
    }

    private final boolean CheckPrinter() throws InterruptedException {
        Log.d("PRTLIB", "CheckPrinter...");
        byte[] bArr = new byte[16];
        byte[] bArr2 = new byte[19];
        String strByteToHex = Tools.byteToHex(bArr2);
        HPRTPrinterHelper.INSTANCE.logcat("MD5Rand:" + strByteToHex);
        HPRTPrinterHelper.INSTANCE.logcat("MD5Return:" + Tools.byteToHex(bArr));
        if (WriteData(bArr2) > 0) {
            byte[] bArrReadData = ReadData(2);
            HPRTPrinterHelper.INSTANCE.logcat("PrinterReturn:" + Tools.byteToHex(bArrReadData));
            if (bArrReadData.length == 0) {
                if (WriteData(bArr2) <= 0) {
                    return false;
                }
                bArrReadData = ReadData(2);
                if (bArrReadData.length == 0) {
                    return false;
                }
            }
            String strByteToHex2 = Tools.byteToHex(bArrReadData);
            Intrinsics.checkNotNullExpressionValue(strByteToHex2, "byteToHex(...)");
            String strByteToHex3 = Tools.byteToHex(bArr);
            Intrinsics.checkNotNullExpressionValue(strByteToHex3, "byteToHex(...)");
            if (!StringsKt.contains$default((CharSequence) strByteToHex2, (CharSequence) strByteToHex3, false, 2, (Object) null)) {
                Log.d("PRTLIB", "CheckPrinterNot Right Printer." + Tools.byteToHex(bArrReadData));
                return false;
            }
            Log.d("PRTLIB", "CheckPrinterRight Printer succeed.");
            return true;
        }
        Log.d("PRTLIB", "CheckPrinterNot Right Printer.Write Error!");
        return false;
    }

    @Override // HPRTAndroidSDK.BaseOperator, HPRTAndroidSDK.IPort
    public boolean reConnect() {
        ClosePort();
        return OpenPort(bluetoothAddress);
    }

    /* JADX INFO: compiled from: BTOperator.kt */
    @Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010\u000b\u001a\u00020\fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\r\"\u0004\b\u000e\u0010\u000fR\u001a\u0010\u0010\u001a\u00020\u0011X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0015¨\u0006\u0016"}, d2 = {"LHPRTAndroidSDK/BTOperator$Companion;", "", "<init>", "()V", "MY_UUID", "Ljava/util/UUID;", "MY_UUID2", "bluetoothAddress", "", "PrinterName", "InPrinterName", "isShake", "", "()Z", "setShake", "(Z)V", ConnectMethod.BLUETOOTH, "", "getBluetooth", "()I", "setBluetooth", "(I)V", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Companion() {
        }

        public final boolean isShake() {
            return BTOperator.isShake;
        }

        public final void setShake(boolean z) {
            BTOperator.isShake = z;
        }

        public final int getBluetooth() {
            return BTOperator.bluetooth;
        }

        public final void setBluetooth(int i) {
            BTOperator.bluetooth = i;
        }
    }

    static {
        UUID uuidFromString = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        Intrinsics.checkNotNullExpressionValue(uuidFromString, "fromString(...)");
        MY_UUID = uuidFromString;
        UUID uuidFromString2 = UUID.fromString("0000ff13-0000-1000-8000-00805F9B34FB");
        Intrinsics.checkNotNullExpressionValue(uuidFromString2, "fromString(...)");
        MY_UUID2 = uuidFromString2;
        bluetoothAddress = "";
        PrinterName = "";
        InPrinterName = "";
        isShake = true;
        bluetooth = 1024;
    }
}
