package com.prt.print.utils.printer;

import HPRTAndroidSDK.HPRTPrinterHelper;
import com.prt.base.common.DeviceInfo;

/* JADX INFO: loaded from: classes6.dex */
public class CPCLA200UPrintManager extends CPCLPrintManager {
    @Override // com.prt.print.utils.printer.CPCLPrintManager, com.prt.print.utils.printer.IPrintManager
    public int setPaperLearn(DeviceInfo deviceInfo, int paperType) {
        try {
            return HPRTPrinterHelper.setGapDetectA200U();
        } catch (Exception unused) {
            return -1;
        }
    }
}
