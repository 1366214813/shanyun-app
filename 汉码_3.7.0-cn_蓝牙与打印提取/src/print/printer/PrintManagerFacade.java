package com.prt.print.utils.printer;

import HPRTAndroidSDK.HPRTPrinterHelper;
import android.graphics.Bitmap;
import com.blankj.utilcode.util.LogUtils;
import com.prt.base.common.DeviceInfo;
import com.prt.base.utils.AppUtils;
import com.prt.base.utils.StringUtils;
import com.prt.print.utils.PrintSettingUtils;
import java.util.List;

/* JADX INFO: loaded from: classes6.dex */
public class PrintManagerFacade extends BasePrintManager {
    protected static IPrintManager iPrintManager;
    private CPCLPrintManager cpclPrintManager;
    private CPCLA200UPrintManager cpcla200uPrintManager;
    private CPCLT260PrintManager cpclt260PrintManager;
    private ESCPrinterManager escPrintManager;
    private ESCA200UPrinterManager esca200uPrinterManager;
    private ESCPOLIPrinterManager escpoliPrintManager;
    private ESCTL31WPrinterManager esctl31WPrinterManager;
    private MT8900PrinterManager mt8900PrinterManager;
    private SunmiESCPrinterManager sunmiESCPrinterManager;
    private SunmiTSPLPrinterManager sunmiTSPLPrinterManager;
    private TSPLPrinterManager tsplPrinterManager;
    private TSPLTL31WPrinterManager tspltl31WPrinterManager;
    private ZPLPrintManager zplPrintManager;

    private PrintManagerFacade() {
        new HPRTPrinterHelper(AppUtils.getContext(), "");
    }

    public static PrintManagerFacade getInstance() {
        return SingletonHolder.instance;
    }

    public void creteInstance(DeviceInfo deviceInfo) {
        iPrintManager = getPrintManager(deviceInfo);
    }

    @Override // com.prt.print.utils.printer.IPrintManager
    public int setPaperLearn(DeviceInfo deviceInfo, int paperType) {
        if (iPrintManager == null) {
            iPrintManager = getPrintManager(deviceInfo);
        }
        IPrintManager iPrintManager2 = iPrintManager;
        if (iPrintManager2 == null) {
            return 0;
        }
        return iPrintManager2.setPaperLearn(deviceInfo, paperType);
    }

    @Override // com.prt.print.utils.printer.IPrintManager
    public boolean setPaperType(DeviceInfo deviceInfo, int paperType) {
        if (iPrintManager == null) {
            iPrintManager = getPrintManager(deviceInfo);
        }
        IPrintManager iPrintManager2 = iPrintManager;
        if (iPrintManager2 == null) {
            return false;
        }
        return iPrintManager2.setPaperType(deviceInfo, paperType);
    }

    @Override // com.prt.print.utils.printer.IPrintManager
    public synchronized boolean setDensity(DeviceInfo deviceInfo, int density) {
        if (iPrintManager == null) {
            iPrintManager = getPrintManager(deviceInfo);
        }
        IPrintManager iPrintManager2 = iPrintManager;
        if (iPrintManager2 == null) {
            return false;
        }
        if (density <= 0 || density > 4) {
            return true;
        }
        return iPrintManager2.setDensity(deviceInfo, density);
    }

    @Override // com.prt.print.utils.printer.BasePrintManager, com.prt.print.utils.printer.IPrintManager
    public synchronized int printBitmap(DeviceInfo deviceInfo, Bitmap bitmap, int perCount, int alignment, boolean isLabel, boolean containImg, boolean keepPrint, int typeA4) {
        return iPrintManager.printBitmap(deviceInfo, bitmap, perCount, alignment, isLabel, containImg, keepPrint, typeA4);
    }

    @Override // com.prt.print.utils.printer.BasePrintManager, com.prt.print.utils.printer.IPrintManager
    public int printDoubleColorBitmap(DeviceInfo deviceInfo, List<Bitmap> bitmaps, int x, int y, int type, int density, int perCount, int alignment, boolean isLabel, boolean keepPrint) {
        return iPrintManager.printDoubleColorBitmap(deviceInfo, bitmaps, x, y, type, density, perCount, alignment, isLabel, keepPrint);
    }

    protected IPrintManager getPrintManager(DeviceInfo deviceInfo) {
        if (deviceInfo == null || StringUtils.isEmpty(deviceInfo.instruct)) {
            return null;
        }
        String str = deviceInfo.instruct;
        str.hashCode();
        switch (str) {
            case "ESC_POLI":
                if (this.escpoliPrintManager == null) {
                    this.escpoliPrintManager = new ESCPOLIPrinterManager();
                }
                return this.escpoliPrintManager;
            case "T260CPCL":
                if (this.cpclt260PrintManager == null) {
                    this.cpclt260PrintManager = new CPCLT260PrintManager();
                }
                return this.cpclt260PrintManager;
            case "ESC":
                if (deviceInfo.getPrinterVendor() == 1) {
                    if (this.sunmiESCPrinterManager == null) {
                        this.sunmiESCPrinterManager = new SunmiESCPrinterManager();
                    }
                    return this.sunmiESCPrinterManager;
                }
                if (deviceInfo.isMTSeries()) {
                    if (this.mt8900PrinterManager == null) {
                        this.mt8900PrinterManager = new MT8900PrinterManager();
                    }
                    return this.mt8900PrinterManager;
                }
                if (PrintSettingUtils.INSTANCE.isD25RBT() || PrintSettingUtils.INSTANCE.isTL31W()) {
                    if (this.esctl31WPrinterManager == null) {
                        this.esctl31WPrinterManager = new ESCTL31WPrinterManager();
                    }
                    return this.esctl31WPrinterManager;
                }
                if (PrintSettingUtils.INSTANCE.isA200U()) {
                    if (this.esca200uPrinterManager == null) {
                        this.esca200uPrinterManager = new ESCA200UPrinterManager();
                    }
                    return this.esca200uPrinterManager;
                }
                if (this.escPrintManager == null) {
                    this.escPrintManager = new ESCPrinterManager();
                }
                return this.escPrintManager;
            case "ZPL":
                if (this.zplPrintManager == null) {
                    this.zplPrintManager = new ZPLPrintManager();
                }
                return this.zplPrintManager;
            case "CPCL":
                if (PrintSettingUtils.INSTANCE.isA200U()) {
                    if (this.cpcla200uPrintManager == null) {
                        this.cpcla200uPrintManager = new CPCLA200UPrintManager();
                    }
                    return this.cpcla200uPrintManager;
                }
                if (this.cpclPrintManager == null) {
                    this.cpclPrintManager = new CPCLPrintManager();
                }
                return this.cpclPrintManager;
            case "TSPL":
                if (deviceInfo.getPrinterVendor() == 1) {
                    if (this.sunmiTSPLPrinterManager == null) {
                        this.sunmiTSPLPrinterManager = new SunmiTSPLPrinterManager();
                    }
                    return this.sunmiTSPLPrinterManager;
                }
                if (PrintSettingUtils.INSTANCE.isD25RBT() || PrintSettingUtils.INSTANCE.isTL31W()) {
                    if (this.tspltl31WPrinterManager == null) {
                        this.tspltl31WPrinterManager = new TSPLTL31WPrinterManager();
                    }
                    return this.tspltl31WPrinterManager;
                }
                if (this.tsplPrinterManager == null) {
                    this.tsplPrinterManager = new TSPLPrinterManager();
                }
                return this.tsplPrinterManager;
            default:
                return null;
        }
    }

    public static IPrintManager getiPrintManager() {
        return iPrintManager;
    }

    public int savePrintSetting(DeviceInfo deviceInfo, List<String> list) {
        if (StringUtils.isEmpty(deviceInfo.instruct)) {
            return -1;
        }
        if (iPrintManager == null) {
            creteInstance(deviceInfo);
        }
        int iSavePrinterSetting = iPrintManager.savePrinterSetting(list);
        LogUtils.d("保存打印设置结果：" + iSavePrinterSetting);
        return iSavePrinterSetting;
    }

    private static final class SingletonHolder {
        static final PrintManagerFacade instance = new PrintManagerFacade();

        private SingletonHolder() {
        }
    }
}
