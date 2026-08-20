package com.prt.print.utils.printer;

import HPRTAndroidSDK.HPRTPrinterHelper;
import HPRTAndroidSDK.dbcolor.PrinterDataGenerator;
import android.graphics.Bitmap;
import android.util.Log;
import com.lee.editorpanel.utils.ImageUtils;
import com.prt.base.common.DeviceHelper;
import com.prt.base.common.DeviceInfo;
import com.prt.base.utils.KLogger;
import com.prt.base.utils.RecycleUtils;
import com.prt.provider.common.App;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes6.dex */
class TSPLPrinterManager extends BasePrintManager {
    private int convertCompressModel(int sourceModel) {
        return (sourceModel == 3 || sourceModel != 4) ? 3 : 16;
    }

    TSPLPrinterManager() {
    }

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
        int i = (density * 4) - 1;
        if (i < 0) {
            return true;
        }
        try {
            iWriteData = HPRTPrinterHelper.WriteData(("DENSITY " + i + "\r\n").getBytes());
            HPRTPrinterHelper.setConnectState(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iWriteData > 0;
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

    @Override // com.prt.print.utils.printer.BasePrintManager, com.prt.print.utils.printer.IPrintManager
    public synchronized int printBitmap(DeviceInfo deviceInfo, Bitmap bitmap, int i, int i2, boolean z, boolean z2, boolean z3, int i3) {
        int i4;
        String strValueOf;
        String strValueOf2;
        int continuousLongDotCount;
        int i5;
        long j;
        long jCurrentTimeMillis = System.currentTimeMillis();
        try {
            strValueOf = String.valueOf((int) (((double) (deviceInfo.printerHeadWidth / deviceInfo.dpm)) + 0.5d));
            strValueOf2 = String.valueOf((int) (((double) (bitmap.getHeight() / deviceInfo.dpm)) + 0.5d));
            continuousLongDotCount = (!deviceInfo.isTsplContinuousLong() || deviceInfo.printerBean == null) ? 200 : (int) (deviceInfo.printerBean.getContinuousLongDotCount() / deviceInfo.dpm);
        } catch (Exception e) {
            e = e;
            i4 = -1;
        }
        if (Integer.parseInt(strValueOf2) <= continuousLongDotCount || !deviceInfo.isTsplContinuousLong()) {
            int iPrintAreaSize = HPRTPrinterHelper.printAreaSize(strValueOf, strValueOf2);
            Bitmap bitmapCropWhite = (deviceInfo.isNotContinuePaper() && deviceInfo.needCheckRoundInchLabel()) ? ImageUtils.cropWhite(bitmap, false, false, false, true) : bitmap;
            if (iPrintAreaSize >= 0) {
                int iConvertCompressModel = 2 != deviceInfo.compress ? convertCompressModel(deviceInfo.compressModel) : 0;
                int imageStartX = getImageStartX(deviceInfo.printerHeadWidth, bitmapCropWhite.getWidth(), i2);
                if (imageStartX < 0 && DeviceHelper.needSendOutOfSizeTip(z3, deviceInfo, bitmapCropWhite.getWidth())) {
                    return -3;
                }
                HPRTPrinterHelper.CLS();
                if (deviceInfo.printerBean != null && deviceInfo.printerBean.getSupportTear()) {
                    HPRTPrinterHelper.WriteData("SET TEAR ON\r\n".getBytes());
                }
                if ("J70".equalsIgnoreCase(deviceInfo.printerName) || "J60".equalsIgnoreCase(deviceInfo.printerName)) {
                    HPRTPrinterHelper.WriteData("SET CUTTER ON\r\n".getBytes());
                }
                Log.e("PrintTime", "图片打印前->" + (System.currentTimeMillis() - jCurrentTimeMillis) + "mm");
                int iPrintImage = HPRTPrinterHelper.printImage(String.valueOf(imageStartX), "0", bitmapCropWhite, true, iConvertCompressModel, z2 ? 1 : 0);
                try {
                    Log.e("PrintTime", "图片打印时间->" + (System.currentTimeMillis() - jCurrentTimeMillis) + "mm");
                    if (iPrintImage == -2) {
                        return -2;
                    }
                    i4 = (iPrintImage <= 0 || HPRTPrinterHelper.Print(String.valueOf(i), "1") <= 0) ? -1 : 0;
                    try {
                        RecycleUtils.recycle(bitmap, bitmapCropWhite);
                        Log.e("PrintTime", "图片打印时间2->" + (System.currentTimeMillis() - jCurrentTimeMillis) + "mm");
                    } catch (Exception e2) {
                        e = e2;
                        KLogger.e(e.getMessage());
                    }
                } catch (Exception e3) {
                    e = e3;
                    i4 = iPrintImage;
                }
                KLogger.e(e.getMessage());
            } else {
                i4 = -1;
            }
        } else {
            int i6 = -1;
            int i7 = 0;
            while (i7 < i) {
                try {
                    List<Bitmap> listCutBitmap = ImageUtils.cutBitmap(bitmap, (int) (continuousLongDotCount * deviceInfo.dpm));
                    for (Bitmap bitmapCropWhite2 : listCutBitmap) {
                        try {
                            i5 = i6;
                        } catch (Exception e4) {
                            e = e4;
                            i5 = i6;
                        }
                        try {
                            int i8 = i7;
                            int iPrintAreaSize2 = HPRTPrinterHelper.printAreaSize(strValueOf, String.valueOf((int) (((double) (bitmapCropWhite2.getHeight() / deviceInfo.dpm)) + 0.5d)));
                            if (deviceInfo.isNotContinuePaper() && deviceInfo.needCheckRoundInchLabel()) {
                                bitmapCropWhite2 = ImageUtils.cropWhite(bitmapCropWhite2, false, false, false, true);
                            }
                            Bitmap bitmap2 = bitmapCropWhite2;
                            if (iPrintAreaSize2 >= 0) {
                                int iConvertCompressModel2 = 2 != deviceInfo.compress ? convertCompressModel(deviceInfo.compressModel) : 0;
                                int imageStartX2 = getImageStartX(deviceInfo.printerHeadWidth, bitmap2.getWidth(), i2);
                                if (imageStartX2 < 0 && DeviceHelper.needSendOutOfSizeTip(z3, deviceInfo, bitmap2.getWidth())) {
                                    return -3;
                                }
                                HPRTPrinterHelper.CLS();
                                if (deviceInfo.printerBean != null && deviceInfo.printerBean.getSupportTear()) {
                                    HPRTPrinterHelper.WriteData("SET TEAR ON\r\n".getBytes());
                                }
                                if ("J70".equalsIgnoreCase(deviceInfo.printerName) || "J60".equalsIgnoreCase(deviceInfo.printerName)) {
                                    HPRTPrinterHelper.WriteData("SET CUTTER ON\r\n".getBytes());
                                }
                                StringBuilder sb = new StringBuilder();
                                sb.append("图片打印前->");
                                j = jCurrentTimeMillis;
                                sb.append(System.currentTimeMillis() - j);
                                sb.append("mm");
                                Log.e("PrintTime", sb.toString());
                                int iPrintImage2 = HPRTPrinterHelper.printImage(String.valueOf(imageStartX2), "0", bitmap2, true, iConvertCompressModel2, z2 ? 1 : 0);
                                Log.e("PrintTime", "图片打印时间->" + (System.currentTimeMillis() - j) + "mm");
                                if (iPrintImage2 == -2) {
                                    return -2;
                                }
                                int iPrint = HPRTPrinterHelper.Print(String.valueOf(1), "1");
                                if (iPrintImage2 > 0 && iPrint > 0) {
                                    i6 = 0;
                                }
                                i7 = i8;
                                jCurrentTimeMillis = j;
                            } else {
                                j = jCurrentTimeMillis;
                            }
                            i6 = i5;
                            i7 = i8;
                            jCurrentTimeMillis = j;
                        } catch (Exception e5) {
                            e = e5;
                            i4 = i5;
                            KLogger.e(e.getMessage());
                            return i4;
                        }
                    }
                    int i9 = i6;
                    int i10 = i7;
                    long j2 = jCurrentTimeMillis;
                    Iterator<Bitmap> it2 = listCutBitmap.iterator();
                    while (it2.hasNext()) {
                        RecycleUtils.recycle(it2.next());
                    }
                    i7 = i10 + 1;
                    i6 = i9;
                    jCurrentTimeMillis = j2;
                } catch (Exception e6) {
                    e = e6;
                    i4 = i6;
                }
            }
            i4 = i6;
        }
        return i4;
    }

    @Override // com.prt.print.utils.printer.BasePrintManager, com.prt.print.utils.printer.IPrintManager
    public int printDoubleColorBitmap(DeviceInfo deviceInfo, List<Bitmap> bitmaps, int x, int y, int type, int density, int perCount, int alignment, boolean isLabel, boolean keepPrint) {
        int iPrintDoubleColorBitmap;
        int i = -1;
        try {
            try {
                if (bitmaps.size() < 2) {
                    return printBitmap(deviceInfo, bitmaps.get(0), perCount, alignment, isLabel, false, keepPrint, 0);
                }
                Bitmap bitmap = bitmaps.get(0);
                Bitmap bitmapCopy = bitmaps.get(1).copy(Bitmap.Config.ARGB_8888, false);
                Bitmap bitmapCopy2 = bitmaps.get(2).copy(Bitmap.Config.ARGB_8888, false);
                if (HPRTPrinterHelper.printAreaSize(String.valueOf((int) (((double) (deviceInfo.printerHeadWidth / deviceInfo.dpm)) + 0.5d)), String.valueOf((int) (((double) (bitmapCopy.getHeight() / deviceInfo.dpm)) + 0.5d))) >= 0) {
                    if (2 != deviceInfo.compress) {
                        convertCompressModel(deviceInfo.compressModel);
                    }
                    int imageStartX = getImageStartX(deviceInfo.printerHeadWidth, bitmapCopy.getWidth(), alignment);
                    if (imageStartX < 0 && DeviceHelper.needSendOutOfSizeTip(keepPrint, deviceInfo, bitmapCopy.getWidth())) {
                        return -3;
                    }
                    if (App.INSTANCE.useEscTwoColorPrint()) {
                        byte[] bArrGenerateSetPositionCommand = PrinterDataGenerator.generateSetPositionCommand(imageStartX / 2);
                        byte[] bArrGenerateSaveParametersCommand = PrinterDataGenerator.generateSaveParametersCommand();
                        HPRTPrinterHelper.WriteData(bArrGenerateSetPositionCommand);
                        HPRTPrinterHelper.WriteData(bArrGenerateSaveParametersCommand);
                        HPRTPrinterHelper.ReadData(1);
                        HPRTPrinterHelper.WriteData(PrinterDataGenerator.generateGetPositionCommand(2));
                        HPRTPrinterHelper.ReadData(1);
                        iPrintDoubleColorBitmap = HPRTPrinterHelper.WriteData(PrinterDataGenerator.generatePrinterData(bitmap, imageStartX));
                    } else {
                        HPRTPrinterHelper.CLS();
                        iPrintDoubleColorBitmap = HPRTPrinterHelper.printDoubleColorBitmap(bitmapCopy2, bitmapCopy, x, y, type, 0, density, perCount);
                    }
                    if (iPrintDoubleColorBitmap == -2) {
                        return -2;
                    }
                    int iPrint = HPRTPrinterHelper.Print(String.valueOf(perCount), "1");
                    if (iPrintDoubleColorBitmap > 0 && iPrint > 0) {
                        i = 0;
                    }
                    RecycleUtils.recycle(bitmaps.get(0), bitmapCopy, bitmapCopy2);
                }
                return i;
            } catch (Exception e) {
                e = e;
            }
        } catch (Exception e2) {
            e = e2;
        }
        KLogger.e(e.getMessage());
        return -1;
    }
}
