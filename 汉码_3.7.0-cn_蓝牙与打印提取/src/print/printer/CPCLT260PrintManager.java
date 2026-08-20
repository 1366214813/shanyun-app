package com.prt.print.utils.printer;

import HPRTAndroidSDK.HPRTPrinterHelper;
import android.graphics.Bitmap;
import com.prt.base.common.DeviceHelper;
import com.prt.base.common.DeviceInfo;
import com.prt.base.utils.KLogger;
import com.prt.base.utils.RecycleUtils;
import java.util.List;

/* JADX INFO: loaded from: classes6.dex */
public class CPCLT260PrintManager extends BasePrintManager {
    private static final int DENSITY_LEVEL_1 = 35;
    private static final int DENSITY_LEVEL_2 = 55;
    private static final int DENSITY_LEVEL_3 = 75;
    private static final int DENSITY_LEVEL_4 = 95;
    private static final int PRINT_DATA_OUT_OF_COUNT = -3;
    private static final int PRINT_SUCCESS = 0;
    private final byte[] densityInstruct = {27, 28, 38, 32, 86, 49, 32, 115, 101, 116, 107, 101, 121, 13, 10, 1, -53, 0, 1, -1, 27, 28, 38, 32, 86, 49, 32, 100, 111, 32, 34, 115, 97, 118, 101, 95, 112, 97, 114, 97, 109, 95, 122, 111, 110, 101, 34, 13, 10};
    private int density = -1;

    private int mapCpclCompressType(int deviceCompress) {
        if (deviceCompress == 1) {
            return 2;
        }
        if (deviceCompress != 2) {
            return deviceCompress != 3 ? 1 : 2;
        }
        return 0;
    }

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
    public int printBitmap(DeviceInfo deviceInfo, Bitmap bitmap, int i, int i2, boolean z, boolean z2, boolean z3, int i3) {
        Exception exc;
        int i4 = -1;
        try {
            if (!HPRTPrinterHelper.setPrintPageType(z ? 1 : 0)) {
                return -4;
            }
            KLogger.i("Lee", "density--> " + mapDensity(this.density));
            Bitmap bitmapCopy = bitmap.copy(Bitmap.Config.ARGB_8888, false);
            int imageStartX = getImageStartX(deviceInfo.printerHeadWidth, bitmap.getWidth(), i2);
            if (imageStartX < 0 && DeviceHelper.needSendOutOfSizeTip(z3, deviceInfo, bitmapCopy.getWidth())) {
                return -3;
            }
            this.densityInstruct[19] = (byte) mapDensity(this.density);
            HPRTPrinterHelper.WriteData(this.densityInstruct);
            int iPrintBitmapCPCL = HPRTPrinterHelper.printBitmapCPCL(bitmapCopy, imageStartX, 0, 0, mapCpclCompressType(deviceInfo.compress), -1, i);
            try {
                RecycleUtils.recycle(bitmapCopy, bitmap);
                if (iPrintBitmapCPCL == -3) {
                    return -2;
                }
                return iPrintBitmapCPCL == 0 ? 0 : -1;
            } catch (Exception e) {
                i4 = iPrintBitmapCPCL;
                exc = e;
            }
        } catch (Exception e2) {
            exc = e2;
        }
        KLogger.e(exc.getMessage());
        return i4;
    }

    @Override // com.prt.print.utils.printer.BasePrintManager, com.prt.print.utils.printer.IPrintManager
    public int printDoubleColorBitmap(DeviceInfo deviceInfo, List<Bitmap> list, int i, int i2, int i3, int i4, int i5, int i6, boolean z, boolean z2) {
        int i7 = -1;
        try {
            if (!HPRTPrinterHelper.setPrintPageType(z ? 1 : 0)) {
                return -4;
            }
            KLogger.i("Lee", "density--> " + mapDensity(i4));
            Bitmap bitmapCopy = list.get(1).copy(Bitmap.Config.ARGB_8888, false);
            Bitmap bitmapCopy2 = list.get(2).copy(Bitmap.Config.ARGB_8888, false);
            int imageStartX = getImageStartX(deviceInfo.printerHeadWidth, bitmapCopy.getWidth(), i6);
            if (imageStartX < 0 && DeviceHelper.needSendOutOfSizeTip(z2, deviceInfo, bitmapCopy.getWidth())) {
                return -3;
            }
            this.densityInstruct[19] = (byte) mapDensity(i4);
            HPRTPrinterHelper.WriteData(this.densityInstruct);
            int iPrintDoubleColorBitmap = HPRTPrinterHelper.printDoubleColorBitmap(bitmapCopy2, bitmapCopy, imageStartX, i2, i3, mapCpclCompressType(deviceInfo.compress), mapDensity(i4), i5);
            try {
                RecycleUtils.recycle(list.get(0), bitmapCopy, bitmapCopy2);
                if (iPrintDoubleColorBitmap == -3) {
                    return -2;
                }
                return iPrintDoubleColorBitmap == 0 ? 0 : -1;
            } catch (Exception e) {
                e = e;
                i7 = iPrintDoubleColorBitmap;
            }
        } catch (Exception e2) {
            e = e2;
        }
        KLogger.e(e.getMessage());
        return i7;
    }

    private int getImageStartX(int printHeadWidth, int bitmapWidth, int alignment) {
        if (alignment == 0) {
            return (printHeadWidth - bitmapWidth) / 2;
        }
        if (alignment != 1) {
            return 0;
        }
        return printHeadWidth - bitmapWidth;
    }
}
