package HPRTAndroidSDK;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes.dex */
public class SerialOperator extends BaseOperator {
    private int baudrate;
    private Context context;
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;
    private String port;
    byte[] readData1;
    private int readDataN;
    private Readerthread readerthread;
    private Thread timing1;
    private boolean blnOpenPort = false;
    private boolean Isokread = false;

    @Override // HPRTAndroidSDK.IPort
    public String GetPortType() {
        return null;
    }

    @Override // HPRTAndroidSDK.IPort
    public String GetPrinterModel() {
        return null;
    }

    @Override // HPRTAndroidSDK.IPort
    public String GetPrinterName() {
        return null;
    }

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
    public void SetReadTimeout(int readTimeout) {
    }

    @Override // HPRTAndroidSDK.IPort
    public void SetWriteTimeout(int writeTimeout) {
    }

    @Override // HPRTAndroidSDK.IPort
    public void setIsFirst(boolean isFrist) {
    }

    @Override // HPRTAndroidSDK.IPort
    public void setKey(int nKey, int mKey) {
    }

    public SerialOperator(Context context, String port) {
        this.context = context;
    }

    @Override // HPRTAndroidSDK.IPort
    public boolean OpenPort(String PortParam, String PortNumber) {
        this.port = PortParam;
        this.baudrate = Integer.valueOf(PortNumber).intValue();
        try {
            System.out.println("mFd:" + this.mFd);
            if (this.mFd == null) {
                return false;
            }
            this.mFileInputStream = new FileInputStream(this.mFd);
            this.mFileOutputStream = new FileOutputStream(this.mFd);
            this.blnOpenPort = true;
            return true;
        } catch (SecurityException unused) {
            return false;
        }
    }

    @Override // HPRTAndroidSDK.IPort
    public boolean ClosePort() {
        FileOutputStream fileOutputStream = this.mFileOutputStream;
        if (fileOutputStream == null || this.mFileInputStream == null) {
            return true;
        }
        try {
            fileOutputStream.close();
            this.mFileInputStream.close();
            this.blnOpenPort = false;
            return true;
        } catch (IOException unused) {
            return false;
        }
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
        if (this.mFileOutputStream == null) {
            return -1;
        }
        try {
            byte[] bArr = new byte[10000];
            int i2 = intDataLength / 10000;
            int i3 = 0;
            while (i3 < i2) {
                int i4 = i3 * 10000;
                while (true) {
                    i = i3 + 1;
                    if (i4 >= i * 10000) {
                        break;
                    }
                    bArr[i4 % 10000] = Data[i4];
                    i4++;
                }
                this.mFileOutputStream.write(bArr, 0, 10000);
                if (HPRTPrinterHelper.isWriteLog) {
                    if (HPRTPrinterHelper.isHex) {
                        String strByteToHex = Tools.byteToHex(bArr);
                        HPRTPrinterHelper.logcat(strByteToHex);
                        LogUlit.writeFileToSDCard(this.context, strByteToHex.getBytes(), HPRTConst.FOLDER, HPRTConst.FOLDER_NAME, true, true);
                    } else {
                        LogUlit.writeFileToSDCard(this.context, bArr, HPRTConst.FOLDER, HPRTConst.FOLDER_NAME, true, true);
                    }
                }
                i3 = i;
            }
            if (intDataLength % 10000 == 0) {
                return 1;
            }
            int i5 = i2 * 10000;
            int length = Data.length - i5;
            byte[] bArr2 = new byte[length];
            for (int i6 = i5; i6 < Data.length; i6++) {
                bArr2[i6 - i5] = Data[i6];
            }
            this.mFileOutputStream.write(bArr2, 0, length);
            if (!HPRTPrinterHelper.isWriteLog) {
                return 1;
            }
            if (HPRTPrinterHelper.isHex) {
                String strByteToHex2 = Tools.byteToHex(bArr2);
                HPRTPrinterHelper.logcat(strByteToHex2);
                LogUlit.writeFileToSDCard(this.context, strByteToHex2.getBytes(), HPRTConst.FOLDER, HPRTConst.FOLDER_NAME, true, true);
                return 1;
            }
            LogUlit.writeFileToSDCard(this.context, bArr2, HPRTConst.FOLDER, HPRTConst.FOLDER_NAME, true, true);
            return 1;
        } catch (IOException e) {
            System.out.println("WriteData:" + e.getMessage().toString());
            return -1;
        }
    }

    @Override // HPRTAndroidSDK.IPort
    public byte[] ReadData(int second) {
        int i = 0;
        byte[] bArr = new byte[0];
        if (this.mFileInputStream == null) {
            return bArr;
        }
        while (true) {
            int i2 = second * 10;
            if (i >= i2) {
                break;
            }
            try {
                int iAvailable = this.mFileInputStream.available();
                if (iAvailable > 0) {
                    bArr = new byte[iAvailable];
                    this.mFileInputStream.read(bArr);
                    i = i2 + 1;
                } else {
                    Thread.sleep(100L);
                    i++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }
        return bArr;
    }

    @Override // HPRTAndroidSDK.IPort
    public byte[] ReadDataMillisecond(int millisecond) {
        return new byte[0];
    }

    @Override // HPRTAndroidSDK.IPort
    /* JADX INFO: renamed from: IsOpen */
    public boolean getBlnOpenPort() {
        return this.blnOpenPort;
    }

    @Override // HPRTAndroidSDK.IPort
    /* JADX INFO: renamed from: getInputStream */
    public InputStream getMmInStream() {
        return this.mFileInputStream;
    }

    @Override // HPRTAndroidSDK.IPort
    /* JADX INFO: renamed from: getOutputStream */
    public OutputStream getMmOutStream() {
        return this.mFileOutputStream;
    }

    public class Readerthread extends Thread {
        public Readerthread(byte[] Data) {
            SerialOperator.this.readData1 = Data;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            super.run();
            SerialOperator.this.timing1 = new Thread() { // from class: HPRTAndroidSDK.SerialOperator.Readerthread.1
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    for (int i = 0; i < 2; i++) {
                        try {
                            sleep(1000L);
                        } catch (InterruptedException unused) {
                            SerialOperator.this.readDataN = -1;
                            SerialOperator.this.Isokread = false;
                            return;
                        }
                    }
                    SerialOperator.this.readDataN = -1;
                    SerialOperator.this.Isokread = false;
                }
            };
            SerialOperator.this.timing1.start();
            try {
                SerialOperator.this.mFileInputStream.available();
                SerialOperator.this.mFileInputStream.read(SerialOperator.this.readData1, 0, SerialOperator.this.readData1.length);
                SerialOperator.this.readDataN = 1;
                SerialOperator.this.Isokread = false;
            } catch (Exception unused) {
                SerialOperator.this.readDataN = -1;
                SerialOperator.this.Isokread = false;
            }
        }
    }

    public int Readdata(byte[] Data) {
        Readerthread readerthread;
        this.Isokread = true;
        this.readDataN = -1;
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
