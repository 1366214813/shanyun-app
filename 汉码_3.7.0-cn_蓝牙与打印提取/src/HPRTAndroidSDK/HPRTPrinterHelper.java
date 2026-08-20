package HPRTAndroidSDK;

import HPRTAndroidSDK.bean.D31SConfig;
import HPRTAndroidSDK.bean.RFIDInfo;
import HPRTAndroidSDK.bean.ResultSendData;
import LZO_Compress.LZOCompress;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import androidtranscoder.format.MediaFormatExtraConstants;
import com.alibaba.android.bindingx.core.internal.BindingXConstants;
import com.facebook.imagepipeline.producers.HttpUrlConnectionNetworkFetcher;
import com.facebook.imageutils.JfifUtil;
import com.google.android.gms.common.ConnectionResult;
import com.prt.print.common.PrintConstant;
import com.prt.print.utils.PoliDeviceDetailsParser;
import com.taobao.accs.common.Constants;
import com.taobao.weex.common.Constants;
import com.taobao.weex.el.parse.Operators;
import com.taobao.weex.ui.component.WXBasicComponentType;
import com.taobao.weex.ui.module.WXModalUIModule;
import com.umeng.analytics.pro.f;
import com.zhihu.matisse.internal.loader.AlbumLoader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Triple;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.SpillingKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.text.Charsets;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import kotlinx.coroutines.DelayKt;
import okio.Utf8;
import org.apache.poi.hssf.record.PaletteRecord;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.ss.formula.ptg.Area3DPtg;
import org.apache.poi.ss.formula.ptg.AreaErrPtg;
import org.apache.poi.ss.formula.ptg.AttrPtg;
import org.apache.poi.ss.formula.ptg.BoolPtg;
import org.apache.poi.ss.formula.ptg.DeletedRef3DPtg;
import org.apache.poi.ss.formula.ptg.IntPtg;
import org.apache.poi.ss.formula.ptg.MemFuncPtg;
import org.apache.poi.ss.formula.ptg.MissingArgPtg;
import org.apache.poi.ss.formula.ptg.NumberPtg;
import org.apache.poi.ss.formula.ptg.ParenthesisPtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.Ref3DPtg;
import org.apache.poi.ss.formula.ptg.RefErrorPtg;
import org.apache.poi.ss.formula.ptg.RefNPtg;
import org.apache.poi.ss.formula.ptg.RefPtg;
import org.apache.poi.ss.formula.ptg.StringPtg;
import org.apache.xmlbeans.impl.common.NameUtil;
import utils.ByteUtils;
import utils.ConvertUtil;
import utils.ExtKt;

/* JADX INFO: compiled from: HPRTPrinterHelper.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\u0018\u0000 \f2\u00020\u0001:\u0002\u000b\fB\t\b\u0016¢\u0006\u0004\b\u0002\u0010\u0003B\u001d\b\u0016\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\u0004\b\u0002\u0010\bJ\b\u0010\t\u001a\u00020\nH\u0002¨\u0006\r"}, d2 = {"LHPRTAndroidSDK/HPRTPrinterHelper;", "Ljava/io/Serializable;", "<init>", "()V", f.X, "Landroid/content/Context;", "printerName", "", "(Landroid/content/Context;Ljava/lang/String;)V", "GetPrinterClass", "", "setOnProgress", "Companion", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class HPRTPrinterHelper implements Serializable {
    private static boolean Is_BLE_Type = false;
    public static final String NG = "NG";
    public static final int OUT_HEIGHT = 118;
    public static final int PARAMETER_ERROR = -2;
    public static final int PRINTER_ERROR = -4;
    public static final int PROPERTY_LENGTH = 500;
    private static Context PreContext = null;
    private static int PrinterClass = 0;
    private static String PrinterName = null;
    public static final int SEND_SUCCEED = 0;
    public static final int WIDTH = 292;
    public static final int WRITE_ERROR = -1;
    private static OnDataFilterListener filterListener;
    public static boolean isHex;
    public static boolean isLog;
    private static boolean isPortOpen;
    private static boolean isPrint;
    public static boolean isWriteLog;
    private static IPort printer;

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static final byte[][] BAYER_PATTERN = {new byte[]{24, 10, 12, 26, 35, 47, 49, 37}, new byte[]{8, 0, 2, 14, 45, Area3DPtg.sid, 61, 51}, new byte[]{MissingArgPtg.sid, 6, 4, 16, AreaErrPtg.sid, 57, Utf8.REPLACEMENT_BYTE, 53}, new byte[]{IntPtg.sid, 20, 18, 28, 33, MemFuncPtg.sid, 55, 39}, new byte[]{34, 46, 48, RefPtg.sid, AttrPtg.sid, 11, 13, 27}, new byte[]{RefNPtg.sid, Ref3DPtg.sid, DeletedRef3DPtg.sid, 50, 9, 1, 3, 15}, new byte[]{RefErrorPtg.sid, PaletteRecord.STANDARD_PALETTE_SIZE, 62, 52, StringPtg.sid, 7, 5, 17}, new byte[]{32, 40, 54, 38, NumberPtg.sid, ParenthesisPtg.sid, 19, BoolPtg.sid}};
    private static final PublicFunction PF = new PublicFunction();
    private static String LanguageEncode = "GBK";
    private static int BetweenWriteAndReadDelay = ConnectionResult.DRIVE_EXTERNAL_STORAGE_REQUIRED;
    private static String sPortType = "";

    /* JADX INFO: compiled from: HPRTPrinterHelper.kt */
    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0003H&¨\u0006\u0007À\u0006\u0003"}, d2 = {"LHPRTAndroidSDK/HPRTPrinterHelper$setOnProgress;", "", "onProgress", "", "progress", "", "failure", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public interface setOnProgress {
        void failure();

        void onProgress(int progress);
    }

    @JvmStatic
    public static final int CLS() throws Exception {
        return INSTANCE.CLS();
    }

    @JvmStatic
    public static final void IsBLEType(boolean z) {
        INSTANCE.IsBLEType(z);
    }

    @JvmStatic
    public static final boolean IsOpened() {
        return INSTANCE.IsOpened();
    }

    @JvmStatic
    public static final boolean PortClose() throws Exception {
        return INSTANCE.PortClose();
    }

    @JvmStatic
    public static final int PortOpen(String str) throws Exception {
        return INSTANCE.PortOpen(str);
    }

    @JvmStatic
    public static final int Print(String str, String str2) throws Exception {
        return INSTANCE.Print(str, str2);
    }

    @JvmStatic
    public static final byte[] ReadData(int i) {
        return INSTANCE.ReadData(i);
    }

    @JvmStatic
    public static final byte[] ReadDataMillisecond(int i) {
        return INSTANCE.ReadDataMillisecond(i);
    }

    @JvmStatic
    public static final int SetJustification(int i) throws Exception {
        return INSTANCE.SetJustification(i);
    }

    @JvmStatic
    public static final int WriteData(byte[] bArr) throws Exception {
        return INSTANCE.WriteData(bArr);
    }

    @JvmStatic
    public static final void cleanRead() {
        INSTANCE.cleanRead();
    }

    @JvmStatic
    public static final int end(int i) throws Exception {
        return INSTANCE.end(i);
    }

    @JvmStatic
    public static final byte[] getMTPrinterStatus(int i) {
        return INSTANCE.getMTPrinterStatus(i);
    }

    public static final String getNFCTemplate() {
        return INSTANCE.getNFCTemplate();
    }

    @JvmStatic
    public static final RFIDInfo getNFCVerifyInfo(int i) {
        return INSTANCE.getNFCVerifyInfo(i);
    }

    @JvmStatic
    public static final String getNewCPCLPrinterSN(int i, int i2) {
        return INSTANCE.getNewCPCLPrinterSN(i, i2);
    }

    @JvmStatic
    public static final String getNewPrinterSN(int i, int i2) {
        return INSTANCE.getNewPrinterSN(i, i2);
    }

    @JvmStatic
    public static final String getNfcUid() {
        return INSTANCE.getNfcUid();
    }

    @JvmStatic
    public static final String getPrintName(int i, int i2) throws Exception {
        return INSTANCE.getPrintName(i, i2);
    }

    @JvmStatic
    public static final byte[] getPrintStatusNotFilter(int i) {
        return INSTANCE.getPrintStatusNotFilter(i);
    }

    @JvmStatic
    public static final String getStandardESCPrinterName(int i) {
        return INSTANCE.getStandardESCPrinterName(i);
    }

    @JvmStatic
    public static final String getStandardESCPrinterSN(int i) {
        return INSTANCE.getStandardESCPrinterSN(i);
    }

    @JvmStatic
    public static final String getStandardESCPrinterVersion(int i) {
        return INSTANCE.getStandardESCPrinterVersion(i);
    }

    public static final String getTphModel() {
        return INSTANCE.getTphModel();
    }

    @JvmStatic
    public static final int getTphWidthTypeByKey445() {
        return INSTANCE.getTphWidthTypeByKey445();
    }

    @JvmStatic
    public static final String getVersion(int i, int i2) throws Exception {
        return INSTANCE.getVersion(i, i2);
    }

    @JvmStatic
    public static final void logcat(String str) {
        INSTANCE.logcat(str);
    }

    @JvmStatic
    public static final void logcatE(String str) {
        INSTANCE.logcatE(str);
    }

    @JvmStatic
    public static final void logcatI(String str) {
        INSTANCE.logcatI(str);
    }

    @JvmStatic
    public static final int printAreaSize(String str, String str2) throws Exception {
        return INSTANCE.printAreaSize(str, str2);
    }

    @JvmStatic
    public static final int printBitmap(Bitmap bitmap, int i, int i2, int i3) throws Exception {
        return INSTANCE.printBitmap(bitmap, i, i2, i3);
    }

    @JvmStatic
    public static final int printBitmapCPCL(Bitmap bitmap, int i, int i2, int i3, int i4, int i5, int i6) throws Exception {
        return INSTANCE.printBitmapCPCL(bitmap, i, i2, i3, i4, i5, i6);
    }

    @JvmStatic
    public static final int printBitmapCPCL(Bitmap bitmap, int i, int i2, int i3, int i4, int i5, int i6, int i7) throws Exception {
        return INSTANCE.printBitmapCPCL(bitmap, i, i2, i3, i4, i5, i6, i7);
    }

    @JvmStatic
    public static final int printBitmapLZO_ZPL(Bitmap bitmap, int i) {
        return INSTANCE.printBitmapLZO_ZPL(bitmap, i);
    }

    @JvmStatic
    public static final int printBitmapPackage(Bitmap bitmap, int i, int i2) throws Exception {
        return INSTANCE.printBitmapPackage(bitmap, i, i2);
    }

    @JvmStatic
    public static final int printDoubleColorBitmap(Bitmap bitmap, Bitmap bitmap2, int i, int i2, int i3, int i4, int i5, int i6) throws Exception {
        return INSTANCE.printDoubleColorBitmap(bitmap, bitmap2, i, i2, i3, i4, i5, i6);
    }

    @JvmStatic
    public static final int printImage(String str, String str2, Bitmap bitmap, boolean z, int i, int i2) throws Exception {
        return INSTANCE.printImage(str, str2, bitmap, z, i, i2);
    }

    @JvmStatic
    public static final void sendUpdateToPrint(InputStream inputStream, setOnProgress setonprogress, int i) {
        INSTANCE.sendUpdateToPrint(inputStream, setonprogress, i);
    }

    @JvmStatic
    public static final void setConnectState(int i) {
        INSTANCE.setConnectState(i);
    }

    @JvmStatic
    public static final boolean setESCFeed(int i) throws Exception {
        return INSTANCE.setESCFeed(i);
    }

    @JvmStatic
    public static final int setGapDetectA200U() throws Exception {
        return INSTANCE.setGapDetectA200U();
    }

    @JvmStatic
    public static final int setGapDetectCPCL() {
        return INSTANCE.setGapDetectCPCL();
    }

    @JvmStatic
    public static final int setGapDetectESC() throws Exception {
        return INSTANCE.setGapDetectESC();
    }

    @JvmStatic
    public static final int setGapDetectTL31W(int i) throws Exception {
        return INSTANCE.setGapDetectTL31W(i);
    }

    @JvmStatic
    public static final int setGapDetectTSPL() throws Exception {
        return INSTANCE.setGapDetectTSPL();
    }

    @JvmStatic
    public static final boolean setPollForm(int i) {
        return INSTANCE.setPollForm(i);
    }

    @JvmStatic
    public static final boolean setPollPrintDensity(int i) {
        return INSTANCE.setPollPrintDensity(i);
    }

    @JvmStatic
    public static final boolean setPrintFeed(int i) {
        return INSTANCE.setPrintFeed(i);
    }

    @JvmStatic
    public static final void setPrintPagePositionA200U(int i) {
        INSTANCE.setPrintPagePositionA200U(i);
    }

    @JvmStatic
    public static final boolean setPrintPageType(int i) {
        return INSTANCE.setPrintPageType(i);
    }

    @JvmStatic
    public static final int setXY(String str, String str2) throws Exception {
        return INSTANCE.setXY(str, str2);
    }

    @JvmStatic
    public static final int start() throws Exception {
        return INSTANCE.start();
    }

    public HPRTPrinterHelper() {
    }

    public HPRTPrinterHelper(Context context, String str) {
        PreContext = context;
        PrinterName = str;
        GetPrinterClass();
    }

    private final void GetPrinterClass() {
        PrinterClass = 33554945;
    }

    /* JADX INFO: compiled from: HPRTPrinterHelper.kt */
    @Metadata(d1 = {"\u0000\u0096\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u0012\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0016\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010 \n\u0000\n\u0002\u0010\u0002\n\u0002\b\u001e\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0005\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\bU\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\b\u0014\n\u0002\u0018\u0002\n\u0002\b\u0019\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u00105\u001a\u00020\u00172\u0006\u00106\u001a\u00020\u0017J\u0014\u00107\u001a\u00020\u00052\f\u00108\u001a\b\u0012\u0004\u0012\u00020\u001009J\u0010\u0010:\u001a\u00020;2\u0006\u0010<\u001a\u00020\u0005H\u0007J\b\u0010@\u001a\u00020\u0005H\u0007J\u0006\u0010A\u001a\u00020\u0005J\u0006\u0010B\u001a\u00020\u0005J\u0010\u0010C\u001a\u00020;2\u0006\u0010D\u001a\u00020\u0017H\u0007J\u0010\u0010E\u001a\u00020\u00052\u0006\u0010F\u001a\u00020\u0005H\u0007J\u0006\u0010G\u001a\u00020\u0005J\b\u0010H\u001a\u00020\u0017H\u0007J\u0010\u0010I\u001a\u00020\u00052\u0006\u0010J\u001a\u00020\u0010H\u0007J\b\u0010K\u001a\u00020\u0017H\u0007J\u000e\u0010L\u001a\u00020\u00052\u0006\u0010M\u001a\u00020\tJ\u0012\u0010N\u001a\u00020\u00052\b\u0010O\u001a\u0004\u0018\u00010\tH\u0007J\u0010\u0010P\u001a\u00020\t2\u0006\u0010Q\u001a\u00020\u0005H\u0007J\u000e\u0010R\u001a\u00020\t2\u0006\u0010Q\u001a\u00020\u0005J\u0010\u0010S\u001a\u00020\t2\u0006\u0010Q\u001a\u00020\u0005H\u0007J\u0016\u0010S\u001a\u00020\t2\u0006\u0010Q\u001a\u00020\u00052\u0006\u0010T\u001a\u00020\u0017J\u0016\u0010S\u001a\u00020\t2\u0006\u0010U\u001a\u00020\u00172\u0006\u0010V\u001a\u00020\u0005J\u000e\u0010W\u001a\u00020\t2\u0006\u0010Q\u001a\u00020\u0005J&\u0010X\u001a\u00020\u00052\u0006\u0010Y\u001a\u00020Z2\u0006\u0010[\u001a\u00020\u00052\u0006\u0010\\\u001a\u00020\u00052\u0006\u0010]\u001a\u00020\u0017J.\u0010X\u001a\u00020\u00172\u0006\u0010^\u001a\u00020Z2\u0006\u0010_\u001a\u00020\u00052\u0006\u0010`\u001a\u00020\u00052\u0006\u0010a\u001a\u00020\u00172\u0006\u0010b\u001a\u00020\u0017J\u001e\u0010X\u001a\u00020\u00172\u0006\u0010O\u001a\u00020\t2\u0006\u0010_\u001a\u00020\u00052\u0006\u0010a\u001a\u00020\u0017J\u0016\u0010c\u001a\u00020Z2\u0006\u0010^\u001a\u00020Z2\u0006\u0010b\u001a\u00020\u0017J\u0016\u0010d\u001a\u00020Z2\u0006\u0010^\u001a\u00020Z2\u0006\u0010b\u001a\u00020\u0017J\u000e\u0010e\u001a\u00020\t2\u0006\u0010Y\u001a\u00020ZJ\u0016\u0010f\u001a\u00020g2\u0006\u0010h\u001a\u00020\t2\u0006\u0010_\u001a\u00020\u0005J&\u0010i\u001a\u00020\u00052\u0006\u0010Y\u001a\u00020Z2\u0006\u0010[\u001a\u00020j2\u0006\u0010k\u001a\u00020j2\u0006\u0010\\\u001a\u00020\u0005J0\u0010l\u001a\u00020\u00052\u0006\u0010Y\u001a\u00020Z2\u0006\u0010[\u001a\u00020j2\u0006\u0010k\u001a\u00020j2\u0006\u0010\\\u001a\u00020\u00052\u0006\u0010]\u001a\u00020\u0017H\u0002J(\u0010m\u001a\u00020\u00052\u0006\u0010Y\u001a\u00020Z2\u0006\u0010[\u001a\u00020j2\u0006\u0010k\u001a\u00020j2\u0006\u0010\\\u001a\u00020\u0005H\u0002J\u0012\u0010n\u001a\u00020;2\b\u0010o\u001a\u0004\u0018\u00010\u0010H\u0007J\u0012\u0010p\u001a\u00020;2\b\u0010o\u001a\u0004\u0018\u00010\u0010H\u0007J\u0012\u0010q\u001a\u00020;2\b\u0010o\u001a\u0004\u0018\u00010\u0010H\u0007J&\u0010r\u001a\u00020;2\b\u0010s\u001a\u0004\u0018\u00010t2\b\u0010u\u001a\u0004\u0018\u00010v2\b\b\u0002\u0010w\u001a\u00020\u0005H\u0007J:\u0010x\u001a\u00020;2\u0006\u0010`\u001a\u00020\u00052\u0006\u0010y\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010z\u001a\u00020\u00052\u0006\u0010{\u001a\u00020\u00102\n\b\u0002\u0010|\u001a\u0004\u0018\u00010\tJ<\u0010}\u001a\u00020\t2\u0006\u0010`\u001a\u00020\u00052\u0006\u0010y\u001a\u00020\u00102\u0006\u0010z\u001a\u00020\u00052\u0006\u0010{\u001a\u00020\u00102\b\b\u0002\u0010~\u001a\u00020\u00172\n\b\u0002\u0010\u007f\u001a\u0004\u0018\u00010\u0010J*\u0010\u0080\u0001\u001a\u00020\t2\t\b\u0002\u0010\u0081\u0001\u001a\u00020\u00052\t\b\u0002\u0010\u0082\u0001\u001a\u00020\u00052\t\b\u0002\u0010\u0083\u0001\u001a\u00020\u0005H\u0002J\u0019\u0010\u0084\u0001\u001a\u00020;2\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010z\u001a\u00020\u0005H\u0002J\u0011\u0010\u0085\u0001\u001a\u00020;2\u0006\u0010y\u001a\u00020\u0010H\u0002J\u0019\u0010\u0086\u0001\u001a\u00020;2\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010z\u001a\u00020\u0005H\u0002J)\u0010\u0087\u0001\u001a\u00020;2\u0006\u0010`\u001a\u00020\u00052\u0006\u0010y\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010z\u001a\u00020\u0005H\u0002J\u0019\u0010\u0088\u0001\u001a\u00020;2\u0006\u0010y\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0010H\u0002J-\u0010\u0089\u0001\u001a\u00020;2\u0006\u0010y\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010z\u001a\u00020\u00052\n\b\u0002\u0010|\u001a\u0004\u0018\u00010\tH\u0002J\u0012\u0010\u008a\u0001\u001a\u00020\t2\u0007\u0010\u008b\u0001\u001a\u00020tH\u0002J\u001c\u0010\u008c\u0001\u001a\u00020\u00102\u0007\u0010\u008d\u0001\u001a\u00020\u00052\b\b\u0002\u0010`\u001a\u00020\u0005H\u0007J\u0011\u0010\u008e\u0001\u001a\u00020\t2\u0006\u0010M\u001a\u00020\tH\u0002J\u001a\u0010\u008f\u0001\u001a\u00020\t2\u0006\u0010M\u001a\u00020\t2\u0007\u0010\u0090\u0001\u001a\u00020\u0005H\u0002J\u001c\u0010\u0091\u0001\u001a\u00020\u00102\u0007\u0010\u008d\u0001\u001a\u00020\u00052\b\b\u0002\u0010`\u001a\u00020\u0005H\u0007J\u001a\u0010\u0092\u0001\u001a\u00020\u00052\u0007\u0010\u0093\u0001\u001a\u00020\u00102\u0006\u0010_\u001a\u00020\u0010H\u0007J\t\u0010\u0094\u0001\u001a\u00020\u0005H\u0007J=\u0010\u0095\u0001\u001a\u00020\u00052\u0007\u0010\u0096\u0001\u001a\u00020\u00102\u0007\u0010\u0097\u0001\u001a\u00020\u00102\u0006\u0010Y\u001a\u00020Z2\u0007\u0010\u0098\u0001\u001a\u00020\u00172\u0007\u0010\u0099\u0001\u001a\u00020\u00052\u0006\u0010`\u001a\u00020\u0005H\u0007J!\u0010\u009a\u0001\u001a\u00020\t2\u0006\u0010Y\u001a\u00020Z2\u0006\u0010[\u001a\u00020j2\u0006\u0010k\u001a\u00020jH\u0002J \u0010l\u001a\u00020\t2\u0006\u0010Y\u001a\u00020Z2\u0006\u0010[\u001a\u00020j2\u0006\u0010k\u001a\u00020jH\u0002JF\u0010X\u001a\u00020\u00052\u0007\u0010\u0096\u0001\u001a\u00020\u00102\u0007\u0010\u0097\u0001\u001a\u00020\u00102\u0007\u0010\u0093\u0001\u001a\u00020\u00102\u0006\u0010_\u001a\u00020\u00102\u0007\u0010\u009b\u0001\u001a\u00020\t2\u0007\u0010\u0098\u0001\u001a\u00020\u00172\u0007\u0010\u0099\u0001\u001a\u00020\u0005H\u0002J\u0011\u0010\u009c\u0001\u001a\u00020\t2\u0006\u0010\u0011\u001a\u00020\u0005H\u0002J\u001b\u0010\u009d\u0001\u001a\u00020\u00052\u0007\u0010\u009e\u0001\u001a\u00020\u00102\u0007\u0010\u009f\u0001\u001a\u00020\u0010H\u0007J\u0017\u0010X\u001a\u00020\u00052\u0006\u0010^\u001a\u00020Z2\u0007\u0010 \u0001\u001a\u00020\u0005J\u001f\u0010X\u001a\u00020\u00052\u0007\u0010\u0093\u0001\u001a\u00020\u00052\u0006\u0010_\u001a\u00020\u00052\u0006\u0010M\u001a\u00020\tJ%\u0010¡\u0001\u001a\u0004\u0018\u00010\t2\u0007\u0010¢\u0001\u001a\u00020\t2\u0007\u0010\u0093\u0001\u001a\u00020\u00052\u0006\u0010_\u001a\u00020\u0005H\u0002J$\u0010£\u0001\u001a\u0004\u0018\u00010\t2\u0006\u0010M\u001a\u00020\t2\u0007\u0010\u0093\u0001\u001a\u00020\u00052\u0006\u0010_\u001a\u00020\u0005H\u0002J\u0012\u0010¤\u0001\u001a\u0004\u0018\u00010\t2\u0007\u0010\u008d\u0001\u001a\u00020\u0005J\u0014\u0010¥\u0001\u001a\u0004\u0018\u00010\t2\u0007\u0010\u008d\u0001\u001a\u00020\u0005H\u0007J\u0014\u0010¦\u0001\u001a\u0004\u0018\u00010\t2\u0007\u0010\u008d\u0001\u001a\u00020\u0005H\u0007J\u001b\u0010§\u0001\u001a\u0004\u0018\u00010\t2\u0007\u0010\u008d\u0001\u001a\u00020\u0005H\u0086@¢\u0006\u0003\u0010¨\u0001J\u0007\u0010©\u0001\u001a\u00020;J\u0012\u0010ª\u0001\u001a\u00020\u00172\u0007\u0010«\u0001\u001a\u00020\u0005H\u0007J\u0012\u0010¬\u0001\u001a\u00020\u00172\u0007\u0010\u00ad\u0001\u001a\u00020\u0005H\u0007J\u0010\u0010®\u0001\u001a\u00020\u00172\u0007\u0010\u00ad\u0001\u001a\u00020\u0005J\u0007\u0010¯\u0001\u001a\u00020;J\u0012\u0010°\u0001\u001a\u00020;2\u0007\u0010\u00ad\u0001\u001a\u00020\u0005H\u0007J\u0007\u0010±\u0001\u001a\u00020\u0005J\u0007\u0010²\u0001\u001a\u00020\u0005J\u0012\u0010³\u0001\u001a\u00020\u00172\u0007\u0010´\u0001\u001a\u00020\u0005H\u0007J\t\u0010µ\u0001\u001a\u00020\u0005H\u0007J\u0012\u0010¶\u0001\u001a\u00020\u00052\u0007\u0010·\u0001\u001a\u00020\u0005H\u0007J\u001b\u0010¸\u0001\u001a\u00020\u00052\u0007\u0010¹\u0001\u001a\u00020\u00102\u0007\u0010º\u0001\u001a\u00020\u0010H\u0007J\u001a\u0010»\u0001\u001a\u00020\u00052\u0006\u0010Y\u001a\u00020Z2\u0007\u0010¼\u0001\u001a\u00020\u0005H\u0007J\t\u0010½\u0001\u001a\u00020\u0005H\u0007J\t\u0010¾\u0001\u001a\u00020\u0005H\u0007J\t\u0010¿\u0001\u001a\u00020\u0005H\u0007J\u0011\u0010À\u0001\u001a\u00020\u00052\u0006\u0010`\u001a\u00020\u0005H\u0007J*\u0010X\u001a\u00020\u00052\u0006\u0010^\u001a\u00020Z2\u0006\u0010`\u001a\u00020\u00052\u0007\u0010Á\u0001\u001a\u00020\u00052\u0007\u0010Â\u0001\u001a\u00020\u0005H\u0007JH\u0010Ã\u0001\u001a\u00020\u00052\b\u0010^\u001a\u0004\u0018\u00010Z2\u0007\u0010Ä\u0001\u001a\u00020\u00052\u0007\u0010Å\u0001\u001a\u00020\u00052\u0006\u0010`\u001a\u00020\u00052\u0007\u0010Á\u0001\u001a\u00020\u00052\u0007\u0010«\u0001\u001a\u00020\u00052\u0007\u0010Æ\u0001\u001a\u00020\u0005H\u0007JQ\u0010Ã\u0001\u001a\u00020\u00052\b\u0010^\u001a\u0004\u0018\u00010Z2\u0007\u0010Ä\u0001\u001a\u00020\u00052\u0007\u0010Å\u0001\u001a\u00020\u00052\u0006\u0010`\u001a\u00020\u00052\u0007\u0010Á\u0001\u001a\u00020\u00052\u0007\u0010Ç\u0001\u001a\u00020\u00052\u0007\u0010«\u0001\u001a\u00020\u00052\u0007\u0010Æ\u0001\u001a\u00020\u0005H\u0007J8\u0010È\u0001\u001a\u0004\u0018\u00010\t2\b\u0010^\u001a\u0004\u0018\u00010Z2\u0007\u0010Ä\u0001\u001a\u00020\u00052\u0007\u0010Å\u0001\u001a\u00020\u00052\u0006\u0010`\u001a\u00020\u00052\u0007\u0010Á\u0001\u001a\u00020\u0005H\u0002J6\u0010É\u0001\u001a\u00020\u00052\b\u0010^\u001a\u0004\u0018\u00010Z2\u0007\u0010Ä\u0001\u001a\u00020\u00052\u0007\u0010Å\u0001\u001a\u00020\u00052\u0006\u0010`\u001a\u00020\u00052\u0007\u0010Á\u0001\u001a\u00020\u0005H\u0002J\t\u0010Ê\u0001\u001a\u00020;H\u0007J\n\u0010Ë\u0001\u001a\u0005\u0018\u00010Ì\u0001J\u0013\u0010Í\u0001\u001a\u00020\u00052\n\u0010Î\u0001\u001a\u0005\u0018\u00010Ì\u0001J\t\u0010Ï\u0001\u001a\u00020\u0005H\u0007J\u000b\u0010Ð\u0001\u001a\u0004\u0018\u00010\u0010H\u0007J\u0015\u0010Ù\u0001\u001a\u0005\u0018\u00010Ú\u00012\u0007\u0010\u008d\u0001\u001a\u00020\u0005H\u0007J\u000f\u0010Û\u0001\u001a\u00020\t2\u0006\u0010M\u001a\u00020\tJ\u0012\u0010Ü\u0001\u001a\u00020\u00172\u0007\u0010´\u0001\u001a\u00020\u0005H\u0007J\u001c\u0010Ý\u0001\u001a\u00020\u00102\u0007\u0010\u008d\u0001\u001a\u00020\u00052\b\b\u0002\u0010`\u001a\u00020\u0005H\u0007J\u001c\u0010Þ\u0001\u001a\u00020\u00102\u0007\u0010\u008d\u0001\u001a\u00020\u00052\b\b\u0002\u0010`\u001a\u00020\u0005H\u0007J\u0012\u0010ß\u0001\u001a\u00020\u00102\u0007\u0010\u008d\u0001\u001a\u00020\u0005H\u0007J\u0012\u0010à\u0001\u001a\u00020\u00102\u0007\u0010\u008d\u0001\u001a\u00020\u0005H\u0007J\u0012\u0010á\u0001\u001a\u00020\u00102\u0007\u0010\u008d\u0001\u001a\u00020\u0005H\u0007J\u000f\u0010â\u0001\u001a\u00020\u00172\u0006\u0010`\u001a\u00020\u0005J\u000f\u0010ã\u0001\u001a\u00020\u00172\u0006\u0010`\u001a\u00020\u0005J\u0007\u0010ä\u0001\u001a\u00020\u0017J\u0007\u0010å\u0001\u001a\u00020\u0017J$\u0010æ\u0001\u001a\u00020\u00052\b\u0010^\u001a\u0004\u0018\u00010Z2\u0007\u0010 \u0001\u001a\u00020\u00052\u0006\u0010w\u001a\u00020\u0005H\u0007J1\u0010\u009a\u0001\u001a\u00020\u00052\u0006\u0010Y\u001a\u00020Z2\u0006\u0010[\u001a\u00020j2\u0006\u0010k\u001a\u00020j2\u0006\u0010\\\u001a\u00020\u00052\u0006\u0010]\u001a\u00020\u0017H\u0002JT\u0010ç\u0001\u001a\u00020\u00052\t\u0010è\u0001\u001a\u0004\u0018\u00010Z2\t\u0010é\u0001\u001a\u0004\u0018\u00010Z2\u0007\u0010Ä\u0001\u001a\u00020\u00052\u0007\u0010Å\u0001\u001a\u00020\u00052\u0006\u0010`\u001a\u00020\u00052\u0007\u0010Á\u0001\u001a\u00020\u00052\u0007\u0010«\u0001\u001a\u00020\u00052\u0007\u0010Æ\u0001\u001a\u00020\u0005H\u0007J\u0011\u0010ê\u0001\u001a\u00020;2\b\u0010/\u001a\u0004\u0018\u000100J\u0012\u0010ë\u0001\u001a\u00020\u00172\u0007\u0010´\u0001\u001a\u00020\u0005H\u0007J\u0007\u0010ì\u0001\u001a\u00020\u0005J\u0011\u0010í\u0001\u001a\u00020\t2\b\b\u0002\u0010`\u001a\u00020\u0010J\u0011\u0010î\u0001\u001a\u00030ï\u00012\u0007\u0010ð\u0001\u001a\u00020\tJ$\u0010ñ\u0001\u001a\u00020;2\u0006\u0010M\u001a\u00020\t2\t\b\u0002\u0010\u0099\u0001\u001a\u00020\u00102\b\u0010ò\u0001\u001a\u00030ï\u0001J\r\u0010ó\u0001\u001a\u00020\u0010*\u00020\u0005H\u0002J\u0010\u0010ô\u0001\u001a\u00020\u00102\u0007\u0010õ\u0001\u001a\u00020\u0005J\u0010\u0010ö\u0001\u001a\u00020\u00052\u0007\u0010÷\u0001\u001a\u00020\u0010J\u000f\u0010ø\u0001\u001a\u00020\u00102\u0006\u0010M\u001a\u00020\tJ\"\u0010ù\u0001\u001a\u00020\u00102\u0007\u0010ð\u0001\u001a\u00020\t2\u0007\u0010µ\u0001\u001a\u00020\u00052\u0007\u0010¶\u0001\u001a\u00020\u0005J\u0010\u0010ú\u0001\u001a\u00020\u00102\u0007\u0010û\u0001\u001a\u00020\u0005J\u0010\u0010ü\u0001\u001a\u00020\u00052\u0007\u0010ý\u0001\u001a\u00020\u0010J\u0011\u0010þ\u0001\u001a\u00020;2\b\b\u0002\u0010`\u001a\u00020\u0005J\u0007\u0010ÿ\u0001\u001a\u00020\u0005J\u0007\u0010\u0080\u0002\u001a\u00020\u0010J\u000f\u0010\u0080\u0002\u001a\u00020\u00102\u0006\u0010M\u001a\u00020\tJ\u0010\u0010\u0081\u0002\u001a\u00020\u00172\u0007\u0010\u0082\u0002\u001a\u00020\u0005R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u0019\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b¢\u0006\n\n\u0002\u0010\f\u001a\u0004\b\n\u0010\u000bR\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0082\u000e¢\u0006\u0002\n\u0000R\"\u0010\u0013\u001a\u0004\u0018\u00010\u00122\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0018\u001a\u00020\u00198\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u001a\u001a\u00020\u0010X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u001c\"\u0004\b\u001d\u0010\u001eR\u001a\u0010\u001f\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b \u0010!\"\u0004\b\"\u0010#R\u000e\u0010$\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010%\u001a\u00020\u0010X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010&\u001a\u00020\u0017X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010'\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u0012\u0010(\u001a\u00020\u00178\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0012\u0010)\u001a\u00020\u00178\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0012\u0010*\u001a\u00020\u00178\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u001a\u0010+\u001a\u00020\u0017X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b+\u0010,\"\u0004\b-\u0010.R\u001c\u0010/\u001a\u0004\u0018\u000100X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b1\u00102\"\u0004\b3\u00104R\u001c\u0010=\u001a\u0004\u0018\u00010\u00108FX\u0087\u0004¢\u0006\f\u0012\u0004\b>\u0010\u0003\u001a\u0004\b?\u0010\u001cR\u001f\u0010Ñ\u0001\u001a\u0004\u0018\u00010\u00108FX\u0087\u0004¢\u0006\u000e\u0012\u0005\bÒ\u0001\u0010\u0003\u001a\u0005\bÓ\u0001\u0010\u001cR\u0014\u0010Ô\u0001\u001a\u00020\t8F¢\u0006\b\u001a\u0006\bÕ\u0001\u0010Ö\u0001R\u0014\u0010×\u0001\u001a\u00020\t8F¢\u0006\b\u001a\u0006\bØ\u0001\u0010Ö\u0001R\u000f\u0010\u0083\u0002\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000f\u0010\u0084\u0002\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000f\u0010\u0085\u0002\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000f\u0010\u0086\u0002\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000f\u0010\u0087\u0002\u001a\u00020\u0010X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0088\u0002"}, d2 = {"LHPRTAndroidSDK/HPRTPrinterHelper$Companion;", "", "<init>", "()V", "WIDTH", "", "OUT_HEIGHT", "BAYER_PATTERN", "", "", "getBAYER_PATTERN", "()[[B", "[[B", "PreContext", "Landroid/content/Context;", "PrinterName", "", "value", "LHPRTAndroidSDK/IPort;", "printer", "getPrinter", "()LHPRTAndroidSDK/IPort;", "isPortOpen", "", "PF", "LHPRTAndroidSDK/PublicFunction;", "LanguageEncode", "getLanguageEncode", "()Ljava/lang/String;", "setLanguageEncode", "(Ljava/lang/String;)V", "BetweenWriteAndReadDelay", "getBetweenWriteAndReadDelay", "()I", "setBetweenWriteAndReadDelay", "(I)V", "PrinterClass", "sPortType", "Is_BLE_Type", "PROPERTY_LENGTH", "isLog", "isWriteLog", "isHex", "isPrint", "()Z", "setPrint", "(Z)V", "filterListener", "LHPRTAndroidSDK/OnDataFilterListener;", "getFilterListener", "()LHPRTAndroidSDK/OnDataFilterListener;", "setFilterListener", "(LHPRTAndroidSDK/OnDataFilterListener;)V", "changingPrintQuality", "good", "savePrinterSetting", "list", "", "setConnectState", "", "connectState", "tphModel", "getTphModel$annotations", "getTphModel", "getTphWidthTypeByKey445", "getPaperTypeV1", "getDensity", "IsBLEType", "isBLEType", "SetJustification", "justification", "getHeadControlMethod", "IsOpened", "PortOpen", "portSetting", "PortClose", "PrintData", "data", "WriteData", "bData", "ReadData", "time", "ReadDataNotFilter", "ReadDataMillisecond", "checkListener", "checkConnectState", "millisecond", "ReadDataMillisecondNotFilter", "printBitmap", "bmp", "Landroid/graphics/Bitmap;", "halftoneType", "printdpi", "isCompress", "bitmap", "height", "type", "compress", WXBasicComponentType.RECYCLER, "orderDither", "errorDiffusionFloyd", "threshold", "getRealSendData", "LHPRTAndroidSDK/bean/ResultSendData;", "dataBytes", "saveBitmap", "", "scaleMode", "CreateBitmapPrintDatas", "SaveBmpDatas", "logcat", "log", "logcatI", "logcatE", "sendUpdateToPrint", "fileInputStream", "Ljava/io/InputStream;", "onprogress", "LHPRTAndroidSDK/HPRTPrinterHelper$setOnProgress;", "packageSize", "send", "command", "encode", "name", "valueBytes", "getValue", "isD31S", "instruct", "readValueData", "totalTimeout", "sliceTimeout", "tailTimeout", "getKey", "getVal", "getDo", "setDo", "setVal", "setKey", "InputStreamToByte", "is", "getVersion", AlbumLoader.COLUMN_COUNT, "extractTextResponse", "writeAndReadTextResponse", "timeout", "getPrintName", "printAreaSize", "width", "CLS", "printImage", "x_pos", "y_pos", "isNegate", Constants.KEY_MODEL, "PrintBitmap", "code_data", "intTo4Bytes", "Print", "strnum", "strcopies", "bitType", "AddPrintNVImage", "bDatas", "lzoCompress", "getPrintStatus", "getPrintStatusNotFilter", "getMTPrinterStatus", "getMTPrinterStatusNotFilter", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "clearCache", "setPollPrintDensity", "density", "setPrintPageType", PackageRelationship.TYPE_ATTRIBUTE_NAME, "setPageType", "restartPrint", "setPrintPagePositionA200U", "saveParamZone", "saveParamAndResetPrinter", "setPrintFeed", "feed", "start", "end", "quantity", "setXY", "X", "Y", "printBitmapLZO_ZPL", "algorithmMode", "setGapDetectTSPL", "setGapDetectESC", "setGapDetectA200U", "setGapDetectTL31W", "compressType", "byteLength", "printBitmapCPCL", Constants.Name.X, Constants.Name.Y, "number", "printHeadWidth", "getBitmapCPCLData", "sendBitmapCPCL", "cleanRead", "getWifiParameter", "LHPRTAndroidSDK/WIFIBean;", "setWifiParameter", "wifiBean", "setGapDetectCPCL", "getNfcUid", "nFCTemplate", "getNFCTemplate$annotations", "getNFCTemplate", "battery", "getBattery", "()[B", "pooliPaperType", "getPooliPaperType", "getNFCVerifyInfo", "LHPRTAndroidSDK/bean/RFIDInfo;", "deleteZeroByte", "setPollForm", "getNewPrinterSN", "getNewCPCLPrinterSN", "getStandardESCPrinterSN", "getStandardESCPrinterName", "getStandardESCPrinterVersion", "setA200UPaperType", "setPaperType", "checkConnection", "checkConnectionSunmi", "printBitmapPackage", "printDoubleColorBitmap", "blackBitmap", "redBitmap", "setOnDataFilterListener", "setESCFeed", "getPackageSize", "reqD31SConfig", "getConfig", "LHPRTAndroidSDK/bean/D31SConfig;", "bytes", "setConfig", BindingXConstants.KEY_CONFIG, "toHex", "getCodePage", "codePagePos", "getCodePagePos", "codePage", "byteToHexWithEmpty", "getString", "getCharSet", "pos", "getCharSetPos", "charSet", "setPageMode", "getCarbonSurplus", "getCarbonNo", "setDensity", MediaFormatExtraConstants.KEY_LEVEL, "PARAMETER_ERROR", "WRITE_ERROR", "SEND_SUCCEED", "PRINTER_ERROR", HPRTPrinterHelper.NG, "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        @JvmStatic
        public static /* synthetic */ void getNFCTemplate$annotations() {
        }

        @JvmStatic
        public static /* synthetic */ void getTphModel$annotations() {
        }

        private Companion() {
        }

        public final byte[][] getBAYER_PATTERN() {
            return HPRTPrinterHelper.BAYER_PATTERN;
        }

        public final IPort getPrinter() {
            return HPRTPrinterHelper.printer;
        }

        public final String getLanguageEncode() {
            return HPRTPrinterHelper.LanguageEncode;
        }

        public final void setLanguageEncode(String str) {
            Intrinsics.checkNotNullParameter(str, "<set-?>");
            HPRTPrinterHelper.LanguageEncode = str;
        }

        public final int getBetweenWriteAndReadDelay() {
            return HPRTPrinterHelper.BetweenWriteAndReadDelay;
        }

        public final void setBetweenWriteAndReadDelay(int i) {
            HPRTPrinterHelper.BetweenWriteAndReadDelay = i;
        }

        public final boolean isPrint() {
            return HPRTPrinterHelper.isPrint;
        }

        public final void setPrint(boolean z) {
            HPRTPrinterHelper.isPrint = z;
        }

        public final OnDataFilterListener getFilterListener() {
            return HPRTPrinterHelper.filterListener;
        }

        public final void setFilterListener(OnDataFilterListener onDataFilterListener) {
            HPRTPrinterHelper.filterListener = onDataFilterListener;
        }

        public final boolean changingPrintQuality(boolean good) {
            StringBuilder sb = new StringBuilder();
            sb.append(good);
            Log.e("changingPrintQuality", sb.toString());
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            if (!good) {
                IPort printer2 = getPrinter();
                Intrinsics.checkNotNull(printer2);
                printer2.WriteData(ExtKt.hexStringToBytes("1B 1C 26 20 56 31 20 64 6F 20 22 6E 66 63 5F 61 6C 61 72 6D 22 0D 0A"));
                String str = new String(ReadDataMillisecond(200), Charsets.UTF_8);
                Log.e("changingPrintQuality1", str);
                return Intrinsics.areEqual(WXModalUIModule.OK, str);
            }
            IPort printer3 = getPrinter();
            Intrinsics.checkNotNull(printer3);
            printer3.WriteData(ExtKt.hexStringToBytes("1B 1C 26 20 56 31 20 64 6F 20 22 6E 66 63 5F 72 65 64 65 74 65 63 74 22 0D 0A"));
            String str2 = new String(ReadDataMillisecond(200), Charsets.UTF_8);
            if (Intrinsics.areEqual(WXModalUIModule.OK, str2)) {
                return true;
            }
            Log.e("changingPrintQuality2", str2);
            return false;
        }

        public final int savePrinterSetting(List<String> list) {
            Intrinsics.checkNotNullParameter(list, "list");
            if (getPrinter() == null) {
                return -1;
            }
            int size = list.size();
            int i = 0;
            for (int i2 = 0; i2 < size; i2++) {
                String str = list.get(i2);
                if (!TextUtils.isEmpty(str)) {
                    byte[] bArrHexStringToBytes = ExtKt.hexStringToBytes(str);
                    IPort printer = getPrinter();
                    Intrinsics.checkNotNull(printer);
                    printer.setConnectState(1);
                    IPort printer2 = getPrinter();
                    Intrinsics.checkNotNull(printer2);
                    printer2.WriteData(bArrHexStringToBytes);
                    IPort printer3 = getPrinter();
                    Intrinsics.checkNotNull(printer3);
                    printer3.ReadDataMillisecond(200);
                    i++;
                }
            }
            if (i > 0) {
                return saveParamZone();
            }
            return -1;
        }

        @JvmStatic
        public final void setConnectState(int connectState) {
            if (getPrinter() == null) {
                return;
            }
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(connectState);
        }

        public final String getTphModel() {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            byte[] bArrHexStringToBytes = ExtKt.hexStringToBytes("1B 1C 26 20 56 31 20 67 65 74 76 61 6C 20 22 74 70 68 5F 6D 6F 64 65 6C 22 0D 0A");
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            printer2.WriteData(bArrHexStringToBytes);
            byte[] bArrReadDataMillisecond = ReadDataMillisecond(500);
            if (bArrReadDataMillisecond.length > 0) {
                return new String(deleteZeroByte(bArrReadDataMillisecond), Charsets.UTF_8);
            }
            return null;
        }

        @JvmStatic
        public final int getTphWidthTypeByKey445() {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            byte[] bArrHexStringToBytes = ExtKt.hexStringToBytes("1B 1C 26 20 56 31 20 67 65 74 6B 65 79 0D 0A 01 BD 01 01");
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            printer2.WriteData(bArrHexStringToBytes);
            byte[] bArrReadDataMillisecond = ReadDataMillisecond(500);
            if (bArrReadDataMillisecond.length == 0) {
                return -1;
            }
            return bArrReadDataMillisecond[0] & 255;
        }

        public final int getPaperTypeV1() {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            byte[] bArrHexStringToBytes = ExtKt.hexStringToBytes("1B 1C 26 20 56 31 20 67 65 74 6B 65 79 0D 0A 01 CE 00 01");
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            printer2.WriteData(bArrHexStringToBytes);
            byte[] bArrReadDataMillisecond = ReadDataMillisecond(500);
            if (bArrReadDataMillisecond.length == 0) {
                return -1;
            }
            return bArrReadDataMillisecond[0];
        }

        public final int getDensity() {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            byte[] bArrHexStringToBytes = ExtKt.hexStringToBytes("1B 1C 26 20 56 31 20 67 65 74 6B 65 79 0D 0A 01 CB 00 01");
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            printer2.WriteData(bArrHexStringToBytes);
            byte[] bArrReadDataMillisecond = ReadDataMillisecond(500);
            if (bArrReadDataMillisecond.length == 0) {
                return -1;
            }
            return bArrReadDataMillisecond[0];
        }

        @JvmStatic
        public final void IsBLEType(boolean isBLEType) {
            HPRTPrinterHelper.Is_BLE_Type = isBLEType;
        }

        /* JADX WARN: Removed duplicated region for block: B:10:0x0011  */
        /* JADX WARN: Removed duplicated region for block: B:9:0x000f  */
        @JvmStatic
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public final int SetJustification(int justification) throws Exception {
            byte b;
            if (justification == 0) {
                b = 0;
            } else if (justification == 1) {
                b = 1;
            } else {
                if (justification != 2) {
                    switch (justification) {
                    }
                }
                b = 2;
            }
            byte[] bArr = {27, 97, b};
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            int iWriteData = printer2.WriteData(bArr);
            IPort printer3 = getPrinter();
            Intrinsics.checkNotNull(printer3);
            printer3.setConnectState(0);
            return iWriteData;
        }

        public final int getHeadControlMethod() {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            byte[] bArrHexStringToBytes = ExtKt.hexStringToBytes("1b 1c 26 20 56 31 20 67 65 74 6b 65 79 0d 0a 01 ca 00 01");
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            printer2.WriteData(bArrHexStringToBytes);
            byte[] bArrReadDataMillisecond = ReadDataMillisecond(2000);
            if (bArrReadDataMillisecond == null) {
                return -1;
            }
            IPort printer3 = getPrinter();
            Intrinsics.checkNotNull(printer3);
            printer3.setConnectState(0);
            if (bArrReadDataMillisecond.length == 0) {
                return 0;
            }
            return bArrReadDataMillisecond[0];
        }

        @JvmStatic
        public final boolean IsOpened() {
            return HPRTPrinterHelper.isPortOpen;
        }

        @JvmStatic
        public final boolean PortClose() throws Exception {
            boolean zClosePort;
            if (getPrinter() != null) {
                IPort printer = getPrinter();
                Intrinsics.checkNotNull(printer);
                zClosePort = printer.ClosePort();
            } else {
                zClosePort = true;
            }
            HPRTPrinterHelper.isPortOpen = !zClosePort;
            return zClosePort;
        }

        public final int PrintData(byte[] data) throws Exception {
            Intrinsics.checkNotNullParameter(data, "data");
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            int iWriteData = printer2.WriteData(data, data.length);
            IPort printer3 = getPrinter();
            Intrinsics.checkNotNull(printer3);
            printer3.setConnectState(0);
            return iWriteData;
        }

        @JvmStatic
        public final int WriteData(byte[] bData) throws Exception {
            if (getPrinter() == null) {
                return -1;
            }
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            return printer2.WriteData(bData);
        }

        @JvmStatic
        public final byte[] ReadData(int time) {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            byte[] bArrReadData = printer.ReadData(time);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            printer2.setConnectState(0);
            byte[] bArrFilter = DataFilter.filter(bArrReadData, getFilterListener());
            Intrinsics.checkNotNullExpressionValue(bArrFilter, "filter(...)");
            return bArrFilter;
        }

        public final byte[] ReadDataNotFilter(int time) {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            byte[] bArrReadData = printer.ReadData(time);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            printer2.setConnectState(0);
            Intrinsics.checkNotNull(bArrReadData);
            return bArrReadData;
        }

        @JvmStatic
        public final byte[] ReadDataMillisecond(int time) {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            byte[] bArrReadDataMillisecond = printer.ReadDataMillisecond(time);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            printer2.setConnectState(0);
            byte[] bArrFilter = DataFilter.filter(bArrReadDataMillisecond, getFilterListener());
            Intrinsics.checkNotNullExpressionValue(bArrFilter, "filter(...)");
            return bArrFilter;
        }

        public final byte[] ReadDataMillisecond(int time, boolean checkListener) {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            byte[] bArrReadDataMillisecond = printer.ReadDataMillisecond(time);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            printer2.setConnectState(0);
            if (checkListener && getFilterListener() == null) {
                Intrinsics.checkNotNull(bArrReadDataMillisecond);
                return bArrReadDataMillisecond;
            }
            byte[] bArrFilter = DataFilter.filter(bArrReadDataMillisecond, getFilterListener());
            Intrinsics.checkNotNullExpressionValue(bArrFilter, "filter(...)");
            return bArrFilter;
        }

        public final byte[] ReadDataMillisecond(boolean checkConnectState, int millisecond) {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            byte[] bArrReadDataMillisecond = printer.ReadDataMillisecond(checkConnectState, millisecond);
            if (getFilterListener() == null) {
                Intrinsics.checkNotNull(bArrReadDataMillisecond);
                return bArrReadDataMillisecond;
            }
            byte[] bArrFilter = DataFilter.filter(bArrReadDataMillisecond, getFilterListener());
            Intrinsics.checkNotNullExpressionValue(bArrFilter, "filter(...)");
            return bArrFilter;
        }

        public final byte[] ReadDataMillisecondNotFilter(int time) {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            byte[] bArrReadDataMillisecond = printer.ReadDataMillisecond(time);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            printer2.setConnectState(0);
            Intrinsics.checkNotNull(bArrReadDataMillisecond);
            return bArrReadDataMillisecond;
        }

        public final int printBitmap(Bitmap bmp, int halftoneType, int printdpi, boolean isCompress) throws Exception {
            Intrinsics.checkNotNullParameter(bmp, "bmp");
            return CreateBitmapPrintDatas(bmp, (byte) halftoneType, (byte) 0, printdpi, isCompress);
        }

        public final boolean printBitmap(Bitmap bitmap, int height, int type, boolean compress, boolean recycler) throws Exception {
            Bitmap bitmapErrorDiffusionFloyd;
            Intrinsics.checkNotNullParameter(bitmap, "bitmap");
            if (type == 1) {
                bitmapErrorDiffusionFloyd = errorDiffusionFloyd(bitmap, recycler);
            } else if (type == 2) {
                bitmapErrorDiffusionFloyd = orderDither(bitmap, recycler);
            } else {
                bitmapErrorDiffusionFloyd = bitmap.copy(Bitmap.Config.RGB_565, recycler);
                Intrinsics.checkNotNullExpressionValue(bitmapErrorDiffusionFloyd, "copy(...)");
            }
            boolean zPrintBitmap = printBitmap(threshold(bitmapErrorDiffusionFloyd), height, compress);
            if (type == 0) {
                WriteData(ExtKt.hexStringToBytes("0A 0A 0A 0A 0A 0A 0A 0A 0A 0A 0A 0A"));
            } else {
                WriteData(new byte[]{12});
            }
            if (!(ReadDataMillisecond(100).length == 0)) {
                ReadDataMillisecond(200);
            }
            if (!bitmapErrorDiffusionFloyd.isRecycled()) {
                bitmapErrorDiffusionFloyd.recycle();
            }
            return zPrintBitmap;
        }

        public final boolean printBitmap(byte[] bData, int height, boolean compress) throws Exception {
            byte[] bArrPackPerBytes3;
            Intrinsics.checkNotNullParameter(bData, "bData");
            byte[] bArr = {27, 18, 119};
            if (compress) {
                bArr = new byte[]{27, 18, 120};
            }
            ResultSendData realSendData = getRealSendData(bData, height);
            int height2 = realSendData.getHeight();
            byte[] data = realSendData.getData();
            byte[] bArrByteMergerAll = ConvertUtil.byteMergerAll(bArr, Tools.intTo2Bytes(HPRTPrinterHelper.WIDTH), Tools.intTo2Bytes(height2));
            Intrinsics.checkNotNullExpressionValue(bArrByteMergerAll, "byteMergerAll(...)");
            List<byte[]> listAddBytesToList3 = Tools.addBytesToList3(data);
            Intrinsics.checkNotNullExpressionValue(listAddBytesToList3, "addBytesToList3(...)");
            WriteData(bArrByteMergerAll);
            int size = listAddBytesToList3.size();
            for (int i = 0; i < size; i++) {
                if (compress) {
                    bArrPackPerBytes3 = Tools.packPerBytes4(listAddBytesToList3, i);
                } else {
                    bArrPackPerBytes3 = Tools.packPerBytes3(listAddBytesToList3, i);
                }
                logcat("压缩前:" + listAddBytesToList3.get(i).length + " 压缩后:" + bArrPackPerBytes3.length);
                if (WriteData(bArrPackPerBytes3) < 0) {
                    return false;
                }
                ReadDataMillisecondNotFilter(100);
            }
            return true;
        }

        public final Bitmap orderDither(Bitmap bitmap, boolean recycler) {
            Intrinsics.checkNotNullParameter(bitmap, "bitmap");
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] iArr = new int[width * height];
            bitmap.getPixels(iArr, 0, width, 0, 0, width, height);
            if (recycler) {
                bitmap.recycle();
            }
            for (int i = 0; i < height; i++) {
                for (int i2 = 0; i2 < width; i2++) {
                    int i3 = (width * i) + i2;
                    int i4 = iArr[i3];
                    iArr[i3] = ((((Color.red(i4) * 299) + (Color.green(i4) * 587)) + (Color.blue(i4) * 114)) / 1000) / 4 >= getBAYER_PATTERN()[i2 % 8][i % 8] ? -1 : -16777216;
                }
            }
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            Intrinsics.checkNotNullExpressionValue(bitmapCreateBitmap, "createBitmap(...)");
            bitmapCreateBitmap.setPixels(iArr, 0, width, 0, 0, width, height);
            return bitmapCreateBitmap;
        }

        public final Bitmap errorDiffusionFloyd(Bitmap bitmap, boolean recycler) {
            int i;
            Intrinsics.checkNotNullParameter(bitmap, "bitmap");
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int i2 = width * height;
            int[] iArr = new int[i2];
            bitmap.getPixels(iArr, 0, width, 0, 0, width, height);
            if (recycler) {
                bitmap.recycle();
            }
            int[] iArr2 = new int[i2];
            for (int i3 = 0; i3 < height; i3++) {
                for (int i4 = 0; i4 < width; i4++) {
                    int i5 = (width * i3) + i4;
                    int i6 = iArr[i5];
                    iArr2[i5] = (((Color.red(i6) * 299) + (Color.green(i6) * 587)) + (Color.blue(i6) * 114)) / 1000;
                }
            }
            for (int i7 = 0; i7 < height; i7++) {
                for (int i8 = 0; i8 < width; i8++) {
                    int i9 = (width * i7) + i8;
                    int i10 = iArr2[i9];
                    if (i10 >= 128) {
                        i10 -= 255;
                        i = -1;
                    } else {
                        i = -16777216;
                    }
                    iArr[i9] = i;
                    int i11 = i7 + 1;
                    if (i11 < height && i8 + 1 < width && i8 - 1 >= 0) {
                        int i12 = i9 + 1;
                        iArr2[i12] = iArr2[i12] + ((i10 * 7) / 16);
                        int i13 = (width * i11) + i8;
                        int i14 = i13 - 1;
                        iArr2[i14] = iArr2[i14] + ((i10 * 3) / 16);
                        iArr2[i13] = iArr2[i13] + ((i10 * 5) / 16);
                        int i15 = i13 + 1;
                        iArr2[i15] = iArr2[i15] + (i10 / 16);
                    } else if (i11 < height && i8 - 1 < 0) {
                        int i16 = i9 + 1;
                        iArr2[i16] = iArr2[i16] + ((i10 * 7) / 16);
                        int i17 = (width * i11) + i8;
                        iArr2[i17] = iArr2[i17] + ((i10 * 5) / 16);
                        int i18 = i17 + 1;
                        iArr2[i18] = iArr2[i18] + (i10 / 16);
                    } else if (i11 < height && i8 + 1 >= width) {
                        int i19 = (width * i11) + i8;
                        int i20 = i19 - 1;
                        iArr2[i20] = iArr2[i20] + ((i10 * 3) / 16);
                        iArr2[i19] = iArr2[i19] + ((i10 * 5) / 16);
                    } else if (i11 >= height && i8 + 1 < width) {
                        int i21 = i9 + 1;
                        iArr2[i21] = iArr2[i21] + ((i10 * 7) / 16);
                    }
                }
            }
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            Intrinsics.checkNotNullExpressionValue(bitmapCreateBitmap, "createBitmap(...)");
            bitmapCreateBitmap.setPixels(iArr, 0, width, 0, 0, width, height);
            return bitmapCreateBitmap;
        }

        public final byte[] threshold(Bitmap bmp) {
            Bitmap bmp2 = bmp;
            Intrinsics.checkNotNullParameter(bmp2, "bmp");
            try {
                int width = bmp2.getWidth();
                int height = bmp2.getHeight();
                int i = (width % 8 == 0 ? width : ((width / 8) + 1) * 8) / 8;
                int i2 = height * i;
                byte[] bArr = new byte[i2];
                for (int i3 = 0; i3 < i2; i3++) {
                    bArr[i3] = 0;
                }
                int i4 = 0;
                int i5 = 0;
                while (i4 < height) {
                    int[] iArr = new int[width];
                    bmp2.getPixels(iArr, 0, width, 0, i4, width, 1);
                    int i6 = 0;
                    for (int i7 = 0; i7 < width; i7++) {
                        i6++;
                        int i8 = iArr[i7];
                        if (i6 > 8) {
                            i5++;
                            i6 = 1;
                        }
                        if (i8 != -1) {
                            int i9 = 1 << (8 - i6);
                            if (((Color.red(i8) + Color.green(i8)) + Color.blue(i8)) / 3 < 128) {
                                bArr[i5] = (byte) (bArr[i5] | i9);
                            }
                        }
                    }
                    i4++;
                    i5 = i * i4;
                    bmp2 = bmp;
                }
                return bArr;
            } catch (Exception e) {
                e.printStackTrace();
                return new byte[0];
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        public final ResultSendData getRealSendData(byte[] dataBytes, int height) {
            Intrinsics.checkNotNullParameter(dataBytes, "dataBytes");
            int i = 0;
            int i2 = 0;
            loop0: while (i2 < 118) {
                byte[] bArr = new byte[HPRTPrinterHelper.WIDTH];
                int i3 = i2 + 1;
                System.arraycopy(dataBytes, (dataBytes.length - 1) - (i3 * HPRTPrinterHelper.WIDTH), bArr, 0, HPRTPrinterHelper.WIDTH);
                for (int i4 = 0; i4 < 292; i4++) {
                    if (bArr[i4] != 0) {
                        break loop0;
                    }
                }
                i2 = i3;
            }
            int length = dataBytes.length - (i2 * HPRTPrinterHelper.WIDTH);
            byte[] bArr2 = new byte[length];
            System.arraycopy(dataBytes, 0, bArr2, 0, length);
            ResultSendData resultSendData = new ResultSendData(null, i, 3, 0 == true ? 1 : 0);
            resultSendData.setData(bArr2);
            resultSendData.setHeight(height - i2);
            return resultSendData;
        }

        public final int saveBitmap(Bitmap bmp, byte halftoneType, byte scaleMode, int printdpi) throws Exception {
            Intrinsics.checkNotNullParameter(bmp, "bmp");
            return SaveBmpDatas(bmp, halftoneType, scaleMode, printdpi);
        }

        private final int CreateBitmapPrintDatas(Bitmap bmp, byte halftoneType, byte scaleMode, int printdpi, boolean isCompress) {
            PrinterDataCore printerDataCore = new PrinterDataCore();
            printerDataCore.HalftoneMode = halftoneType;
            printerDataCore.ScaleMode = scaleMode;
            if (isCompress) {
                printerDataCore.CompressMode = (byte) 3;
            } else {
                printerDataCore.CompressMode = (byte) 2;
            }
            byte[] bArrPrintDataFormat = printerDataCore.PrintDataFormat(bmp, printdpi);
            if (bArrPrintDataFormat == null) {
                return -1;
            }
            byte[] bArr = new byte[10000];
            int length = bArrPrintDataFormat.length;
            int i = length / 10000;
            int i2 = 0;
            int iWriteData = 0;
            while (i2 < i) {
                i2++;
                int i3 = i2 * 10000;
                for (int i4 = i2 * 10000; i4 < i3; i4++) {
                    bArr[i4 % 10000] = bArrPrintDataFormat[i4];
                }
                IPort printer = getPrinter();
                Intrinsics.checkNotNull(printer);
                iWriteData = printer.WriteData(bArr);
            }
            if (length % 10000 != 0) {
                int i5 = i * 10000;
                byte[] bArr2 = new byte[bArrPrintDataFormat.length - i5];
                int length2 = bArrPrintDataFormat.length;
                for (int i6 = i5; i6 < length2; i6++) {
                    bArr2[i6 - i5] = bArrPrintDataFormat[i6];
                }
                IPort printer2 = getPrinter();
                Intrinsics.checkNotNull(printer2);
                iWriteData = printer2.WriteData(bArr2);
            }
            setConnectState(0);
            return iWriteData;
        }

        private final int SaveBmpDatas(Bitmap bmp, byte halftoneType, byte scaleMode, int printdpi) {
            PrinterDataCore printerDataCore = new PrinterDataCore();
            printerDataCore.HalftoneMode = halftoneType;
            printerDataCore.ScaleMode = scaleMode;
            byte[] bArrSaveDataFormat = printerDataCore.SaveDataFormat(bmp, printdpi);
            if (bArrSaveDataFormat == null) {
                return -1;
            }
            logcat("总数据：" + Tools.byteToHex(bArrSaveDataFormat));
            byte[] bArr = new byte[10000];
            int length = bArrSaveDataFormat.length;
            int i = length / 10000;
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            int i2 = 0;
            int iWriteData = 0;
            while (i2 < i) {
                i2++;
                int i3 = i2 * 10000;
                for (int i4 = i2 * 10000; i4 < i3; i4++) {
                    bArr[i4 % 10000] = bArrSaveDataFormat[i4];
                }
                IPort printer2 = getPrinter();
                Intrinsics.checkNotNull(printer2);
                iWriteData = printer2.WriteData(bArr);
            }
            if (length % 10000 != 0) {
                int i5 = i * 10000;
                byte[] bArr2 = new byte[bArrSaveDataFormat.length - i5];
                int length2 = bArrSaveDataFormat.length;
                for (int i6 = i5; i6 < length2; i6++) {
                    bArr2[i6 - i5] = bArrSaveDataFormat[i6];
                }
                IPort printer3 = getPrinter();
                Intrinsics.checkNotNull(printer3);
                iWriteData = printer3.WriteData(bArr2);
            }
            IPort printer4 = getPrinter();
            Intrinsics.checkNotNull(printer4);
            printer4.setConnectState(0);
            return iWriteData;
        }

        @JvmStatic
        public final void logcat(String log) {
            if (HPRTPrinterHelper.isLog) {
                Intrinsics.checkNotNull(log);
                Log.d("PrintHMark", log);
            }
        }

        @JvmStatic
        public final void logcatI(String log) {
            if (HPRTPrinterHelper.isLog) {
                Intrinsics.checkNotNull(log);
                Log.i("PrintHMark", log);
            }
        }

        @JvmStatic
        public final void logcatE(String log) {
            if (HPRTPrinterHelper.isLog) {
                Intrinsics.checkNotNull(log);
                Log.e("PrintHMark", log);
            }
        }

        public static /* synthetic */ void sendUpdateToPrint$default(Companion companion, InputStream inputStream, setOnProgress setonprogress, int i, int i2, Object obj) {
            if ((i2 & 4) != 0) {
                i = 2016;
            }
            companion.sendUpdateToPrint(inputStream, setonprogress, i);
        }

        @JvmStatic
        public final void sendUpdateToPrint(InputStream fileInputStream, setOnProgress onprogress, int packageSize) {
            String str;
            List<byte[]> list;
            int iWriteData;
            int i;
            int i2 = packageSize;
            String str2 = "包数：";
            if (fileInputStream == null || onprogress == null) {
                return;
            }
            try {
                List<byte[]> listAddBytesToList = Tools.addBytesToList(InputStreamToByte(fileInputStream), i2);
                int size = listAddBytesToList.size();
                logcat("总包数：" + size);
                int i3 = 0;
                while (i3 < size) {
                    byte[] bArrPackPerBytes = Tools.packPerBytes(listAddBytesToList, i3, i2);
                    logcat("写的数据：" + Tools.byteToHex(bArrPackPerBytes));
                    try {
                        iWriteData = WriteData(bArrPackPerBytes);
                        logcat("写的数据长度：" + iWriteData);
                        i = i3 + 1;
                        logcat(str2 + i);
                    } catch (Exception unused) {
                        str = str2;
                    }
                    if (iWriteData <= 0) {
                        logcat("升级失败1");
                        onprogress.failure();
                        return;
                    }
                    boolean z = true;
                    boolean z2 = false;
                    while (true) {
                        if (!z) {
                            break;
                        }
                        byte[] bArrReadDataNotFilter = ReadDataNotFilter(2);
                        logcat("返回：" + Tools.byteToHex(bArrReadDataNotFilter));
                        if (bArrReadDataNotFilter.length == 0) {
                            logcat("重写的数据：" + Tools.byteToHex(bArrPackPerBytes));
                            int iWriteData2 = WriteData(bArrPackPerBytes);
                            logcat("重写的数据长度：" + iWriteData2);
                            logcat(str2 + i);
                            if (iWriteData2 <= 0) {
                                logcat("升级失败2");
                                onprogress.failure();
                                break;
                            }
                            while (z) {
                                byte[] bArrReadDataNotFilter2 = ReadDataNotFilter(2);
                                String strByteToHex = Tools.byteToHex(bArrReadDataNotFilter2);
                                StringBuilder sb = new StringBuilder();
                                str = str2;
                                try {
                                    sb.append("重新返回：");
                                    sb.append(strByteToHex);
                                    logcat(sb.toString());
                                    if (bArrReadDataNotFilter2.length == 0) {
                                        logcat("升级失败3");
                                        z = false;
                                        z2 = false;
                                    }
                                    String strByteToHex2 = Tools.byteToHex(bArrReadDataNotFilter2);
                                    Intrinsics.checkNotNullExpressionValue(strByteToHex2, "byteToHex(...)");
                                    list = listAddBytesToList;
                                    try {
                                        str2 = str;
                                        listAddBytesToList = list;
                                        if (StringsKt.contains$default((CharSequence) strByteToHex2, (CharSequence) "1B1C26", false, 2, (Object) null)) {
                                            z = false;
                                            z2 = true;
                                        }
                                    } catch (Exception unused2) {
                                    }
                                } catch (Exception unused3) {
                                }
                            }
                        }
                        str = str2;
                        list = listAddBytesToList;
                        try {
                            String strByteToHex3 = Tools.byteToHex(bArrReadDataNotFilter);
                            Intrinsics.checkNotNullExpressionValue(strByteToHex3, "byteToHex(...)");
                            if (StringsKt.contains$default((CharSequence) strByteToHex3, (CharSequence) "1B1C26", false, 2, (Object) null)) {
                                str2 = str;
                                listAddBytesToList = list;
                                z = false;
                                z2 = true;
                            } else {
                                str2 = str;
                                listAddBytesToList = list;
                            }
                        } catch (Exception unused4) {
                        }
                    }
                    str = str2;
                    list = listAddBytesToList;
                    if (!z2) {
                        logcat("升级失败");
                        onprogress.failure();
                        return;
                    } else {
                        onprogress.onProgress((i * 100) / size);
                        i3++;
                        i2 = packageSize;
                        str2 = str;
                        listAddBytesToList = list;
                    }
                    list = listAddBytesToList;
                    logcat("升级失败，异常" + (i3 + 1));
                    onprogress.failure();
                    i3++;
                    i2 = packageSize;
                    str2 = str;
                    listAddBytesToList = list;
                }
            } catch (Exception unused5) {
                Intrinsics.checkNotNull(onprogress);
                onprogress.failure();
            }
        }

        public static /* synthetic */ void send$default(Companion companion, int i, String str, String str2, int i2, String str3, byte[] bArr, int i3, Object obj) {
            if ((i3 & 32) != 0) {
                bArr = null;
            }
            companion.send(i, str, str2, i2, str3, bArr);
        }

        public final void send(int type, String command, String value, int encode, String name, byte[] valueBytes) {
            Intrinsics.checkNotNullParameter(command, "command");
            Intrinsics.checkNotNullParameter(value, "value");
            Intrinsics.checkNotNullParameter(name, "name");
            try {
                logcat("发送的数据：" + command + " 类型：" + type + " value：" + value + " 编码：" + encode + " name：" + name);
                setConnectState(1);
                if (type == 1) {
                    setKey(command, value, encode, valueBytes);
                } else if (type == 2) {
                    setVal(command, value);
                } else if (type == 3) {
                    setDo(type, command, value, encode);
                } else if (type == 4) {
                    IPort printer = getPrinter();
                    Intrinsics.checkNotNull(printer);
                    printer.WriteData(ExtKt.hexStringToBytes(command));
                } else {
                    IPort printer2 = getPrinter();
                    Intrinsics.checkNotNull(printer2);
                    byte[] bytes = command.getBytes(Charsets.UTF_8);
                    Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
                    printer2.WriteData(bytes);
                }
                IPort printer3 = getPrinter();
                Intrinsics.checkNotNull(printer3);
                byte[] bArrReadDataMillisecond = printer3.ReadDataMillisecond(2000);
                setConnectState(0);
                logcat("返回的数据：" + Tools.byteToHex(bArrReadDataMillisecond));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static /* synthetic */ byte[] getValue$default(Companion companion, int i, String str, int i2, String str2, boolean z, String str3, int i3, Object obj) {
            boolean z2 = (i3 & 16) != 0 ? false : z;
            if ((i3 & 32) != 0) {
                str3 = "TSPL";
            }
            return companion.getValue(i, str, i2, str2, z2, str3);
        }

        public final byte[] getValue(int type, String command, int encode, String name, boolean isD31S, String instruct) {
            Triple triple;
            byte[] bArrHexStringToBytes;
            Intrinsics.checkNotNullParameter(command, "command");
            Intrinsics.checkNotNullParameter(name, "name");
            try {
                logcat("##############" + name + "#################");
                logcat("发送的数据：" + command + " 类型：" + type + "编码：" + encode + " name：" + name);
                if (type == 1) {
                    getKey(command, encode);
                } else if (type == 2) {
                    getVal(command);
                } else if (type == 3) {
                    getDo(command, encode);
                } else if (type == 4) {
                    IPort printer = getPrinter();
                    Intrinsics.checkNotNull(printer);
                    printer.WriteData(ExtKt.hexStringToBytes(command));
                } else if (isD31S) {
                    if (Intrinsics.areEqual(instruct, "ESC")) {
                        bArrHexStringToBytes = ExtKt.hexStringToBytes("12 62");
                    } else {
                        bArrHexStringToBytes = ExtKt.hexStringToBytes("5a 5a 5a 43 52 0A");
                    }
                    WriteData(bArrHexStringToBytes);
                } else {
                    IPort printer2 = getPrinter();
                    Intrinsics.checkNotNull(printer2);
                    printer2.WriteData(ExtKt.hexStringToBytes(command));
                }
                if (type == 1 && encode <= 2) {
                    triple = new Triple(800, Integer.valueOf(ConnectionResult.DRIVE_EXTERNAL_STORAGE_REQUIRED), 2200);
                } else {
                    triple = new Triple(1200, 2200, 3000);
                }
                int iIntValue = ((Number) triple.component1()).intValue();
                int iIntValue2 = ((Number) triple.component2()).intValue();
                int iIntValue3 = ((Number) triple.component3()).intValue();
                long jCurrentTimeMillis = System.currentTimeMillis();
                byte[] valueData = readValueData(iIntValue, 250, 120);
                if (valueData.length == 0) {
                    valueData = readValueData(iIntValue2, 350, 150);
                }
                if (valueData.length == 0) {
                    valueData = readValueData(iIntValue3, 500, 200);
                }
                logcat(" 读取耗时：" + (System.currentTimeMillis() - jCurrentTimeMillis) + " ms");
                logcat("返回的数据：".concat(new String(valueData, Charsets.UTF_8)));
                logcat("##############" + name + "#################");
                return valueData;
            } catch (Exception e) {
                e.printStackTrace();
                return new byte[0];
            }
        }

        static /* synthetic */ byte[] readValueData$default(Companion companion, int i, int i2, int i3, int i4, Object obj) {
            if ((i4 & 1) != 0) {
                i = 1000;
            }
            if ((i4 & 2) != 0) {
                i2 = 100;
            }
            if ((i4 & 4) != 0) {
                i3 = 100;
            }
            return companion.readValueData(i, i2, i3);
        }

        private final byte[] readValueData(int totalTimeout, int sliceTimeout, int tailTimeout) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while (totalTimeout > 0) {
                int i = totalTimeout < sliceTimeout ? totalTimeout : sliceTimeout;
                IPort printer = getPrinter();
                Intrinsics.checkNotNull(printer);
                byte[] bArrReadDataMillisecond = printer.ReadDataMillisecond(i);
                Intrinsics.checkNotNull(bArrReadDataMillisecond);
                if (!(bArrReadDataMillisecond.length == 0)) {
                    byteArrayOutputStream.write(bArrReadDataMillisecond);
                    for (int i2 = 0; i2 < 2; i2++) {
                        IPort printer2 = HPRTPrinterHelper.INSTANCE.getPrinter();
                        Intrinsics.checkNotNull(printer2);
                        byte[] bArrReadDataMillisecond2 = printer2.ReadDataMillisecond(tailTimeout);
                        Intrinsics.checkNotNull(bArrReadDataMillisecond2);
                        if (bArrReadDataMillisecond2.length == 0) {
                            byte[] byteArray = byteArrayOutputStream.toByteArray();
                            Intrinsics.checkNotNullExpressionValue(byteArray, "toByteArray(...)");
                            return byteArray;
                        }
                        byteArrayOutputStream.write(bArrReadDataMillisecond2);
                    }
                    byte[] byteArray2 = byteArrayOutputStream.toByteArray();
                    Intrinsics.checkNotNullExpressionValue(byteArray2, "toByteArray(...)");
                    return byteArray2;
                }
                totalTimeout -= i;
            }
            byte[] byteArray3 = byteArrayOutputStream.toByteArray();
            Intrinsics.checkNotNullExpressionValue(byteArray3, "toByteArray(...)");
            return byteArray3;
        }

        private final void getKey(String value, int encode) throws Exception {
            byte[] bytes = "& V1 getkey\r\n".getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
            WriteData(ArraysKt.plus(ArraysKt.plus(ArraysKt.plus(ArraysKt.plus(new byte[]{27, 28}, bytes), new byte[]{1}), ByteUtils.INSTANCE.intToBytes2_l(Integer.parseInt(value))), new byte[]{(byte) encode}));
        }

        private final void getVal(String command) throws Exception {
            byte[] bytes = "& V1 getval ".getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
            byte[] bytes2 = "\"".getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes2, "getBytes(...)");
            byte[] bytes3 = "\r\n".getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes3, "getBytes(...)");
            ByteUtils byteUtils = ByteUtils.INSTANCE;
            byte[] bytes4 = command.getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes4, "getBytes(...)");
            WriteData(byteUtils.mergeData(new byte[]{27, 28}, bytes, bytes2, bytes4, bytes2, bytes3));
        }

        private final void getDo(String value, int encode) throws Exception {
            byte[] bytes = "& V1 do\r\n".getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
            byte[] bArrPlus = ArraysKt.plus(ArraysKt.plus(ArraysKt.plus(ArraysKt.plus(new byte[]{27, 28}, bytes), new byte[]{1}), ByteUtils.INSTANCE.intToBytes2_l(Integer.parseInt(value))), (byte) encode);
            byte[] bytes2 = value.getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes2, "getBytes(...)");
            WriteData(ArraysKt.plus(bArrPlus, bytes2));
        }

        private final void setDo(int type, String command, String value, int encode) throws Exception {
            byte[] bytes = "& V1 do \"".getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
            byte[] bArr = {27, 28};
            if (value.length() == 0) {
                byte[] bArrPlus = ArraysKt.plus(bArr, bytes);
                byte[] bytes2 = command.getBytes(Charsets.UTF_8);
                Intrinsics.checkNotNullExpressionValue(bytes2, "getBytes(...)");
                byte[] bArrPlus2 = ArraysKt.plus(bArrPlus, bytes2);
                byte[] bytes3 = "\"\r\n".getBytes(Charsets.UTF_8);
                Intrinsics.checkNotNullExpressionValue(bytes3, "getBytes(...)");
                WriteData(ArraysKt.plus(bArrPlus2, bytes3));
                return;
            }
            byte[] bArrPlus3 = ArraysKt.plus(bArr, bytes);
            byte[] bytes4 = command.getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes4, "getBytes(...)");
            byte[] bArrPlus4 = ArraysKt.plus(bArrPlus3, bytes4);
            byte[] bytes5 = "\"".getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes5, "getBytes(...)");
            byte[] bArrPlus5 = ArraysKt.plus(bArrPlus4, bytes5);
            byte[] bytes6 = "\r\n".getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes6, "getBytes(...)");
            WriteData(ArraysKt.plus(ArraysKt.plus(bArrPlus5, bytes6), (byte) Integer.parseInt(value)));
        }

        private final void setVal(String command, String value) throws Exception {
            byte[] bytes = "& V1 setval ".getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
            byte[] bytes2 = "\"".getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes2, "getBytes(...)");
            byte[] bytes3 = "\r\n".getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes3, "getBytes(...)");
            ByteUtils byteUtils = ByteUtils.INSTANCE;
            byte[] bytes4 = command.getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes4, "getBytes(...)");
            byte[] bytes5 = Operators.SPACE_STR.getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes5, "getBytes(...)");
            byte[] bytes6 = value.getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes6, "getBytes(...)");
            WriteData(byteUtils.mergeData(new byte[]{27, 28}, bytes, bytes2, bytes4, bytes2, bytes5, bytes2, bytes6, bytes2, bytes3));
        }

        static /* synthetic */ void setKey$default(Companion companion, String str, String str2, int i, byte[] bArr, int i2, Object obj) throws Exception {
            if ((i2 & 8) != 0) {
                bArr = null;
            }
            companion.setKey(str, str2, i, bArr);
        }

        private final void setKey(String command, String value, int encode, byte[] valueBytes) throws Exception {
            byte[] bytes = "& V1 setkey\r\n".getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
            byte[] bArr = {27, 28};
            byte[] bArrIntToBytes2_l = ByteUtils.INSTANCE.intToBytes2_l(Integer.parseInt(command));
            Integer intOrNull = StringsKt.toIntOrNull(value);
            int iIntValue = intOrNull != null ? intOrNull.intValue() : 0;
            if (encode == 1) {
                WriteData(ArraysKt.plus(ArraysKt.plus(ArraysKt.plus(ArraysKt.plus(ArraysKt.plus(bArr, bytes), new byte[]{1}), bArrIntToBytes2_l), (byte) encode), (byte) iIntValue));
                return;
            }
            if (encode == 2) {
                WriteData(ArraysKt.plus(ArraysKt.plus(ArraysKt.plus(ArraysKt.plus(ArraysKt.plus(bArr, bytes), new byte[]{1}), bArrIntToBytes2_l), (byte) encode), ByteUtils.INSTANCE.intToBytes2_l(iIntValue)));
                return;
            }
            if (encode == 4) {
                byte[] bArrPlus = ArraysKt.plus(ArraysKt.plus(ArraysKt.plus(ArraysKt.plus(bArr, bytes), new byte[]{1}), bArrIntToBytes2_l), (byte) encode);
                if (valueBytes == null) {
                    valueBytes = ByteUtils.INSTANCE.intToBytes4_l(iIntValue);
                }
                WriteData(ArraysKt.plus(bArrPlus, valueBytes));
                return;
            }
            byte[] bytes2 = value.getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes2, "getBytes(...)");
            WriteData(ArraysKt.plus(ArraysKt.plus(ArraysKt.plus(ArraysKt.plus(ArraysKt.plus(bArr, bytes), new byte[]{1}), bArrIntToBytes2_l), (byte) bytes2.length), bytes2));
        }

        private final byte[] InputStreamToByte(InputStream is) throws IOException {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while (true) {
                int i = is.read();
                if (i != -1) {
                    byteArrayOutputStream.write(i);
                } else {
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    byteArrayOutputStream.close();
                    Intrinsics.checkNotNull(byteArray);
                    return byteArray;
                }
            }
        }

        public static /* synthetic */ String getVersion$default(Companion companion, int i, int i2, int i3, Object obj) throws Exception {
            if ((i3 & 2) != 0) {
                i2 = 0;
            }
            return companion.getVersion(i, i2);
        }

        @JvmStatic
        public final String getVersion(int count, int type) throws Exception {
            byte[] bArrReadDataMillisecond;
            if (count == 0) {
                return "";
            }
            if (!(getPrinter() instanceof WiFiOperator)) {
                ReadDataMillisecond(200);
            }
            if (type == 0) {
                byte[] bArrWriteAndReadTextResponse = writeAndReadTextResponse(ExtKt.hexStringToBytes("1B 1C 26 20 56 31 20 67 65 74 76 61 6C 20 22 70 72 69 6E 74 65 72 5F 76 65 72 73 69 6F 6E 22 0D 0A"), 5000);
                if (bArrWriteAndReadTextResponse.length == 0) {
                    WriteData(ExtKt.hexStringToBytes("1b 12 56"));
                    bArrReadDataMillisecond = ReadDataMillisecond(1000);
                } else {
                    bArrReadDataMillisecond = bArrWriteAndReadTextResponse;
                }
            } else if (type == 1) {
                WriteData(ExtKt.hexStringToBytes("1b 12 56"));
                bArrReadDataMillisecond = ReadDataMillisecond(1000);
            } else {
                WriteData(ExtKt.hexStringToBytes("1b 12 64"));
                byte[] bArrReadDataMillisecond2 = ReadDataMillisecond(1000);
                String firmwareVersion = PoliDeviceDetailsParser.INSTANCE.parseFirmwareVersion(bArrReadDataMillisecond2);
                return firmwareVersion == null ? new String(bArrReadDataMillisecond2, Charsets.UTF_8) : firmwareVersion;
            }
            byte[] bArrExtractTextResponse = extractTextResponse(bArrReadDataMillisecond);
            if (bArrExtractTextResponse.length == 0 && count > 0) {
                return getVersion(count - 1, type);
            }
            return new String(bArrExtractTextResponse, Charsets.UTF_8);
        }

        private final byte[] extractTextResponse(byte[] data) {
            byte b;
            int iIndexOf = ArraysKt.indexOf(data, (byte) 0);
            if (iIndexOf < 0) {
                iIndexOf = data.length;
            }
            int i = 2;
            if (data.length < 2 || (((b = data[0]) != 78 || data[1] != 71) && (b != 79 || data[1] != 75))) {
                i = 0;
            }
            return i < iIndexOf ? ArraysKt.copyOfRange(data, i, iIndexOf) : new byte[0];
        }

        private final byte[] writeAndReadTextResponse(byte[] data, int timeout) throws Exception {
            IPort printer = getPrinter();
            WiFiOperator wiFiOperator = printer instanceof WiFiOperator ? (WiFiOperator) printer : null;
            if (wiFiOperator != null) {
                IPort printer2 = getPrinter();
                Intrinsics.checkNotNull(printer2);
                printer2.setConnectState(1);
                try {
                    return wiFiOperator.writeAndReadText(data, timeout);
                } finally {
                    IPort printer3 = getPrinter();
                    Intrinsics.checkNotNull(printer3);
                    printer3.setConnectState(0);
                }
            }
            WriteData(data);
            return ReadDataMillisecond(timeout);
        }

        public static /* synthetic */ String getPrintName$default(Companion companion, int i, int i2, int i3, Object obj) throws Exception {
            if ((i3 & 2) != 0) {
                i2 = 0;
            }
            return companion.getPrintName(i, i2);
        }

        @JvmStatic
        public final String getPrintName(int count, int type) throws Exception {
            byte[] bArrReadDataMillisecond;
            if (count == 0) {
                return "";
            }
            if (!(getPrinter() instanceof WiFiOperator)) {
                ReadDataMillisecond(200);
            }
            if (type == 0) {
                bArrReadDataMillisecond = writeAndReadTextResponse(ExtKt.hexStringToBytes("1B 1C 26 20 56 31 20 67 65 74 76 61 6C 20 22 70 72 69 6E 74 65 72 5F 6E 61 6D 65 22 0D 0A"), 5000);
                if (bArrReadDataMillisecond.length == 0) {
                    WriteData(ExtKt.hexStringToBytes("1B 1C 26 20 56 31 20 67 65 74 6B 65 79 0D 0A 01 04 00 20"));
                    bArrReadDataMillisecond = ReadDataMillisecond(1000);
                    if (bArrReadDataMillisecond.length > 32) {
                        bArrReadDataMillisecond = ArraysKt.copyOfRange(bArrReadDataMillisecond, bArrReadDataMillisecond.length - 32, bArrReadDataMillisecond.length);
                    }
                }
            } else {
                WriteData(ExtKt.hexStringToBytes("1B 1C 26 20 56 31 20 67 65 74 6B 65 79 0D 0A 01 04 00 20"));
                bArrReadDataMillisecond = ReadDataMillisecond(1000);
                if (bArrReadDataMillisecond.length > 32) {
                    bArrReadDataMillisecond = ArraysKt.copyOfRange(bArrReadDataMillisecond, bArrReadDataMillisecond.length - 32, bArrReadDataMillisecond.length);
                }
            }
            byte[] bArrExtractTextResponse = extractTextResponse(bArrReadDataMillisecond);
            if (bArrExtractTextResponse.length == 0 && count > 0) {
                return getPrintName(count - 1, type);
            }
            return new String(bArrExtractTextResponse, Charsets.UTF_8);
        }

        @JvmStatic
        public final int printAreaSize(String width, String height) throws Exception {
            Intrinsics.checkNotNullParameter(width, "width");
            Intrinsics.checkNotNullParameter(height, "height");
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            String str = "SIZE " + width + "mm," + height + "mm\r\n";
            Charset charsetForName = Charset.forName(getLanguageEncode());
            Intrinsics.checkNotNullExpressionValue(charsetForName, "forName(...)");
            byte[] bytes = str.getBytes(charsetForName);
            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
            int iWriteData = printer2.WriteData(bytes);
            IPort printer3 = getPrinter();
            Intrinsics.checkNotNull(printer3);
            printer3.setConnectState(0);
            return iWriteData;
        }

        @JvmStatic
        public final int CLS() throws Exception {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            Charset charsetForName = Charset.forName(getLanguageEncode());
            Intrinsics.checkNotNullExpressionValue(charsetForName, "forName(...)");
            byte[] bytes = "CLS\r\n".getBytes(charsetForName);
            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
            int iWriteData = printer2.WriteData(bytes);
            IPort printer3 = getPrinter();
            Intrinsics.checkNotNull(printer3);
            printer3.setConnectState(0);
            return iWriteData;
        }

        @JvmStatic
        public final int printImage(String x_pos, String y_pos, Bitmap bmp, boolean isNegate, int model, int type) throws Exception {
            int width;
            Intrinsics.checkNotNullParameter(x_pos, "x_pos");
            Intrinsics.checkNotNullParameter(y_pos, "y_pos");
            Intrinsics.checkNotNullParameter(bmp, "bmp");
            if (bmp.getWidth() % 8 == 0) {
                width = bmp.getWidth() / 8;
            } else {
                width = (bmp.getWidth() / 8) + 1;
            }
            int height = bmp.getHeight();
            byte[] bArrPrintBitmap = PrintBitmap(bmp, (byte) type, (byte) 0);
            long jCurrentTimeMillis = System.currentTimeMillis();
            StringBuilder sb = new StringBuilder();
            sb.append(width);
            String string = sb.toString();
            StringBuilder sb2 = new StringBuilder();
            sb2.append(height);
            int iPrintBitmap = printBitmap(x_pos, y_pos, string, sb2.toString(), bArrPrintBitmap, isNegate, model);
            Log.e("PrintTime", "图片下发->" + (System.currentTimeMillis() - jCurrentTimeMillis) + "mm");
            return iPrintBitmap;
        }

        private final byte[] PrintBitmap(Bitmap bmp, byte halftoneType, byte scaleMode) throws Exception {
            long jCurrentTimeMillis = System.currentTimeMillis();
            byte[] bArrCreateBitmapPrintDatas = CreateBitmapPrintDatas(bmp, halftoneType, scaleMode);
            Log.e("PrintTime", "图片压缩->" + (System.currentTimeMillis() - jCurrentTimeMillis) + "mm");
            return bArrCreateBitmapPrintDatas;
        }

        private final byte[] CreateBitmapPrintDatas(Bitmap bmp, byte halftoneType, byte scaleMode) {
            PrinterDataCore printerDataCore = new PrinterDataCore();
            printerDataCore.HalftoneMode = halftoneType;
            printerDataCore.ScaleMode = scaleMode;
            printerDataCore.CompressMode = (byte) 4;
            byte[] bArrPrintDataFormat = printerDataCore.PrintDataFormat(bmp, 200);
            Intrinsics.checkNotNullExpressionValue(bArrPrintDataFormat, "PrintDataFormat(...)");
            return bArrPrintDataFormat;
        }

        private final int printBitmap(String x_pos, String y_pos, String width, String height, byte[] code_data, boolean isNegate, int model) throws Exception {
            if (model != 0 && model != 1 && model != 2) {
                if (model != 3) {
                    switch (model) {
                        case 16:
                        case 17:
                        case 18:
                            break;
                        default:
                            return -2;
                    }
                }
                if (isNegate) {
                    int length = code_data.length;
                    for (int i = 0; i < length; i++) {
                        code_data[i] = (byte) (~code_data[i]);
                    }
                }
                int length2 = code_data.length;
                byte[] bArr = new byte[length2];
                int[] iArr = new int[1];
                int iLzoCompressData = new LZOCompress().lzoCompressData(code_data, code_data.length, bArr, iArr, new byte[64000]);
                Log.d("TAG", "图片压缩后大小：" + iArr[0] + " outData:" + length2 + " result:" + iLzoCompressData);
                int i2 = iArr[0];
                if (i2 > length2) {
                    IPort printer = getPrinter();
                    Intrinsics.checkNotNull(printer);
                    Charset charsetForName = Charset.forName(getLanguageEncode());
                    Intrinsics.checkNotNullExpressionValue(charsetForName, "forName(...)");
                    byte[] bytes = ("BITMAP " + x_pos + "," + y_pos + "," + width + "," + height + ",0,").getBytes(charsetForName);
                    Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
                    printer.WriteData(bytes);
                    IPort printer2 = getPrinter();
                    Intrinsics.checkNotNull(printer2);
                    printer2.setConnectState(1);
                    IPort printer3 = getPrinter();
                    Intrinsics.checkNotNull(printer3);
                    int iWriteData = printer3.WriteData(code_data);
                    IPort printer4 = getPrinter();
                    Intrinsics.checkNotNull(printer4);
                    printer4.setConnectState(0);
                    return iWriteData;
                }
                byte[] bArr2 = new byte[i2];
                for (int i3 = 0; i3 < i2; i3++) {
                    bArr2[i3] = bArr[i3];
                }
                IPort printer5 = getPrinter();
                Intrinsics.checkNotNull(printer5);
                Charset charsetForName2 = Charset.forName(getLanguageEncode());
                Intrinsics.checkNotNullExpressionValue(charsetForName2, "forName(...)");
                byte[] bytes2 = ("BITMAP " + x_pos + "," + y_pos + "," + width + "," + height + "," + model + ",").getBytes(charsetForName2);
                Intrinsics.checkNotNullExpressionValue(bytes2, "getBytes(...)");
                printer5.WriteData(bytes2);
                IPort printer6 = getPrinter();
                Intrinsics.checkNotNull(printer6);
                printer6.setConnectState(1);
                IPort printer7 = getPrinter();
                Intrinsics.checkNotNull(printer7);
                printer7.WriteData(intTo4Bytes(i2));
                IPort printer8 = getPrinter();
                Intrinsics.checkNotNull(printer8);
                int iWriteData2 = printer8.WriteData(bArr2);
                IPort printer9 = getPrinter();
                Intrinsics.checkNotNull(printer9);
                printer9.setConnectState(0);
                return iWriteData2;
            }
            IPort printer10 = getPrinter();
            Intrinsics.checkNotNull(printer10);
            Charset charsetForName3 = Charset.forName(getLanguageEncode());
            Intrinsics.checkNotNullExpressionValue(charsetForName3, "forName(...)");
            byte[] bytes3 = ("BITMAP " + x_pos + "," + y_pos + "," + width + "," + height + "," + model + ",").getBytes(charsetForName3);
            Intrinsics.checkNotNullExpressionValue(bytes3, "getBytes(...)");
            printer10.WriteData(bytes3);
            if (isNegate) {
                int length3 = code_data.length;
                for (int i4 = 0; i4 < length3; i4++) {
                    code_data[i4] = (byte) (~code_data[i4]);
                }
            }
            IPort printer11 = getPrinter();
            Intrinsics.checkNotNull(printer11);
            printer11.setConnectState(1);
            IPort printer12 = getPrinter();
            Intrinsics.checkNotNull(printer12);
            int iWriteData3 = printer12.WriteData(code_data);
            IPort printer13 = getPrinter();
            Intrinsics.checkNotNull(printer13);
            printer13.setConnectState(0);
            return iWriteData3;
        }

        private final byte[] intTo4Bytes(int value) {
            return new byte[]{(byte) (value & 255), (byte) ((value >> 8) & 255), (byte) ((value >> 16) & 255), (byte) ((value >> 24) & 255)};
        }

        @JvmStatic
        public final int Print(String strnum, String strcopies) throws Exception {
            Intrinsics.checkNotNullParameter(strnum, "strnum");
            Intrinsics.checkNotNullParameter(strcopies, "strcopies");
            String str = "PRINT " + strnum + "," + strcopies + "\r\n";
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            Charset charsetForName = Charset.forName(getLanguageEncode());
            Intrinsics.checkNotNullExpressionValue(charsetForName, "forName(...)");
            byte[] bytes = str.getBytes(charsetForName);
            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
            int iWriteData = printer2.WriteData(bytes);
            setConnectState(0);
            return iWriteData;
        }

        public final int printBitmap(Bitmap bitmap, int bitType) throws Exception {
            Intrinsics.checkNotNullParameter(bitmap, "bitmap");
            int width = bitmap.getWidth();
            int i = width % 8;
            int i2 = width / 8;
            if (i != 0) {
                i2++;
            }
            bitmap.getHeight();
            PrinterDataCore printerDataCore = new PrinterDataCore();
            printerDataCore.HalftoneMode = (byte) bitType;
            printerDataCore.CompressMode = (byte) 4;
            byte[] bArrPrintDataFormat = printerDataCore.PrintDataFormat(bitmap, 200);
            if (bArrPrintDataFormat.length > 3250) {
                List<byte[]> listAddBytesToList = Tools.addBytesToList(bArrPrintDataFormat, (3250 / i2) * i2);
                int size = listAddBytesToList.size();
                for (int i3 = 0; i3 < size; i3++) {
                    if (WriteData(printerDataCore.SubcontractingLzo(listAddBytesToList.get(i3), i2, listAddBytesToList.get(i3).length / i2)) == -1) {
                        setConnectState(0);
                        return -1;
                    }
                }
            } else {
                printBitmap(bitmap, bitType, 200, false);
            }
            return 1;
        }

        public final int printBitmap(int width, int height, byte[] data) throws Exception {
            byte[] bArrLzoCompress;
            Intrinsics.checkNotNullParameter(data, "data");
            int length = data.length;
            byte[] bArr = new byte[length];
            int[] iArr = new int[1];
            int i = width % 8;
            int i2 = width / 8;
            if (i != 0) {
                i2++;
            }
            int iLzoCompressData = new LZOCompress().lzoCompressData(data, data.length, bArr, iArr, new byte[64000]);
            Log.d("TAG", "printBitmap：" + iArr[0] + "outData:" + length + "result:" + iLzoCompressData);
            int i3 = iArr[0];
            if (i3 > data.length) {
                bArrLzoCompress = AddPrintNVImage(data, i2, height);
            } else {
                byte[] bArr2 = new byte[i3];
                for (int i4 = 0; i4 < i3; i4++) {
                    bArr2[i4] = bArr[i4];
                }
                bArrLzoCompress = lzoCompress(bArr2, i2, height);
            }
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            if (printer2.WriteData(bArrLzoCompress) == -1) {
                IPort printer3 = getPrinter();
                Intrinsics.checkNotNull(printer3);
                printer3.setConnectState(0);
                return -1;
            }
            IPort printer4 = getPrinter();
            Intrinsics.checkNotNull(printer4);
            printer4.setConnectState(0);
            return 1;
        }

        private final byte[] AddPrintNVImage(byte[] bDatas, int width, int height) {
            try {
                byte[] bArr = new byte[bDatas.length + 8];
                bArr[0] = BoolPtg.sid;
                bArr[1] = 118;
                bArr[2] = 48;
                bArr[3] = 0;
                bArr[4] = (byte) (width % 256);
                bArr[5] = (byte) (width / 256);
                bArr[6] = (byte) (height % 256);
                bArr[7] = (byte) (height / 256);
                int length = bDatas.length;
                for (int i = 0; i < length; i++) {
                    bArr[i + 8] = bDatas[i];
                }
                return bArr;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private final byte[] lzoCompress(byte[] data, int width, int height) {
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
                int length = bArrIntTo4Bytes.length;
                for (int i = 0; i < length; i++) {
                    bArr[i + 8] = bArrIntTo4Bytes[i];
                }
                int length2 = data.length;
                for (int i2 = 0; i2 < length2; i2++) {
                    bArr[i2 + 12] = data[i2];
                }
                return bArr;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public final byte[] getPrintStatus(int count) throws Exception {
            if (count == 0) {
                return null;
            }
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            if (printer2.WriteData(ExtKt.hexStringToBytes("1B 12 73")) == -1) {
                return null;
            }
            long jCurrentTimeMillis = System.currentTimeMillis();
            byte[] bArrReadDataMillisecond = ReadDataMillisecond(3000);
            if (bArrReadDataMillisecond.length <= 7) {
                bArrReadDataMillisecond = ReadDataMillisecond(3000);
            }
            logcat("getPrintStatus-time1->" + (System.currentTimeMillis() - jCurrentTimeMillis) + " count:" + count);
            return bArrReadDataMillisecond.length == 0 ? getPrintStatus(count - 1) : bArrReadDataMillisecond;
        }

        @JvmStatic
        public final byte[] getPrintStatusNotFilter(int count) {
            if (count <= 0) {
                return null;
            }
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            if (printer2.WriteData(ExtKt.hexStringToBytes("1B 12 73")) == -1) {
                return null;
            }
            long jCurrentTimeMillis = System.currentTimeMillis();
            byte[] bArrReadDataMillisecondNotFilter = ReadDataMillisecondNotFilter(2000);
            logcat("getPrintStatus-time2->" + (System.currentTimeMillis() - jCurrentTimeMillis) + " count:" + count);
            return bArrReadDataMillisecondNotFilter.length == 0 ? getPrintStatusNotFilter(count - 1) : bArrReadDataMillisecondNotFilter;
        }

        @JvmStatic
        public final byte[] getMTPrinterStatus(int count) {
            if (count <= 0) {
                return null;
            }
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            if (printer2.WriteData(ExtKt.hexStringToBytes("1B 12 73 1B 12 73")) == -1) {
                return null;
            }
            long jCurrentTimeMillis = System.currentTimeMillis();
            byte[] bArrReadDataMillisecond = ReadDataMillisecond(2000);
            logcat("getPrintStatus-time2->" + (System.currentTimeMillis() - jCurrentTimeMillis) + " count:" + count);
            if (bArrReadDataMillisecond.length == 0) {
                return getMTPrinterStatus(count - 1);
            }
            IPort printer3 = getPrinter();
            Intrinsics.checkNotNull(printer3);
            printer3.setConnectState(0);
            return bArrReadDataMillisecond;
        }

        /* JADX WARN: Removed duplicated region for block: B:7:0x0014  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public final Object getMTPrinterStatusNotFilter(int i, Continuation<? super byte[]> continuation) {
            HPRTPrinterHelper$Companion$getMTPrinterStatusNotFilter$1 hPRTPrinterHelper$Companion$getMTPrinterStatusNotFilter$1;
            if (continuation instanceof HPRTPrinterHelper$Companion$getMTPrinterStatusNotFilter$1) {
                hPRTPrinterHelper$Companion$getMTPrinterStatusNotFilter$1 = (HPRTPrinterHelper$Companion$getMTPrinterStatusNotFilter$1) continuation;
                if ((hPRTPrinterHelper$Companion$getMTPrinterStatusNotFilter$1.label & Integer.MIN_VALUE) != 0) {
                    hPRTPrinterHelper$Companion$getMTPrinterStatusNotFilter$1.label -= Integer.MIN_VALUE;
                } else {
                    hPRTPrinterHelper$Companion$getMTPrinterStatusNotFilter$1 = new HPRTPrinterHelper$Companion$getMTPrinterStatusNotFilter$1(this, continuation);
                }
            }
            Object obj = hPRTPrinterHelper$Companion$getMTPrinterStatusNotFilter$1.result;
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            int i2 = hPRTPrinterHelper$Companion$getMTPrinterStatusNotFilter$1.label;
            if (i2 == 0) {
                ResultKt.throwOnFailure(obj);
                if (i <= 0) {
                    return null;
                }
                IPort printer = getPrinter();
                Intrinsics.checkNotNull(printer);
                printer.setConnectState(1);
                hPRTPrinterHelper$Companion$getMTPrinterStatusNotFilter$1.I$0 = i;
                hPRTPrinterHelper$Companion$getMTPrinterStatusNotFilter$1.label = 1;
                if (DelayKt.delay(300L, hPRTPrinterHelper$Companion$getMTPrinterStatusNotFilter$1) != coroutine_suspended) {
                }
            }
            if (i2 != 1) {
                if (i2 != 2) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                long j = hPRTPrinterHelper$Companion$getMTPrinterStatusNotFilter$1.J$0;
                int i3 = hPRTPrinterHelper$Companion$getMTPrinterStatusNotFilter$1.I$0;
                ResultKt.throwOnFailure(obj);
                return obj;
            }
            i = hPRTPrinterHelper$Companion$getMTPrinterStatusNotFilter$1.I$0;
            ResultKt.throwOnFailure(obj);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            if (printer2.WriteData(ExtKt.hexStringToBytes("1B 12 73 1B 12 73")) == -1) {
                return null;
            }
            long jCurrentTimeMillis = System.currentTimeMillis();
            byte[] bArrReadDataMillisecondNotFilter = ReadDataMillisecondNotFilter(2000);
            logcat("getPrintStatus-time2->" + (System.currentTimeMillis() - jCurrentTimeMillis) + " count:" + i);
            IPort printer3 = getPrinter();
            Intrinsics.checkNotNull(printer3);
            printer3.setConnectState(0);
            if (bArrReadDataMillisecondNotFilter.length != 0) {
                return bArrReadDataMillisecondNotFilter;
            }
            hPRTPrinterHelper$Companion$getMTPrinterStatusNotFilter$1.L$0 = SpillingKt.nullOutSpilledVariable(bArrReadDataMillisecondNotFilter);
            hPRTPrinterHelper$Companion$getMTPrinterStatusNotFilter$1.I$0 = i;
            hPRTPrinterHelper$Companion$getMTPrinterStatusNotFilter$1.J$0 = jCurrentTimeMillis;
            hPRTPrinterHelper$Companion$getMTPrinterStatusNotFilter$1.label = 2;
            Object mTPrinterStatusNotFilter = getMTPrinterStatusNotFilter(i - 1, hPRTPrinterHelper$Companion$getMTPrinterStatusNotFilter$1);
            return mTPrinterStatusNotFilter == coroutine_suspended ? coroutine_suspended : mTPrinterStatusNotFilter;
        }

        public final void clearCache() throws Exception {
            WriteData(new byte[]{27, 18, 67, 27, 18, 67});
            ReadDataMillisecond(500);
        }

        @JvmStatic
        public final boolean setPollPrintDensity(int density) {
            if (density < 0 || density > 255) {
                return false;
            }
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            if (printer2.WriteData(new byte[]{BoolPtg.sid, 115, 101, 116, 99, (byte) density}) == -1) {
                IPort printer3 = getPrinter();
                Intrinsics.checkNotNull(printer3);
                printer3.setConnectState(0);
                return false;
            }
            IPort printer4 = getPrinter();
            Intrinsics.checkNotNull(printer4);
            printer4.setConnectState(0);
            return true;
        }

        @JvmStatic
        public final boolean setPrintPageType(int Type) {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            int iWriteData = printer2.WriteData(new byte[]{BoolPtg.sid, 115, 101, 116, 112, (byte) Type});
            IPort printer3 = getPrinter();
            Intrinsics.checkNotNull(printer3);
            printer3.setConnectState(0);
            return iWriteData != -1;
        }

        public final boolean setPageType(int Type) {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            printer2.WriteData(new byte[]{BoolPtg.sid, 115, 101, 116, 112, (byte) Type});
            byte[] bArrReadDataMillisecond = ReadDataMillisecond(2000);
            if (bArrReadDataMillisecond.length == 0) {
                return false;
            }
            return StringsKt.contains$default((CharSequence) new String(bArrReadDataMillisecond, Charsets.UTF_8), (CharSequence) WXModalUIModule.OK, false, 2, (Object) null);
        }

        public final void restartPrint() {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            printer2.WriteData(ExtKt.hexStringToBytes("1b 1c 26 20 56 31 20 64 6f 20 22 72 65 73 65 74 5f 70 72 69 6e 74 65 72 22 0d 0a"));
            IPort printer3 = getPrinter();
            Intrinsics.checkNotNull(printer3);
            printer3.ReadDataMillisecond(500);
            IPort printer4 = getPrinter();
            Intrinsics.checkNotNull(printer4);
            printer4.setConnectState(0);
        }

        @JvmStatic
        public final void setPrintPagePositionA200U(int Type) {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            printer2.WriteData(new byte[]{27, 97, (byte) Type});
            IPort printer3 = getPrinter();
            Intrinsics.checkNotNull(printer3);
            printer3.setConnectState(0);
        }

        public final int saveParamZone() {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            byte[] bytes = "& V1 do \"".getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
            byte[] bytes2 = "save_param_zone".getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes2, "getBytes(...)");
            byte[] bytes3 = "\"\r\n".getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes3, "getBytes(...)");
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            printer2.WriteData(Tools.mergeBytes(new byte[]{27, 28}, bytes, bytes2, bytes3));
            IPort printer3 = getPrinter();
            Intrinsics.checkNotNull(printer3);
            byte[] bArrReadDataMillisecond = printer3.ReadDataMillisecond(500);
            IPort printer4 = getPrinter();
            Intrinsics.checkNotNull(printer4);
            printer4.setConnectState(0);
            Intrinsics.checkNotNull(bArrReadDataMillisecond);
            if (bArrReadDataMillisecond.length == 0) {
                return -1;
            }
            return bArrReadDataMillisecond[0];
        }

        public final int saveParamAndResetPrinter() {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            byte[] bytes = "& V1 do \"".getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
            byte[] bytes2 = "save_param".getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes2, "getBytes(...)");
            byte[] bytes3 = "\"\r\n".getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes3, "getBytes(...)");
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            printer2.WriteData(Tools.mergeBytes(new byte[]{27, 28}, bytes, bytes2, bytes3));
            IPort printer3 = getPrinter();
            Intrinsics.checkNotNull(printer3);
            printer3.ReadDataMillisecond(500);
            IPort printer4 = getPrinter();
            Intrinsics.checkNotNull(printer4);
            printer4.setConnectState(0);
            return 4;
        }

        @JvmStatic
        public final boolean setPrintFeed(int feed) {
            if (feed >= 0 && feed <= 65535) {
                byte[] bArr = {(byte) (feed & 255), (byte) ((feed >> 8) & 255)};
                IPort printer = getPrinter();
                Intrinsics.checkNotNull(printer);
                printer.setConnectState(1);
                IPort printer2 = getPrinter();
                Intrinsics.checkNotNull(printer2);
                int iWriteData = printer2.WriteData(new byte[]{27, 27, 1, bArr[0], bArr[1]});
                IPort printer3 = getPrinter();
                Intrinsics.checkNotNull(printer3);
                printer3.setConnectState(0);
                if (iWriteData != -1) {
                    return true;
                }
            }
            return false;
        }

        @JvmStatic
        public final int start() throws Exception {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            byte[] bytes = "^XA\r\n".getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
            int iWriteData = printer2.WriteData(bytes);
            IPort printer3 = getPrinter();
            Intrinsics.checkNotNull(printer3);
            printer3.setConnectState(0);
            return iWriteData;
        }

        @JvmStatic
        public final int end(int quantity) throws Exception {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            Charset charsetForName = Charset.forName(getLanguageEncode());
            Intrinsics.checkNotNullExpressionValue(charsetForName, "forName(...)");
            byte[] bytes = ("^XGR:XXXX.GRF,1,1^FS\n^PQ" + quantity + ",1,1,Y\n^XZ\n^XA\n^IDR:XXXX.GRF\n^XZ\n").getBytes(charsetForName);
            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
            int iWriteData = printer2.WriteData(bytes);
            setConnectState(0);
            return iWriteData;
        }

        @JvmStatic
        public final int setXY(String X, String Y) throws Exception {
            Intrinsics.checkNotNullParameter(X, "X");
            Intrinsics.checkNotNullParameter(Y, "Y");
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            String str = "^FO" + X + "," + Y + "\r\n";
            Charset charsetForName = Charset.forName(getLanguageEncode());
            Intrinsics.checkNotNullExpressionValue(charsetForName, "forName(...)");
            byte[] bytes = str.getBytes(charsetForName);
            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
            int iWriteData = printer2.WriteData(bytes);
            setConnectState(0);
            return iWriteData;
        }

        @JvmStatic
        public final int printBitmapLZO_ZPL(Bitmap bmp, int algorithmMode) {
            int i;
            Intrinsics.checkNotNullParameter(bmp, "bmp");
            PrinterDataCore printerDataCore = new PrinterDataCore();
            printerDataCore.HalftoneMode = (byte) algorithmMode;
            printerDataCore.CompressMode = (byte) 5;
            byte[] bArrPrintDataFormat = printerDataCore.PrintDataFormat(bmp, 200);
            int length = bArrPrintDataFormat.length;
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            if (width % 8 == 0) {
                i = width / 8;
            } else {
                i = (width / 8) + 1;
            }
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            byte[] bytes = ("~DGR:XXXX.GRF," + (height * i) + "," + i + ",:LZO:" + length + ",").getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
            int iWriteData = printer2.WriteData(bytes);
            IPort printer3 = getPrinter();
            Intrinsics.checkNotNull(printer3);
            printer3.WriteData(bArrPrintDataFormat);
            IPort printer4 = getPrinter();
            Intrinsics.checkNotNull(printer4);
            byte[] bytes2 = "\r\n".getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes2, "getBytes(...)");
            printer4.WriteData(bytes2);
            IPort printer5 = getPrinter();
            Intrinsics.checkNotNull(printer5);
            printer5.setConnectState(0);
            return iWriteData;
        }

        @JvmStatic
        public final int setGapDetectTSPL() throws Exception {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            byte[] bytes = "GAPDETECT\r\n".getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
            int iWriteData = printer2.WriteData(bytes);
            IPort printer3 = getPrinter();
            Intrinsics.checkNotNull(printer3);
            printer3.setConnectState(0);
            return iWriteData;
        }

        @JvmStatic
        public final int setGapDetectESC() throws Exception {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            int iWriteData = printer2.WriteData(new byte[]{18, 33});
            IPort printer3 = getPrinter();
            Intrinsics.checkNotNull(printer3);
            printer3.setConnectState(0);
            return iWriteData;
        }

        @JvmStatic
        public final int setGapDetectA200U() throws Exception {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            int iWriteData = printer2.WriteData(new byte[]{27, 28, 38, 32, 86, 49, 32, 100, 111, 32, 34, 112, 97, 112, 101, 114, 95, 108, 101, 97, 114, 110, 34, 13, 10});
            IPort printer3 = getPrinter();
            Intrinsics.checkNotNull(printer3);
            printer3.setConnectState(0);
            return iWriteData;
        }

        @JvmStatic
        public final int setGapDetectTL31W(int type) throws Exception {
            byte[] bArr = new byte[1];
            if (type != 0) {
                if (type == 2) {
                    bArr[0] = 1;
                } else {
                    bArr[0] = 2;
                }
            }
            byte[] bArrHexStringToBytesMoreOneByte = ConvertUtil.hexStringToBytesMoreOneByte("1B 1C 26 20 56 31 20 64 6F 20 22 6C 6F 63 61 74 5F 6C 65 61 72 6E 22 0D 0A", bArr[0]);
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            int iWriteData = printer2.WriteData(bArrHexStringToBytesMoreOneByte);
            IPort printer3 = getPrinter();
            Intrinsics.checkNotNull(printer3);
            printer3.setConnectState(0);
            return iWriteData;
        }

        @JvmStatic
        public final int printBitmap(Bitmap bitmap, int type, int compressType, int byteLength) throws Exception {
            Intrinsics.checkNotNullParameter(bitmap, "bitmap");
            if (compressType == 0) {
                return printBitmap(bitmap, type, 200, false);
            }
            if (compressType != 1) {
                if (compressType != 2) {
                    return -2;
                }
                return printBitmap(bitmap, type, 200, true);
            }
            if (byteLength > 0) {
                return printBitmapPackage(bitmap, type, byteLength);
            }
            return printBitmap(bitmap, type);
        }

        @JvmStatic
        public final int printBitmapCPCL(Bitmap bitmap, int x, int y, int type, int compressType, int density, int number) throws Exception {
            if (bitmap == null) {
                return -2;
            }
            int height = bitmap.getHeight();
            if (getPrinter() == null) {
                return -1;
            }
            ArrayList arrayList = new ArrayList();
            byte[] bytes = ("! 0 200 200 " + height + Operators.SPACE_STR + number + "\r\n").getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
            arrayList.add(bytes);
            if (density != -1) {
                byte[] bytes2 = ("TONE " + density + "\r\n").getBytes(Charsets.UTF_8);
                Intrinsics.checkNotNullExpressionValue(bytes2, "getBytes(...)");
                arrayList.add(bytes2);
            }
            byte[] bitmapCPCLData = getBitmapCPCLData(bitmap, x, y, type, compressType);
            if (bitmapCPCLData == null) {
                return -2;
            }
            arrayList.add(bitmapCPCLData);
            byte[] bytes3 = "FORM\r\nPRINT\r\n".getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes3, "getBytes(...)");
            arrayList.add(bytes3);
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            printer2.WriteData(Tools.listToBytes(arrayList));
            setConnectState(0);
            return 0;
        }

        @JvmStatic
        public final int printBitmapCPCL(Bitmap bitmap, int x, int y, int type, int compressType, int printHeadWidth, int density, int number) throws Exception {
            if (bitmap == null) {
                return -2;
            }
            int height = bitmap.getHeight();
            if (getPrinter() == null) {
                return -1;
            }
            ArrayList arrayList = new ArrayList();
            byte[] bytes = ("! 0 200 200 " + height + Operators.SPACE_STR + number + "\r\n").getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
            arrayList.add(bytes);
            if (density != -1) {
                byte[] bytes2 = ("TONE " + density + "\r\n").getBytes(Charsets.UTF_8);
                Intrinsics.checkNotNullExpressionValue(bytes2, "getBytes(...)");
                arrayList.add(bytes2);
            }
            byte[] bitmapCPCLData = getBitmapCPCLData(bitmap, x, y, type, compressType);
            if (bitmapCPCLData == null) {
                return -2;
            }
            byte[] bytes3 = ("PW " + printHeadWidth + "\r\n").getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes3, "getBytes(...)");
            arrayList.add(bytes3);
            arrayList.add(bitmapCPCLData);
            byte[] bytes4 = "FORM\r\nPRINT\r\n".getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes4, "getBytes(...)");
            arrayList.add(bytes4);
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            printer2.WriteData(Tools.listToBytes(arrayList));
            IPort printer3 = getPrinter();
            Intrinsics.checkNotNull(printer3);
            printer3.setConnectState(0);
            return 0;
        }

        /* JADX WARN: Removed duplicated region for block: B:22:0x007e  */
        /* JADX WARN: Removed duplicated region for block: B:30:0x0098 A[ADDED_TO_REGION] */
        /* JADX WARN: Removed duplicated region for block: B:45:0x017f  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        private final byte[] getBitmapCPCLData(Bitmap bitmap, int x, int y, int type, int compressType) throws Exception {
            List<byte[]> listAddBytesToList;
            int i;
            int[] iArr;
            int i2;
            int i3 = compressType;
            if (bitmap == null) {
                return null;
            }
            ArrayList arrayList = new ArrayList();
            int width = bitmap.getWidth() % 8 == 0 ? bitmap.getWidth() / 8 : (bitmap.getWidth() / 8) + 1;
            int height = bitmap.getHeight();
            byte[] bArrPrintBitmap = PrintBitmap(bitmap, (byte) type, (byte) 0);
            int length = bArrPrintBitmap.length;
            if (i3 == 1) {
                if (length > 102400) {
                    int i4 = 102400 / width;
                    listAddBytesToList = Tools.addBytesToList(bArrPrintBitmap, i4 * width);
                    i = i4;
                    i3 = 2;
                    byte[] bArr = new byte[bArrPrintBitmap.length * 2];
                    iArr = new int[1];
                    new LZOCompress().lzoCompressData(bArrPrintBitmap, bArrPrintBitmap.length, bArr, iArr, new byte[64000]);
                    if (i3 == 2) {
                    }
                    i2 = iArr[0];
                    if (i2 > bArrPrintBitmap.length) {
                        byte[] bytes = ("CG " + width + Operators.SPACE_STR + height + Operators.SPACE_STR + x + Operators.SPACE_STR + y + Operators.SPACE_STR).getBytes(Charsets.UTF_8);
                        Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
                        arrayList.add(bytes);
                        arrayList.add(bArrPrintBitmap);
                    }
                    return Tools.listToBytes(arrayList);
                }
                listAddBytesToList = null;
            } else if (i3 != 2) {
                listAddBytesToList = null;
            } else {
                if (length > 102400) {
                    int i5 = 102400 / width;
                    listAddBytesToList = Tools.addBytesToList(bArrPrintBitmap, i5 * width);
                    i = i5;
                    byte[] bArr2 = new byte[bArrPrintBitmap.length * 2];
                    iArr = new int[1];
                    new LZOCompress().lzoCompressData(bArrPrintBitmap, bArrPrintBitmap.length, bArr2, iArr, new byte[64000]);
                    if ((i3 == 2 && iArr[0] + bArrPrintBitmap.length > 307200) || getPrinter() == null) {
                        return null;
                    }
                    i2 = iArr[0];
                    if (i2 > bArrPrintBitmap.length || i3 == 0) {
                        byte[] bytes2 = ("CG " + width + Operators.SPACE_STR + height + Operators.SPACE_STR + x + Operators.SPACE_STR + y + Operators.SPACE_STR).getBytes(Charsets.UTF_8);
                        Intrinsics.checkNotNullExpressionValue(bytes2, "getBytes(...)");
                        arrayList.add(bytes2);
                        arrayList.add(bArrPrintBitmap);
                    } else if (i3 == 1) {
                        byte[] bArr3 = new byte[i2];
                        System.arraycopy(bArr2, 0, bArr3, 0, i2);
                        byte[] bytes3 = ("CGLZO " + width + Operators.SPACE_STR + height + Operators.SPACE_STR + x + Operators.SPACE_STR + y + Operators.SPACE_STR + i2 + "\r\n").getBytes(Charsets.UTF_8);
                        Intrinsics.checkNotNullExpressionValue(bytes3, "getBytes(...)");
                        arrayList.add(bytes3);
                        arrayList.add(bArr3);
                    } else if (i3 == 2) {
                        List<byte[]> list = listAddBytesToList;
                        if (list == null || list.isEmpty()) {
                            return null;
                        }
                        int i6 = 0;
                        for (int size = list.size(); i6 < size; size = size) {
                            new LZOCompress().lzoCompressData(listAddBytesToList.get(i6), listAddBytesToList.get(i6).length, bArr2, iArr, new byte[64000]);
                            int i7 = iArr[0];
                            byte[] bArr4 = new byte[i7];
                            System.arraycopy(bArr2, 0, bArr4, 0, i7);
                            List<byte[]> list2 = listAddBytesToList;
                            byte[] bytes4 = ("CGLZO " + width + Operators.SPACE_STR + (listAddBytesToList.get(i6).length / width) + Operators.SPACE_STR + x + Operators.SPACE_STR + ((i6 * i) + y) + Operators.SPACE_STR + i7 + "\r\n").getBytes(Charsets.UTF_8);
                            Intrinsics.checkNotNullExpressionValue(bytes4, "getBytes(...)");
                            arrayList.add(bytes4);
                            arrayList.add(bArr4);
                            byte[] bytes5 = "\r\n".getBytes(Charsets.UTF_8);
                            Intrinsics.checkNotNullExpressionValue(bytes5, "getBytes(...)");
                            arrayList.add(bytes5);
                            i6++;
                            listAddBytesToList = list2;
                        }
                    }
                    return Tools.listToBytes(arrayList);
                }
                listAddBytesToList = null;
                i3 = 1;
            }
            i = 0;
            byte[] bArr22 = new byte[bArrPrintBitmap.length * 2];
            iArr = new int[1];
            new LZOCompress().lzoCompressData(bArrPrintBitmap, bArrPrintBitmap.length, bArr22, iArr, new byte[64000]);
            if (i3 == 2) {
            }
            i2 = iArr[0];
            if (i2 > bArrPrintBitmap.length) {
            }
            return Tools.listToBytes(arrayList);
        }

        /* JADX WARN: Removed duplicated region for block: B:26:0x007b A[RETURN] */
        /* JADX WARN: Removed duplicated region for block: B:28:0x007d  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        private final int sendBitmapCPCL(Bitmap bitmap, int x, int y, int type, int compressType) throws Exception {
            List<byte[]> listAddBytesToList;
            int i;
            int i2;
            if (bitmap == null) {
                return -2;
            }
            int width = bitmap.getWidth() % 8 == 0 ? bitmap.getWidth() / 8 : (bitmap.getWidth() / 8) + 1;
            int height = bitmap.getHeight();
            Companion companion = this;
            byte[] bArrPrintBitmap = companion.PrintBitmap(bitmap, (byte) type, (byte) 0);
            int length = bArrPrintBitmap.length;
            if (compressType != 2) {
                listAddBytesToList = null;
                i = compressType;
            } else {
                if (length > 102400) {
                    int i3 = 102400 / width;
                    listAddBytesToList = Tools.addBytesToList(bArrPrintBitmap, i3 * width);
                    i = compressType;
                    i2 = i3;
                    byte[] bArr = new byte[bArrPrintBitmap.length * 2];
                    int[] iArr = new int[1];
                    new LZOCompress().lzoCompressData(bArrPrintBitmap, bArrPrintBitmap.length, bArr, iArr, new byte[64000]);
                    if (i == 2 && iArr[0] + bArrPrintBitmap.length > 307200) {
                        return -3;
                    }
                    if (companion.getPrinter() != null) {
                        return -1;
                    }
                    IPort printer = companion.getPrinter();
                    Intrinsics.checkNotNull(printer);
                    printer.setConnectState(1);
                    int i4 = iArr[0];
                    if (i4 > bArrPrintBitmap.length || i == 0) {
                        IPort printer2 = getPrinter();
                        Intrinsics.checkNotNull(printer2);
                        byte[] bytes = ("CG " + width + Operators.SPACE_STR + height + Operators.SPACE_STR + x + Operators.SPACE_STR + y + Operators.SPACE_STR).getBytes(Charsets.UTF_8);
                        Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
                        printer2.WriteData(bytes);
                        IPort printer3 = getPrinter();
                        Intrinsics.checkNotNull(printer3);
                        printer3.WriteData(bArrPrintBitmap);
                    } else if (i == 1) {
                        byte[] bArr2 = new byte[i4];
                        System.arraycopy(bArr, 0, bArr2, 0, i4);
                        IPort printer4 = companion.getPrinter();
                        Intrinsics.checkNotNull(printer4);
                        byte[] bytes2 = ("CGLZO " + width + Operators.SPACE_STR + height + Operators.SPACE_STR + x + Operators.SPACE_STR + y + Operators.SPACE_STR + i4 + "\r\n").getBytes(Charsets.UTF_8);
                        Intrinsics.checkNotNullExpressionValue(bytes2, "getBytes(...)");
                        printer4.WriteData(bytes2);
                        IPort printer5 = companion.getPrinter();
                        Intrinsics.checkNotNull(printer5);
                        printer5.WriteData(bArr2);
                    } else if (i == 2) {
                        List<byte[]> list = listAddBytesToList;
                        if (list == null || list.isEmpty()) {
                            return -4;
                        }
                        int size = list.size();
                        int i5 = 0;
                        while (i5 < size) {
                            new LZOCompress().lzoCompressData(listAddBytesToList.get(i5), listAddBytesToList.get(i5).length, bArr, iArr, new byte[64000]);
                            int i6 = iArr[0];
                            byte[] bArr3 = new byte[i6];
                            System.arraycopy(bArr, 0, bArr3, 0, i6);
                            IPort printer6 = companion.getPrinter();
                            Intrinsics.checkNotNull(printer6);
                            int i7 = size;
                            int i8 = i5;
                            byte[] bytes3 = ("CGLZO " + width + Operators.SPACE_STR + (listAddBytesToList.get(i5).length / width) + Operators.SPACE_STR + x + Operators.SPACE_STR + ((i5 * i2) + y) + Operators.SPACE_STR + i6 + "\r\n").getBytes(Charsets.UTF_8);
                            Intrinsics.checkNotNullExpressionValue(bytes3, "getBytes(...)");
                            printer6.WriteData(bytes3);
                            IPort printer7 = getPrinter();
                            Intrinsics.checkNotNull(printer7);
                            printer7.WriteData(bArr3);
                            IPort printer8 = getPrinter();
                            Intrinsics.checkNotNull(printer8);
                            byte[] bytes4 = "\r\n".getBytes(Charsets.UTF_8);
                            Intrinsics.checkNotNullExpressionValue(bytes4, "getBytes(...)");
                            printer8.WriteData(bytes4);
                            i5 = i8 + 1;
                            companion = this;
                            size = i7;
                        }
                    }
                    IPort printer9 = getPrinter();
                    Intrinsics.checkNotNull(printer9);
                    printer9.setConnectState(0);
                    return 0;
                }
                listAddBytesToList = null;
                i = 1;
            }
            i2 = 0;
            byte[] bArr4 = new byte[bArrPrintBitmap.length * 2];
            int[] iArr2 = new int[1];
            new LZOCompress().lzoCompressData(bArrPrintBitmap, bArrPrintBitmap.length, bArr4, iArr2, new byte[64000]);
            if (i == 2) {
            }
            if (companion.getPrinter() != null) {
            }
        }

        @JvmStatic
        public final void cleanRead() {
            try {
                if (getPrinter() == null) {
                    return;
                }
                if (HPRTPrinterHelper.Is_BLE_Type) {
                    ReadDataMillisecond(50);
                } else {
                    do {
                    } while (!(ReadDataMillisecond(50).length == 0));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public final WIFIBean getWifiParameter() {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            if (printer2.WriteData(CommandData.getWifiCommand()) == -1) {
                return null;
            }
            byte[] bArrReadDataMillisecond = ReadDataMillisecond(5000);
            if (bArrReadDataMillisecond.length == 0) {
                return null;
            }
            return Tools.setWifiBean(new WIFIBean(), new String(bArrReadDataMillisecond, Charsets.UTF_8));
        }

        public final int setWifiParameter(WIFIBean wifiBean) {
            if (wifiBean == null) {
                return -2;
            }
            String ssid = wifiBean.getSsid();
            Intrinsics.checkNotNullExpressionValue(ssid, "getSsid(...)");
            byte[] bytes = ssid.getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
            if (bytes.length > 28) {
                return -2;
            }
            String password = wifiBean.getPassword();
            Intrinsics.checkNotNullExpressionValue(password, "getPassword(...)");
            byte[] bytes2 = password.getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes2, "getBytes(...)");
            if (bytes2.length > 28) {
                return -2;
            }
            if (!Intrinsics.areEqual(wifiBean.getMode(), PrintConstant.WifiMode.MODE_STA) && !Intrinsics.areEqual(wifiBean.getMode(), "AP")) {
                return -2;
            }
            cleanRead();
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            if (printer2.WriteData(CommandData.setWifiCommand(wifiBean)) == -1) {
                return -1;
            }
            IPort printer3 = getPrinter();
            Intrinsics.checkNotNull(printer3);
            if (printer3.WriteData(CommandData.setRestartWifi()) == -1) {
                return -1;
            }
            byte[] bArrReadDataMillisecond = ReadDataMillisecond(HttpUrlConnectionNetworkFetcher.HTTP_DEFAULT_TIMEOUT);
            if (bArrReadDataMillisecond.length == 0) {
                return -3;
            }
            String str = new String(bArrReadDataMillisecond, Charsets.UTF_8);
            Log.d("Print", "sRead: ".concat(str));
            String str2 = str;
            if (StringsKt.contains$default((CharSequence) str2, (CharSequence) "wifi_ready", false, 2, (Object) null)) {
                return 0;
            }
            return StringsKt.contains$default((CharSequence) str2, (CharSequence) "wifi_error", false, 2, (Object) null) ? -4 : -5;
        }

        @JvmStatic
        public final int setGapDetectCPCL() {
            if (getPrinter() == null) {
                return -1;
            }
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            int iWriteData = printer2.WriteData(new byte[]{BoolPtg.sid, 115, 101, 116, 76});
            ReadDataMillisecond(500);
            return iWriteData;
        }

        @JvmStatic
        public final String getNfcUid() {
            if (getPrinter() != null) {
                IPort printer = getPrinter();
                Intrinsics.checkNotNull(printer);
                printer.setConnectState(1);
                byte[] bArrHexStringToBytes = ExtKt.hexStringToBytes("1B 1C 26 20 56 31 20 67 65 74 76 61 6C 20 22 6E 66 63 5F 75 69 64 22 0D 0A");
                IPort printer2 = getPrinter();
                Intrinsics.checkNotNull(printer2);
                printer2.WriteData(bArrHexStringToBytes);
                byte[] bArrReadDataMillisecondNotFilter = ReadDataMillisecondNotFilter(300);
                if (bArrReadDataMillisecondNotFilter.length <= 7) {
                    return null;
                }
                String str = new String(new byte[]{bArrReadDataMillisecondNotFilter[0], bArrReadDataMillisecondNotFilter[1], bArrReadDataMillisecondNotFilter[2], bArrReadDataMillisecondNotFilter[3], bArrReadDataMillisecondNotFilter[4], bArrReadDataMillisecondNotFilter[5], bArrReadDataMillisecondNotFilter[6]}, Charsets.UTF_8);
                if (StringsKt.contains$default((CharSequence) DataFilter.HEAD_NFC_UID, (CharSequence) str, false, 2, (Object) null)) {
                    byte[] bArr = new byte[4];
                    if (bArrReadDataMillisecondNotFilter.length < 11) {
                        return null;
                    }
                    System.arraycopy(bArrReadDataMillisecondNotFilter, 7, bArr, 0, 4);
                    String strByteToHex = Tools.byteToHex(bArr);
                    Intrinsics.checkNotNull(strByteToHex);
                    return StringsKt.replace$default(strByteToHex, Operators.SPACE_STR, "", false, 4, (Object) null);
                }
                if (Intrinsics.areEqual("pooli_s", str)) {
                    DataFilter.filter(bArrReadDataMillisecondNotFilter, getFilterListener());
                }
            }
            return null;
        }

        public final String getNFCTemplate() {
            if (getPrinter() != null) {
                byte[] bArrHexStringToBytes = ExtKt.hexStringToBytes("1B 1C 26 20 56 31 20 67 65 74 76 61 6C 20 22 6E 66 63 5F 72 69 62 62 6F 6E 5F 74 79 70 65 22 0D 0A");
                IPort printer = getPrinter();
                Intrinsics.checkNotNull(printer);
                printer.setConnectState(1);
                IPort printer2 = getPrinter();
                Intrinsics.checkNotNull(printer2);
                printer2.WriteData(bArrHexStringToBytes);
                byte[] bArrReadDataMillisecond = ReadDataMillisecond(1000, true);
                if (bArrReadDataMillisecond.length == 0) {
                    bArrReadDataMillisecond = ReadDataMillisecond(1000, true);
                    if (bArrReadDataMillisecond.length == 0) {
                        return null;
                    }
                }
                Log.e("TAG", "onFilter- getNFCTemplate: " + ByteUtils.INSTANCE.bytetohex(bArrReadDataMillisecond));
                byte[] bArr = new byte[15];
                if (bArrReadDataMillisecond.length < 15) {
                    return null;
                }
                System.arraycopy(bArrReadDataMillisecond, 0, bArr, 0, 15);
                if (StringsKt.contains$default((CharSequence) DataFilter.HEAD_NFC_RIBBON_TYPE, (CharSequence) new String(bArr, Charsets.UTF_8), false, 2, (Object) null) && bArrReadDataMillisecond.length > 16) {
                    byte[] bArr2 = new byte[16];
                    if (bArrReadDataMillisecond.length < 31) {
                        return null;
                    }
                    System.arraycopy(bArrReadDataMillisecond, 15, bArr2, 0, 16);
                    String str = new String(deleteZeroByte(bArr2), Charsets.UTF_8);
                    if (str.length() > 0 && getFilterListener() != null) {
                        DataFilter.filter(bArr2, getFilterListener());
                    }
                    return str;
                }
            }
            return null;
        }

        public final byte[] getBattery() {
            byte[] bArr = {BoolPtg.sid, 103, 101, 116, 118};
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            printer2.WriteData(bArr);
            return ReadDataMillisecond(500);
        }

        public final byte[] getPooliPaperType() {
            byte[] bArr = {BoolPtg.sid, 103, 101, 116, 112};
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            printer2.WriteData(bArr);
            return ReadDataMillisecond(500);
        }

        @JvmStatic
        public final RFIDInfo getNFCVerifyInfo(int count) {
            RFIDInfo rFIDInfo = null;
            if (count == 0) {
                return null;
            }
            if (getPrinter() != null) {
                IPort printer = getPrinter();
                Intrinsics.checkNotNull(printer);
                printer.setConnectState(1);
                IPort printer2 = getPrinter();
                Intrinsics.checkNotNull(printer2);
                printer2.WriteData(new byte[]{27, 28, 38, 32, 86, 49, 32, 103, 101, 116, 118, 97, 108, 32, 34, 110, 102, 99, 95, 118, 101, 114, 105, 102, 121, 95, 105, 110, 102, 111, 34, 13, 10});
                byte[] bArrReadDataMillisecond = ReadDataMillisecond(500);
                if (bArrReadDataMillisecond.length == 0) {
                    bArrReadDataMillisecond = ReadDataMillisecond(2000);
                }
                if (bArrReadDataMillisecond.length == 0) {
                    return getNFCVerifyInfo(count - 1);
                }
                if (StringsKt.contains$default((CharSequence) "nfc_verify_info", (CharSequence) new String(new byte[]{bArrReadDataMillisecond[0], bArrReadDataMillisecond[1], bArrReadDataMillisecond[2], bArrReadDataMillisecond[3], bArrReadDataMillisecond[4], bArrReadDataMillisecond[5], bArrReadDataMillisecond[6], bArrReadDataMillisecond[7], bArrReadDataMillisecond[8], bArrReadDataMillisecond[9], bArrReadDataMillisecond[10], bArrReadDataMillisecond[11], bArrReadDataMillisecond[12], bArrReadDataMillisecond[13], bArrReadDataMillisecond[14]}, Charsets.UTF_8), false, 2, (Object) null)) {
                    rFIDInfo = new RFIDInfo();
                    byte[] bArr = new byte[4];
                    if (bArrReadDataMillisecond.length < 19) {
                        return getNFCVerifyInfo(count - 1);
                    }
                    System.arraycopy(bArrReadDataMillisecond, 15, bArr, 0, 4);
                    rFIDInfo.uid = Tools.byteToHex(bArr);
                    byte[] bArr2 = new byte[4];
                    if (bArrReadDataMillisecond.length < 23) {
                        return getNFCVerifyInfo(count - 1);
                    }
                    System.arraycopy(bArrReadDataMillisecond, 19, bArr2, 0, 4);
                    rFIDInfo.remainMileage = String.valueOf(ByteUtils.INSTANCE.bytesToInt(bArr2, 0));
                }
            }
            return rFIDInfo;
        }

        public final byte[] deleteZeroByte(byte[] data) {
            Intrinsics.checkNotNullParameter(data, "data");
            int length = data.length;
            int i = 0;
            for (int i2 = 0; i2 < length && data[i2] != 0; i2++) {
                i++;
            }
            byte[] bArr = new byte[i];
            System.arraycopy(data, 0, bArr, 0, i);
            return bArr;
        }

        @JvmStatic
        public final boolean setPollForm(int feed) {
            if (feed < 0) {
                return false;
            }
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            return printer.WriteData(new byte[]{BoolPtg.sid, 102, (byte) (feed & 255), (byte) ((feed >> 8) & 255)}) != -1;
        }

        public static /* synthetic */ String getNewPrinterSN$default(Companion companion, int i, int i2, int i3, Object obj) {
            if ((i3 & 2) != 0) {
                i2 = 0;
            }
            return companion.getNewPrinterSN(i, i2);
        }

        @JvmStatic
        public final String getNewPrinterSN(int count, int type) {
            String str;
            if (count == 0) {
                return "";
            }
            try {
                if (type == 0) {
                    if (getPrinter() instanceof WiFiOperator) {
                        byte[] bArrExtractTextResponse = extractTextResponse(writeAndReadTextResponse(ExtKt.hexStringToBytes("1b 1c 26 20 56 31 20 67 65 74 76 61 6c 20 22 73 65 72 69 61 6c 5f 6e 6f 22 0d 0a"), 5000));
                        return (bArrExtractTextResponse.length == 0 ? 1 : 0) == 0 ? new String(bArrExtractTextResponse, Charsets.UTF_8) : getNewPrinterSN(count - 1, type);
                    }
                    WriteData(ExtKt.hexStringToBytes("1b 1c 26 20 56 31 20 67 65 74 6b 65 79 0d 0a 00 01 00 20"));
                    byte[] bArrReadDataMillisecond = ReadDataMillisecond(500);
                    ArrayList arrayList = new ArrayList();
                    int length = bArrReadDataMillisecond.length;
                    while (i < length) {
                        byte b = bArrReadDataMillisecond[i];
                        if (b != 0) {
                            arrayList.add(Byte.valueOf(b));
                        }
                        i++;
                    }
                    if (CollectionsKt.toByteArray(arrayList).length == 0) {
                        WriteData(ExtKt.hexStringToBytes("1b 1c 26 20 56 31 20 67 65 74 76 61 6c 20 22 73 65 72 69 61 6c 5f 6e 6f 22 0d 0a"));
                        bArrReadDataMillisecond = ReadDataMillisecond(500);
                        if (bArrReadDataMillisecond.length == 0) {
                            return getNewPrinterSN(count - 1, type);
                        }
                    }
                    str = new String(deleteZeroByte(bArrReadDataMillisecond), Charsets.UTF_8);
                } else {
                    WriteData(ExtKt.hexStringToBytes("1b 12 4e"));
                    byte[] bArrReadDataMillisecond2 = ReadDataMillisecond(500);
                    ArrayList arrayList2 = new ArrayList();
                    for (byte b2 : bArrReadDataMillisecond2) {
                        if (b2 != 0) {
                            arrayList2.add(Byte.valueOf(b2));
                        }
                    }
                    if (CollectionsKt.toByteArray(arrayList2).length == 0) {
                        WriteData(ExtKt.hexStringToBytes("1b 1c 26 20 56 31 20 67 65 74 6b 65 79 0d 0a 00 01 00 20"));
                        bArrReadDataMillisecond2 = ReadDataMillisecond(500);
                        if (bArrReadDataMillisecond2.length == 0) {
                            return getNewPrinterSN(count - 1, 1);
                        }
                    }
                    str = new String(deleteZeroByte(bArrReadDataMillisecond2), Charsets.UTF_8);
                    if (StringsKt.startsWith$default(str, "sn", false, 2, (Object) null)) {
                        str = str.substring(StringsKt.indexOf$default((CharSequence) str, "sn", 0, false, 6, (Object) null) + 2);
                        Intrinsics.checkNotNullExpressionValue(str, "substring(...)");
                    }
                }
                return str;
            } catch (Exception unused) {
                return "";
            }
        }

        public static /* synthetic */ String getNewCPCLPrinterSN$default(Companion companion, int i, int i2, int i3, Object obj) {
            if ((i3 & 2) != 0) {
                i2 = 0;
            }
            return companion.getNewCPCLPrinterSN(i, i2);
        }

        @JvmStatic
        public final String getNewCPCLPrinterSN(int count, int type) {
            String str;
            if (count == 0) {
                return "";
            }
            try {
                if (type == 0) {
                    WriteData(new byte[]{BoolPtg.sid, 73, 68});
                    byte[] bArrReadDataMillisecond = ReadDataMillisecond(500);
                    ArrayList arrayList = new ArrayList();
                    for (byte b : bArrReadDataMillisecond) {
                        if (b != 0) {
                            arrayList.add(Byte.valueOf(b));
                        }
                    }
                    if (CollectionsKt.toByteArray(arrayList).length == 0) {
                        WriteData(ExtKt.hexStringToBytes("1b 1c 26 20 56 31 20 67 65 74 6b 65 79 0d 0a 00 01 00 20"));
                        byte[] bArrReadDataMillisecond2 = ReadDataMillisecond(500);
                        if (bArrReadDataMillisecond2.length == 0) {
                            return getNewCPCLPrinterSN$default(this, count - 1, 0, 2, null);
                        }
                        str = new String(deleteZeroByte(bArrReadDataMillisecond2), Charsets.UTF_8);
                    } else {
                        str = new String(deleteZeroByte(bArrReadDataMillisecond), Charsets.UTF_8);
                    }
                } else {
                    WriteData(ExtKt.hexStringToBytes("1b 1c 26 20 56 31 20 67 65 74 6b 65 79 0d 0a 00 01 00 20"));
                    byte[] bArrReadDataMillisecond3 = ReadDataMillisecond(500);
                    ArrayList arrayList2 = new ArrayList();
                    for (byte b2 : bArrReadDataMillisecond3) {
                        if (b2 != 0) {
                            arrayList2.add(Byte.valueOf(b2));
                        }
                    }
                    if (CollectionsKt.toByteArray(arrayList2).length == 0) {
                        WriteData(ExtKt.hexStringToBytes("1b 12 4e"));
                        bArrReadDataMillisecond3 = ReadDataMillisecond(500);
                        if (bArrReadDataMillisecond3.length == 0) {
                            return getNewCPCLPrinterSN(count - 1, 1);
                        }
                    }
                    str = new String(deleteZeroByte(bArrReadDataMillisecond3), Charsets.UTF_8);
                    if (StringsKt.startsWith$default(str, "sn", false, 2, (Object) null)) {
                        str = str.substring(StringsKt.indexOf$default((CharSequence) str, "sn", 0, false, 6, (Object) null) + 2);
                        Intrinsics.checkNotNullExpressionValue(str, "substring(...)");
                    }
                }
                return str;
            } catch (Exception unused) {
                return "";
            }
        }

        @JvmStatic
        public final String getStandardESCPrinterSN(int count) {
            if (count == 0) {
                return "";
            }
            try {
                WriteData(new byte[]{BoolPtg.sid, 73, 68});
                byte[] bArrReadDataMillisecond = ReadDataMillisecond(500);
                return bArrReadDataMillisecond.length == 0 ? getStandardESCPrinterSN(count - 1) : StringsKt.trimStart(new String(deleteZeroByte(bArrReadDataMillisecond), Charsets.UTF_8), NameUtil.USCORE);
            } catch (Exception unused) {
                return "";
            }
        }

        @JvmStatic
        public final String getStandardESCPrinterName(int count) {
            if (count == 0) {
                return "";
            }
            try {
                WriteData(new byte[]{BoolPtg.sid, 73, 67});
                byte[] bArrReadDataMillisecond = ReadDataMillisecond(500);
                if (bArrReadDataMillisecond.length == 0) {
                    return getStandardESCPrinterName(count - 1);
                }
                return StringsKt.trim((CharSequence) new Regex("[^A-Za-z0-9_\\s&^-]+").replace(StringsKt.trimStart(new String(deleteZeroByte(bArrReadDataMillisecond), Charsets.UTF_8), NameUtil.USCORE), "")).toString();
            } catch (Exception unused) {
                return "";
            }
        }

        @JvmStatic
        public final String getStandardESCPrinterVersion(int count) {
            if (count == 0) {
                return "";
            }
            try {
                WriteData(new byte[]{BoolPtg.sid, 73, 65});
                byte[] bArrReadDataMillisecond = ReadDataMillisecond(500);
                if (bArrReadDataMillisecond.length == 0) {
                    return getStandardESCPrinterVersion(count - 1);
                }
                return StringsKt.trim((CharSequence) new Regex("[^0-9.&^-]+").replace((CharSequence) CollectionsKt.first(StringsKt.split$default((CharSequence) StringsKt.trimStart(new String(deleteZeroByte(bArrReadDataMillisecond), Charsets.UTF_8), NameUtil.USCORE), new String[]{"/"}, false, 0, 6, (Object) null)), "")).toString();
            } catch (Exception unused) {
                return "";
            }
        }

        public final boolean setA200UPaperType(int type) {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            if (type == 0) {
                IPort printer2 = getPrinter();
                Intrinsics.checkNotNull(printer2);
                int iWriteData = printer2.WriteData(new byte[]{27, 28, 38, 32, 86, 49, 32, 115, 101, 116, 118, 97, 108, 32, 34, 112, 97, 112, 101, 114, 95, 116, 121, 112, 101, 34, 32, 34, 48, 34, 13, 10});
                IPort printer3 = getPrinter();
                Intrinsics.checkNotNull(printer3);
                printer3.setConnectState(0);
                return iWriteData != -1;
            }
            IPort printer4 = getPrinter();
            Intrinsics.checkNotNull(printer4);
            int iWriteData2 = printer4.WriteData(new byte[]{27, 28, 38, 32, 86, 49, 32, 115, 101, 116, 118, 97, 108, 32, 34, 112, 97, 112, 101, 114, 95, 116, 121, 112, 101, 34, 32, 34, 49, 34, 13, 10});
            IPort printer5 = getPrinter();
            Intrinsics.checkNotNull(printer5);
            printer5.setConnectState(0);
            return iWriteData2 != -1;
        }

        public final boolean setPaperType(int type) {
            if (type >= 0 && type <= 3) {
                IPort printer = getPrinter();
                Intrinsics.checkNotNull(printer);
                printer.setConnectState(1);
                IPort printer2 = getPrinter();
                Intrinsics.checkNotNull(printer2);
                int iWriteData = printer2.WriteData(new byte[]{27, 28, 38, 32, 86, 49, 32, 115, 101, 116, 107, 101, 121, 13, 10, 1, -50, 0, 1, (byte) type});
                IPort printer3 = getPrinter();
                Intrinsics.checkNotNull(printer3);
                printer3.setConnectState(0);
                if (iWriteData != -1) {
                    return true;
                }
            }
            return false;
        }

        public final boolean checkConnection() {
            if (getPrinter() == null) {
                return false;
            }
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            OutputStream outputStream = printer.getMmOutStream();
            if (outputStream == null) {
                return false;
            }
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            printer2.setConnectState(1);
            try {
                outputStream.write(new byte[]{27, 28, 38, 32, 86, 49, 32, 103, 101, 116, 118, 97, 108, 32, 34, 112, 114, 105, 110, 116, 101, 114, 95, 110, 97, 109, 101, 34, 13, 10});
                return ReadDataMillisecond(2000).length != 0;
            } catch (IOException unused) {
                Log.e("TAG", "socket is close");
                return false;
            }
        }

        public final boolean checkConnectionSunmi() {
            if (getPrinter() == null) {
                return false;
            }
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            OutputStream outputStream = printer.getMmOutStream();
            if (outputStream == null) {
                return false;
            }
            try {
                outputStream.write(new byte[]{27, Ptg.CLASS_ARRAY});
                return true;
            } catch (IOException unused) {
                Log.e("TAG", "socket is close");
                return false;
            }
        }

        @JvmStatic
        public final int printBitmapPackage(Bitmap bitmap, int bitType, int packageSize) throws Exception {
            if (bitmap == null) {
                return -2;
            }
            int width = bitmap.getWidth();
            int i = width % 8;
            int i2 = width / 8;
            if (i != 0) {
                i2++;
            }
            bitmap.getHeight();
            PrinterDataCore printerDataCore = new PrinterDataCore();
            byte b = (byte) bitType;
            printerDataCore.HalftoneMode = b;
            printerDataCore.CompressMode = (byte) 4;
            byte[] bArrPrintDataFormat = printerDataCore.PrintDataFormat(bitmap, 200);
            if (bArrPrintDataFormat.length > packageSize) {
                List<byte[]> listAddBytesToList = Tools.addBytesToList(bArrPrintDataFormat, (packageSize / i2) * i2);
                int size = listAddBytesToList.size();
                for (int i3 = 0; i3 < size; i3++) {
                    if (WriteData(printerDataCore.SubcontractingLzo(listAddBytesToList.get(i3), i2, listAddBytesToList.get(i3).length / i2)) == -1) {
                        setConnectState(0);
                        return -1;
                    }
                }
                setConnectState(0);
            } else {
                PrintBitmap(bitmap, b, (byte) 0, 200, true);
            }
            return 1;
        }

        private final int PrintBitmap(Bitmap bmp, byte halftoneType, byte scaleMode, int printdpi, boolean isCompress) throws Exception {
            return CreateBitmapPrintDatas(bmp, halftoneType, scaleMode, printdpi, isCompress);
        }

        @JvmStatic
        public final int printDoubleColorBitmap(Bitmap blackBitmap, Bitmap redBitmap, int x, int y, int type, int compressType, int density, int number) throws Exception {
            if (blackBitmap == null || redBitmap == null) {
                return -2;
            }
            int height = blackBitmap.getHeight();
            if (getPrinter() == null) {
                return -1;
            }
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            if (printer.WriteData(CommandData.getStartCPCLCommand(height, number)) == -1) {
                return -1;
            }
            if (density != -1) {
                IPort printer2 = getPrinter();
                Intrinsics.checkNotNull(printer2);
                printer2.WriteData(CommandData.getToneCPCLCommand(density));
            }
            IPort printer3 = getPrinter();
            Intrinsics.checkNotNull(printer3);
            printer3.WriteData(CommandData.getLayerCommand(1));
            int iSendBitmapCPCL = sendBitmapCPCL(blackBitmap, x, y, type, compressType);
            if (iSendBitmapCPCL != 0) {
                return iSendBitmapCPCL;
            }
            IPort printer4 = getPrinter();
            Intrinsics.checkNotNull(printer4);
            printer4.WriteData(CommandData.getLayerCommand(0));
            int iSendBitmapCPCL2 = sendBitmapCPCL(redBitmap, x, y, type, compressType);
            if (iSendBitmapCPCL2 != 0) {
                return iSendBitmapCPCL2;
            }
            IPort printer5 = getPrinter();
            Intrinsics.checkNotNull(printer5);
            printer5.WriteData(CommandData.getFormCPCLCommand());
            IPort printer6 = getPrinter();
            Intrinsics.checkNotNull(printer6);
            printer6.WriteData(CommandData.getPrintCPCLCommand());
            setConnectState(0);
            return 0;
        }

        public final void setOnDataFilterListener(OnDataFilterListener filterListener) {
            if (HPRTPrinterHelper.INSTANCE.getFilterListener() == null) {
                HPRTPrinterHelper.INSTANCE.setFilterListener(filterListener);
            } else if (filterListener == null) {
                HPRTPrinterHelper.INSTANCE.setFilterListener(null);
            }
        }

        @JvmStatic
        public final boolean setESCFeed(int feed) throws Exception {
            setConnectState(1);
            byte[] bArr = new byte[feed];
            for (int i = 0; i < feed; i++) {
                bArr[i] = 10;
            }
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            int iWriteData = printer.WriteData(bArr);
            setConnectState(0);
            return iWriteData != -1;
        }

        public final int getPackageSize() {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            byte[] bArrHexStringToBytes = ExtKt.hexStringToBytes("1B 1C 26 20 56 31 20 67 65 74 76 61 6C 20 22 70 61 63 6B 5F 6C 65 6E 67 74 68 22 0D 0A");
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            printer2.WriteData(bArrHexStringToBytes);
            byte[] bArrReadData = ReadData(1000);
            if (bArrReadData.length == 0) {
                IPort printer3 = getPrinter();
                Intrinsics.checkNotNull(printer3);
                printer3.setConnectState(0);
                return 2016;
            }
            IPort printer4 = getPrinter();
            Intrinsics.checkNotNull(printer4);
            printer4.setConnectState(0);
            if (bArrReadData.length == 4) {
                return ByteUtils.INSTANCE.bytes4ToInt_l(bArrReadData, 0);
            }
            return 2016;
        }

        public static /* synthetic */ byte[] reqD31SConfig$default(Companion companion, String str, int i, Object obj) {
            if ((i & 1) != 0) {
                str = "TSPL";
            }
            return companion.reqD31SConfig(str);
        }

        public final byte[] reqD31SConfig(String type) throws Exception {
            byte[] bArrHexStringToBytes;
            Intrinsics.checkNotNullParameter(type, "type");
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            if (Intrinsics.areEqual(type, "ESC")) {
                bArrHexStringToBytes = ExtKt.hexStringToBytes("12 62");
            } else {
                bArrHexStringToBytes = ExtKt.hexStringToBytes("5a 5a 5a 43 52 0A");
            }
            WriteData(bArrHexStringToBytes);
            byte[] bArrReadDataMillisecond = ReadDataMillisecond(1000);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            printer2.setConnectState(0);
            return bArrReadDataMillisecond;
        }

        public final D31SConfig getConfig(byte[] bytes) {
            Intrinsics.checkNotNullParameter(bytes, "bytes");
            D31SConfig d31SConfig = new D31SConfig(null, null, false, null, null, false, false, false, 0, 0, 0, 0, null, 8191, null);
            if (bytes.length != 256) {
                HPRTPrinterHelper.INSTANCE.logcatI("数据长度不够");
                return d31SConfig;
            }
            if (bytes[0] != 'x') {
                HPRTPrinterHelper.INSTANCE.logcatI("不是D31S返回的配置信息");
                return d31SConfig;
            }
            logcatI("name: " + getString(bytes, 9, 16));
            String string = getString(bytes, 199, JfifUtil.MARKER_RST7);
            d31SConfig.setBlueName(string);
            logcatI("蓝牙名称: " + string);
            String string2 = getString(bytes, 20, 24);
            d31SConfig.setBluePassword(string2);
            logcatI("蓝牙密码: " + string2);
            boolean z = ((char) bytes[162]) == 1;
            d31SConfig.setSimplePair(z);
            logcatI("简单配对: " + z);
            int i = bytes[56];
            logcatI("国际字符集: " + getCharSet(i));
            d31SConfig.setCharSet(getCharSet(i));
            int i2 = bytes[55];
            d31SConfig.setCodePage(getCodePage(i2));
            logcatI("codePage: " + getCodePage(i2));
            boolean z2 = ((char) bytes[83]) == 1;
            d31SConfig.setAutoLocate(z2);
            logcatI("开机自动定位: " + z2);
            boolean z3 = ((char) bytes[100]) == 1;
            d31SConfig.setRePrint(z3);
            logcatI("出错重打: " + z3);
            boolean z4 = ((char) bytes[113]) == 1;
            d31SConfig.setRePrintTip(z4);
            logcatI("是否打印重打提示信息: " + z4);
            int i3 = bytes[34];
            d31SConfig.setPrintDensity(i3);
            logcatI("打印浓度: " + i3);
            int i4 = bytes[58];
            d31SConfig.setPaperType(i4);
            logcatI("纸张类型: ".concat(i4 == 0 ? "连续纸" : "标签纸"));
            if (bytes[131] == 0) {
                d31SConfig.setHeatValue(bytes[130]);
                logcatI("加热表微调: " + bytes[130]);
            } else {
                d31SConfig.setHeatValue(bytes[130]);
                logcatI("加热表微调: " + d31SConfig.getHeatValue());
            }
            int i5 = bytes[132];
            d31SConfig.setPrintSpeed(i5);
            logcatI("打印速度: " + i5);
            String strValueOf = String.valueOf(bytes[114]);
            d31SConfig.setStopPrint(strValueOf);
            logcatI("停止打印: " + strValueOf);
            int i6 = 0;
            for (int i7 = 0; i7 < 254; i7++) {
                int i8 = bytes[i7];
                if (i8 < 0) {
                    i8 += 256;
                }
                i6 += i8;
            }
            logcatI("校验位1: " + toHex((byte) (i6 % 256)) + Operators.SPACE_STR + toHex((byte) (i6 / 256)));
            return d31SConfig;
        }

        public static /* synthetic */ void setConfig$default(Companion companion, byte[] bArr, String str, D31SConfig d31SConfig, int i, Object obj) throws Exception {
            if ((i & 2) != 0) {
                str = "TSPL";
            }
            companion.setConfig(bArr, str, d31SConfig);
        }

        public final void setConfig(byte[] data, String model, D31SConfig config) throws Exception {
            byte[] bArrHexStringToBytes;
            Intrinsics.checkNotNullParameter(data, "data");
            Intrinsics.checkNotNullParameter(model, "model");
            Intrinsics.checkNotNullParameter(config, "config");
            if (Intrinsics.areEqual(model, "ESC")) {
                bArrHexStringToBytes = ExtKt.hexStringToBytes("12 63");
            } else {
                bArrHexStringToBytes = ExtKt.hexStringToBytes("5a 5a 5a 43 57 20");
            }
            byte[] bArrHexStringToBytes2 = ExtKt.hexStringToBytes("0d 0a");
            byte[] bArr = new byte[bArrHexStringToBytes.length + 256 + bArrHexStringToBytes2.length];
            int printDensity = config.getPrintDensity();
            int paperType = config.getPaperType();
            int heatValue = config.getHeatValue();
            int printSpeed = config.getPrintSpeed();
            String stopPrint = config.getStopPrint();
            String charSet = config.getCharSet();
            String codePage = config.getCodePage();
            boolean autoLocate = config.getAutoLocate();
            boolean rePrint = config.getRePrint();
            boolean rePrintTip = config.getRePrintTip();
            String blueName = config.getBlueName();
            String bluePassword = config.getBluePassword();
            boolean zIsSimplePair = config.isSimplePair();
            data[34] = (byte) printDensity;
            data[58] = (byte) paperType;
            if (heatValue >= 0) {
                data[130] = (byte) heatValue;
                data[131] = 0;
            } else {
                data[130] = (byte) heatValue;
                data[131] = -1;
            }
            data[132] = (byte) printSpeed;
            data[114] = Byte.parseByte(stopPrint);
            data[56] = (byte) getCharSetPos(charSet);
            data[55] = (byte) getCodePagePos(codePage);
            data[83] = autoLocate ? (byte) 1 : (byte) 0;
            data[100] = rePrint ? (byte) 1 : (byte) 0;
            data[113] = rePrintTip ? (byte) 1 : (byte) 0;
            byte[] bytes = blueName.getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
            byte[] bArr2 = new byte[16];
            System.arraycopy(bytes, 0, bArr2, 0, bytes.length);
            System.arraycopy(bArr2, 0, data, 199, 16);
            byte[] bytes2 = bluePassword.getBytes(Charsets.UTF_8);
            Intrinsics.checkNotNullExpressionValue(bytes2, "getBytes(...)");
            System.arraycopy(bytes2, 0, data, 20, bytes2.length);
            data[162] = zIsSimplePair ? (byte) 1 : (byte) 0;
            int i = 0;
            for (int i2 = 0; i2 < 254; i2++) {
                int i3 = data[i2];
                if (i3 < 0) {
                    i3 += 256;
                }
                i += i3;
            }
            byte b = (byte) (i % 256);
            data[254] = b;
            data[255] = (byte) (i / 256);
            logcat("校验位: " + toHex(b) + Operators.SPACE_STR + toHex(data[255]));
            logcat("==================设置的参数===================");
            getConfig(data);
            logcat("==================设置的数据===================");
            System.arraycopy(bArrHexStringToBytes, 0, bArr, 0, bArrHexStringToBytes.length);
            System.arraycopy(data, 0, bArr, bArrHexStringToBytes.length, data.length);
            System.arraycopy(bArrHexStringToBytes2, 0, bArr, bArrHexStringToBytes.length + data.length, bArrHexStringToBytes2.length);
            logcat(byteToHexWithEmpty(bArr));
            WriteData(bArr);
        }

        private final String toHex(int i) {
            StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
            String str = String.format("%02X", Arrays.copyOf(new Object[]{Integer.valueOf(i)}, 1));
            Intrinsics.checkNotNullExpressionValue(str, "format(...)");
            return str;
        }

        public final String getCodePage(int codePagePos) {
            if (codePagePos == 0) {
                return "Pc437(USA Standard Europe)";
            }
            if (codePagePos == 1) {
                return "Katakana";
            }
            if (codePagePos == 2) {
                return "Pc850(Multilingual)";
            }
            if (codePagePos == 3) {
                return "Pc860(Portuguese)";
            }
            if (codePagePos == 4) {
                return "Pc863(Canadian-French)";
            }
            if (codePagePos == 5) {
                return "Pc865(Nordic)";
            }
            if (codePagePos == 26) {
                return "TIS18(Thai)";
            }
            if (codePagePos == 36) {
                return "Pc862(Hebrew)";
            }
            if (codePagePos == 37) {
                return "Pc864(Arabic)";
            }
            if (codePagePos == 39) {
                return "ISO8859-2(Latin2)";
            }
            if (codePagePos != 40) {
                switch (codePagePos) {
                    case 13:
                        return "Pc857(Turkish)";
                    case 14:
                        return "Pc737(Greek)";
                    case 15:
                        return "ISO8859-7(Greek)";
                    case 16:
                        return "WPC1252";
                    case 17:
                        return "Pc866(Cyrillic #2)";
                    case 18:
                        return "Pc852(Latin2)";
                    case 19:
                        return "Pc858(Euro)";
                    case 20:
                        return "KU42(UK)";
                    case 21:
                        return "TIS11(Thai)";
                    default:
                        switch (codePagePos) {
                            case 32:
                                return "Pc720(Arabic)";
                            case 33:
                                return "WPC775";
                            case 34:
                                return "Pc855(Cyrillic)";
                            default:
                                switch (codePagePos) {
                                    case 45:
                                        return "WPC1250(Latin2)";
                                    case 46:
                                        return "WPC1251(Cyrillic)";
                                    case 47:
                                        return "WPC1253(Greek)";
                                    case 48:
                                        return "WPC1254(Turkish)";
                                    case 49:
                                        return "WPC1255(Hebrew)";
                                    case 50:
                                        return "WPC1256(Arabic)";
                                    case 51:
                                        return "WPC1257(Baltic)";
                                    case 52:
                                        return "WPC1258(Vietnam)";
                                    default:
                                        switch (codePagePos) {
                                            case 54:
                                                return "MIK(Cyrillic/Bulgarian)";
                                            case 55:
                                                return "CP775(Latin5)";
                                            case 56:
                                                return "Iran";
                                            case 57:
                                                return "Iran II";
                                            case 58:
                                                return "Latvian";
                                            case 59:
                                                return "ISO-8859-1(West Europe)";
                                            case 60:
                                                return "ISO-8859-3(Latin3)";
                                            case 61:
                                                return "ISO-8859-4(Baltic)";
                                            case 62:
                                                return "ISO-8859-5(Cyrillic)";
                                            case 63:
                                                return "ISO-8859-6(Arabic)";
                                            case 64:
                                                return "ISO-8859-8(Hebrew)";
                                            case 65:
                                                return "ISO-8859-9(Latin5)";
                                            case 66:
                                                return "PC856(Hebrew)";
                                            case 67:
                                                return "ABICOMP";
                                            case 68:
                                                return "MONGOLIAN";
                                            default:
                                                return "";
                                        }
                                }
                        }
                }
            }
            return "ISO8859-15(Latin9)";
        }

        /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
        public final int getCodePagePos(String codePage) {
            Intrinsics.checkNotNullParameter(codePage, "codePage");
            switch (codePage.hashCode()) {
                case -2037840888:
                    return !codePage.equals("WPC1252") ? 0 : 16;
                case -1993723053:
                    return !codePage.equals("ISO-8859-4(Baltic)") ? 0 : 61;
                case -1752249311:
                    return !codePage.equals("MIK(Cyrillic/Bulgarian)") ? 0 : 54;
                case -1728298869:
                    return !codePage.equals("WPC775") ? 0 : 33;
                case -1725167766:
                    return !codePage.equals("ISO-8859-6(Arabic)") ? 0 : 63;
                case -1702509288:
                    return !codePage.equals("CP775(Latin5)") ? 0 : 55;
                case -1606282096:
                    return !codePage.equals("Pc850(Multilingual)") ? 0 : 2;
                case -1432039837:
                    return !codePage.equals("WPC1254(Turkish)") ? 0 : 48;
                case -1400090808:
                    return !codePage.equals("WPC1253(Greek)") ? 0 : 47;
                case -1377459804:
                    return !codePage.equals("Pc866(Cyrillic #2)") ? 0 : 17;
                case -1359107135:
                    return !codePage.equals("WPC1251(Cyrillic)") ? 0 : 46;
                case -1287988255:
                    return !codePage.equals("ISO8859-2(Latin2)") ? 0 : 39;
                case -1156318229:
                    return !codePage.equals("WPC1258(Vietnam)") ? 0 : 52;
                case -973160632:
                    return !codePage.equals("PC856(Hebrew)") ? 0 : 66;
                case -887669892:
                    return !codePage.equals("Pc858(Euro)") ? 0 : 19;
                case -575093174:
                    return !codePage.equals("Iran II") ? 0 : 57;
                case -513203357:
                    return !codePage.equals("TIS11(Thai)") ? 0 : 21;
                case -482787497:
                    return !codePage.equals("ABICOMP") ? 0 : 67;
                case -379048242:
                    return !codePage.equals("ISO-8859-1(West Europe)") ? 0 : 59;
                case -125865938:
                    return !codePage.equals("ISO-8859-5(Cyrillic)") ? 0 : 62;
                case 2287414:
                    return !codePage.equals("Iran") ? 0 : 56;
                case 105897412:
                    return !codePage.equals("ISO-8859-3(Latin3)") ? 0 : 60;
                case 107079974:
                    return !codePage.equals("Pc857(Turkish)") ? 0 : 13;
                case 284639405:
                    return !codePage.equals("WPC1257(Baltic)") ? 0 : 51;
                case 344084729:
                    return !codePage.equals("WPC1250(Latin2)") ? 0 : 45;
                case 374742153:
                    return !codePage.equals("Pc737(Greek)") ? 0 : 14;
                case 375897579:
                    return !codePage.equals("Pc863(Canadian-French)") ? 0 : 4;
                case 419909323:
                    return !codePage.equals("Pc860(Portuguese)") ? 0 : 3;
                case 501667065:
                    return !codePage.equals("ISO-8859-8(Hebrew)") ? 0 : 64;
                case 734138040:
                    return !codePage.equals("Pc864(Arabic)") ? 0 : 37;
                case 739854413:
                    return !codePage.equals("WPC1255(Hebrew)") ? 0 : 49;
                case 888222388:
                    return !codePage.equals("ISO8859-15(Latin9)") ? 0 : 40;
                case 898257956:
                    codePage.equals("Pc437(USA Standard Europe)");
                    return 0;
                case 954262229:
                    return !codePage.equals("Pc852(Latin2)") ? 0 : 18;
                case 1121797438:
                    return !codePage.equals("MONGOLIAN") ? 0 : 68;
                case 1169147500:
                    return !codePage.equals("Katakana") ? 0 : 1;
                case 1404355114:
                    return !codePage.equals("TIS18(Thai)") ? 0 : 26;
                case 1526325395:
                    return !codePage.equals("KU42(UK)") ? 0 : 20;
                case 1600856131:
                    return !codePage.equals("Pc862(Hebrew)") ? 0 : 36;
                case 1606982687:
                    return !codePage.equals("Pc855(Cyrillic)") ? 0 : 34;
                case 1617240350:
                    return !codePage.equals("ISO8859-7(Greek)") ? 0 : 15;
                case 1618578463:
                    return !codePage.equals("Latvian") ? 0 : 58;
                case 1680590785:
                    return !codePage.equals("WPC1256(Arabic)") ? 0 : 50;
                case 1804168824:
                    return !codePage.equals("Pc865(Nordic)") ? 0 : 5;
                case 1957026679:
                    return !codePage.equals("Pc720(Arabic)") ? 0 : 32;
                case 2146072584:
                    return !codePage.equals("ISO-8859-9(Latin5)") ? 0 : 65;
                default:
                    return 0;
            }
        }

        public final String byteToHexWithEmpty(byte[] data) {
            Intrinsics.checkNotNullParameter(data, "data");
            StringBuilder sb = new StringBuilder(data.length);
            for (byte b : data) {
                StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
                String str = String.format("%02X ", Arrays.copyOf(new Object[]{Byte.valueOf(b)}, 1));
                Intrinsics.checkNotNullExpressionValue(str, "format(...)");
                sb.append(str);
            }
            String string = sb.toString();
            Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
            return string;
        }

        public final String getString(byte[] bytes, int start, int end) {
            Intrinsics.checkNotNullParameter(bytes, "bytes");
            if (start < 0 || end > bytes.length) {
                return "";
            }
            int i = end - start;
            byte[] bArr = new byte[i];
            System.arraycopy(bytes, start, bArr, 0, i);
            return new String(deleteZeroByte(bArr), Charsets.UTF_8);
        }

        public final String getCharSet(int pos) {
            switch (pos) {
                case 0:
                    return "U.S.A.";
                case 1:
                    return "France";
                case 2:
                    return "Germany";
                case 3:
                    return "U.K.";
                case 4:
                    return "Denmark I";
                case 5:
                    return "Sweden";
                case 6:
                    return "Italy";
                case 7:
                    return "Spain I";
                case 8:
                    return "Japan";
                case 9:
                    return "Norway";
                case 10:
                    return "Denmark II";
                case 11:
                    return "Spain II";
                case 12:
                    return "Latin";
                case 13:
                    return "Korea";
                case 14:
                    return "Slovenia/Croatia";
                case 15:
                    return "China";
                default:
                    return "";
            }
        }

        /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
        public final int getCharSetPos(String charSet) {
            Intrinsics.checkNotNullParameter(charSet, "charSet");
            switch (charSet.hashCode()) {
                case -1955869026:
                    return !charSet.equals("Norway") ? 0 : 9;
                case -1816488575:
                    charSet.equals("U.S.A.");
                    return 0;
                case -1805740532:
                    return !charSet.equals("Sweden") ? 0 : 5;
                case -662818333:
                    return !charSet.equals("Denmark I") ? 0 : 4;
                case -347324526:
                    return !charSet.equals("Spain I") ? 0 : 7;
                case 2578812:
                    return !charSet.equals("U.K.") ? 0 : 3;
                case 20901853:
                    return !charSet.equals("Slovenia/Croatia") ? 0 : 14;
                case 65078583:
                    return !charSet.equals("China") ? 0 : 15;
                case 70969475:
                    return !charSet.equals("Italy") ? 0 : 6;
                case 71341030:
                    return !charSet.equals("Japan") ? 0 : 8;
                case 72683658:
                    return !charSet.equals("Korea") ? 0 : 13;
                case 73192164:
                    return !charSet.equals("Latin") ? 0 : 12;
                case 927468230:
                    return !charSet.equals("Denmark II") ? 0 : 10;
                case 1588421523:
                    return !charSet.equals("Germany") ? 0 : 2;
                case 2112320571:
                    return !charSet.equals("France") ? 0 : 1;
                case 2117841655:
                    return !charSet.equals("Spain II") ? 0 : 11;
                default:
                    return 0;
            }
        }

        public static /* synthetic */ void setPageMode$default(Companion companion, int i, int i2, Object obj) throws Exception {
            if ((i2 & 1) != 0) {
                i = 0;
            }
            companion.setPageMode(i);
        }

        public final void setPageMode(int type) throws Exception {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            WriteData(ExtKt.hexStringToBytes("1b 1c 26 20 56 31 20 73 65 74 76 61 6c 20 22 70 61 70 65 72 5f 74 79 70 65 22 20 22 " + (type == 2 ? "33" : "31") + " 22 0d 0a"));
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            printer2.setConnectState(0);
        }

        public final int getCarbonSurplus() throws Exception {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            WriteData(ExtKt.hexStringToBytes("1b 12 53"));
            byte[] bArrReadDataMillisecondNotFilter = ReadDataMillisecondNotFilter(2000);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            printer2.setConnectState(0);
            if (bArrReadDataMillisecondNotFilter.length == 0) {
                return 0;
            }
            return ByteUtils.INSTANCE.bytes4ToInt_l_and(bArrReadDataMillisecondNotFilter, 5);
        }

        public final String getCarbonNo() throws Exception {
            IPort printer = getPrinter();
            Intrinsics.checkNotNull(printer);
            printer.setConnectState(1);
            WriteData(ExtKt.hexStringToBytes("1b 12 52"));
            byte[] bArrReadDataMillisecondNotFilter = ReadDataMillisecondNotFilter(1000);
            IPort printer2 = getPrinter();
            Intrinsics.checkNotNull(printer2);
            printer2.setConnectState(0);
            return getCarbonNo(bArrReadDataMillisecondNotFilter);
        }

        public final String getCarbonNo(byte[] data) {
            int iLastIndexOf;
            Intrinsics.checkNotNullParameter(data, "data");
            if (data.length == 0 || data.length <= 5 || (iLastIndexOf = ArraysKt.lastIndexOf(data, (byte) 0)) == -1 || iLastIndexOf <= 6) {
                return "—";
            }
            int i = iLastIndexOf - 6;
            byte[] bArr = new byte[i];
            System.arraycopy(data, 6, bArr, 0, i);
            return new String(bArr, Charsets.UTF_8);
        }

        public final boolean setDensity(int level) {
            try {
                byte[] bArr = {-53, 0, 1, (byte) level};
                IPort printer = getPrinter();
                Intrinsics.checkNotNull(printer);
                printer.setConnectState(1);
                int iWriteData = WriteData(Tools.mergeBytes(new byte[]{27, 28, 38, 32, 86, 49, 32, 115, 101, 116, 107, 101, 121, 13, 10, 1}, bArr));
                ReadDataMillisecondNotFilter(1000);
                IPort printer2 = getPrinter();
                Intrinsics.checkNotNull(printer2);
                printer2.setConnectState(0);
                return iWriteData == 0;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @JvmStatic
        public final int PortOpen(String portSetting) throws Exception {
            List listEmptyList;
            boolean zOpenPort;
            Intrinsics.checkNotNullParameter(portSetting, "portSetting");
            String str = portSetting;
            int length = str.length() - 1;
            int i = 0;
            boolean z = false;
            while (i <= length) {
                boolean z2 = Intrinsics.compare((int) str.charAt(!z ? i : length), 32) <= 0;
                if (z) {
                    if (!z2) {
                        break;
                    }
                    length--;
                } else if (z2) {
                    i++;
                } else {
                    z = true;
                }
            }
            if (str.subSequence(i, length + 1).toString().length() <= 4) {
                return -1;
            }
            List<String> listSplit = new Regex(",").split(str, 0);
            if (!listSplit.isEmpty()) {
                ListIterator<String> listIterator = listSplit.listIterator(listSplit.size());
                while (listIterator.hasPrevious()) {
                    if (listIterator.previous().length() != 0) {
                        listEmptyList = CollectionsKt.take(listSplit, listIterator.nextIndex() + 1);
                        break;
                    }
                }
                listEmptyList = CollectionsKt.emptyList();
            } else {
                listEmptyList = CollectionsKt.emptyList();
            }
            String[] strArr = (String[]) listEmptyList.toArray(new String[0]);
            if (Intrinsics.areEqual(strArr[0], "Bluetooth")) {
                if (strArr.length != 2) {
                    return -2;
                }
                String str2 = HPRTPrinterHelper.PrinterName;
                HPRTPrinterHelper.printer = str2 != null ? new BTOperator(HPRTPrinterHelper.PreContext, str2) : null;
                IPort printer = getPrinter();
                Intrinsics.checkNotNull(printer, "null cannot be cast to non-null type HPRTAndroidSDK.BTOperator");
                ((BTOperator) printer).IsBLEType(HPRTPrinterHelper.Is_BLE_Type);
                IPort printer2 = getPrinter();
                Intrinsics.checkNotNull(printer2, "null cannot be cast to non-null type HPRTAndroidSDK.BTOperator");
                zOpenPort = ((BTOperator) printer2).OpenPort(strArr[1]);
            } else if (Intrinsics.areEqual(strArr[0], "WiFi")) {
                if (strArr.length != 3) {
                    return -2;
                }
                HPRTPrinterHelper.printer = new WiFiOperator(HPRTPrinterHelper.PreContext, HPRTPrinterHelper.PrinterName);
                IPort printer3 = getPrinter();
                Intrinsics.checkNotNull(printer3, "null cannot be cast to non-null type HPRTAndroidSDK.WiFiOperator");
                zOpenPort = ((WiFiOperator) printer3).OpenPort(strArr[1], strArr[2]);
            } else if (!Intrinsics.areEqual(strArr[0], "Serial")) {
                zOpenPort = false;
            } else {
                if (strArr.length != 3) {
                    return -2;
                }
                HPRTPrinterHelper.printer = new SerialOperator(HPRTPrinterHelper.PreContext, strArr[1]);
                IPort printer4 = getPrinter();
                Intrinsics.checkNotNull(printer4, "null cannot be cast to non-null type HPRTAndroidSDK.SerialOperator");
                zOpenPort = ((SerialOperator) printer4).OpenPort(strArr[1], strArr[2]);
            }
            Log.d("Print", "isConnection：" + zOpenPort);
            if (!zOpenPort) {
                HPRTPrinterHelper.isPortOpen = false;
                return -1;
            }
            IPort printer5 = getPrinter();
            Intrinsics.checkNotNull(printer5);
            printer5.setIsFirst(true);
            if (!HPRTConst.isShack) {
                HPRTPrinterHelper.isPortOpen = true;
                return 0;
            }
            boolean unused = HPRTPrinterHelper.Is_BLE_Type;
            IPort printer6 = getPrinter();
            Intrinsics.checkNotNull(printer6);
            printer6.setIsFirst(false);
            HPRTPrinterHelper.isPortOpen = true;
            int iNextInt = new Random().nextInt(100) + 1;
            int iNextInt2 = new Random().nextInt(100) + 1;
            int iChackHands = Check.ChackHands(iNextInt, iNextInt2);
            if (iChackHands == 2 && (getPrinter() instanceof WiFiOperator)) {
                IPort printer7 = getPrinter();
                Intrinsics.checkNotNull(printer7);
                if (printer7.reConnect()) {
                    iChackHands = Check.ChackHands(iNextInt, iNextInt2);
                }
            }
            if (iChackHands != 0) {
                if (iChackHands == 1 || iChackHands == 2) {
                    Check.count = 0;
                    HPRTPrinterHelper.isPortOpen = true;
                    return 0;
                }
                PortClose();
                HPRTPrinterHelper.isPortOpen = false;
                return iChackHands;
            }
            IPort printer8 = getPrinter();
            Intrinsics.checkNotNull(printer8);
            printer8.setIsFirst(false);
            IPort printer9 = getPrinter();
            Intrinsics.checkNotNull(printer9);
            printer9.setKey(iNextInt, iNextInt2);
            HPRTPrinterHelper.isPortOpen = true;
            return 0;
        }
    }
}
