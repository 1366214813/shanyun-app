package HPRTAndroidSDK;

import HPRTAndroidSDK.HPRTPrinterHelper;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.TimeoutKt;
import utils.BasePrinterConstant;
import utils.BaseProtocolUtils;
import utils.HPCode;

/* JADX INFO: compiled from: HPRTPrinterHelperV1.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 2, 0}, xi = 48)
@DebugMetadata(c = "HPRTAndroidSDK.HPRTPrinterHelperV1$Companion$sendTaskEnd$1", f = "HPRTPrinterHelperV1.kt", i = {0}, l = {156}, m = "invokeSuspend", n = {"pack"}, s = {"I$0"})
final class HPRTPrinterHelperV1$Companion$sendTaskEnd$1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
    final /* synthetic */ String $id;
    final /* synthetic */ SetResultListener $listener;
    int I$0;
    int label;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    HPRTPrinterHelperV1$Companion$sendTaskEnd$1(String str, SetResultListener setResultListener, Continuation<? super HPRTPrinterHelperV1$Companion$sendTaskEnd$1> continuation) {
        super(2, continuation);
        this.$id = str;
        this.$listener = setResultListener;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        return new HPRTPrinterHelperV1$Companion$sendTaskEnd$1(this.$id, this.$listener, continuation);
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
        return ((HPRTPrinterHelperV1$Companion$sendTaskEnd$1) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
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
                HPRTPrinterHelper.Companion companion = HPRTPrinterHelper.INSTANCE;
                BaseProtocolUtils baseProtocolUtils = BaseProtocolUtils.INSTANCE;
                byte[] bytes = this.$id.getBytes(Charsets.UTF_8);
                Intrinsics.checkNotNullExpressionValue(bytes, "getBytes(...)");
                companion.WriteData(BaseProtocolUtils.getSetValKeyByteArray$default(baseProtocolUtils, BasePrinterConstant.ValKeyStr.TASK_END, 0, packNum, bytes, 2, null));
                this.I$0 = packNum;
                this.label = 1;
                obj = TimeoutKt.withTimeout(5000L, new HPRTPrinterHelperV1$Companion$sendTaskEnd$1$result$1(packNum, null), this);
                if (obj == coroutine_suspended) {
                    return coroutine_suspended;
                }
                i = packNum;
            } catch (Exception unused) {
                i = packNum;
                this.$listener.onResult(HPCode.INSTANCE.getHP_SEND_TIMEOUT());
            }
        } else {
            if (i2 != 1) {
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            i = this.I$0;
            try {
                ResultKt.throwOnFailure(obj);
            } catch (Exception unused2) {
                this.$listener.onResult(HPCode.INSTANCE.getHP_SEND_TIMEOUT());
            }
        }
        this.$listener.onResult(((Number) obj).intValue());
        HPRTPrinterHelperV1.INSTANCE.getMap().remove("task_end_" + i);
        return Unit.INSTANCE;
    }
}
