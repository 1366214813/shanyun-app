package com.prt.print.utils.printer;

import HPRTAndroidSDK.HPRTPrinterHelper;
import com.prt.base.common.DeviceInfo;
import java.util.List;

/* JADX INFO: loaded from: classes6.dex */
class TSPLTL31WPrinterManager extends TSPLPrinterManager {
    @Override // com.prt.print.utils.printer.TSPLPrinterManager, com.prt.print.utils.printer.IPrintManager
    public boolean setPaperType(DeviceInfo deviceInfo, int paperType) {
        return true;
    }

    TSPLTL31WPrinterManager() {
    }

    @Override // com.prt.print.utils.printer.TSPLPrinterManager, com.prt.print.utils.printer.IPrintManager
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

    @Override // com.prt.print.utils.printer.TSPLPrinterManager, com.prt.print.utils.printer.IPrintManager
    public synchronized boolean setDensity(DeviceInfo deviceInfo, int density) {
        return true;
    }

    @Override // com.prt.print.utils.printer.BasePrintManager, com.prt.print.utils.printer.IPrintManager
    public int savePrinterSetting(List<String> list) {
        return super.savePrinterSetting(list);
    }
}
