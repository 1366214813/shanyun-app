package com.prt.print.utils.printer;

import HPRTAndroidSDK.HPRTPrinterHelper;
import android.graphics.Bitmap;
import com.prt.base.common.DeviceHelper;
import com.prt.base.common.DeviceInfo;
import com.prt.base.utils.KLogger;
import com.prt.base.utils.RecycleUtils;
import java.io.ByteArrayOutputStream;
import java.util.List;

/* JADX INFO: loaded from: classes6.dex */
public class SunmiTSPLPrinterManager extends BasePrintManager {
    @Override // com.prt.print.utils.printer.IPrintManager
    public int setPaperLearn(DeviceInfo deviceInfo, int paperType) {
        try {
            return HPRTPrinterHelper.setGapDetectTSPL();
        } catch (Exception e) {
            e.printStackTrace();
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
        if (density <= -1) {
            return true;
        }
        try {
            iWriteData = HPRTPrinterHelper.WriteData(("DENSITY " + Math.max(1, Math.min(15, density)) + "\r\n").getBytes());
            HPRTPrinterHelper.setConnectState(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iWriteData > 0;
    }

    private int getImageStartX(int printHeadWidth, int bitmapWidth, int alignment) {
        int i;
        if (alignment != 0) {
            i = alignment != 1 ? 0 : printHeadWidth - bitmapWidth;
        } else {
            i = (printHeadWidth - bitmapWidth) / 2;
        }
        return Math.max(0, i);
    }

    @Override // com.prt.print.utils.printer.BasePrintManager, com.prt.print.utils.printer.IPrintManager
    public synchronized int printBitmap(DeviceInfo deviceInfo, Bitmap bitmap, int perCount, int alignment, boolean isLabel, boolean containImg, boolean keepPrint, int typeA4) {
        int i;
        Bitmap bitmapCopy;
        int width;
        int height;
        int i2;
        int i3;
        int imageStartX;
        int i4;
        int i5 = -1;
        try {
            bitmapCopy = bitmap.copy(Bitmap.Config.ARGB_8888, false);
            width = bitmapCopy.getWidth();
            height = bitmapCopy.getHeight();
            i2 = (int) (((double) (deviceInfo.printerHeadWidth / deviceInfo.dpm)) + 0.5d);
            i3 = (int) (((double) (height / deviceInfo.dpm)) + 0.5d);
            imageStartX = getImageStartX(deviceInfo.printerHeadWidth, width, alignment);
            i4 = 1;
        } catch (Exception e) {
            KLogger.e(e.getMessage());
            i = -1;
        }
        if (imageStartX < 0 && DeviceHelper.needSendOutOfSizeTip(keepPrint, deviceInfo, width)) {
            RecycleUtils.recycle(bitmapCopy);
            return -3;
        }
        int i6 = (width + 7) / 8;
        byte[] bArrConvertBitmapToMonochrome = convertBitmapToMonochrome(bitmapCopy);
        int i7 = 0;
        int i8 = -1;
        while (true) {
            if (i7 >= perCount) {
                break;
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write("CLS\r\n".getBytes());
            byteArrayOutputStream.write("DIRECTION 0,0\r\n".getBytes());
            if (deviceInfo.density > i5) {
                byteArrayOutputStream.write(("DENSITY " + Math.max(i4, Math.min(15, deviceInfo.density)) + "\r\n").getBytes());
            }
            byteArrayOutputStream.write(("SIZE " + i2 + " mm," + i3 + " mm\r\n").getBytes());
            if (isLabel) {
                byteArrayOutputStream.write("GAP 3 mm,0 mm\n\r\n".getBytes());
            } else {
                byteArrayOutputStream.write("GAP 0,0\r\n".getBytes());
            }
            if (deviceInfo.printerBean != null && deviceInfo.printerBean.getSupportTear()) {
                byteArrayOutputStream.write("SET TEAR ON\r\n".getBytes());
            }
            byteArrayOutputStream.write("CLS\r\n".getBytes());
            byteArrayOutputStream.write(("BITMAP " + Math.max(0, imageStartX) + ",0," + i6 + "," + height + ",0,").getBytes());
            byteArrayOutputStream.write(bArrConvertBitmapToMonochrome);
            byteArrayOutputStream.write("\r\n".getBytes());
            byteArrayOutputStream.write("PRINT 1,1\r\n".getBytes());
            int iWriteData = HPRTPrinterHelper.WriteData(byteArrayOutputStream.toByteArray());
            byteArrayOutputStream.close();
            if (iWriteData <= 0) {
                i8 = -1;
                break;
            }
            i7++;
            i5 = -1;
            i4 = 1;
            i8 = 0;
        }
        RecycleUtils.recycle(bitmapCopy);
        i = i8;
        return i;
    }

    private byte[] convertBitmapToMonochrome(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int i = (width + 7) / 8;
        int i2 = i * height;
        byte[] bArr = new byte[i2];
        for (int i3 = 0; i3 < i2; i3++) {
            bArr[i3] = -1;
        }
        for (int i4 = 0; i4 < height; i4++) {
            for (int i5 = 0; i5 < width; i5++) {
                int pixel = bitmap.getPixel(i5, i4);
                int i6 = (((((pixel >> 16) & 255) * 299) + (((pixel >> 8) & 255) * 587)) + ((pixel & 255) * 114)) / 1000;
                if (((pixel >> 24) & 255) >= 128 && i6 < 128) {
                    int i7 = (i4 * i) + (i5 / 8);
                    bArr[i7] = (byte) (((byte) (~(1 << (7 - (i5 % 8))))) & bArr[i7]);
                }
            }
        }
        return bArr;
    }

    @Override // com.prt.print.utils.printer.BasePrintManager, com.prt.print.utils.printer.IPrintManager
    public int printDoubleColorBitmap(DeviceInfo deviceInfo, List<Bitmap> bitmaps, int x, int y, int type, int density, int perCount, int alignment, boolean isLabel, boolean keepPrint) {
        if (bitmaps == null || bitmaps.size() <= 0) {
            return -1;
        }
        return printBitmap(deviceInfo, bitmaps.get(0), perCount, alignment, isLabel, false, keepPrint, 0);
    }
}
