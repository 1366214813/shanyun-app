package HPRTAndroidSDK;

import android.bluetooth.BluetoothGatt;
import com.bhm.ble.device.BleDevice;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Ref;

/* JADX INFO: compiled from: D8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class BTOperator$$ExternalSyntheticLambda9 implements Function4 {
    public final /* synthetic */ Ref.BooleanRef f$0;

    public /* synthetic */ BTOperator$$ExternalSyntheticLambda9(Ref.BooleanRef booleanRef) {
        this.f$0 = booleanRef;
    }

    @Override // kotlin.jvm.functions.Function4
    public final Object invoke(Object obj, Object obj2, Object obj3, Object obj4) {
        return BTOperator.OpenPort$lambda$5$lambda$3(this.f$0, ((Boolean) obj).booleanValue(), (BleDevice) obj2, (BluetoothGatt) obj3, ((Integer) obj4).intValue());
    }
}
