package HPRTAndroidSDK;

import com.bhm.ble.callback.BleWriteCallback;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Ref;

/* JADX INFO: compiled from: D8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class BTOperator$$ExternalSyntheticLambda11 implements Function1 {
    public final /* synthetic */ Ref.BooleanRef f$0;
    public final /* synthetic */ Ref.BooleanRef f$1;

    public /* synthetic */ BTOperator$$ExternalSyntheticLambda11(Ref.BooleanRef booleanRef, Ref.BooleanRef booleanRef2) {
        this.f$0 = booleanRef;
        this.f$1 = booleanRef2;
    }

    @Override // kotlin.jvm.functions.Function1
    public final Object invoke(Object obj) {
        return BTOperator.writeData$lambda$10$lambda$9(this.f$0, this.f$1, (BleWriteCallback) obj);
    }
}
