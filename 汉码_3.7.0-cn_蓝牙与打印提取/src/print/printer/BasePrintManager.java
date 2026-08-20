package com.prt.print.utils.printer;

import HPRTAndroidSDK.HPRTConst;
import HPRTAndroidSDK.HPRTPrinterHelper;
import HPRTAndroidSDK.PrinterMapping;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.android.gms.common.internal.ServiceSpecificExtraArgs;
import com.prt.base.common.ConnectMethod;
import com.prt.base.common.DeviceInfo;
import com.prt.base.common.UserConstant;
import com.prt.base.utils.KLogger;
import com.prt.event.BuriedPointUtils;
import com.prt.print.data.bean.Param;
import com.prt.print.data.bean.PrinterBean;
import com.prt.print.event.PrinterUnSupportEvent;
import com.prt.print.ui.service.DeviceService;
import com.prt.print.utils.printer.IPrintManager;
import com.prt.provider.common.App;
import com.prt.provider.data.database.CloudDeviceInfo;
import com.prt.provider.data.database.LocalConnectDeviceInfo;
import com.taobao.weex.common.Constants;
import hmark.ui.activity.HomeActivity;
import hprt.com.hmark.release.R;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import org.apache.poi.ss.util.CellUtil;
import org.apache.xmlbeans.impl.common.NameUtil;
import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;
import org.litepal.Operator;

/* JADX INFO: compiled from: BasePrintManager.kt */
/* JADX INFO: loaded from: classes6.dex */
@Metadata(d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000e\b&\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0004J\u0010\u0010\n\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0004J\u0012\u0010\u000b\u001a\u0004\u0018\u00010\u00072\u0006\u0010\f\u001a\u00020\u0007H\u0002J\u0018\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\b\u001a\u00020\tH\u0002J\u0012\u0010\u0011\u001a\u00020\u00052\b\u0010\u0012\u001a\u0004\u0018\u00010\u0007H\u0002J\u0010\u0010\u0013\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0004J\u0018\u0010\u0014\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u0015\u001a\u00020\u0005H\u0016J\u0018\u0010\u0016\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u0017\u001a\u00020\u0005H\u0016J\u0012\u0010\u0018\u001a\u00020\u000e2\b\u0010\u0019\u001a\u0004\u0018\u00010\tH\u0002J\u0010\u0010\u001a\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\tH\u0016J\b\u0010\u001b\u001a\u00020\u0005H\u0016J\b\u0010\u001c\u001a\u00020\u0005H\u0016J\u001c\u0010\u001d\u001a\u00020\u000e2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\b\u0010 \u001a\u0004\u0018\u00010!H\u0016J\u0016\u0010\"\u001a\u00020#2\f\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00070%H\u0016J^\u0010&\u001a\u00020#2\u0006\u0010\b\u001a\u00020\t2\f\u0010'\u001a\b\u0012\u0004\u0012\u00020(0%2\u0006\u0010)\u001a\u00020#2\u0006\u0010*\u001a\u00020#2\u0006\u0010+\u001a\u00020#2\u0006\u0010,\u001a\u00020#2\u0006\u0010-\u001a\u00020#2\u0006\u0010.\u001a\u00020#2\u0006\u0010/\u001a\u00020\u00052\u0006\u00100\u001a\u00020\u0005H\u0016JL\u00101\u001a\u00020#2\b\u0010\b\u001a\u0004\u0018\u00010\t2\b\u00102\u001a\u0004\u0018\u00010(2\u0006\u0010-\u001a\u00020#2\u0006\u0010.\u001a\u00020#2\u0006\u0010/\u001a\u00020\u00052\u0006\u00103\u001a\u00020\u00052\u0006\u00100\u001a\u00020\u00052\u0006\u00104\u001a\u00020#H\u0016J\b\u0010\u0004\u001a\u00020\u000eH\u0016J\b\u00105\u001a\u00020\u000eH\u0002R\u0012\u0010\u0004\u001a\u00020\u00058\u0004@\u0004X\u0085\u000e¢\u0006\u0002\n\u0000¨\u00066"}, d2 = {"Lcom/prt/print/utils/printer/BasePrintManager;", "Lcom/prt/print/utils/printer/IPrintManager;", "<init>", "()V", "stopPrint", "", "getPrinterSN", "", UserConstant.ApiFun.FUN_PRINTER_MAPPING, "Lcom/prt/base/common/DeviceInfo;", "getPrinterNameByVendor", "matchPrinterNameByProtocol", "printerName", "updateTphConfig", "", "printerBean", "Lcom/prt/print/data/bean/PrinterBean;", "matchNoEncryptionPrinterByConfig", "bluetoothName", "getFirmwareVersionByVendor", "connectBluetooth", "save", "connectBluetoothNoFailEvent", "updateDeviceInfoByPrinter", "saveLocalDeviceInfo", "deviceKeep", "connectWifi", "isConnected", "disConnect", "updateFirmware", "firmwareFile", "Ljava/io/File;", ServiceSpecificExtraArgs.CastExtraArgs.LISTENER, "Lcom/prt/print/utils/printer/IPrintManager$FirmwareProgressListener;", "savePrinterSetting", "", "list", "", "printDoubleColorBitmap", "bitmaps", "Landroid/graphics/Bitmap;", Constants.Name.X, Constants.Name.Y, "type", "density", "perCount", CellUtil.ALIGNMENT, "isLabel", "keepPrint", "printBitmap", "bitmap", "containImg", "typeA4", "clearDocumentsLogs", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
public abstract class BasePrintManager implements IPrintManager {
    protected boolean stopPrint;

    @Override // com.prt.print.utils.printer.IPrintManager
    public int printBitmap(DeviceInfo deviceInfo, Bitmap bitmap, int perCount, int alignment, boolean isLabel, boolean containImg, boolean keepPrint, int typeA4) {
        return 0;
    }

    @Override // com.prt.print.utils.printer.IPrintManager
    public int printDoubleColorBitmap(DeviceInfo deviceInfo, List<Bitmap> bitmaps, int x, int y, int type, int density, int perCount, int alignment, boolean isLabel, boolean keepPrint) {
        Intrinsics.checkNotNullParameter(deviceInfo, "deviceInfo");
        Intrinsics.checkNotNullParameter(bitmaps, "bitmaps");
        return 0;
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    protected final String getPrinterSN(DeviceInfo deviceInfo) {
        Intrinsics.checkNotNullParameter(deviceInfo, "deviceInfo");
        if (deviceInfo.printerVendor == 1) {
            return HPRTPrinterHelper.INSTANCE.getStandardESCPrinterSN(3);
        }
        String newPrinterSN = HPRTPrinterHelper.INSTANCE.getNewPrinterSN(3, deviceInfo.isMTSeries() ? 1 : 0);
        if (TextUtils.isEmpty(newPrinterSN)) {
            return HPRTPrinterHelper.INSTANCE.getNewCPCLPrinterSN(3, deviceInfo.isMTSeries() ? 1 : 0);
        }
        return newPrinterSN;
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    protected final String getPrinterNameByVendor(DeviceInfo deviceInfo) {
        String strTrimStart;
        Intrinsics.checkNotNullParameter(deviceInfo, "deviceInfo");
        if (deviceInfo.printerVendor == 1) {
            strTrimStart = HPRTPrinterHelper.INSTANCE.getStandardESCPrinterName(3);
        } else {
            String strReplace = new Regex("[^A-Za-z0-9_\\s&^-]+").replace(HPRTPrinterHelper.INSTANCE.getPrintName(1, deviceInfo.isMTSeries() ? 1 : 0), "");
            int length = strReplace.length() - 1;
            int i = 0;
            boolean z = false;
            while (i <= length) {
                boolean z2 = Intrinsics.compare((int) strReplace.charAt(!z ? i : length), 32) <= 0;
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
            strTrimStart = StringsKt.trimStart(strReplace.subSequence(i, length + 1).toString(), NameUtil.USCORE);
        }
        deviceInfo.printerName = strTrimStart;
        String strMatchPrinterNameByProtocol = matchPrinterNameByProtocol(strTrimStart);
        return strMatchPrinterNameByProtocol == null ? strTrimStart : strMatchPrinterNameByProtocol;
    }

    private final String matchPrinterNameByProtocol(String printerName) {
        Iterator it2;
        if (printerName.length() == 0) {
            return null;
        }
        try {
            it2 = Operator.findAll(CloudDeviceInfo.class, new long[0]).iterator();
        } catch (Exception e) {
            String message = e.getMessage();
            if (message == null) {
                message = "";
            }
            KLogger.e("matchPrinterNameByProtocol", message);
        }
        while (it2.hasNext()) {
            PrinterBean printerBean = (PrinterBean) GsonUtils.fromJson(((CloudDeviceInfo) it2.next()).getJson(), PrinterBean.class);
            if (printerBean.matchProtocolName(printerName)) {
                return printerBean.getModelName();
            }
            return null;
        }
        return null;
    }

    private final void updateTphConfig(PrinterBean printerBean, DeviceInfo deviceInfo) {
        SPUtils sPUtils = SPUtils.getInstance();
        if (printerBean.getNeedModifySHEC()) {
            String tphModel = HPRTPrinterHelper.INSTANCE.getTphModel();
            if (tphModel == null) {
                tphModel = "";
            }
            sPUtils.put("tphModel", tphModel);
            sPUtils.put("tphWidthType445", "");
            KLogger.i("Lee", "tphModel: " + tphModel);
            return;
        }
        if (printerBean.getNeedModifyN31TPHAfter10028() && deviceInfo.isFirmwareVersionAtLeast("1.00.28")) {
            int tphWidthTypeByKey445 = HPRTPrinterHelper.INSTANCE.getTphWidthTypeByKey445();
            String strValueOf = tphWidthTypeByKey445 >= 0 ? String.valueOf(tphWidthTypeByKey445) : "";
            sPUtils.put("tphModel", "");
            sPUtils.put("tphWidthType445", strValueOf);
            KLogger.i("Lee", "tphWidthType445: " + strValueOf);
            return;
        }
        sPUtils.put("tphModel", "");
        sPUtils.put("tphWidthType445", "");
    }

    private final boolean matchNoEncryptionPrinterByConfig(String bluetoothName) {
        Iterator it2;
        String str = bluetoothName;
        if (str == null || str.length() == 0) {
            return false;
        }
        try {
            long jCurrentTimeMillis = System.currentTimeMillis();
            List listFindAll = Operator.findAll(CloudDeviceInfo.class, new long[0]);
            KLogger.i("Lee", "findAll CloudDeviceInfo cost: " + (System.currentTimeMillis() - jCurrentTimeMillis) + "ms, count: " + listFindAll.size());
            it2 = listFindAll.iterator();
        } catch (Exception e) {
            String message = e.getMessage();
            if (message == null) {
                message = "";
            }
            KLogger.e("matchNoEncryptionPrinterByConfig", message);
        }
        while (it2.hasNext()) {
            PrinterBean printerBean = (PrinterBean) GsonUtils.fromJson(((CloudDeviceInfo) it2.next()).getJson(), PrinterBean.class);
            if (printerBean.matchBluetoothName(bluetoothName) && printerBean.isNoEncryption()) {
                KLogger.i("Lee", "Matched no-encryption printer by config: " + bluetoothName + " -> " + printerBean.getModelName());
                return true;
            }
            return DeviceInfo.INSTANCE.isSunmiPrinterByNameFallback(bluetoothName);
        }
        return DeviceInfo.INSTANCE.isSunmiPrinterByNameFallback(bluetoothName);
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x002d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    protected final String getFirmwareVersionByVendor(DeviceInfo deviceInfo) {
        String lowerCase;
        Intrinsics.checkNotNullParameter(deviceInfo, "deviceInfo");
        if (deviceInfo.printerVendor == 1) {
            return HPRTPrinterHelper.INSTANCE.getStandardESCPrinterVersion(3);
        }
        String str = deviceInfo.printerName;
        if (str != null) {
            Locale locale = Locale.getDefault();
            Intrinsics.checkNotNullExpressionValue(locale, "getDefault(...)");
            lowerCase = str.toLowerCase(locale);
            Intrinsics.checkNotNullExpressionValue(lowerCase, "toLowerCase(...)");
            if (lowerCase == null) {
                lowerCase = "";
            }
        }
        CloudDeviceInfo cloudDeviceInfo = (CloudDeviceInfo) LitePal.where("lower(modelName)=?", lowerCase).findFirst(CloudDeviceInfo.class);
        if (cloudDeviceInfo != null) {
            deviceInfo.instruct = ((PrinterBean) GsonUtils.fromJson(cloudDeviceInfo.getJson(), PrinterBean.class)).getInstruct();
        }
        String strReplace = new Regex("[^0-9.&^-]+").replace(HPRTPrinterHelper.INSTANCE.getVersion(1, deviceInfo.isMTSeries() ? 1 : Intrinsics.areEqual(deviceInfo.instruct, PrinterMapping.ESC_POLI) ? 2 : 0), "");
        int length = strReplace.length() - 1;
        int i = 0;
        boolean z = false;
        while (i <= length) {
            boolean z2 = Intrinsics.compare((int) strReplace.charAt(!z ? i : length), 32) <= 0;
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
        return strReplace.subSequence(i, length + 1).toString();
    }

    /* JADX WARN: Can't wrap try/catch for region: R(11:99|15|16|(2:98|18)(1:22)|23|101|(6:25|(8:31|32|(2:34|(1:36))|37|(1:39)(1:41)|42|(4:44|(5:46|(1:55)(4:50|51|52|(1:54))|56|(2:62|(1:64))|65)(2:66|(2:68|69))|70|(1:74))(2:75|(2:79|80))|81)(2:29|30)|92|93|(1:96)|97)(2:82|83)|102|84|(1:96)|97) */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x0291, code lost:
    
        r0 = e;
     */
    /* JADX WARN: Code restructure failed: missing block: B:87:0x0292, code lost:
    
        r14 = r13;
     */
    @Override // com.prt.print.utils.printer.IPrintManager
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean connectBluetooth(DeviceInfo deviceInfo, boolean save) {
        boolean z;
        boolean z2;
        int iPortOpen;
        LocalConnectDeviceInfo localConnectDeviceInfo;
        Intrinsics.checkNotNullParameter(deviceInfo, "deviceInfo");
        clearDocumentsLogs();
        boolean z3 = false;
        if (deviceInfo.printerVendor == 0 && matchNoEncryptionPrinterByConfig(deviceInfo.name)) {
            deviceInfo.printerVendor = 1;
            deviceInfo.encryption = 0;
            z = true;
            KLogger.i("Lee", "Auto detected no-encryption printer by config: " + deviceInfo.name);
        } else {
            z = true;
        }
        String str = deviceInfo.address;
        if (HPRTPrinterHelper.INSTANCE.IsOpened() && !TextUtils.isEmpty(deviceInfo.printerName) && !TextUtils.isEmpty(deviceInfo.instruct)) {
            return z;
        }
        try {
            DeviceService.INSTANCE.stopCheckStatus();
            HPRTPrinterHelper.INSTANCE.IsBLEType(false);
            if (deviceInfo.printerVendor == 1) {
                try {
                    HPRTConst.isShack = false;
                } catch (Exception e) {
                    e = e;
                }
            } else {
                HPRTConst.isShack = true;
            }
            iPortOpen = HPRTPrinterHelper.INSTANCE.PortOpen("Bluetooth," + str);
            KLogger.i("Lee", "connectBluetooth -> " + str + " connectResult: " + iPortOpen);
        } catch (Exception e2) {
            e = e2;
        }
        try {
        } catch (Exception e3) {
            e = e3;
            z3 = false;
        }
        if (iPortOpen == 0) {
            String printerNameByVendor = getPrinterNameByVendor(deviceInfo);
            if (TextUtils.isEmpty(printerNameByVendor) && !HPRTConst.isShack) {
                HPRTConst.isShack = true;
                return connectBluetooth(deviceInfo, save);
            }
            String firmwareVersionByVendor = getFirmwareVersionByVendor(deviceInfo);
            String printerSN = getPrinterSN(deviceInfo);
            if (TextUtils.isEmpty(printerNameByVendor)) {
                printerNameByVendor = getPrinterNameByVendor(deviceInfo);
                if (TextUtils.isEmpty(printerNameByVendor)) {
                    return false;
                }
            }
            deviceInfo.printerName = printerNameByVendor;
            deviceInfo.firmwareVersion = firmwareVersionByVendor;
            deviceInfo.sn = TextUtils.isEmpty(printerSN) ? deviceInfo.sn : printerSN;
            if (!TextUtils.isEmpty(printerNameByVendor)) {
                Locale locale = Locale.getDefault();
                Intrinsics.checkNotNullExpressionValue(locale, "getDefault(...)");
                String lowerCase = printerNameByVendor.toLowerCase(locale);
                Intrinsics.checkNotNullExpressionValue(lowerCase, "toLowerCase(...)");
                CloudDeviceInfo cloudDeviceInfo = (CloudDeviceInfo) LitePal.where("lower(modelName)=?", lowerCase).findFirst(CloudDeviceInfo.class);
                if (cloudDeviceInfo != null) {
                    PrinterBean printerBean = (PrinterBean) GsonUtils.fromJson(cloudDeviceInfo.getJson(), PrinterBean.class);
                    if (printerBean.getParams().isEmpty() && !TextUtils.isEmpty(printerBean.getReferToPrinter())) {
                        String referToPrinter = printerBean.getReferToPrinter();
                        Locale locale2 = Locale.getDefault();
                        Intrinsics.checkNotNullExpressionValue(locale2, "getDefault(...)");
                        String lowerCase2 = referToPrinter.toLowerCase(locale2);
                        Intrinsics.checkNotNullExpressionValue(lowerCase2, "toLowerCase(...)");
                        CloudDeviceInfo cloudDeviceInfo2 = (CloudDeviceInfo) LitePal.where("lower(modelName)=?", lowerCase2).findFirst(CloudDeviceInfo.class);
                        if (cloudDeviceInfo2 != null) {
                            printerBean.setParams(((PrinterBean) GsonUtils.fromJson(cloudDeviceInfo2.getJson(), PrinterBean.class)).getParams());
                        }
                    }
                    List<Param> otherParams = printerBean.getOtherParams();
                    if (otherParams != null && otherParams.isEmpty() && !TextUtils.isEmpty(printerBean.getReferToPrinter())) {
                        String referToPrinter2 = printerBean.getReferToPrinter();
                        Locale locale3 = Locale.getDefault();
                        Intrinsics.checkNotNullExpressionValue(locale3, "getDefault(...)");
                        String lowerCase3 = referToPrinter2.toLowerCase(locale3);
                        Intrinsics.checkNotNullExpressionValue(lowerCase3, "toLowerCase(...)");
                        CloudDeviceInfo cloudDeviceInfo3 = (CloudDeviceInfo) LitePal.where("lower(modelName)=?", lowerCase3).findFirst(CloudDeviceInfo.class);
                        if (cloudDeviceInfo3 != null) {
                            printerBean.setOtherParams(((PrinterBean) GsonUtils.fromJson(cloudDeviceInfo3.getJson(), PrinterBean.class)).getOtherParams());
                        }
                    }
                    deviceInfo.printerBean = printerBean;
                    Intrinsics.checkNotNull(printerBean);
                    updateTphConfig(printerBean, deviceInfo);
                    deviceInfo.updatePrintParams();
                    App.INSTANCE.updateDeviceInfoByPrinter(deviceInfo);
                    App.INSTANCE.refreshDeviceInfo();
                } else if (!App.INSTANCE.supportForExternalPrinters()) {
                    App.INSTANCE.showTip(R.string.print_app_un_support_this_printer, printerNameByVendor);
                    return false;
                }
                if (TextUtils.isEmpty(deviceInfo.distance) && (localConnectDeviceInfo = (LocalConnectDeviceInfo) LitePal.where("deviceAddress=?", deviceInfo.address).findFirst(LocalConnectDeviceInfo.class)) != null) {
                    deviceInfo.distance = localConnectDeviceInfo.getDistance();
                }
            } else if (!App.INSTANCE.supportForExternalPrinters() && !(ActivityUtils.getTopActivity() instanceof HomeActivity)) {
                App.INSTANCE.showTip(R.string.print_printer_name_is_null, null);
                return false;
            }
            KLogger.i("Lee", "printerName: " + printerNameByVendor);
            KLogger.i("Lee", "firmwareVersion: " + firmwareVersionByVendor);
            KLogger.i("Lee", "newPrinterSN: " + printerSN);
            KLogger.i("Lee", "speed: " + deviceInfo.speed);
            z2 = true;
            z3 = false;
            e.printStackTrace();
            String message = e.getMessage();
            Intrinsics.checkNotNull(message);
            Log.e("connectBluetooth", message);
            DeviceService.INSTANCE.startCheckStatus();
            z2 = z3;
            if (z2 && save) {
                saveLocalDeviceInfo(deviceInfo);
            }
            return z2;
        }
        EventBus.getDefault().post(new PrinterUnSupportEvent(iPortOpen));
        z2 = false;
        DeviceService.INSTANCE.startCheckStatus();
        if (z2) {
            saveLocalDeviceInfo(deviceInfo);
        }
        return z2;
    }

    /* JADX WARN: Removed duplicated region for block: B:98:0x027e  */
    @Override // com.prt.print.utils.printer.IPrintManager
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean connectBluetoothNoFailEvent(DeviceInfo deviceInfo, boolean updateDeviceInfoByPrinter) {
        boolean z;
        boolean z2;
        LocalConnectDeviceInfo localConnectDeviceInfo;
        Intrinsics.checkNotNullParameter(deviceInfo, "deviceInfo");
        clearDocumentsLogs();
        if (deviceInfo.printerVendor == 0 && matchNoEncryptionPrinterByConfig(deviceInfo.name)) {
            deviceInfo.printerVendor = 1;
            deviceInfo.encryption = 0;
            z = true;
            KLogger.i("Lee", "Auto detected no-encryption printer by config (NoFailEvent): " + deviceInfo.name);
        } else {
            z = true;
        }
        String str = deviceInfo.address;
        if (HPRTPrinterHelper.INSTANCE.IsOpened() && !TextUtils.isEmpty(deviceInfo.printerName) && !TextUtils.isEmpty(deviceInfo.instruct)) {
            return z;
        }
        try {
            DeviceService.INSTANCE.stopCheckStatus();
            HPRTPrinterHelper.INSTANCE.IsBLEType(false);
            if (deviceInfo.printerVendor == 1) {
                try {
                    HPRTConst.isShack = false;
                } catch (Exception e) {
                    e = e;
                    z2 = false;
                }
            } else {
                HPRTConst.isShack = true;
            }
            int iPortOpen = HPRTPrinterHelper.INSTANCE.PortOpen("Bluetooth," + str);
            KLogger.i("Lee", "connectBluetooth -> " + str + " connectResult: " + iPortOpen);
            if (iPortOpen != 0) {
                return false;
            }
            String printerNameByVendor = getPrinterNameByVendor(deviceInfo);
            if (TextUtils.isEmpty(printerNameByVendor) && !HPRTConst.isShack) {
                HPRTConst.isShack = true;
                return connectBluetoothNoFailEvent(deviceInfo, updateDeviceInfoByPrinter);
            }
            String firmwareVersionByVendor = getFirmwareVersionByVendor(deviceInfo);
            String printerSN = getPrinterSN(deviceInfo);
            if (TextUtils.isEmpty(printerNameByVendor)) {
                printerNameByVendor = getPrinterNameByVendor(deviceInfo);
                if (TextUtils.isEmpty(printerNameByVendor)) {
                    return false;
                }
            }
            deviceInfo.printerName = printerNameByVendor;
            deviceInfo.firmwareVersion = firmwareVersionByVendor;
            deviceInfo.sn = TextUtils.isEmpty(printerSN) ? deviceInfo.sn : printerSN;
            if (TextUtils.isEmpty(printerNameByVendor)) {
                return false;
            }
            Locale locale = Locale.getDefault();
            Intrinsics.checkNotNullExpressionValue(locale, "getDefault(...)");
            String lowerCase = printerNameByVendor.toLowerCase(locale);
            Intrinsics.checkNotNullExpressionValue(lowerCase, "toLowerCase(...)");
            CloudDeviceInfo cloudDeviceInfo = (CloudDeviceInfo) LitePal.where("lower(modelName)=?", lowerCase).findFirst(CloudDeviceInfo.class);
            if (cloudDeviceInfo != null) {
                PrinterBean printerBean = (PrinterBean) GsonUtils.fromJson(cloudDeviceInfo.getJson(), PrinterBean.class);
                if (printerBean.getParams().isEmpty() && !TextUtils.isEmpty(printerBean.getReferToPrinter())) {
                    String referToPrinter = printerBean.getReferToPrinter();
                    Locale locale2 = Locale.getDefault();
                    Intrinsics.checkNotNullExpressionValue(locale2, "getDefault(...)");
                    String lowerCase2 = referToPrinter.toLowerCase(locale2);
                    Intrinsics.checkNotNullExpressionValue(lowerCase2, "toLowerCase(...)");
                    CloudDeviceInfo cloudDeviceInfo2 = (CloudDeviceInfo) LitePal.where("lower(modelName)=?", lowerCase2).findFirst(CloudDeviceInfo.class);
                    if (cloudDeviceInfo2 != null) {
                        printerBean.setParams(((PrinterBean) GsonUtils.fromJson(cloudDeviceInfo2.getJson(), PrinterBean.class)).getParams());
                    }
                }
                List<Param> otherParams = printerBean.getOtherParams();
                if (otherParams != null && otherParams.isEmpty() && !TextUtils.isEmpty(printerBean.getReferToPrinter())) {
                    String referToPrinter2 = printerBean.getReferToPrinter();
                    Locale locale3 = Locale.getDefault();
                    Intrinsics.checkNotNullExpressionValue(locale3, "getDefault(...)");
                    String lowerCase3 = referToPrinter2.toLowerCase(locale3);
                    Intrinsics.checkNotNullExpressionValue(lowerCase3, "toLowerCase(...)");
                    CloudDeviceInfo cloudDeviceInfo3 = (CloudDeviceInfo) LitePal.where("lower(modelName)=?", lowerCase3).findFirst(CloudDeviceInfo.class);
                    if (cloudDeviceInfo3 != null) {
                        printerBean.setOtherParams(((PrinterBean) GsonUtils.fromJson(cloudDeviceInfo3.getJson(), PrinterBean.class)).getOtherParams());
                    }
                }
                deviceInfo.printerBean = printerBean;
                Intrinsics.checkNotNull(printerBean);
                updateTphConfig(printerBean, deviceInfo);
                deviceInfo.updatePrintParams();
                if (updateDeviceInfoByPrinter) {
                    App.INSTANCE.updateDeviceInfoByPrinter(deviceInfo);
                }
            }
            if (TextUtils.isEmpty(deviceInfo.distance) && (localConnectDeviceInfo = (LocalConnectDeviceInfo) LitePal.where("deviceAddress=?", deviceInfo.address).findFirst(LocalConnectDeviceInfo.class)) != null) {
                deviceInfo.distance = localConnectDeviceInfo.getDistance();
            }
            KLogger.i("Lee", "printerName: " + printerNameByVendor);
            KLogger.i("Lee", "firmwareVersion: " + firmwareVersionByVendor);
            KLogger.i("Lee", "newPrinterSN: " + printerSN);
            KLogger.i("Lee", "speed: " + deviceInfo.speed);
            try {
                BuriedPointUtils buriedPointUtils = BuriedPointUtils.INSTANCE;
                String str2 = deviceInfo.printerName;
                if (str2 == null) {
                    str2 = "";
                }
                try {
                    buriedPointUtils.connectData(str2, ConnectMethod.INSTANCE.getConnectMethod(deviceInfo.connectType), true, null);
                    DeviceService.INSTANCE.startCheckStatus();
                    if (updateDeviceInfoByPrinter) {
                        saveLocalDeviceInfo(deviceInfo);
                    }
                    return true;
                } catch (Exception e2) {
                    e = e2;
                    z2 = true;
                    if (AppUtils.isAppDebug()) {
                    }
                    DeviceService.INSTANCE.startCheckStatus();
                    return z2;
                }
            } catch (Exception e3) {
                e = e3;
            }
        } catch (Exception e4) {
            e = e4;
        }
        z2 = false;
        if (AppUtils.isAppDebug()) {
            e.fillInStackTrace();
            String message = e.getMessage();
            Intrinsics.checkNotNull(message);
            Log.e("connectBluetooth", message);
        }
        DeviceService.INSTANCE.startCheckStatus();
        return z2;
    }

    private final void saveLocalDeviceInfo(DeviceInfo deviceKeep) {
        if (deviceKeep == null) {
            return;
        }
        LocalConnectDeviceInfo localConnectDeviceInfo = new LocalConnectDeviceInfo(0, null, null, null, null, null, null, null, null, 0L, false, false, 0, null, 16383, null);
        LocalConnectDeviceInfo localConnectDeviceInfo2 = (LocalConnectDeviceInfo) LitePal.where("deviceAddress = ?", deviceKeep.address).findFirst(LocalConnectDeviceInfo.class);
        if (localConnectDeviceInfo2 == null || !localConnectDeviceInfo2.isEditName()) {
            localConnectDeviceInfo.setDeviceName(deviceKeep.name);
        } else {
            localConnectDeviceInfo.setEditName(true);
        }
        localConnectDeviceInfo.setDeviceAddress(deviceKeep.address);
        localConnectDeviceInfo.setConnectType(deviceKeep.connectType);
        if (deviceKeep.printerBean != null) {
            PrinterBean printerBean = deviceKeep.printerBean;
            Intrinsics.checkNotNull(printerBean);
            localConnectDeviceInfo.setDeviceIcon(printerBean.getPrinterPreview());
        }
        localConnectDeviceInfo.setDistance(deviceKeep.distance);
        localConnectDeviceInfo.setPrinterName(deviceKeep.printerName);
        localConnectDeviceInfo.setDeviceInfo(GsonUtils.toJson(deviceKeep));
        localConnectDeviceInfo.saveOrUpdate("deviceAddress = ?", deviceKeep.address);
    }

    @Override // com.prt.print.utils.printer.IPrintManager
    public boolean connectWifi(DeviceInfo deviceInfo) {
        String str;
        LocalConnectDeviceInfo localConnectDeviceInfo;
        Intrinsics.checkNotNullParameter(deviceInfo, "deviceInfo");
        clearDocumentsLogs();
        boolean z = true;
        if (HPRTPrinterHelper.INSTANCE.IsOpened() && !TextUtils.isEmpty(deviceInfo.printerName) && !TextUtils.isEmpty(deviceInfo.instruct)) {
            return true;
        }
        try {
            DeviceService.INSTANCE.stopCheckStatus();
            if (TextUtils.isEmpty(deviceInfo.port)) {
                str = "9100";
            } else {
                str = deviceInfo.port;
                Intrinsics.checkNotNull(str);
            }
            if (deviceInfo.printerVendor == 1) {
                HPRTConst.isShack = false;
            } else {
                HPRTConst.isShack = true;
            }
            if (HPRTPrinterHelper.INSTANCE.PortOpen("WiFi," + deviceInfo.address + "," + str) == 0) {
                String printerNameByVendor = getPrinterNameByVendor(deviceInfo);
                if (TextUtils.isEmpty(printerNameByVendor) && !HPRTConst.isShack) {
                    HPRTConst.isShack = true;
                    return connectWifi(deviceInfo);
                }
                String firmwareVersionByVendor = getFirmwareVersionByVendor(deviceInfo);
                String printerSN = getPrinterSN(deviceInfo);
                if (TextUtils.isEmpty(deviceInfo.name)) {
                    deviceInfo.name = printerNameByVendor;
                }
                deviceInfo.printerName = printerNameByVendor;
                deviceInfo.firmwareVersion = firmwareVersionByVendor;
                if (TextUtils.isEmpty(printerSN)) {
                    printerSN = deviceInfo.sn;
                }
                deviceInfo.sn = printerSN;
                if (!TextUtils.isEmpty(printerNameByVendor)) {
                    Locale locale = Locale.getDefault();
                    Intrinsics.checkNotNullExpressionValue(locale, "getDefault(...)");
                    String lowerCase = printerNameByVendor.toLowerCase(locale);
                    Intrinsics.checkNotNullExpressionValue(lowerCase, "toLowerCase(...)");
                    CloudDeviceInfo cloudDeviceInfo = (CloudDeviceInfo) LitePal.where("lower(modelName)=?", lowerCase).findFirst(CloudDeviceInfo.class);
                    if (cloudDeviceInfo != null) {
                        PrinterBean printerBean = (PrinterBean) GsonUtils.fromJson(cloudDeviceInfo.getJson(), PrinterBean.class);
                        if (printerBean.getParams().isEmpty() && !TextUtils.isEmpty(printerBean.getReferToPrinter())) {
                            String referToPrinter = printerBean.getReferToPrinter();
                            Locale locale2 = Locale.getDefault();
                            Intrinsics.checkNotNullExpressionValue(locale2, "getDefault(...)");
                            String lowerCase2 = referToPrinter.toLowerCase(locale2);
                            Intrinsics.checkNotNullExpressionValue(lowerCase2, "toLowerCase(...)");
                            CloudDeviceInfo cloudDeviceInfo2 = (CloudDeviceInfo) LitePal.where("lower(modelName)=?", lowerCase2).findFirst(CloudDeviceInfo.class);
                            if (cloudDeviceInfo2 != null) {
                                printerBean.setParams(((PrinterBean) GsonUtils.fromJson(cloudDeviceInfo2.getJson(), PrinterBean.class)).getParams());
                            }
                        }
                        List<Param> otherParams = printerBean.getOtherParams();
                        if (otherParams != null && otherParams.isEmpty() && !TextUtils.isEmpty(printerBean.getReferToPrinter())) {
                            String referToPrinter2 = printerBean.getReferToPrinter();
                            Locale locale3 = Locale.getDefault();
                            Intrinsics.checkNotNullExpressionValue(locale3, "getDefault(...)");
                            String lowerCase3 = referToPrinter2.toLowerCase(locale3);
                            Intrinsics.checkNotNullExpressionValue(lowerCase3, "toLowerCase(...)");
                            CloudDeviceInfo cloudDeviceInfo3 = (CloudDeviceInfo) LitePal.where("lower(modelName)=?", lowerCase3).findFirst(CloudDeviceInfo.class);
                            if (cloudDeviceInfo3 != null) {
                                printerBean.setOtherParams(((PrinterBean) GsonUtils.fromJson(cloudDeviceInfo3.getJson(), PrinterBean.class)).getOtherParams());
                            }
                        }
                        deviceInfo.printerBean = printerBean;
                        Intrinsics.checkNotNull(printerBean);
                        updateTphConfig(printerBean, deviceInfo);
                        deviceInfo.updatePrintParams();
                        App.INSTANCE.updateDeviceInfoByPrinter(deviceInfo);
                    } else if (!App.INSTANCE.supportForExternalPrinters()) {
                        App.INSTANCE.showTip(R.string.print_app_un_support_this_printer, printerNameByVendor);
                        return false;
                    }
                    if (TextUtils.isEmpty(deviceInfo.distance) && (localConnectDeviceInfo = (LocalConnectDeviceInfo) LitePal.where("deviceAddress=?", deviceInfo.address).findFirst(LocalConnectDeviceInfo.class)) != null) {
                        deviceInfo.distance = localConnectDeviceInfo.getDistance();
                    }
                } else if (!App.INSTANCE.supportForExternalPrinters() && !(ActivityUtils.getTopActivity() instanceof HomeActivity)) {
                    App.INSTANCE.showTip(R.string.print_printer_name_is_null, null);
                    return false;
                }
                try {
                    DeviceService.INSTANCE.startCheckStatus();
                } catch (Exception e) {
                    e = e;
                    DeviceService.INSTANCE.startCheckStatus();
                    String message = e.getMessage();
                    if (message == null) {
                        message = "";
                    }
                    Log.e("connectBluetooth", message);
                }
            } else {
                z = false;
            }
        } catch (Exception e2) {
            e = e2;
            z = false;
        }
        if (z) {
            saveLocalDeviceInfo(deviceInfo);
        }
        return z;
    }

    @Override // com.prt.print.utils.printer.IPrintManager
    public boolean isConnected() {
        return HPRTPrinterHelper.INSTANCE.IsOpened();
    }

    @Override // com.prt.print.utils.printer.IPrintManager
    public boolean disConnect() {
        if (!HPRTPrinterHelper.INSTANCE.IsOpened()) {
            return true;
        }
        try {
            return HPRTPrinterHelper.INSTANCE.PortClose();
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage();
            if (message == null) {
                message = "";
            }
            Log.e("connectBluetooth", message);
            return false;
        }
    }

    @Override // com.prt.print.utils.printer.IPrintManager
    public void updateFirmware(File firmwareFile, final IPrintManager.FirmwareProgressListener listener) {
        try {
            if (firmwareFile == null) {
                if (listener != null) {
                    listener.onFailure();
                }
            } else {
                int packageSize = HPRTPrinterHelper.INSTANCE.getPackageSize();
                FileInputStream fileInputStream = new FileInputStream(firmwareFile);
                try {
                    HPRTPrinterHelper.INSTANCE.sendUpdateToPrint(fileInputStream, new HPRTPrinterHelper.setOnProgress() { // from class: com.prt.print.utils.printer.BasePrintManager$updateFirmware$1$1
                        @Override // HPRTAndroidSDK.HPRTPrinterHelper.setOnProgress
                        public void onProgress(int progress) {
                            IPrintManager.FirmwareProgressListener firmwareProgressListener = listener;
                            if (firmwareProgressListener != null) {
                                firmwareProgressListener.onProgress(progress);
                                if (100 == progress) {
                                    this.disConnect();
                                    listener.onFinish();
                                }
                            }
                        }

                        @Override // HPRTAndroidSDK.HPRTPrinterHelper.setOnProgress
                        public void failure() {
                            KLogger.e("failure");
                            IPrintManager.FirmwareProgressListener firmwareProgressListener = listener;
                            if (firmwareProgressListener != null) {
                                firmwareProgressListener.onFailure();
                            }
                        }
                    }, packageSize);
                    Unit unit = Unit.INSTANCE;
                    CloseableKt.closeFinally(fileInputStream, null);
                } finally {
                }
            }
        } catch (Exception e) {
            KLogger.e(e.getMessage());
            if (listener != null) {
                listener.onFailure();
            }
        }
    }

    @Override // com.prt.print.utils.printer.IPrintManager
    public int savePrinterSetting(List<String> list) {
        Intrinsics.checkNotNullParameter(list, "list");
        return HPRTPrinterHelper.INSTANCE.savePrinterSetting(list);
    }

    @Override // com.prt.print.utils.printer.IPrintManager
    public void stopPrint() {
        this.stopPrint = true;
    }

    private final void clearDocumentsLogs() {
        String absolutePath;
        File[] fileArrListFiles;
        try {
            File externalFilesDir = App.INSTANCE.getCONTEXT().getExternalFilesDir("Documents");
            if (externalFilesDir == null || (absolutePath = externalFilesDir.getAbsolutePath()) == null) {
                absolutePath = "";
            }
            File file = new File(absolutePath);
            if (file.exists() && file.isDirectory() && (fileArrListFiles = file.listFiles()) != null) {
                for (File file2 : fileArrListFiles) {
                    if (file2.isFile()) {
                        file2.delete();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
