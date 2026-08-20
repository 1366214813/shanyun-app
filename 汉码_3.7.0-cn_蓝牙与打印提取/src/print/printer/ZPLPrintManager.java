package com.prt.print.utils.printer;

import HPRTAndroidSDK.HPRTPrinterHelper;
import HPRTAndroidSDK.dbcolor.PrinterDataGenerator;
import android.graphics.Bitmap;
import android.text.TextUtils;
import com.blankj.utilcode.util.LogUtils;
import com.prt.base.common.DeviceHelper;
import com.prt.base.common.DeviceInfo;
import com.prt.base.utils.AppUtils;
import com.prt.base.utils.KLogger;
import com.prt.base.utils.RecycleUtils;
import com.prt.provider.common.App;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/* JADX INFO: loaded from: classes6.dex */
class ZPLPrintManager extends BasePrintManager {
    @Override // com.prt.print.utils.printer.IPrintManager
    public int setPaperLearn(DeviceInfo deviceInfo, int paperType) {
        return 0;
    }

    ZPLPrintManager() {
    }

    @Override // com.prt.print.utils.printer.IPrintManager
    public boolean setPaperType(DeviceInfo deviceInfo, int paperType) {
        return HPRTPrinterHelper.setPrintPageType(paperType);
    }

    @Override // com.prt.print.utils.printer.IPrintManager
    public synchronized boolean setDensity(DeviceInfo deviceInfo, int density) {
        int i;
        if (density == -1 || density == 0) {
            return true;
        }
        if (density == 1) {
            i = 8;
        } else if (density == 2) {
            i = 15;
        } else if (density == 3) {
            i = 22;
        } else {
            if (density != 4) {
                return true;
            }
            i = 30;
        }
        try {
            HPRTPrinterHelper.WriteData(("^SD" + i + "\r\n").getBytes());
            HPRTPrinterHelper.setConnectState(0);
            return true;
        } catch (Exception unused) {
            return false;
        }
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
    public synchronized int printBitmap(DeviceInfo deviceInfo, Bitmap bitmap, int perCount, int alignment, boolean isLabel, boolean containImg, boolean keepPrint, int typeA4) {
        int i;
        try {
            i = 0;
            Bitmap bitmapCopy = bitmap.copy(Bitmap.Config.ARGB_8888, false);
            int imageStartX = getImageStartX(deviceInfo.printerHeadWidth, bitmap.getWidth(), alignment);
            if (imageStartX < 0 && DeviceHelper.needSendOutOfSizeTip(keepPrint, deviceInfo, bitmapCopy.getWidth())) {
                return -3;
            }
            HPRTPrinterHelper.printBitmapLZO_ZPL(bitmapCopy, 0);
            HPRTPrinterHelper.start();
            if (!isLabel) {
                String str = deviceInfo.printerName;
                HPRTPrinterHelper.WriteData(("^LL" + bitmap.getHeight() + "\r\n").getBytes());
            }
            HPRTPrinterHelper.setXY(String.valueOf(imageStartX), "0");
            HPRTPrinterHelper.WriteData(("^PW" + deviceInfo.printerHeadWidth + "\r\n").getBytes());
            HPRTPrinterHelper.end(perCount);
            RecycleUtils.recycle(bitmapCopy, bitmap);
        } catch (Exception e) {
            KLogger.e(e.getMessage());
            i = -1;
        }
        return i;
    }

    @Override // com.prt.print.utils.printer.BasePrintManager, com.prt.print.utils.printer.IPrintManager
    public int printDoubleColorBitmap(DeviceInfo deviceInfo, List<Bitmap> bitmaps, int x, int y, int type, int density, int perCount, int alignment, boolean isLabel, boolean keepPrint) {
        try {
            Bitmap bitmap = bitmaps.get(0);
            Bitmap bitmapCopy = bitmaps.get(1).copy(Bitmap.Config.ARGB_8888, false);
            Bitmap bitmapCopy2 = bitmaps.get(2).copy(Bitmap.Config.ARGB_8888, false);
            int imageStartX = getImageStartX(deviceInfo.printerHeadWidth, bitmapCopy.getWidth(), alignment);
            if (imageStartX < 0 && DeviceHelper.needSendOutOfSizeTip(keepPrint, deviceInfo, bitmapCopy.getWidth())) {
                return -3;
            }
            if (App.INSTANCE.useEscTwoColorPrint()) {
                byte[] bArrGenerateSetPositionCommand = PrinterDataGenerator.generateSetPositionCommand(imageStartX / 2);
                byte[] bArrGenerateSaveParametersCommand = PrinterDataGenerator.generateSaveParametersCommand();
                HPRTPrinterHelper.WriteData(bArrGenerateSetPositionCommand);
                HPRTPrinterHelper.ReadData(1);
                HPRTPrinterHelper.WriteData(bArrGenerateSaveParametersCommand);
                HPRTPrinterHelper.ReadData(1);
                HPRTPrinterHelper.WriteData(PrinterDataGenerator.generateGetPositionCommand(2));
                HPRTPrinterHelper.ReadData(1);
                byte[] bArrGeneratePrinterData = PrinterDataGenerator.generatePrinterData(bitmap, imageStartX);
                Thread.sleep(100L);
                HPRTPrinterHelper.WriteData(bArrGeneratePrinterData);
            } else {
                HPRTPrinterHelper.printBitmapLZO_ZPL(bitmap, 0);
                HPRTPrinterHelper.start();
                if (!isLabel) {
                    String str = deviceInfo.printerName;
                    if (!TextUtils.isEmpty(str) && (str.contains("IT4S") || str.contains("E430B"))) {
                        HPRTPrinterHelper.WriteData(("^LL" + bitmap.getHeight() + "\r\n").getBytes());
                    }
                }
                HPRTPrinterHelper.setXY(String.valueOf(imageStartX), "0");
                HPRTPrinterHelper.WriteData(("^PW" + deviceInfo.printerHeadWidth + "\r\n").getBytes());
                HPRTPrinterHelper.end(perCount);
            }
            RecycleUtils.recycle(bitmaps.get(0), bitmapCopy, bitmapCopy2);
            return 0;
        } catch (Exception e) {
            KLogger.e(e.getMessage());
            return -1;
        }
    }

    private void saveByteArrayToInternalStorage(byte[] data, String fileName) {
        try {
            FileOutputStream fileOutputStreamOpenFileOutput = AppUtils.getContext().openFileOutput(fileName, 0);
            fileOutputStreamOpenFileOutput.write(data);
            fileOutputStreamOpenFileOutput.close();
            File file = new File(AppUtils.getContext().getFilesDir(), fileName);
            LogUtils.d("SaveFile", "文件大小: " + file.length() + " bytes");
            StringBuilder sb = new StringBuilder("文件路径: ");
            sb.append(file.getAbsolutePath());
            LogUtils.d("SaveFile", sb.toString());
        } catch (IOException e) {
            LogUtils.e("SaveFile", "保存失败: " + e.getMessage());
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:36:0x003b A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public byte[] getAssetBytes(String fileName) {
        ByteArrayOutputStream byteArrayOutputStream;
        byte[] bArr;
        try {
            InputStream inputStreamOpen = AppUtils.getContext().getAssets().open(fileName);
            try {
                byteArrayOutputStream = new ByteArrayOutputStream();
                try {
                    bArr = new byte[1024];
                } finally {
                }
            } finally {
            }
            while (true) {
                int i = inputStreamOpen.read(bArr);
                if (i == -1) {
                    break;
                }
                byteArrayOutputStream.write(bArr, 0, i);
            }
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            if (inputStreamOpen != null) {
                inputStreamOpen.close();
            }
            return byteArray;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
