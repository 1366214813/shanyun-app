package HPRTAndroidSDK;

import com.bhm.ble.device.BleDevice;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Ref;

/* JADX INFO: compiled from: D8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class BTOperator$$ExternalSyntheticLambda2 implements Function2 {
    public final /* synthetic */ Ref.BooleanRef f$0;
    public final /* synthetic */ Ref.ObjectRef f$1;

    public /* synthetic */ BTOperator$$ExternalSyntheticLambda2(Ref.BooleanRef booleanRef, Ref.ObjectRef objectRef) {
        this.f$0 = booleanRef;
        this.f$1 = objectRef;
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(Object obj, Object obj2) {
        return BTOperator.ReadData$lambda$14$lambda$13$lambda$11(this.f$0, this.f$1, (BleDevice) obj, (byte[]) obj2);
    }
}
