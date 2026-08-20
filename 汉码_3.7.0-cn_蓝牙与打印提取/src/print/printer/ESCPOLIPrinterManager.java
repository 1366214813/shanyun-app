package com.prt.print.utils.printer;

import HPRTAndroidSDK.HPRTPrinterHelper;
import HPRTAndroidSDK.HPRTPrinterHelperV1;
import android.graphics.Bitmap;
import com.blankj.utilcode.util.SPUtils;
import com.prt.base.common.DeviceInfo;
import com.prt.base.utils.KLogger;
import com.prt.base.utils.RecycleUtils;
import com.prt.print.data.bean.PrinterStatus;
import java.util.List;
import org.apache.poi.ss.formula.ptg.Ptg;

/* JADX INFO: loaded from: classes6.dex */
public class ESCPOLIPrinterManager extends BasePrintManager {
    private int mapDensity(int density) {
        if (density == 1) {
            return 35;
        }
        if (density == 2) {
            return 55;
        }
        if (density != 3) {
            return density != 4 ? -1 : 95;
        }
        return 75;
    }

    private int mapEscCompressType(int deviceCompress) {
        if (deviceCompress == 1) {
            return 1;
        }
        if (deviceCompress != 2) {
            return deviceCompress != 3 ? 2 : 1;
        }
        return 0;
    }

    @Override // com.prt.print.utils.printer.IPrintManager
    public int setPaperLearn(DeviceInfo deviceInfo, int paperType) {
        try {
            return HPRTPrinterHelper.setGapDetectCPCL();
        } catch (Exception unused) {
            return -1;
        }
    }

    @Override // com.prt.print.utils.printer.IPrintManager
    public boolean setPaperType(DeviceInfo deviceInfo, int paperType) {
        return HPRTPrinterHelper.setPrintPageType(paperType);
    }

    @Override // com.prt.print.utils.printer.IPrintManager
    public synchronized boolean setDensity(DeviceInfo deviceInfo, int density) {
        if (density <= 0 || density > 4) {
            return true;
        }
        return HPRTPrinterHelper.setPollPrintDensity(mapDensity(density));
    }

    private void setImageAlignment(int alignment) {
        try {
            if (alignment == 0) {
                HPRTPrinterHelper.SetJustification(1);
            } else if (alignment == 1) {
                HPRTPrinterHelper.SetJustification(2);
            } else {
                HPRTPrinterHelper.SetJustification(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:35:0x0089, code lost:
    
        r5 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x008a, code lost:
    
        HPRTAndroidSDK.HPRTPrinterHelper.setConnectState(0);
        com.prt.base.utils.RecycleUtils.recycle(r8);
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x0094, code lost:
    
        r10 = r5;
     */
    @Override // com.prt.print.utils.printer.BasePrintManager, com.prt.print.utils.printer.IPrintManager
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public synchronized int printBitmap(DeviceInfo deviceInfo, Bitmap bitmap, int perCount, int alignment, boolean isLabel, boolean containImg, boolean keepPrint, int typeA4) {
        int i;
        int iPrintBitmap;
        i = -1;
        try {
            if (deviceInfo.subcontract == 0) {
                HPRTPrinterHelper.WriteData(new byte[]{27, Ptg.CLASS_ARRAY});
            }
            boolean z = SPUtils.getInstance().getBoolean("havaSavePrinterSetting", false);
            boolean z2 = SPUtils.getInstance().getBoolean("havaChangePrintDirection", false);
            if (deviceInfo.isT260() && (!z || !z2)) {
                alignment = -1;
            }
            setImageAlignment(alignment);
            Bitmap bitmapCopy = bitmap.copy(Bitmap.Config.ARGB_8888, false);
            int i2 = 0;
            while (true) {
                if (i2 >= perCount) {
                    break;
                }
                if (this.stopPrint) {
                    this.stopPrint = false;
                    break;
                }
                PrinterStatus printerStatus = new PrinterStatus(deviceInfo.statusValue);
                if (deviceInfo.isM1() && printerStatus.isLocateFail()) {
                    return -1;
                }
                if (deviceInfo.subcontract > 0) {
                    iPrintBitmap = HPRTPrinterHelper.printBitmapPackage(bitmap, 0, (deviceInfo.subcontract / 2) * 1024);
                } else {
                    iPrintBitmap = HPRTPrinterHelper.printBitmap(bitmapCopy, 0, mapEscCompressType(deviceInfo.compress), 200);
                }
                if (isLabel) {
                    HPRTPrinterHelper.setPollForm(960);
                } else {
                    HPRTPrinterHelper.setPrintFeed(90);
                }
                HPRTPrinterHelper.ReadDataMillisecond(1);
                if (iPrintBitmap <= 0) {
                    int i3 = -1;
                    break;
                }
                i2++;
            }
        } catch (Exception unused) {
        }
        return i;
    }

    /* JADX WARN: Code restructure failed: missing block: B:25:0x0096, code lost:
    
        r0 = 0;
     */
    @Override // com.prt.print.utils.printer.BasePrintManager, com.prt.print.utils.printer.IPrintManager
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int printDoubleColorBitmap(DeviceInfo deviceInfo, List<Bitmap> bitmaps, int x, int y, int type, int density, int perCount, int alignment, boolean isLabel, boolean keepPrint) {
        int i;
        int iPrintDoubleColorBitmap;
        try {
            HPRTPrinterHelper.WriteData(new byte[]{27, Ptg.CLASS_ARRAY});
            setImageAlignment(alignment);
            Bitmap bitmapCopy = bitmaps.get(1).copy(Bitmap.Config.ARGB_8888, false);
            Bitmap bitmapCopy2 = bitmaps.get(2).copy(Bitmap.Config.ARGB_8888, false);
            int i2 = 0;
            while (true) {
                if (i2 >= perCount) {
                    break;
                }
                if (this.stopPrint) {
                    this.stopPrint = false;
                    break;
                }
                if (deviceInfo.isNew1()) {
                    HPRTPrinterHelperV1.printDoubleColor(bitmapCopy, bitmapCopy2);
                    iPrintDoubleColorBitmap = 0;
                } else if (2 != deviceInfo.compress) {
                    if (deviceInfo.compressModel == 1) {
                        iPrintDoubleColorBitmap = HPRTPrinterHelper.printDoubleColorBitmap(bitmapCopy2, bitmapCopy, x, y, type, 2, density, 1);
                    } else {
                        iPrintDoubleColorBitmap = HPRTPrinterHelper.printDoubleColorBitmap(bitmapCopy2, bitmapCopy, x, y, type, 1, density, 1);
                    }
                } else {
                    iPrintDoubleColorBitmap = HPRTPrinterHelper.printDoubleColorBitmap(bitmapCopy2, bitmapCopy, x, y, type, 0, density, 1);
                }
                if (isLabel) {
                    HPRTPrinterHelper.setPollForm(960);
                } else {
                    HPRTPrinterHelper.setPrintFeed(90);
                }
                HPRTPrinterHelper.ReadDataMillisecond(1000);
                if (iPrintDoubleColorBitmap <= 0) {
                    i = -1;
                    break;
                }
                i2++;
            }
            HPRTPrinterHelper.setConnectState(0);
            for (int i3 = 0; i3 < bitmaps.size(); i3++) {
                RecycleUtils.recycle(bitmaps.get(i3));
            }
            return i;
        } catch (Exception e) {
            KLogger.e(e.getMessage());
            return -1;
        }
    }
}
