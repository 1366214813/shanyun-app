package HPRTAndroidSDK.dbcolor;

import android.graphics.Bitmap;
import android.graphics.Color;
import com.blankj.utilcode.util.LogUtils;
import com.taobao.weex.el.parse.Operators;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.StringCompanionObject;
import org.apache.poi.ss.formula.ptg.BoolPtg;
import org.apache.poi.ss.formula.ptg.DeletedRef3DPtg;
import org.apache.poi.ss.formula.ptg.Ptg;

/* JADX INFO: compiled from: PrinterDataGenerator.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0010\u0005\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0010\u000e\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J$\u0010\r\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\b\b\u0002\u0010\u0012\u001a\u00020\u0013H\u0007J\u001a\u0010\r\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0014\u001a\u00020\u0011H\u0007J\u0018\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u0011H\u0002J\u0010\u0010\u001a\u001a\u00020\u00052\u0006\u0010\u001b\u001a\u00020\u0011H\u0007J\u0010\u0010\u001c\u001a\u00020\u00052\u0006\u0010\u0019\u001a\u00020\u0011H\u0007J\b\u0010\u001d\u001a\u00020\u0005H\u0007J\u0018\u0010\u001e\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u001f\u001a\u00020\u0011H\u0002J\u0010\u0010 \u001a\u00020\u00112\u0006\u0010!\u001a\u00020\u0011H\u0002J \u0010\"\u001a\u00020\u00132\u0006\u0010#\u001a\u00020\u00112\u0006\u0010$\u001a\u00020\u00112\u0006\u0010%\u001a\u00020\u0011H\u0002J \u0010&\u001a\u00020\u00132\u0006\u0010#\u001a\u00020\u00112\u0006\u0010$\u001a\u00020\u00112\u0006\u0010%\u001a\u00020\u0011H\u0002J\u0010\u0010'\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J\u0012\u0010(\u001a\u00020)2\b\u0010*\u001a\u0004\u0018\u00010\u0005H\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\bX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006+"}, d2 = {"LHPRTAndroidSDK/dbcolor/PrinterDataGenerator;", "", "<init>", "()V", "ESC_INIT", "", "GS_BITMAP_CMD", "CR", "", "LF", "SET_POSITION_PREFIX", "GET_POSITION_PREFIX", "SAVE_PARAM_CMD", "generatePrinterData", "bitmap", "Landroid/graphics/Bitmap;", "startXPosition", "", "saveParams", "", "startX", "setStartPosition", "", "outputStream", "Ljava/io/ByteArrayOutputStream;", "xPosition", "generateGetPositionCommand", "byteCount", "generateSetPositionCommand", "generateSaveParametersCommand", "convertBitmapToBytes", "byteWidth", "convertPixelTo2Bit", "pixel", "isWhitePixel", "red", "green", "blue", "isRedPixel", "processBitmap", "bytesToHex", "", "bytes", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class PrinterDataGenerator {
    private static final byte CR = 13;
    private static final byte LF = 10;
    public static final PrinterDataGenerator INSTANCE = new PrinterDataGenerator();
    private static final byte[] ESC_INIT = {27, Ptg.CLASS_ARRAY};
    private static final byte[] GS_BITMAP_CMD = {BoolPtg.sid, 117, 48, 0};
    private static final byte[] SET_POSITION_PREFIX = {27, 28, 38, 32, 86, 49, 32, 115, 101, 116, 107, 101, 121, 13, 10, 1, DeletedRef3DPtg.sid, 1};
    private static final byte[] GET_POSITION_PREFIX = {27, 28, 38, 32, 86, 49, 32, 103, 101, 116, 107, 101, 121, 13, 10, 1, DeletedRef3DPtg.sid, 1};
    private static final byte[] SAVE_PARAM_CMD = {27, 28, 38, 32, 86, 49, 32, 100, 111, 32, 34, 115, 97, 118, 101, 95, 112, 97, 114, 97, 109, 95, 122, 111, 110, 101, 34, 13, 10};

    private final boolean isRedPixel(int red, int green, int blue) {
        return red == 255 && green == 0 && blue == 0;
    }

    private final boolean isWhitePixel(int red, int green, int blue) {
        return red > 200 && green > 200 && blue > 200;
    }

    private PrinterDataGenerator() {
    }

    public static /* synthetic */ byte[] generatePrinterData$default(Bitmap bitmap, int i, boolean z, int i2, Object obj) {
        if ((i2 & 4) != 0) {
            z = true;
        }
        return generatePrinterData(bitmap, i, z);
    }

    @JvmStatic
    public static final byte[] generatePrinterData(Bitmap bitmap, int startXPosition, boolean saveParams) {
        Intrinsics.checkNotNullParameter(bitmap, "bitmap");
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write(ESC_INIT);
            byteArrayOutputStream.write(GS_BITMAP_CMD);
            byteArrayOutputStream.write(INSTANCE.processBitmap(bitmap));
            byteArrayOutputStream.write(13);
            LogUtils.d("生成打印数据完成 - 起始位置: " + startXPosition + ", 保存参数: " + saveParams);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("生成打印数据失败: " + e.getMessage());
            return null;
        }
    }

    @JvmStatic
    public static final byte[] generatePrinterData(Bitmap bitmap, int startX) {
        Intrinsics.checkNotNullParameter(bitmap, "bitmap");
        return generatePrinterData(bitmap, startX, true);
    }

    private final void setStartPosition(ByteArrayOutputStream outputStream, int xPosition) {
        try {
            outputStream.write(SET_POSITION_PREFIX);
            if (xPosition > 255) {
                outputStream.write(2);
                outputStream.write(xPosition & 255);
                outputStream.write(255 & (xPosition >> 8));
            } else {
                outputStream.write(1);
                outputStream.write(xPosition & 255);
            }
            LogUtils.d("设置起始X位置: " + xPosition);
        } catch (Exception e) {
            LogUtils.e("设置起始位置失败: " + e.getMessage());
        }
    }

    @JvmStatic
    public static final byte[] generateGetPositionCommand(int byteCount) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(GET_POSITION_PREFIX);
        byteArrayOutputStream.write(byteCount);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        Intrinsics.checkNotNullExpressionValue(byteArray, "toByteArray(...)");
        return byteArray;
    }

    @JvmStatic
    public static final byte[] generateSetPositionCommand(int xPosition) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        INSTANCE.setStartPosition(byteArrayOutputStream, xPosition);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        Intrinsics.checkNotNullExpressionValue(byteArray, "toByteArray(...)");
        return byteArray;
    }

    @JvmStatic
    public static final byte[] generateSaveParametersCommand() {
        LogUtils.d("生成保存参数指令");
        return (byte[]) SAVE_PARAM_CMD.clone();
    }

    private final byte[] convertBitmapToBytes(Bitmap bitmap, int byteWidth) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < height; i++) {
            for (int i2 = 0; i2 < byteWidth; i2++) {
                int i3 = i2 * 4;
                int i4 = i3 + 1;
                int i5 = i3 + 2;
                int i6 = i3 + 3;
                arrayList.add(Byte.valueOf((byte) (((i3 < width ? convertPixelTo2Bit(bitmap.getPixel(i3, i)) : 0) << 6) | ((i4 < width ? convertPixelTo2Bit(bitmap.getPixel(i4, i)) : 0) << 4) | ((i5 < width ? convertPixelTo2Bit(bitmap.getPixel(i5, i)) : 0) << 2) | (i6 < width ? convertPixelTo2Bit(bitmap.getPixel(i6, i)) : 0))));
            }
        }
        byte[] bArr = new byte[arrayList.size()];
        int size = arrayList.size();
        for (int i7 = 0; i7 < size; i7++) {
            Object obj = arrayList.get(i7);
            Intrinsics.checkNotNull(obj);
            bArr[i7] = ((Number) obj).byteValue();
        }
        return bArr;
    }

    private final int convertPixelTo2Bit(int pixel) {
        int iAlpha = Color.alpha(pixel);
        int iRed = Color.red(pixel);
        int iGreen = Color.green(pixel);
        int iBlue = Color.blue(pixel);
        boolean z = iAlpha > 128 && !isWhitePixel(iRed, iGreen, iBlue);
        boolean z2 = z && isRedPixel(iRed, iGreen, iBlue);
        int i = z ? 2 : 0;
        return z2 ? i | 1 : i;
    }

    private final byte[] processBitmap(Bitmap bitmap) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int i = ((width + 3) / 4) * 2;
        byteArrayOutputStream.write(i & 255);
        byteArrayOutputStream.write((i >> 8) & 255);
        LogUtils.d("位图宽度: " + width + " 像素, 字节宽度: " + i + " 字节");
        byteArrayOutputStream.write(height & 255);
        byteArrayOutputStream.write((height >> 8) & 255);
        LogUtils.d("位图高度: " + height + " 像素");
        byteArrayOutputStream.write(convertBitmapToBytes(bitmap, i));
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        Intrinsics.checkNotNullExpressionValue(byteArray, "toByteArray(...)");
        return byteArray;
    }

    @JvmStatic
    public static final String bytesToHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int length = bytes.length;
        for (int i = 0; i < length; i++) {
            StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
            String str = String.format("%02X", Arrays.copyOf(new Object[]{Integer.valueOf(bytes[i] & 255)}, 1));
            Intrinsics.checkNotNullExpressionValue(str, "format(...)");
            sb.append(str);
            if (i < bytes.length - 1) {
                sb.append(Operators.SPACE_STR);
            }
        }
        String string = sb.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }
}
