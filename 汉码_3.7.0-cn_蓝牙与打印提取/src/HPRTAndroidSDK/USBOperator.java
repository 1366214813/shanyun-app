package HPRTAndroidSDK;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.util.Log;
import android.widget.ArrayAdapter;
import com.alipay.sdk.m.s.e;
import com.huawei.hms.push.AttributionReporter;
import com.prt.base.common.ConnectMethod;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class USBOperator extends BaseOperator {
    private static final String ACTION_USB_PERMISSION = "com.android.example.PRTSDK";
    private static String InPrinterName = "";
    protected static final int LIBUSB_DT_STRING = 3;
    private static String PrinterName = "";
    protected static final int STD_USB_REQUEST_GET_DESCRIPTOR = 6;
    private Context PreContext;
    public Intent intent;
    private PendingIntent mPermissionIntent;
    public ArrayAdapter<String> mUSBDevicesArrayAdapter;
    byte[] readData1;
    private int readDataN;
    private Readerthread readerthread;
    private Thread timing1;
    public static List<String> PrinterList1 = new ArrayList();
    public static List<String> PrinterList2 = new ArrayList();
    private static boolean blnOpenPort = false;
    private UsbManager mUsbManager = null;
    private UsbDevice device = null;
    private UsbDeviceConnection connection = null;
    public int intPermissionState = 0;
    private UsbEndpoint inEndpoint = null;
    private UsbEndpoint outEndpoint = null;
    private int ReadTimeout = 1000;
    private int WriteTimeout = 1000;
    private boolean DoPermissionYet = false;
    private boolean Isokread = false;
    private BroadcastReceiver mUsbReceiver1 = new BroadcastReceiver() { // from class: HPRTAndroidSDK.USBOperator.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (USBOperator.ACTION_USB_PERMISSION.equals(action)) {
                if (USBOperator.this.DoPermissionYet) {
                    return;
                }
                synchronized (this) {
                    USBOperator.this.device = (UsbDevice) intent.getParcelableExtra(e.p);
                    int i = 0;
                    if (intent.getBooleanExtra(AttributionReporter.SYSTEM_PERMISSION, false)) {
                        if (USBOperator.this.device != null) {
                            USBOperator.this.connection = null;
                            USBOperator uSBOperator = USBOperator.this;
                            uSBOperator.connection = uSBOperator.mUsbManager.openDevice(USBOperator.this.device);
                            if (USBOperator.this.connection == null) {
                                USBOperator.blnOpenPort = false;
                                return;
                            }
                            try {
                                UsbInterface usbInterface = USBOperator.this.device.getInterface(0);
                                for (int i2 = 0; i2 < usbInterface.getEndpointCount(); i2++) {
                                    UsbEndpoint endpoint = usbInterface.getEndpoint(i2);
                                    if (7 == usbInterface.getInterfaceClass() && endpoint.getDirection() == 128) {
                                        USBOperator.this.outEndpoint = endpoint;
                                        if (usbInterface.getEndpointCount() == 1) {
                                            USBOperator.this.inEndpoint = endpoint;
                                        }
                                    }
                                    if (7 == usbInterface.getInterfaceClass() && endpoint.getDirection() == 0) {
                                        USBOperator.this.inEndpoint = endpoint;
                                        if (usbInterface.getEndpointCount() == 1) {
                                            USBOperator.this.outEndpoint = endpoint;
                                        }
                                    }
                                }
                                USBOperator uSBOperator2 = USBOperator.this;
                                uSBOperator2.connection = uSBOperator2.mUsbManager.openDevice(USBOperator.this.device);
                                USBOperator.this.connection.claimInterface(usbInterface, true);
                                try {
                                    byte[] bArr = new byte[255];
                                    int iControlTransfer = USBOperator.this.connection.controlTransfer(128, 6, USBOperator.this.connection.getRawDescriptors()[15] | 768, 0, bArr, 255, 0);
                                    if (iControlTransfer > 2) {
                                        byte[] bArr2 = new byte[(iControlTransfer - 2) / 2];
                                        for (int i3 = 2; i3 < iControlTransfer; i3++) {
                                            if (i3 % 2 == 0) {
                                                bArr2[i] = bArr[i3];
                                                i++;
                                            }
                                        }
                                        USBOperator.PrinterName = new String(bArr2, "ASCII");
                                    } else {
                                        USBOperator.PrinterName = new String(bArr, 2, iControlTransfer, "ASCII");
                                    }
                                    USBOperator.PrinterName = USBOperator.PrinterName.trim();
                                    try {
                                        Thread.sleep(100L);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    USBOperator.blnOpenPort = true;
                                    Log.d("PRTLIB", "OpenPort --> connect Check Array Is Wrong!");
                                } catch (UnsupportedEncodingException e2) {
                                    e2.getStackTrace();
                                }
                            } catch (Exception unused) {
                                USBOperator.blnOpenPort = false;
                                return;
                            }
                        }
                    } else {
                        USBOperator.blnOpenPort = false;
                        return;
                    }
                }
            }
            if ("android.hardware.usb.action.USB_DEVICE_DETACHED".equals(action)) {
                USBOperator.this.device = (UsbDevice) intent.getParcelableExtra(e.p);
                if (USBOperator.this.device != null) {
                    USBOperator.this.ClosePort();
                }
            }
        }
    };

    @Override // HPRTAndroidSDK.IPort
    public void InitPort() {
    }

    @Override // HPRTAndroidSDK.IPort
    public void IsBLEType(boolean isBLEType) {
    }

    @Override // HPRTAndroidSDK.IPort
    public boolean OpenPort(String PortParam, String PortNumber) {
        return false;
    }

    @Override // HPRTAndroidSDK.IPort
    public int OpenPortTest(String PortParam) {
        return 0;
    }

    @Override // HPRTAndroidSDK.IPort
    /* JADX INFO: renamed from: getInputStream */
    public InputStream getMmInStream() {
        return null;
    }

    @Override // HPRTAndroidSDK.IPort
    /* JADX INFO: renamed from: getOutputStream */
    public OutputStream getMmOutStream() {
        return null;
    }

    @Override // HPRTAndroidSDK.IPort
    public void setIsFirst(boolean isFrist) {
    }

    @Override // HPRTAndroidSDK.IPort
    public void setKey(int nKey, int mKey) {
    }

    public USBOperator(Context context) {
        this.mPermissionIntent = null;
        this.PreContext = null;
        this.PreContext = context;
        this.mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 67108864);
        IntentFilter intentFilter = new IntentFilter(ACTION_USB_PERMISSION);
        if (Build.VERSION.SDK_INT >= 26) {
            this.PreContext.registerReceiver(this.mUsbReceiver1, intentFilter, 2);
        } else {
            this.PreContext.registerReceiver(this.mUsbReceiver1, intentFilter, 4);
        }
        InPrinterName = "HPRT";
    }

    public USBOperator(Context context, String strPrintName) {
        this.mPermissionIntent = null;
        this.PreContext = null;
        this.PreContext = context;
        this.mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 67108864);
        IntentFilter intentFilter = new IntentFilter(ACTION_USB_PERMISSION);
        if (Build.VERSION.SDK_INT >= 26) {
            this.PreContext.registerReceiver(this.mUsbReceiver1, intentFilter, 2);
        } else {
            this.PreContext.registerReceiver(this.mUsbReceiver1, intentFilter, 4);
        }
        InPrinterName = strPrintName;
    }

    @Override // HPRTAndroidSDK.IPort
    public boolean OpenPort(String PortParam) {
        UsbManager usbManager = (UsbManager) this.PreContext.getSystemService(ConnectMethod.USB);
        this.mUsbManager = usbManager;
        Iterator<UsbDevice> it2 = usbManager.getDeviceList().values().iterator();
        while (true) {
            if (it2.hasNext()) {
                UsbDevice next = it2.next();
                this.device = next;
                int interfaceCount = next.getInterfaceCount();
                for (int i = 0; i < interfaceCount; i++) {
                    if (this.device.getInterface(i).getInterfaceClass() == 7) {
                        this.mUsbManager.requestPermission(this.device, this.mPermissionIntent);
                        blnOpenPort = true;
                        return true;
                    }
                }
            } else {
                blnOpenPort = false;
                return false;
            }
        }
    }

    @Override // HPRTAndroidSDK.IPort
    public boolean OpenPort(UsbDevice USBdevice) {
        try {
            UsbManager usbManager = (UsbManager) this.PreContext.getSystemService(ConnectMethod.USB);
            this.mUsbManager = usbManager;
            this.DoPermissionYet = true;
            if (USBdevice != null) {
                this.connection = null;
                UsbDeviceConnection usbDeviceConnectionOpenDevice = usbManager.openDevice(USBdevice);
                this.connection = usbDeviceConnectionOpenDevice;
                if (usbDeviceConnectionOpenDevice == null) {
                    blnOpenPort = false;
                    return false;
                }
                UsbInterface usbInterface = USBdevice.getInterface(0);
                for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                    UsbEndpoint endpoint = usbInterface.getEndpoint(i);
                    if (7 == usbInterface.getInterfaceClass() && endpoint.getDirection() == 128) {
                        this.outEndpoint = endpoint;
                        if (usbInterface.getEndpointCount() == 1) {
                            this.inEndpoint = endpoint;
                        }
                    }
                    if (7 == usbInterface.getInterfaceClass() && endpoint.getDirection() == 0) {
                        this.inEndpoint = endpoint;
                        if (usbInterface.getEndpointCount() == 1) {
                            this.outEndpoint = endpoint;
                        }
                    }
                }
                UsbDeviceConnection usbDeviceConnectionOpenDevice2 = this.mUsbManager.openDevice(USBdevice);
                this.connection = usbDeviceConnectionOpenDevice2;
                usbDeviceConnectionOpenDevice2.claimInterface(usbInterface, true);
                try {
                    byte[] bArr = new byte[255];
                    int iControlTransfer = this.connection.controlTransfer(128, 6, this.connection.getRawDescriptors()[15] | 768, 0, bArr, 255, 0);
                    if (iControlTransfer > 2) {
                        byte[] bArr2 = new byte[(iControlTransfer - 2) / 2];
                        int i2 = 0;
                        for (int i3 = 2; i3 < iControlTransfer; i3++) {
                            if (i3 % 2 == 0) {
                                bArr2[i2] = bArr[i3];
                                i2++;
                            }
                        }
                        PrinterName = new String(bArr2, "ASCII");
                    } else {
                        PrinterName = new String(bArr, 2, iControlTransfer, "ASCII");
                    }
                    PrinterName = PrinterName.trim();
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    blnOpenPort = true;
                    Log.d("PRTLIB", "OpenPort --> connect Check Array Is Wrong!");
                } catch (UnsupportedEncodingException e2) {
                    e2.getStackTrace();
                }
            } else {
                blnOpenPort = false;
            }
            return blnOpenPort;
        } catch (Exception unused) {
            blnOpenPort = false;
            return false;
        }
    }

    @Override // HPRTAndroidSDK.IPort
    public boolean ClosePort() {
        if (this.device == null) {
            return true;
        }
        this.connection.close();
        this.connection = null;
        this.device = null;
        blnOpenPort = false;
        return true;
    }

    @Override // HPRTAndroidSDK.IPort
    public void SetReadTimeout(int readTimeout) {
        this.ReadTimeout = readTimeout;
    }

    @Override // HPRTAndroidSDK.IPort
    public void SetWriteTimeout(int writeTimeout) {
        this.WriteTimeout = writeTimeout;
    }

    @Override // HPRTAndroidSDK.IPort
    public int WriteData(byte[] Data) {
        return WriteData(Data, 0, Data.length);
    }

    @Override // HPRTAndroidSDK.IPort
    public int WriteData(byte[] Data, int intDataLength) {
        return WriteData(Data, 0, intDataLength);
    }

    @Override // HPRTAndroidSDK.IPort
    public int WriteData(byte[] Data, int intOffset, int intDataLength) {
        int i;
        try {
            byte[] bArr = new byte[10000];
            int i2 = intDataLength / 10000;
            int i3 = 0;
            int i4 = 0;
            while (i3 < i2) {
                int i5 = i3 * 10000;
                while (true) {
                    i = i3 + 1;
                    if (i5 >= i * 10000) {
                        break;
                    }
                    bArr[i5 % 10000] = Data[i5];
                    i5++;
                }
                int iBulkTransfer = this.connection.bulkTransfer(this.inEndpoint, bArr, 10000, this.WriteTimeout);
                if (HPRTPrinterHelper.isWriteLog) {
                    if (HPRTPrinterHelper.isHex) {
                        LogUlit.writeFileToSDCard(this.PreContext, Tools.byteToHex(bArr).getBytes(), HPRTConst.FOLDER, HPRTConst.FOLDER_NAME, true, true);
                    } else {
                        LogUlit.writeFileToSDCard(this.PreContext, bArr, HPRTConst.FOLDER, HPRTConst.FOLDER_NAME, true, true);
                    }
                }
                i3 = i;
                i4 = iBulkTransfer;
            }
            if (intDataLength % 10000 == 0) {
                return i4;
            }
            int i6 = i2 * 10000;
            int length = Data.length - i6;
            byte[] bArr2 = new byte[length];
            for (int i7 = i6; i7 < Data.length; i7++) {
                bArr2[i7 - i6] = Data[i7];
            }
            int iBulkTransfer2 = this.connection.bulkTransfer(this.inEndpoint, bArr2, length, this.WriteTimeout);
            if (HPRTPrinterHelper.isWriteLog) {
                if (HPRTPrinterHelper.isHex) {
                    LogUlit.writeFileToSDCard(this.PreContext, Tools.byteToHex(bArr2).getBytes(), HPRTConst.FOLDER, HPRTConst.FOLDER_NAME, true, true);
                    return iBulkTransfer2;
                }
                LogUlit.writeFileToSDCard(this.PreContext, bArr2, HPRTConst.FOLDER, HPRTConst.FOLDER_NAME, true, true);
            }
            return iBulkTransfer2;
        } catch (Exception e) {
            Log.d("PRTLIB", "WriteData --> error " + e.getMessage());
            return -1;
        }
    }

    @Override // HPRTAndroidSDK.IPort
    /* JADX INFO: renamed from: IsOpen */
    public boolean getBlnOpenPort() {
        return blnOpenPort;
    }

    @Override // HPRTAndroidSDK.IPort
    public String GetPortType() {
        return "USB";
    }

    @Override // HPRTAndroidSDK.IPort
    public String GetPrinterName() {
        return PrinterName;
    }

    @Override // HPRTAndroidSDK.IPort
    public String GetPrinterModel() {
        return PrinterName;
    }

    @Override // HPRTAndroidSDK.IPort
    public byte[] ReadData(int second) {
        byte[] bArr = new byte[64];
        byte[] bArr2 = new byte[0];
        int i = 0;
        while (true) {
            int i2 = second * 10;
            if (i >= i2) {
                break;
            }
            try {
                Thread.sleep(100L);
                i++;
                int iBulkTransfer = this.connection.bulkTransfer(this.outEndpoint, bArr, 64, 1000);
                if (iBulkTransfer > 0) {
                    bArr2 = new byte[iBulkTransfer];
                    for (int i3 = 0; i3 < iBulkTransfer; i3++) {
                        bArr2[i3] = bArr[i3];
                    }
                    i = i2;
                }
            } catch (Exception unused) {
            }
        }
        return bArr2;
    }

    @Override // HPRTAndroidSDK.IPort
    public byte[] ReadDataMillisecond(int millisecond) {
        return new byte[0];
    }

    public class Readerthread extends Thread {
        public Readerthread(byte[] Data) {
            USBOperator.this.readData1 = Data;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            USBOperator.this.timing1 = new Thread() { // from class: HPRTAndroidSDK.USBOperator.Readerthread.1
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    for (int i = 0; i < 2; i++) {
                        try {
                            sleep(1000L);
                        } catch (InterruptedException unused) {
                            USBOperator.this.readDataN = -1;
                            USBOperator.this.Isokread = false;
                            return;
                        }
                    }
                    USBOperator.this.readDataN = -1;
                    USBOperator.this.Isokread = false;
                }
            };
            USBOperator.this.timing1.start();
            try {
                USBOperator uSBOperator = USBOperator.this;
                uSBOperator.readDataN = uSBOperator.connection.bulkTransfer(USBOperator.this.outEndpoint, USBOperator.this.readData1, USBOperator.this.readData1.length, USBOperator.this.ReadTimeout);
                USBOperator.this.Isokread = false;
            } catch (Exception unused) {
                USBOperator.this.readDataN = -1;
                USBOperator.this.Isokread = false;
            }
        }
    }

    public int Readdata(byte[] Data) {
        Readerthread readerthread;
        this.Isokread = true;
        this.readDataN = 0;
        Readerthread readerthread2 = new Readerthread(Data);
        this.readerthread = readerthread2;
        readerthread2.start();
        while (true) {
            boolean z = this.Isokread;
            if (!z) {
                return this.readDataN;
            }
            if (!z && (readerthread = this.readerthread) != null) {
                this.readerthread = null;
                readerthread.interrupt();
                Thread thread = this.timing1;
                this.timing1 = null;
                thread.interrupt();
            }
        }
    }
}
