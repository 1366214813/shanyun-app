package HPRTAndroidSDK;

import com.bhm.ble.device.BleDevice;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Ref;

/* JADX INFO: compiled from: D8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class BTOperator$$ExternalSyntheticLambda5 implements Function2 {
    public final /* synthetic */ Ref.BooleanRef f$0;

    public /* synthetic */ BTOperator$$ExternalSyntheticLambda5(Ref.BooleanRef booleanRef) {
        this.f$0 = booleanRef;
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(Object obj, Object obj2) {
        return BTOperator.writeData$lambda$10$lambda$9$lambda$7(this.f$0, (BleDevice) obj, ((Boolean) obj2).booleanValue());
    }
}
