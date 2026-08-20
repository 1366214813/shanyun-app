package HPRTAndroidSDK;

import HPRTAndroidSDK.HPRTPrinterHelperV1;
import HPRTAndroidSDK.PackLengthListener;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.TimeoutKt;
import utils.BaseProtocolUtils;
import utils.HPCode;

/* JADX INFO: compiled from: HPRTPrinterHelperV1.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 2, 0}, xi = 48)
@DebugMetadata(c = "HPRTAndroidSDK.HPRTPrinterHelperV1$Companion$getPackLength$1", f = "HPRTPrinterHelperV1.kt", i = {0}, l = {181}, m = "invokeSuspend", n = {"id"}, s = {"I$0"})
final class HPRTPrinterHelperV1$Companion$getPackLength$1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
    final /* synthetic */ PackLengthListener $listener;
    int I$0;
    int label;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    HPRTPrinterHelperV1$Companion$getPackLength$1(PackLengthListener packLengthListener, Continuation<? super HPRTPrinterHelperV1$Companion$getPackLength$1> continuation) {
        super(2, continuation);
        this.$listener = packLengthListener;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        return new HPRTPrinterHelperV1$Companion$getPackLength$1(this.$listener, continuation);
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
        return ((HPRTPrinterHelperV1$Companion$getPackLength$1) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Object invokeSuspend(Object obj) {
        int i;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i2 = this.label;
        if (i2 == 0) {
            ResultKt.throwOnFailure(obj);
            int packNum = BaseProtocolUtils.INSTANCE.getPackNum();
            try {
                HPRTPrinterHelper.INSTANCE.WriteData(BaseProtocolUtils.getGetValKeyByteArray$default(BaseProtocolUtils.INSTANCE, "pack_length", packNum, 1, null, 8, null));
                this.I$0 = packNum;
                this.label = 1;
                obj = TimeoutKt.withTimeout(5000L, new HPRTPrinterHelperV1$Companion$getPackLength$1$result$1(packNum, null), this);
                if (obj == coroutine_suspended) {
                    return coroutine_suspended;
                }
                i = packNum;
            } catch (Exception unused) {
                i = packNum;
                PackLengthListener.CC.getLen$default(this.$listener, HPCode.INSTANCE.getHP_SEND_TIMEOUT(), 0, 2, null);
            }
        } else {
            if (i2 != 1) {
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            i = this.I$0;
            try {
                ResultKt.throwOnFailure(obj);
            } catch (Exception unused2) {
                PackLengthListener.CC.getLen$default(this.$listener, HPCode.INSTANCE.getHP_SEND_TIMEOUT(), 0, 2, null);
            }
        }
        HPRTPrinterHelperV1.Companion.ResultData resultData = (HPRTPrinterHelperV1.Companion.ResultData) obj;
        this.$listener.getLen(resultData.getCode(), ((Number) resultData.getResult()).intValue());
        HPRTPrinterHelperV1.INSTANCE.getMap().remove("pack_length_" + i);
        return Unit.INSTANCE;
    }
}
