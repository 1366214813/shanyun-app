package com.prt.print.utils.printer;

import HPRTAndroidSDK.HPRTPrinterHelper;
import android.graphics.Bitmap;
import android.util.Log;
import com.hjq.toast.Toaster;
import com.lee.editorpanel.utils.ImageUtils;
import com.prt.base.common.DeviceInfo;
import com.prt.base.common.UserConstant;
import com.prt.base.coroutines.CoroutineExtKt;
import com.prt.print.ui.service.DeviceService;
import com.prt.provider.common.App;
import hprt.com.hmark.release.R;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import org.apache.poi.ss.util.CellUtil;
import utils.BmpUtil;

/* JADX INFO: compiled from: MT8900PrinterManager.kt */
/* JADX INFO: loaded from: classes6.dex */
@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003JL\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\b\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\n\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\r2\u0006\u0010\u000f\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020\u0005H\u0016¨\u0006\u0011"}, d2 = {"Lcom/prt/print/utils/printer/MT8900PrinterManager;", "Lcom/prt/print/utils/printer/ESCPrinterManager;", "<init>", "()V", "printBitmap", "", UserConstant.ApiFun.FUN_PRINTER_MAPPING, "Lcom/prt/base/common/DeviceInfo;", "bitmap", "Landroid/graphics/Bitmap;", "perCount", CellUtil.ALIGNMENT, "isLabel", "", "containImg", "keepPrint", "typeA4", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class MT8900PrinterManager extends ESCPrinterManager {
    @Override // com.prt.print.utils.printer.ESCPrinterManager, com.prt.print.utils.printer.BasePrintManager, com.prt.print.utils.printer.IPrintManager
    public int printBitmap(DeviceInfo deviceInfo, Bitmap bitmap, int perCount, int alignment, boolean isLabel, boolean containImg, boolean keepPrint, int typeA4) throws Exception {
        Bitmap bitmapScaleBitmap;
        if (deviceInfo == null || bitmap == null) {
            return -1;
        }
        DeviceService.INSTANCE.stopCheckStatus();
        int carbonSurplus = HPRTPrinterHelper.INSTANCE.getCarbonSurplus();
        DeviceService.INSTANCE.startCheckStatus();
        Log.e("carbonSurplus", String.valueOf(carbonSurplus));
        if (1 <= carbonSurplus && carbonSurplus < 297) {
            CoroutineExtKt.runMain(new Function0() { // from class: com.prt.print.utils.printer.MT8900PrinterManager$$ExternalSyntheticLambda0
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    return MT8900PrinterManager.printBitmap$lambda$0();
                }
            });
            return -1;
        }
        HPRTPrinterHelper.Companion.setPageMode$default(HPRTPrinterHelper.INSTANCE, 0, 1, null);
        if (typeA4 == 2) {
            bitmapScaleBitmap = new BmpUtil().setA5Bitmap(bitmap);
        } else {
            bitmapScaleBitmap = ImageUtils.scaleBitmap(bitmap, 2336, (bitmap.getHeight() * 2336) / bitmap.getWidth(), new boolean[0]);
        }
        Bitmap bitmap2 = bitmapScaleBitmap;
        if (bitmap2 == null) {
            return 0;
        }
        if (HPRTPrinterHelper.INSTANCE.printBitmap(bitmap2, bitmap2.getHeight(), typeA4, deviceInfo.compress != 2, true)) {
            return 0;
        }
        HPRTPrinterHelper.INSTANCE.clearCache();
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit printBitmap$lambda$0() {
        Toaster.showShort((CharSequence) App.INSTANCE.getCONTEXT().getString(R.string.base_ribbon_low));
        return Unit.INSTANCE;
    }
}
