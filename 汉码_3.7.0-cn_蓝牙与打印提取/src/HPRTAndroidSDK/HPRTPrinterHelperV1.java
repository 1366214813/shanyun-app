package HPRTAndroidSDK;

import android.graphics.Bitmap;
import android.graphics.Color;
import anet.channel.strategy.dispatch.DispatchConstants;
import com.google.android.gms.common.internal.ServiceSpecificExtraArgs;
import com.prt.print.utils.TemplateOperation;
import com.taobao.weex.el.parse.Operators;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.ByteCompanionObject;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.BuildersKt__Builders_commonKt;
import kotlinx.coroutines.CancellableContinuation;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.GlobalScope;
import org.apache.poi.ss.formula.ptg.Ptg;

/* JADX INFO: compiled from: HPRTPrinterHelperV1.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\u0018\u0000 \u00042\u00020\u0001:\u0001\u0004B\u0007¢\u0006\u0004\b\u0002\u0010\u0003¨\u0006\u0005"}, d2 = {"LHPRTAndroidSDK/HPRTPrinterHelperV1;", "", "<init>", "()V", "Companion", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class HPRTPrinterHelperV1 {

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static final Map<String, CancellableContinuation<?>> map = new LinkedHashMap();

    @JvmStatic
    public static final byte[] getDoubleColorData(Bitmap bitmap, Bitmap bitmap2) {
        return INSTANCE.getDoubleColorData(bitmap, bitmap2);
    }

    @JvmStatic
    public static final void printDoubleColor(Bitmap bitmap, Bitmap bitmap2) {
        INSTANCE.printDoubleColor(bitmap, bitmap2);
    }

    /* JADX INFO: compiled from: HPRTPrinterHelperV1.kt */
    @Metadata(d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001:\u0001#B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\rH\u0007J\u0018\u0010\u000f\u001a\u00020\u00102\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\rH\u0007J\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u00102\u0006\u0010\u0012\u001a\u00020\rJ \u0010\u0013\u001a\u00020\u000b2\u0006\u0010\u0014\u001a\u00020\u00102\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0016H\u0002J\u0016\u0010\u0018\u001a\u00020\u000b2\u0006\u0010\u0019\u001a\u00020\u00062\u0006\u0010\u001a\u001a\u00020\u001bJ\u0010\u0010\u001c\u001a\u00020\u000b2\u0006\u0010\u001a\u001a\u00020\u001dH\u0002J&\u0010\u001e\u001a\u0012\u0012\u0004\u0012\u00020\u00100\u001fj\b\u0012\u0004\u0012\u00020\u0010` 2\u0006\u0010!\u001a\u00020\u00102\u0006\u0010\"\u001a\u00020\u0016R!\u0010\u0004\u001a\u0012\u0012\u0004\u0012\u00020\u0006\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00070\u0005¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t¨\u0006$"}, d2 = {"LHPRTAndroidSDK/HPRTPrinterHelperV1$Companion;", "", "<init>", "()V", "map", "", "", "Lkotlinx/coroutines/CancellableContinuation;", "getMap", "()Ljava/util/Map;", "printDoubleColor", "", "blackBitmap", "Landroid/graphics/Bitmap;", "redBitmap", "getDoubleColorData", "", "threshold", "bmp", "sendTaskData", "taskData", "width", "", "taskID", "sendTaskEnd", "id", ServiceSpecificExtraArgs.CastExtraArgs.LISTENER, "LHPRTAndroidSDK/SetResultListener;", "getPackLength", "LHPRTAndroidSDK/PackLengthListener;", "fileToArrayList", "Ljava/util/ArrayList;", "Lkotlin/collections/ArrayList;", "fileData", "pkgLen", "ResultData", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final Map<String, CancellableContinuation<?>> getMap() {
            return HPRTPrinterHelperV1.map;
        }

        @JvmStatic
        public final void printDoubleColor(Bitmap blackBitmap, Bitmap redBitmap) {
            Intrinsics.checkNotNullParameter(blackBitmap, "blackBitmap");
            Intrinsics.checkNotNullParameter(redBitmap, "redBitmap");
            if (blackBitmap.getWidth() == redBitmap.getWidth() && blackBitmap.getHeight() == redBitmap.getHeight()) {
                sendTaskData(getDoubleColorData(blackBitmap, redBitmap), blackBitmap.getWidth(), 0);
            }
        }

        @JvmStatic
        public final byte[] getDoubleColorData(Bitmap blackBitmap, Bitmap redBitmap) {
            byte b;
            int i;
            int i2;
            int i3;
            byte b2;
            int i4;
            int i5;
            int i6;
            Intrinsics.checkNotNullParameter(blackBitmap, "blackBitmap");
            Intrinsics.checkNotNullParameter(redBitmap, "redBitmap");
            byte[] bArrThreshold = threshold(blackBitmap);
            byte[] bArrThreshold2 = threshold(redBitmap);
            Intrinsics.checkNotNull(bArrThreshold);
            byte b3 = 2;
            byte[] bArr = new byte[bArrThreshold.length * 2];
            int length = bArrThreshold.length;
            int i7 = 0;
            int i8 = 0;
            int i9 = 0;
            while (i7 < length) {
                byte b4 = bArrThreshold[i7];
                int i10 = i8 + 1;
                Byte bValueOf = bArrThreshold2 != null ? Byte.valueOf(bArrThreshold2[i8]) : null;
                if (((byte) (b4 & 1)) == 1) {
                    b = 3;
                } else {
                    b = (bValueOf == null || ((byte) (bValueOf.byteValue() & 1)) != 1) ? (byte) 0 : (byte) 2;
                }
                if (((byte) (b4 & 2)) == b3) {
                    i = 3;
                } else {
                    i = (bValueOf == null || ((byte) (bValueOf.byteValue() & b3)) != b3) ? 0 : 2;
                }
                if (((byte) (b4 & 4)) == 4) {
                    i2 = 3;
                } else {
                    i2 = (bValueOf == null || ((byte) (bValueOf.byteValue() & 4)) != 4) ? 0 : 2;
                }
                if (((byte) (b4 & 8)) == 8) {
                    i3 = 3;
                } else {
                    i3 = (bValueOf == null || ((byte) (bValueOf.byteValue() & 8)) != 8) ? 0 : 2;
                }
                if (((byte) (b4 & 16)) == 16) {
                    b2 = 3;
                } else {
                    b2 = (bValueOf == null || ((byte) (bValueOf.byteValue() & 16)) != 16) ? (byte) 0 : (byte) 2;
                }
                byte[] bArr2 = bArrThreshold2;
                if (((byte) (b4 & 32)) == 32) {
                    i4 = 3;
                } else {
                    i4 = (bValueOf == null || ((byte) (bValueOf.byteValue() & 32)) != 32) ? 0 : 2;
                }
                int i11 = i4;
                if (((byte) (b4 & Ptg.CLASS_ARRAY)) == 64) {
                    i5 = 3;
                } else {
                    i5 = (bValueOf == null || ((byte) (bValueOf.byteValue() & Ptg.CLASS_ARRAY)) != 64) ? 0 : 2;
                }
                if (((byte) (b4 & ByteCompanionObject.MIN_VALUE)) == -128) {
                    i6 = 3;
                } else {
                    i6 = (bValueOf == null || ((byte) (bValueOf.byteValue() & ByteCompanionObject.MIN_VALUE)) != -128) ? 0 : 2;
                }
                bArr[i9] = (byte) (((byte) (((byte) (i5 << 4)) | ((byte) (b2 | ((byte) (i11 << 2)))))) | ((byte) (i6 << 6)));
                bArr[i9 + 1] = (byte) (((byte) (i3 << 6)) | ((byte) (((byte) (((byte) (i << 2)) | b)) | ((byte) (i2 << 4)))));
                i9 += 2;
                i7++;
                i8 = i10;
                bArrThreshold2 = bArr2;
                b3 = 2;
            }
            return bArr;
        }

        public final byte[] threshold(Bitmap bmp) {
            Bitmap bmp2 = bmp;
            Intrinsics.checkNotNullParameter(bmp2, "bmp");
            try {
                int width = bmp2.getWidth();
                int height = bmp2.getHeight();
                if (width % 8 != 0) {
                    width = ((width / 8) + 1) * 8;
                }
                int i = width / 8;
                int i2 = height * i;
                byte[] bArr = new byte[i2];
                for (int i3 = 0; i3 < i2; i3++) {
                    bArr[i3] = 0;
                }
                int i4 = 0;
                int i5 = 0;
                while (i5 < height) {
                    int[] iArr = new int[i];
                    bmp2.getPixels(iArr, 0, i, 0, i5, i, 1);
                    int i6 = 0;
                    for (int i7 = 0; i7 < i; i7++) {
                        i6++;
                        int i8 = iArr[i7];
                        if (i6 > 8) {
                            i4++;
                            i6 = 1;
                        }
                        if (i8 != -1) {
                            int i9 = 1 << (8 - i6);
                            if (((Color.red(i8) + Color.green(i8)) + Color.blue(i8)) / 3 < 128) {
                                bArr[i4] = (byte) (bArr[i4] | i9);
                            }
                        }
                    }
                    i5++;
                    i4 = i * i5;
                    bmp2 = bmp;
                }
                return bArr;
            } catch (Exception unused) {
                return null;
            }
        }

        private final void sendTaskData(byte[] taskData, int width, int taskID) {
            getPackLength(new HPRTPrinterHelperV1$Companion$sendTaskData$1(width, taskData, taskID));
        }

        public final void sendTaskEnd(String id2, SetResultListener listener) {
            Intrinsics.checkNotNullParameter(id2, "id");
            Intrinsics.checkNotNullParameter(listener, "listener");
            BuildersKt__Builders_commonKt.launch$default(GlobalScope.INSTANCE, Dispatchers.getIO(), null, new HPRTPrinterHelperV1$Companion$sendTaskEnd$1(id2, listener, null), 2, null);
        }

        private final void getPackLength(PackLengthListener listener) {
            BuildersKt__Builders_commonKt.launch$default(GlobalScope.INSTANCE, Dispatchers.getIO(), null, new HPRTPrinterHelperV1$Companion$getPackLength$1(listener, null), 2, null);
        }

        /* JADX INFO: compiled from: HPRTPrinterHelperV1.kt */
        @Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00020\u0002B\u0017\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00028\u0000¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\r\u001a\u00020\u0004HÆ\u0003J\u000e\u0010\u000e\u001a\u00028\u0000HÆ\u0003¢\u0006\u0002\u0010\u000bJ(\u0010\u000f\u001a\b\u0012\u0004\u0012\u00028\u00000\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00042\b\b\u0002\u0010\u0005\u001a\u00028\u0000HÆ\u0001¢\u0006\u0002\u0010\u0010J\u0013\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0002HÖ\u0003J\t\u0010\u0014\u001a\u00020\u0004HÖ\u0001J\t\u0010\u0015\u001a\u00020\u0016HÖ\u0001R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0013\u0010\u0005\u001a\u00028\u0000¢\u0006\n\n\u0002\u0010\f\u001a\u0004\b\n\u0010\u000b¨\u0006\u0017"}, d2 = {"LHPRTAndroidSDK/HPRTPrinterHelperV1$Companion$ResultData;", "T", "", "code", "", "result", "<init>", "(ILjava/lang/Object;)V", "getCode", "()I", "getResult", "()Ljava/lang/Object;", "Ljava/lang/Object;", "component1", "component2", TemplateOperation.COPY, "(ILjava/lang/Object;)LHPRTAndroidSDK/HPRTPrinterHelperV1$Companion$ResultData;", "equals", "", DispatchConstants.OTHER, "hashCode", "toString", "", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
        public static final /* data */ class ResultData<T> {
            private final int code;
            private final T result;

            /* JADX WARN: Multi-variable type inference failed */
            public static /* synthetic */ ResultData copy$default(ResultData resultData, int i, Object obj, int i2, Object obj2) {
                if ((i2 & 1) != 0) {
                    i = resultData.code;
                }
                if ((i2 & 2) != 0) {
                    obj = resultData.result;
                }
                return resultData.copy(i, obj);
            }

            /* JADX INFO: renamed from: component1, reason: from getter */
            public final int getCode() {
                return this.code;
            }

            public final T component2() {
                return this.result;
            }

            public final ResultData<T> copy(int code, T result) {
                return new ResultData<>(code, result);
            }

            public boolean equals(Object other) {
                if (this == other) {
                    return true;
                }
                if (!(other instanceof ResultData)) {
                    return false;
                }
                ResultData resultData = (ResultData) other;
                return this.code == resultData.code && Intrinsics.areEqual(this.result, resultData.result);
            }

            public int hashCode() {
                int i = this.code * 31;
                T t = this.result;
                return i + (t == null ? 0 : t.hashCode());
            }

            public String toString() {
                return "ResultData(code=" + this.code + ", result=" + this.result + Operators.BRACKET_END_STR;
            }

            public ResultData(int i, T t) {
                this.code = i;
                this.result = t;
            }

            public final int getCode() {
                return this.code;
            }

            public final T getResult() {
                return this.result;
            }
        }

        public final ArrayList<byte[]> fileToArrayList(byte[] fileData, int pkgLen) {
            Intrinsics.checkNotNullParameter(fileData, "fileData");
            ArrayList<byte[]> arrayList = new ArrayList<>();
            int length = fileData.length / pkgLen;
            for (int i = 0; i < length; i++) {
                byte[] bArr = new byte[pkgLen];
                System.arraycopy(fileData, i * pkgLen, bArr, 0, pkgLen);
                arrayList.add(bArr);
            }
            int i2 = length * pkgLen;
            int length2 = fileData.length - i2;
            if (length2 > 0) {
                byte[] bArr2 = new byte[length2];
                System.arraycopy(fileData, i2, bArr2, 0, length2);
                arrayList.add(bArr2);
            }
            return arrayList;
        }
    }
}
