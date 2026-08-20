package HPRTAndroidSDK;

import HPRTAndroidSDK.HPRTPrinterHelper;
import com.zhihu.matisse.internal.loader.AlbumLoader;
import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;

/* JADX INFO: compiled from: HPRTPrinterHelper.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(k = 3, mv = {2, 2, 0}, xi = 48)
@DebugMetadata(c = "HPRTAndroidSDK.HPRTPrinterHelper$Companion", f = "HPRTPrinterHelper.kt", i = {0, 1, 1, 1}, l = {1786, 1795}, m = "getMTPrinterStatusNotFilter", n = {AlbumLoader.COLUMN_COUNT, "bytes", AlbumLoader.COLUMN_COUNT, "l"}, s = {"I$0", "L$0", "I$0", "J$0"})
final class HPRTPrinterHelper$Companion$getMTPrinterStatusNotFilter$1 extends ContinuationImpl {
    int I$0;
    long J$0;
    Object L$0;
    int label;
    /* synthetic */ Object result;
    final /* synthetic */ HPRTPrinterHelper.Companion this$0;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    HPRTPrinterHelper$Companion$getMTPrinterStatusNotFilter$1(HPRTPrinterHelper.Companion companion, Continuation<? super HPRTPrinterHelper$Companion$getMTPrinterStatusNotFilter$1> continuation) {
        super(continuation);
        this.this$0 = companion;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Object invokeSuspend(Object obj) {
        this.result = obj;
        this.label |= Integer.MIN_VALUE;
        return this.this$0.getMTPrinterStatusNotFilter(0, this);
    }
}
