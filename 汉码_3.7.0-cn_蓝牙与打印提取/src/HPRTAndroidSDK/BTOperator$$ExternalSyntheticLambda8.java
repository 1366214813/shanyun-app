package HPRTAndroidSDK;

import android.bluetooth.BluetoothGatt;
import com.bhm.ble.device.BleDevice;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Ref;

/* JADX INFO: compiled from: D8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class BTOperator$$ExternalSyntheticLambda8 implements Function2 {
    public final /* synthetic */ BTOperator f$0;
    public final /* synthetic */ Ref.BooleanRef f$1;
    public final /* synthetic */ Ref.BooleanRef f$2;

    public /* synthetic */ BTOperator$$ExternalSyntheticLambda8(BTOperator bTOperator, Ref.BooleanRef booleanRef, Ref.BooleanRef booleanRef2) {
        this.f$0 = bTOperator;
        this.f$1 = booleanRef;
        this.f$2 = booleanRef2;
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(Object obj, Object obj2) {
        return BTOperator.OpenPort$lambda$5$lambda$2(this.f$0, this.f$1, this.f$2, (BleDevice) obj, (BluetoothGatt) obj2);
    }
}
