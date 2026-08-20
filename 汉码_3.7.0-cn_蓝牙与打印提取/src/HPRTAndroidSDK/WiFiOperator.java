package HPRTAndroidSDK;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.util.Log;
import com.taobao.weex.ui.component.WXComponent;
import com.umeng.analytics.pro.f;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;

/* JADX INFO: compiled from: WiFiOperator.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\"\b\u0016\u0018\u0000 O2\u00020\u0001:\u0003MNOB\u0013\b\u0016\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u0004\u0010\u0005B\u001d\b\u0016\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\u0004\b\u0004\u0010\bJ\u0010\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020\nH\u0016J\b\u0010'\u001a\u00020%H\u0016J\u0010\u0010(\u001a\u00020%2\u0006\u0010)\u001a\u00020\u0013H\u0016J\u0010\u0010*\u001a\u00020%2\u0006\u0010+\u001a\u00020\u0013H\u0016J\u0012\u0010,\u001a\u00020\n2\b\u0010-\u001a\u0004\u0018\u00010.H\u0016J\u0012\u0010,\u001a\u00020\n2\b\u0010/\u001a\u0004\u0018\u00010\u0007H\u0016J\u0012\u00100\u001a\u00020\u00132\b\u0010/\u001a\u0004\u0018\u00010\u0007H\u0016J\u0018\u0010,\u001a\u00020\n2\u0006\u0010/\u001a\u00020\u00072\u0006\u00101\u001a\u00020\u0007H\u0016J\b\u00102\u001a\u00020\nH\u0016J\u0010\u00103\u001a\u00020\u00132\u0006\u00104\u001a\u00020\u0017H\u0016J\u0018\u00103\u001a\u00020\u00132\u0006\u00104\u001a\u00020\u00172\u0006\u00105\u001a\u00020\u0013H\u0016J \u00103\u001a\u00020\u00132\u0006\u00104\u001a\u00020\u00172\u0006\u00106\u001a\u00020\u00132\u0006\u00105\u001a\u00020\u0013H\u0016J \u00107\u001a\u00020%2\b\u00104\u001a\u0004\u0018\u00010\u00172\u0006\u00106\u001a\u00020\u00132\u0006\u00105\u001a\u00020\u0013J\u0016\u00108\u001a\u00020\u00172\u0006\u00109\u001a\u00020\u00172\u0006\u0010:\u001a\u00020\u0013J\n\u0010;\u001a\u0004\u0018\u00010\u0017H\u0002J\u0012\u0010<\u001a\u0004\u0018\u00010\u00172\u0006\u0010=\u001a\u00020\u0013H\u0016J\u0012\u0010>\u001a\u0004\u0018\u00010\u00172\u0006\u0010?\u001a\u00020\u0013H\u0016J\b\u0010@\u001a\u00020\nH\u0016J\b\u0010A\u001a\u00020\u0007H\u0016J\n\u0010B\u001a\u0004\u0018\u00010\u0007H\u0016J\n\u0010C\u001a\u0004\u0018\u00010\u0007H\u0016J\u0010\u0010D\u001a\u00020%2\u0006\u0010 \u001a\u00020\nH\u0016J\u0018\u0010E\u001a\u00020%2\u0006\u0010F\u001a\u00020\u00132\u0006\u0010G\u001a\u00020\u0013H\u0016J\n\u0010H\u001a\u0004\u0018\u00010\fH\u0016J\n\u0010I\u001a\u0004\u0018\u00010\u000eH\u0016J\u0006\u0010J\u001a\u00020\u0007J\u000e\u0010K\u001a\u00020%2\u0006\u0010\u0011\u001a\u00020\u0007J\b\u0010L\u001a\u00020\nH\u0016R\u000e\u0010\t\u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0013X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u0013X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u0013X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\nX\u0082D¢\u0006\u0002\n\u0000R\u0010\u0010\u001d\u001a\u0004\u0018\u00010\u001eX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u001f\u001a\u0004\u0018\u00010\u001eX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\u0013X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020\u0013X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010#\u001a\u0004\u0018\u00010\u0003X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006P"}, d2 = {"LHPRTAndroidSDK/WiFiOperator;", "LHPRTAndroidSDK/BaseOperator;", f.X, "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "strPrinterName", "", "(Landroid/content/Context;Ljava/lang/String;)V", "blnOpenPort", "", "mmInStream", "Ljava/io/InputStream;", "mmOutStream", "Ljava/io/OutputStream;", "socketC", "Ljava/net/Socket;", "ipAddress", "port", "", "textCommandLock", "", "textReadBuffer", "", "ReadTimeout", "wifi", "intWR", "a", "isok", "timing1", "Ljava/lang/Thread;", "readerthread", "isFrist", "n", WXComponent.PROP_FS_MATCH_PARENT, "mContext", "IsBLEType", "", "isBLEType", "InitPort", "SetReadTimeout", "readTimeout", "SetWriteTimeout", "writeTimeout", "OpenPort", "usbdevice", "Landroid/hardware/usb/UsbDevice;", "PortParam", "OpenPortTest", "PortNumber", "ClosePort", "WriteData", "Data", "intDataLength", "intOffset", "keepAliveWriteData", "writeAndReadText", "data", "timeout", "takeTextResponse", "ReadData", "second", "ReadDataMillisecond", "millisecond", "IsOpen", "GetPortType", "GetPrinterName", "GetPrinterModel", "setIsFirst", "setKey", "nKey", "mKey", "getInputStream", "getOutputStream", "getIpAddress", "setIpAddress", "reConnect", "CloseThread", "readThread", "Companion", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
public class WiFiOperator extends BaseOperator {
    private int ReadTimeout;
    private final int a;
    private boolean blnOpenPort;
    private final int intWR;
    private String ipAddress;
    private boolean isFrist;
    private final boolean isok;
    private int m;
    private Context mContext;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    private int n;
    private int port;
    private final Thread readerthread;
    private Socket socketC;
    private final Object textCommandLock;
    private byte[] textReadBuffer;
    private final Thread timing1;
    private boolean wifi;
    private static String PrinterName = "";
    private static String InPrinterName = "";

    @Override // HPRTAndroidSDK.IPort
    public void InitPort() {
    }

    @Override // HPRTAndroidSDK.IPort
    public void IsBLEType(boolean isBLEType) {
    }

    @Override // HPRTAndroidSDK.IPort
    public boolean OpenPort(UsbDevice usbdevice) {
        return false;
    }

    @Override // HPRTAndroidSDK.IPort
    public boolean OpenPort(String PortParam) {
        return false;
    }

    @Override // HPRTAndroidSDK.IPort
    public int OpenPortTest(String PortParam) {
        return 0;
    }

    @Override // HPRTAndroidSDK.IPort
    public void SetWriteTimeout(int writeTimeout) {
    }

    public final void keepAliveWriteData(byte[] Data, int intOffset, int intDataLength) {
    }

    public WiFiOperator(Context context) {
        this.ipAddress = "";
        this.textCommandLock = new Object();
        this.textReadBuffer = new byte[0];
        this.ReadTimeout = 1000;
        this.wifi = true;
        this.isok = true;
        this.mContext = context;
        InPrinterName = "HPRT";
    }

    public WiFiOperator(Context context, String str) {
        this.ipAddress = "";
        this.textCommandLock = new Object();
        this.textReadBuffer = new byte[0];
        this.ReadTimeout = 1000;
        this.wifi = true;
        this.isok = true;
        this.mContext = context;
        PrinterName = str;
        InPrinterName = str;
    }

    @Override // HPRTAndroidSDK.IPort
    public void SetReadTimeout(int readTimeout) {
        this.ReadTimeout = readTimeout;
    }

    @Override // HPRTAndroidSDK.IPort
    public boolean OpenPort(final String PortParam, final String PortNumber) {
        Intrinsics.checkNotNullParameter(PortParam, "PortParam");
        Intrinsics.checkNotNullParameter(PortNumber, "PortNumber");
        try {
            Thread thread = new Thread() { // from class: HPRTAndroidSDK.WiFiOperator$OpenPort$thread$1
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    this.this$0.ipAddress = PortParam;
                    this.this$0.port = Integer.parseInt(PortNumber);
                    this.this$0.wifi = true;
                    this.this$0.blnOpenPort = false;
                    if (this.this$0.ipAddress.length() <= 0 || this.this$0.port <= 0) {
                        return;
                    }
                    try {
                        this.this$0.socketC = new Socket();
                        InetSocketAddress inetSocketAddress = new InetSocketAddress(this.this$0.ipAddress, this.this$0.port);
                        Socket socket = this.this$0.socketC;
                        Intrinsics.checkNotNull(socket);
                        socket.connect(inetSocketAddress, 5000);
                        WiFiOperator wiFiOperator = this.this$0;
                        Socket socket2 = wiFiOperator.socketC;
                        Intrinsics.checkNotNull(socket2);
                        wiFiOperator.mmInStream = socket2.getInputStream();
                        WiFiOperator wiFiOperator2 = this.this$0;
                        Socket socket3 = wiFiOperator2.socketC;
                        Intrinsics.checkNotNull(socket3);
                        wiFiOperator2.mmOutStream = socket3.getOutputStream();
                        Object obj = this.this$0.textCommandLock;
                        WiFiOperator wiFiOperator3 = this.this$0;
                        synchronized (obj) {
                            wiFiOperator3.textReadBuffer = new byte[0];
                            Unit unit = Unit.INSTANCE;
                        }
                        this.this$0.blnOpenPort = true;
                    } catch (Exception e) {
                        Log.e("WiFiOperator", "OpenPort --> UNconnect " + e.getMessage());
                        this.this$0.blnOpenPort = false;
                    }
                }
            };
            thread.start();
            thread.join();
            return this.blnOpenPort;
        } catch (InterruptedException unused) {
            return false;
        }
    }

    @Override // HPRTAndroidSDK.IPort
    public boolean ClosePort() {
        try {
            CloseThread closeThread = new CloseThread();
            closeThread.start();
            closeThread.join();
            return closeThread.getIsClose();
        } catch (Exception unused) {
            return false;
        }
    }

    /* JADX INFO: compiled from: WiFiOperator.kt */
    @Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\b\u0080\u0004\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\t\u001a\u00020\nH\u0016R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0004\u0010\u0006\"\u0004\b\u0007\u0010\b¨\u0006\u000b"}, d2 = {"LHPRTAndroidSDK/WiFiOperator$CloseThread;", "Ljava/lang/Thread;", "<init>", "(LHPRTAndroidSDK/WiFiOperator;)V", "isClose", "", "()Z", "setClose", "(Z)V", "run", "", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public final class CloseThread extends Thread {
        private boolean isClose;

        public CloseThread() {
        }

        /* JADX INFO: renamed from: isClose, reason: from getter */
        public final boolean getIsClose() {
            return this.isClose;
        }

        public final void setClose(boolean z) {
            this.isClose = z;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            try {
                if (WiFiOperator.this.mmInStream != null) {
                    InputStream inputStream = WiFiOperator.this.mmInStream;
                    Intrinsics.checkNotNull(inputStream);
                    inputStream.close();
                    WiFiOperator.this.mmInStream = null;
                }
                if (WiFiOperator.this.mmOutStream != null) {
                    OutputStream outputStream = WiFiOperator.this.mmOutStream;
                    Intrinsics.checkNotNull(outputStream);
                    outputStream.close();
                    WiFiOperator.this.mmOutStream = null;
                }
                if (WiFiOperator.this.socketC != null) {
                    Socket socket = WiFiOperator.this.socketC;
                    Intrinsics.checkNotNull(socket);
                    socket.close();
                    WiFiOperator.this.socketC = null;
                }
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException unused) {
                    this.isClose = false;
                }
                this.isClose = true;
            } catch (IOException unused2) {
                this.isClose = false;
            }
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
    public int WriteData(final byte[] Data, int intOffset, final int intDataLength) {
        Intrinsics.checkNotNullParameter(Data, "Data");
        try {
            final byte[] bArr = new byte[intDataLength];
            for (int i = 0; i < intDataLength; i++) {
                if (this.isFrist) {
                    bArr[i] = Data[intOffset + i];
                } else {
                    bArr[i] = (byte) (Data[intOffset + i] ^ (this.n + this.m));
                }
            }
            Thread thread = new Thread() { // from class: HPRTAndroidSDK.WiFiOperator$WriteData$thread$1
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    try {
                        if (this.this$0.mmOutStream == null) {
                            return;
                        }
                        OutputStream outputStream = this.this$0.mmOutStream;
                        Intrinsics.checkNotNull(outputStream);
                        outputStream.write(bArr, 0, intDataLength);
                        if (HPRTPrinterHelper.isLog) {
                            HPRTPrinterHelper.INSTANCE.logcat("Write:" + Tools.byteToHexWithEmpty(Data));
                        }
                        if (HPRTPrinterHelper.isWriteLog) {
                            if (HPRTPrinterHelper.isHex) {
                                String str = "Write:" + Tools.byteToHexWithEmpty(Data);
                                Context context = this.this$0.mContext;
                                byte[] bytes = str.getBytes(Charsets.UTF_8);
                                Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
                                LogUlit.writeFileToSDCard(context, bytes, HPRTConst.FOLDER, HPRTConst.FOLDER_NAME, true, true);
                            } else {
                                LogUlit.writeFileToSDCard(this.this$0.mContext, Data, HPRTConst.FOLDER, HPRTConst.FOLDER_NAME, true, true);
                            }
                            Context context2 = this.this$0.mContext;
                            byte[] bytes2 = new String(Data, Charsets.UTF_8).getBytes(Charsets.UTF_8);
                            Intrinsics.checkNotNullExpressionValue(bytes2, "getBytes(...)");
                            LogUlit.writeFileToSDCard(context2, bytes2, HPRTConst.FOLDER, HPRTConst.FOLDER_NAME, true, true);
                        }
                        OutputStream outputStream2 = this.this$0.mmOutStream;
                        Intrinsics.checkNotNull(outputStream2);
                        outputStream2.flush();
                    } catch (Exception e) {
                        Log.d("PRTLIB", "WriteData --> error " + e.getMessage());
                        this.this$0.blnOpenPort = false;
                    }
                }
            };
            thread.start();
            thread.join();
            return intDataLength;
        } catch (Exception e) {
            Log.d("PRTLIB", "WriteData --> error " + e.getMessage());
            return -1;
        }
    }

    public final byte[] writeAndReadText(byte[] data, int timeout) {
        InputStream inputStream;
        Intrinsics.checkNotNullParameter(data, "data");
        synchronized (this.textCommandLock) {
            byte[] bArrTakeTextResponse = takeTextResponse();
            if (bArrTakeTextResponse != null) {
                return bArrTakeTextResponse;
            }
            if (WriteData(data) == -1) {
                return new byte[0];
            }
            Socket socket = this.socketC;
            if (socket == null) {
                return new byte[0];
            }
            int soTimeout = socket.getSoTimeout();
            try {
                long jCurrentTimeMillis = System.currentTimeMillis() + ((long) timeout);
                while (true) {
                    byte[] bArrTakeTextResponse2 = takeTextResponse();
                    if (bArrTakeTextResponse2 != null) {
                        return bArrTakeTextResponse2;
                    }
                    long jCurrentTimeMillis2 = jCurrentTimeMillis - System.currentTimeMillis();
                    if (jCurrentTimeMillis2 <= 0) {
                        return new byte[0];
                    }
                    socket.setSoTimeout((int) Math.min(jCurrentTimeMillis2, 200L));
                    byte[] bArr = new byte[256];
                    try {
                        inputStream = this.mmInStream;
                    } catch (SocketTimeoutException unused) {
                    }
                    if (inputStream == null) {
                        return new byte[0];
                    }
                    int i = inputStream.read(bArr);
                    if (i < 0) {
                        return new byte[0];
                    }
                    if (i > 0) {
                        byte[] bArrCopyOf = Arrays.copyOf(bArr, i);
                        Intrinsics.checkNotNullExpressionValue(bArrCopyOf, "copyOf(...)");
                        if (HPRTPrinterHelper.isLog) {
                            HPRTPrinterHelper.INSTANCE.logcat("Read:" + Tools.byteToHexWithEmpty(bArrCopyOf));
                            HPRTPrinterHelper.INSTANCE.logcat("Read:" + new String(bArrCopyOf, Charsets.UTF_8));
                        }
                        this.textReadBuffer = ArraysKt.plus(this.textReadBuffer, bArrCopyOf);
                    }
                }
            } catch (IOException unused2) {
                return new byte[0];
            } finally {
                socket.setSoTimeout(soTimeout);
            }
        }
    }

    private final byte[] takeTextResponse() {
        while (true) {
            int iIndexOf = ArraysKt.indexOf(this.textReadBuffer, (byte) 0);
            if (iIndexOf < 0) {
                return null;
            }
            byte[] bArrCopyOfRange = ArraysKt.copyOfRange(this.textReadBuffer, 0, iIndexOf);
            byte[] bArr = this.textReadBuffer;
            this.textReadBuffer = ArraysKt.copyOfRange(bArr, iIndexOf + 1, bArr.length);
            if (!Arrays.equals(bArrCopyOfRange, new byte[]{78, 71}) && !Arrays.equals(bArrCopyOfRange, new byte[]{79, 75})) {
                return bArrCopyOfRange;
            }
        }
    }

    @Override // HPRTAndroidSDK.IPort
    public byte[] ReadData(int second) {
        readThread readthread = new readThread(second * 1000);
        readthread.start();
        try {
            readthread.join();
            return readthread.getData();
        } catch (InterruptedException unused) {
            return new byte[0];
        }
    }

    @Override // HPRTAndroidSDK.IPort
    public byte[] ReadDataMillisecond(int millisecond) {
        readThread readthread = new readThread(millisecond);
        readthread.start();
        try {
            readthread.join();
            return readthread.getData();
        } catch (InterruptedException unused) {
            return new byte[0];
        }
    }

    @Override // HPRTAndroidSDK.IPort
    /* JADX INFO: renamed from: IsOpen, reason: from getter */
    public boolean getBlnOpenPort() {
        return this.blnOpenPort;
    }

    @Override // HPRTAndroidSDK.IPort
    public String GetPortType() {
        return "WiFi";
    }

    @Override // HPRTAndroidSDK.IPort
    public String GetPrinterName() {
        return PrinterName;
    }

    @Override // HPRTAndroidSDK.IPort
    public String GetPrinterModel() {
        return PrinterName;
    }

    /* JADX INFO: compiled from: WiFiOperator.kt */
    @Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0010\u0012\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\b\u0080\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\u0010\u001a\u00020\u0011H\u0016R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u001a\u0010\n\u001a\u00020\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000f¨\u0006\u0012"}, d2 = {"LHPRTAndroidSDK/WiFiOperator$readThread;", "Ljava/lang/Thread;", "second", "", "<init>", "(LHPRTAndroidSDK/WiFiOperator;I)V", "getSecond", "()I", "setSecond", "(I)V", "Data", "", "getData", "()[B", "setData", "([B)V", "run", "", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public final class readThread extends Thread {
        private byte[] Data = new byte[0];
        private int second;

        public readThread(int i) {
            this.second = i;
        }

        public final int getSecond() {
            return this.second;
        }

        public final void setSecond(int i) {
            this.second = i;
        }

        public final byte[] getData() {
            return this.Data;
        }

        public final void setData(byte[] bArr) {
            Intrinsics.checkNotNullParameter(bArr, "<set-?>");
            this.Data = bArr;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            int i = 0;
            while (i < this.second && WiFiOperator.this.mmInStream != null) {
                try {
                    InputStream inputStream = WiFiOperator.this.mmInStream;
                    Intrinsics.checkNotNull(inputStream);
                    int iAvailable = inputStream.available();
                    if (iAvailable > 0) {
                        this.Data = new byte[iAvailable];
                        InputStream inputStream2 = WiFiOperator.this.mmInStream;
                        Intrinsics.checkNotNull(inputStream2);
                        inputStream2.read(this.Data);
                        if (HPRTPrinterHelper.isLog) {
                            HPRTPrinterHelper.INSTANCE.logcat("Read:" + Tools.byteToHexWithEmpty(this.Data));
                            String str = new String(this.Data, Charsets.UTF_8);
                            HPRTPrinterHelper.INSTANCE.logcat("Read:" + str);
                        }
                        i = this.second + 1;
                    } else {
                        try {
                            Thread.sleep(this.second / 20);
                            i += this.second / 20;
                        } catch (InterruptedException unused) {
                            return;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
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
    /* JADX INFO: renamed from: getInputStream, reason: from getter */
    public InputStream getMmInStream() {
        return this.mmInStream;
    }

    @Override // HPRTAndroidSDK.IPort
    /* JADX INFO: renamed from: getOutputStream, reason: from getter */
    public OutputStream getMmOutStream() {
        return this.mmOutStream;
    }

    public final String getIpAddress() {
        return this.ipAddress;
    }

    public final void setIpAddress(String ipAddress) {
        Intrinsics.checkNotNullParameter(ipAddress, "ipAddress");
        this.ipAddress = ipAddress;
    }

    @Override // HPRTAndroidSDK.BaseOperator, HPRTAndroidSDK.IPort
    public boolean reConnect() {
        return this.ipAddress.length() != 0 && this.port > 0 && ClosePort() && OpenPort(this.ipAddress, String.valueOf(this.port));
    }
}
