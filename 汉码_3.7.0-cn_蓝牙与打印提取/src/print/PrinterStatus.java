package com.prt.print.data.bean;

import HPRTAndroidSDK.bean.D31SConfig$$ExternalSyntheticBackport0;
import anet.channel.strategy.dispatch.DispatchConstants;
import com.prt.print.utils.TemplateOperation;
import com.prt.provider.common.App;
import com.taobao.weex.el.parse.Operators;
import hprt.com.hmark.release.R;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: PrinterStatus.kt */
/* JADX INFO: loaded from: classes6.dex */
@Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\"\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\n\b\u0086\b\u0018\u00002\u00020\u0001BW\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\u0003\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b¢\u0006\u0004\b\f\u0010\rB\u0011\b\u0016\u0012\u0006\u0010\n\u001a\u00020\u000b¢\u0006\u0004\b\f\u0010\u000eJ\u0006\u0010\"\u001a\u00020\u0003J\u0006\u0010#\u001a\u00020\u0003J\u0006\u0010$\u001a\u00020\u0003J\u0006\u0010%\u001a\u00020\u0003J\u0006\u0010&\u001a\u00020\u0003J\u0006\u0010'\u001a\u00020\u0003J\u0006\u0010(\u001a\u00020\u0003J\u0006\u0010)\u001a\u00020\u0003J\u0013\u0010*\u001a\u00020\u00032\b\u0010+\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\b\u0010,\u001a\u00020\u000bH\u0016J\b\u0010-\u001a\u00020.H\u0016J\f\u0010/\u001a\b\u0012\u0004\u0012\u00020100J\t\u00102\u001a\u00020\u0003HÆ\u0003J\t\u00103\u001a\u00020\u0003HÆ\u0003J\t\u00104\u001a\u00020\u0003HÆ\u0003J\t\u00105\u001a\u00020\u0003HÆ\u0003J\t\u00106\u001a\u00020\u0003HÆ\u0003J\t\u00107\u001a\u00020\u0003HÆ\u0003J\t\u00108\u001a\u00020\u0003HÆ\u0003J\t\u00109\u001a\u00020\u000bHÆ\u0003JY\u0010:\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u00032\b\b\u0002\u0010\n\u001a\u00020\u000bHÆ\u0001R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0010\"\u0004\b\u0014\u0010\u0012R\u001a\u0010\u0005\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0010\"\u0004\b\u0016\u0010\u0012R\u001a\u0010\u0006\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\u0010\"\u0004\b\u0018\u0010\u0012R\u001a\u0010\u0007\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u0010\"\u0004\b\u001a\u0010\u0012R\u001a\u0010\b\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u0010\"\u0004\b\u001c\u0010\u0012R\u001a\u0010\t\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001d\u0010\u0010\"\u0004\b\u001e\u0010\u0012R\u001a\u0010\n\u001a\u00020\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010 \"\u0004\b!\u0010\u000e¨\u0006;"}, d2 = {"Lcom/prt/print/data/bean/PrinterStatus;", "", "noPaper", "", "heightTemp", "lowTemp", "lowBattery", "openCover", "lowVoltage", "locateFail", "value", "", "<init>", "(ZZZZZZZI)V", "(I)V", "getNoPaper", "()Z", "setNoPaper", "(Z)V", "getHeightTemp", "setHeightTemp", "getLowTemp", "setLowTemp", "getLowBattery", "setLowBattery", "getOpenCover", "setOpenCover", "getLowVoltage", "setLowVoltage", "getLocateFail", "setLocateFail", "getValue", "()I", "setValue", "isNormal", "isNoPaper", "isHeightTemp", "isLowTemp", "isLowBattery", "isOpenCover", "isLowVoltage", "isLocateFail", "equals", DispatchConstants.OTHER, "hashCode", "toString", "", "getStatusItems", "", "Lcom/prt/print/data/bean/StatusItem;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", TemplateOperation.COPY, "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final /* data */ class PrinterStatus {
    private boolean heightTemp;
    private boolean locateFail;
    private boolean lowBattery;
    private boolean lowTemp;
    private boolean lowVoltage;
    private boolean noPaper;
    private boolean openCover;
    private int value;

    public PrinterStatus() {
        this(false, false, false, false, false, false, false, 0, 255, null);
    }

    public static /* synthetic */ PrinterStatus copy$default(PrinterStatus printerStatus, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7, int i, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            z = printerStatus.noPaper;
        }
        if ((i2 & 2) != 0) {
            z2 = printerStatus.heightTemp;
        }
        if ((i2 & 4) != 0) {
            z3 = printerStatus.lowTemp;
        }
        if ((i2 & 8) != 0) {
            z4 = printerStatus.lowBattery;
        }
        if ((i2 & 16) != 0) {
            z5 = printerStatus.openCover;
        }
        if ((i2 & 32) != 0) {
            z6 = printerStatus.lowVoltage;
        }
        if ((i2 & 64) != 0) {
            z7 = printerStatus.locateFail;
        }
        if ((i2 & 128) != 0) {
            i = printerStatus.value;
        }
        boolean z8 = z7;
        int i3 = i;
        boolean z9 = z5;
        boolean z10 = z6;
        return printerStatus.copy(z, z2, z3, z4, z9, z10, z8, i3);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final boolean getNoPaper() {
        return this.noPaper;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final boolean getHeightTemp() {
        return this.heightTemp;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final boolean getLowTemp() {
        return this.lowTemp;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final boolean getLowBattery() {
        return this.lowBattery;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final boolean getOpenCover() {
        return this.openCover;
    }

    /* JADX INFO: renamed from: component6, reason: from getter */
    public final boolean getLowVoltage() {
        return this.lowVoltage;
    }

    /* JADX INFO: renamed from: component7, reason: from getter */
    public final boolean getLocateFail() {
        return this.locateFail;
    }

    /* JADX INFO: renamed from: component8, reason: from getter */
    public final int getValue() {
        return this.value;
    }

    public final PrinterStatus copy(boolean noPaper, boolean heightTemp, boolean lowTemp, boolean lowBattery, boolean openCover, boolean lowVoltage, boolean locateFail, int value) {
        return new PrinterStatus(noPaper, heightTemp, lowTemp, lowBattery, openCover, lowVoltage, locateFail, value);
    }

    public PrinterStatus(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7, int i) {
        this.noPaper = z;
        this.heightTemp = z2;
        this.lowTemp = z3;
        this.lowBattery = z4;
        this.openCover = z5;
        this.lowVoltage = z6;
        this.locateFail = z7;
        this.value = i;
    }

    public /* synthetic */ PrinterStatus(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, boolean z7, int i, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? false : z, (i2 & 2) != 0 ? false : z2, (i2 & 4) != 0 ? false : z3, (i2 & 8) != 0 ? false : z4, (i2 & 16) != 0 ? false : z5, (i2 & 32) != 0 ? false : z6, (i2 & 64) != 0 ? false : z7, (i2 & 128) != 0 ? 0 : i);
    }

    public final boolean getNoPaper() {
        return this.noPaper;
    }

    public final void setNoPaper(boolean z) {
        this.noPaper = z;
    }

    public final boolean getHeightTemp() {
        return this.heightTemp;
    }

    public final void setHeightTemp(boolean z) {
        this.heightTemp = z;
    }

    public final boolean getLowTemp() {
        return this.lowTemp;
    }

    public final void setLowTemp(boolean z) {
        this.lowTemp = z;
    }

    public final boolean getLowBattery() {
        return this.lowBattery;
    }

    public final void setLowBattery(boolean z) {
        this.lowBattery = z;
    }

    public final boolean getOpenCover() {
        return this.openCover;
    }

    public final void setOpenCover(boolean z) {
        this.openCover = z;
    }

    public final boolean getLowVoltage() {
        return this.lowVoltage;
    }

    public final void setLowVoltage(boolean z) {
        this.lowVoltage = z;
    }

    public final boolean getLocateFail() {
        return this.locateFail;
    }

    public final void setLocateFail(boolean z) {
        this.locateFail = z;
    }

    public final int getValue() {
        return this.value;
    }

    public final void setValue(int i) {
        this.value = i;
    }

    public PrinterStatus(int i) {
        this(false, false, false, false, false, false, false, 0, 255, null);
        this.value = i;
        this.noPaper = (i & 1) == 1;
        this.heightTemp = (i & 2) == 2;
        this.lowTemp = (i & 4) == 4;
        this.lowBattery = (i & 8) == 8;
        this.openCover = (i & 16) == 16;
        this.lowVoltage = (i & 32) == 32;
        this.locateFail = (i & 64) == 64;
    }

    public final boolean isNormal() {
        return (this.noPaper || this.heightTemp || this.lowTemp || this.lowBattery || this.openCover || this.lowVoltage || this.locateFail) ? false : true;
    }

    public final boolean isNoPaper() {
        return this.noPaper;
    }

    public final boolean isHeightTemp() {
        return this.heightTemp;
    }

    public final boolean isLowTemp() {
        return this.lowTemp;
    }

    public final boolean isLowBattery() {
        return this.lowBattery;
    }

    public final boolean isOpenCover() {
        return this.openCover;
    }

    public final boolean isLowVoltage() {
        return this.lowVoltage;
    }

    public final boolean isLocateFail() {
        return this.locateFail;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof PrinterStatus)) {
            return false;
        }
        PrinterStatus printerStatus = (PrinterStatus) other;
        return this.noPaper == printerStatus.noPaper && this.heightTemp == printerStatus.heightTemp && this.lowTemp == printerStatus.lowTemp && this.lowBattery == printerStatus.lowBattery && this.openCover == printerStatus.openCover && this.lowVoltage == printerStatus.lowVoltage && this.locateFail == printerStatus.locateFail;
    }

    public int hashCode() {
        return (((((((((((D31SConfig$$ExternalSyntheticBackport0.m(this.noPaper) * 31) + D31SConfig$$ExternalSyntheticBackport0.m(this.heightTemp)) * 31) + D31SConfig$$ExternalSyntheticBackport0.m(this.lowTemp)) * 31) + D31SConfig$$ExternalSyntheticBackport0.m(this.lowBattery)) * 31) + D31SConfig$$ExternalSyntheticBackport0.m(this.openCover)) * 31) + D31SConfig$$ExternalSyntheticBackport0.m(this.lowVoltage)) * 31) + D31SConfig$$ExternalSyntheticBackport0.m(this.locateFail);
    }

    public String toString() {
        return "PrinterStatus(noPaper=" + this.noPaper + ", heightTemp=" + this.heightTemp + ", lowTemp=" + this.lowTemp + ", lowBattery=" + this.lowBattery + ", openCover=" + this.openCover + ", lowVoltage=" + this.lowVoltage + ", locateFail=" + this.locateFail + Operators.BRACKET_END_STR;
    }

    public final List<StatusItem> getStatusItems() {
        ArrayList arrayList = new ArrayList();
        if (!isNormal()) {
            if (isNoPaper()) {
                String string = App.INSTANCE.getCONTEXT().getString(R.string.base_print_no_paper);
                Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
                StatusItem statusItem = new StatusItem(string, R.string.base_print_tip_no_paper_tip, R.string.base_please_go_to_store, 1, R.drawable.base_svg_no_paper);
                if (!arrayList.contains(statusItem)) {
                    arrayList.add(statusItem);
                }
            }
            if (isOpenCover()) {
                String string2 = App.INSTANCE.getCONTEXT().getString(R.string.base_print_open_cover);
                Intrinsics.checkNotNullExpressionValue(string2, "getString(...)");
                StatusItem statusItem2 = new StatusItem(string2, R.string.base_print_open_cover_tip, -1, 3, R.drawable.base_svg_open_cover);
                if (!arrayList.contains(statusItem2)) {
                    arrayList.add(statusItem2);
                }
            }
            if (isHeightTemp()) {
                String string3 = App.INSTANCE.getCONTEXT().getString(R.string.base_print_height_temp);
                Intrinsics.checkNotNullExpressionValue(string3, "getString(...)");
                StatusItem statusItem3 = new StatusItem(string3, R.string.base_print_height_temp_tip, -1, 4, R.drawable.base_svg_height_temp);
                if (!arrayList.contains(statusItem3)) {
                    arrayList.add(statusItem3);
                }
            }
            if (isLocateFail()) {
                String string4 = App.INSTANCE.getCONTEXT().getString(R.string.base_print_locate_fail);
                Intrinsics.checkNotNullExpressionValue(string4, "getString(...)");
                StatusItem statusItem4 = new StatusItem(string4, R.string.base_print_locate_fail_tip, -1, 5, R.drawable.base_svg_locate_fail);
                if (!arrayList.contains(statusItem4)) {
                    arrayList.add(statusItem4);
                }
            }
            if (isLowBattery()) {
                String string5 = App.INSTANCE.getCONTEXT().getString(R.string.base_print_low_battery);
                Intrinsics.checkNotNullExpressionValue(string5, "getString(...)");
                StatusItem statusItem5 = new StatusItem(string5, R.string.base_print_low_battery_tip, -1, 6, R.drawable.base_svg_low_battery);
                if (!arrayList.contains(statusItem5)) {
                    arrayList.add(statusItem5);
                }
            }
        }
        return arrayList;
    }
}
