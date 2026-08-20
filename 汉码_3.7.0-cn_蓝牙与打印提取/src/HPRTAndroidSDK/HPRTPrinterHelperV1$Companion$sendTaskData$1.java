package HPRTAndroidSDK;

import java.util.concurrent.ExecutorService;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import utils.BaseProtocolUtils;
import utils.HPCode;
import utils.ThreadExecutors;

/* JADX INFO: compiled from: HPRTPrinterHelperV1.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\u0019\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\u0018\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005H\u0016¨\u0006\u0007"}, d2 = {"HPRTAndroidSDK/HPRTPrinterHelperV1$Companion$sendTaskData$1", "LHPRTAndroidSDK/PackLengthListener;", "getLen", "", "code", "", "len", "Hmark_cnRelease"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class HPRTPrinterHelperV1$Companion$sendTaskData$1 implements PackLengthListener {
    final /* synthetic */ byte[] $taskData;
    final /* synthetic */ int $taskID;
    final /* synthetic */ int $width;

    HPRTPrinterHelperV1$Companion$sendTaskData$1(int i, byte[] bArr, int i2) {
        this.$width = i;
        this.$taskData = bArr;
        this.$taskID = i2;
    }

    @Override // HPRTAndroidSDK.PackLengthListener
    public void getLen(int code, final int len) {
        if (code == HPCode.INSTANCE.getHP_SEND_SUCCEED()) {
            ExecutorService sendThread = ThreadExecutors.INSTANCE.getSendThread();
            final int i = this.$width;
            final byte[] bArr = this.$taskData;
            final int i2 = this.$taskID;
            sendThread.submit(new Runnable() { // from class: HPRTAndroidSDK.HPRTPrinterHelperV1$Companion$sendTaskData$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    HPRTPrinterHelperV1$Companion$sendTaskData$1.getLen$lambda$1(i, len, bArr, i2);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void getLen$lambda$1(int i, int i2, byte[] bArr, int i3) {
        int i4 = ((i + 7) / 8) * 2;
        int i5 = 0;
        for (Object obj : HPRTPrinterHelperV1.INSTANCE.fileToArrayList(bArr, (i2 / i4) * i4)) {
            int i6 = i5 + 1;
            if (i5 < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            byte[] bArr2 = (byte[]) obj;
            IPort printer = HPRTPrinterHelper.INSTANCE.getPrinter();
            if (printer != null) {
                printer.WriteData(BaseProtocolUtils.INSTANCE.getPrintByteArray(i3, bArr2.length / i4, bArr2));
            }
            i5 = i6;
        }
        HPRTPrinterHelperV1.INSTANCE.sendTaskEnd(String.valueOf(i3), new SetResultListener() { // from class: HPRTAndroidSDK.HPRTPrinterHelperV1$Companion$sendTaskData$1$getLen$1$2
            @Override // HPRTAndroidSDK.SetResultListener
            public void onResult(int code) {
            }
        });
    }
}
