package com.prt.print.utils.printer;

import HPRTAndroidSDK.HPRTPrinterHelper;
import android.graphics.Bitmap;
import com.prt.base.common.DeviceInfo;
import com.prt.base.utils.KLogger;
import com.prt.base.utils.RecycleUtils;
import com.prt.print.utils.PrintSettingUtils;
import java.util.List;
import org.apache.poi.ss.formula.ptg.BoolPtg;
import org.apache.poi.ss.formula.ptg.Ptg;

/* JADX INFO: loaded from: classes6.dex */
class ESCTL31WPrinterManager extends ESCPrinterManager {
    @Override // com.prt.print.utils.printer.ESCPrinterManager, com.prt.print.utils.printer.IPrintManager
    public boolean setPaperType(DeviceInfo deviceInfo, int paperType) {
        return true;
    }

    ESCTL31WPrinterManager() {
    }

    @Override // com.prt.print.utils.printer.ESCPrinterManager, com.prt.print.utils.printer.IPrintManager
    public int setPaperLearn(DeviceInfo deviceInfo, int paperType) {
        if (paperType == 1) {
            paperType = 2;
        } else if (paperType == 2) {
            paperType = 1;
        }
        try {
            return HPRTPrinterHelper.setGapDetectTL31W(paperType);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override // com.prt.print.utils.printer.ESCPrinterManager, com.prt.print.utils.printer.IPrintManager
    public synchronized boolean setDensity(DeviceInfo deviceInfo, int density) {
        return true;
    }

    @Override // com.prt.print.utils.printer.ESCPrinterManager, com.prt.print.utils.printer.BasePrintManager, com.prt.print.utils.printer.IPrintManager
    public synchronized int printBitmap(DeviceInfo deviceInfo, Bitmap bitmap, int perCount, int alignment, boolean isLabel, boolean containImg, boolean keepPrint, int typeA4) {
        int i;
        int i2;
        int iPrintBitmap;
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
                if (2 != deviceInfo.compress) {
                    if (deviceInfo.compressModel == 1) {
                        if (PrintSettingUtils.INSTANCE.isA200U()) {
                            iPrintBitmap = HPRTPrinterHelper.printBitmap(bitmapCopy, 0, 1, 800);
                        } else {
                            iPrintBitmap = HPRTPrinterHelper.printBitmap(bitmapCopy, 0, 1, 0);
                        }
                    } else {
                        iPrintBitmap = HPRTPrinterHelper.printBitmap(bitmapCopy, 0, 2, 0);
                    }
                } else {
                    iPrintBitmap = HPRTPrinterHelper.printBitmap(bitmapCopy, 0, 0, 0);
                }
                if (iPrintBitmap <= 0) {
                    i2 = -1;
                    break;
                }
                if (isLabel) {
                    HPRTPrinterHelper.WriteData(new byte[]{12});
                    if ("J70".equalsIgnoreCase(deviceInfo.printerName) || "J60".equalsIgnoreCase(deviceInfo.printerName)) {
                        HPRTPrinterHelper.WriteData(new byte[]{BoolPtg.sid, 86, 65, 0});
                    }
                } else if (deviceInfo.printerBean != null && (paperLength = deviceInfo.printerBean.getPaperLength()) != 0) {
                    HPRTPrinterHelper.setESCFeed(paperLength);
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

    @Override // com.prt.print.utils.printer.BasePrintManager, com.prt.print.utils.printer.IPrintManager
    public int savePrinterSetting(List<String> list) {
        return super.savePrinterSetting(list);
    }
}
