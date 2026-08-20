package com.prt.print.utils.printer;

import HPRTAndroidSDK.HPRTPrinterHelper;
import android.graphics.Bitmap;
import com.prt.base.common.DeviceHelper;
import com.prt.base.common.DeviceInfo;
import com.prt.base.utils.KLogger;
import com.prt.base.utils.RecycleUtils;
import java.util.List;

/* JADX INFO: loaded from: classes6.dex */
public class CPCLPrintManager extends BasePrintManager {
    protected static final int DENSITY_LEVEL_1 = 10;
    protected static final int DENSITY_LEVEL_2 = 100;
    protected static final int DENSITY_LEVEL_3 = 200;
    protected static final int DENSITY_LEVEL_4 = 300;
    protected static final int PRINT_DATA_OUT_OF_COUNT = -3;
    protected static final int PRINT_SUCCESS = 0;
    protected int density = -1;

    private int mapCpclCompressType(int deviceCompress) {
        if (deviceCompress == 1) {
            return 2;
        }
        if (deviceCompress != 2) {
            return deviceCompress != 3 ? 1 : 2;
        }
        return 0;
    }

    protected int mapDensity(int density) {
        if (density == 1) {
            return 10;
        }
        if (density == 2) {
            return 100;
        }
        if (density != 3) {
            return density != 4 ? -1 : 300;
        }
        return 200;
    }

    @Override // com.prt.print.utils.printer.IPrintManager
    public int setPaperLearn(DeviceInfo deviceInfo, int paperType) {
        return HPRTPrinterHelper.setGapDetectCPCL();
    }

    @Override // com.prt.print.utils.printer.IPrintManager
    public boolean setPaperType(DeviceInfo deviceInfo, int paperType) {
        return HPRTPrinterHelper.setPrintPageType(paperType);
    }

    @Override // com.prt.print.utils.printer.IPrintManager
    public boolean setDensity(DeviceInfo deviceInfo, int density) {
        this.density = density;
        return true;
    }

    @Override // com.prt.print.utils.printer.BasePrintManager, com.prt.print.utils.printer.IPrintManager
    public int printBitmap(DeviceInfo deviceInfo, Bitmap bitmap, int perCount, int alignment, boolean isLabel, boolean containImg, boolean keepPrint, int typeA4) {
        Exception exc;
        int i = -1;
        try {
            Bitmap bitmapCopy = bitmap.copy(Bitmap.Config.ARGB_8888, false);
            int imageStartX = getImageStartX(deviceInfo.printerHeadWidth, bitmap.getWidth(), alignment);
            if (imageStartX < 0 && DeviceHelper.needSendOutOfSizeTip(keepPrint, deviceInfo, bitmapCopy.getWidth())) {
                return -3;
            }
            int iPrintBitmapCPCL = HPRTPrinterHelper.printBitmapCPCL(bitmapCopy, imageStartX, 0, 0, mapCpclCompressType(deviceInfo.compress), deviceInfo.printerHeadWidth, mapDensity(this.density), perCount);
            try {
                RecycleUtils.recycle(bitmapCopy, bitmap);
                if (iPrintBitmapCPCL == -3) {
                    return -2;
                }
                return iPrintBitmapCPCL == 0 ? 0 : -1;
            } catch (Exception e) {
                i = iPrintBitmapCPCL;
                exc = e;
            }
        } catch (Exception e2) {
            exc = e2;
        }
        KLogger.e(exc.getMessage());
        return i;
    }

    @Override // com.prt.print.utils.printer.BasePrintManager, com.prt.print.utils.printer.IPrintManager
    public int printDoubleColorBitmap(DeviceInfo deviceInfo, List<Bitmap> bitmaps, int x, int y, int type, int density, int perCount, int alignment, boolean isLabel, boolean keepPrint) {
        int i = -1;
        try {
            Bitmap bitmapCopy = bitmaps.get(1).copy(Bitmap.Config.ARGB_8888, false);
            Bitmap bitmapCopy2 = bitmaps.get(2).copy(Bitmap.Config.ARGB_8888, false);
            int imageStartX = getImageStartX(deviceInfo.printerHeadWidth, bitmapCopy.getWidth(), alignment);
            if (imageStartX < 0 && DeviceHelper.needSendOutOfSizeTip(keepPrint, deviceInfo, bitmapCopy.getWidth())) {
                return -3;
            }
            int iPrintDoubleColorBitmap = HPRTPrinterHelper.printDoubleColorBitmap(bitmapCopy2, bitmapCopy, imageStartX, y, type, mapCpclCompressType(deviceInfo.compress), mapDensity(density), perCount);
            try {
                RecycleUtils.recycle(bitmaps.get(0), bitmapCopy2, bitmapCopy);
                if (iPrintDoubleColorBitmap == -3) {
                    return -2;
                }
                return iPrintDoubleColorBitmap == 0 ? 0 : -1;
            } catch (Exception e) {
                e = e;
                i = iPrintDoubleColorBitmap;
            }
        } catch (Exception e2) {
            e = e2;
        }
        KLogger.e(e.getMessage());
        return i;
    }

    protected int getImageStartX(int printHeadWidth, int bitmapWidth, int alignment) {
        if (alignment == 0) {
            return (printHeadWidth - bitmapWidth) / 2;
        }
        if (alignment != 1) {
            return 0;
        }
        return printHeadWidth - bitmapWidth;
    }
}
