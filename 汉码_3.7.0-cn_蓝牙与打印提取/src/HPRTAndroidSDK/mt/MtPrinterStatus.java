package HPRTAndroidSDK.mt;

import HPRTAndroidSDK.bean.D31SConfig$$ExternalSyntheticBackport0;
import anet.channel.strategy.dispatch.DispatchConstants;
import com.prt.print.utils.TemplateOperation;
import com.prt.provider.common.App;
import com.taobao.weex.el.parse.Operators;
import hprt.com.hmark.release.R;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: MtPrinterStatus.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0016\n\u0002\u0010\u000e\n\u0002\b\u001a\b\u0086\b\u0018\u00002\u00020\u0001BK\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\n¢\u0006\u0004\b\f\u0010\rJ\b\u0010.\u001a\u00020!H\u0016J\t\u0010/\u001a\u00020\u0003HÆ\u0003J\t\u00100\u001a\u00020\u0003HÆ\u0003J\t\u00101\u001a\u00020\u0003HÆ\u0003J\t\u00102\u001a\u00020\u0003HÆ\u0003J\t\u00103\u001a\u00020\u0003HÆ\u0003J\t\u00104\u001a\u00020\u0003HÆ\u0003J\t\u00105\u001a\u00020\nHÆ\u0003J\t\u00106\u001a\u00020\nHÆ\u0003JY\u00107\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\nHÆ\u0001J\u0013\u00108\u001a\u00020\n2\b\u00109\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010:\u001a\u00020\u0003HÖ\u0001R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u000f\"\u0004\b\u0013\u0010\u0011R\u001a\u0010\u0005\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u000f\"\u0004\b\u0015\u0010\u0011R\u001a\u0010\u0006\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u000f\"\u0004\b\u0017\u0010\u0011R\u001a\u0010\u0007\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u000f\"\u0004\b\u0019\u0010\u0011R\u001a\u0010\b\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u000f\"\u0004\b\u001b\u0010\u0011R\u001a\u0010\t\u001a\u00020\nX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\u001c\"\u0004\b\u001d\u0010\u001eR\u001a\u0010\u000b\u001a\u00020\nX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\u001c\"\u0004\b\u001f\u0010\u001eR\u0011\u0010 \u001a\u00020!8F¢\u0006\u0006\u001a\u0004\b\"\u0010#R\u0011\u0010$\u001a\u00020!8F¢\u0006\u0006\u001a\u0004\b%\u0010#R\u0011\u0010&\u001a\u00020\n8F¢\u0006\u0006\u001a\u0004\b&\u0010\u001cR\u0011\u0010'\u001a\u00020\n8F¢\u0006\u0006\u001a\u0004\b'\u0010\u001cR\u0011\u0010(\u001a\u00020\n8F¢\u0006\u0006\u001a\u0004\b(\u0010\u001cR\u0011\u0010)\u001a\u00020\n8F¢\u0006\u0006\u001a\u0004\b)\u0010\u001cR\u0011\u0010*\u001a\u00020\n8F¢\u0006\u0006\u001a\u0004\b*\u0010\u001cR\u0011\u0010+\u001a\u00020\n8F¢\u0006\u0006\u001a\u0004\b+\u0010\u001cR\u0011\u0010,\u001a\u00020\n8F¢\u0006\u0006\u001a\u0004\b,\u0010\u001cR\u0011\u0010-\u001a\u00020\n8F¢\u0006\u0006\u001a\u0004\b-\u0010\u001c¨\u0006;"}, d2 = {"LHPRTAndroidSDK/mt/MtPrinterStatus;", "", "state", "", "battery", "time", "density", "mode", "temperature", "isBuffIsNull", "", "isIdle", "<init>", "(IIIIIIZZ)V", "getState", "()I", "setState", "(I)V", "getBattery", "setBattery", "getTime", "setTime", "getDensity", "setDensity", "getMode", "setMode", "getTemperature", "setTemperature", "()Z", "setBuffIsNull", "(Z)V", "setIdle", "printerStatusInfo", "", "getPrinterStatusInfo", "()Ljava/lang/String;", "printerStatus", "getPrinterStatus", "isNormal", "isPaperLoss", "isPaperError", "isTempTooHigh", "isPowerLow", "isUncap", "isCarbonMiss", "isNoOfficialCarbon", "toString", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", TemplateOperation.COPY, "equals", DispatchConstants.OTHER, "hashCode", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final /* data */ class MtPrinterStatus {
    private int battery;
    private int density;
    private boolean isBuffIsNull;
    private boolean isIdle;
    private int mode;
    private int state;
    private int temperature;
    private int time;

    public static /* synthetic */ MtPrinterStatus copy$default(MtPrinterStatus mtPrinterStatus, int i, int i2, int i3, int i4, int i5, int i6, boolean z, boolean z2, int i7, Object obj) {
        if ((i7 & 1) != 0) {
            i = mtPrinterStatus.state;
        }
        if ((i7 & 2) != 0) {
            i2 = mtPrinterStatus.battery;
        }
        if ((i7 & 4) != 0) {
            i3 = mtPrinterStatus.time;
        }
        if ((i7 & 8) != 0) {
            i4 = mtPrinterStatus.density;
        }
        if ((i7 & 16) != 0) {
            i5 = mtPrinterStatus.mode;
        }
        if ((i7 & 32) != 0) {
            i6 = mtPrinterStatus.temperature;
        }
        if ((i7 & 64) != 0) {
            z = mtPrinterStatus.isBuffIsNull;
        }
        if ((i7 & 128) != 0) {
            z2 = mtPrinterStatus.isIdle;
        }
        boolean z3 = z;
        boolean z4 = z2;
        int i8 = i5;
        int i9 = i6;
        return mtPrinterStatus.copy(i, i2, i3, i4, i8, i9, z3, z4);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final int getState() {
        return this.state;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final int getBattery() {
        return this.battery;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final int getTime() {
        return this.time;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final int getDensity() {
        return this.density;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final int getMode() {
        return this.mode;
    }

    /* JADX INFO: renamed from: component6, reason: from getter */
    public final int getTemperature() {
        return this.temperature;
    }

    /* JADX INFO: renamed from: component7, reason: from getter */
    public final boolean getIsBuffIsNull() {
        return this.isBuffIsNull;
    }

    /* JADX INFO: renamed from: component8, reason: from getter */
    public final boolean getIsIdle() {
        return this.isIdle;
    }

    public final MtPrinterStatus copy(int state, int battery, int time, int density, int mode, int temperature, boolean isBuffIsNull, boolean isIdle) {
        return new MtPrinterStatus(state, battery, time, density, mode, temperature, isBuffIsNull, isIdle);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MtPrinterStatus)) {
            return false;
        }
        MtPrinterStatus mtPrinterStatus = (MtPrinterStatus) other;
        return this.state == mtPrinterStatus.state && this.battery == mtPrinterStatus.battery && this.time == mtPrinterStatus.time && this.density == mtPrinterStatus.density && this.mode == mtPrinterStatus.mode && this.temperature == mtPrinterStatus.temperature && this.isBuffIsNull == mtPrinterStatus.isBuffIsNull && this.isIdle == mtPrinterStatus.isIdle;
    }

    public int hashCode() {
        return (((((((((((((this.state * 31) + this.battery) * 31) + this.time) * 31) + this.density) * 31) + this.mode) * 31) + this.temperature) * 31) + D31SConfig$$ExternalSyntheticBackport0.m(this.isBuffIsNull)) * 31) + D31SConfig$$ExternalSyntheticBackport0.m(this.isIdle);
    }

    public MtPrinterStatus(int i, int i2, int i3, int i4, int i5, int i6, boolean z, boolean z2) {
        this.state = i;
        this.battery = i2;
        this.time = i3;
        this.density = i4;
        this.mode = i5;
        this.temperature = i6;
        this.isBuffIsNull = z;
        this.isIdle = z2;
    }

    public /* synthetic */ MtPrinterStatus(int i, int i2, int i3, int i4, int i5, int i6, boolean z, boolean z2, int i7, DefaultConstructorMarker defaultConstructorMarker) {
        this(i, i2, i3, i4, i5, i6, (i7 & 64) != 0 ? false : z, (i7 & 128) != 0 ? false : z2);
    }

    public final int getState() {
        return this.state;
    }

    public final void setState(int i) {
        this.state = i;
    }

    public final int getBattery() {
        return this.battery;
    }

    public final void setBattery(int i) {
        this.battery = i;
    }

    public final int getTime() {
        return this.time;
    }

    public final void setTime(int i) {
        this.time = i;
    }

    public final int getDensity() {
        return this.density;
    }

    public final void setDensity(int i) {
        this.density = i;
    }

    public final int getMode() {
        return this.mode;
    }

    public final void setMode(int i) {
        this.mode = i;
    }

    public final int getTemperature() {
        return this.temperature;
    }

    public final void setTemperature(int i) {
        this.temperature = i;
    }

    public final boolean isBuffIsNull() {
        return this.isBuffIsNull;
    }

    public final void setBuffIsNull(boolean z) {
        this.isBuffIsNull = z;
    }

    public final boolean isIdle() {
        return this.isIdle;
    }

    public final void setIdle(boolean z) {
        this.isIdle = z;
    }

    public final String getPrinterStatusInfo() {
        switch (this.state) {
            case 1:
                String string = App.INSTANCE.getCONTEXT().getString(R.string.base_high_temperature);
                Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
                return string;
            case 2:
                String string2 = App.INSTANCE.getCONTEXT().getString(R.string.base_low_battery);
                Intrinsics.checkNotNullExpressionValue(string2, "getString(...)");
                return string2;
            case 3:
                String string3 = App.INSTANCE.getCONTEXT().getString(R.string.base_open_the_lid);
                Intrinsics.checkNotNullExpressionValue(string3, "getString(...)");
                return string3;
            case 4:
                String string4 = App.INSTANCE.getCONTEXT().getString(R.string.base_lack_of_carbon_ribbon);
                Intrinsics.checkNotNullExpressionValue(string4, "getString(...)");
                return string4;
            case 5:
                String string5 = App.INSTANCE.getCONTEXT().getString(R.string.base_unofficial_carbon_ribbon);
                Intrinsics.checkNotNullExpressionValue(string5, "getString(...)");
                return string5;
            case 6:
                String string6 = App.INSTANCE.getCONTEXT().getString(R.string.base_print_no_paper);
                Intrinsics.checkNotNullExpressionValue(string6, "getString(...)");
                return string6;
            case 7:
                String string7 = App.INSTANCE.getCONTEXT().getString(R.string.base_device_abnormality);
                Intrinsics.checkNotNullExpressionValue(string7, "getString(...)");
                return string7;
            case 8:
                String string8 = App.INSTANCE.getCONTEXT().getString(R.string.base_density_level_3);
                Intrinsics.checkNotNullExpressionValue(string8, "getString(...)");
                return string8;
            case 9:
                return "里程耗尽";
            default:
                String string9 = App.INSTANCE.getCONTEXT().getString(R.string.base_unknown_state);
                Intrinsics.checkNotNullExpressionValue(string9, "getString(...)");
                return string9;
        }
    }

    public final String getPrinterStatus() {
        switch (this.state) {
            case 1:
                String string = App.INSTANCE.getCONTEXT().getString(R.string.base_confirm_the_printer_is_high_temp);
                Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
                return string;
            case 2:
                String string2 = App.INSTANCE.getCONTEXT().getString(R.string.base_confirm_the_printer_is_preheated);
                Intrinsics.checkNotNullExpressionValue(string2, "getString(...)");
                return string2;
            case 3:
                String string3 = App.INSTANCE.getCONTEXT().getString(R.string.base_confirm_the_printer_has_an_open_cover);
                Intrinsics.checkNotNullExpressionValue(string3, "getString(...)");
                return string3;
            case 4:
                String string4 = App.INSTANCE.getCONTEXT().getString(R.string.base_confirm_the_printer_is_installed_with_carbon_ribbon);
                Intrinsics.checkNotNullExpressionValue(string4, "getString(...)");
                return string4;
            case 5:
                String string5 = App.INSTANCE.getCONTEXT().getString(R.string.base_confirm_the_printer_is_installed_with_office_ribbon);
                Intrinsics.checkNotNullExpressionValue(string5, "getString(...)");
                return string5;
            case 6:
                String string6 = App.INSTANCE.getCONTEXT().getString(R.string.base_confirm_the_paper_is_installed_properly);
                Intrinsics.checkNotNullExpressionValue(string6, "getString(...)");
                return string6;
            case 7:
                String string7 = App.INSTANCE.getCONTEXT().getString(R.string.base_confirm_the_paper_is_installed_properly);
                Intrinsics.checkNotNullExpressionValue(string7, "getString(...)");
                return string7;
            case 8:
                String string8 = App.INSTANCE.getCONTEXT().getString(R.string.base_the_printer_is_functioning_properly);
                Intrinsics.checkNotNullExpressionValue(string8, "getString(...)");
                return string8;
            case 9:
                return "授权的里程耗尽";
            default:
                return "";
        }
    }

    public final boolean isNormal() {
        return this.state == 8;
    }

    public final boolean isPaperLoss() {
        return this.state == 6;
    }

    public final boolean isPaperError() {
        return this.state == 7;
    }

    public final boolean isTempTooHigh() {
        return this.state == 1;
    }

    public final boolean isPowerLow() {
        return this.state == 2;
    }

    public final boolean isUncap() {
        return this.state == 3;
    }

    public final boolean isCarbonMiss() {
        return this.state == 4;
    }

    public final boolean isNoOfficialCarbon() {
        return this.state == 5;
    }

    public String toString() {
        return "MtPrinterStatus(state=" + this.state + ", battery=" + this.battery + ", time=" + this.time + ", density=" + this.density + ", mode=" + this.mode + ", temperature=" + this.temperature + ", 缓存为空=" + this.isBuffIsNull + ", 空闲=" + this.isIdle + Operators.BRACKET_END_STR;
    }
}
