package com.prt.print.utils.printer;

import HPRTAndroidSDK.HPRTPrinterHelper;
import android.graphics.Bitmap;
import com.prt.base.common.DeviceInfo;
import com.prt.base.utils.KLogger;
import com.prt.base.utils.RecycleUtils;
import org.apache.poi.ss.formula.ptg.BoolPtg;
import org.apache.poi.ss.formula.ptg.Ptg;

/* JADX INFO: loaded from: classes6.dex */
class ESCA200UPrinterManager extends ESCPrinterManager {
    @Override // com.prt.print.utils.printer.ESCPrinterManager, com.prt.print.utils.printer.IPrintManager
    public boolean setPaperType(DeviceInfo deviceInfo, int paperType) {
        return true;
    }

    ESCA200UPrinterManager() {
    }

    @Override // com.prt.print.utils.printer.ESCPrinterManager, com.prt.print.utils.printer.IPrintManager
    public int setPaperLearn(DeviceInfo deviceInfo, int paperType) {
        try {
            return HPRTPrinterHelper.setGapDetectA200U();
        } catch (Exception unused) {
            return -1;
        }
    }

    @Override // com.prt.print.utils.printer.ESCPrinterManager
    protected void setImageAlignment(int alignment) {
        try {
            if (alignment == 0) {
                HPRTPrinterHelper.setPrintPagePositionA200U(1);
            } else if (alignment == 1) {
                HPRTPrinterHelper.setPrintPagePositionA200U(2);
            } else {
                HPRTPrinterHelper.setPrintPagePositionA200U(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.prt.print.utils.printer.ESCPrinterManager, com.prt.print.utils.printer.BasePrintManager, com.prt.print.utils.printer.IPrintManager
    public synchronized int printBitmap(DeviceInfo deviceInfo, Bitmap bitmap, int perCount, int alignment, boolean isLabel, boolean containImg, boolean keepPrint, int typeA4) {
        int i;
        int i2;
        int paperLength;
        i = -1;
        try {
            HPRTPrinterHelper.WriteData(new byte[]{27, Ptg.CLASS_ARRAY});
            setImageAlignment(alignment);
            Bitmap bitmapCopy = bitmap.copy(Bitmap.Config.ARGB_8888, false);
            int i3 = 0;
            while (true) {
                if (i3 >= perCount) {
                    i2 = 0;
                    break;
                }
                if (HPRTPrinterHelper.printBitmap(bitmapCopy, 0, deviceInfo.compress, deviceInfo.subcontract == 0 ? 200 : deviceInfo.subcontract * 1024) <= 0) {
                    i2 = -1;
                    break;
                }
                if (deviceInfo.printerBean != null && (paperLength = deviceInfo.printerBean.getPaperLength()) != 0 && !isLabel) {
                    HPRTPrinterHelper.setESCFeed(paperLength);
                }
                if (isLabel) {
                    HPRTPrinterHelper.WriteData(new byte[]{12});
                    if ("J70".equalsIgnoreCase(deviceInfo.printerName) || "J60".equalsIgnoreCase(deviceInfo.printerName)) {
                        HPRTPrinterHelper.WriteData(new byte[]{BoolPtg.sid, 86, 65, 0});
                    }
                } else {
                    HPRTPrinterHelper.WriteData(new byte[]{10, 10, 10});
                }
                HPRTPrinterHelper.setConnectState(0);
                i3++;
            }
            RecycleUtils.recycle(bitmapCopy);
            i = i2;
        } catch (Exception e) {
            KLogger.e(e.getMessage());
        }
        return i;
    }
}
