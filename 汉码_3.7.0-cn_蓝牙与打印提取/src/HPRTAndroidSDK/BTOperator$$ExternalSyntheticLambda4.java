package HPRTAndroidSDK;

import com.bhm.ble.callback.BleReadCallback;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Ref;

/* JADX INFO: compiled from: D8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class BTOperator$$ExternalSyntheticLambda4 implements Function1 {
    public final /* synthetic */ Ref.BooleanRef f$0;
    public final /* synthetic */ Ref.ObjectRef f$1;

    public /* synthetic */ BTOperator$$ExternalSyntheticLambda4(Ref.BooleanRef booleanRef, Ref.ObjectRef objectRef) {
        this.f$0 = booleanRef;
        this.f$1 = objectRef;
    }

    @Override // kotlin.jvm.functions.Function1
    public final Object invoke(Object obj) {
        return BTOperator.ReadDataMillisecond$lambda$18$lambda$17(this.f$0, this.f$1, (BleReadCallback) obj);
    }
}
