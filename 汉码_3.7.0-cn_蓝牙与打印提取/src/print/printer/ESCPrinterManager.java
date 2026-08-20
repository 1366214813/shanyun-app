package com.prt.print.utils.printer;

import HPRTAndroidSDK.HPRTPrinterHelper;
import android.graphics.Bitmap;
import com.prt.base.common.DeviceInfo;
import com.prt.base.utils.KLogger;
import com.prt.base.utils.RecycleUtils;
import java.util.List;
import org.apache.poi.ss.formula.ptg.BoolPtg;
import org.apache.poi.ss.formula.ptg.Ptg;

/* JADX INFO: loaded from: classes6.dex */
public class ESCPrinterManager extends BasePrintManager {
    public int mapEscCompressType(int deviceCompress) {
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
            return HPRTPrinterHelper.setGapDetectESC();
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
        int iWriteData = -1;
        if (density == -1) {
            return true;
        }
        try {
            iWriteData = HPRTPrinterHelper.WriteData(new byte[]{BoolPtg.sid, 121, (byte) Math.floor(density - 1)});
            HPRTPrinterHelper.setConnectState(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iWriteData > 0;
    }

    protected void setImageAlignment(int alignment) {
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

    /* JADX WARN: Code restructure failed: missing block: B:39:0x008f, code lost:
    
        com.prt.base.utils.RecycleUtils.recycle(r6);
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x0096, code lost:
    
        r11 = r5;
     */
    @Override // com.prt.print.utils.printer.BasePrintManager, com.prt.print.utils.printer.IPrintManager
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public synchronized int printBitmap(DeviceInfo deviceInfo, Bitmap bitmap, int perCount, int alignment, boolean isLabel, boolean containImg, boolean keepPrint, int typeA4) {
        int i;
        int i2;
        int paperLength;
        i = -1;
        try {
            HPRTPrinterHelper.WriteData(new byte[]{27, Ptg.CLASS_ARRAY});
            setImageAlignment(alignment);
            Bitmap bitmapCopy = bitmap.copy(Bitmap.Config.ARGB_8888, false);
            int i3 = deviceInfo.isA300E() ? 8192 : 800;
            int i4 = 0;
            while (true) {
                if (i4 >= perCount) {
                    i2 = 0;
                    break;
                }
                int iPrintBitmap = HPRTPrinterHelper.printBitmap(bitmapCopy, 0, mapEscCompressType(deviceInfo.compress), deviceInfo.subcontract == 0 ? i3 : deviceInfo.subcontract * 1024);
                HPRTPrinterHelper.INSTANCE.ReadDataMillisecond(true, 100);
                if (iPrintBitmap <= 0) {
                    i2 = -1;
                    break;
                }
                if (deviceInfo.isRfid() && deviceInfo.statusValue != 0) {
                    return 0;
                }
                if (deviceInfo.printerBean != null && (paperLength = deviceInfo.printerBean.getPaperLength()) != 0 && !isLabel) {
                    HPRTPrinterHelper.setESCFeed(paperLength);
                }
                if (isLabel) {
                    HPRTPrinterHelper.WriteData(new byte[]{12});
                    if ("J70".equalsIgnoreCase(deviceInfo.printerName) || "J60".equalsIgnoreCase(deviceInfo.printerName)) {
                        HPRTPrinterHelper.WriteData(new byte[]{BoolPtg.sid, 86, 65, 0});
                    }
                    HPRTPrinterHelper.setConnectState(0);
                }
                i4++;
            }
        } catch (Exception e) {
            KLogger.e(e.getMessage());
        }
        return i;
    }

    @Override // com.prt.print.utils.printer.BasePrintManager, com.prt.print.utils.printer.IPrintManager
    public int printDoubleColorBitmap(DeviceInfo deviceInfo, List<Bitmap> bitmaps, int x, int y, int type, int density, int perCount, int alignment, boolean isLabel, boolean keepPrint) {
        int i;
        int iPrintDoubleColorBitmap;
        int paperLength;
        try {
            HPRTPrinterHelper.WriteData(new byte[]{27, Ptg.CLASS_ARRAY});
        } catch (Exception e) {
            e = e;
        }
        try {
            setImageAlignment(alignment);
            Bitmap bitmapCopy = bitmaps.get(1).copy(Bitmap.Config.ARGB_8888, false);
            Bitmap bitmapCopy2 = bitmaps.get(2).copy(Bitmap.Config.ARGB_8888, false);
            int i2 = 0;
            while (true) {
                if (i2 >= perCount) {
                    i = 0;
                    break;
                }
                if (2 != deviceInfo.compress) {
                    if (deviceInfo.compressModel == 1) {
                        iPrintDoubleColorBitmap = HPRTPrinterHelper.printDoubleColorBitmap(bitmapCopy2, bitmapCopy, x, y, type, 1, density, 1);
                    } else {
                        iPrintDoubleColorBitmap = HPRTPrinterHelper.printDoubleColorBitmap(bitmapCopy2, bitmapCopy, x, y, type, 2, density, 1);
                    }
                } else {
                    iPrintDoubleColorBitmap = HPRTPrinterHelper.printDoubleColorBitmap(bitmapCopy2, bitmapCopy, x, y, type, 0, density, 1);
                }
                if (iPrintDoubleColorBitmap <= 0) {
                    i = -1;
                    break;
                }
                if (deviceInfo.printerBean != null && (paperLength = deviceInfo.printerBean.getPaperLength()) != 0 && !isLabel) {
                    HPRTPrinterHelper.setESCFeed(paperLength);
                }
                if (isLabel) {
                    HPRTPrinterHelper.WriteData(new byte[]{12});
                }
                HPRTPrinterHelper.setConnectState(0);
                i2++;
            }
            for (int i3 = 0; i3 < bitmaps.size(); i3++) {
                RecycleUtils.recycle(bitmaps.get(i3));
            }
            return i;
        } catch (Exception e2) {
            e = e2;
            KLogger.e(e.getMessage());
            return -1;
        }
    }
}
