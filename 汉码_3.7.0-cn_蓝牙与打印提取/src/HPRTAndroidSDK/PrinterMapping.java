package HPRTAndroidSDK;

import com.google.gson.annotations.SerializedName;
import com.prt.provider.data.bean.PrintCompressModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: PrinterMapping.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\t\u0018\u0000 \"2\u00020\u0001:\u0001\"B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010!\u001a\u00020\u0005H\u0016R\u0014\u0010\u0004\u001a\u0004\u0018\u00010\u00058\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R \u0010\u0006\u001a\u0004\u0018\u00010\u00058\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u0012\u0010\u000b\u001a\u00020\f8\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u001e\u0010\r\u001a\u00020\f8\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u001e\u0010\u0012\u001a\u00020\f8\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u000f\"\u0004\b\u0014\u0010\u0011R\u001e\u0010\u0015\u001a\u00020\f8\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u000f\"\u0004\b\u0017\u0010\u0011R\u001c\u0010\u0018\u001a\f\u0012\u0006\u0012\u0004\u0018\u00010\u001a\u0018\u00010\u00198\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u001e\u0010\u001b\u001a\u00020\f8\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\u000f\"\u0004\b\u001d\u0010\u0011R\u001e\u0010\u001e\u001a\u00020\f8\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010\u000f\"\u0004\b \u0010\u0011¨\u0006#"}, d2 = {"LHPRTAndroidSDK/PrinterMapping;", "", "<init>", "()V", "printerName", "", "instruct", "getInstruct", "()Ljava/lang/String;", "setInstruct", "(Ljava/lang/String;)V", "compress", "", "encryption", "getEncryption", "()I", "setEncryption", "(I)V", "printerHeadWidth", "getPrinterHeadWidth", "setPrinterHeadWidth", "printerDpi", "getPrinterDpi", "setPrinterDpi", "compressModelList", "", "Lcom/prt/provider/data/bean/PrintCompressModel;", "subContract", "getSubContract", "setSubContract", "biColorPrint", "getBiColorPrint", "setBiColorPrint", "toString", "Companion", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class PrinterMapping {
    public static final int COMPRESS = 1;
    public static final String CPCL = "CPCL";
    public static final String CPCL_T260 = "T260CPCL";
    public static final String ESC = "ESC";
    public static final String ESC_POLI = "ESC_POLI";
    private static final List<String> INSTRUCTION_LIST;
    public static final int MODEL_ESC_ALL = 2;
    public static final int MODEL_ESC_DEX = 1;
    public static final int MODEL_TSPL_16 = 4;
    public static final int MODEL_TSPL_3 = 3;
    public static final int MODEL_UN_SUPPORT = 5;
    public static final int MODEL_ZPL_COMPRESS = 6;
    public static final String TSPL = "TSPL";
    public static final int UN_COMPRESS = 2;
    public static final String ZPL = "ZPL";

    @SerializedName("bicolor_print")
    private int biColorPrint = 1;

    @SerializedName("compress")
    public int compress;

    @SerializedName("compressModelList")
    public List<PrintCompressModel> compressModelList;

    @SerializedName("encryption")
    private int encryption;

    @SerializedName("instruct")
    private String instruct;

    @SerializedName("printerDpi")
    private int printerDpi;

    @SerializedName("printerHeadWidth")
    private int printerHeadWidth;

    @SerializedName("name")
    public String printerName;

    @SerializedName("subcontract")
    private int subContract;

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    public static final int MODEL_UN_KNOW = -1;

    public final String getInstruct() {
        return this.instruct;
    }

    public final void setInstruct(String str) {
        this.instruct = str;
    }

    public final int getEncryption() {
        return this.encryption;
    }

    public final void setEncryption(int i) {
        this.encryption = i;
    }

    public final int getPrinterHeadWidth() {
        return this.printerHeadWidth;
    }

    public final void setPrinterHeadWidth(int i) {
        this.printerHeadWidth = i;
    }

    public final int getPrinterDpi() {
        return this.printerDpi;
    }

    public final void setPrinterDpi(int i) {
        this.printerDpi = i;
    }

    public final int getSubContract() {
        return this.subContract;
    }

    public final void setSubContract(int i) {
        this.subContract = i;
    }

    public final int getBiColorPrint() {
        return this.biColorPrint;
    }

    public final void setBiColorPrint(int i) {
        this.biColorPrint = i;
    }

    public String toString() {
        return "PrinterMapping{printerName='" + this.printerName + "', instruct='" + this.instruct + "', compress=" + this.compress + ", encryption=" + this.encryption + ", printerHeadWidth=" + this.printerHeadWidth + ", printerDpi=" + this.printerDpi + ", compressModelList=" + this.compressModelList + ", subContract=" + this.subContract + ", biColorPrint=" + this.biColorPrint + "}";
    }

    /* JADX INFO: compiled from: PrinterMapping.kt */
    @Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010!\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\fX\u0086T¢\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u00020\f8\u0006X\u0087D¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\fX\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\fX\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\fX\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\fX\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\fX\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\fX\u0086T¢\u0006\u0002\n\u0000R\u0019\u0010\u0015\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\u0016¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018¨\u0006\u0019"}, d2 = {"LHPRTAndroidSDK/PrinterMapping$Companion;", "", "<init>", "()V", "ESC", "", PrinterMapping.ESC_POLI, "TSPL", PrinterMapping.ZPL, PrinterMapping.CPCL, "CPCL_T260", "COMPRESS", "", "UN_COMPRESS", "MODEL_UN_KNOW", "MODEL_ESC_DEX", "MODEL_ESC_ALL", "MODEL_TSPL_3", "MODEL_TSPL_16", "MODEL_UN_SUPPORT", "MODEL_ZPL_COMPRESS", "INSTRUCTION_LIST", "", "getINSTRUCTION_LIST", "()Ljava/util/List;", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final List<String> getINSTRUCTION_LIST() {
            return PrinterMapping.INSTRUCTION_LIST;
        }
    }

    static {
        ArrayList arrayList = new ArrayList();
        INSTRUCTION_LIST = arrayList;
        Locale locale = Locale.getDefault();
        Intrinsics.checkNotNullExpressionValue(locale, "getDefault(...)");
        String upperCase = "ESC".toUpperCase(locale);
        Intrinsics.checkNotNullExpressionValue(upperCase, "toUpperCase(...)");
        arrayList.add(upperCase);
        Locale locale2 = Locale.getDefault();
        Intrinsics.checkNotNullExpressionValue(locale2, "getDefault(...)");
        String upperCase2 = "TSPL".toUpperCase(locale2);
        Intrinsics.checkNotNullExpressionValue(upperCase2, "toUpperCase(...)");
        arrayList.add(upperCase2);
        Locale locale3 = Locale.getDefault();
        Intrinsics.checkNotNullExpressionValue(locale3, "getDefault(...)");
        String upperCase3 = ZPL.toUpperCase(locale3);
        Intrinsics.checkNotNullExpressionValue(upperCase3, "toUpperCase(...)");
        arrayList.add(upperCase3);
        Locale locale4 = Locale.getDefault();
        Intrinsics.checkNotNullExpressionValue(locale4, "getDefault(...)");
        String upperCase4 = CPCL.toUpperCase(locale4);
        Intrinsics.checkNotNullExpressionValue(upperCase4, "toUpperCase(...)");
        arrayList.add(upperCase4);
        Locale locale5 = Locale.getDefault();
        Intrinsics.checkNotNullExpressionValue(locale5, "getDefault(...)");
        String upperCase5 = CPCL_T260.toUpperCase(locale5);
        Intrinsics.checkNotNullExpressionValue(upperCase5, "toUpperCase(...)");
        arrayList.add(upperCase5);
        Locale locale6 = Locale.getDefault();
        Intrinsics.checkNotNullExpressionValue(locale6, "getDefault(...)");
        String upperCase6 = ESC_POLI.toUpperCase(locale6);
        Intrinsics.checkNotNullExpressionValue(upperCase6, "toUpperCase(...)");
        arrayList.add(upperCase6);
    }
}
