package HPRTAndroidSDK.bean;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import anet.channel.strategy.dispatch.DispatchConstants;
import com.prt.print.utils.TemplateOperation;
import com.taobao.weex.el.parse.Operators;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: D31SConfig.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b'\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0015\b\u0086\b\u0018\u00002\u00020\u0001B\u0089\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\u0006\u0012\b\b\u0002\u0010\n\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u000b\u001a\u00020\u0006\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\b\b\u0002\u0010\u000e\u001a\u00020\r\u0012\b\b\u0002\u0010\u000f\u001a\u00020\r\u0012\b\b\u0002\u0010\u0010\u001a\u00020\r\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u0003¢\u0006\u0004\b\u0012\u0010\u0013J\u0016\u00103\u001a\u00020\u00032\u0006\u00104\u001a\u0002052\u0006\u00106\u001a\u00020\rJ\u001e\u00107\u001a\u0002082\u0006\u00104\u001a\u0002052\u0006\u00109\u001a\u00020\r2\u0006\u0010:\u001a\u00020\u0003J\t\u0010;\u001a\u00020\u0003HÆ\u0003J\t\u0010<\u001a\u00020\u0003HÆ\u0003J\t\u0010=\u001a\u00020\u0006HÆ\u0003J\t\u0010>\u001a\u00020\u0003HÆ\u0003J\t\u0010?\u001a\u00020\u0003HÆ\u0003J\t\u0010@\u001a\u00020\u0006HÆ\u0003J\t\u0010A\u001a\u00020\u0006HÆ\u0003J\t\u0010B\u001a\u00020\u0006HÆ\u0003J\t\u0010C\u001a\u00020\rHÆ\u0003J\t\u0010D\u001a\u00020\rHÆ\u0003J\t\u0010E\u001a\u00020\rHÆ\u0003J\t\u0010F\u001a\u00020\rHÆ\u0003J\t\u0010G\u001a\u00020\u0003HÆ\u0003J\u008b\u0001\u0010H\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u00062\b\b\u0002\u0010\n\u001a\u00020\u00062\b\b\u0002\u0010\u000b\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\r2\b\b\u0002\u0010\u000f\u001a\u00020\r2\b\b\u0002\u0010\u0010\u001a\u00020\r2\b\b\u0002\u0010\u0011\u001a\u00020\u0003HÆ\u0001J\u0013\u0010I\u001a\u00020\u00062\b\u0010J\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010K\u001a\u00020\rHÖ\u0001J\t\u0010L\u001a\u00020\u0003HÖ\u0001R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0015\"\u0004\b\u0019\u0010\u0017R\u001a\u0010\u0005\u001a\u00020\u0006X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u001a\"\u0004\b\u001b\u0010\u001cR\u001a\u0010\u0007\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001d\u0010\u0015\"\u0004\b\u001e\u0010\u0017R\u001a\u0010\b\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010\u0015\"\u0004\b \u0010\u0017R\u001a\u0010\t\u001a\u00020\u0006X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b!\u0010\u001a\"\u0004\b\"\u0010\u001cR\u001a\u0010\n\u001a\u00020\u0006X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b#\u0010\u001a\"\u0004\b$\u0010\u001cR\u001a\u0010\u000b\u001a\u00020\u0006X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b%\u0010\u001a\"\u0004\b&\u0010\u001cR\u001a\u0010\f\u001a\u00020\rX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b'\u0010(\"\u0004\b)\u0010*R\u001a\u0010\u000e\u001a\u00020\rX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b+\u0010(\"\u0004\b,\u0010*R\u001a\u0010\u000f\u001a\u00020\rX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b-\u0010(\"\u0004\b.\u0010*R\u001a\u0010\u0010\u001a\u00020\rX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b/\u0010(\"\u0004\b0\u0010*R\u001a\u0010\u0011\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b1\u0010\u0015\"\u0004\b2\u0010\u0017¨\u0006M"}, d2 = {"LHPRTAndroidSDK/bean/D31SConfig;", "", "blueName", "", "bluePassword", "isSimplePair", "", "charSet", "codePage", "autoLocate", "rePrint", "rePrintTip", "printDensity", "", "paperType", "heatValue", "printSpeed", "stopPrint", "<init>", "(Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;ZZZIIIILjava/lang/String;)V", "getBlueName", "()Ljava/lang/String;", "setBlueName", "(Ljava/lang/String;)V", "getBluePassword", "setBluePassword", "()Z", "setSimplePair", "(Z)V", "getCharSet", "setCharSet", "getCodePage", "setCodePage", "getAutoLocate", "setAutoLocate", "getRePrint", "setRePrint", "getRePrintTip", "setRePrintTip", "getPrintDensity", "()I", "setPrintDensity", "(I)V", "getPaperType", "setPaperType", "getHeatValue", "setHeatValue", "getPrintSpeed", "setPrintSpeed", "getStopPrint", "setStopPrint", "getValue", "bytes", "", "pos", "setValue", "", "i", TypedValues.Custom.S_STRING, "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "component10", "component11", "component12", "component13", TemplateOperation.COPY, "equals", DispatchConstants.OTHER, "hashCode", "toString", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final /* data */ class D31SConfig {
    private boolean autoLocate;
    private String blueName;
    private String bluePassword;
    private String charSet;
    private String codePage;
    private int heatValue;
    private boolean isSimplePair;
    private int paperType;
    private int printDensity;
    private int printSpeed;
    private boolean rePrint;
    private boolean rePrintTip;
    private String stopPrint;

    public D31SConfig() {
        this(null, null, false, null, null, false, false, false, 0, 0, 0, 0, null, 8191, null);
    }

    public static /* synthetic */ D31SConfig copy$default(D31SConfig d31SConfig, String str, String str2, boolean z, String str3, String str4, boolean z2, boolean z3, boolean z4, int i, int i2, int i3, int i4, String str5, int i5, Object obj) {
        if ((i5 & 1) != 0) {
            str = d31SConfig.blueName;
        }
        return d31SConfig.copy(str, (i5 & 2) != 0 ? d31SConfig.bluePassword : str2, (i5 & 4) != 0 ? d31SConfig.isSimplePair : z, (i5 & 8) != 0 ? d31SConfig.charSet : str3, (i5 & 16) != 0 ? d31SConfig.codePage : str4, (i5 & 32) != 0 ? d31SConfig.autoLocate : z2, (i5 & 64) != 0 ? d31SConfig.rePrint : z3, (i5 & 128) != 0 ? d31SConfig.rePrintTip : z4, (i5 & 256) != 0 ? d31SConfig.printDensity : i, (i5 & 512) != 0 ? d31SConfig.paperType : i2, (i5 & 1024) != 0 ? d31SConfig.heatValue : i3, (i5 & 2048) != 0 ? d31SConfig.printSpeed : i4, (i5 & 4096) != 0 ? d31SConfig.stopPrint : str5);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final String getBlueName() {
        return this.blueName;
    }

    /* JADX INFO: renamed from: component10, reason: from getter */
    public final int getPaperType() {
        return this.paperType;
    }

    /* JADX INFO: renamed from: component11, reason: from getter */
    public final int getHeatValue() {
        return this.heatValue;
    }

    /* JADX INFO: renamed from: component12, reason: from getter */
    public final int getPrintSpeed() {
        return this.printSpeed;
    }

    /* JADX INFO: renamed from: component13, reason: from getter */
    public final String getStopPrint() {
        return this.stopPrint;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final String getBluePassword() {
        return this.bluePassword;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final boolean getIsSimplePair() {
        return this.isSimplePair;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final String getCharSet() {
        return this.charSet;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final String getCodePage() {
        return this.codePage;
    }

    /* JADX INFO: renamed from: component6, reason: from getter */
    public final boolean getAutoLocate() {
        return this.autoLocate;
    }

    /* JADX INFO: renamed from: component7, reason: from getter */
    public final boolean getRePrint() {
        return this.rePrint;
    }

    /* JADX INFO: renamed from: component8, reason: from getter */
    public final boolean getRePrintTip() {
        return this.rePrintTip;
    }

    /* JADX INFO: renamed from: component9, reason: from getter */
    public final int getPrintDensity() {
        return this.printDensity;
    }

    public final D31SConfig copy(String blueName, String bluePassword, boolean isSimplePair, String charSet, String codePage, boolean autoLocate, boolean rePrint, boolean rePrintTip, int printDensity, int paperType, int heatValue, int printSpeed, String stopPrint) {
        Intrinsics.checkNotNullParameter(blueName, "blueName");
        Intrinsics.checkNotNullParameter(bluePassword, "bluePassword");
        Intrinsics.checkNotNullParameter(charSet, "charSet");
        Intrinsics.checkNotNullParameter(codePage, "codePage");
        Intrinsics.checkNotNullParameter(stopPrint, "stopPrint");
        return new D31SConfig(blueName, bluePassword, isSimplePair, charSet, codePage, autoLocate, rePrint, rePrintTip, printDensity, paperType, heatValue, printSpeed, stopPrint);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof D31SConfig)) {
            return false;
        }
        D31SConfig d31SConfig = (D31SConfig) other;
        return Intrinsics.areEqual(this.blueName, d31SConfig.blueName) && Intrinsics.areEqual(this.bluePassword, d31SConfig.bluePassword) && this.isSimplePair == d31SConfig.isSimplePair && Intrinsics.areEqual(this.charSet, d31SConfig.charSet) && Intrinsics.areEqual(this.codePage, d31SConfig.codePage) && this.autoLocate == d31SConfig.autoLocate && this.rePrint == d31SConfig.rePrint && this.rePrintTip == d31SConfig.rePrintTip && this.printDensity == d31SConfig.printDensity && this.paperType == d31SConfig.paperType && this.heatValue == d31SConfig.heatValue && this.printSpeed == d31SConfig.printSpeed && Intrinsics.areEqual(this.stopPrint, d31SConfig.stopPrint);
    }

    public int hashCode() {
        return (((((((((((((((((((((((this.blueName.hashCode() * 31) + this.bluePassword.hashCode()) * 31) + D31SConfig$$ExternalSyntheticBackport0.m(this.isSimplePair)) * 31) + this.charSet.hashCode()) * 31) + this.codePage.hashCode()) * 31) + D31SConfig$$ExternalSyntheticBackport0.m(this.autoLocate)) * 31) + D31SConfig$$ExternalSyntheticBackport0.m(this.rePrint)) * 31) + D31SConfig$$ExternalSyntheticBackport0.m(this.rePrintTip)) * 31) + this.printDensity) * 31) + this.paperType) * 31) + this.heatValue) * 31) + this.printSpeed) * 31) + this.stopPrint.hashCode();
    }

    public String toString() {
        return "D31SConfig(blueName=" + this.blueName + ", bluePassword=" + this.bluePassword + ", isSimplePair=" + this.isSimplePair + ", charSet=" + this.charSet + ", codePage=" + this.codePage + ", autoLocate=" + this.autoLocate + ", rePrint=" + this.rePrint + ", rePrintTip=" + this.rePrintTip + ", printDensity=" + this.printDensity + ", paperType=" + this.paperType + ", heatValue=" + this.heatValue + ", printSpeed=" + this.printSpeed + ", stopPrint=" + this.stopPrint + Operators.BRACKET_END_STR;
    }

    public D31SConfig(String blueName, String bluePassword, boolean z, String charSet, String codePage, boolean z2, boolean z3, boolean z4, int i, int i2, int i3, int i4, String stopPrint) {
        Intrinsics.checkNotNullParameter(blueName, "blueName");
        Intrinsics.checkNotNullParameter(bluePassword, "bluePassword");
        Intrinsics.checkNotNullParameter(charSet, "charSet");
        Intrinsics.checkNotNullParameter(codePage, "codePage");
        Intrinsics.checkNotNullParameter(stopPrint, "stopPrint");
        this.blueName = blueName;
        this.bluePassword = bluePassword;
        this.isSimplePair = z;
        this.charSet = charSet;
        this.codePage = codePage;
        this.autoLocate = z2;
        this.rePrint = z3;
        this.rePrintTip = z4;
        this.printDensity = i;
        this.paperType = i2;
        this.heatValue = i3;
        this.printSpeed = i4;
        this.stopPrint = stopPrint;
    }

    public /* synthetic */ D31SConfig(String str, String str2, boolean z, String str3, String str4, boolean z2, boolean z3, boolean z4, int i, int i2, int i3, int i4, String str5, int i5, DefaultConstructorMarker defaultConstructorMarker) {
        this((i5 & 1) != 0 ? "" : str, (i5 & 2) != 0 ? "" : str2, (i5 & 4) != 0 ? false : z, (i5 & 8) != 0 ? "U.S.A." : str3, (i5 & 16) != 0 ? "Default" : str4, (i5 & 32) != 0 ? false : z2, (i5 & 64) != 0 ? false : z3, (i5 & 128) != 0 ? false : z4, (i5 & 256) != 0 ? 0 : i, (i5 & 512) != 0 ? 0 : i2, (i5 & 1024) != 0 ? 0 : i3, (i5 & 2048) == 0 ? i4 : 0, (i5 & 4096) != 0 ? "" : str5);
    }

    public final String getBlueName() {
        return this.blueName;
    }

    public final void setBlueName(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.blueName = str;
    }

    public final String getBluePassword() {
        return this.bluePassword;
    }

    public final void setBluePassword(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.bluePassword = str;
    }

    public final boolean isSimplePair() {
        return this.isSimplePair;
    }

    public final void setSimplePair(boolean z) {
        this.isSimplePair = z;
    }

    public final String getCharSet() {
        return this.charSet;
    }

    public final void setCharSet(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.charSet = str;
    }

    public final String getCodePage() {
        return this.codePage;
    }

    public final void setCodePage(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.codePage = str;
    }

    public final boolean getAutoLocate() {
        return this.autoLocate;
    }

    public final void setAutoLocate(boolean z) {
        this.autoLocate = z;
    }

    public final boolean getRePrint() {
        return this.rePrint;
    }

    public final void setRePrint(boolean z) {
        this.rePrint = z;
    }

    public final boolean getRePrintTip() {
        return this.rePrintTip;
    }

    public final void setRePrintTip(boolean z) {
        this.rePrintTip = z;
    }

    public final int getPrintDensity() {
        return this.printDensity;
    }

    public final void setPrintDensity(int i) {
        this.printDensity = i;
    }

    public final int getPaperType() {
        return this.paperType;
    }

    public final void setPaperType(int i) {
        this.paperType = i;
    }

    public final int getHeatValue() {
        return this.heatValue;
    }

    public final void setHeatValue(int i) {
        this.heatValue = i;
    }

    public final int getPrintSpeed() {
        return this.printSpeed;
    }

    public final void setPrintSpeed(int i) {
        this.printSpeed = i;
    }

    public final String getStopPrint() {
        return this.stopPrint;
    }

    public final void setStopPrint(String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.stopPrint = str;
    }

    public final String getValue(byte[] bytes, int pos) {
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        if (pos == 20) {
            return this.bluePassword;
        }
        if (pos == 34) {
            return String.valueOf(this.printDensity);
        }
        if (pos == 83) {
            return this.autoLocate ? "1" : "0";
        }
        if (pos == 100) {
            return this.rePrint ? "1" : "0";
        }
        if (pos == 162) {
            return this.isSimplePair ? "1" : "0";
        }
        if (pos == 198) {
            return this.blueName;
        }
        if (pos == 113) {
            return this.rePrintTip ? "1" : "0";
        }
        if (pos != 114) {
            switch (pos) {
                case 56:
                    return this.codePage;
                case 57:
                    return this.charSet;
                case 58:
                    return String.valueOf(this.paperType);
                default:
                    switch (pos) {
                        case 130:
                        case 131:
                            return String.valueOf(this.heatValue);
                        case 132:
                            return String.valueOf(this.printSpeed);
                        default:
                            return "";
                    }
            }
        }
        return this.stopPrint;
    }

    public final void setValue(byte[] bytes, int i, String string) {
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        Intrinsics.checkNotNullParameter(string, "string");
        if (i == 34) {
            int i2 = Integer.parseInt(string);
            this.printDensity = i2;
            bytes[34] = (byte) i2;
            return;
        }
        if (i == 58) {
            int i3 = Integer.parseInt(string);
            this.paperType = i3;
            bytes[58] = (byte) i3;
            return;
        }
        if (i == 83) {
            byte bAreEqual = Intrinsics.areEqual(string, "1");
            this.autoLocate = bAreEqual;
            bytes[83] = bAreEqual;
            return;
        }
        if (i == 100) {
            byte bAreEqual2 = Intrinsics.areEqual(string, "1");
            this.rePrint = bAreEqual2;
            bytes[100] = bAreEqual2;
            return;
        }
        if (i == 113) {
            byte bAreEqual3 = Intrinsics.areEqual(string, "1");
            this.rePrintTip = bAreEqual3;
            bytes[113] = bAreEqual3;
            return;
        }
        if (i != 114) {
            switch (i) {
                case 130:
                case 131:
                    int i4 = Integer.parseInt(string);
                    this.heatValue = i4;
                    bytes[130] = (byte) i4;
                    if (i4 < 0) {
                        bytes[131] = -1;
                    } else {
                        bytes[131] = 0;
                    }
                    break;
                case 132:
                    int i5 = Integer.parseInt(string);
                    this.printSpeed = i5;
                    bytes[132] = (byte) i5;
                    break;
            }
            return;
        }
        this.stopPrint = string;
        bytes[114] = Byte.parseByte(string);
    }
}
