package HPRTAndroidSDK;

import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class Check {
    public static final int HANDSHAKE_NO_RESPONSE = 2;
    static int count;

    /* JADX WARN: Removed duplicated region for block: B:27:0x00a2 A[Catch: Exception -> 0x00ac, TRY_LEAVE, TryCatch #0 {Exception -> 0x00ac, blocks: (B:3:0x0004, B:6:0x0049, B:8:0x004f, B:12:0x0057, B:15:0x0074, B:17:0x007a, B:20:0x007e, B:22:0x0097, B:27:0x00a2), top: B:35:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:31:0x00a9 A[ORIG_RETURN, RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static int ChackHands(int n, int m) {
        byte b;
        byte b2;
        try {
            HPRTPrinterHelper.cleanRead();
            byte b3 = (byte) n;
            byte b4 = (byte) m;
            if (HPRTPrinterHelper.WriteData(new byte[]{27, 28, 115, 101, 116, 32, 109, 109, b3, b4}) == -1) {
                return -1;
            }
            byte[] bArrReadData = HPRTPrinterHelper.ReadData(3);
            if (bArrReadData != null) {
                b = 27;
                if (bArrReadData.length < 2) {
                }
                HPRTPrinterHelper.logcat("成功：" + Tools.byteToHex(bArrReadData));
                b2 = bArrReadData[0];
                if (b2 != 79 && bArrReadData[1] == 75) {
                    return 0;
                }
                if (b2 != 78) {
                    return bArrReadData[1] == 71 ? 1 : -4;
                }
                return -4;
            }
            b = 27;
            if (HPRTPrinterHelper.WriteData(new byte[]{b, 28, 115, 101, 116, 32, 109, 109, b3, b4}) == -1) {
                return -1;
            }
            bArrReadData = HPRTPrinterHelper.ReadData(3);
            if (bArrReadData != null) {
                if (bArrReadData.length < 2) {
                }
                HPRTPrinterHelper.logcat("成功：" + Tools.byteToHex(bArrReadData));
                b2 = bArrReadData[0];
                if (b2 != 79) {
                }
                if (b2 != 78) {
                }
            }
            return 2;
        } catch (Exception unused) {
            return -1;
        }
    }

    public static boolean CheckPrinter() {
        byte[] bArr;
        byte[] bArr2;
        try {
            Log.d("PRTLIB", "CheckPrinter...");
            bArr = new byte[16];
            bArr2 = new byte[19];
            if (HPRTPrinterHelper.isLog) {
                String strByteToHex = Tools.byteToHex(bArr2);
                if (strByteToHex.contains("1B1B1B")) {
                    if (count == 1) {
                        count = 0;
                    }
                    count++;
                }
                HPRTPrinterHelper.logcat("MD5Rand:" + strByteToHex);
                HPRTPrinterHelper.logcat("MD5Return:" + Tools.byteToHex(bArr));
            }
            HPRTPrinterHelper.cleanRead();
        } catch (Exception unused) {
        }
        if (HPRTPrinterHelper.WriteData(bArr2) <= 0) {
            Log.d("PRTLIB", "CheckPrinterNot Right Printer.Write Error!");
            return false;
        }
        byte[] bArrReadData = HPRTPrinterHelper.ReadData(5);
        HPRTPrinterHelper.logcat("PrinterReturn:" + Tools.byteToHex(bArrReadData));
        if (bArrReadData.length == 0) {
            if (HPRTPrinterHelper.WriteData(bArr2) <= 0) {
                return false;
            }
            bArrReadData = HPRTPrinterHelper.ReadData(5);
            if (bArrReadData.length == 0) {
                return false;
            }
        }
        if (Tools.byteToHex(bArrReadData).contains(Tools.byteToHex(bArr))) {
            Log.d("PRTLIB", "CheckPrinterRight Printer succeed.");
            return true;
        }
        Log.d("PRTLIB", "CheckPrinterNot Right Printer." + Tools.byteToHex(bArrReadData));
        return false;
    }
}
