package HPRTAndroidSDK;

import LZO_Compress.LZOCompress;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.formula.ptg.BoolPtg;
import org.mozilla.universalchardet.prober.HebrewProber;
import utils.ConvertUtil;

/* JADX INFO: loaded from: classes.dex */
public class PrinterDataCore {
    public int BitmapWidth = 0;
    public int PrintDataHeight = 0;
    public byte HalftoneMode = 1;
    public byte ScaleMode = 0;
    public byte CompressMode = 0;
    private int LBlank = 0;
    private int RBlank = 0;
    private int out_Max = 10000;
    private byte[] bb_saveHead = {27, 28, 38, 32, 86, 49, 32, 100, 111, 32, 34, 114, 101, 102, 114, 101, 115, 104, 95, 100, 97, 116, 97, 34, 13, 10};

    public byte[] PrintDataFormat(Bitmap bmp, int ptintdpi) {
        byte[] bArrCreatePrintBitmapData;
        try {
            if (this.HalftoneMode > 0) {
                bArrCreatePrintBitmapData = GetImageDataRasterMono(bmp);
            } else {
                bArrCreatePrintBitmapData = CreatePrintBitmapData(bmp);
            }
            byte b = this.CompressMode;
            if (b == 0) {
                return CompressPrintData(bArrCreatePrintBitmapData, ptintdpi);
            }
            if (b == 1) {
                return AddPrintCode(bArrCreatePrintBitmapData);
            }
            if (b == 2) {
                return AddPrintNVImage(bArrCreatePrintBitmapData);
            }
            if (b == 3) {
                return lzoCompress(lzoCompressData(bArrCreatePrintBitmapData));
            }
            return b == 5 ? lzoCompressData(bArrCreatePrintBitmapData) : bArrCreatePrintBitmapData;
        } catch (Exception e) {
            HPRTPrinterHelper.logcat("PrintDataFormat:" + e.getMessage());
            return null;
        }
    }

    public byte[] bb_PrintDataFormat(Bitmap bmp, int ptintdpi, int n) {
        byte[] bArrCreatePrintBitmapData;
        try {
            if (this.HalftoneMode > 0) {
                bArrCreatePrintBitmapData = GetImageDataRasterMono(bmp);
            } else {
                bArrCreatePrintBitmapData = CreatePrintBitmapData(bmp);
            }
            byte b = this.CompressMode;
            if (b == 0) {
                return CompressPrintData(bArrCreatePrintBitmapData, ptintdpi);
            }
            if (b == 1) {
                return bb_AddPrintCode(bArrCreatePrintBitmapData, n);
            }
            if (b == 2) {
                return bb_AddPrintNVImage(bArrCreatePrintBitmapData, n);
            }
            return b == 3 ? bb_lzoCompress(lzoCompressData(bArrCreatePrintBitmapData), n) : bArrCreatePrintBitmapData;
        } catch (Exception e) {
            HPRTPrinterHelper.logcat("PrintDataFormat:" + e.getMessage().toString());
            return null;
        }
    }

    public byte[] SaveDataFormat(Bitmap bmp, int ptintdpi) {
        byte[] bArrCreatePrintBitmapData;
        try {
            if (this.HalftoneMode > 0) {
                bArrCreatePrintBitmapData = GetImageDataRasterMono(bmp);
            } else {
                bArrCreatePrintBitmapData = CreatePrintBitmapData(bmp);
            }
            return bb_saveImg(lzoCompressData(bArrCreatePrintBitmapData));
        } catch (Exception e) {
            HPRTPrinterHelper.logcat("PrintDataFormat:" + e.getMessage().toString());
            return null;
        }
    }

    public byte[] SubcontractingLzo(byte[] data, int width, int height) {
        return lzoCompress(lzoCompressData(data), width, height);
    }

    private byte[] lzoCompressData(byte[] data) {
        byte[] bArr = new byte[data.length * 2];
        int[] iArr = new int[1];
        new LZOCompress().lzoCompressData(data, data.length, bArr, iArr, new byte[64000]);
        StringBuilder sb = new StringBuilder("原图：");
        sb.append(data.length);
        sb.append("压缩后：");
        sb.append(iArr[0]);
        Log.d("Print", sb.toString());
        int i = iArr[0];
        byte[] bArr2 = new byte[i];
        for (int i2 = 0; i2 < i; i2++) {
            bArr2[i2] = bArr[i2];
        }
        if (data.length + i > 1024000) {
            return null;
        }
        return bArr2;
    }

    private byte[] lzoCompress(byte[] data) {
        try {
            byte[] bArr = new byte[data.length + 12];
            bArr[0] = BoolPtg.sid;
            bArr[1] = 118;
            bArr[2] = 48;
            bArr[3] = 48;
            int i = this.BitmapWidth;
            bArr[4] = (byte) (i % 256);
            bArr[5] = (byte) (i / 256);
            int i2 = this.PrintDataHeight;
            bArr[6] = (byte) (i2 % 256);
            bArr[7] = (byte) (i2 / 256);
            byte[] bArrIntTo4Bytes = intTo4Bytes(data.length);
            for (int i3 = 0; i3 < bArrIntTo4Bytes.length; i3++) {
                bArr[i3 + 8] = bArrIntTo4Bytes[i3];
            }
            for (int i4 = 0; i4 < data.length; i4++) {
                bArr[i4 + 12] = data[i4];
            }
            return bArr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] bb_saveImg(byte[] data) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.bb_saveHead);
        int i = this.BitmapWidth;
        int i2 = this.PrintDataHeight;
        arrayList.add(new byte[]{(byte) (i % 256), (byte) (i / 256), (byte) (i2 % 256), (byte) (i2 / 256)});
        arrayList.add(intTo4Bytes(data.length));
        arrayList.add(data);
        return ConvertUtil.byteMergerAll(arrayList);
    }

    private byte[] bb_lzoCompress(byte[] data, int n) {
        try {
            byte[] bArr = new byte[data.length + 13];
            bArr[0] = BoolPtg.sid;
            bArr[1] = 118;
            bArr[2] = 48;
            bArr[3] = 48;
            int i = this.BitmapWidth;
            bArr[4] = (byte) (i % 256);
            bArr[5] = (byte) (i / 256);
            int i2 = this.PrintDataHeight;
            bArr[6] = (byte) (i2 % 256);
            bArr[7] = (byte) (i2 / 256);
            bArr[8] = (byte) n;
            byte[] bArrIntTo4Bytes = intTo4Bytes(data.length);
            for (int i3 = 0; i3 < bArrIntTo4Bytes.length; i3++) {
                bArr[i3 + 9] = bArrIntTo4Bytes[i3];
            }
            for (int i4 = 0; i4 < data.length; i4++) {
                bArr[i4 + 13] = data[i4];
            }
            Log.d("prt", Tools.byteToHex(bArr));
            return bArr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] lzoCompress(byte[] data, int width, int height) {
        try {
            byte[] bArr = new byte[data.length + 12];
            bArr[0] = BoolPtg.sid;
            bArr[1] = 118;
            bArr[2] = 48;
            bArr[3] = 48;
            bArr[4] = (byte) (width % 256);
            bArr[5] = (byte) (width / 256);
            bArr[6] = (byte) (height % 256);
            bArr[7] = (byte) (height / 256);
            byte[] bArrIntTo4Bytes = intTo4Bytes(data.length);
            for (int i = 0; i < bArrIntTo4Bytes.length; i++) {
                bArr[i + 8] = bArrIntTo4Bytes[i];
            }
            for (int i2 = 0; i2 < data.length; i2++) {
                bArr[i2 + 12] = data[i2];
            }
            return bArr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] intTo4Bytes(int value) {
        return new byte[]{(byte) (value & 255), (byte) ((value >> 8) & 255), (byte) ((value >> 16) & 255), (byte) ((value >> 24) & 255)};
    }

    private byte[] CompressPrintData(byte[] pData, int printdpi) {
        try {
            byte[] bArr = new byte[this.BitmapWidth];
            List<byte[]> arrayList = new ArrayList<>();
            List<byte[]> arrayList2 = new ArrayList<>();
            List<byte[]> arrayList3 = new ArrayList<>();
            for (int i = 0; i < this.PrintDataHeight; i++) {
                int i2 = this.BitmapWidth;
                int i3 = i * i2;
                byte[] bArr2 = new byte[i2];
                boolean z = true;
                int i4 = 0;
                int i5 = 0;
                for (int i6 = 0; i6 < this.BitmapWidth; i6++) {
                    byte b = pData[i3 + i6];
                    if (b != 0) {
                        if (i6 == 0) {
                            i4 = 0;
                        } else if (i4 > i5) {
                            i4 = i5;
                        }
                        i5 = i6;
                        z = false;
                    }
                    bArr2[i6] = b;
                }
                if (!z) {
                    int i7 = this.LBlank;
                    if (i7 == 0) {
                        this.LBlank = i4;
                    } else {
                        if (i7 < i4) {
                            i4 = i7;
                        }
                        this.LBlank = i4;
                    }
                    int i8 = this.RBlank;
                    if (i8 >= i5) {
                        i5 = i8;
                    }
                    this.RBlank = i5;
                    int size = arrayList3.size();
                    if (size > 0) {
                        if (size > 24) {
                            if (arrayList2.size() > 0) {
                                arrayList.add(TrimBitmapBlank(arrayList2));
                            }
                            arrayList.add(CreateFeedLineCMD(arrayList3, printdpi));
                            arrayList2 = new ArrayList<>();
                        } else {
                            arrayList2.addAll(arrayList3);
                        }
                        arrayList3 = new ArrayList<>();
                    }
                    arrayList2.add(bArr2);
                    if (arrayList2.size() == 100) {
                        arrayList.add(TrimBitmapBlank(arrayList2));
                        arrayList2 = new ArrayList<>();
                    }
                } else {
                    arrayList3.add(bArr2);
                }
            }
            int size2 = arrayList3.size();
            if (size2 <= 0) {
                arrayList.add(TrimBitmapBlank(arrayList2));
            } else if (size2 > 24) {
                if (arrayList2.size() > 0) {
                    arrayList.add(TrimBitmapBlank(arrayList2));
                }
                arrayList.add(CreateFeedLineCMD(arrayList3, printdpi));
            } else {
                arrayList2.addAll(arrayList3);
                arrayList.add(TrimBitmapBlank(arrayList2));
            }
            return sysCopy(arrayList);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] CreateFeedLineCMD(List<byte[]> BlankDatas, int printdpi) {
        int i = printdpi == 300 ? 12 : 8;
        try {
            ArrayList arrayList = new ArrayList();
            int size = BlankDatas.size();
            for (int i2 = 0; i2 < size; i2 += HebrewProber.NORMAL_NUN) {
                byte[] bArr = {27, 74, 0};
                int i3 = size - i2;
                if (i3 > 240) {
                    bArr[2] = -16;
                } else {
                    bArr[2] = (byte) ((i3 * 8) / i);
                }
                arrayList.add(bArr);
            }
            return sysCopy(arrayList);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] TrimBitmapBlank(List<byte[]> LineDatas) {
        try {
            int i = (this.RBlank - this.LBlank) + 1;
            int size = LineDatas.size();
            byte[] bArr = new byte[(i * size) + ((size % 2300 > 0 ? (size / 2300) + 1 : size / 2300) * 8)];
            int i2 = 0;
            int i3 = 0;
            while (i2 < size) {
                int i4 = i2 + 2300;
                int i5 = i4 < size ? 2300 : size - i2;
                int i6 = 2308 * i3;
                bArr[i6] = BoolPtg.sid;
                bArr[i6 + 1] = 118;
                bArr[i6 + 2] = 48;
                bArr[i6 + 3] = this.ScaleMode;
                bArr[i6 + 4] = (byte) (i % 256);
                bArr[i6 + 5] = (byte) (i / 256);
                bArr[i6 + 6] = (byte) (i5 % 256);
                bArr[i6 + 7] = (byte) (i5 / 256);
                while (i2 < size) {
                    System.arraycopy(LineDatas.get(i2), this.LBlank, bArr, (i2 * i) + i6 + 8, i);
                    i2++;
                }
                i3++;
                i2 = i4;
            }
            this.LBlank = 0;
            this.RBlank = 0;
            return bArr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] AddPrintNVImage(byte[] bDatas) {
        try {
            byte[] bArr = new byte[bDatas.length + 8];
            bArr[0] = BoolPtg.sid;
            bArr[1] = 118;
            bArr[2] = 48;
            bArr[3] = 0;
            int i = this.BitmapWidth;
            bArr[4] = (byte) (i % 256);
            bArr[5] = (byte) (i / 256);
            int i2 = this.PrintDataHeight;
            bArr[6] = (byte) (i2 % 256);
            bArr[7] = (byte) (i2 / 256);
            for (int i3 = 0; i3 < bDatas.length; i3++) {
                bArr[i3 + 8] = bDatas[i3];
            }
            return bArr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] bb_AddPrintNVImage(byte[] bDatas, int n) {
        try {
            byte[] bArr = new byte[bDatas.length + 9];
            bArr[0] = BoolPtg.sid;
            bArr[1] = 118;
            bArr[2] = 48;
            bArr[3] = 0;
            int i = this.BitmapWidth;
            bArr[4] = (byte) (i % 256);
            bArr[5] = (byte) (i / 256);
            int i2 = this.PrintDataHeight;
            bArr[6] = (byte) (i2 % 256);
            bArr[7] = (byte) (i2 / 256);
            bArr[8] = (byte) n;
            for (int i3 = 0; i3 < bDatas.length; i3++) {
                bArr[i3 + 9] = bDatas[i3];
            }
            return bArr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] AddPrintCode(byte[] bDatas) {
        try {
            int i = this.PrintDataHeight;
            ArrayList arrayList = new ArrayList();
            int i2 = 0;
            while (i >= 100) {
                int i3 = this.BitmapWidth;
                int i4 = i3 * 100;
                byte[] bArr = new byte[i4 + 8];
                bArr[0] = BoolPtg.sid;
                bArr[1] = 118;
                bArr[2] = 48;
                bArr[3] = 0;
                bArr[4] = (byte) (i3 % 256);
                bArr[5] = (byte) (i3 / 256);
                bArr[6] = 100;
                bArr[7] = 0;
                for (int i5 = 0; i5 < i4; i5++) {
                    bArr[i5 + 9] = bDatas[i5 + i2];
                }
                arrayList.add(bArr);
                i2 += this.BitmapWidth * 100;
                i -= 100;
            }
            if (i > 0) {
                int i6 = this.BitmapWidth;
                int i7 = i6 * i;
                byte[] bArr2 = new byte[i7 + 8];
                bArr2[0] = BoolPtg.sid;
                bArr2[1] = 118;
                bArr2[2] = 48;
                bArr2[3] = 0;
                bArr2[4] = (byte) (i6 % 256);
                bArr2[5] = (byte) (i6 / 256);
                bArr2[6] = (byte) (i % 256);
                bArr2[7] = (byte) (i / 256);
                for (int i8 = 0; i8 < i7; i8++) {
                    bArr2[i8 + 9] = bDatas[i8 + i2];
                }
                arrayList.add(bArr2);
            }
            return sysCopy(arrayList);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] bb_AddPrintCode(byte[] bDatas, int n) {
        try {
            int i = this.PrintDataHeight;
            ArrayList arrayList = new ArrayList();
            int i2 = 0;
            while (i >= 100) {
                int i3 = this.BitmapWidth;
                int i4 = i3 * 100;
                byte[] bArr = new byte[i4 + 9];
                bArr[0] = BoolPtg.sid;
                bArr[1] = 118;
                bArr[2] = 48;
                bArr[3] = 0;
                bArr[4] = (byte) (i3 % 256);
                bArr[5] = (byte) (i3 / 256);
                bArr[6] = 100;
                bArr[7] = 0;
                bArr[8] = (byte) n;
                for (int i5 = 0; i5 < i4 + 1; i5++) {
                    bArr[i5 + 9] = bDatas[i5 + i2];
                }
                arrayList.add(bArr);
                i2 += this.BitmapWidth * 100;
                i -= 100;
            }
            if (i > 0) {
                int i6 = this.BitmapWidth;
                int i7 = i6 * i;
                byte[] bArr2 = new byte[i7 + 9];
                bArr2[0] = BoolPtg.sid;
                bArr2[1] = 118;
                bArr2[2] = 48;
                bArr2[3] = 0;
                bArr2[4] = (byte) (i6 % 256);
                bArr2[5] = (byte) (i6 / 256);
                bArr2[6] = (byte) (i % 256);
                bArr2[7] = (byte) (i / 256);
                bArr2[8] = (byte) n;
                for (int i8 = 0; i8 < i7 + 1; i8++) {
                    bArr2[i8 + 9] = bDatas[i8 + i2];
                }
                arrayList.add(bArr2);
            }
            return sysCopy(arrayList);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] GetImageDataRasterMono(Bitmap pimage) {
        int i;
        float f;
        int width = pimage.getWidth();
        int height = pimage.getHeight();
        int i2 = (width + 7) >> 3;
        try {
            this.PrintDataHeight = height;
            this.BitmapWidth = i2;
            int i3 = width * height;
            int i4 = i2 * height;
            int[] iArr = new int[i3];
            pimage.getPixels(iArr, 0, width, 0, 0, width, height);
            int i5 = 0;
            int i6 = 0;
            while (true) {
                i = 255;
                if (i5 >= i3) {
                    break;
                }
                int i7 = iArr[i5];
                int[] iArr2 = iArr;
                iArr2[i6] = ((byte) ((((double) Color.red(i7)) * 0.29891d) + (((double) Color.green(i7)) * 0.58661d) + (((double) Color.blue(i7)) * 0.11448d))) & 255;
                i5++;
                iArr = iArr2;
                i6++;
            }
            int[] iArr3 = iArr;
            int i8 = 0;
            while (i8 < height) {
                int i9 = i8 * width;
                int i10 = 0;
                while (i10 < width) {
                    int i11 = iArr3[i9];
                    if (i11 > 128) {
                        f = i11 - 255;
                        iArr3[i9] = i;
                    } else {
                        f = i11;
                        iArr3[i9] = 0;
                    }
                    int i12 = width - 1;
                    if (i10 < i12) {
                        int i13 = i9 + 1;
                        iArr3[i13] = iArr3[i13] + ((int) (((double) f) * 0.4375d));
                    }
                    if (i8 < height - 1) {
                        if (i10 > 1) {
                            int i14 = (i9 + width) - 1;
                            iArr3[i14] = iArr3[i14] + ((int) (((double) f) * 0.1875d));
                        }
                        int i15 = i9 + width;
                        double d = f;
                        iArr3[i15] = iArr3[i15] + ((int) (d * 0.3125d));
                        if (i10 < i12) {
                            int i16 = i15 + 1;
                            iArr3[i16] = iArr3[i16] + ((int) (d * 0.0625d));
                        }
                    }
                    i9++;
                    i10++;
                    i = 255;
                }
                i8++;
                i = 255;
            }
            byte[] bArr = new byte[i4];
            for (int i17 = 0; i17 < height; i17++) {
                int i18 = i17 * width;
                int i19 = i17 * i2;
                int i20 = 0;
                while (true) {
                    int i21 = 0;
                    while (i20 < width) {
                        int i22 = i20 % 8;
                        int i23 = i18 + 1;
                        if (iArr3[i18] <= 128) {
                            i21 |= 128 >> i22;
                        }
                        i20++;
                        if (i22 != 7 && i20 != width) {
                            i18 = i23;
                        }
                        int i24 = i19 + 1;
                        bArr[i19] = (byte) i21;
                        i19 = i24;
                        i18 = i23;
                    }
                }
            }
            return bArr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] CreatePrintBitmapData(Bitmap bmp) {
        try {
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            this.PrintDataHeight = height;
            int i = (width % 8 == 0 ? width : ((width / 8) + 1) * 8) / 8;
            this.BitmapWidth = i;
            int i2 = i * height;
            byte[] bArr = new byte[i2];
            int i3 = width * height;
            int[] iArr = new int[i3];
            bmp.getPixels(iArr, 0, width, 0, 0, width, height);
            for (int i4 = 0; i4 < i3; i4++) {
                int i5 = iArr[i4];
                iArr[i4] = ((byte) ((((double) Color.red(i5)) * 0.29891d) + (((double) Color.green(i5)) * 0.58661d) + (((double) Color.blue(i5)) * 0.11448d))) & 255;
            }
            Log.d("Print", "CreatePrintBitmapData: threshold->" + getThreshold(iArr, width, height));
            for (int i6 = 0; i6 < i2; i6++) {
                bArr[i6] = 0;
            }
            int i7 = 0;
            int i8 = 0;
            while (i8 < height) {
                int[] iArr2 = new int[width];
                bmp.getPixels(iArr2, 0, width, 0, i8, width, 1);
                int i9 = 0;
                for (int i10 = 0; i10 < width; i10++) {
                    i9++;
                    int i11 = iArr2[i10];
                    if (i9 > 8) {
                        i7++;
                        i9 = 1;
                    }
                    if (i11 != -1) {
                        int i12 = 1 << (8 - i9);
                        if (((Color.red(i11) + Color.green(i11)) + Color.blue(i11)) / 3 <= 128) {
                            bArr[i7] = (byte) (bArr[i7] | i12);
                        }
                    }
                }
                i8++;
                i7 = this.BitmapWidth * i8;
            }
            return bArr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] sysCopy(List<byte[]> srcArrays) {
        Iterator<byte[]> it2 = srcArrays.iterator();
        int length = 0;
        while (it2.hasNext()) {
            length += it2.next().length;
        }
        byte[] bArr = new byte[length];
        int length2 = 0;
        for (byte[] bArr2 : srcArrays) {
            System.arraycopy(bArr2, 0, bArr, length2, bArr2.length);
            length2 += bArr2.length;
        }
        return bArr;
    }

    public static int getThreshold(int[] gray, int w, int h) {
        int[] iArr;
        int i;
        int[] iArr2 = new int[256];
        int i2 = w * h;
        int i3 = 0;
        int i4 = 0;
        for (int i5 : gray) {
            i4 += i5;
            iArr2[i5] = iArr2[i5] + 1;
        }
        double d = 0.0d;
        int i6 = 0;
        int i7 = 0;
        int i8 = 0;
        for (int i9 = 256; i3 < i9; i9 = 256) {
            int i10 = iArr2[i3];
            int i11 = i6 + (i3 * i10);
            i7 += i10;
            if (i7 == 0) {
                i = i11;
                iArr = iArr2;
            } else {
                int i12 = i2 - i7;
                int i13 = i4 - i11;
                if (i12 == 0) {
                    break;
                }
                double d2 = i7;
                iArr = iArr2;
                double d3 = (d2 * 1.0d) / ((double) i2);
                i = i11;
                double d4 = ((((double) i11) * 1.0d) / d2) - ((((double) i13) * 1.0d) / ((double) i12));
                double d5 = d3 * (1.0d - d3) * d4 * d4;
                if (d5 > d) {
                    i8 = i3;
                    d = d5;
                }
            }
            i3++;
            i6 = i;
            iArr2 = iArr;
        }
        return i8;
    }
}
