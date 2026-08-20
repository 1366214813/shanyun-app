package HPRTAndroidSDK;

import HPRTAndroidSDK.HPRTPrinterHelperV1;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CancellableContinuationImpl;
import kotlinx.coroutines.CoroutineScope;

/* JADX INFO: compiled from: HPRTPrinterHelperV1.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\u0018\u0002\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001*\u00020\u0003H\n"}, d2 = {"<anonymous>", "LHPRTAndroidSDK/HPRTPrinterHelperV1$Companion$ResultData;", "", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 2, 0}, xi = 48)
@DebugMetadata(c = "HPRTAndroidSDK.HPRTPrinterHelperV1$Companion$getPackLength$1$result$1", f = "HPRTPrinterHelperV1.kt", i = {0}, l = {222}, m = "invokeSuspend", n = {"$i$f$suspendCancellableCoroutine"}, s = {"I$1"})
final class HPRTPrinterHelperV1$Companion$getPackLength$1$result$1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super HPRTPrinterHelperV1.Companion.ResultData<Integer>>, Object> {
    final /* synthetic */ int $id;
    int I$0;
    int I$1;
    int label;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    HPRTPrinterHelperV1$Companion$getPackLength$1$result$1(int i, Continuation<? super HPRTPrinterHelperV1$Companion$getPackLength$1$result$1> continuation) {
        super(2, continuation);
        this.$id = i;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        return new HPRTPrinterHelperV1$Companion$getPackLength$1$result$1(this.$id, continuation);
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(CoroutineScope coroutineScope, Continuation<? super HPRTPrinterHelperV1.Companion.ResultData<Integer>> continuation) {
        return ((HPRTPrinterHelperV1$Companion$getPackLength$1$result$1) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Object invokeSuspend(Object obj) {
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i = this.label;
        if (i != 0) {
            if (i != 1) {
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            ResultKt.throwOnFailure(obj);
            return obj;
        }
        ResultKt.throwOnFailure(obj);
        int i2 = this.$id;
        this.I$0 = i2;
        this.I$1 = 0;
        this.label = 1;
        HPRTPrinterHelperV1$Companion$getPackLength$1$result$1 hPRTPrinterHelperV1$Companion$getPackLength$1$result$1 = this;
        CancellableContinuationImpl cancellableContinuationImpl = new CancellableContinuationImpl(IntrinsicsKt.intercepted(hPRTPrinterHelperV1$Companion$getPackLength$1$result$1), 1);
        cancellableContinuationImpl.initCancellability();
        HPRTPrinterHelperV1.INSTANCE.getMap().put("pack_length_" + i2, cancellableContinuationImpl);
        Object result = cancellableContinuationImpl.getResult();
        if (result == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            DebugProbesKt.probeCoroutineSuspended(hPRTPrinterHelperV1$Companion$getPackLength$1$result$1);
        }
        return result == coroutine_suspended ? coroutine_suspended : result;
    }
}
