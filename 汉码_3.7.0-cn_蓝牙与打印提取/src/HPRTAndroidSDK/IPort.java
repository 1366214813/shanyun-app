package HPRTAndroidSDK;

import android.hardware.usb.UsbDevice;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes.dex */
public interface IPort {
    public static final int CONNECT_STATE_NORMAL = 0;
    public static final int CONNECT_STATE_WRITE_AND_READ_ONCE = 1;
    public static final boolean IsPortOpen = false;
    public static final String PortType = "";
    public static final String paramPortSetting = "";

    boolean ClosePort();

    String GetPortType();

    String GetPrinterModel();

    String GetPrinterName();

    void InitPort();

    void IsBLEType(boolean isBLEType);

    boolean IsOpen();

    boolean OpenPort(UsbDevice usbdevice);

    boolean OpenPort(String PortParam);

    boolean OpenPort(String PortParam, String PortNumber);

    int OpenPortTest(String PortParam);

    byte[] ReadData(int second);

    byte[] ReadDataMillisecond(int millisecond);

    byte[] ReadDataMillisecond(boolean checkConnectState, int second);

    void SetReadTimeout(int readTimeout);

    void SetWriteTimeout(int writeTimeout);

    int WriteData(byte[] Data);

    int WriteData(byte[] Data, int intDataLength);

    int WriteData(byte[] Data, int intOffset, int intDataLength);

    int getConnectState();

    InputStream getInputStream();

    OutputStream getOutputStream();

    boolean reConnect();

    void setConnectState(int connectState);

    void setIsFirst(boolean isFirst);

    void setKey(int nKey, int mKey);
}
