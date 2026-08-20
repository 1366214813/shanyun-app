package HPRTAndroidSDK;

import com.bhm.ble.device.BleDevice;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Ref;

/* JADX INFO: compiled from: D8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class BTOperator$$ExternalSyntheticLambda0 implements Function4 {
    public final /* synthetic */ Ref.BooleanRef f$0;

    public /* synthetic */ BTOperator$$ExternalSyntheticLambda0(Ref.BooleanRef booleanRef) {
        this.f$0 = booleanRef;
    }

    @Override // kotlin.jvm.functions.Function4
    public final Object invoke(Object obj, Object obj2, Object obj3, Object obj4) {
        return BTOperator.writeData$lambda$10$lambda$9$lambda$6(this.f$0, (BleDevice) obj, ((Integer) obj2).intValue(), ((Integer) obj3).intValue(), (byte[]) obj4);
    }
}
