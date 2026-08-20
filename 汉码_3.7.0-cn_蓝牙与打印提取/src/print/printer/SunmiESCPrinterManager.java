package com.prt.print.utils.printer;

import HPRTAndroidSDK.HPRTPrinterHelper;
import android.graphics.Bitmap;
import com.prt.base.common.DeviceInfo;
import com.prt.base.utils.KLogger;
import java.io.ByteArrayOutputStream;
import java.util.List;
import org.apache.poi.ss.formula.ptg.BoolPtg;
import org.apache.poi.ss.formula.ptg.Ptg;

/* JADX INFO: loaded from: classes6.dex */
public class SunmiESCPrinterManager extends BasePrintManager {
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
            iWriteData = HPRTPrinterHelper.WriteData(new byte[]{BoolPtg.sid, 40, 69, 2, 0, 17, (byte) Math.max(1, Math.min(8, density))});
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

    @Override // com.prt.print.utils.printer.BasePrintManager, com.prt.print.utils.printer.IPrintManager
    public synchronized int printBitmap(DeviceInfo deviceInfo, Bitmap bitmap, int perCount, int alignment, boolean isLabel, boolean containImg, boolean keepPrint, int typeA4) {
        int i;
        int paperLength;
        i = -1;
        try {
            KLogger.d("ESC printBitmap: original bitmap=" + bitmap + ", width=" + bitmap.getWidth() + ", height=" + bitmap.getHeight() + ", config=" + bitmap.getConfig());
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int pixel = bitmap.getPixel(width / 2, height / 2);
            StringBuilder sb = new StringBuilder("ESC printBitmap: centerPixel=");
            sb.append(Integer.toHexString(pixel));
            KLogger.d(sb.toString());
            int i2 = (width + 7) / 8;
            byte[] bArrConvertBitmapToMonochrome = convertBitmapToMonochrome(bitmap);
            char c = 0;
            int i3 = 0;
            while (true) {
                if (i3 >= perCount) {
                    i = 0;
                    break;
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byteArrayOutputStream.write(new byte[]{27, Ptg.CLASS_ARRAY});
                int i4 = alignment != 0 ? alignment != 1 ? 0 : 2 : 1;
                byte[] bArr = new byte[3];
                bArr[c] = 27;
                bArr[1] = 97;
                bArr[2] = (byte) i4;
                byteArrayOutputStream.write(bArr);
                byteArrayOutputStream.write(new byte[]{BoolPtg.sid, 118, 48, 0});
                byteArrayOutputStream.write(new byte[]{(byte) (i2 & 255), (byte) ((i2 >> 8) & 255)});
                byteArrayOutputStream.write(new byte[]{(byte) (height & 255), (byte) ((height >> 8) & 255)});
                byteArrayOutputStream.write(bArrConvertBitmapToMonochrome);
                if (isLabel) {
                    byteArrayOutputStream.write(new byte[]{12});
                } else if (deviceInfo.printerBean != null && (paperLength = deviceInfo.printerBean.getPaperLength()) != 0) {
                    for (int i5 = 0; i5 < paperLength; i5++) {
                        byteArrayOutputStream.write(new byte[]{10});
                    }
                }
                if (deviceInfo.printerBean != null && deviceInfo.printerBean.getSupportSlice()) {
                    byteArrayOutputStream.write(new byte[]{BoolPtg.sid, 86, 65, 0});
                }
                int iWriteData = HPRTPrinterHelper.WriteData(byteArrayOutputStream.toByteArray());
                byteArrayOutputStream.close();
                HPRTPrinterHelper.setConnectState(0);
                if (iWriteData <= 0) {
                    break;
                }
                i3++;
                c = 0;
            }
        } catch (Exception e) {
            KLogger.e(e.getMessage());
        }
        return i;
    }

    private byte[] convertBitmapToMonochrome(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int i = (width + 7) / 8;
        byte[] bArr = new byte[i * height];
        int[] iArr = new int[width * height];
        bitmap.getPixels(iArr, 0, width, 0, 0, width, height);
        int i2 = 0;
        for (int i3 = 0; i3 < height; i3++) {
            for (int i4 = 0; i4 < width; i4++) {
                int i5 = iArr[(i3 * width) + i4];
                int i6 = (((((i5 >> 16) & 255) * 299) + (((i5 >> 8) & 255) * 587)) + ((i5 & 255) * 114)) / 1000;
                if (((i5 >> 24) & 255) > 0 && i6 < 128) {
                    int i7 = (i3 * i) + (i4 / 8);
                    bArr[i7] = (byte) ((1 << (7 - (i4 % 8))) | bArr[i7]);
                    i2++;
                }
            }
        }
        KLogger.d("convertBitmapToMonochrome: width=" + width + ", height=" + height + ", blackPixels=" + i2);
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
